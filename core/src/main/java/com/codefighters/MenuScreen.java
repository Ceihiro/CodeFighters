package com.codefighters;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class MenuScreen implements Screen {
    final CodeFightersGame game;
    float frame = 0;

    public MenuScreen(final CodeFightersGame game) {
        this.game = game;
    }

    @Override
    public void render(float delta) {
        frame++;
        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();

        Gdx.gl.glClearColor(0.02f, 0.02f, 0.05f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Grid background
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        game.shapeRenderer.setColor(0.08f, 0.08f, 0.15f, 1);
        for (int i = 0; i < w; i += 40) {
            game.shapeRenderer.line(i, 0, i, h);
        }
        for (int i = 0; i < h; i += 40) {
            game.shapeRenderer.line(0, i, w, i);
        }
        game.shapeRenderer.end();

        // Title
        game.batch.begin();
        game.fontBold.setColor(0.31f, 0.76f, 0.97f, 1);
        game.fontBold.draw(game.batch, "CODE FIGHTERS", w/2 - 180, h * 0.7f);

        game.fontSmall.setColor(0.6f, 0.6f, 0.6f, 1);
        game.fontSmall.draw(game.batch, "Security Awareness Arcade Fighter", w/2 - 150, h * 0.62f);
        game.batch.end();

        // Buttons
        float btnW = 280, btnH = 60;
        float btnX = w/2 - btnW/2;
        float startY = h * 0.42f;

        // Start Fight button
        drawButton(btnX, startY, btnW, btnH, "START FIGHT", 0x00bcd4, 0x008ba3);
        if (isButtonClicked(btnX, startY, btnW, btnH)) {
            game.setScreen(game.charSelectScreen);
        }

        // Gallery button
        drawButton(btnX, startY - 80, btnW, btnH, "GALLERY", 0x7c4dff, 0x5e35b1);
        if (isButtonClicked(btnX, startY - 80, btnW, btnH)) {
            game.setScreen(game.galleryScreen);
        }

        // Controls hint
        game.batch.begin();
        game.fontSmall.setColor(0.4f, 0.4f, 0.4f, 1);
        game.fontSmall.draw(game.batch, "Tap buttons or use on-screen controls", w/2 - 130, h * 0.12f);
        game.batch.end();
    }

    void drawButton(float x, float y, float bw, float bh, String text, int hoverCol, int col) {
        float mx = Gdx.input.getX();
        float my = Gdx.graphics.getHeight() - Gdx.input.getY();
        boolean hover = mx >= x && mx <= x + bw && my >= y && my <= y + bh;

        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        if (hover) {
            game.shapeRenderer.setColor(((hoverCol >> 16) & 0xFF)/255f,
                ((hoverCol >> 8) & 0xFF)/255f, (hoverCol & 0xFF)/255f, 1);
        } else {
            game.shapeRenderer.setColor(((col >> 16) & 0xFF)/255f,
                ((col >> 8) & 0xFF)/255f, (col & 0xFF)/255f, 1);
        }
        game.shapeRenderer.rect(x, y, bw, bh);
        game.shapeRenderer.end();

        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        game.shapeRenderer.setColor(1, 1, 1, 1);
        game.shapeRenderer.rect(x, y, bw, bh);
        game.shapeRenderer.end();

        game.batch.begin();
        game.font.setColor(1, 1, 1, 1);
        game.font.draw(game.batch, text, x + bw/2 - 60, y + bh/2 + 10);
        game.batch.end();
    }

    boolean isButtonClicked(float x, float y, float w, float h) {
        float mx = Gdx.input.getX();
        float my = Gdx.graphics.getHeight() - Gdx.input.getY();
        return mx >= x && mx <= x + w && my >= y && my <= y + h && Gdx.input.justTouched();
    }

    @Override public void show() {}
    @Override public void resize(int width, int height) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override public void dispose() {}
}
