package com.codefighters;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
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
    public void render(float delta) {
        timer += delta;
        Gdx.gl.glClearColor(0.04f, 0.04f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();

        // Background grid
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        game.shapeRenderer.setColor(0.1f, 0.1f, 0.24f, 1);
        for (int i = 0; i < w; i += 40) {
            game.shapeRenderer.line(i, 0, i, h);
        }
        for (int i = 0; i < h; i += 40) {
            game.shapeRenderer.line(0, i, w, i);
        }
        game.shapeRenderer.end();

        game.batch.begin();

        // Title
        game.fontBold.setColor(1, 1, 1, 1);
        game.fontBold.draw(game.batch, "SELECT YOUR FIGHTER", w/2 - 200, h * 0.92f);

        // === PLAYER SIDE (Left) ===
        float leftX = w * 0.15f;
        float cardY = h * 0.45f;
        float cardW = w * 0.28f;
        float cardH = h * 0.55f;

        game.batch.end();
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        game.shapeRenderer.setColor(0.07f, 0.07f, 0.14f, 1);
        game.shapeRenderer.rect(leftX, cardY - cardH/2, cardW, cardH);
        game.shapeRenderer.end();
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        game.shapeRenderer.setColor(0.31f, 0.76f, 0.97f, 1);
        game.shapeRenderer.rect(leftX, cardY - cardH/2, cardW, cardH);
        game.shapeRenderer.end();

        game.batch.begin();
        // Draw character placeholder
        drawCharacterPlaceholder(leftX + cardW/2, cardY + 30, 1.5f, true, playerChar.primaryColor);

        game.fontBold.setColor(0.31f, 0.76f, 0.97f, 1);
        game.fontBold.draw(game.batch, playerChar.name, leftX + 20, cardY - 40);
        game.fontSmall.setColor(0.7f, 0.7f, 0.7f, 1);
        game.fontSmall.draw(game.batch, playerChar.subtitle, leftX + 20, cardY - 65);
        game.fontSmall.setColor(0.5f, 0.5f, 0.5f, 1);
        game.fontSmall.draw(game.batch, "Special: " + playerChar.specialName, leftX + 20, cardY - 90);
        game.batch.end();

        // === VS ===
        game.batch.begin();
        game.fontBold.setColor(1, 0.09f, 0.27f, 1);
        game.fontBold.draw(game.batch, "VS", w/2 - 35, h * 0.55f);
        game.batch.end();

        // === ENEMY SIDE (Right) ===
        float rightX = w * 0.57f;
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        game.shapeRenderer.setColor(0.07f, 0.07f, 0.14f, 1);
        game.shapeRenderer.rect(rightX, cardY - cardH/2, cardW, cardH);
        game.shapeRenderer.end();
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        game.shapeRenderer.setColor(1, 0.09f, 0.27f, 1);
        game.shapeRenderer.rect(rightX, cardY - cardH/2, cardW, cardH);
        game.shapeRenderer.end();

        game.batch.begin();
        drawEnemyPlaceholder(rightX + cardW/2, cardY + 30, 1.5f, enemy.primaryColor);

        game.fontBold.setColor(1, 0.09f, 0.27f, 1);
        game.fontBold.draw(game.batch, enemy.name, rightX + 20, cardY - 40);
        game.fontSmall.setColor(0.7f, 0.7f, 0.7f, 1);
        game.fontSmall.draw(game.batch, enemy.type, rightX + 20, cardY - 65);
        game.fontSmall.setColor(0.5f, 0.5f, 0.5f, 1);
        game.fontSmall.draw(game.batch, "Weakness: " + enemy.weakness.substring(0, Math.min(35, enemy.weakness.length())) + "...",
            rightX + 20, cardY - 90);
        game.batch.end();

        // === ENEMY SELECTION GRID (Bottom) ===
        float gridY = h * 0.18f;
        float slotSize = 70;
        float startGridX = w/2 - slotSize * 2.5f;

        game.batch.begin();
        game.fontSmall.setColor(0.6f, 0.6f, 0.6f, 1);
        game.fontSmall.draw(game.batch, "SELECT ENEMY:", startGridX, gridY + slotSize + 20);
        game.batch.end();

        for (int i = 0; i < 5; i++) {
            float sx = startGridX + i * (slotSize + 10);
            boolean isSelected = (i == 0); // Only WannaCry for now
            boolean unlocked = (i == 0);

            game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            if (isSelected) {
                game.shapeRenderer.setColor(1, 0.09f, 0.27f, 1);
            } else if (unlocked) {
                game.shapeRenderer.setColor(0.13f, 0.13f, 0.2f, 1);
            } else {
                game.shapeRenderer.setColor(0.08f, 0.08f, 0.1f, 1);
            }
            game.shapeRenderer.rect(sx, gridY, slotSize, slotSize);
            game.shapeRenderer.end();

            game.shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            game.shapeRenderer.setColor(isSelected ? 1f : 0.3f, isSelected ? 0.7f : 0.3f, 0.3f, 1);
            game.shapeRenderer.rect(sx, gridY, slotSize, slotSize);
            game.shapeRenderer.end();

            game.batch.begin();
            if (unlocked) {
                game.fontSmall.setColor(1, 1, 1, 1);
                game.fontSmall.draw(game.batch, i == 0 ? "WCRY" : "???", sx + 15, gridY + 40);
            } else {
                game.fontBold.setColor(0.3f, 0.3f, 0.3f, 1);
                game.fontBold.draw(game.batch, "?", sx + 25, gridY + 45);
            }
            game.batch.end();
        }

        // === BUTTONS ===
        float btnW = 200, btnH = 50;
        float btnY = h * 0.08f;

        drawButton(w * 0.25f - btnW/2, btnY, btnW, btnH, "BACK", 0x555555, 0x222222);
        if (isButtonClicked(w * 0.25f - btnW/2, btnY, btnW, btnH)) {
            game.setScreen(game.menuScreen);
        }

        drawButton(w * 0.75f - btnW/2, btnY, btnW, btnH, "FIGHT!", 0x00e676, 0x00c853);
        if (isButtonClicked(w * 0.75f - btnW/2, btnY, btnW, btnH)) {
            game.fightScreen.resetFight();
            game.setScreen(game.fightScreen);
        }

        // Controls hint
        game.batch.begin();
        game.fontSmall.setColor(0.4f, 0.4f, 0.4f, 1);
        game.fontSmall.draw(game.batch, "In-Game: Left/Right arrows = Move | A = Light Attack | S = Special | D = Block",
            w/2 - 280, h * 0.05f);
        game.batch.end();
    }

    void drawCharacterPlaceholder(float cx, float cy, float scale, boolean facingRight, int color) {
        float r = ((color >> 16) & 0xFF) / 255f;
        float g = ((color >> 8) & 0xFF) / 255f;
        float b = (color & 0xFF) / 255f;

        game.batch.end();
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        game.shapeRenderer.setColor(r, g, b, 1);
        // Body
        game.shapeRenderer.rect(cx - 25*scale, cy - 70*scale, 50*scale, 50*scale);
        // Head
        game.shapeRenderer.setColor(1, 0.8f, 0.67f, 1);
        game.shapeRenderer.rect(cx - 18*scale, cy - 100*scale, 36*scale, 35*scale);
        // Headphones
        game.shapeRenderer.setColor(0.3f, 0.3f, 0.3f, 1);
        game.shapeRenderer.rect(cx - 22*scale, cy - 105*scale, 44*scale, 8*scale);
        game.shapeRenderer.rect(cx - 25*scale, cy - 105*scale, 8*scale, 30*scale);
        game.shapeRenderer.rect(cx + 17*scale, cy - 105*scale, 8*scale, 30*scale);
        game.shapeRenderer.end();
        game.batch.begin();
    }

    void drawEnemyPlaceholder(float cx, float cy, float scale, int color) {
        float r = ((color >> 16) & 0xFF) / 255f;
        float g = ((color >> 8) & 0xFF) / 255f;
        float b = (color & 0xFF) / 255f;

        game.batch.end();
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        // Glitchy body
        game.shapeRenderer.setColor(r, g, b, 0.9f);
        game.shapeRenderer.triangle(
            cx, cy - 100*scale,
            cx + 30*scale, cy - 30*scale,
            cx - 30*scale, cy - 30*scale
        );
        // Lock head
        game.shapeRenderer.setColor(0.8f, 0, 0, 1);
        game.shapeRenderer.rect(cx - 20*scale, cy - 130*scale, 40*scale, 35*scale);
        // Keyhole
        game.shapeRenderer.setColor(1, 0.92f, 0.23f, 1);
        game.shapeRenderer.circle(cx, cy - 115*scale, 5*scale, 12);
        game.shapeRenderer.rect(cx - 3*scale, cy - 115*scale, 6*scale, 12*scale);
        game.shapeRenderer.end();
        game.batch.begin();
    }

    void drawButton(float x, float y, float w, float h, String text, int hoverCol, int col) {
        float mx = Gdx.input.getX();
        float my = Gdx.graphics.getHeight() - Gdx.input.getY();
        boolean hover = mx >= x && mx <= x + w && my >= y && my <= y + h;

        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        if (hover) {
            game.shapeRenderer.setColor(((hoverCol >> 16) & 0xFF)/255f,
                ((hoverCol >> 8) & 0xFF)/255f, (hoverCol & 0xFF)/255f, 1);
        } else {
            game.shapeRenderer.setColor(((col >> 16) & 0xFF)/255f,
                ((col >> 8) & 0xFF)/255f, (col & 0xFF)/255f, 1);
        }
        game.shapeRenderer.rect(x, y, w, h);
        game.shapeRenderer.end();

        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        game.shapeRenderer.setColor(1, 1, 1, 1);
        game.shapeRenderer.rect(x, y, w, h);
        game.shapeRenderer.end();

        game.batch.begin();
        game.font.setColor(1, 1, 1, 1);
        game.font.draw(game.batch, text, x + w/2 - 50, y + h/2 + 10);
        game.batch.end();
    }

    boolean isButtonClicked(float x, float y, float w, float h) {
        float mx = Gdx.input.getX();
        float my = Gdx.graphics.getHeight() - Gdx.input.getY();
        boolean hover = mx >= x && mx <= x + w && my >= y && my <= y + h;
        return hover && Gdx.input.justTouched();
    }

    @Override public void show() {}
    @Override public void resize(int width, int height) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override public void dispose() {}
}
