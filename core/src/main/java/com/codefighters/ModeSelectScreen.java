package com.codefighters;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class ModeSelectScreen implements Screen {
    final CodeFightersGame game;
    float timer = 0;

    public ModeSelectScreen(final CodeFightersGame game) {
        this.game = game;
    }

    @Override
    public void show() {
        game.updateFontScales();
        timer = 0;
    }

    @Override
    public void render(float delta) {
        timer += delta;
        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();

        Gdx.gl.glClearColor(0.02f, 0.02f, 0.06f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Cyber background
        game.batch.begin();
        if (game.texBgMenu != null) {
            game.batch.setColor(0.25f, 0.35f, 0.45f, 1f);
            game.batch.draw(game.texBgMenu, 0, 0, w, h);
            game.batch.setColor(1, 1, 1, 1);
        }
        game.batch.end();

        // Dark overlay
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        game.shapeRenderer.setColor(0, 0, 0, 0.65f);
        game.shapeRenderer.rect(0, 0, w, h);
        game.shapeRenderer.end();

        // Header Title
        game.batch.begin();
        game.fontTitle.setColor(0.31f, 0.76f, 0.97f, 1);
        game.drawTextCentered(game.fontTitle, "SELECT OPERATION MODE", w / 2f, h * 0.91f);
        game.batch.end();

        // Two large mode cards side-by-side
        float cardW = w * 0.41f;
        float cardH = h * 0.62f;
        float cardY = h * 0.20f;

        // Card 1: ARCADE MODE (Left)
        float arcadeX = w * 0.07f;
        drawModeCard(arcadeX, cardY, cardW, cardH,
            "ARCADE MODE",
            "SINGLE PLAYER",
            "Deploy Cat against the rogue WannaCry AI.\n"
            + "Best of 3 rounds.\n"
            + "Utilize Dash maneuvers, combos, and code beams\n"
            + "to neutralize the ransomware threat.",
            0x00bcd4, 0x004d5a, true);

        if (isButtonClicked(arcadeX, cardY, cardW, cardH)) {
            game.playSound(game.sndMenuSelect, 1.0f);
            game.currentMode = GameMode.ARCADE;
            game.setScreen(game.charSelectScreen);
        }

        // Card 2: VERSUS MODE (Right)
        float versusX = w * 0.52f;
        drawModeCard(versusX, cardY, cardW, cardH,
            "VERSUS MODE",
            "SAME-DEVICE 2-PLAYER",
            "Direct 1v1 local combat on this single device!\n"
            + "Player 1 (Left Controls - Cyan)\n"
            + "Player 2 (Right Controls - Orange)\n"
            + "Simultaneous multi-touch, full dash mechanics,\n"
            + "and best of 3 rounds tournament match.",
            0xff9800, 0xbf360c, false);

        if (isButtonClicked(versusX, cardY, cardW, cardH)) {
            game.playSound(game.sndMenuSelect, 1.0f);
            game.currentMode = GameMode.VERSUS;
            game.setScreen(game.versusCharSelectScreen);
        }

        // Back button
        float btnW = w * 0.20f;
        float btnH = h * 0.075f;
        drawButton(w * 0.05f, h * 0.045f, btnW, btnH, "BACK", 0x546e7a, 0x263238);
        if (isButtonClicked(w * 0.05f, h * 0.045f, btnW, btnH)) {
            game.playSound(game.sndMenuBack, 1.0f);
            game.setScreen(game.menuScreen);
        }
    }

    void drawModeCard(float x, float y, float w, float h, String title, String tag, String desc, int primaryCol, int secondaryCol, boolean isArcade) {
        float mx = Gdx.input.getX();
        float my = Gdx.graphics.getHeight() - Gdx.input.getY();
        boolean hover = mx >= x && mx <= x + w && my >= y && my <= y + h;

        float r = ((primaryCol >> 16) & 0xFF) / 255f;
        float g = ((primaryCol >> 8) & 0xFF) / 255f;
        float b = (primaryCol & 0xFF) / 255f;

        // Card background
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        game.shapeRenderer.setColor(0.04f, 0.07f, 0.12f, hover ? 0.95f : 0.85f);
        game.shapeRenderer.rect(x, y, w, h);

        // Header banner on card
        game.shapeRenderer.setColor(r * 0.8f, g * 0.8f, b * 0.8f, hover ? 0.95f : 0.75f);
        game.shapeRenderer.rect(x, y + h - h * 0.18f, w, h * 0.18f);
        game.shapeRenderer.end();

        // Card Border
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        game.shapeRenderer.setColor(r, g, b, hover ? 1f : 0.65f);
        game.shapeRenderer.rect(x, y, w, h);
        game.shapeRenderer.end();

        // Header text
        game.batch.begin();
        game.fontBold.setColor(1, 1, 1, 1);
        game.drawTextCentered(game.fontBold, title, x + w / 2f, y + h - h * 0.07f);

        game.fontSmall.setColor(r, g, b, 1);
        game.drawTextCentered(game.fontSmall, tag, x + w / 2f, y + h - h * 0.13f);

        // Sprite illustration in center
        float spriteH = h * 0.40f;
        float spriteW = spriteH;
        float spriteY = y + h * 0.38f;

        if (isArcade) {
            if (game.texPlayerIdle != null) {
                game.batch.draw(game.texPlayerIdle, x + w / 2f - spriteW / 2f, spriteY, spriteW, spriteH);
            }
        } else {
            // Versus preview: Two small fighters facing each other
            float halfS = spriteH * 0.75f;
            if (game.texPlayerIdle != null) {
                // P1 Cyan
                game.batch.setColor(1, 1, 1, 1);
                game.batch.draw(game.texPlayerIdle, x + w * 0.18f - halfS / 2f, spriteY, halfS, halfS);
                // P2 Orange tint
                game.batch.setColor(1.0f, 0.65f, 0.2f, 1);
                game.batch.draw(game.texPlayerIdle,
                    x + w * 0.82f - halfS / 2f, spriteY, halfS, halfS,
                    0, 0, game.texPlayerIdle.getWidth(), game.texPlayerIdle.getHeight(), true, false);
                game.batch.setColor(1, 1, 1, 1);
            }
        }

        // Description text
        game.font.setColor(0.88f, 0.90f, 0.95f, 1);
        String[] lines = desc.split("\n");
        float lineY = y + h * 0.30f;
        for (String line : lines) {
            game.drawTextCentered(game.font, line, x + w / 2f, lineY);
            lineY -= h * 0.065f;
        }

        // Action prompt
        game.fontBold.setColor(hover ? 1f : r, hover ? 1f : g, hover ? 1f : b, 1);
        game.drawTextCentered(game.fontBold, hover ? "> TAP TO LAUNCH <" : "TAP TO SELECT", x + w / 2f, y + h * 0.06f);
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
