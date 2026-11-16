package com.codefighters;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class MenuScreen implements Screen {
    final CodeFightersGame game;
    float frame = 0;
    int menuState = 0; // 0 = title/attract mode, 1 = main menu
    float idleTimer = 0;

    // Character idle bob
    float playerBobY = 0, enemyBobY = 0;

    public MenuScreen(final CodeFightersGame game) {
        this.game = game;
    }

    @Override
    public void show() {
        game.updateFontScales();
        menuState = 0;
        idleTimer = 0;
        frame = 0;
        game.playMusic(game.musicMenu, true);
    }

    @Override
    public void render(float delta) {
        frame += delta * 60f;
        idleTimer += delta;

        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();

        if (Gdx.input.isTouched() || Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.ANY_KEY)) {
            idleTimer = 0;
        }

        Gdx.gl.glClearColor(0.02f, 0.02f, 0.05f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Draw background
        game.batch.begin();
        if (game.texBgMenu != null) {
            game.batch.draw(game.texBgMenu, 0, 0, w, h);
        }
        game.batch.end();

        // Dark gradient overlays for high readability
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        game.shapeRenderer.setColor(0, 0, 0, 0.40f);
        game.shapeRenderer.rect(0, h * 0.5f, w, h * 0.5f);
        game.shapeRenderer.setColor(0, 0, 0, 0.68f);
        game.shapeRenderer.rect(0, 0, w, h * 0.5f);
        game.shapeRenderer.end();

        // Subtle character breathing bob
        playerBobY = (float) Math.sin(frame * 0.05f) * 6f;
        enemyBobY = (float) Math.sin(frame * 0.05f + Math.PI) * 6f;

        drawMenuCharacters(w, h);

        if (menuState == 0) {
            drawTitleScreen(w, h);
        } else {
            drawMainMenu(w, h);
        }
    }

    void drawMenuCharacters(float w, float h) {
        float spriteH = h * 0.52f;
        float spriteW = spriteH;
        float groundY = h * 0.06f;

        boolean pFrame2 = (((int)(frame / 22)) % 2 == 1);
        Texture pTex = (pFrame2 && game.texPlayerIdle2 != null) ? game.texPlayerIdle2 : game.texPlayerIdle;

        boolean eFrame2 = (((int)(frame / 22)) % 2 == 1);
        Texture eTex = (eFrame2 && game.texEnemyIdle2 != null) ? game.texEnemyIdle2 : game.texEnemyIdle;

        // Player on bottom-left
        game.batch.begin();
        if (pTex != null) {
            game.batch.setColor(1, 1, 1, 0.88f);
            game.batch.draw(pTex, w * 0.02f, groundY + playerBobY, spriteW, spriteH);
            game.batch.setColor(1, 1, 1, 1);
        }
        // Enemy on bottom-right (facing left)
        if (eTex != null) {
            game.batch.setColor(1, 1, 1, 0.88f);
            game.batch.draw(eTex,
                w * 0.66f, groundY + enemyBobY, spriteW, spriteH,
                0, 0, eTex.getWidth(), eTex.getHeight(), true, false);
            game.batch.setColor(1, 1, 1, 1);
        }
        game.batch.end();
    }

    void drawTitleScreen(float w, float h) {
        // Large Title Logo
        Texture titleTex = game.texTitleLogoLarge != null ? game.texTitleLogoLarge : game.texTitleLogo;
        float logoW = Math.min(w * 0.70f, 620);
        float logoH = (titleTex != null) ? logoW * ((float) titleTex.getHeight() / (float) titleTex.getWidth()) : logoW * 0.25f;
        float logoX = w / 2f - logoW / 2f;
        float logoY = h * 0.60f;

        game.batch.begin();
        if (game.texTitleLogoGlow != null) {
            float glowPulse = 0.5f + (float) Math.sin(frame * 0.05f) * 0.25f;
            game.batch.setColor(1, 1, 1, glowPulse);
            game.batch.draw(game.texTitleLogoGlow, logoX - 10, logoY - 10, logoW + 20, logoH + 20);
            game.batch.setColor(1, 1, 1, 1);
        }
        if (titleTex != null) {
            float pulse = 1f + (float) Math.sin(frame * 0.035f) * 0.015f;
            float pw = logoW * pulse;
            float ph = logoH * pulse;
            game.batch.draw(titleTex, logoX + (logoW - pw) / 2f, logoY, pw, ph);
        } else {
            game.fontTitle.setColor(0.31f, 0.76f, 0.97f, 1);
            game.drawTextCentered(game.fontTitle, "CODE FIGHTERS", w / 2f, logoY + logoH * 0.6f);
        }
        game.batch.end();

        // Subtitle
        game.batch.begin();
        game.font.setColor(0.7f, 0.85f, 1.0f, 0.9f);
        game.drawTextCentered(game.font, "Security Awareness Arcade Fighter", w / 2f, logoY - h * 0.04f);
        game.batch.end();

        // Blinking "TAP TO START"
        boolean blink = ((int)(frame / 28)) % 2 == 0;
        if (blink) {
            float tapW = w * 0.46f;
            float tapH = h * 0.085f;
            float tapX = w / 2f - tapW / 2f;
            float tapY = h * 0.42f;

            game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            game.shapeRenderer.setColor(0.0f, 0.56f, 0.76f, 0.35f);
            game.shapeRenderer.rect(tapX, tapY, tapW, tapH);
            game.shapeRenderer.end();

            game.shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            game.shapeRenderer.setColor(0.31f, 0.85f, 1.0f, 0.9f);
            game.shapeRenderer.rect(tapX, tapY, tapW, tapH);
            game.shapeRenderer.end();

            game.batch.begin();
            game.fontBold.setColor(0.31f, 0.9f, 1.0f, 1f);
            game.drawTextCentered(game.fontBold, "TAP TO START", w / 2f, tapY + tapH / 2f);
            game.batch.end();
        }

        // Bottom credits
        game.batch.begin();
        game.fontSmall.setColor(0.5f, 0.5f, 0.5f, 1f);
        game.drawTextCentered(game.fontSmall, "v1.0.0", w / 2f, h * 0.04f);
        game.batch.end();

        if (Gdx.input.justTouched()) {
            game.playSound(game.sndMenuSelect, 1.0f);
            menuState = 1;
            idleTimer = 0;
        }
    }

    void drawMainMenu(float w, float h) {
        // Title logo at top (compact)
        Texture titleTex = game.texTitleLogoLarge != null ? game.texTitleLogoLarge : game.texTitleLogo;
        float logoW = Math.min(w * 0.52f, 480);
        float logoH = (titleTex != null) ? logoW * ((float) titleTex.getHeight() / (float) titleTex.getWidth()) : logoW * 0.25f;
        float logoX = w / 2f - logoW / 2f;
        float logoY = h * 0.72f;

        game.batch.begin();
        if (titleTex != null) {
            game.batch.draw(titleTex, logoX, logoY, logoW, logoH);
        } else {
            game.fontBold.setColor(0.31f, 0.76f, 0.97f, 1);
            game.drawTextCentered(game.fontBold, "CODE FIGHTERS", w / 2f, logoY + logoH / 2f);
        }
        game.batch.end();

        // Stacked menu buttons
        float btnW = w * 0.44f;
        float btnH = h * 0.078f;
        float btnX = w / 2f - btnW / 2f;
        float startY = h * 0.65f;
        float gap = btnH * 1.18f;

        // 1. ARCADE MODE
        drawMenuButton(btnX, startY, btnW, btnH, "ARCADE MODE", 0x00bcd4, 0x004d5a, true);
        if (isButtonClicked(btnX, startY, btnW, btnH)) {
            game.currentMode = GameMode.ARCADE;
            game.setScreen(game.charSelectScreen);
        }

        // 2. VERSUS MODE
        drawMenuButton(btnX, startY - gap, btnW, btnH, "VERSUS MODE (2P)", 0xff9800, 0xbf360c, false);
        if (isButtonClicked(btnX, startY - gap, btnW, btnH)) {
            game.currentMode = GameMode.VERSUS;
            game.setScreen(game.versusCharSelectScreen);
        }

        // 3. GALLERY
        drawMenuButton(btnX, startY - gap * 2, btnW, btnH, "GALLERY", 0x7c4dff, 0x2e185c, false);
        if (isButtonClicked(btnX, startY - gap * 2, btnW, btnH)) {
            game.setScreen(game.galleryScreen);
        }

        // 4. HOW TO PLAY
        drawMenuButton(btnX, startY - gap * 3, btnW, btnH, "HOW TO PLAY", 0x26a69a, 0x0d473d, false);
        if (isButtonClicked(btnX, startY - gap * 3, btnW, btnH)) {
            game.setScreen(game.howToPlayScreen);
        }

        // 5. SETTINGS
        drawMenuButton(btnX, startY - gap * 4, btnW, btnH, "SETTINGS", 0x546e7a, 0x263238, false);
        if (isButtonClicked(btnX, startY - gap * 4, btnW, btnH)) {
            game.setScreen(game.settingsScreen);
        }

        // 6. CREDITS
        drawMenuButton(btnX, startY - gap * 5, btnW, btnH, "CREDITS", 0x455a64, 0x1c2833, false);
        if (isButtonClicked(btnX, startY - gap * 5, btnW, btnH)) {
            game.setScreen(game.creditsScreen);
        }

        // Footer
        game.batch.begin();
        game.fontSmall.setColor(0.5f, 0.5f, 0.5f, 1f);
        game.drawTextCentered(game.fontSmall, "v1.0.0", w / 2f, h * 0.028f);
        game.batch.end();
    }

    void drawMenuButton(float x, float y, float bw, float bh, String text, int activeCol, int inactiveCol, boolean featured) {
        float mx = Gdx.input.getX();
        float my = Gdx.graphics.getHeight() - Gdx.input.getY();
        boolean hover = mx >= x && mx <= x + bw && my >= y && my <= y + bh;

        Texture btnTex = hover ? game.texBtnPressed : game.texBtnNormal;
        if (btnTex != null) {
            game.batch.begin();
            game.batch.draw(btnTex, x, y, bw, bh);
            game.batch.end();
        } else {
            int col = hover ? activeCol : inactiveCol;
            float r = ((col >> 16) & 0xFF) / 255f;
            float g = ((col >> 8) & 0xFF) / 255f;
            float b = (col & 0xFF) / 255f;

            // Button background
            game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            game.shapeRenderer.setColor(r, g, b, hover ? 0.95f : 0.75f);
            game.shapeRenderer.rect(x, y, bw, bh);
            game.shapeRenderer.end();

            // Left accent bar (thicker for featured)
            game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            game.shapeRenderer.setColor(r * 1.5f, g * 1.5f, b * 1.5f, 1f);
            game.shapeRenderer.rect(x, y, featured ? 8 : 4, bh);
            game.shapeRenderer.end();

            // Border
            game.shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            game.shapeRenderer.setColor(r * 1.4f, g * 1.4f, b * 1.4f, hover ? 1f : 0.6f);
            game.shapeRenderer.rect(x, y, bw, bh);
            game.shapeRenderer.end();
        }

        // Large, clear centered text
        game.batch.begin();
        game.fontBold.setColor(hover ? 1f : 0.92f, hover ? 1f : 0.92f, hover ? 1f : 0.92f, 1f);
        game.drawTextCentered(game.fontBold, text, x + bw / 2f, y + bh / 2f);
        game.batch.end();
    }

    boolean isButtonClicked(float x, float y, float w, float h) {
        float mx = Gdx.input.getX();
        float my = Gdx.graphics.getHeight() - Gdx.input.getY();
        boolean clicked = mx >= x && mx <= x + w && my >= y && my <= y + h && Gdx.input.justTouched();
        if (clicked) game.playSound(game.sndMenuSelect, 0.9f);
        return clicked;
    }

    @Override public void resize(int width, int height) {
        game.updateFontScales();
    }
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override public void dispose() {}
}
