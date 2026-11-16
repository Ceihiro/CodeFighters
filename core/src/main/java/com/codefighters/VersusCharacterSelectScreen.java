package com.codefighters;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class VersusCharacterSelectScreen implements Screen {
    final CodeFightersGame game;
    float timer = 0;
    boolean p1Ready = false;
    boolean p2Ready = false;

    public VersusCharacterSelectScreen(final CodeFightersGame game) {
        this.game = game;
    }

    @Override
    public void show() {
        game.updateFontScales();
        timer = 0;
        p1Ready = false;
        p2Ready = false;
    }

    @Override
    public void render(float delta) {
        timer += delta;
        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();

        Gdx.gl.glClearColor(0.02f, 0.02f, 0.05f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Background
        game.batch.begin();
        if (game.texBgVersus != null) {
            game.batch.draw(game.texBgVersus, 0, 0, w, h);
        }
        game.batch.end();

        // Cyber grid background fallback
        if (game.texBgVersus == null) {
            game.shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            game.shapeRenderer.setColor(0.08f, 0.10f, 0.20f, 1);
            for (int i = 0; i < w; i += 50) game.shapeRenderer.line(i, 0, i, h);
            for (int i = 0; i < h; i += 50) game.shapeRenderer.line(0, i, w, i);
            game.shapeRenderer.end();
        }

        // Title Header
        game.batch.begin();
        game.fontTitle.setColor(1, 1, 1, 1);
        game.drawTextCentered(game.fontTitle, "VERSUS BATTLE SELECT", w / 2f, h * 0.93f);
        game.batch.end();

        float cardY = h * 0.44f;
        float cardW = w * 0.38f;
        float cardH = h * 0.54f;

        // P1 CARD (Left)
        float leftX = w * 0.08f;
        drawFighterCard(leftX, cardY - cardH / 2f, cardW, cardH, true, p1Ready);

        // Center VS Badge
        game.batch.begin();
        if (game.texVsBadge != null) {
            float vsW = h * 0.11f;
            float vsH = vsW * 0.65f;
            game.batch.draw(game.texVsBadge, w / 2f - vsW / 2f, cardY - vsH / 2f, vsW, vsH);
        } else {
            game.fontTitle.setColor(1, 0.85f, 0.1f, 1);
            game.drawTextCentered(game.fontTitle, "VS", w / 2f, cardY);
        }
        game.batch.end();

        // P2 CARD (Right)
        float rightX = w * 0.54f;
        drawFighterCard(rightX, cardY - cardH / 2f, cardW, cardH, false, p2Ready);

        // Ready toggle buttons
        float rBtnW = w * 0.22f;
        float rBtnH = h * 0.08f;
        float rBtnY = h * 0.18f;

        // P1 Ready Button
        drawButton(leftX + (cardW - rBtnW) / 2f, rBtnY, rBtnW, rBtnH,
            p1Ready ? "P1: READY!" : "P1: PRESS READY",
            p1Ready ? 0x00e676 : 0x00bcd4, p1Ready ? 0x00b248 : 0x00838f);
        if (isButtonClicked(leftX + (cardW - rBtnW) / 2f, rBtnY, rBtnW, rBtnH)) {
            p1Ready = !p1Ready;
            game.playSound(game.sndMenuSelect, 1.0f);
        }

        // P2 Ready Button
        drawButton(rightX + (cardW - rBtnW) / 2f, rBtnY, rBtnW, rBtnH,
            p2Ready ? "P2: READY!" : "P2: PRESS READY",
            p2Ready ? 0x00e676 : 0xff9800, p2Ready ? 0x00b248 : 0xe65100);
        if (isButtonClicked(rightX + (cardW - rBtnW) / 2f, rBtnY, rBtnW, rBtnH)) {
            p2Ready = !p2Ready;
            game.playSound(game.sndMenuSelect, 1.0f);
        }

        // Bottom Action buttons
        float btnW = w * 0.20f;
        float btnH = h * 0.075f;
        float btnY = h * 0.045f;

        drawButton(w * 0.08f, btnY, btnW, btnH, "BACK", 0x546e7a, 0x263238);
        if (isButtonClicked(w * 0.08f, btnY, btnW, btnH)) {
            game.playSound(game.sndMenuBack, 1.0f);
            game.setScreen(game.modeSelectScreen);
        }

        boolean canStart = (p1Ready && p2Ready);
        drawButton(w * 0.72f, btnY, btnW, btnH, canStart ? "START DUEL!" : "READY UP",
            canStart ? 0x00e676 : 0x78909c, canStart ? 0x00b248 : 0x37474f);
        if (canStart && isButtonClicked(w * 0.72f, btnY, btnW, btnH)) {
            game.playSound(game.sndRoundStart, 1.0f);
            game.setScreen(game.versusVsScreen);
        }
    }

    void drawFighterCard(float x, float y, float w, float h, boolean isP1, boolean isReady) {
        // Card Background
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        game.shapeRenderer.setColor(0.06f, 0.07f, 0.12f, 0.92f);
        game.shapeRenderer.rect(x, y, w, h);
        game.shapeRenderer.end();

        // Card Border
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        if (isP1) {
            game.shapeRenderer.setColor(0.0f, 0.85f, 1.0f, 1f);
        } else {
            game.shapeRenderer.setColor(1.0f, 0.60f, 0.15f, 1f);
        }
        game.shapeRenderer.rect(x, y, w, h);
        game.shapeRenderer.end();

        // Sprite inside card
        float spriteH = h * 0.52f;
        float spriteW = spriteH;
        float spriteX = x + w / 2f - spriteW / 2f;
        float spriteY = y + h * 0.40f;

        boolean isFrame2 = (((int)(timer * 2.8f)) % 2 == 1);
        Texture tex = (isFrame2 && game.texPlayerIdle2 != null) ? game.texPlayerIdle2 : game.texPlayerIdle;

        game.batch.begin();
        if (tex != null) {
            if (isP1) {
                game.batch.setColor(1f, 1f, 1f, 1f);
                game.batch.draw(tex, spriteX, spriteY, spriteW, spriteH);
            } else {
                game.batch.setColor(1.0f, 0.65f, 0.2f, 1f);
                game.batch.draw(tex, spriteX, spriteY, spriteW, spriteH,
                    0, 0, tex.getWidth(), tex.getHeight(), true, false);
                game.batch.setColor(1f, 1f, 1f, 1f);
            }
        }

        // Details below
        float textY = y + h * 0.32f;
        if (isP1) {
            game.fontBold.setColor(0.31f, 0.85f, 1.0f, 1f);
            game.drawTextCentered(game.fontBold, "PLAYER 1: CAT", x + w / 2f, textY);
            game.font.setColor(0.7f, 0.9f, 1.0f, 1f);
            game.drawTextCentered(game.font, "Control Scheme: Left Screen (Cyan)", x + w / 2f, textY - 24);
        } else {
            game.fontBold.setColor(1.0f, 0.70f, 0.2f, 1f);
            game.drawTextCentered(game.fontBold, "PLAYER 2: CAT (ORANGE)", x + w / 2f, textY);
            game.font.setColor(1.0f, 0.85f, 0.7f, 1f);
            game.drawTextCentered(game.font, "Control Scheme: Right Screen (Orange)", x + w / 2f, textY - 24);
        }

        if (isReady) {
            game.fontBold.setColor(0.2f, 1f, 0.4f, 1f);
            game.drawTextCentered(game.fontBold, "[ LOCKED IN ]", x + w / 2f, y + 26);
        }
        game.batch.end();
    }

    void drawButton(float x, float y, float bw, float bh, String text, int hoverCol, int col) {
        float mx = Gdx.input.getX();
        float my = Gdx.graphics.getHeight() - Gdx.input.getY();
        boolean hover = mx >= x && mx <= x + bw && my >= y && my <= y + bh;

        int c = hover ? hoverCol : col;
        float r = ((c >> 16) & 0xFF) / 255f;
        float g = ((c >> 8) & 0xFF) / 255f;
        float b = (c & 0xFF) / 255f;

        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        game.shapeRenderer.setColor(r, g, b, 0.85f);
        game.shapeRenderer.rect(x, y, bw, bh);
        game.shapeRenderer.end();

        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        game.shapeRenderer.setColor(r * 1.3f, g * 1.3f, b * 1.3f, 1f);
        game.shapeRenderer.rect(x, y, bw, bh);
        game.shapeRenderer.end();

        game.batch.begin();
        game.fontBold.setColor(1, 1, 1, 1);
        game.drawTextCentered(game.fontBold, text, x + bw / 2f, y + bh / 2f);
        game.batch.end();
    }

    boolean isButtonClicked(float x, float y, float w, float h) {
        float mx = Gdx.input.getX();
        float my = Gdx.graphics.getHeight() - Gdx.input.getY();
        return mx >= x && mx <= x + w && my >= y && my <= y + h && Gdx.input.justTouched();
    }

    @Override public void resize(int width, int height) { game.updateFontScales(); }
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override public void dispose() {}
}
