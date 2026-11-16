package com.codefighters;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class GalleryDetailScreen implements Screen {
    final CodeFightersGame game;
    float timer = 0;
    String entityId = "cat"; // "cat" or "wannacry"

    public GalleryDetailScreen(final CodeFightersGame game) {
        this.game = game;
    }

    public void setEntity(String id) {
        this.entityId = id;
    }

    @Override
    public void show() {
        game.updateFontScales();
        timer = 0;
    }

    @Override
    public void render(float delta) {
        timer += delta;
        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();

        Gdx.gl.glClearColor(0.02f, 0.02f, 0.05f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Background
        game.batch.begin();
        if (game.texBgGallery != null) {
            game.batch.setColor(0.35f, 0.35f, 0.45f, 1f);
            game.batch.draw(game.texBgGallery, 0, 0, w, h);
            game.batch.setColor(1, 1, 1, 1);
        }
        game.batch.end();

        // Dark overlay / Cyber grid background
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        game.shapeRenderer.setColor(0, 0, 0, 0.65f);
        game.shapeRenderer.rect(0, 0, w, h);
        game.shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);

        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        game.shapeRenderer.setColor(0.08f, 0.10f, 0.20f, 1);
        for (int i = 0; i < w; i += 50) game.shapeRenderer.line(i, 0, i, h);
        for (int i = 0; i < h; i += 50) game.shapeRenderer.line(0, i, w, i);
        game.shapeRenderer.end();

        boolean isCat = "cat".equals(entityId);

        // Header Title
        game.batch.begin();
        game.fontTitle.setColor(isCat ? 0.31f : 1.0f, isCat ? 0.76f : 0.2f, isCat ? 0.97f : 0.3f, 1);
        game.drawTextCentered(game.fontTitle, isCat ? "DEFENDER PROFILE: CAT" : "THREAT ANALYSIS: WANNACRY", w / 2f, h * 0.93f);
        game.batch.end();

        // Left Card: Animated Sprite Showcase
        float leftW = w * 0.32f;
        float cardH = h * 0.70f;
        float cardY = h * 0.16f;
        float leftX = w * 0.06f;

        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        game.shapeRenderer.setColor(0.05f, 0.08f, 0.14f, 0.92f);
        game.shapeRenderer.rect(leftX, cardY, leftW, cardH);
        game.shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);

        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        game.shapeRenderer.setColor(isCat ? 0.31f : 1.0f, isCat ? 0.76f : 0.2f, isCat ? 0.97f : 0.3f, 0.8f);
        game.shapeRenderer.rect(leftX, cardY, leftW, cardH);
        game.shapeRenderer.end();

        // Draw animated sprite
        float spriteH = cardH * 0.65f;
        float spriteW = spriteH;
        float spriteX = leftX + leftW / 2f - spriteW / 2f;
        float spriteY = cardY + cardH * 0.25f;

        boolean isFrame2 = (((int)(timer * 2.8f)) % 2 == 1);
        Texture tex = null;
        if (isCat) {
            tex = (isFrame2 && game.texPlayerIdle2 != null) ? game.texPlayerIdle2 : game.texPlayerIdle;
        } else {
            tex = (isFrame2 && game.texEnemyIdle2 != null) ? game.texEnemyIdle2 : game.texEnemyIdle;
        }

        game.batch.begin();
        if (tex != null) {
            if (isCat) {
                game.batch.draw(tex, spriteX, spriteY, spriteW, spriteH);
            } else {
                game.batch.draw(tex, spriteX, spriteY, spriteW, spriteH,
                    0, 0, tex.getWidth(), tex.getHeight(), true, false);
            }
        }

        // Subtitle below sprite
        game.fontBold.setColor(1, 1, 1, 1);
        game.drawTextCentered(game.fontBold, isCat ? "Cat (Byte)" : "WannaCry Ransomware", leftX + leftW / 2f, cardY + cardH * 0.15f);
        game.fontSmall.setColor(0.7f, 0.8f, 0.9f, 1);
        game.drawTextCentered(game.fontSmall, isCat ? "Class: Cyber Security Lead" : "Class: Cryptoworm / Ransomware", leftX + leftW / 2f, cardY + cardH * 0.08f);
        game.batch.end();

        // Right Card: Detailed Dossier & Stats
        float rightX = w * 0.42f;
        float rightW = w * 0.52f;

        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        game.shapeRenderer.setColor(0.04f, 0.07f, 0.12f, 0.92f);
        game.shapeRenderer.rect(rightX, cardY, rightW, cardH);
        game.shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);

        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        game.shapeRenderer.setColor(0.2f, 0.4f, 0.6f, 0.8f);
        game.shapeRenderer.rect(rightX, cardY, rightW, cardH);
        game.shapeRenderer.end();

        game.batch.begin();
        float textY = cardY + cardH - 32;
        float padX = rightX + 22;
        float maxTextW = rightW - 44;

        if (isCat) {
            game.fontBold.setColor(0.31f, 0.85f, 1.0f, 1);
            game.fontBold.draw(game.batch, "TACTICAL SPECIFICATIONS", padX, textY);
            textY -= 32;

            game.fontSmall.setColor(0.9f, 0.95f, 1.0f, 1);
            game.fontSmall.draw(game.batch, "ROLE: Frontline Cyber Defense Specialist", padX, textY, maxTextW, com.badlogic.gdx.utils.Align.left, true);
            textY -= 26;
            game.fontSmall.draw(game.batch, "ATTRIBUTES: Rapid reflexes, 3-hit combo strings, invincible backdash", padX, textY, maxTextW, com.badlogic.gdx.utils.Align.left, true);
            textY -= 36;

            game.fontBold.setColor(1.0f, 0.85f, 0.2f, 1);
            game.fontBold.draw(game.batch, "COMBAT ABILITIES", padX, textY);
            textY -= 30;

            game.fontSmall.setColor(0.9f, 0.95f, 1.0f, 1);
            game.fontSmall.draw(game.batch, "- Forward Dash: Quick advance into Dash Attack (1.5x DMG)", padX, textY, maxTextW, com.badlogic.gdx.utils.Align.left, true);
            textY -= 25;
            game.fontSmall.draw(game.batch, "- Backward Dash: Full invulnerability frames (i-frames)", padX, textY, maxTextW, com.badlogic.gdx.utils.Align.left, true);
            textY -= 25;
            game.fontSmall.draw(game.batch, "- Triple Scratch: 3-hit combo chain", padX, textY, maxTextW, com.badlogic.gdx.utils.Align.left, true);
            textY -= 25;
            game.fontSmall.draw(game.batch, "- Code Beam Special: High-velocity data projectile", padX, textY, maxTextW, com.badlogic.gdx.utils.Align.left, true);
            textY -= 25;
            game.fontSmall.draw(game.batch, "- Firewall Shield: Block attacks (-70% damage reduction)", padX, textY, maxTextW, com.badlogic.gdx.utils.Align.left, true);
            textY -= 35;

            game.fontBold.setColor(0.2f, 1.0f, 0.5f, 1);
            game.fontBold.draw(game.batch, "DEFENSE STRATEGY", padX, textY);
            textY -= 28;
            game.fontSmall.setColor(0.85f, 0.9f, 0.95f, 1);
            game.fontSmall.draw(game.batch, "Keep mid-range, bait the threat's lunge, and punish with Dash Attack.", padX, textY, maxTextW, com.badlogic.gdx.utils.Align.left, true);
        } else {
            game.fontBold.setColor(1.0f, 0.3f, 0.3f, 1);
            game.fontBold.draw(game.batch, "THREAT INTEL & HISTORICAL ARCHIVE", padX, textY);
            textY -= 32;

            game.fontSmall.setColor(0.9f, 0.95f, 1.0f, 1);
            game.fontSmall.draw(game.batch, "OUTBREAK DATE: May 12, 2017", padX, textY, maxTextW, com.badlogic.gdx.utils.Align.left, true);
            textY -= 25;
            game.fontSmall.draw(game.batch, "IMPACT: 200,000+ endpoints across 150 nations infected.", padX, textY, maxTextW, com.badlogic.gdx.utils.Align.left, true);
            textY -= 25;
            game.fontSmall.draw(game.batch, "ESTIMATED LOSS: Exceeding $4 Billion in damages.", padX, textY, maxTextW, com.badlogic.gdx.utils.Align.left, true);
            textY -= 35;

            game.fontBold.setColor(1.0f, 0.85f, 0.2f, 1);
            game.fontBold.draw(game.batch, "EXPLOIT MECHANISM", padX, textY);
            textY -= 30;

            game.fontSmall.setColor(0.9f, 0.95f, 1.0f, 1);
            game.fontSmall.draw(game.batch, "Propagated via EternalBlue (MS17-010) through SMBv1.", padX, textY, maxTextW, com.badlogic.gdx.utils.Align.left, true);
            textY -= 25;
            game.fontSmall.draw(game.batch, "Encrypted hard drives and demanded Bitcoin ransom.", padX, textY, maxTextW, com.badlogic.gdx.utils.Align.left, true);
            textY -= 35;

            game.fontBold.setColor(0.2f, 1.0f, 0.5f, 1);
            game.fontBold.draw(game.batch, "COUNTERMEASURE & WEAKNESS", padX, textY);
            textY -= 28;
            game.fontSmall.setColor(0.85f, 0.9f, 0.95f, 1);
            game.fontSmall.draw(game.batch, "Combat Weakness: Block heavy attacks, punish on recovery!", padX, textY, maxTextW, com.badlogic.gdx.utils.Align.left, true);
            textY -= 25;
            game.fontSmall.draw(game.batch, "Real-world fix: Kill switch domain & security patch MS17-010.", padX, textY, maxTextW, com.badlogic.gdx.utils.Align.left, true);
        }
        game.batch.end();

        // Bottom Navigation Buttons
        float btnW = w * 0.18f;
        float btnH = h * 0.075f;
        float btnY = h * 0.045f;

        drawButton(w * 0.06f, btnY, btnW, btnH, "BACK", 0x546e7a, 0x263238);
        if (isButtonClicked(w * 0.06f, btnY, btnW, btnH)) {
            game.playSound(game.sndMenuBack, 0.8f);
            game.setScreen(game.galleryScreen);
        }

        // Toggle Entity Button
        drawButton(w * 0.76f, btnY, btnW, btnH, isCat ? "VIEW WANNACRY >" : "< VIEW CAT",
            isCat ? 0xff1744 : 0x00bcd4, isCat ? 0xb71c1c : 0x00838f);
        if (isButtonClicked(w * 0.76f, btnY, btnW, btnH)) {
            game.playSound(game.sndMenuSelect, 0.8f);
            entityId = isCat ? "wannacry" : "cat";
        }
    }

    void drawButton(float x, float y, float bw, float bh, String text, int hoverCol, int col) {
        float mx = Gdx.input.getX();
        float my = Gdx.graphics.getHeight() - Gdx.input.getY();
        boolean hover = mx >= x && mx <= x + bw && my >= y && my <= y + bh;

        int c = hover ? hoverCol : col;
        float r = ((c >> 16) & 0xFF) / 255f;
        float g = ((c >> 8) & 0xFF) / 255f;
        float b = (c & 0xFF) / 255f;

        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        game.shapeRenderer.setColor(r, g, b, hover ? 1f : 0.85f);
        game.shapeRenderer.rect(x, y, bw, bh);
        game.shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);

        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        game.shapeRenderer.setColor(r * 1.3f, g * 1.3f, b * 1.3f, 1f);
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

    @Override public void resize(int width, int height) { game.updateFontScales(); }
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override public void dispose() {}
}
