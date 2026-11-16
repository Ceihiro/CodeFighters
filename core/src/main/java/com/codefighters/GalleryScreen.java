package com.codefighters;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class GalleryScreen implements Screen {
    final CodeFightersGame game;
    int tab = 0; // 0 = Characters, 1 = Enemies, 2 = Dossier
    int selectedChar = 0;

    public GalleryScreen(final CodeFightersGame game) {
        this.game = game;
    }

    @Override
    public void show() {
        game.updateFontScales();
    }

    @Override
    public void render(float delta) {
        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();

        Gdx.gl.glClearColor(0.03f, 0.03f, 0.08f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Background
        game.batch.begin();
        if (game.texBgGallery != null) {
            game.batch.setColor(0.35f, 0.35f, 0.45f, 1f);
            game.batch.draw(game.texBgGallery, 0, 0, w, h);
            game.batch.setColor(1, 1, 1, 1);
        }
        game.batch.end();

        // Dark overlay / Cyberpunk background grid
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

        // Header Title
        game.batch.begin();
        game.fontTitle.setColor(1, 1, 1, 1);
        game.drawTextCentered(game.fontTitle, "INTEL ARCHIVE", w / 2f, h * 0.94f);
        game.batch.end();

        // Layout measurements: tabs sit cleanly on top of the content card
        float cardX = w * 0.06f;
        float cardW = w * 0.88f;
        float cardY = h * 0.12f;
        float contentH = h * 0.69f;
        float contentY = cardY + contentH; // exactly h * 0.81f

        float tabH = h * 0.060f;
        float tabY = contentY; // tab bottom touches card top perfectly
        float tabW = (cardW - 20f) / 3f;

        // Draw Content Card First
        if (tab == 0) drawCharactersTab(w, h, contentY, contentH);
        else if (tab == 1) drawEnemiesTab(w, h, contentY, contentH);
        else drawDossierTab(w, h, contentY, contentH);

        // Draw Tabs on top (so they never get covered or blocked)
        float t1X = cardX;
        float t2X = cardX + tabW + 10f;
        float t3X = cardX + (tabW + 10f) * 2;

        drawTab(t1X, tabY, tabW, tabH, "CHARACTERS", tab == 0);
        drawTab(t2X, tabY, tabW, tabH, "THREATS", tab == 1);
        drawTab(t3X, tabY, tabW, tabH, "DOSSIER", tab == 2);

        if (isTabClicked(t1X, tabY, tabW, tabH)) {
            if (tab != 0) game.playSound(game.sndMenuNav, 0.7f);
            tab = 0;
        }
        if (isTabClicked(t2X, tabY, tabW, tabH)) {
            if (tab != 1) game.playSound(game.sndMenuNav, 0.7f);
            tab = 1;
        }
        if (isTabClicked(t3X, tabY, tabW, tabH)) {
            if (tab != 2) game.playSound(game.sndMenuNav, 0.7f);
            tab = 2;
        }

        // Back button
        float bBtnW = w * 0.18f;
        float bBtnH = h * 0.068f;
        drawButton(cardX, h * 0.035f, bBtnW, bBtnH, "BACK", 0x546e7a, 0x263238);
        if (isButtonClicked(cardX, h * 0.035f, bBtnW, bBtnH)) {
            game.playSound(game.sndMenuBack, 0.8f);
            game.setScreen(game.menuScreen);
        }
    }

    void drawCharactersTab(float w, float h, float contentY, float contentH) {
        float cardX = w * 0.07f;
        float cardW = w * 0.86f;
        float cardY = contentY - contentH;

        // Container
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        game.shapeRenderer.setColor(0.06f, 0.08f, 0.14f, 0.92f);
        game.shapeRenderer.rect(cardX, cardY, cardW, contentH);
        game.shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);

        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        game.shapeRenderer.setColor(0.31f, 0.76f, 0.97f, 0.9f);
        game.shapeRenderer.rect(cardX, cardY, cardW, contentH);
        game.shapeRenderer.end();

        // Character Sprite (Left side)
        float spriteH = contentH * 0.65f;
        float spriteW = spriteH * 0.6f;
        float spriteX = cardX + 30;
        float spriteY = cardY + contentH * 0.08f;

        game.batch.begin();
        if (game.texPlayerIdle != null) {
            game.batch.draw(game.texPlayerIdle, spriteX, spriteY, spriteW, spriteH);
        }
        game.batch.end();

        // Character Info (Right side)
        CharacterData c = CharacterData.getTeamLead();
        float textX = cardX + spriteW + 40;
        float textY = contentY - 40;

        game.batch.begin();
        game.fontTitle.setColor(0.31f, 0.85f, 1.0f, 1f);
        game.fontTitle.draw(game.batch, c.name, textX, textY);

        game.fontBold.setColor(0.7f, 0.85f, 1.0f, 1f);
        game.fontBold.draw(game.batch, c.subtitle, textX, textY - 50);

        game.font.setColor(0.9f, 0.9f, 0.95f, 1f);
        game.font.draw(game.batch, c.bio, textX, textY - 110, cardW - spriteW - 90, com.badlogic.gdx.utils.Align.left, true);

        float specY = textY - 240;
        game.fontBold.setColor(1.0f, 0.85f, 0.2f, 1f);
        game.fontBold.draw(game.batch, "SPECIAL: " + c.specialName, textX, specY);

        game.fontSmall.setColor(0.8f, 0.8f, 0.8f, 1f);
        game.fontSmall.draw(game.batch, c.specialDesc, textX, specY - 35, cardW - spriteW - 90, com.badlogic.gdx.utils.Align.left, true);
        game.batch.end();

        // Dossier detail button
        float dBtnW = 220;
        float dBtnH = 48;
        drawButton(cardX + cardW - dBtnW - 30, cardY + 25, dBtnW, dBtnH, "FULL DOSSIER >", 0x00bcd4, 0x006064);
        if (isButtonClicked(cardX + cardW - dBtnW - 30, cardY + 25, dBtnW, dBtnH)) {
            game.galleryDetailScreen.setEntity("cat");
            game.setScreen(game.galleryDetailScreen);
        }
    }

    void drawEnemiesTab(float w, float h, float contentY, float contentH) {
        float cardX = w * 0.07f;
        float cardW = w * 0.86f;
        float cardY = contentY - contentH;

        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        game.shapeRenderer.setColor(0.08f, 0.05f, 0.08f, 0.92f);
        game.shapeRenderer.rect(cardX, cardY, cardW, contentH);
        game.shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);

        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        game.shapeRenderer.setColor(1.0f, 0.2f, 0.35f, 0.9f);
        game.shapeRenderer.rect(cardX, cardY, cardW, contentH);
        game.shapeRenderer.end();

        EnemyData e = EnemyData.getWannaCry();
        float spriteH = contentH * 0.65f;
        float spriteW = spriteH * 0.6f;
        float spriteX = cardX + 30;
        float spriteY = cardY + contentH * 0.08f;

        game.batch.begin();
        if (game.texEnemyIdle != null) {
            game.batch.draw(game.texEnemyIdle, spriteX, spriteY, spriteW, spriteH);
        }
        game.batch.end();

        float textX = cardX + spriteW + 40;
        float textY = contentY - 40;

        game.batch.begin();
        game.fontTitle.setColor(1.0f, 0.25f, 0.35f, 1f);
        game.fontTitle.draw(game.batch, e.name, textX, textY);

        game.fontBold.setColor(1.0f, 0.6f, 0.6f, 1f);
        game.fontBold.draw(game.batch, "CLASSIFICATION: " + e.type.toUpperCase(), textX, textY - 50);

        game.font.setColor(0.95f, 0.9f, 0.9f, 1f);
        game.font.draw(game.batch, e.bio, textX, textY - 105, cardW - spriteW - 90, com.badlogic.gdx.utils.Align.left, true);

        float weakY = textY - 220;
        game.fontBold.setColor(1.0f, 0.85f, 0.2f, 1f);
        game.fontBold.draw(game.batch, "TACTICAL COUNTER:", textX, weakY);

        game.fontSmall.setColor(0.85f, 0.85f, 0.85f, 1f);
        game.fontSmall.draw(game.batch, e.weakness, textX, weakY - 35, cardW - spriteW - 90, com.badlogic.gdx.utils.Align.left, true);

        if (e.realWorldFact != null) {
            float factY = weakY - 90;
            game.fontBold.setColor(0.31f, 0.85f, 1.0f, 1f);
            game.fontBold.draw(game.batch, "REAL WORLD INTEL:", textX, factY);
            game.fontSmall.setColor(0.75f, 0.85f, 0.95f, 1f);
            game.fontSmall.draw(game.batch, e.realWorldFact, textX, factY - 35, cardW - spriteW - 90, com.badlogic.gdx.utils.Align.left, true);
        }
        game.batch.end();

        float dBtnW = 220;
        float dBtnH = 48;
        drawButton(cardX + cardW - dBtnW - 30, cardY + 25, dBtnW, dBtnH, "FULL DOSSIER >", 0xff1744, 0xb71c1c);
        if (isButtonClicked(cardX + cardW - dBtnW - 30, cardY + 25, dBtnW, dBtnH)) {
            game.galleryDetailScreen.setEntity("wannacry");
            game.setScreen(game.galleryDetailScreen);
        }
    }

    void drawCombosTab(float w, float h, float contentY, float contentH) {
        float cardX = w * 0.07f;
        float cardW = w * 0.86f;
        float cardY = contentY - contentH;

        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        game.shapeRenderer.setColor(0.06f, 0.08f, 0.14f, 0.92f);
        game.shapeRenderer.rect(cardX, cardY, cardW, contentH);
        game.shapeRenderer.end();

        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        game.shapeRenderer.setColor(0.31f, 0.76f, 0.97f, 0.9f);
        game.shapeRenderer.rect(cardX, cardY, cardW, contentH);
        game.shapeRenderer.end();

        CharacterData c = CharacterData.getTeamLead();
        float y = contentY - 35;

        game.batch.begin();
        game.fontBold.setColor(0.31f, 0.85f, 1.0f, 1f);
        game.fontBold.draw(game.batch, c.name.toUpperCase() + " - COMMAND LIST", cardX + 30, y);
        game.batch.end();

        y -= 60;
        for (String combo : c.combos) {
            game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            game.shapeRenderer.setColor(0.10f, 0.14f, 0.22f, 0.95f);
            game.shapeRenderer.rect(cardX + 30, y - 42, cardW - 60, 52);
            game.shapeRenderer.end();

            game.shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            game.shapeRenderer.setColor(0.31f, 0.76f, 0.97f, 0.5f);
            game.shapeRenderer.rect(cardX + 30, y - 42, cardW - 60, 52);
            game.shapeRenderer.end();

            game.batch.begin();
            game.font.setColor(1f, 1f, 1f, 1f);
            game.font.draw(game.batch, combo, cardX + 50, y - 8);
            game.batch.end();

            y -= 70;
        }

        game.batch.begin();
        game.fontSmall.setColor(0.6f, 0.7f, 0.8f, 1f);
        game.fontSmall.draw(game.batch, "Tip: Chain consecutive light attacks within the hit window for maximum combo multiplier!",
            cardX + 30, cardY + 30);
        game.batch.end();
    }

    void drawDossierTab(float w, float h, float contentY, float contentH) {
        float cardX = w * 0.07f;
        float cardW = w * 0.86f;
        float cardY = contentY - contentH;

        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        game.shapeRenderer.setColor(0.04f, 0.07f, 0.12f, 0.95f);
        game.shapeRenderer.rect(cardX, cardY, cardW, contentH);
        game.shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);

        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        game.shapeRenderer.setColor(0.31f, 0.76f, 0.97f, 0.9f);
        game.shapeRenderer.rect(cardX, cardY, cardW, contentH);
        game.shapeRenderer.end();

        // Centre divider
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        game.shapeRenderer.setColor(0.4f, 0.5f, 0.7f, 0.6f);
        game.shapeRenderer.line(w * 0.5f, cardY, w * 0.5f, contentY);
        game.shapeRenderer.end();

        float lx = cardX + 20f;
        float ly = contentY - 30f;
        float rx = w * 0.52f;
        float ry = contentY - 30f;

        game.batch.begin();

        // ---- LEFT: BYTE ----
        game.fontBold.setColor(0.2f, 1f, 0.85f, 1f);
        game.fontBold.draw(game.batch, "BYTE // DEFENDER", lx, ly); ly -= 32f;
        game.fontSmall.setColor(0.9f, 0.9f, 0.95f, 1f);
        game.fontSmall.draw(game.batch, "REAL NAME: CAT", lx, ly); ly -= 24f;
        game.fontSmall.draw(game.batch, "ROLE: Ethical Hacker", lx, ly); ly -= 24f;
        game.fontSmall.draw(game.batch, "CLASS: White-Hat Operative", lx, ly); ly -= 24f;
        game.fontSmall.draw(game.batch, "WEAPON: Code Blaster", lx, ly); ly -= 24f;
        game.fontSmall.draw(game.batch, "SPECIAL: Code Beam + Shield", lx, ly); ly -= 24f;
        game.fontSmall.setColor(0.2f, 1f, 0.4f, 1f);
        game.fontSmall.draw(game.batch, "STATUS: FRIENDLY", lx, ly); ly -= 32f;
        game.fontSmall.setColor(0.75f, 0.9f, 1f, 1f);
        game.fontSmall.draw(game.batch, "Bio: Elite defender trained in", lx, ly); ly -= 22f;
        game.fontSmall.draw(game.batch, "offensive security ops.", lx, ly);

        // ---- RIGHT: WANNACRY ----
        game.fontBold.setColor(1f, 0.25f, 0.3f, 1f);
        game.fontBold.draw(game.batch, "WANNACRY // THREAT", rx, ry); ry -= 32f;
        game.fontSmall.setColor(0.9f, 0.9f, 0.95f, 1f);
        game.fontSmall.draw(game.batch, "TYPE: Ransomware Worm", rx, ry); ry -= 24f;
        game.fontSmall.draw(game.batch, "CVE: MS17-010 EternalBlue", rx, ry); ry -= 24f;
        game.fontSmall.draw(game.batch, "ORIGIN: Lazarus Group", rx, ry); ry -= 24f;
        game.fontSmall.draw(game.batch, "DAMAGE: $4B Worldwide", rx, ry); ry -= 24f;
        game.fontSmall.draw(game.batch, "WEAKNESS: Kill Switch + Patch", rx, ry); ry -= 24f;
        game.fontSmall.setColor(1f, 0.25f, 0.25f, 1f);
        game.fontSmall.draw(game.batch, "STATUS: HOSTILE", rx, ry); ry -= 32f;
        game.fontSmall.setColor(1f, 0.75f, 0.75f, 1f);
        game.fontSmall.draw(game.batch, "Bio: Encrypts files, demands", rx, ry); ry -= 22f;
        game.fontSmall.draw(game.batch, "Bitcoin. 150+ countries via SMBv1.", rx, ry);

        game.batch.end();
    }

    void drawTab(float x, float y, float w, float h, String text, boolean active) {
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        game.shapeRenderer.setColor(active ? 0.16f : 0.07f, active ? 0.38f : 0.10f, active ? 0.52f : 0.15f, 0.95f);
        game.shapeRenderer.rect(x, y, w, h);
        game.shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);

        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        game.shapeRenderer.setColor(active ? 0.31f : 0.20f, active ? 0.85f : 0.35f, 1f, 1f);
        game.shapeRenderer.rect(x, y, w, h);
        game.shapeRenderer.end();

        game.batch.begin();
        game.fontBold.setColor(active ? 1f : 0.65f, active ? 1f : 0.65f, active ? 1f : 0.65f, 1f);
        game.drawTextCentered(game.fontBold, text, x + w / 2f, y + h / 2f);
        game.batch.end();
    }

    boolean isTabClicked(float x, float y, float w, float h) {
        float mx = Gdx.input.getX();
        float my = Gdx.graphics.getHeight() - Gdx.input.getY();
        return mx >= x && mx <= x + w && my >= y && my <= y + h && Gdx.input.justTouched();
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
        game.shapeRenderer.setColor(r, g, b, hover ? 1f : 0.8f);
        game.shapeRenderer.rect(x, y, bw, bh);
        game.shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);

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
