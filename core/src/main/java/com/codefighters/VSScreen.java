package com.codefighters;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class VSScreen implements Screen {
    final CodeFightersGame game;
    float timer = 0;
    static final float DURATION = 2.8f;
    float flashAlpha = 1f;

    public VSScreen(final CodeFightersGame game) {
        this.game = game;
    }

    @Override
    public void show() {
        game.updateFontScales();
        timer = 0;
        flashAlpha = 1f;
        game.playSound(game.sndRoundStart, 1.0f);
    }

    @Override
    public void render(float delta) {
        timer += delta;

        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();

        Gdx.gl.glClearColor(0.04f, 0.01f, 0.02f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Background: bg_fight with heavy dark overlay
        game.batch.begin();
        if (game.texBgFight != null) {
            game.batch.setColor(0.2f, 0.2f, 0.2f, 1);
            game.batch.draw(game.texBgFight, 0, 0, w, h);
            game.batch.setColor(1, 1, 1, 1);
        }
        game.batch.end();

        // Dark overlay
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        game.shapeRenderer.setColor(0, 0, 0, 0.72f);
        game.shapeRenderer.rect(0, 0, w, h);

        // Center split line
        if (game.texVsScreenDivider == null) {
            game.shapeRenderer.setColor(0.9f, 0.05f, 0.15f, 1f);
            game.shapeRenderer.rect(w / 2f - 3, 0, 6, h);
        }
        game.shapeRenderer.end();

        if (game.texVsScreenDivider != null) {
            game.batch.begin();
            game.batch.draw(game.texVsScreenDivider, w / 2f - 10, 0, 20, h);
            game.batch.end();
        }

        // Animated entry: fighters slide in from edges
        float slideIn = Math.min(1f, timer / 0.55f);
        float easeSlide = 1f - (1f - slideIn) * (1f - slideIn);

        float playerTargetX = w * 0.22f;
        float enemyTargetX = w * 0.78f;
        float playerStartX = -w * 0.35f;
        float enemyStartX = w * 1.35f;

        float playerX = playerStartX + (playerTargetX - playerStartX) * easeSlide;
        float enemyX = enemyStartX + (enemyTargetX - enemyStartX) * easeSlide;

        // Ground shadow beneath fighters
        float groundY = h * 0.20f;
        float spriteH = h * 0.62f;
        float spriteW = spriteH;

        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        game.shapeRenderer.setColor(0, 0, 0, 0.55f);
        game.shapeRenderer.ellipse(playerX - spriteW * 0.25f, groundY - 10, spriteW * 0.5f, 24);
        game.shapeRenderer.ellipse(enemyX - spriteW * 0.25f, groundY - 10, spriteW * 0.5f, 24);
        game.shapeRenderer.end();

        // Draw Player Sprite (left)
        boolean isFrame2 = (((int)(timer * 2.8f)) % 2 == 1);
        Texture pTex = (isFrame2 && game.texByteIdle2 != null) ? game.texByteIdle2 : game.texByteIdle1;
        if (pTex == null) pTex = game.texPlayerIdle;
        Texture eTex = (isFrame2 && game.texWannaCryIdle2 != null) ? game.texWannaCryIdle2 : game.texWannaCryIdle1;
        if (eTex == null) eTex = game.texEnemyIdle;

        game.batch.begin();
        if (pTex != null) {
            game.batch.draw(pTex, playerX - spriteW / 2f, groundY, spriteW, spriteH);
        }
        // Draw Enemy Sprite (right, facing left)
        if (eTex != null) {
            game.batch.draw(eTex,
                enemyX - spriteW / 2f, groundY, spriteW, spriteH,
                0, 0, eTex.getWidth(), eTex.getHeight(), true, false);
        }
        game.batch.end();

        // Name plates (animated fade in)
        float namePlateAlpha = Math.min(1f, Math.max(0f, (timer - 0.4f) / 0.35f));
        float plateH = h * 0.11f;
        float plateY = h * 0.08f;
        float plateW = w * 0.42f;

        // Player name plate (bottom-left)
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        game.shapeRenderer.setColor(0f, 0.45f, 0.65f, namePlateAlpha * 0.92f);
        game.shapeRenderer.rect(w * 0.03f, plateY, plateW, plateH);
        game.shapeRenderer.end();
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        game.shapeRenderer.setColor(0.31f, 0.85f, 1.0f, namePlateAlpha);
        game.shapeRenderer.rect(w * 0.03f, plateY, plateW, plateH);
        game.shapeRenderer.end();

        // Enemy name plate (bottom-right)
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        game.shapeRenderer.setColor(0.65f, 0.05f, 0.15f, namePlateAlpha * 0.92f);
        game.shapeRenderer.rect(w * 0.55f, plateY, plateW, plateH);
        game.shapeRenderer.end();
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        game.shapeRenderer.setColor(1.0f, 0.2f, 0.35f, namePlateAlpha);
        game.shapeRenderer.rect(w * 0.55f, plateY, plateW, plateH);
        game.shapeRenderer.end();

        game.batch.begin();
        // Player details
        game.fontBold.setColor(0.31f, 0.95f, 1.0f, namePlateAlpha);
        game.fontBold.draw(game.batch, "CAT", w * 0.05f, plateY + plateH - 18);
        game.font.setColor(0.85f, 0.95f, 1.0f, namePlateAlpha);
        game.font.draw(game.batch, "Game Architect * Cyber Defender", w * 0.05f, plateY + plateH * 0.35f);

        // Enemy details
        game.fontBold.setColor(1.0f, 0.35f, 0.45f, namePlateAlpha);
        game.fontBold.draw(game.batch, "WANNACRY", w * 0.57f, plateY + plateH - 18);
        game.font.setColor(1.0f, 0.75f, 0.75f, namePlateAlpha);
        game.font.draw(game.batch, "Ransomware Threat", w * 0.57f, plateY + plateH * 0.35f);
        game.batch.end();

        // Center VS Impact badge
        float vsAlpha = Math.min(1f, Math.max(0f, (timer - 0.25f) / 0.25f));
        float vsPulse = 1f + (float) Math.sin(timer * 7f) * 0.03f;

        game.batch.begin();
        if (game.texVsBadge != null) {
            float vsW = h * 0.16f * vsPulse;
            float vsH = vsW * 0.65f;
            game.batch.setColor(1, 1, 1, vsAlpha);
            game.batch.draw(game.texVsBadge, w / 2f - vsW / 2f, h * 0.58f - vsH / 2f, vsW, vsH);
            game.batch.setColor(1, 1, 1, 1);
        } else {
            game.fontTitle.getData().setScale(game.fontTitle.getData().scaleX * vsPulse * 1.5f);
            game.fontTitle.setColor(1f, 0.90f, 0.15f, vsAlpha);
            game.drawTextCentered(game.fontTitle, "VS", w / 2f, h * 0.58f);
            game.fontTitle.getData().setScale(game.fontTitle.getData().scaleX / (vsPulse * 1.5f));
        }
        game.batch.end();

        // Initial flash effect
        if (timer < 0.25f) {
            flashAlpha = 1f - (timer / 0.25f);
            game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            game.shapeRenderer.setColor(1, 1, 1, flashAlpha);
            game.shapeRenderer.rect(0, 0, w, h);
            game.shapeRenderer.end();
        }

        // Auto-transition to fight
        if (timer >= DURATION || (timer > 0.8f && Gdx.input.justTouched())) {
            game.fightScreen.setMode(GameMode.ARCADE);
            game.setScreen(game.fightScreen);
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
