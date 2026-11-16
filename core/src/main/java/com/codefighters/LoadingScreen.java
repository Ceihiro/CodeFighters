package com.codefighters;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class LoadingScreen implements Screen {
    final CodeFightersGame game;
    float timer = 0;
    float loadProgress = 0;
    int tipIndex = 0;
    Screen targetScreen;

    static final String[] TIPS = {
        "PRO TIP: Double-tap forward to Dash into a high-damage Dash Attack!",
        "PRO TIP: Double-tap backward for an evasive Backdash with invulnerability frames!",
        "PRO TIP: Hold Block to deploy your cyber firewall and reduce damage by 70%!",
        "PRO TIP: Fill your Super Art gauge by landing strikes, then unleash SPECIAL!",
        "PRO TIP: In Same-Device Versus, two players can duel simultaneously on one screen!",
        "PRO TIP: First fighter to score 2 round wins takes the entire match!"
    };

    public LoadingScreen(final CodeFightersGame game) {
        this.game = game;
    }

    public void setTarget(Screen target) {
        this.targetScreen = target;
        this.loadProgress = 0;
        this.timer = 0;
        this.tipIndex = (int) (Math.random() * TIPS.length);
    }

    @Override
    public void show() {
        game.updateFontScales();
        timer = 0;
        loadProgress = 0;
    }

    @Override
    public void render(float delta) {
        timer += delta;
        loadProgress = Math.min(1.0f, timer / 1.5f);

        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();

        Gdx.gl.glClearColor(0.02f, 0.02f, 0.06f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Background
        game.batch.begin();
        if (game.texBgLoading != null) {
            game.batch.draw(game.texBgLoading, 0, 0, w, h);
        }
        game.batch.end();

        // Cyber grid background overlay
        if (game.texBgLoading == null) {
            game.shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            game.shapeRenderer.setColor(0.06f, 0.12f, 0.22f, 1);
            for (int i = 0; i < w; i += 50) game.shapeRenderer.line(i, 0, i, h);
            for (int i = 0; i < h; i += 50) game.shapeRenderer.line(0, i, w, i);
            game.shapeRenderer.end();
        }

        // Title
        game.batch.begin();
        game.fontTitle.setColor(0.31f, 0.76f, 0.97f, 1);
        game.drawTextCentered(game.fontTitle, "INITIALIZING PROTOCOLS...", w / 2f, h * 0.75f);
        game.batch.end();

        // Tip Card
        float cardW = w * 0.82f;
        float cardH = h * 0.22f;
        float cardX = w / 2f - cardW / 2f;
        float cardY = h * 0.42f;

        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        game.shapeRenderer.setColor(0.05f, 0.08f, 0.14f, 0.92f);
        game.shapeRenderer.rect(cardX, cardY, cardW, cardH);
        game.shapeRenderer.end();

        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        game.shapeRenderer.setColor(0.31f, 0.76f, 0.97f, 0.85f);
        game.shapeRenderer.rect(cardX, cardY, cardW, cardH);
        game.shapeRenderer.end();

        game.batch.begin();
        game.fontBold.setColor(1.0f, 0.88f, 0.2f, 1f);
        game.drawTextCentered(game.fontBold, "TACTICAL INTEL", w / 2f, cardY + cardH * 0.78f);

        game.font.setColor(0.92f, 0.95f, 1f, 1f);
        game.drawTextCentered(game.font, TIPS[tipIndex], w / 2f, cardY + cardH * 0.38f);
        game.batch.end();

        // Progress Bar
        float barW = w * 0.72f;
        float barH = h * 0.052f;
        float barX = w / 2f - barW / 2f;
        float barY = h * 0.22f;

        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        // Bar background
        game.shapeRenderer.setColor(0.08f, 0.10f, 0.16f, 1f);
        game.shapeRenderer.rect(barX, barY, barW, barH);
        // Bar fill
        game.shapeRenderer.setColor(0.0f, 0.85f, 1.0f, 1f);
        game.shapeRenderer.rect(barX, barY, barW * loadProgress, barH);
        game.shapeRenderer.end();

        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        game.shapeRenderer.setColor(0.4f, 0.9f, 1.0f, 1f);
        game.shapeRenderer.rect(barX, barY, barW, barH);
        game.shapeRenderer.end();

        // Progress percentage text
        game.batch.begin();
        game.fontSmall.setColor(1, 1, 1, 1);
        int pct = (int) (loadProgress * 100f);
        game.drawTextCentered(game.fontSmall, "LOADING... " + pct + "%", w / 2f, barY + barH / 2f);
        game.batch.end();

        if (loadProgress >= 1.0f) {
            if (targetScreen != null) {
                game.setScreen(targetScreen);
            } else {
                game.setScreen(game.menuScreen);
            }
        }
    }

    @Override public void resize(int width, int height) { game.updateFontScales(); }
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override public void dispose() {}
}
