package com.codefighters;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class GalleryScreen implements Screen {
    final CodeFightersGame game;
    int tab = 0;
    int selectedChar = 0;
    int selectedEnemy = 0;

    public GalleryScreen(final CodeFightersGame game) {
        this.game = game;
    }

    @Override
    public void render(float delta) {
        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();

        Gdx.gl.glClearColor(0.04f, 0.04f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

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
        game.fontBold.setColor(1, 1, 1, 1);
        game.fontBold.draw(game.batch, "GALLERY", w/2 - 80, h * 0.95f);
        game.batch.end();

        float tabW = w * 0.25f;
        float tabH = h * 0.07f;
        float tabY = h * 0.84f;

        drawTab(w * 0.08f, tabY, tabW, tabH, "CHARACTERS", tab == 0);
        drawTab(w * 0.37f, tabY, tabW, tabH, "ENEMIES", tab == 1);
        drawTab(w * 0.66f, tabY, tabW, tabH, "COMBOS", tab == 2);

        if (isTabClicked(w * 0.08f, tabY, tabW, tabH)) tab = 0;
        if (isTabClicked(w * 0.37f, tabY, tabW, tabH)) tab = 1;
        if (isTabClicked(w * 0.66f, tabY, tabW, tabH)) tab = 2;

        float contentY = h * 0.78f;
        float contentH = h * 0.68f;

        if (tab == 0) drawCharactersTab(w, h, contentY, contentH);
        else if (tab == 1) drawEnemiesTab(w, h, contentY, contentH);
        else drawCombosTab(w, h, contentY, contentH);

        drawButton(w * 0.02f, h * 0.02f, w * 0.12f, h * 0.06f, "BACK", 0x555555, 0x222222);
        if (isButtonClicked(w * 0.02f, h * 0.02f, w * 0.12f, h * 0.06f)) {
            game.setScreen(game.menuScreen);
        }
    }

    void drawCharactersTab(float w, float h, float contentY, float contentH) {
        float listW = w * 0.25f;
        float cardX = w * 0.32f;
        float cardW = w * 0.62f;

        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        game.shapeRenderer.setColor(0.07f, 0.07f, 0.14f, 1);
        game.shapeRenderer.rect(w * 0.03f, contentY - contentH, listW, contentH);
        game.shapeRenderer.end();

        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        game.shapeRenderer.setColor(0.2f, 0.2f, 0.3f, 1);
        game.shapeRenderer.rect(w * 0.03f, contentY - contentH, listW, contentH);
        game.shapeRenderer.end();

        float entryH = h * 0.1f;
        float entryY = contentY - 20;
        boolean isSelected = (selectedChar == 0);

        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        game.shapeRenderer.setColor(isSelected ? 0.2f : 0.1f, isSelected ? 0.4f : 0.1f, isSelected ? 0.5f : 0.2f, 1);
        game.shapeRenderer.rect(w * 0.035f, entryY - entryH, listW - 0.01f, entryH);
        game.shapeRenderer.end();

        game.batch.begin();
        game.fontSmall.setColor(1, 1, 1, 1);
        game.fontSmall.draw(game.batch, "Team Lead", w * 0.05f, entryY - entryH/2 + 5);
        game.fontSmall.setColor(0.5f, 0.5f, 0.5f, 1);
        game.fontSmall.draw(game.batch, "Game Architect", w * 0.05f, entryY - entryH/2 - 15);
        game.batch.end();

        if (isEntryClicked(w * 0.035f, entryY - entryH, listW - 0.01f, entryH)) selectedChar = 0;

        CharacterData c = CharacterData.getTeamLead();

        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        game.shapeRenderer.setColor(0.07f, 0.07f, 0.14f, 1);
        game.shapeRenderer.rect(cardX, contentY - contentH, cardW, contentH);
        game.shapeRenderer.end();

        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        game.shapeRenderer.setColor(0.31f, 0.76f, 0.97f, 1);
        game.shapeRenderer.rect(cardX, contentY - contentH, cardW, contentH);
        game.shapeRenderer.end();

        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        game.shapeRenderer.setColor(0.31f, 0.76f, 0.97f, 1);
        game.shapeRenderer.rect(cardX + 30, contentY - 180, 120, 120);
        game.shapeRenderer.setColor(1, 0.8f, 0.67f, 1);
        game.shapeRenderer.rect(cardX + 50, contentY - 160, 80, 60);
        game.shapeRenderer.end();

        game.batch.begin();
        game.fontBold.setColor(0.31f, 0.76f, 0.97f, 1);
        game.fontBold.draw(game.batch, c.name, cardX + 180, contentY - 50);
        game.fontSmall.setColor(0.7f, 0.7f, 0.7f, 1);
        game.fontSmall.draw(game.batch, c.subtitle, cardX + 180, contentY - 80);

        game.fontSmall.setColor(0.9f, 0.9f, 0.9f, 1);
        wrapText(c.bio, cardX + 30, contentY - 210, cardW - 60, 22);

        game.fontBold.setColor(1, 0.92f, 0.23f, 1);
        game.fontBold.draw(game.batch, "ABILITIES", cardX + 30, contentY - 320);

        game.fontSmall.setColor(0.31f, 0.76f, 0.97f, 1);
        game.fontSmall.draw(game.batch, "Special: " + c.specialName, cardX + 30, contentY - 355);
        game.fontSmall.setColor(0.7f, 0.7f, 0.7f, 1);
        game.fontSmall.draw(game.batch, c.specialDesc, cardX + 30, contentY - 380);

        game.fontSmall.setColor(0.31f, 0.76f, 0.97f, 1);
        game.fontSmall.draw(game.batch, "Super: " + c.superName, cardX + 30, contentY - 415);
        game.fontSmall.setColor(0.7f, 0.7f, 0.7f, 1);
        game.fontSmall.draw(game.batch, c.superDesc, cardX + 30, contentY - 440);

        game.batch.end();
    }

    void drawEnemiesTab(float w, float h, float contentY, float contentH) {
        float listW = w * 0.25f;
        float cardX = w * 0.32f;
        float cardW = w * 0.62f;

        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        game.shapeRenderer.setColor(0.07f, 0.07f, 0.14f, 1);
        game.shapeRenderer.rect(w * 0.03f, contentY - contentH, listW, contentH);
        game.shapeRenderer.end();

        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        game.shapeRenderer.setColor(0.2f, 0.2f, 0.3f, 1);
        game.shapeRenderer.rect(w * 0.03f, contentY - contentH, listW, contentH);
        game.shapeRenderer.end();

        float entryH = h * 0.1f;
        float entryY = contentY - 20;
        boolean unlocked = game.progress.isEnemyDefeated("wannacry");
        boolean isSelected = (selectedEnemy == 0);

        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        game.shapeRenderer.setColor(isSelected ? 0.5f : 0.3f, isSelected ? 0.1f : 0.05f, isSelected ? 0.15f : 0.05f, 1);
        game.shapeRenderer.rect(w * 0.035f, entryY - entryH, listW - 0.01f, entryH);
        game.shapeRenderer.end();

        game.batch.begin();
        game.fontSmall.setColor(unlocked ? 1 : 0.4f, unlocked ? 1 : 0.4f, unlocked ? 1 : 0.4f, 1);
        game.fontSmall.draw(game.batch, unlocked ? "WannaCry" : "???", w * 0.05f, entryY - entryH/2 + 5);
        game.fontSmall.setColor(0.5f, 0.5f, 0.5f, 1);
        game.fontSmall.draw(game.batch, unlocked ? "Ransomware" : "Locked - Defeat to unlock", w * 0.05f, entryY - entryH/2 - 15);
        game.batch.end();

        if (unlocked && isEntryClicked(w * 0.035f, entryY - entryH, listW - 0.01f, entryH)) selectedEnemy = 0;

        EnemyData e = EnemyData.getWannaCry();

        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        game.shapeRenderer.setColor(0.07f, 0.07f, 0.14f, 1);
        game.shapeRenderer.rect(cardX, contentY - contentH, cardW, contentH);
        game.shapeRenderer.end();

        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        game.shapeRenderer.setColor(1, 0.09f, 0.27f, 1);
        game.shapeRenderer.rect(cardX, contentY - contentH, cardW, contentH);
        game.shapeRenderer.end();

        if (unlocked) {
            game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            game.shapeRenderer.setColor(1, 0.09f, 0.27f, 1);
            float cx = cardX + 90;
            float cy = contentY - 90;
            game.shapeRenderer.triangle(cx, cy - 60, cx + 40, cy + 20, cx - 40, cy + 20);
            game.shapeRenderer.setColor(0.8f, 0, 0, 1);
            game.shapeRenderer.rect(cx - 25, cy + 20, 50, 35);
            game.shapeRenderer.end();

            game.batch.begin();
            game.fontBold.setColor(1, 0.09f, 0.27f, 1);
            game.fontBold.draw(game.batch, e.name, cardX + 180, contentY - 50);
            game.fontSmall.setColor(0.7f, 0.7f, 0.7f, 1);
            game.fontSmall.draw(game.batch, e.type, cardX + 180, contentY - 80);

            game.fontSmall.setColor(0.9f, 0.9f, 0.9f, 1);
            wrapText(e.bio, cardX + 30, contentY - 180, cardW - 60, 22);

            game.fontBold.setColor(1, 0.92f, 0.23f, 1);
            game.fontBold.draw(game.batch, "REAL WORLD FACT", cardX + 30, contentY - 300);
            game.fontSmall.setColor(0.7f, 0.7f, 0.7f, 1);
            wrapText(e.realWorldFact, cardX + 30, contentY - 325, cardW - 60, 22);

            game.fontBold.setColor(0, 1, 0.5f, 1);
            game.fontBold.draw(game.batch, "WEAKNESS", cardX + 30, contentY - 420);
            game.fontSmall.setColor(0.7f, 0.7f, 0.7f, 1);
            game.fontSmall.draw(game.batch, e.weakness, cardX + 30, contentY - 445);
            game.batch.end();
        } else {
            game.batch.begin();
            game.fontBold.setColor(0.3f, 0.3f, 0.3f, 1);
            game.fontBold.draw(game.batch, "???", cardX + cardW/2 - 30, contentY - contentH/2);
            game.fontSmall.setColor(0.4f, 0.4f, 0.4f, 1);
            game.fontSmall.draw(game.batch, "Defeat this enemy in combat to unlock its data.",
                cardX + cardW/2 - 180, contentY - contentH/2 - 30);
            game.batch.end();
        }
    }

    void drawCombosTab(float w, float h, float contentY, float contentH) {
        CharacterData c = CharacterData.getTeamLead();

        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        game.shapeRenderer.setColor(0.07f, 0.07f, 0.14f, 1);
        game.shapeRenderer.rect(w * 0.05f, contentY - contentH, w * 0.9f, contentH);
        game.shapeRenderer.end();

        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        game.shapeRenderer.setColor(0.31f, 0.76f, 0.97f, 1);
        game.shapeRenderer.rect(w * 0.05f, contentY - contentH, w * 0.9f, contentH);
        game.shapeRenderer.end();

        game.batch.begin();
        game.fontBold.setColor(0.31f, 0.76f, 0.97f, 1);
        game.fontBold.draw(game.batch, c.name + " - COMBO LIST", w * 0.08f, contentY - 30);

        float y = contentY - 80;
        for (String combo : c.combos) {
            game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            game.shapeRenderer.setColor(0.1f, 0.2f, 0.3f, 1);
            game.shapeRenderer.rect(w * 0.08f, y - 35, w * 0.84f, 50);
            game.shapeRenderer.end();

            game.shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            game.shapeRenderer.setColor(0.31f, 0.76f, 0.97f, 0.5f);
            game.shapeRenderer.rect(w * 0.08f, y - 35, w * 0.84f, 50);
            game.shapeRenderer.end();

            game.fontSmall.setColor(0.9f, 0.9f, 0.9f, 1);
            game.fontSmall.draw(game.batch, combo, w * 0.1f, y - 5);
            y -= 70;
        }

        game.fontSmall.setColor(0.5f, 0.5f, 0.5f, 1);
        game.fontSmall.draw(game.batch, "Tip: Chain light attacks within the combo window for maximum damage!",
            w * 0.08f, contentY - contentH + 40);
        game.batch.end();
    }

    void wrapText(String text, float x, float startY, float maxWidth, float lineHeight) {
        String[] words = text.split(" ");
        StringBuilder line = new StringBuilder();
        float y = startY;
        for (String word : words) {
            if (line.length() + word.length() > maxWidth / 8) {
                game.fontSmall.draw(game.batch, line.toString(), x, y);
                line = new StringBuilder(word + " ");
                y -= lineHeight;
            } else {
                line.append(word).append(" ");
            }
        }
        if (line.length() > 0) {
            game.fontSmall.draw(game.batch, line.toString(), x, y);
        }
    }

    void drawTab(float x, float y, float w, float h, String text, boolean active) {
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        game.shapeRenderer.setColor(active ? 0.2f : 0.1f, active ? 0.4f : 0.1f, active ? 0.5f : 0.2f, 1);
        game.shapeRenderer.rect(x, y - h, w, h);
        game.shapeRenderer.end();

        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        game.shapeRenderer.setColor(active ? 0.5f : 0.2f, active ? 0.8f : 0.2f, 1, 1);
        game.shapeRenderer.rect(x, y - h, w, h);
        game.shapeRenderer.end();

        game.batch.begin();
        game.fontSmall.setColor(1, 1, 1, 1);
        game.fontSmall.draw(game.batch, text, x + w/2 - 40, y - h/2 + 5);
        game.batch.end();
    }

    boolean isTabClicked(float x, float y, float w, float h) {
        float mx = Gdx.input.getX();
        float my = Gdx.graphics.getHeight() - Gdx.input.getY();
        return mx >= x && mx <= x + w && my >= y - h && my <= y && Gdx.input.justTouched();
    }

    boolean isEntryClicked(float x, float y, float w, float h) {
        float mx = Gdx.input.getX();
        float my = Gdx.graphics.getHeight() - Gdx.input.getY();
        return mx >= x && mx <= x + w && my >= y && my <= y + h && Gdx.input.justTouched();
    }

    void drawButton(float x, float y, float bw, float bh, String text, int hoverCol, int col) {
        float mx = Gdx.input.getX();
        float my = Gdx.graphics.getHeight() - Gdx.input.getY();
        boolean hover = mx >= x && mx <= x + bw && my >= y && my <= y + bh;

        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        if (hover) {
            game.shapeRenderer.setColor(((hoverCol >> 16) & 0xFF)/255f,
                ((hoverCol >> 8) & 0xFF)/255f, (hoverCol & 0xFF)/255f, 1);
        } else {
            game.shapeRenderer.setColor(((col >> 16) & 0xFF)/255f,
                ((col >> 8) & 0xFF)/255f, (col & 0xFF)/255f, 1);
        }
        game.shapeRenderer.rect(x, y, bw, bh);
        game.shapeRenderer.end();

        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        game.shapeRenderer.setColor(1, 1, 1, 1);
        game.shapeRenderer.rect(x, y, bw, bh);
        game.shapeRenderer.end();

        game.batch.begin();
        game.font.setColor(1, 1, 1, 1);
        game.font.draw(game.batch, text, x + bw/2 - 30, y + bh/2 + 10);
        game.batch.end();
    }

    boolean isButtonClicked(float x, float y, float w, float h) {
        float mx = Gdx.input.getX();
        float my = Gdx.graphics.getHeight() - Gdx.input.getY();
        return mx >= x && mx <= x + w && my >= y && my <= y + h && Gdx.input.justTouched();
    }

    @Override public void show() {}
    @Override public void resize(int width, int height) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override public void dispose() {}
}
