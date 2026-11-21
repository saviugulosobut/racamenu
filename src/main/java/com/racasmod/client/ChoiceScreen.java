package com.racasmod.client;

import com.racasmod.net.Payloads;
import com.racasmod.race.Ability;
import com.racasmod.race.Attr;
import com.racasmod.race.Race;
import com.racasmod.race.Races;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.lwjgl.glfw.GLFW;

/**
 * Menu usado para escolher a raça e para o Herobrine copiar poderes.
 * Tem busca (é só digitar), lista com rolagem, chips de atributos e painel de detalhes.
 */
public class ChoiceScreen extends Screen {
    private record Chip(String text, int color) {}

    private final List<Race> entries;
    private final List<Race> view = new ArrayList<>();
    private final boolean mimic;
    private final boolean closable;

    private String query = "";
    private int selected = 0;
    private int scroll = 0;
    private int detailScroll = 0;
    private int detailMax = 0;
    private int rows = 8;
    private int px, py, pw, ph;
    private int btnX, btnY, btnW;

    private static final int LIST_W = 140;
    private static final int ROW_H = 22;
    private static final int LIST_TOP = 48;
    private static final int BTN_H = 20;

    public ChoiceScreen(List<Race> entries, boolean mimic, boolean closable) {
        super(Text.literal(mimic ? "Copiar poder" : "Escolha sua raça"));
        this.entries = entries;
        this.mimic = mimic;
        this.closable = closable;
        rebuildView();
    }

    // ------------------------------------------------------------ estado
    private Race current() { return view.isEmpty() ? null : view.get(selected); }

    private static int clamp(int v, int lo, int hi) { return Math.max(lo, Math.min(hi, v)); }

    private static int argb(int a, int rgb) { return (clamp(a, 0, 255) << 24) | (rgb & 0xFFFFFF); }

    private void rebuildView() {
        Race keep = (selected >= 0 && selected < view.size()) ? view.get(selected) : null;
        view.clear();
        String q = query.toLowerCase(Locale.ROOT).trim();
        for (Race r : entries) {
            if (q.isEmpty() || r.name.toLowerCase(Locale.ROOT).contains(q) || r.id.contains(q)) view.add(r);
        }
        int idx = keep == null ? -1 : view.indexOf(keep);
        selected = idx >= 0 ? idx : 0;
        scroll = 0;
        detailScroll = 0;
        ensureVisible();
    }

    private void ensureVisible() {
        if (selected < scroll) scroll = selected;
        if (selected >= scroll + rows) scroll = selected - rows + 1;
        scroll = clamp(scroll, 0, Math.max(0, view.size() - rows));
    }

    private void select(int i) {
        if (view.isEmpty()) return;
        selected = clamp(i, 0, view.size() - 1);
        ensureVisible();
        detailScroll = 0;
    }

    private void choose() {
        Race r = current();
        if (r == null) return;
        if (mimic) ClientPlayNetworking.send(new Payloads.MimicPayload(r.id));
        else ClientPlayNetworking.send(new Payloads.ChoosePayload(r.id));
        close();
    }

    @Override
    protected void init() {
        pw = Math.min(width - 12, 470);
        ph = Math.min(height - 12, 280);
        px = (width - pw) / 2;
        py = (height - ph) / 2;
        rows = Math.max(1, (ph - LIST_TOP - 20) / ROW_H);
        btnX = px + LIST_W + 16;
        btnW = pw - LIST_W - 26;
        btnY = py + ph - 26;
        ensureVisible();
    }

    // ------------------------------------------------------------ helpers de desenho
    private void rrect(DrawContext c, int x1, int y1, int x2, int y2, int color) {
        c.fill(x1 + 1, y1, x2 - 1, y1 + 1, color);
        c.fill(x1, y1 + 1, x2, y2 - 1, color);
        c.fill(x1 + 1, y2 - 1, x2 - 1, y2, color);
    }

    private void rframe(DrawContext c, int x1, int y1, int x2, int y2, int color) {
        c.fill(x1 + 1, y1, x2 - 1, y1 + 1, color);
        c.fill(x1 + 1, y2 - 1, x2 - 1, y2, color);
        c.fill(x1, y1 + 1, x1 + 1, y2 - 1, color);
        c.fill(x2 - 1, y1 + 1, x2, y2 - 1, color);
    }

    private static int readable(int rgb) {
        int r = (rgb >> 16) & 0xFF, g = (rgb >> 8) & 0xFF, b = rgb & 0xFF;
        double lum = 0.299 * r + 0.587 * g + 0.114 * b;
        return lum > 170 ? 0xFF111111 : 0xFFFFFFFF;
    }

    private static String signed(double v) {
        String s = Math.abs(v - Math.rint(v)) < 0.05
                ? String.valueOf((long) Math.rint(v))
                : String.format(Locale.ROOT, "%.1f", v);
        return v > 0 ? "+" + s : s;
    }

    private List<Chip> chips(Race r) {
        List<Chip> out = new ArrayList<>();
        for (Race.AttrMod m : r.attrs) {
            double v = m.value();
            boolean good = m.attr() == Attr.FALL ? v < 0 : v > 0;
            int color = m.attr() == Attr.SCALE ? 0xFF81D4FA : (good ? 0xFF66BB6A : 0xFFEF5350);
            String t = switch (m.attr()) {
                case HEALTH -> signed(v / 2) + " corações";
                case SPEED -> "Velocidade " + signed(v * 100) + "%";
                case DAMAGE -> "Dano " + signed(v);
                case ARMOR -> "Armadura " + signed(v);
                case KNOCKBACK -> "Anti-knockback " + signed(v * 100) + "%";
                case JUMP -> "Pulo " + signed(v * 100) + "%";
                case SCALE -> "Tamanho " + signed(v * 100) + "%";
                case FALL -> "Dano de queda " + signed(v * 100) + "%";
                case REACH -> "Alcance " + signed(v);
                case MINE -> "Mineração " + signed(v * 100) + "%";
                case STEP -> "Sobe blocos " + signed(v);
                case LUCK -> "Sorte " + signed(v);
            };
            out.add(new Chip(t, color));
        }
        if (r.lifesteal > 0f) out.add(new Chip("Rouba vida " + signed(r.lifesteal * 100) + "%", 0xFF66BB6A));
        if (r.revive) out.add(new Chip("Renasce ao morrer", 0xFFFFD54F));
        if (r.hunger > 0f) out.add(new Chip("Fome extra", 0xFFEF5350));
        return out;
    }

    private int block(DrawContext c, String text, int x, int y, int w, int color) {
        for (OrderedText line : textRenderer.wrapLines(Text.literal(text), w)) {
            c.drawText(textRenderer, line, x, y, color, true);
            y += 10;
        }
        return y;
    }

    // ------------------------------------------------------------ desenho
    @Override
    public void renderBackground(DrawContext c, int mx, int my, float delta) {
        Race cur = current();
        int rgb = cur != null ? cur.color : 0x7C4DFF;
        int col = 0xFF000000 | rgb;
        float pulse = 0.5f + 0.5f * (float) Math.sin(System.currentTimeMillis() / 350.0);

        // fundo, brilho e sombra
        c.fillGradient(0, 0, width, height, 0xF0070710, 0xF0141428);
        c.fillGradient(px - 8, py - 8, px + pw + 8, py + ph + 8, argb(36, rgb), argb(0, rgb));
        rrect(c, px + 3, py + 4, px + pw + 3, py + ph + 4, 0x66000000);

        // painel + barra de título
        rrect(c, px, py, px + pw, py + ph, 0xFF11111B);
        c.fillGradient(px + 1, py + 1, px + pw - 1, py + 22, argb(0xB0, rgb), 0xFF11111B);
        rframe(c, px, py, px + pw, py + ph, col);
        rframe(c, px - 1, py - 1, px + pw + 1, py + ph + 1, argb(70 + (int) (80 * pulse), rgb));

        Text title = Text.literal(mimic ? "COPIAR PODER" : "ESCOLHA SUA RAÇA").formatted(Formatting.BOLD);
        c.drawText(textRenderer, title, px + 10, py + 8, 0xFFFFFFFF, true);
        String cnt = view.size() + (query.isEmpty() ? "" : "/" + entries.size()) + (mimic ? " poderes" : " raças");
        Text ct = Text.literal(cnt);
        c.drawText(textRenderer, ct, px + pw - 10 - textRenderer.getWidth(ct), py + 8, 0xFFE0E0EC, true);

        // busca
        int sx = px + 6, sy = py + 28, sw = LIST_W, sh = 14;
        rrect(c, sx, sy, sx + sw, sy + sh, 0xFF1C1C2B);
        rframe(c, sx, sy, sx + sw, sy + sh, query.isEmpty() ? 0xFF3A3A55 : col);
        boolean blink = (System.currentTimeMillis() / 500) % 2 == 0;
        String shown = query.isEmpty() ? "Digite para buscar..." : query + (blink ? "_" : "");
        c.drawText(textRenderer, Text.literal(shown), sx + 5, sy + 3, query.isEmpty() ? 0xFF77778A : 0xFFFFFFFF, false);

        // lista
        int lx = px + 6;
        int ly = py + LIST_TOP;
        if (view.isEmpty()) {
            c.drawText(textRenderer, Text.literal("Nada encontrado"), lx + 6, ly + 6, 0xFF9E9EAE, false);
        }
        for (int i = 0; i < rows; i++) {
            int idx = scroll + i;
            if (idx >= view.size()) break;
            Race r = view.get(idx);
            int ry = ly + i * ROW_H;
            boolean sel = idx == selected;
            boolean hover = mx >= lx && mx < lx + LIST_W && my >= ry && my < ry + ROW_H - 2;
            int rc = 0xFF000000 | r.color;
            if (sel) {
                c.fillGradient(lx, ry, lx + LIST_W, ry + ROW_H - 2, argb(0x70, r.color), argb(0x20, r.color));
                rframe(c, lx, ry, lx + LIST_W, ry + ROW_H - 2, argb(150 + (int) (105 * pulse), r.color));
            } else if (hover) {
                rrect(c, lx, ry, lx + LIST_W, ry + ROW_H - 2, 0x33FFFFFF);
            } else {
                rrect(c, lx, ry, lx + LIST_W, ry + ROW_H - 2, 0x18FFFFFF);
            }
            c.fill(lx + 1, ry + 2, lx + 3, ry + ROW_H - 4, rc);
            c.drawItem(r.stack, lx + 6, ry + 2);
            String nm = textRenderer.trimToWidth(r.name, LIST_W - 34);
            c.drawText(textRenderer, Text.literal(nm), lx + 26, ry + 6, sel ? 0xFFFFFFFF : 0xFFBDBDCC, true);
        }
        if (view.size() > rows) {
            int trackH = rows * ROW_H - 2;
            int barH = Math.max(12, trackH * rows / view.size());
            int barY = ly + (trackH - barH) * scroll / Math.max(1, view.size() - rows);
            c.fill(lx + LIST_W + 3, ly, lx + LIST_W + 5, ly + trackH, 0x22FFFFFF);
            c.fill(lx + LIST_W + 3, barY, lx + LIST_W + 5, barY + barH, col);
        }
        c.drawText(textRenderer, Text.literal("Setas + Enter | digite p/ buscar"), lx, py + ph - 12, 0xFF6A6A80, false);

        if (cur == null) return;

        // ------------------------------------------------ painel de detalhes
        int rx = px + LIST_W + 16;
        int rw = pw - LIST_W - 26;
        int hy = py + 28;

        c.fillGradient(rx, hy, rx + rw, hy + 54, argb(0xB0, rgb), 0xFF171724);
        rframe(c, rx, hy, rx + rw, hy + 54, col);
        rrect(c, rx + 6, hy + 6, rx + 50, hy + 48, 0x66000000);
        rframe(c, rx + 6, hy + 6, rx + 50, hy + 48, argb(120, rgb));

        c.getMatrices().push();
        c.getMatrices().translate(rx + 8, hy + 7, 0);
        c.getMatrices().scale(2.5f, 2.5f, 1f);
        c.drawItem(cur.stack, 0, 0);
        c.getMatrices().pop();

        c.getMatrices().push();
        c.getMatrices().translate(rx + 58, hy + 9, 0);
        c.getMatrices().scale(1.6f, 1.6f, 1f);
        c.drawText(textRenderer, Text.literal(cur.name).formatted(Formatting.BOLD), 0, 0, 0xFFFFFFFF, true);
        c.getMatrices().pop();

        if (mimic) {
            Text kind = Text.literal(Races.get(cur.id) != null ? "RAÇA" : "MOB");
            c.drawText(textRenderer, kind, rx + rw - 6 - textRenderer.getWidth(kind), hy + 5, 0xFFD0D0DC, false);
        }

        String sub = cur.ability == Ability.NONE ? "Sem habilidade ativa"
                : "[R] " + cur.ability.title + " (" + cur.ability.cooldownSeconds + "s)";
        if (cur.mimic) sub = "[M] Copiar poder  |  [R] Usar";
        c.drawText(textRenderer, Text.literal(sub), rx + 58, hy + 38, 0xFFFFE082, true);

        // chips de atributos
        int cx = rx + 4, cy = hy + 60, maxX = rx + rw - 2;
        boolean any = false;
        for (Chip ch : chips(cur)) {
            int w = textRenderer.getWidth(ch.text()) + 10;
            if (cx + w > maxX && cx > rx + 4) { cx = rx + 4; cy += 14; }
            rrect(c, cx, cy, cx + w, cy + 12, argb(0x40, ch.color()));
            rframe(c, cx, cy, cx + w, cy + 12, argb(0x90, ch.color()));
            c.drawText(textRenderer, Text.literal(ch.text()), cx + 5, cy + 2, ch.color(), false);
            cx += w + 4;
            any = true;
        }
        int textTop = any ? cy + 18 : hy + 60;
        int bottom = py + ph - 32;
        int viewH = Math.max(10, bottom - textTop);
        int tw = rw - 14;

        detailScroll = clamp(detailScroll, 0, detailMax);
        int ty0 = textTop - detailScroll;
        int ty = ty0;
        c.enableScissor(rx, textTop, rx + rw, bottom);
        ty = block(c, cur.desc, rx + 4, ty, tw, 0xFFCFCFE0) + 4;
        if (cur.ability != Ability.NONE) {
            c.drawText(textRenderer, Text.literal("Habilidade").formatted(Formatting.BOLD), rx + 4, ty, 0xFFFFD54F, true);
            ty += 11;
            ty = block(c, cur.ability.title + " - " + cur.ability.desc, rx + 6, ty, tw - 4, 0xFFFFE082) + 4;
        }
        if (!cur.perks.isEmpty()) {
            c.drawText(textRenderer, Text.literal("Vantagens").formatted(Formatting.BOLD), rx + 4, ty, 0xFF66BB6A, true);
            ty += 11;
            for (String s : cur.perks) ty = block(c, "+ " + s, rx + 6, ty, tw - 4, 0xFFA5D6A7);
            ty += 4;
        }
        if (!cur.flaws.isEmpty()) {
            c.drawText(textRenderer, Text.literal("Fraquezas").formatted(Formatting.BOLD), rx + 4, ty, 0xFFEF5350, true);
            ty += 11;
            for (String s : cur.flaws) ty = block(c, "- " + s, rx + 6, ty, tw - 4, 0xFFEF9A9A);
        }
        c.disableScissor();
        detailMax = Math.max(0, (ty - ty0) - viewH);

        if (detailMax > 0) {
            int bh = Math.max(10, viewH * viewH / (viewH + detailMax));
            int by = textTop + (viewH - bh) * detailScroll / detailMax;
            c.fill(rx + rw - 3, textTop, rx + rw - 1, textTop + viewH, 0x22FFFFFF);
            c.fill(rx + rw - 3, by, rx + rw - 1, by + bh, col);
        }

        // botão de confirmar
        boolean hb = mx >= btnX && mx < btnX + btnW && my >= btnY && my < btnY + BTN_H;
        c.fillGradient(btnX, btnY, btnX + btnW, btnY + BTN_H,
                hb ? argb(0xFF, rgb) : argb(0xD0, rgb), hb ? argb(0xC8, rgb) : argb(0x80, rgb));
        rframe(c, btnX, btnY, btnX + btnW, btnY + BTN_H, hb ? 0xFFFFFFFF : col);
        Text bt = Text.literal((mimic ? "Copiar: " : "Ser ") + cur.name).formatted(Formatting.BOLD);
        c.drawText(textRenderer, bt, btnX + (btnW - textRenderer.getWidth(bt)) / 2, btnY + 6,
                hb ? readable(rgb) : 0xFFFFFFFF, hb ? false : true);
    }

    // ------------------------------------------------------------ entrada
    @Override
    public boolean mouseClicked(double mx, double my, int button) {
        if (button == 0) {
            int lx = px + 6;
            int ly = py + LIST_TOP;
            if (mx >= lx && mx < lx + LIST_W && my >= ly) {
                int i = (int) ((my - ly) / ROW_H);
                if (i >= 0 && i < rows && scroll + i < view.size()) {
                    select(scroll + i);
                    return true;
                }
            }
            if (current() != null && mx >= btnX && mx < btnX + btnW && my >= btnY && my < btnY + BTN_H) {
                choose();
                return true;
            }
        }
        return super.mouseClicked(mx, my, button);
    }

    @Override
    public boolean mouseScrolled(double mx, double my, double horizontal, double vertical) {
        if (mx >= px + LIST_W + 12) {
            detailScroll = clamp(detailScroll - (int) (vertical * 14), 0, detailMax);
        } else {
            int max = Math.max(0, view.size() - rows);
            scroll = clamp(scroll - (int) Math.signum(vertical), 0, max);
        }
        return true;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_DOWN) { select(selected + 1); return true; }
        if (keyCode == GLFW.GLFW_KEY_UP) { select(selected - 1); return true; }
        if (keyCode == GLFW.GLFW_KEY_PAGE_DOWN) { select(selected + rows); return true; }
        if (keyCode == GLFW.GLFW_KEY_PAGE_UP) { select(selected - rows); return true; }
        if (keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER) { choose(); return true; }
        if (keyCode == GLFW.GLFW_KEY_BACKSPACE && !query.isEmpty()) {
            query = query.substring(0, query.length() - 1);
            rebuildView();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        if (chr >= 32 && chr != 127 && query.length() < 18) {
            query += chr;
            rebuildView();
            return true;
        }
        return super.charTyped(chr, modifiers);
    }

    @Override
    public boolean shouldCloseOnEsc() { return closable; }

    @Override
    public boolean shouldPause() { return false; }
            }
                   
