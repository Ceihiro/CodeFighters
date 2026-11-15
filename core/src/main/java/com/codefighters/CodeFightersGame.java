package com.codefighters;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class CodeFightersGame extends Game {
    public SpriteBatch batch;
    public BitmapFont font;
    public BitmapFont fontBold;
    public BitmapFont fontSmall;
    public ShapeRenderer shapeRenderer;
    public GameProgress progress;

    // Sound effects
    public Sound sndHitPunch;
    public Sound sndSpecialFire;
    public Sound sndBlockShield;
    public Sound sndDamageGlitch;
    public Sound sndWinJingle;

    public MenuScreen menuScreen;
    public CharacterSelectScreen charSelectScreen;
    public FightScreen fightScreen;
    public WinScreen winScreen;
    public LoseScreen loseScreen;
    public GalleryScreen galleryScreen;

    @Override
    public void create() {
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();

        // Create fonts safely - if .fnt file missing, use built-in font
        try {
            font = new BitmapFont(Gdx.files.internal("default.fnt"));
            fontBold = new BitmapFont(Gdx.files.internal("default.fnt"));
            fontSmall = new BitmapFont(Gdx.files.internal("default.fnt"));
        } catch (Exception e) {
            // Fallback to default libGDX font
            font = new BitmapFont();
            fontBold = new BitmapFont();
            fontSmall = new BitmapFont();
        }

        fontBold.getData().setScale(1.4f);
        font.getData().setScale(1.1f);
        fontSmall.getData().setScale(0.8f);

        progress = new GameProgress();

        // Load sound effects safely - if files missing, null is OK
        sndHitPunch = loadSoundSafe("sounds/hit_punch.ogg");
        sndSpecialFire = loadSoundSafe("sounds/special_fire.ogg");
        sndBlockShield = loadSoundSafe("sounds/block_shield.ogg");
        sndDamageGlitch = loadSoundSafe("sounds/damage_glitch.ogg");
        sndWinJingle = loadSoundSafe("sounds/win_jingle.ogg");

        menuScreen = new MenuScreen(this);
        charSelectScreen = new CharacterSelectScreen(this);
        fightScreen = new FightScreen(this);
        winScreen = new WinScreen(this);
        loseScreen = new LoseScreen(this);
        galleryScreen = new GalleryScreen(this);

        setScreen(menuScreen);
    }

    // Helper to load sound without crashing
    Sound loadSoundSafe(String path) {
        try {
            if (Gdx.files.internal(path).exists()) {
                return Gdx.audio.newSound(Gdx.files.internal(path));
            } else {
                System.out.println("Sound not found: " + path);
                return null;
            }
        } catch (Exception e) {
            System.out.println("Failed to load sound: " + path + " - " + e.getMessage());
            return null;
        }
    }

    // Helper to play sound safely (won't crash if null)
    public void playSound(Sound sound, float volume) {
        if (sound != null) {
            sound.play(volume);
        }
    }

    @Override
    public void render() {
        super.render();
    }

    @Override
    public void dispose() {
        batch.dispose();
        shapeRenderer.dispose();
        font.dispose();
        fontBold.dispose();
        fontSmall.dispose();

        // Dispose sounds safely
        disposeSound(sndHitPunch);
        disposeSound(sndSpecialFire);
        disposeSound(sndBlockShield);
        disposeSound(sndDamageGlitch);
        disposeSound(sndWinJingle);
    }

    void disposeSound(Sound sound) {
        if (sound != null) sound.dispose();
    }
}
