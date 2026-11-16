package com.codefighters;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class VersusVsScreen implements Screen {
    final CodeFightersGame game;
    float timer = 0;
    static final float DURATION = 2.6f;

    boolean soundPlayed = false;

    public VersusVsScreen(final CodeFightersGame game) {
        this.game = game;
    }

    @Override
    public void show() {
        game.updateFontScales();
        timer = 0;
        soundPlayed = false;
        if (game.sndRoundStart != null) {
            game.playSound(game.sndRoundStart, 0.85f);
            soundPlayed = true;
        }
    }

    @Override
    public void render(float delta) {
        timer += delta;
        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();

        Gdx.gl.glClearColor(0.02f, 0.02f, 0.06f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Background
        game.batch.begin();
        Texture bg = game.texBgVersus != null ? game.texBgVersus : game.texBgFight;
        if (bg != null) {
            game.batch.setColor(0.35f, 0.35f, 0.42f, 1);
            game.batch.draw(bg, 0, 0, w, h);
            game.batch.setColor(1, 1, 1, 1);
        }
        game.batch.end();

        // Dark overlay
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        game.shapeRenderer.setColor(0, 0, 0, 0.65f);
        game.shapeRenderer.rect(0, 0, w, h);

        // Center split line
        game.shapeRenderer.setColor(1.0f, 0.7f, 0.1f, 1f);
        game.shapeRenderer.rect(w / 2f - 3, 0, 6, h);
        game.shapeRenderer.end();

        if (game.texVsScreenDivider != null) {
            game.batch.begin();
            game.batch.draw(game.texVsScreenDivider, w / 2f - 16, 0, 32, h);
            game.batch.end();
        }

        // Animated slide-in
        float slideIn = Math.min(1f, timer / 0.55f);
        float easeSlide = 1f - (1f - slideIn) * (1f - slideIn);

        float p1TargetX = w * 0.22f;
        float p2TargetX = w * 0.78f;
        float p1StartX = -w * 0.35f;
        float p2StartX = w * 1.35f;

        float p1X = p1StartX + (p1TargetX - p1StartX) * easeSlide;
        float p2X = p2StartX + (p2TargetX - p2StartX) * easeSlide;

        float groundY = h * 0.20f;
        float spriteH = h * 0.62f;
        float spriteW = spriteH;

        // Ground shadows
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        game.shapeRenderer.setColor(0, 0, 0, 0.55f);
        game.shapeRenderer.ellipse(p1X - spriteW * 0.25f, groundY - 10, spriteW * 0.5f, 24);
        game.shapeRenderer.ellipse(p2X - spriteW * 0.25f, groundY - 10, spriteW * 0.5f, 24);
        game.shapeRenderer.end();

        // Draw sprites
        boolean isFrame2 = (((int)(timer * 2.8f)) % 2 == 1);
        Texture tex = (isFrame2 && game.texPlayerIdle2 != null) ? game.texPlayerIdle2 : game.texPlayerIdle;

        game.batch.begin();
        if (tex != null) {
            // P1 Left (Cyan normal)
            game.batch.setColor(1, 1, 1, 1);
            game.batch.draw(tex, p1X - spriteW / 2f, groundY, spriteW, spriteH);

            // P2 Right (Orange tint, flipped)
            game.batch.setColor(1.0f, 0.65f, 0.2f, 1);
            game.batch.draw(tex,
                p2X - spriteW / 2f, groundY, spriteW, spriteH,
                0, 0, tex.getWidth(), tex.getHeight(), true, false);
            game.batch.setColor(1, 1, 1, 1);
        }
        game.batch.end();

        // Center VS Impact
        float pulse = 1f + (float) Math.sin(timer * 8f) * 0.08f;
        game.batch.begin();
        if (game.texVsBadge != null) {
            float badgeSize = 130 * pulse;
            game.batch.draw(game.texVsBadge, w / 2f - badgeSize / 2f, h * 0.52f - badgeSize / 2f, badgeSize, badgeSize);
        } else {
            game.fontTitle.setColor(1, 0.85f, 0.1f, 1);
            game.fontTitle.getData().setScale(game.fontTitle.getScaleX() * pulse);
            game.drawTextCentered(game.fontTitle, "VS", w / 2f, h * 0.52f);
            game.updateFontScales();
        }
        game.batch.end();

        // Name plates
        float plateH = h * 0.11f;
        float plateY = h * 0.08f;
        float plateW = w * 0.42f;

        // P1 Plate
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        game.shapeRenderer.setColor(0.02f, 0.25f, 0.35f, 0.88f);
        game.shapeRenderer.rect(w * 0.05f, plateY, plateW, plateH);
        game.shapeRenderer.end();

        // P2 Plate
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        game.shapeRenderer.setColor(0.35f, 0.15f, 0.02f, 0.88f);
        game.shapeRenderer.rect(w * 0.53f, plateY, plateW, plateH);
        game.shapeRenderer.end();

        game.batch.begin();
        game.fontBold.setColor(0.31f, 0.9f, 1.0f, 1);
        game.drawTextCentered(game.fontBold, "PLAYER 1: CAT", w * 0.05f + plateW / 2f, plateY + plateH * 0.65f);
        game.fontSmall.setColor(0.7f, 0.9f, 1.0f, 1);
        game.drawTextCentered(game.fontSmall, "Blue Team - Left Controls", w * 0.05f + plateW / 2f, plateY + plateH * 0.28f);

        game.fontBold.setColor(1.0f, 0.70f, 0.2f, 1);
        game.drawTextCentered(game.fontBold, "PLAYER 2: CAT", w * 0.53f + plateW / 2f, plateY + plateH * 0.65f);
        game.fontSmall.setColor(1.0f, 0.85f, 0.7f, 1);
        game.drawTextCentered(game.fontSmall, "Orange Team - Right Controls", w * 0.53f + plateW / 2f, plateY + plateH * 0.28f);
        game.batch.end();

        if (timer >= DURATION || (timer > 0.8f && Gdx.input.justTouched())) {
            game.fightScreen.setMode(GameMode.VERSUS);
            game.setScreen(game.fightScreen);
        }
    }

    @Override public void resize(int width, int height) { game.updateFontScales(); }
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override public void dispose() {}
}
