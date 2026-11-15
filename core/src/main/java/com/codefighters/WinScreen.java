package com.codefighters;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class WinScreen implements Screen {
    final CodeFightersGame game;
    float timer = 0;

    public WinScreen(final CodeFightersGame game) {
        this.game = game;
    }

    @Override
    public void render(float delta) {
        timer += delta;
        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();

        Gdx.gl.glClearColor(0, 0.08f, 0.02f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Animated background particles
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (int i = 0; i < 15; i++) {
            float px = (float)(Math.sin(timer * 0.5 + i * 1.3) * w * 0.4 + w/2);
            float py = (float)(Math.cos(timer * 0.3 + i * 0.9) * h * 0.3 + h * 0.5f);
            game.shapeRenderer.setColor(0, 1, 0.3f, 0.3f);
            game.shapeRenderer.circle(px, py, 3 + (float)Math.sin(timer + i) * 2, 8);
        }
        game.shapeRenderer.end();

        game.batch.begin();

        // Title
        game.fontBold.setColor(0, 1, 0.3f, 1);
        game.fontBold.draw(game.batch, "SYSTEM SECURED!", w/2 - 220, h * 0.72f);

        // Subtitle
        game.font.setColor(0.7f, 0.7f, 0.7f, 1);
        game.font.draw(game.batch, "WannaCry has been neutralized.", w/2 - 180, h * 0.62f);

        // Educational tip
        game.fontSmall.setColor(0, 1, 0.5f, 0.9f);
        game.fontSmall.draw(game.batch, "Did you know? Always keep your OS updated to prevent ransomware attacks.",
            w/2 - 250, h * 0.55f);

        // Unlocked message
        game.fontBold.setColor(1, 0.92f, 0.23f, 1);
        game.fontBold.draw(game.batch, "GALLERY UNLOCKED!", w/2 - 150, h * 0.45f);

        game.fontSmall.setColor(0.7f, 0.7f, 0.7f, 1);
        game.fontSmall.draw(game.batch, "You can now view WannaCry's full data in the Gallery.",
            w/2 - 200, h * 0.40f);

        game.batch.end();

        // Buttons
        float btnW = 260, btnH = 55;
        float btnX = w/2 - btnW/2;

        drawButton(btnX, h * 0.28f, btnW, btnH, "PLAY AGAIN", 0x00e5ff, 0x0091ea);
        if (isButtonClicked(btnX, h * 0.28f, btnW, btnH)) {
            game.fightScreen.resetFight();
            game.setScreen(game.fightScreen);
        }

        drawButton(btnX, h * 0.18f, btnW, btnH, "MAIN MENU", 0x7c4dff, 0x311b92);
        if (isButtonClicked(btnX, h * 0.18f, btnW, btnH)) {
            game.setScreen(game.menuScreen);
        }

        drawButton(btnX, h * 0.08f, btnW, btnH, "GALLERY", 0x00e676, 0x00c853);
        if (isButtonClicked(btnX, h * 0.08f, btnW, btnH)) {
            game.setScreen(game.galleryScreen);
        }
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
        game.font.draw(game.batch, text, x + w/2 - 60, y + h/2 + 10);
        game.batch.end();
    }

    boolean isButtonClicked(float x, float y, float w, float h) {
        float mx = Gdx.input.getX();
        float my = Gdx.graphics.getHeight() - Gdx.input.getY();
        return mx >= x && mx <= x + w && my >= y && my <= y + h && Gdx.input.justTouched();
    }

    @Override public void show() { timer = 0; }
    @Override public void resize(int width, int height) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override public void dispose() {}
}
