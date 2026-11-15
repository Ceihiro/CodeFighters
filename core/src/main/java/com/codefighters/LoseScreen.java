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
    public void render(float delta) {
        timer += delta;
        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();

        Gdx.gl.glClearColor(0.08f, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Glitch effect background
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (int i = 0; i < 10; i++) {
            float gx = (float)(Math.random() * w);
            float gy = (float)(Math.random() * h);
            float gw = (float)(Math.random() * 100 + 50);
            float gh = (float)(Math.random() * 5 + 2);
            game.shapeRenderer.setColor(1, 0, 0, 0.2f);
            game.shapeRenderer.rect(gx, gy, gw, gh);
        }
        game.shapeRenderer.end();

        game.batch.begin();

        // Title
        game.fontBold.setColor(1, 0, 0, 1);
        game.fontBold.draw(game.batch, "SYSTEM ENCRYPTED!", w/2 - 240, h * 0.72f);

        // Subtitle
        game.font.setColor(0.7f, 0.7f, 0.7f, 1);
        game.font.draw(game.batch, "WannaCry has taken over your system.", w/2 - 200, h * 0.62f);

        // Tip
        game.fontSmall.setColor(1, 0.3f, 0.3f, 0.9f);
        game.fontSmall.draw(game.batch, "Tip: Hold Block (D) to reduce damage. Counter-attack after enemy swings!",
            w/2 - 260, h * 0.55f);

        game.fontSmall.setColor(0.5f, 0.5f, 0.5f, 1);
        game.fontSmall.draw(game.batch, "Try using your 3-hit combo (tap A 3 times) for consistent damage.",
            w/2 - 230, h * 0.48f);

        game.batch.end();

        // Buttons
        float btnW = 260, btnH = 55;
        float btnX = w/2 - btnW/2;

        drawButton(btnX, h * 0.32f, btnW, btnH, "TRY AGAIN", 0xff1744, 0xb71c1c);
        if (isButtonClicked(btnX, h * 0.32f, btnW, btnH)) {
            game.fightScreen.resetFight();
            game.setScreen(game.fightScreen);
        }

        drawButton(btnX, h * 0.22f, btnW, btnH, "MAIN MENU", 0x7c4dff, 0x311b92);
        if (isButtonClicked(btnX, h * 0.22f, btnW, btnH)) {
            game.setScreen(game.menuScreen);
        }

        drawButton(btnX, h * 0.12f, btnW, btnH, "GALLERY", 0x00e676, 0x00c853);
        if (isButtonClicked(btnX, h * 0.12f, btnW, btnH)) {
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
