package com.codefighters;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class WinScreen implements Screen {
    final CodeFightersGame game;
    boolean jinglePlayed = false;
    float timer = 0;

    float fightTime = 0;
    int maxCombo = 0;
    int totalDamage = 0;

    public WinScreen(final CodeFightersGame game) {
        this.game = game;
    }

    public void setStats(float time, int combo, int damage) {
        fightTime = time;
        maxCombo = combo;
        totalDamage = damage;
    }

    @Override
    public void show() {
        game.updateFontScales();
        jinglePlayed = false;
        timer = 0;
    }

    @Override
    public void render(float delta) {
        timer += delta;

        if (!jinglePlayed) {
            if (game.musicWin != null) {
                game.playMusic(game.musicWin, false);
            } else if (game.sndVictoryFanfare != null) {
                game.playSound(game.sndVictoryFanfare, 0.9f);
            } else {
                game.playSound(game.sndWinJingle, 0.65f);
            }
            if (game.sndAnnouncerWinner != null) {
                game.playSound(game.sndAnnouncerWinner, 0.85f);
            }
            jinglePlayed = true;
        }

        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();

        Gdx.gl.glClearColor(0.01f, 0.04f, 0.02f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Background
        game.batch.begin();
        com.badlogic.gdx.graphics.Texture bg = game.texBgWin != null ? game.texBgWin : game.texBgMenu;
        if (bg != null) {
            game.batch.setColor(0.55f, 0.85f, 0.6f, 1f);
            game.batch.draw(bg, 0, 0, w, h);
            game.batch.setColor(1, 1, 1, 1);
        }
        game.batch.end();

        // Dark overlay
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        game.shapeRenderer.setColor(0, 0, 0, 0.58f);
        game.shapeRenderer.rect(0, 0, w, h);
        game.shapeRenderer.end();

        // Victory confetti / spark particles
        if (game.texFxConfetti != null) {
            game.batch.begin();
            for (int i = 0; i < 15; i++) {
                float cx = (float) ((Math.sin(timer * 0.7f + i * 1.5f) * 0.45f + 0.5f) * w);
                float cy = ((timer * 60f + i * 40f) % (h + 100)) - 50;
                game.batch.draw(game.texFxConfetti, cx, h - cy, 28, 28);
            }
            game.batch.end();
        } else {
            game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            for (int i = 0; i < 22; i++) {
                float px = (float) (Math.sin(timer * 0.9f + i * 1.4f) * w * 0.46f + w / 2f);
                float py = (float) (Math.cos(timer * 1.1f + i * 2.0f) * h * 0.28f + h * 0.52f);
                game.shapeRenderer.setColor(0.1f, 0.85f, 0.3f, 0.55f);
                game.shapeRenderer.circle(px, py, 3 + (i % 3), 8);
            }
            game.shapeRenderer.end();
        }

        // Winner Banner Texture or Rendered
        float bannerY = h * 0.77f;
        float bannerH = h * 0.16f;

        if (game.texWinnerBanner != null) {
            game.batch.begin();
            float bw = Math.min(w * 0.75f, 540);
            game.batch.draw(game.texWinnerBanner, w / 2f - bw / 2f, bannerY, bw, bannerH);
            game.batch.end();
        } else {
            game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            game.shapeRenderer.setColor(0.0f, 0.30f, 0.08f, 0.92f);
            game.shapeRenderer.rect(0, bannerY, w, bannerH);
            game.shapeRenderer.end();

            game.shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            game.shapeRenderer.setColor(0.2f, 1f, 0.4f, 1f);
            game.shapeRenderer.rect(0, bannerY, w, bannerH);
            game.shapeRenderer.end();

            // "VICTORY!" text
            game.batch.begin();
            game.fontTitle.setColor(0.2f, 1f, 0.35f, 1f);
            game.drawTextCentered(game.fontTitle, "VICTORY!", w / 2f, bannerY + bannerH / 2f);
            game.batch.end();
        }

        // Subtitle
        game.batch.begin();
        game.fontBold.setColor(0.85f, 1f, 0.85f, 1f);
        game.drawTextCentered(game.fontBold, "WannaCry neutralized! Security protocols restored.", w / 2f, h * 0.71f);
        game.batch.end();

        // === Stats card ===
        float cardX = w * 0.32f;
        float cardW = w * 0.62f;
        float cardH = h * 0.34f;
        float cardY = h * 0.32f;

        // Draw victory character on left
        game.batch.begin();
        com.badlogic.gdx.graphics.Texture winTex = game.texByteWin1 != null ? game.texByteWin1 : game.texPlayerIdle;
        if (winTex != null) {
            float charH = cardH * 1.35f;
            float charW = charH * (float) winTex.getWidth() / (float) winTex.getHeight();
            game.batch.draw(winTex, w * 0.08f, cardY - 10, charW, charH);
        }
        game.batch.end();

        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        game.shapeRenderer.setColor(0.04f, 0.10f, 0.05f, 0.92f);
        game.shapeRenderer.rect(cardX, cardY, cardW, cardH);
        game.shapeRenderer.end();

        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        game.shapeRenderer.setColor(0.2f, 0.8f, 0.35f, 0.9f);
        game.shapeRenderer.rect(cardX, cardY, cardW, cardH);
        game.shapeRenderer.end();

        int mins = (int)(fightTime / 60);
        int secs = (int)(fightTime % 60);
        String timeStr = String.format("%d:%02d", mins, secs);

        float padX = cardX + 35;
        float valX = cardX + cardW - 35;

        game.batch.begin();
        // Stats Header
        game.fontBold.setColor(0.3f, 1f, 0.5f, 1f);
        game.fontBold.draw(game.batch, "MISSION PERFORMANCE", padX, cardY + cardH - 25);

        // Row 1: Time
        float r1Y = cardY + cardH - 75;
        game.font.setColor(0.8f, 0.85f, 0.8f, 1f);
        game.font.draw(game.batch, "Clear Time:", padX, r1Y);
        game.fontBold.setColor(0.3f, 1f, 0.4f, 1f);
        float tW = game.getTextWidth(game.fontBold, timeStr);
        game.fontBold.draw(game.batch, timeStr, valX - tW, r1Y);

        // Row 2: Max Combo
        float r2Y = cardY + cardH - 130;
        game.font.setColor(0.8f, 0.85f, 0.8f, 1f);
        game.font.draw(game.batch, "Max Combo:", padX, r2Y);
        game.fontBold.setColor(1f, 0.9f, 0.2f, 1f);
        String comboStr = maxCombo + " HITS";
        float cW = game.getTextWidth(game.fontBold, comboStr);
        game.fontBold.draw(game.batch, comboStr, valX - cW, r2Y);

        // Row 3: Total Damage
        float r3Y = cardY + cardH - 185;
        game.font.setColor(0.8f, 0.85f, 0.8f, 1f);
        game.font.draw(game.batch, "Damage Dealt:", padX, r3Y);
        game.fontBold.setColor(0.31f, 0.85f, 1.0f, 1f);
        String dmgStr = totalDamage + " PTS";
        float dW = game.getTextWidth(game.fontBold, dmgStr);
        game.fontBold.draw(game.batch, dmgStr, valX - dW, r3Y);
        game.batch.end();

        // === Action Buttons ===
        float btnW = w * 0.23f;
        float btnH = h * 0.088f;
        float btnY = h * 0.16f;

        drawButton(w * 0.12f, btnY, btnW, btnH, "REMATCH", 0x00c853, 0x0a5c28);
        if (isButtonClicked(w * 0.12f, btnY, btnW, btnH)) {
            game.playSound(game.sndMenuSelect, 0.8f);
            game.fightScreen.resetFight();
            game.setScreen(game.fightScreen);
        }

        drawButton(w * 0.385f, btnY, btnW, btnH, "GALLERY", 0x7c4dff, 0x311b92);
        if (isButtonClicked(w * 0.385f, btnY, btnW, btnH)) {
            game.playSound(game.sndMenuSelect, 0.8f);
            game.setScreen(game.galleryScreen);
        }

        drawButton(w * 0.65f, btnY, btnW, btnH, "MENU", 0x546e7a, 0x263238);
        if (isButtonClicked(w * 0.65f, btnY, btnW, btnH)) {
            game.playSound(game.sndMenuSelect, 0.8f);
            game.setScreen(game.menuScreen);
        }

        // Gallery unlock notice
        game.batch.begin();
        game.fontSmall.setColor(0.5f, 0.85f, 0.5f, 1f);
        game.drawTextCentered(game.fontSmall, "* WannaCry threat intelligence unlocked in Gallery *", w / 2f, h * 0.06f);
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
