package com.codefighters;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class WinScreen implements Screen {
    final CodeFightersGame game;
    boolean jinglePlayed = false;
    int frame = 0;

    public WinScreen(final CodeFightersGame game) {
        this.game = game;
    }

    @Override
    public void show() {
        jinglePlayed = false;
        frame = 0;
    }

    @Override
    public void render(float delta) {
        frame++;

        // Play win jingle only once when screen appears - using safe method
        if (!jinglePlayed) {
            game.playSound(game.sndWinJingle, 0.6f);
            jinglePlayed = true;
        }

        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();

        Gdx.gl.glClearColor(0.02f, 0.05f, 0.02f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Victory particles background
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (int i = 0; i < 20; i++) {
            float px = (float)(Math.sin(frame * 0.02 + i * 1.5) * w * 0.4 + w/2);
            float py = (float)(Math.cos(frame * 0.03 + i * 2.1) * h * 0.3 + h * 0.5);
            game.shapeRenderer.setColor(0.2f + i * 0.03f, 0.8f, 0.2f, 0.6f);
            game.shapeRenderer.circle(px, py, 4 + (i % 3) * 2, 8);
        }
        game.shapeRenderer.end();

        // VICTORY text
        game.batch.begin();
        game.fontBold.setColor(0.2f, 1f, 0.2f, 1);
        game.fontBold.draw(game.batch, "VICTORY!", w/2 - 120, h * 0.75f);

        game.font.setColor(1, 1, 1, 1);
        game.font.draw(game.batch, "WannaCry has been defeated!", w/2 - 180, h * 0.6f);
        game.fontSmall.draw(game.batch, "Gallery updated with new intel.", w/2 - 140, h * 0.5f);
        game.batch.end();

        // Buttons
        drawButton(w * 0.25f, h * 0.25f, w * 0.2f, h * 0.08f, "REMATCH", 0x00aa00, 0x006600);
        drawButton(w * 0.55f, h * 0.25f, w * 0.2f, h * 0.08f, "MENU", 0x555555, 0x222222);

        if (isButtonClicked(w * 0.25f, h * 0.25f, w * 0.2f, h * 0.08f)) {
            game.fightScreen.resetFight();
            game.setScreen(game.fightScreen);
        }
        if (isButtonClicked(w * 0.55f, h * 0.25f, w * 0.2f, h * 0.08f)) {
            game.setScreen(game.menuScreen);
        }
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
        game.font.draw(game.batch, text, x + bw/2 - 50, y + bh/2 + 10);
        game.batch.end();
    }

    boolean isButtonClicked(float x, float y, float w, float h) {
        float mx = Gdx.input.getX();
        float my = Gdx.graphics.getHeight() - Gdx.input.getY();
        return mx >= x && mx <= x + w && my >= y && my <= y + h && Gdx.input.justTouched();
    }

    @Override public void resize(int width, int height) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override public void dispose() {}
}
