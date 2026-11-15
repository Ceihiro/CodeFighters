package com.codefighters;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class HowToPlayScreen implements Screen {
    final CodeFightersGame game;

    public HowToPlayScreen(final CodeFightersGame game) {
        this.game = game;
    }

    @Override
    public void show() {
        game.updateFontScales();
    }

    @Override
    public void render(float delta) {
        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();

        Gdx.gl.glClearColor(0.02f, 0.02f, 0.05f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Grid background
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        game.shapeRenderer.setColor(0.08f, 0.08f, 0.16f, 1);
        for (int i = 0; i < w; i += 45) game.shapeRenderer.line(i, 0, i, h);
        for (int i = 0; i < h; i += 45) game.shapeRenderer.line(0, i, w, i);
        game.shapeRenderer.end();

        // Title
        game.batch.begin();
        game.fontTitle.setColor(0.31f, 0.76f, 0.97f, 1);
        game.drawTextCentered(game.fontTitle, "HOW TO PLAY", w / 2f, h * 0.92f);
        game.batch.end();

        // Instruction Panels
        float cardX = w * 0.08f;
        float cardW = w * 0.84f;
        float startY = h * 0.81f;
        float rowH = h * 0.115f;

        drawInstructionRow(cardX, startY, cardW, rowH, "MOVE", "LEFT / RIGHT buttons or A/D keys to maneuver across the stage.", 0x00bcd4);
        drawInstructionRow(cardX, startY - rowH * 1.15f, cardW, rowH, "ATTACK", "Tap ATK (or J key) for light strikes. Chain 3 hits for a combo!", 0xff1744);
        drawInstructionRow(cardX, startY - rowH * 2.30f, cardW, rowH, "SPECIAL", "Tap SP (or K key) to launch a ranged code beam (18 DMG).", 0x00e5ff);
        drawInstructionRow(cardX, startY - rowH * 3.45f, cardW, rowH, "BLOCK", "Hold BLK (or L key) to deploy firewall barrier, cutting damage by 70%.", 0xffeb3b);
        drawInstructionRow(cardX, startY - rowH * 4.60f, cardW, rowH, "GOAL", "Neutralize WannaCry before your system health reaches 0%!", 0x00e676);

        // Back button
        float btnW = w * 0.32f;
        float btnH = h * 0.082f;
        drawButton(w / 2f - btnW / 2f, h * 0.045f, btnW, btnH, "BACK TO MENU", 0x546e7a, 0x263238);
        if (isButtonClicked(w / 2f - btnW / 2f, h * 0.045f, btnW, btnH)) {
            game.setScreen(game.menuScreen);
        }
    }

    void drawInstructionRow(float x, float y, float w, float h, String badge, String desc, int badgeCol) {
        float r = ((badgeCol >> 16) & 0xFF) / 255f;
        float g = ((badgeCol >> 8) & 0xFF) / 255f;
        float b = (badgeCol & 0xFF) / 255f;

        // Container
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        game.shapeRenderer.setColor(0.06f, 0.08f, 0.13f, 0.88f);
        game.shapeRenderer.rect(x, y, w, h);

        // Left accent badge
        float badgeW = w * 0.18f;
        game.shapeRenderer.setColor(r, g, b, 0.9f);
        game.shapeRenderer.rect(x, y, badgeW, h);
        game.shapeRenderer.end();

        // Border
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        game.shapeRenderer.setColor(0.2f, 0.3f, 0.45f, 0.8f);
        game.shapeRenderer.rect(x, y, w, h);
        game.shapeRenderer.end();

        // Badge Text
        game.batch.begin();
        game.fontBold.setColor(0.05f, 0.05f, 0.05f, 1f);
        game.drawTextCentered(game.fontBold, badge, x + badgeW / 2f, y + h / 2f);

        // Description Text
        game.font.setColor(0.92f, 0.92f, 0.95f, 1f);
        game.font.draw(game.batch, desc, x + badgeW + 20, y + h * 0.65f);
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
        game.shapeRenderer.setColor(r, g, b, hover ? 1f : 0.8f);
        game.shapeRenderer.rect(x, y, bw, bh);
        game.shapeRenderer.end();

        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        game.shapeRenderer.setColor(r * 1.4f, g * 1.4f, b * 1.4f, 1f);
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

    @Override public void resize(int width, int height) {
        game.updateFontScales();
    }
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override public void dispose() {}
}
