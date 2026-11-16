package com.codefighters;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class SettingsScreen implements Screen {
    final CodeFightersGame game;
    Preferences prefs;

    public float sfxVolume = 1.0f;
    public boolean screenShake = true;
    public String difficulty = "NORMAL"; // EASY, NORMAL, HARD

    public SettingsScreen(final CodeFightersGame game) {
        this.game = game;
        loadSettings();
    }

    public final void loadSettings() {
        try {
            prefs = Gdx.app.getPreferences("CodeFightersPrefs");
            sfxVolume = prefs.getFloat("sfxVolume", 1.0f);
            screenShake = prefs.getBoolean("screenShake", true);
            difficulty = prefs.getString("difficulty", "NORMAL");
        } catch (Exception e) {
            sfxVolume = 1.0f;
            screenShake = true;
            difficulty = "NORMAL";
        }
    }

    void saveSettings() {
        try {
            if (prefs == null) {
                prefs = Gdx.app.getPreferences("CodeFightersPrefs");
            }
            if (prefs != null) {
                prefs.putFloat("sfxVolume", sfxVolume);
                prefs.putBoolean("screenShake", screenShake);
                prefs.putString("difficulty", difficulty);
                prefs.flush();
            }
        } catch (Exception ignored) {}
    }

    @Override
    public void show() {
        game.updateFontScales();
        loadSettings();
    }

    @Override
    public void render(float delta) {
        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();

        Gdx.gl.glClearColor(0.02f, 0.02f, 0.06f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Background
        game.batch.begin();
        if (game.texBgMenu != null) {
            game.batch.setColor(0.25f, 0.35f, 0.45f, 1f);
            game.batch.draw(game.texBgMenu, 0, 0, w, h);
            game.batch.setColor(1, 1, 1, 1);
        }
        game.batch.end();

        // Cyber grid
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        game.shapeRenderer.setColor(0, 0, 0, 0.65f);
        game.shapeRenderer.rect(0, 0, w, h);
        game.shapeRenderer.end();

        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        game.shapeRenderer.setColor(0.08f, 0.10f, 0.18f, 1);
        for (int i = 0; i < w; i += 50) game.shapeRenderer.line(i, 0, i, h);
        for (int i = 0; i < h; i += 50) game.shapeRenderer.line(0, i, w, i);
        game.shapeRenderer.end();

        // Title Header
        game.batch.begin();
        game.fontTitle.setColor(0.31f, 0.76f, 0.97f, 1);
        game.drawTextCentered(game.fontTitle, "SYSTEM CONFIGURATION", w / 2f, h * 0.91f);
        game.batch.end();

        // Settings Panel
        float cardW = w * 0.75f;
        float cardH = h * 0.65f;
        float cardX = w / 2f - cardW / 2f;
        float cardY = h * 0.18f;

        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        game.shapeRenderer.setColor(0.04f, 0.07f, 0.12f, 0.92f);
        game.shapeRenderer.rect(cardX, cardY, cardW, cardH);
        game.shapeRenderer.end();

        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        game.shapeRenderer.setColor(0.2f, 0.45f, 0.75f, 0.85f);
        game.shapeRenderer.rect(cardX, cardY, cardW, cardH);
        game.shapeRenderer.end();

        float rowH = cardH * 0.18f;
        float row1Y = cardY + cardH * 0.72f;
        float row2Y = cardY + cardH * 0.48f;
        float row3Y = cardY + cardH * 0.24f;

        // ROW 1: SFX Sound Audio Toggle
        drawSettingRow(cardX + 30, row1Y, cardW - 60, rowH,
            "SOUND EFFECTS",
            (sfxVolume > 0.05f) ? "ON (100%)" : "MUTED",
            (sfxVolume > 0.05f) ? 0x00e676 : 0x757575);
        if (isButtonClicked(cardX + cardW - 240, row1Y, 210, rowH)) {
            sfxVolume = (sfxVolume > 0.05f) ? 0.0f : 1.0f;
            saveSettings();
            game.playSound(game.sndMenuSelect, 0.8f);
        }

        // ROW 2: Screen Shake Toggle
        drawSettingRow(cardX + 30, row2Y, cardW - 60, rowH,
            "SCREEN IMPACT SHAKE",
            screenShake ? "ENABLED" : "DISABLED",
            screenShake ? 0x00bcd4 : 0x757575);
        if (isButtonClicked(cardX + cardW - 240, row2Y, 210, rowH)) {
            screenShake = !screenShake;
            saveSettings();
            game.playSound(game.sndMenuSelect, 0.8f);
        }

        // ROW 3: AI Difficulty
        int diffCol = difficulty.equals("EASY") ? 0x4caf50 : (difficulty.equals("NORMAL") ? 0x00bcd4 : 0xff1744);
        drawSettingRow(cardX + 30, row3Y, cardW - 60, rowH,
            "AI DIFFICULTY",
            difficulty,
            diffCol);
        if (isButtonClicked(cardX + cardW - 240, row3Y, 210, rowH)) {
            if (difficulty.equals("EASY")) difficulty = "NORMAL";
            else if (difficulty.equals("NORMAL")) difficulty = "HARD";
            else difficulty = "EASY";
            saveSettings();
            game.playSound(game.sndMenuSelect, 0.8f);
        }

        // Back button
        float btnW = w * 0.22f;
        float btnH = h * 0.075f;
        drawButton(w / 2f - btnW / 2f, h * 0.045f, btnW, btnH, "SAVE & RETURN", 0x00bcd4, 0x006064);
        if (isButtonClicked(w / 2f - btnW / 2f, h * 0.045f, btnW, btnH)) {
            saveSettings();
            game.playSound(game.sndMenuBack, 0.8f);
            game.setScreen(game.menuScreen);
        }
    }

    void drawSettingRow(float x, float y, float w, float h, String label, String value, int valCol) {
        float r = ((valCol >> 16) & 0xFF) / 255f;
        float g = ((valCol >> 8) & 0xFF) / 255f;
        float b = (valCol & 0xFF) / 255f;

        // Label on left
        game.batch.begin();
        game.fontBold.setColor(0.95f, 0.95f, 0.98f, 1f);
        game.fontBold.draw(game.batch, label, x, y + h * 0.65f);
        game.batch.end();

        // Button box on right
        float btnW = 210;
        float btnX = x + w - btnW;

        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        game.shapeRenderer.setColor(r * 0.35f, g * 0.35f, b * 0.35f, 0.85f);
        game.shapeRenderer.rect(btnX, y, btnW, h);
        game.shapeRenderer.end();

        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        game.shapeRenderer.setColor(r, g, b, 1f);
        game.shapeRenderer.rect(btnX, y, btnW, h);
        game.shapeRenderer.end();

        game.batch.begin();
        game.fontBold.setColor(r, g, b, 1f);
        game.drawTextCentered(game.fontBold, value, btnX + btnW / 2f, y + h / 2f);
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
        game.shapeRenderer.setColor(r, g, b, 0.85f);
        game.shapeRenderer.rect(x, y, bw, bh);
        game.shapeRenderer.end();

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
