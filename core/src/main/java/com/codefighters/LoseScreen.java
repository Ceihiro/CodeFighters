package com.codefighters;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class LoseScreen implements Screen {
    final CodeFightersGame game;
    float timer = 0;

    public LoseScreen(final CodeFightersGame game) {
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

        Gdx.gl.glClearColor(0.05f, 0.01f, 0.01f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Background
        game.batch.begin();
        if (game.texBgFight != null) {
            game.batch.setColor(0.45f, 0.12f, 0.12f, 1f);
            game.batch.draw(game.texBgFight, 0, 0, w, h);
            game.batch.setColor(1, 1, 1, 1);
        }
        game.batch.end();

        // Dark overlay
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        game.shapeRenderer.setColor(0, 0, 0, 0.72f);
        game.shapeRenderer.rect(0, 0, w, h);
        game.shapeRenderer.end();

        // Glitch scanline effects
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (int i = 0; i < 6; i++) {
            float gx = (float) (Math.random() * w * 0.8f);
            float gy = (float) (Math.random() * h);
            float gw = (float) (Math.random() * w * 0.4f + 80);
            float gh = (float) (Math.random() * 4 + 1);
            game.shapeRenderer.setColor(1, 0, 0, (float) Math.random() * 0.22f);
            game.shapeRenderer.rect(gx, gy, gw, gh);
        }
        game.shapeRenderer.end();

        // === YOU LOSE Banner ===
        float bannerY = h * 0.77f;
        float bannerH = h * 0.16f;

        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        game.shapeRenderer.setColor(0.35f, 0.0f, 0.02f, 0.92f);
        game.shapeRenderer.rect(0, bannerY, w, bannerH);
        game.shapeRenderer.end();

        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        game.shapeRenderer.setColor(1f, 0.1f, 0.15f, 1f);
        game.shapeRenderer.rect(0, bannerY, w, bannerH);
        game.shapeRenderer.end();

        // Banner text with glitch shake
        float glitchX = (float) (Math.sin(timer * 25f) * 2f);
        game.batch.begin();
        game.fontTitle.setColor(1f, 0.15f, 0.2f, 1f);
        game.drawTextCentered(game.fontTitle, "YOU LOSE", w / 2f + glitchX, bannerY + bannerH / 2f);
        game.batch.end();

        // Subtitle
        game.batch.begin();
        game.fontBold.setColor(1.0f, 0.4f, 0.4f, 1f);
        game.drawTextCentered(game.fontBold, "SYSTEM ENCRYPTED! WannaCry breached your defenses.", w / 2f, h * 0.71f);
        game.batch.end();

        // === Tips card ===
        float cardX = w * 0.12f;
        float cardW = w * 0.76f;
        float cardH = h * 0.32f;
        float cardY = h * 0.33f;

        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        game.shapeRenderer.setColor(0.12f, 0.04f, 0.04f, 0.92f);
        game.shapeRenderer.rect(cardX, cardY, cardW, cardH);
        game.shapeRenderer.end();

        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        game.shapeRenderer.setColor(0.7f, 0.15f, 0.15f, 0.85f);
        game.shapeRenderer.rect(cardX, cardY, cardW, cardH);
        game.shapeRenderer.end();

        float padX = cardX + 35;
        game.batch.begin();
        game.fontBold.setColor(1f, 0.3f, 0.3f, 1f);
        game.fontBold.draw(game.batch, "TACTICAL RECOVERY TIPS", padX, cardY + cardH - 25);

        float tipY = cardY + cardH - 75;
        game.font.setColor(0.9f, 0.9f, 0.9f, 1f);
        game.font.draw(game.batch, "* Hold BLK to mitigate 70% of heavy ransomware damage.", padX, tipY);

        tipY -= 45;
        game.font.draw(game.batch, "* Tap ATK consecutively to trigger the 3-hit combo burst.", padX, tipY);

        tipY -= 45;
        game.font.draw(game.batch, "* Use SP from safe distance to chip away 18 damage per blast.", padX, tipY);

        tipY -= 45;
        game.font.draw(game.batch, "* Counter-attack immediately after the enemy completes its swing!", padX, tipY);
        game.batch.end();

        // === Action Buttons ===
        float btnW = w * 0.24f;
        float btnH = h * 0.088f;
        float btnY = h * 0.16f;

        drawButton(w * 0.11f, btnY, btnW, btnH, "TRY AGAIN", 0xff1744, 0x8a0b22);
        if (isButtonClicked(w * 0.11f, btnY, btnW, btnH)) {
            game.fightScreen.resetFight();
            game.setScreen(game.fightScreen);
        }

        drawButton(w * 0.38f, btnY, btnW, btnH, "GALLERY", 0x7c4dff, 0x311b92);
        if (isButtonClicked(w * 0.38f, btnY, btnW, btnH)) {
            game.setScreen(game.galleryScreen);
        }

        drawButton(w * 0.65f, btnY, btnW, btnH, "MAIN MENU", 0x546e7a, 0x263238);
        if (isButtonClicked(w * 0.65f, btnY, btnW, btnH)) {
            game.setScreen(game.menuScreen);
        }

        game.batch.begin();
        game.fontSmall.setColor(0.55f, 0.55f, 0.55f, 1f);
        game.drawTextCentered(game.fontSmall, "Every great developer learns from failure. Try again!", w / 2f, h * 0.06f);
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
