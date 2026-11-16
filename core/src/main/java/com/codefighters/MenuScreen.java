package com.codefighters;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class MenuScreen implements Screen {
    final CodeFightersGame game;

    // ── States ────────────────────────────────────────────────────────────────
    // 0 = title/attract  |  1 = glitch transition  |  2 = main menu
    int   menuState = 0;
    float frame     = 0;
    float idleTimer = 0;

    // ── Parallax ──────────────────────────────────────────────────────────────
    float scrollSky   = 0;
    float scrollFar   = 0;
    float scrollMid   = 0;
    float scrollFloor = 0;

    // ── Character run ─────────────────────────────────────────────────────────
    float runFrame     = 0;
    float byteX        = 0;
    float wannaCryBobY = 0;

    // ── Logo & blink ──────────────────────────────────────────────────────────
    float   logoBobY   = 0;
    float   blinkTimer = 0;
    boolean blinkOn    = true;

    // ── Glitch transition ─────────────────────────────────────────────────────
    float   glitchTimer  = 0;
    static final float GLITCH_DURATION = 0.45f;  // total seconds
    static final int   NUM_STRIPS      = 16;      // horizontal strips
    final float[]      stripDelay      = new float[NUM_STRIPS]; // pre-baked stagger

    // ── Screen size ───────────────────────────────────────────────────────────
    float W, H;

    // ── Speed constants ───────────────────────────────────────────────────────
    static final float SPD_SKY   = 20f;
    static final float SPD_FAR   = 45f;
    static final float SPD_MID   = 80f;
    static final float SPD_FLOOR = 160f;
    static final float SPD_RUN   = 120f;
    static final float CHAR_H_FRAC = 0.21f;

    public MenuScreen(final CodeFightersGame game) {
        this.game = game;
    }

    @Override
    public void show() {
        game.updateFontScales();
        menuState  = 0;
        idleTimer  = 0;
        frame      = 0;
        blinkTimer = 0;
        blinkOn    = true;
        scrollSky = scrollFar = scrollMid = scrollFloor = 0;
        byteX = 0;
        game.playMusic(game.musicMenu, true);
    }

    // ─────────────────────────────────────────────────────────────────────────
    @Override
    public void render(float delta) {
        W = Gdx.graphics.getWidth();
        H = Gdx.graphics.getHeight();
        frame     += delta * 60f;
        idleTimer += delta;
        if (Gdx.input.isTouched()) idleTimer = 0;

        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        switch (menuState) {
            case 0: updateParallax(delta); drawTitleScreen();     break;
            case 1: updateParallax(delta); drawGlitchTransition(delta); break;
            default:updateParallax(delta); drawMainMenu();        break;
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  PARALLAX UPDATE
    // ══════════════════════════════════════════════════════════════════════════
    void updateParallax(float delta) {
        float scale = W / 480f;
        scrollSky   += SPD_SKY   * scale * delta;
        scrollFar   += SPD_FAR   * scale * delta;
        scrollMid   += SPD_MID   * scale * delta;
        scrollFloor += SPD_FLOOR * scale * delta;

        byteX += SPD_RUN * scale * delta;
        if (byteX > W + W * CHAR_H_FRAC) byteX = -W * CHAR_H_FRAC * 2;

        runFrame += delta * 10f;
        if (runFrame >= 4f) runFrame -= 4f;

        wannaCryBobY = (float) Math.sin(frame * 0.065f) * (H * 0.025f);
        logoBobY     = (float) Math.sin(frame * 0.040f) * (H * 0.012f);

        blinkTimer += delta;
        if (blinkTimer >= 0.55f) { blinkTimer -= 0.55f; blinkOn = !blinkOn; }
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  TITLE SCREEN
    // ══════════════════════════════════════════════════════════════════════════
    void drawTitleScreen() {
        float groundY = H * 0.14f;
        float charH   = H * CHAR_H_FRAC;
        float charW   = charH;

        // Base background
        game.batch.begin();
        if (game.texBgMenu != null) game.batch.draw(game.texBgMenu, 0, 0, W, H);
        game.batch.end();

        drawTiledLayer(game.texLayerSky,   scrollSky,          0,         H);
        drawTiledLayer(game.texLayerGlow,  scrollSky * 0.8f,  0,         H);
        drawTiledLayer(game.texLayerFar,   scrollFar,          groundY,   H * 0.60f);
        drawTiledLayer(game.texLayerMid,   scrollMid,          groundY * 0.6f, H * 0.42f);
        drawRunningChars(groundY, charW, charH);
        drawTiledLayer(game.texLayerFloor, scrollFloor,        0,         groundY);

        // Transparent gradient
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        game.shapeRenderer.setColor(0f, 0f, 0f, 0.15f);
        game.shapeRenderer.rect(0, H * 0.5f, W, H * 0.5f);
        game.shapeRenderer.setColor(0f, 0f, 0f, 0.35f);
        game.shapeRenderer.rect(0, 0, W, H * 0.5f);
        game.shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);

        drawTitleUI();

        if (Gdx.input.justTouched()) {
            startGlitchTransition();
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  GLITCH TRANSITION  (plays between title and main menu)
    // ══════════════════════════════════════════════════════════════════════════
    void startGlitchTransition() {
        menuState    = 1;
        glitchTimer  = 0;
        // Stagger strips: alternate odd/even rows like Pokémon battle wipe
        // Even rows come from the top half, odd from bottom half, all sweep right→left
        for (int i = 0; i < NUM_STRIPS; i++) {
            // Every other strip is slightly delayed — creates the interlaced wipe look
            stripDelay[i] = i * (GLITCH_DURATION * 0.40f / NUM_STRIPS);
        }
        game.playSound(game.sndGlitchTransition, 1.0f);
    }

    void drawGlitchTransition(float delta) {
        glitchTimer += delta;

        float stripH    = H / NUM_STRIPS;
        // Width of the noise "brush" bar sliding across — 1.5 strips tall in width
        float noiseBarW = stripH * 2.5f;

        // ── Draw the original title scene underneath ──────────────────────────
        // (strips not yet wiped still show the scene)
        float groundY = H * 0.14f;
        float charH   = H * CHAR_H_FRAC;
        float charW   = charH;

        game.batch.begin();
        if (game.texBgMenu != null) game.batch.draw(game.texBgMenu, 0, 0, W, H);
        game.batch.end();
        drawTiledLayer(game.texLayerSky,   scrollSky,   0,             H);
        drawTiledLayer(game.texLayerFar,   scrollFar,   groundY,       H * 0.60f);
        drawTiledLayer(game.texLayerMid,   scrollMid,   groundY * 0.6f, H * 0.42f);
        drawRunningChars(groundY, charW, charH);
        drawTiledLayer(game.texLayerFloor, scrollFloor, 0,             groundY);

        // ── Draw each strip's BLACK cover + noise brush ────────────────────────
        // The black rect grows from RIGHT to LEFT as the strip wipes.
        // The noise tile rides at the LEFT edge of the black area (the brush tip).
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        for (int i = 0; i < NUM_STRIPS; i++) {
            // Y: draw strips from TOP to BOTTOM
            float stripY   = H - (i + 1) * stripH;
            float elapsed  = glitchTimer - stripDelay[i];
            // progress 0→1 over the strip's own travel window
            float p        = Math.max(0f, Math.min(1f, elapsed / (GLITCH_DURATION * 0.75f)));

            if (p <= 0f) continue;  // strip hasn't started yet

            // Leading edge X: starts at right side (W), moves left to 0
            float leadX    = W * (1f - p);
            // Black covers everything to the RIGHT of leadX
            float blackX   = leadX;
            float blackW   = W - leadX;

            // ── Black cover rect (the wiped area) ────────────────────────────
            game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            game.shapeRenderer.setColor(0f, 0f, 0f, 1f);
            game.shapeRenderer.rect(blackX, stripY, blackW, stripH);
            game.shapeRenderer.end();

            // ── Noise tile brush at the leading left edge ─────────────────────
            if (game.texGlitchNoise != null) {
                float brushX = leadX - noiseBarW;
                game.batch.begin();
                // Green-tinted noise tile, tiled across the brush height
                game.batch.setColor(0.2f, 1f, 0.45f, 0.95f);
                float tx = brushX;
                float tw = game.texGlitchNoise.getWidth() * (stripH / game.texGlitchNoise.getHeight());
                while (tx < brushX + noiseBarW) {
                    game.batch.draw(game.texGlitchNoise, tx, stripY, tw, stripH);
                    tx += tw;
                }
                game.batch.setColor(1f, 1f, 1f, 1f);
                game.batch.end();
            }
        }

        Gdx.gl.glDisable(GL20.GL_BLEND);

        // ── All strips done → switch to main menu ────────────────────────────
        boolean allDone = glitchTimer >= (stripDelay[NUM_STRIPS - 1] + GLITCH_DURATION * 0.75f + 0.05f);
        if (allDone) {
            menuState = 2;
        }
    }


    // ══════════════════════════════════════════════════════════════════════════
    //  TILED LAYER DRAW
    // ══════════════════════════════════════════════════════════════════════════
    void drawTiledLayer(Texture tex, float scrollX, float botY, float drawH) {
        if (tex == null) return;
        float texW  = tex.getWidth();
        float texH  = tex.getHeight();
        float scale = drawH / texH;
        float tileW = texW * scale;
        if (tileW < 1) tileW = W;
        float offset = scrollX % tileW;
        if (offset < 0) offset += tileW;
        game.batch.begin();
        float x = -offset;
        while (x < W) {
            game.batch.draw(tex, x, botY, tileW, drawH);
            x += tileW;
        }
        game.batch.end();
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  RUNNING CHARACTERS
    // ══════════════════════════════════════════════════════════════════════════
    void drawRunningChars(float groundY, float charW, float charH) {
        Texture runTex;
        switch ((int) runFrame) {
            case 0:  runTex = game.texRun1 != null ? game.texRun1 : game.texByteWalk1; break;
            case 1:  runTex = game.texRun2 != null ? game.texRun2 : game.texByteWalk2; break;
            case 2:  runTex = game.texRun3 != null ? game.texRun3 : game.texByteWalk1; break;
            default: runTex = game.texRun4 != null ? game.texRun4 : game.texByteWalk2; break;
        }
        Texture wcTex = game.texWannaCryWalk != null
                      ? game.texWannaCryWalk : game.texWannaCryIdle1;
        float wcX = byteX - charW * 1.5f;
        float wcY = groundY + wannaCryBobY;

        game.batch.begin();
        if (wcTex != null) {
            game.batch.setColor(1f, 1f, 1f, 0.92f);
            game.batch.draw(wcTex, wcX + charW, wcY, -charW, charH,
                0, 0, wcTex.getWidth(), wcTex.getHeight(), false, false);
            game.batch.setColor(1f, 1f, 1f, 1f);
        }
        if (runTex != null) {
            game.batch.draw(runTex, byteX, groundY, charW, charH);
        }
        game.batch.end();
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  TITLE UI
    // ══════════════════════════════════════════════════════════════════════════
    void drawTitleUI() {
        game.batch.begin();
        game.font.setColor(0.72f, 0.88f, 1.0f, 0.88f);
        game.drawTextCentered(game.font, "SECURITY AWARENESS ARCADE FIGHTER", W / 2f, H * 0.72f);
        game.batch.end();

        Texture titleTex = game.texTitleLogoLarge != null
                         ? game.texTitleLogoLarge : game.texTitleLogo;
        float logoMaxW = Math.min(W * 0.72f, 560f);
        float logoW    = logoMaxW;
        float logoH    = (titleTex != null)
                         ? logoW * ((float) titleTex.getHeight() / titleTex.getWidth())
                         : logoW * 0.28f;
        float logoX    = W / 2f - logoW / 2f;
        float logoY    = H * 0.50f + logoBobY;

        game.batch.begin();
        if (game.texTitleLogoGlow != null) {
            float glow = 0.45f + (float) Math.sin(frame * 0.045f) * 0.22f;
            game.batch.setColor(1f, 1f, 1f, glow);
            game.batch.draw(game.texTitleLogoGlow, logoX - 12, logoY - 8, logoW + 24, logoH + 16);
            game.batch.setColor(1f, 1f, 1f, 1f);
        }
        if (titleTex != null) {
            game.batch.draw(titleTex, logoX, logoY, logoW, logoH);
        } else {
            game.fontTitle.setColor(0.31f, 0.76f, 0.97f, 1f);
            game.drawTextCentered(game.fontTitle, "CODE FIGHTERS", W / 2f, logoY + logoH * 0.6f);
        }
        game.batch.end();

        if (blinkOn) {
            game.batch.begin();
            game.fontBold.setColor(0.31f, 0.95f, 1.0f, 1f);
            game.drawTextCentered(game.fontBold, "TAP TO START", W / 2f, H * 0.30f);
            game.batch.end();
        }

        game.batch.begin();
        game.fontSmall.setColor(0.50f, 0.52f, 0.58f, 1f);
        game.fontSmall.draw(game.batch, "VER 1.0.0", W * 0.03f, H * 0.055f);
        game.drawTextCentered(game.fontSmall, "(C) C0D3_4U", W / 2f, H * 0.055f);
        game.batch.end();
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  MAIN MENU
    // ══════════════════════════════════════════════════════════════════════════
    void drawMainMenu() {
        drawTiledLayer(game.texLayerSky,  scrollSky,         0,         H);
        drawTiledLayer(game.texLayerGlow, scrollSky * 0.8f,  0,         H);
        drawTiledLayer(game.texLayerFar,  scrollFar,         H * 0.14f, H * 0.60f);
        drawTiledLayer(game.texLayerMid,  scrollMid,         H * 0.08f, H * 0.42f);

        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        game.shapeRenderer.setColor(0f, 0f, 0f, 0.78f);
        game.shapeRenderer.rect(0, 0, W, H);
        game.shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);

        Texture titleTex = game.texTitleLogoLarge != null
                         ? game.texTitleLogoLarge : game.texTitleLogo;
        float logoW = Math.min(W * 0.52f, 460f);
        float logoH = (titleTex != null)
                      ? logoW * ((float) titleTex.getHeight() / titleTex.getWidth())
                      : logoW * 0.25f;
        float logoX = W / 2f - logoW / 2f;
        float logoY = H * 0.74f;

        game.batch.begin();
        if (titleTex != null) {
            game.batch.draw(titleTex, logoX, logoY, logoW, logoH);
        } else {
            game.fontBold.setColor(0.31f, 0.76f, 0.97f, 1f);
            game.drawTextCentered(game.fontBold, "CODE FIGHTERS", W / 2f, logoY + logoH / 2f);
        }
        game.batch.end();

        float btnW  = W * 0.44f;
        float btnH  = H * 0.078f;
        float btnX  = W / 2f - btnW / 2f;
        float startY = H * 0.67f;
        float gap   = btnH * 1.18f;

        drawMenuButton(btnX, startY,          btnW, btnH, "ARCADE MODE",     0x00bcd4, 0x004d5a, true);
        if (isButtonClicked(btnX, startY,          btnW, btnH)) { game.currentMode = GameMode.ARCADE;  game.setScreen(game.charSelectScreen); }

        drawMenuButton(btnX, startY - gap,     btnW, btnH, "VERSUS MODE (2P)", 0xff9800, 0xbf360c, false);
        if (isButtonClicked(btnX, startY - gap,     btnW, btnH)) { game.currentMode = GameMode.VERSUS; game.setScreen(game.versusCharSelectScreen); }

        drawMenuButton(btnX, startY - gap * 2, btnW, btnH, "GALLERY",         0x7c4dff, 0x2e185c, false);
        if (isButtonClicked(btnX, startY - gap * 2, btnW, btnH)) { game.setScreen(game.galleryScreen); }

        drawMenuButton(btnX, startY - gap * 3, btnW, btnH, "HOW TO PLAY",     0x26a69a, 0x0d473d, false);
        if (isButtonClicked(btnX, startY - gap * 3, btnW, btnH)) { game.setScreen(game.howToPlayScreen); }

        drawMenuButton(btnX, startY - gap * 4, btnW, btnH, "SETTINGS",        0x546e7a, 0x263238, false);
        if (isButtonClicked(btnX, startY - gap * 4, btnW, btnH)) { game.setScreen(game.settingsScreen); }

        drawMenuButton(btnX, startY - gap * 5, btnW, btnH, "CREDITS",         0x455a64, 0x1c2833, false);
        if (isButtonClicked(btnX, startY - gap * 5, btnW, btnH)) { game.setScreen(game.creditsScreen); }

        game.batch.begin();
        game.fontSmall.setColor(0.45f, 0.47f, 0.52f, 1f);
        game.fontSmall.draw(game.batch, "VER 1.0.0", W * 0.03f, H * 0.055f);
        game.drawTextCentered(game.fontSmall, "(C) C0D3_4U", W / 2f, H * 0.055f);
        game.batch.end();
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  SHARED HELPERS
    // ══════════════════════════════════════════════════════════════════════════
    void drawMenuButton(float x, float y, float bw, float bh, String text,
                        int activeCol, int inactiveCol, boolean featured) {
        float mx    = Gdx.input.getX();
        float my    = H - Gdx.input.getY();
        boolean hover = mx >= x && mx <= x + bw && my >= y && my <= y + bh;

        Texture btnTex = hover ? game.texBtnPressed : game.texBtnNormal;
        if (btnTex != null) {
            game.batch.begin();
            game.batch.draw(btnTex, x, y, bw, bh);
            game.batch.end();
        } else {
            int col = hover ? activeCol : inactiveCol;
            float r = ((col >> 16) & 0xFF) / 255f;
            float g = ((col >>  8) & 0xFF) / 255f;
            float b =  (col        & 0xFF) / 255f;

            Gdx.gl.glEnable(GL20.GL_BLEND);
            Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

            game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            game.shapeRenderer.setColor(r, g, b, hover ? 0.95f : 0.78f);
            game.shapeRenderer.rect(x, y, bw, bh);
            game.shapeRenderer.end();

            game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            game.shapeRenderer.setColor(Math.min(r * 1.5f, 1f), Math.min(g * 1.5f, 1f), Math.min(b * 1.5f, 1f), 1f);
            game.shapeRenderer.rect(x, y, featured ? 8 : 4, bh);
            game.shapeRenderer.end();

            game.shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            game.shapeRenderer.setColor(Math.min(r * 1.4f, 1f), Math.min(g * 1.4f, 1f), Math.min(b * 1.4f, 1f), hover ? 1f : 0.6f);
            game.shapeRenderer.rect(x, y, bw, bh);
            game.shapeRenderer.end();

            Gdx.gl.glDisable(GL20.GL_BLEND);
        }

        game.batch.begin();
        game.fontBold.setColor(hover ? 1f : 0.92f, hover ? 1f : 0.92f, hover ? 1f : 0.92f, 1f);
        game.drawTextCentered(game.fontBold, text, x + bw / 2f, y + bh / 2f);
        game.batch.end();
    }

    boolean isButtonClicked(float x, float y, float w, float h) {
        float mx = Gdx.input.getX();
        float my = H - Gdx.input.getY();
        boolean clicked = mx >= x && mx <= x + w && my >= y && my <= y + h && Gdx.input.justTouched();
        if (clicked) game.playSound(game.sndMenuSelect, 0.9f);
        return clicked;
    }

    @Override public void resize(int width, int height) { game.updateFontScales(); W = width; H = height; }
    @Override public void pause()   {}
    @Override public void resume()  {}
    @Override public void hide()    {}
    @Override public void dispose() {}
}
