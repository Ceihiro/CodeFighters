package com.codefighters;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class CharacterSelectScreen implements Screen {
    final CodeFightersGame game;
    float timer = 0;
    CharacterData playerChar;
    EnemyData enemy;

    public CharacterSelectScreen(final CodeFightersGame game) {
        this.game = game;
        this.playerChar = CharacterData.getTeamLead();
        this.enemy = EnemyData.getWannaCry();
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

        Gdx.gl.glClearColor(0.03f, 0.03f, 0.08f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Cyberpunk background grid
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        game.shapeRenderer.setColor(0.08f, 0.10f, 0.20f, 1);
        for (int i = 0; i < w; i += 50) game.shapeRenderer.line(i, 0, i, h);
        for (int i = 0; i < h; i += 50) game.shapeRenderer.line(0, i, w, i);
        game.shapeRenderer.end();

        // Title Header
        game.batch.begin();
        game.fontTitle.setColor(1, 1, 1, 1);
        game.drawTextCentered(game.fontTitle, "SELECT YOUR FIGHTER", w / 2f, h * 0.93f);
        game.batch.end();

        // Card dimensions
        float cardY = h * 0.44f;
        float cardW = w * 0.38f;
        float cardH = h * 0.54f;

        // === PLAYER CARD (Left) ===
        float leftX = w * 0.08f;
        drawCharacterCard(leftX, cardY - cardH / 2f, cardW, cardH, true);

        // === VS BADGE (Center) ===
        game.batch.begin();
        game.fontTitle.setColor(1, 0.85f, 0.1f, 1);
        game.drawTextCentered(game.fontTitle, "VS", w / 2f, cardY);
        game.batch.end();

        // === ENEMY CARD (Right) ===
        float rightX = w * 0.54f;
        drawCharacterCard(rightX, cardY - cardH / 2f, cardW, cardH, false);

        // === ENEMY SELECTION GRID (Bottom) ===
        float slotSize = h * 0.10f;
        float gridY = h * 0.16f;
        float startGridX = w / 2f - (slotSize * 2.5f + 20);

        game.batch.begin();
        game.fontSmall.setColor(0.7f, 0.7f, 0.7f, 1);
        game.drawTextCentered(game.fontSmall, "TARGET SELECT", w / 2f, gridY + slotSize + h * 0.025f);
        game.batch.end();

        for (int i = 0; i < 5; i++) {
            float sx = startGridX + i * (slotSize + 10);
            boolean isSelected = (i == 0);
            boolean unlocked = (i == 0);

            game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            game.shapeRenderer.setColor(isSelected ? 0.7f : 0.12f, isSelected ? 0.1f : 0.12f, isSelected ? 0.2f : 0.16f, 0.9f);
            game.shapeRenderer.rect(sx, gridY, slotSize, slotSize);
            game.shapeRenderer.end();

            game.shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            game.shapeRenderer.setColor(isSelected ? 1f : 0.3f, isSelected ? 0.2f : 0.3f, isSelected ? 0.4f : 0.3f, 1);
            game.shapeRenderer.rect(sx, gridY, slotSize, slotSize);
            game.shapeRenderer.end();

            game.batch.begin();
            if (unlocked) {
                game.fontBold.setColor(1, 1, 1, 1);
                game.drawTextCentered(game.fontBold, "WCRY", sx + slotSize / 2f, gridY + slotSize / 2f);
            } else {
                game.fontBold.setColor(0.4f, 0.4f, 0.4f, 1);
                game.drawTextCentered(game.fontBold, "?", sx + slotSize / 2f, gridY + slotSize / 2f);
            }
            game.batch.end();
        }

        // === ACTION BUTTONS (Bottom) ===
        float btnW = w * 0.22f;
        float btnH = h * 0.082f;
        float btnY = h * 0.045f;

        drawButton(w * 0.18f, btnY, btnW, btnH, "BACK", 0x546e7a, 0x263238);
        if (isButtonClicked(w * 0.18f, btnY, btnW, btnH)) {
            game.setScreen(game.menuScreen);
        }

        drawButton(w * 0.60f, btnY, btnW, btnH, "FIGHT!", 0x00c853, 0x0a5c28);
        if (isButtonClicked(w * 0.60f, btnY, btnW, btnH)) {
            game.setScreen(game.vsScreen);
        }
    }

    void drawCharacterCard(float x, float y, float w, float h, boolean isPlayer) {
        // Card Background
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        game.shapeRenderer.setColor(0.06f, 0.07f, 0.12f, 0.92f);
        game.shapeRenderer.rect(x, y, w, h);
        game.shapeRenderer.end();

        // Card Border
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        game.shapeRenderer.setColor(isPlayer ? 0.31f : 1.0f, isPlayer ? 0.76f : 0.15f, isPlayer ? 0.97f : 0.30f, 1f);
        game.shapeRenderer.rect(x, y, w, h);
        game.shapeRenderer.end();

        // Character Sprite inside card with idle animation
        float spriteH = h * 0.52f;
        float spriteW = spriteH;
        float spriteX = x + w / 2f - spriteW / 2f;
        float spriteY = y + h * 0.42f;

        boolean isFrame2 = (((int)(timer * 2.5f)) % 2 == 1);

        game.batch.begin();
        if (isPlayer) {
            Texture tex = (isFrame2 && game.texPlayerIdle2 != null) ? game.texPlayerIdle2 : game.texPlayerIdle;
            if (tex != null) game.batch.draw(tex, spriteX, spriteY, spriteW, spriteH);
        } else {
            Texture tex = (isFrame2 && game.texEnemyIdle2 != null) ? game.texEnemyIdle2 : game.texEnemyIdle;
            if (tex != null) {
                game.batch.draw(tex, spriteX, spriteY, spriteW, spriteH,
                    0, 0, tex.getWidth(), tex.getHeight(), true, false);
            }
        }
        game.batch.end();

        // Text details below sprite
        game.batch.begin();
        float textY = y + h * 0.36f;
        float padX = x + 20;

        if (isPlayer) {
            game.fontBold.setColor(0.31f, 0.85f, 1.0f, 1f);
            game.fontBold.draw(game.batch, playerChar.name, padX, textY);

            game.font.setColor(0.75f, 0.85f, 0.95f, 1f);
            game.font.draw(game.batch, playerChar.subtitle, padX, textY - h * 0.07f);

            game.fontSmall.setColor(0.6f, 0.8f, 0.95f, 0.9f);
            game.fontSmall.draw(game.batch, "Special: " + playerChar.specialName, padX, textY - h * 0.13f);
        } else {
            game.fontBold.setColor(1.0f, 0.2f, 0.35f, 1f);
            game.fontBold.draw(game.batch, enemy.name, padX, textY);

            game.font.setColor(0.95f, 0.7f, 0.75f, 1f);
            game.font.draw(game.batch, enemy.type, padX, textY - h * 0.07f);

            game.fontSmall.setColor(0.9f, 0.6f, 0.6f, 0.9f);
            game.fontSmall.draw(game.batch, "Weakness: Block & Counter", padX, textY - h * 0.13f);
        }
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
