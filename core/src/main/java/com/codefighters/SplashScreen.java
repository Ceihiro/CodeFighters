package com.codefighters;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;

public class SplashScreen implements Screen {
    final CodeFightersGame game;
    float timer = 0;
    static final float DURATION = 2.5f;

    public SplashScreen(final CodeFightersGame game) {
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

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Fade in for first 0.8s, hold, fade out last 0.8s
        float alpha;
        if (timer < 0.8f) {
            alpha = timer / 0.8f;
        } else if (timer < DURATION - 0.8f) {
            alpha = 1f;
        } else {
            alpha = Math.max(0, (DURATION - timer) / 0.8f);
        }

        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();

        game.batch.begin();

        // Studio name
        game.fontTitle.setColor(0.31f, 0.85f, 1.0f, alpha);
        game.drawTextCentered(game.fontTitle, "C0D3_4U Studio", w / 2f, h / 2f + h * 0.035f);

        // Subtitle
        game.font.setColor(0.6f, 0.65f, 0.7f, alpha * 0.8f);
        game.drawTextCentered(game.font, "PRESENTS", w / 2f, h / 2f - h * 0.045f);

        game.batch.end();

        // Auto-transition after splash ends or on touch
        if (timer >= DURATION || Gdx.input.justTouched()) {
            game.setScreen(game.menuScreen);
        }
    }

    @Override public void resize(int width, int height) {
        game.updateFontScales();
    }
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override public void dispose() {}
}
