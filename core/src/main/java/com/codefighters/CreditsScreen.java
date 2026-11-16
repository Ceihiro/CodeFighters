package com.codefighters;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class CreditsScreen implements Screen {
    final CodeFightersGame game;
    float timer = 0;
    float scrollY = 0;
    float lastTouchY = -1;
    static final float TOTAL_CREDITS_H = 880f;

    final Color colClear = new Color(0.04f, 0.07f, 0.12f, 0f);
    final Color colOpaque = new Color(0.04f, 0.07f, 0.12f, 0.95f);

    public CreditsScreen(final CodeFightersGame game) {
        this.game = game;
    }

    @Override
    public void show() {
        game.updateFontScales();
        timer = 0;
        scrollY = 0;
        lastTouchY = -1;
    }

    @Override
    public void render(float delta) {
        timer += delta;
        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();

        // Credits Panel Dimensions
        float cardW = w * 0.78f;
        float cardH = h * 0.68f;
        float cardX = w / 2f - cardW / 2f;
        float cardY = h * 0.16f;

        // Manual touch drag or auto scroll
        if (Gdx.input.isTouched()) {
            float touchY = h - Gdx.input.getY();
            float touchX = Gdx.input.getX();
            // If dragging inside the card
            if (touchX >= cardX && touchX <= cardX + cardW && touchY >= cardY && touchY <= cardY + cardH) {
                if (lastTouchY >= 0) {
                    scrollY += (touchY - lastTouchY) * 1.5f;
                }
                lastTouchY = touchY;
            } else {
                lastTouchY = -1;
                scrollY += 45f * delta;
            }
        } else {
            lastTouchY = -1;
            scrollY += 45f * delta;
        }

        // Auto-loop scroll
        if (scrollY > TOTAL_CREDITS_H + cardH + 40f) {
            scrollY = 0;
        }
        if (scrollY < -50f) {
            scrollY = -50f;
        }

        Gdx.gl.glClearColor(0.02f, 0.02f, 0.05f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Background
        game.batch.begin();
        if (game.texBgMenu != null) {
            game.batch.setColor(0.22f, 0.28f, 0.40f, 1f);
            game.batch.draw(game.texBgMenu, 0, 0, w, h);
            game.batch.setColor(1, 1, 1, 1);
        }
        game.batch.end();

        // Cyber grid overlay
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

        // Header Title (Comfortably positioned above the credits box)
        game.batch.begin();
        game.fontTitle.setColor(0.31f, 0.76f, 0.97f, 1);
        game.drawTextCentered(game.fontTitle, "CREDITS & ACKNOWLEDGMENTS", w / 2f, h * 0.92f);
        game.batch.end();

        // Draw Credits Panel Box
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        game.shapeRenderer.setColor(0.04f, 0.07f, 0.12f, 0.94f);
        game.shapeRenderer.rect(cardX, cardY, cardW, cardH);
        game.shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);

        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        game.shapeRenderer.setColor(0.2f, 0.45f, 0.75f, 0.85f);
        game.shapeRenderer.rect(cardX, cardY, cardW, cardH);
        game.shapeRenderer.end();

        // Draw Scissored Credits Text (strictly clipped inside the panel box!)
        game.batch.begin();
        game.batch.flush();
        Gdx.gl.glEnable(GL20.GL_SCISSOR_TEST);
        Gdx.gl.glScissor((int) cardX, (int) cardY, (int) cardW, (int) cardH);

        float textY = cardY - 20f + scrollY;

        game.fontBold.setColor(0.2f, 1.0f, 0.85f, 1);
        game.drawTextCentered(game.fontBold, "DEVELOPMENT & PRODUCTION", w / 2f, textY);
        textY -= 42;
        game.font.setColor(1, 1, 1, 1);
        game.drawTextCentered(game.font, "C0D3_4U Studio", w / 2f, textY);
        textY -= 58;

        game.fontBold.setColor(0.2f, 1.0f, 0.85f, 1);
        game.drawTextCentered(game.fontBold, "GAME ARCHITECT & LEAD DESIGNER", w / 2f, textY);
        textY -= 42;
        game.font.setColor(1, 1, 1, 1);
        game.drawTextCentered(game.font, "Cat (Byte Developer Team)", w / 2f, textY);
        textY -= 58;

        game.fontBold.setColor(0.2f, 1.0f, 0.85f, 1);
        game.drawTextCentered(game.fontBold, "CYBER DEFENSE LORE & CVE RESEARCH", w / 2f, textY);
        textY -= 42;
        game.font.setColor(1, 1, 1, 1);
        game.drawTextCentered(game.font, "MS17-010 EternalBlue Security Analysis", w / 2f, textY);
        textY -= 58;

        game.fontBold.setColor(0.2f, 1.0f, 0.85f, 1);
        game.drawTextCentered(game.fontBold, "TECHNOLOGY STACK", w / 2f, textY);
        textY -= 42;
        game.font.setColor(1, 1, 1, 1);
        game.drawTextCentered(game.font, "Powered by LibGDX 2D Engine", w / 2f, textY);
        textY -= 58;

        game.fontBold.setColor(0.2f, 1.0f, 0.85f, 1);
        game.drawTextCentered(game.fontBold, "PIXEL ART & ASSETS", w / 2f, textY);
        textY -= 42;
        game.font.setColor(1, 1, 1, 1);
        game.drawTextCentered(game.font, "Byte & WannaCry Art by C0D3_4U Studio", w / 2f, textY);
        textY -= 58;

        game.fontBold.setColor(0.2f, 1.0f, 0.85f, 1);
        game.drawTextCentered(game.fontBold, "SOUND DESIGN", w / 2f, textY);
        textY -= 42;
        game.font.setColor(1, 1, 1, 1);
        game.drawTextCentered(game.font, "SFX & Music Composed In-House", w / 2f, textY);
        textY -= 58;

        game.fontBold.setColor(1.0f, 0.85f, 0.2f, 1);
        game.drawTextCentered(game.fontBold, "SPECIAL THANKS", w / 2f, textY);
        textY -= 42;
        game.font.setColor(0.85f, 0.90f, 0.95f, 1);
        game.drawTextCentered(game.font, "To All Cyber Defenders Worldwide", w / 2f, textY);
        textY -= 68;

        game.fontBold.setColor(0.2f, 1.0f, 0.85f, 1);
        game.drawTextCentered(game.fontBold, "DISCLAIMER", w / 2f, textY);
        textY -= 42;
        game.font.setColor(1, 1, 1, 1);
        game.drawTextCentered(game.font, "WannaCry shown for education only", w / 2f, textY);
        textY -= 70;

        game.fontBold.setColor(1f, 0.9f, 0.1f, 1f);
        game.drawTextCentered(game.fontBold, "*** STAY PATCHED. STAY SAFE. ***", w / 2f, textY);

        game.batch.flush();
        Gdx.gl.glDisable(GL20.GL_SCISSOR_TEST);
        game.batch.end();

        // Top & Bottom gradient fade edges for smooth cinema feel
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        float fadeH = cardH * 0.12f;
        // Top edge fade
        game.shapeRenderer.rect(cardX + 1, cardY + cardH - fadeH, cardW - 2, fadeH,
            colClear, colClear, colOpaque, colOpaque);
        // Bottom edge fade
        game.shapeRenderer.rect(cardX + 1, cardY, cardW - 2, fadeH,
            colOpaque, colOpaque, colClear, colClear);
        game.shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);

        // Re-draw border so fade doesn't obscure it
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        game.shapeRenderer.setColor(0.2f, 0.45f, 0.75f, 0.85f);
        game.shapeRenderer.rect(cardX, cardY, cardW, cardH);
        game.shapeRenderer.end();

        // Back button
        float btnW = w * 0.22f;
        float btnH = h * 0.075f;
        drawButton(w / 2f - btnW / 2f, h * 0.045f, btnW, btnH, "BACK TO MENU", 0x546e7a, 0x263238);
        if (isButtonClicked(w / 2f - btnW / 2f, h * 0.045f, btnW, btnH)) {
            game.playSound(game.sndMenuBack, 0.8f);
            game.setScreen(game.menuScreen);
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
        game.shapeRenderer.setColor(r, g, b, 0.85f);
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
