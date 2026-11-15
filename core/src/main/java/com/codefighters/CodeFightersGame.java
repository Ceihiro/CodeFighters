package com.codefighters;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class CodeFightersGame extends Game {
    public SpriteBatch batch;
    public BitmapFont fontTitle;
    public BitmapFont fontBold;
    public BitmapFont font;
    public BitmapFont fontSmall;
    public GlyphLayout glyphLayout;
    public ShapeRenderer shapeRenderer;
    public GameProgress progress;

    // Sound effects
    public Sound sndHitPunch;
    public Sound sndSpecialFire;
    public Sound sndBlockShield;
    public Sound sndDamageGlitch;
    public Sound sndWinJingle;

    // Textures
    public Texture texBgMenu;
    public Texture texBgFight;
    public Texture texPlayerIdle;
    public Texture texPlayerIdle2;
    public Texture texPlayerAttack1;
    public Texture texPlayerAttack2;
    public Texture texEnemyIdle;
    public Texture texEnemyIdle2;
    public Texture texEnemyAttack1;
    public Texture texEnemyAttack2;
    public Texture texTitleLogo;
    public Texture texButtonNormal;
    public Texture texButtonPressed;

    // Screens
    public SplashScreen splashScreen;
    public MenuScreen menuScreen;
    public VSScreen vsScreen;
    public CharacterSelectScreen charSelectScreen;
    public FightScreen fightScreen;
    public WinScreen winScreen;
    public LoseScreen loseScreen;
    public GalleryScreen galleryScreen;
    public HowToPlayScreen howToPlayScreen;

    @Override
    public void create() {
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        glyphLayout = new GlyphLayout();

        try {
            fontTitle = new BitmapFont(Gdx.files.internal("default.fnt"));
            fontBold = new BitmapFont(Gdx.files.internal("default.fnt"));
            font = new BitmapFont(Gdx.files.internal("default.fnt"));
            fontSmall = new BitmapFont(Gdx.files.internal("default.fnt"));
        } catch (Exception e) {
            fontTitle = new BitmapFont();
            fontBold = new BitmapFont();
            font = new BitmapFont();
            fontSmall = new BitmapFont();
        }

        updateFontScales();

        progress = new GameProgress();

        // Load sounds safely
        sndHitPunch = loadSoundSafe("sounds/hit_punch.ogg");
        sndSpecialFire = loadSoundSafe("sounds/special_fire.ogg");
        sndBlockShield = loadSoundSafe("sounds/block_shield.ogg");
        sndDamageGlitch = loadSoundSafe("sounds/damage_glitch.ogg");
        sndWinJingle = loadSoundSafe("sounds/win_jingle.ogg");

        // Load textures safely
        texBgMenu = loadTextureSafe("images/bg_menu.png");
        texBgFight = loadTextureSafe("images/bg_fight.png");
        texPlayerIdle = loadTextureSafe("images/player_idle_1.png");
        texPlayerIdle2 = loadTextureSafe("images/player_idle_2.png");
        texPlayerAttack1 = loadTextureSafe("images/player_attack1_1.png");
        texPlayerAttack2 = loadTextureSafe("images/player_attack1_2.png");
        texEnemyIdle = loadTextureSafe("images/enemy_idle_1.png");
        texEnemyIdle2 = loadTextureSafe("images/enemy_idle_2.png");
        texEnemyAttack1 = loadTextureSafe("images/enemy_attack_1.png");
        texEnemyAttack2 = loadTextureSafe("images/enemy_attack_2.png");
        texTitleLogo = loadTextureSafe("images/ui_title_logo.png");
        texButtonNormal = loadTextureSafe("images/ui_button_normal.png");
        texButtonPressed = loadTextureSafe("images/ui_button_normal.png");

        splashScreen = new SplashScreen(this);
        menuScreen = new MenuScreen(this);
        vsScreen = new VSScreen(this);
        charSelectScreen = new CharacterSelectScreen(this);
        fightScreen = new FightScreen(this);
        winScreen = new WinScreen(this);
        loseScreen = new LoseScreen(this);
        galleryScreen = new GalleryScreen(this);
        howToPlayScreen = new HowToPlayScreen(this);

        setScreen(splashScreen);
    }

    public void updateFontScales() {
        float h = Gdx.graphics.getHeight();
        // Generous scaling factor so text is sharp, large, and easily readable on mobile
        float scaleFactor = Math.max(1.0f, h / 540f);
        fontTitle.getData().setScale(scaleFactor * 2.2f);
        fontBold.getData().setScale(scaleFactor * 1.55f);
        font.getData().setScale(scaleFactor * 1.25f);
        fontSmall.getData().setScale(scaleFactor * 0.95f);
    }

    public void drawTextCentered(BitmapFont f, String text, float cx, float cy) {
        glyphLayout.setText(f, text);
        f.draw(batch, text, cx - glyphLayout.width / 2f, cy + glyphLayout.height / 2f);
    }

    public float getTextWidth(BitmapFont f, String text) {
        glyphLayout.setText(f, text);
        return glyphLayout.width;
    }

    public float getTextHeight(BitmapFont f, String text) {
        glyphLayout.setText(f, text);
        return glyphLayout.height;
    }

    Sound loadSoundSafe(String path) {
        try {
            if (Gdx.files.internal(path).exists()) {
                return Gdx.audio.newSound(Gdx.files.internal(path));
            } else {
                System.out.println("Sound not found: " + path);
                return null;
            }
        } catch (Exception e) {
            System.out.println("Failed to load sound: " + path);
            return null;
        }
    }

    Texture loadTextureSafe(String path) {
        try {
            if (Gdx.files.internal(path).exists()) {
                return new Texture(Gdx.files.internal(path));
            } else {
                System.out.println("Texture not found: " + path);
                return null;
            }
        } catch (Exception e) {
            System.out.println("Failed to load texture: " + path);
            return null;
        }
    }

    public void playSound(Sound sound, float volume) {
        if (sound != null) sound.play(volume);
    }

    @Override
    public void render() {
        super.render();
    }

    @Override
    public void dispose() {
        batch.dispose();
        shapeRenderer.dispose();
        fontTitle.dispose();
        fontBold.dispose();
        font.dispose();
        fontSmall.dispose();

        disposeSound(sndHitPunch);
        disposeSound(sndSpecialFire);
        disposeSound(sndBlockShield);
        disposeSound(sndDamageGlitch);
        disposeSound(sndWinJingle);

        disposeTexture(texBgMenu);
        disposeTexture(texBgFight);
        disposeTexture(texPlayerIdle);
        disposeTexture(texPlayerIdle2);
        disposeTexture(texPlayerAttack1);
        disposeTexture(texPlayerAttack2);
        disposeTexture(texEnemyIdle);
        disposeTexture(texEnemyIdle2);
        disposeTexture(texEnemyAttack1);
        disposeTexture(texEnemyAttack2);
        disposeTexture(texTitleLogo);
        disposeTexture(texButtonNormal);
        disposeTexture(texButtonPressed);
    }

    void disposeSound(Sound sound) {
        if (sound != null) sound.dispose();
    }

    void disposeTexture(Texture tex) {
        if (tex != null) tex.dispose();
    }
}
