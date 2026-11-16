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

        // Background
        game.batch.begin();
        if (game.texBgMenu != null) {
            game.batch.setColor(0.25f, 0.35f, 0.45f, 1f);
            game.batch.draw(game.texBgMenu, 0, 0, w, h);
            game.batch.setColor(1, 1, 1, 1);
        }
        game.batch.end();

        // Cyber grid background
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        game.shapeRenderer.setColor(0, 0, 0, 0.65f);
        game.shapeRenderer.rect(0, 0, w, h);
        game.shapeRenderer.end();

        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        game.shapeRenderer.setColor(0.08f, 0.08f, 0.16f, 1);
        for (int i = 0; i < w; i += 45) game.shapeRenderer.line(i, 0, i, h);
        for (int i = 0; i < h; i += 45) game.shapeRenderer.line(0, i, w, i);
        game.shapeRenderer.end();

        // Title
        game.batch.begin();
        game.fontTitle.setColor(0.31f, 0.76f, 0.97f, 1);
        game.drawTextCentered(game.fontTitle, "TACTICAL COMBAT GUIDE", w / 2f, h * 0.93f);
        game.batch.end();

        // Instruction Panels
        float cardX = w * 0.06f;
        float cardW = w * 0.88f;
        float startY = h * 0.76f;
        float rowH = h * 0.092f;

        drawInstructionRow(cardX, startY, cardW, rowH,
            "WALK", "LEFT / RIGHT buttons or A / D keys to walk across the stage.", 0x00bcd4);

        drawInstructionRow(cardX, startY - rowH * 1.08f, cardW, rowH,
            "DASH", "DOUBLE-TAP Left: Invincible Backdash! DOUBLE-TAP Right: Forward Dash into Dash Attack!", 0xff9800);

        drawInstructionRow(cardX, startY - rowH * 2.16f, cardW, rowH,
            "ATTACK", "Tap ATK (or J key) for light strikes. Chain 3 hits for combo! ATK during dash for Dash Attack (1.5x DMG)!", 0xff1744);

        drawInstructionRow(cardX, startY - rowH * 3.24f, cardW, rowH,
            "SPECIAL", "Tap SP (or K key) to unleash the Code Beam projectile (charges with combat).", 0x00e5ff);

        drawInstructionRow(cardX, startY - rowH * 4.32f, cardW, rowH,
            "BLOCK", "Hold BLK (or L key) to deploy cyber firewall, cutting all damage by 70%.", 0xffeb3b);

        drawInstructionRow(cardX, startY - rowH * 5.40f, cardW, rowH,
            "VERSUS 2P", "In Same-Device Versus, P1 fights on Left (Cyan) and P2 fights on Right (Orange) simultaneously!", 0x7c4dff);

        // Back button
        float btnW = w * 0.25f;
        float btnH = h * 0.075f;
        drawButton(w / 2f - btnW / 2f, h * 0.035f, btnW, btnH, "BACK TO MENU", 0x546e7a, 0x263238);
        if (isButtonClicked(w / 2f - btnW / 2f, h * 0.035f, btnW, btnH)) {
            game.playSound(game.sndMenuBack, 0.8f);
            game.setScreen(game.menuScreen);
        }
    }

    void drawInstructionRow(float x, float y, float w, float h, String badge, String desc, int badgeCol) {
        float r = ((badgeCol >> 16) & 0xFF) / 255f;
        float g = ((badgeCol >> 8) & 0xFF) / 255f;
        float b = (badgeCol & 0xFF) / 255f;

        // Container
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        game.shapeRenderer.setColor(0.06f, 0.08f, 0.13f, 0.88f);
        game.shapeRenderer.rect(x, y, w, h);

        // Left accent badge
        float badgeW = w * 0.18f;
        game.shapeRenderer.setColor(r, g, b, 0.9f);
        game.shapeRenderer.rect(x, y, badgeW, h);
        game.shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);

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
        game.fontSmall.setColor(0.92f, 0.92f, 0.95f, 1f);
        game.fontSmall.draw(game.batch, desc, x + badgeW + 20, y + h * 0.65f);
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
