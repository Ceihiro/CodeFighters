package com.codefighters;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class MenuScreen implements Screen {
    final CodeFightersGame game;
    float timer = 0;

    public MenuScreen(final CodeFightersGame game) {
        this.game = game;
    }

    @Override
    public void render(float delta) {
        timer += delta;
        Gdx.gl.glClearColor(0.04f, 0.04f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();

        // Background grid
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        game.shapeRenderer.setColor(0.1f, 0.1f, 0.24f, 1);
        for (int i = 0; i < w; i += 40) {
            game.shapeRenderer.line(i, 0, i, h);
        }
        for (int i = 0; i < h; i += 40) {
            game.shapeRenderer.line(0, i, w, i);
        }
        game.shapeRenderer.end();

        game.batch.begin();

        // Title with glow effect
        game.fontBold.setColor(0.2f, 0.76f, 0.97f, 1);
        game.fontBold.draw(game.batch, "CODE FIGHTERS", w/2 - 180, h * 0.75f);

        game.font.setColor(0.5f, 0.5f, 0.5f, 1);
        game.font.draw(game.batch, "Developers vs Malware", w/2 - 130, h * 0.65f);

        // Subtitle
        game.fontSmall.setColor(0.4f, 0.4f, 0.4f, 1);
        game.fontSmall.draw(game.batch, "Learn cybersecurity through arcade combat",
            w/2 - 160, h * 0.58f);

        // Animated code particles in background
        for (int i = 0; i < 8; i++) {
            float px = (float)(Math.sin(timer * 0.5 + i) * 200 + w/2);
            float py = (float)(Math.cos(timer * 0.3 + i * 0.7) * 100 + h * 0.4f);
            game.fontSmall.setColor(0, 1, 0.8f, 0.3f);
            game.fontSmall.draw(game.batch, Math.random() > 0.5 ? "1" : "0", px, py);
        }

        game.batch.end();

        // Buttons
        float btnW = 280, btnH = 60;
        float btnX = w/2 - btnW/2;
        float startY = h * 0.42f;

        // Start Fight button
        drawButton(btnX, startY, btnW, btnH, "START FIGHT", 0x00bcd4, 0x006064);
        if (isButtonClicked(btnX, startY, btnW, btnH)) {
            game.setScreen(game.selectScreen);
        }

        // Gallery button
        drawButton(btnX, startY - 80, btnW, btnH, "GALLERY", 0x7c4dff, 0x311b92);
        if (isButtonClicked(btnX, startY - 80, btnW, btnH)) {
            game.setScreen(game.galleryScreen);
        }

        // Controls hint
        game.batch.begin();
        game.fontSmall.setColor(0.4f, 0.4f, 0.4f, 1);
        game.fontSmall.draw(game.batch, "Tap buttons or use on-screen controls",
            w/2 - 130, h * 0.12f);
        game.batch.end();
    }

    void drawButton(float x, float y, float w, float h, String text, int hoverCol, int col) {
        float mx = Gdx.input.getX();
        float my = Gdx.graphics.getHeight() - Gdx.input.getY();
        boolean hover = mx >= x && mx <= x + w && my >= y && my <= y + h;

        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        if (hover) {
            game.shapeRenderer.setColor(((hoverCol >> 16) & 0xFF)/255f,
                ((hoverCol >> 8) & 0xFF)/255f, (hoverCol & 0xFF)/255f, 1);
        } else {
            game.shapeRenderer.setColor(((col >> 16) & 0xFF)/255f,
                ((col >> 8) & 0xFF)/255f, (col & 0xFF)/255f, 1);
        }
        game.shapeRenderer.rect(x, y, w, h);
        game.shapeRenderer.end();

        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        game.shapeRenderer.setColor(1, 1, 1, 1);
        game.shapeRenderer.rect(x, y, w, h);
        game.shapeRenderer.end();

        game.batch.begin();
        game.font.setColor(1, 1, 1, 1);
        game.font.draw(game.batch, text, x + w/2 - 70, y + h/2 + 10);
        game.batch.end();
    }

    boolean isButtonClicked(float x, float y, float w, float h) {
        float mx = Gdx.input.getX();
        float my = Gdx.graphics.getHeight() - Gdx.input.getY();
        boolean hover = mx >= x && mx <= x + w && my >= y && my <= y + h;
        return hover && Gdx.input.justTouched();
    }

    @Override public void show() {}
    @Override public void resize(int width, int height) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override public void dispose() {}
}
