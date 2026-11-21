package com.racasmod.client;

import com.racasmod.net.Payloads;
import com.racasmod.race.Ability;
import com.racasmod.race.Race;
import java.util.List;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.lwjgl.glfw.GLFW;

/** Menu bonito usado tanto para escolher a raça quanto para o Herobrine escolher o mob. */
public class ChoiceScreen extends Screen {
    private final List<Race> entries;
    private final boolean mimic;
    private final boolean closable;

    private int selected = 0;
    private int scroll = 0;
    private int px, py, pw, ph, rows;
    private static final int LIST_W = 128;
    private static final int ROW_H = 22;
    private ButtonWidget confirm;

    public ChoiceScreen(List<Race> entries, boolean mimic, boolean closable) {
        super(Text.literal(mimic ? "Copiar poder" : "Escolha sua raça"));
        this.entries = entries;
        this.mimic = mimic;
        this.closable = closable;
    }

    private Race current() { return entries.get(selected); }

    @Override
    protected void init() {
        pw = Math.min(width - 12, 450);
        ph = Math.min(height - 12, 264);
        px = (width - pw) / 2;
        py = (height - ph) / 2;
        rows = Math.max(1, (ph - 34) / ROW_H);

        int bx = px + LIST_W + 16;
        int bw = pw - LIST_W - 26;
        confirm = addDrawableChild(ButtonWidget.builder(Text.literal(""), b -> choose())
                .dimensions(bx, py + ph - 26, bw, 20).build());
        refreshButton();
    }

    private void refreshButton() {
        if (confirm != null) confirm.setMessage(Text.literal((mimic ? "Copiar: " : "Ser ") + current().name));
    }

    private void choose() {
        Race r = current();
        if (mimic) ClientPlayNetworking.send(new Payloads.MimicPayload(r.id));
        else ClientPlayNetworking.send(new Payloads.ChoosePayload(r.id));
        close();
    }

    private void select(int i) {
        selected = Math.max(0, Math.min(entries.size() - 1, i));
        if (selected < scroll) scroll = selected;
        if (selected >= scroll + rows) scroll = selected - rows + 1;
        refreshButton();
    }

    // ------------------------------------------------------------ desenho
    @Override
    public void renderBackground(DrawContext c, int mx, int my, float delta) {
        Race cur = current();
        int col = 0xFF000000 | cur.color;

        c.fillGradient(0, 0, width, height, 0xF00A0A12, 0xF0161628);
        c.fill(px, py, px + pw, py + ph, 0xFF14141E);
        c.drawBorder(px, py, pw, ph, col);

        // título
        Text title = Text.literal((mimic ? "COPIAR PODER DE UM MOB" : "ESCOLHA SUA RAÇA")
                + "  (" + (selected + 1) + "/" + entries.size() + ")").formatted(Formatting.BOLD);
        c.drawText(textRenderer, title, px + 8, py + 8, col, true);
        c.fill(px + 6, py + 20, px + pw - 6, py + 21, 0x66FFFFFF & (0x00FFFFFF | 0x66000000));

        // lista
        int lx = px + 6;
        int ly = py + 26;
        for (int i = 0; i < rows; i++) {
            int idx = scroll + i;
            if (idx >= entries.size()) break;
            Race r = entries.get(idx);
            int ry = ly + i * ROW_H;
            boolean hover = mx >= lx && mx < lx + LIST_W && my >= ry && my < ry + ROW_H - 2;
            int rc = 0xFF000000 | r.color;
            if (idx == selected) {
                c.fill(lx, ry, lx + LIST_W, ry + ROW_H - 2, (0x55 << 24) | r.color);
                c.drawBorder(lx, ry, LIST_W, ROW_H - 2, rc);
            } else if (hover) {
                c.fill(lx, ry, lx + LIST_W, ry + ROW_H - 2, 0x33FFFFFF);
            } else {
                c.fill(lx, ry, lx + LIST_W, ry + ROW_H - 2, 0x22000000);
            }
            c.drawItem(r.stack, lx + 3, ry + 2);
            c.drawText(textRenderer, Text.literal(r.name), lx + 24, ry + 6, idx == selected ? rc : 0xFFCCCCCC, true);
        }
        // barra de rolagem
        if (entries.size() > rows) {
            int trackH = rows * ROW_H - 2;
            int barH = Math.max(12, trackH * rows / entries.size());
            int barY = ly + (trackH - barH) * scroll / Math.max(1, entries.size() - rows);
            c.fill(lx + LIST_W + 3, ly, lx + LIST_W + 6, ly + trackH, 0x33FFFFFF);
            c.fill(lx + LIST_W + 3, barY, lx + LIST_W + 6, barY + barH, col);
        }

        // painel de detalhes
        int rx = px + LIST_W + 16;
        int rw = pw - LIST_W - 26;
        int hy = py + 26;
        c.fillGradient(rx, hy, rx + rw, hy + 54, (0xAA << 24) | cur.color, 0xFF1A1A28);
        c.drawBorder(rx, hy, rw, 54, col);

        c.getMatrices().push();
        c.getMatrices().translate(rx + 10, hy + 7, 0);
        c.getMatrices().scale(2.5f, 2.5f, 1f);
        c.drawItem(cur.stack, 0, 0);
        c.getMatrices().pop();

        c.getMatrices().push();
        c.getMatrices().translate(rx + 58, hy + 9, 0);
        c.getMatrices().scale(1.6f, 1.6f, 1f);
        c.drawText(textRenderer, Text.literal(cur.name).formatted(Formatting.BOLD), 0, 0, 0xFFFFFFFF, true);
        c.getMatrices().pop();

        String sub = cur.ability == Ability.NONE ? "Sem habilidade ativa"
                : "[R] " + cur.ability.title + " (" + cur.ability.cooldownSeconds + "s)";
        if (cur.mimic) sub = "[M] Copiar mob  |  [R] Usar poder";
        c.drawText(textRenderer, Text.literal(sub), rx + 58, hy + 36, 0xFFFFE082, true);

        // texto
        int ty = hy + 60;
        int bottom = py + ph - 32;
        int tw = rw - 12;
        c.enableScissor(rx, ty - 2, rx + rw, bottom);
        ty = block(c, cur.desc, rx + 4, ty, tw, 0xFFBBBBBB) + 4;
        if (cur.ability != Ability.NONE) {
            ty = block(c, "Habilidade: " + cur.ability.desc, rx + 4, ty, tw, 0xFFFFE082) + 4;
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
    }

    private int block(DrawContext c, String text, int x, int y, int w, int color) {
        for (OrderedText line : textRenderer.wrapLines(Text.literal(text), w)) {
            c.drawText(textRenderer, line, x, y, color, true);
            y += 10;
        }
        return y;
    }

    // ------------------------------------------------------------ entrada
    @Override
    public boolean mouseClicked(double mx, double my, int button) {
        int lx = px + 6;
        int ly = py + 26;
        if (button == 0 && mx >= lx && mx < lx + LIST_W && my >= ly) {
            int i = (int) ((my - ly) / ROW_H);
            if (i >= 0 && i < rows && scroll + i < entries.size()) {
                select(scroll + i);
                return true;
            }
        }
        return super.mouseClicked(mx, my, button);
    }

    @Override
    public boolean mouseScrolled(double mx, double my, double horizontal, double vertical) {
        int max = Math.max(0, entries.size() - rows);
        scroll = Math.max(0, Math.min(max, scroll - (int) Math.signum(vertical)));
        return true;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_DOWN) { select(selected + 1); return true; }
        if (keyCode == GLFW.GLFW_KEY_UP) { select(selected - 1); return true; }
        if (keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER) { choose(); return true; }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean shouldCloseOnEsc() { return closable; }

    @Override
    public boolean shouldPause() { return false; }
}
