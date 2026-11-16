package com.codefighters;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
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
    public GameMode currentMode = GameMode.ARCADE;

    // --- Audio: Sounds (25 files) ---
    public Sound sndMenuNav;
    public Sound sndMenuSelect;
    public Sound sndMenuBack;
    public Sound sndRoundStart;
    public Sound sndAnnouncerFight;
    public Sound sndAnnouncerKo;
    public Sound sndAnnouncerWinner;
    public Sound sndGalleryUnlock;
    public Sound sndLowHpWarning;
    public Sound sndTimeOver;
    public Sound sndHitPunch;
    public Sound sndHitHeavy;
    public Sound sndBlockShield;
    public Sound sndSpecialCharge;
    public Sound sndSpecialFire;
    public Sound sndSpecialHit;
    public Sound sndDashWhoosh;
    public Sound sndDashAttackImpact;
    public Sound sndDamageByte;
    public Sound sndDamageWannacry;
    public Sound sndVictoryFanfare;
    public Sound sndDefeatJingle;
    // Backward compatibility aliases
    public Sound sndWinJingle;
    public Sound sndDamageGlitch;
    public Sound sndGlitchTransition;

    // --- Audio: Music ---
    public Music musicMenu;
    public Music musicFight;
    public Music musicWin;
    public Music currentMusic = null;

    // --- Textures: Menu Parallax Layers ---
    public Texture texLayerSky;
    public Texture texLayerFar;
    public Texture texLayerMid;
    public Texture texLayerFloor;
    public Texture texLayerGlow;
    public Texture texRun1;
    public Texture texRun2;
    public Texture texRun3;
    public Texture texRun4;

    // --- Textures: Backgrounds (8 files) ---
    public Texture texBgMenu;
    public Texture texBgFight;
    public Texture texBgCharSelect;
    public Texture texBgVersus;
    public Texture texBgGallery;
    public Texture texBgWin;
    public Texture texBgLose;
    public Texture texBgLoading;

    // --- Textures: Character Byte (15 files) ---
    public Texture texByteIdle1;
    public Texture texByteIdle2;
    public Texture texByteWalk1;
    public Texture texByteWalk2;
    public Texture texByteAttack1;
    public Texture texByteAttack2;
    public Texture texByteAttack3;
    public Texture texByteDash;
    public Texture texByteDashAttack;
    public Texture texByteBlock;
    public Texture texByteSpecialCharge;
    public Texture texByteSpecialFire;
    public Texture texByteHit1;
    public Texture texByteHit2;
    public Texture texByteWin1;
    public Texture texByteWin2;
    public Texture texByteLose;
    public Texture texBytePortrait;

    // --- Textures: Character WannaCry (12 files) ---
    public Texture texWannaCryIdle1;
    public Texture texWannaCryIdle2;
    public Texture texWannaCryWalk;
    public Texture texWannaCryAttack1;
    public Texture texWannaCryAttack2;
    public Texture texWannaCryBlock;
    public Texture texWannaCrySpecial;
    public Texture texWannaCryHit1;
    public Texture texWannaCryHit2;
    public Texture texWannaCryLose;
    public Texture texWannaCryWin;
    public Texture texWannaCryPortrait;

    // Aliases for compatibility
    public Texture texPlayerIdle;
    public Texture texPlayerIdle2;
    public Texture texPlayerAttack1;
    public Texture texPlayerAttack2;
    public Texture texEnemyIdle;
    public Texture texEnemyIdle2;
    public Texture texEnemyAttack1;
    public Texture texEnemyAttack2;

    // --- Textures: Effects (10 files) ---
    public Texture texFxHitSpark;
    public Texture texFxBlockSpark;
    public Texture texFxDashTrail;
    public Texture texFxDashDust;
    public Texture texFxSpecialAura;
    public Texture texFxProjectileTrail;
    public Texture texFxEncryptBurst;
    public Texture texFxFlashWhite;
    public Texture texFxKnockbackLines;
    public Texture texFxConfetti;
    public Texture texGlitchNoise;

    // --- Textures: Projectiles (3 files) ---
    public Texture texProjBlast1;
    public Texture texProjBlast2;
    public Texture texProjBlastTrail;

    // --- Textures: UI Shared (4 files) ---
    public Texture texUiScanlines;
    public Texture texUiVignette;
    public Texture texUiDivider;
    public Texture texUiCursor;

    // --- Textures: UI Buttons (10 files) ---
    public Texture texBtnNormal;
    public Texture texBtnPressed;
    public Texture texBtnSmall;
    public Texture texBtnSmallPressed;
    public Texture texBtnArrowLeft;
    public Texture texBtnArrowRight;
    public Texture texBtnClose;
    public Texture texBtnBack;
    public Texture texBtnReady;
    public Texture texBtnLocked;
    public Texture texButtonNormal;
    public Texture texButtonPressed;

    // --- Textures: UI Controls (11 files) ---
    public Texture texDpadLeft;
    public Texture texDpadLeftPressed;
    public Texture texDpadRight;
    public Texture texDpadRightPressed;
    public Texture texBtnAttack;
    public Texture texBtnAttackPressed;
    public Texture texBtnSpecial;
    public Texture texBtnSpecialPressed;
    public Texture texBtnBlock;
    public Texture texBtnBlockPressed;
    public Texture texBtnPause;

    // --- Textures: UI HUD (16 files) ---
    public Texture texHpBarEmpty;
    public Texture texHpBarFillPlayer;
    public Texture texHpBarFillEnemy;
    public Texture texHpBarDamage;
    public Texture texSuperBarEmpty;
    public Texture texSuperBarFill;
    public Texture texPortraitFramePlayer;
    public Texture texPortraitFrameEnemy;
    public Texture texPortraitFrameP2;
    public Texture texVsBadge;
    public Texture texRoundDotEmpty;
    public Texture texRoundDotFilledP1;
    public Texture texRoundDotFilledP2;
    public Texture texTimerFrame;
    public Texture texComboBg;
    public Texture texPauseOverlay;

    // --- Textures: UI Screens (25 files) ---
    public Texture texTitleLogo;
    public Texture texTitleLogoGlow;
    public Texture texTitleLogoLarge;
    public Texture texTitleLogoIcon;
    public Texture texMenuCharPreview;
    public Texture texCharSelectFrame;
    public Texture texCharSelectFrameSelected;
    public Texture texCharSelectFrameLocked;
    public Texture texVsScreenPanel;
    public Texture texVsScreenDivider;
    public Texture texRoundBanner;
    public Texture texFightBanner;
    public Texture texKoBanner;
    public Texture texTimeOverBanner;
    public Texture texWinnerBanner;
    public Texture texPanelSettings;
    public Texture texPanelHowToPlay;
    public Texture texPanelGallery;
    public Texture texPanelGalleryDetail;
    public Texture texScrollIndicator;
    public Texture texCheckboxEmpty;
    public Texture texCheckboxChecked;
    public Texture texSliderTrack;
    public Texture texSliderKnob;
    public Texture texLoadingBarEmpty;
    public Texture texLoadingBarFill;
    public Texture texAttractModeDim;

    // Complete 17-Screen Architecture
    public SplashScreen splashScreen;
    public LoadingScreen loadingScreen;
    public MenuScreen menuScreen;
    public ModeSelectScreen modeSelectScreen;
    public CharacterSelectScreen charSelectScreen;
    public VSScreen vsScreen;
    public VersusCharacterSelectScreen versusCharSelectScreen;
    public VersusVsScreen versusVsScreen;
    public FightScreen fightScreen;
    public WinScreen winScreen;
    public LoseScreen loseScreen;
    public GalleryScreen galleryScreen;
    public GalleryDetailScreen galleryDetailScreen;
    public HowToPlayScreen howToPlayScreen;
    public SettingsScreen settingsScreen;
    public CreditsScreen creditsScreen;

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

        loadAllAssets();

        // Initialize screens
        settingsScreen = new SettingsScreen(this);
        splashScreen = new SplashScreen(this);
        loadingScreen = new LoadingScreen(this);
        menuScreen = new MenuScreen(this);
        modeSelectScreen = new ModeSelectScreen(this);
        charSelectScreen = new CharacterSelectScreen(this);
        vsScreen = new VSScreen(this);
        versusCharSelectScreen = new VersusCharacterSelectScreen(this);
        versusVsScreen = new VersusVsScreen(this);
        fightScreen = new FightScreen(this);
        winScreen = new WinScreen(this);
        loseScreen = new LoseScreen(this);
        galleryScreen = new GalleryScreen(this);
        galleryDetailScreen = new GalleryDetailScreen(this);
        howToPlayScreen = new HowToPlayScreen(this);
        creditsScreen = new CreditsScreen(this);

        // Directly open main menu (studio intro video is played on app launch)
        setScreen(menuScreen);
    }

    private void loadAllAssets() {
        // --- Sounds ---
        sndMenuNav = loadSoundSafe("sounds/menu_navigate.ogg");
        sndMenuSelect = loadSoundSafe("sounds/menu_select.ogg");
        sndMenuBack = loadSoundSafe("sounds/menu_back.ogg");
        sndRoundStart = loadSoundSafe("sounds/round_start.ogg");
        sndAnnouncerFight = loadSoundSafe("sounds/announcer_fight.ogg");
        sndAnnouncerKo = loadSoundSafe("sounds/announcer_ko.ogg");
        sndAnnouncerWinner = loadSoundSafe("sounds/announcer_winner.ogg");
        sndGalleryUnlock = loadSoundSafe("sounds/gallery_unlock.ogg");
        sndLowHpWarning = loadSoundSafe("sounds/low_hp_warning.ogg");
        sndTimeOver = loadSoundSafe("sounds/time_over.ogg");
        sndHitPunch = loadSoundSafe("sounds/hit_punch.ogg");
        sndHitHeavy = loadSoundSafe("sounds/hit_heavy.ogg");
        sndBlockShield = loadSoundSafe("sounds/block_shield.ogg");
        sndSpecialCharge = loadSoundSafe("sounds/special_charge.ogg");
        sndSpecialFire = loadSoundSafe("sounds/special_fire.ogg");
        sndSpecialHit = loadSoundSafe("sounds/special_hit.ogg");
        sndDashWhoosh = loadSoundSafe("sounds/dash_whoosh.ogg");
        sndDashAttackImpact = loadSoundSafe("sounds/dash_attack_impact.ogg");
        sndDamageByte = loadSoundSafe("sounds/damage_byte.ogg");
        sndDamageWannacry = loadSoundSafe("sounds/damage_wannacry.ogg");
        sndVictoryFanfare = loadSoundSafe("sounds/victory_fanfare.ogg");
        sndDefeatJingle = loadSoundSafe("sounds/defeat_jingle.ogg");
        // Aliases
        sndWinJingle = sndVictoryFanfare != null ? sndVictoryFanfare : sndHitPunch;
        sndDamageGlitch = sndDamageWannacry != null ? sndDamageWannacry : sndHitPunch;
        sndGlitchTransition = loadSoundSafe("sounds/glitch_transition.ogg");

        // --- Music ---
        musicMenu = loadMusicSafe("sounds/menu_music.ogg");
        musicFight = loadMusicSafe("sounds/fight_music.ogg");
        musicWin = loadMusicSafe("sounds/win_music.ogg");

        // --- Backgrounds ---
        // --- Menu Parallax Layers ---
        texLayerSky   = loadTextureSafe("images/menu_parallax/layer_sky.png");
        texLayerFar   = loadTextureSafe("images/menu_parallax/layer_far.png");
        texLayerMid   = loadTextureSafe("images/menu_parallax/layer_mid.png");
        texLayerFloor = loadTextureSafe("images/menu_parallax/layer_floor.png");
        texLayerGlow  = loadTextureSafe("images/menu_parallax/layer_glow.png");
        texRun1       = loadTextureSafe("images/menu_parallax/run_1.png");
        texRun2       = loadTextureSafe("images/menu_parallax/run_2.png");
        texRun3       = loadTextureSafe("images/menu_parallax/run_3.png");
        texRun4       = loadTextureSafe("images/menu_parallax/run_4.png");

        // --- Backgrounds ---
        texBgMenu = loadTextureSafe("images/backgrounds/menu.png");
        texBgFight = loadTextureSafe("images/backgrounds/fight.png");
        texBgCharSelect = loadTextureSafe("images/backgrounds/character_select.png");
        texBgVersus = loadTextureSafe("images/backgrounds/versus.png");
        texBgGallery = loadTextureSafe("images/backgrounds/gallery.png");
        texBgWin = loadTextureSafe("images/backgrounds/win.png");
        texBgLose = loadTextureSafe("images/backgrounds/lose.png");
        texBgLoading = loadTextureSafe("images/backgrounds/loading.png");

        // --- Character: Byte ---
        texByteIdle1 = loadTextureSafe("images/byte/idle_1.png");
        texByteIdle2 = loadTextureSafe("images/byte/idle_2.png");
        texByteWalk1 = loadTextureSafe("images/byte/walk_1.png");
        texByteWalk2 = loadTextureSafe("images/byte/walk_2.png");
        texByteAttack1 = loadTextureSafe("images/byte/attack_1.png");
        texByteAttack2 = loadTextureSafe("images/byte/attack_2.png");
        texByteAttack3 = loadTextureSafe("images/byte/attack_3.png");
        texByteDash = loadTextureSafe("images/byte/dash.png");
        texByteDashAttack = loadTextureSafe("images/byte/dash_attack.png");
        texByteBlock = loadTextureSafe("images/byte/block.png");
        texByteSpecialCharge = loadTextureSafe("images/byte/special_charge.png");
        texByteSpecialFire = loadTextureSafe("images/byte/special_fire.png");
        texByteHit1 = loadTextureSafe("images/byte/hit_1.png");
        texByteHit2 = loadTextureSafe("images/byte/hit_2.png");
        texByteWin1 = loadTextureSafe("images/byte/win_1.png");
        texByteWin2 = loadTextureSafe("images/byte/win_2.png");
        texByteLose = loadTextureSafe("images/byte/lose.png");
        texBytePortrait = loadTextureSafe("images/byte/portrait.png");

        // --- Character: WannaCry ---
        texWannaCryIdle1 = loadTextureSafe("images/wannacry/idle_1.png");
        texWannaCryIdle2 = loadTextureSafe("images/wannacry/idle_2.png");
        texWannaCryWalk = loadTextureSafe("images/wannacry/walk.png");
        texWannaCryAttack1 = loadTextureSafe("images/wannacry/attack_1.png");
        texWannaCryAttack2 = loadTextureSafe("images/wannacry/attack_2.png");
        texWannaCryBlock = loadTextureSafe("images/wannacry/block.png");
        texWannaCrySpecial = loadTextureSafe("images/wannacry/special.png");
        texWannaCryHit1 = loadTextureSafe("images/wannacry/hit_1.png");
        texWannaCryHit2 = loadTextureSafe("images/wannacry/hit_2.png");
        texWannaCryLose = loadTextureSafe("images/wannacry/lose.png");
        texWannaCryWin = loadTextureSafe("images/wannacry/win.png");
        texWannaCryPortrait = loadTextureSafe("images/wannacry/portrait.png");

        // Map compatibility aliases
        texPlayerIdle = texByteIdle1;
        texPlayerIdle2 = texByteIdle2 != null ? texByteIdle2 : texByteIdle1;
        texPlayerAttack1 = texByteAttack1;
        texPlayerAttack2 = texByteAttack2;
        texEnemyIdle = texWannaCryIdle1;
        texEnemyIdle2 = texWannaCryIdle2 != null ? texWannaCryIdle2 : texWannaCryIdle1;
        texEnemyAttack1 = texWannaCryAttack1;
        texEnemyAttack2 = texWannaCryAttack2;

        // --- Effects ---
        texFxHitSpark = loadTextureSafe("images/effects/hit_spark.png");
        texFxBlockSpark = loadTextureSafe("images/effects/block_spark.png");
        texFxDashTrail = loadTextureSafe("images/effects/dash_trail.png");
        texFxDashDust = loadTextureSafe("images/effects/dash_dust.png");
        texFxSpecialAura = loadTextureSafe("images/effects/special_aura.png");
        texFxProjectileTrail = loadTextureSafe("images/effects/projectile_trail.png");
        texFxEncryptBurst = loadTextureSafe("images/effects/encrypt_burst.png");
        texFxFlashWhite = loadTextureSafe("images/effects/screen_flash_white.png");
        texFxKnockbackLines = loadTextureSafe("images/effects/knockback_lines.png");
        texFxConfetti = loadTextureSafe("images/effects/victory_confetti.png");
        texGlitchNoise = loadTextureSafe("images/effects/glitch_noise_tile.png");

        // --- Projectiles ---
        texProjBlast1 = loadTextureSafe("images/projectiles/code_blast_1.png");
        texProjBlast2 = loadTextureSafe("images/projectiles/code_blast_2.png");
        texProjBlastTrail = loadTextureSafe("images/projectiles/code_blast_trail.png");

        // --- UI Shared ---
        texUiScanlines = loadTextureSafe("images/ui/shared/scanline_overlay.png");
        texUiVignette = loadTextureSafe("images/ui/shared/vignette.png");
        texUiDivider = loadTextureSafe("images/ui/shared/divider_line.png");
        texUiCursor = loadTextureSafe("images/ui/shared/cursor_pointer.png");

        // --- UI Buttons ---
        texBtnNormal = loadTextureSafe("images/ui/buttons/button_normal.png");
        texBtnPressed = loadTextureSafe("images/ui/buttons/button_pressed.png");
        texBtnSmall = loadTextureSafe("images/ui/buttons/button_small.png");
        texBtnSmallPressed = loadTextureSafe("images/ui/buttons/button_small_pressed.png");
        texBtnArrowLeft = loadTextureSafe("images/ui/buttons/button_arrow_left.png");
        texBtnArrowRight = loadTextureSafe("images/ui/buttons/button_arrow_right.png");
        texBtnClose = loadTextureSafe("images/ui/buttons/button_close.png");
        texBtnBack = loadTextureSafe("images/ui/buttons/button_back.png");
        texBtnReady = loadTextureSafe("images/ui/buttons/button_ready.png");
        texBtnLocked = loadTextureSafe("images/ui/buttons/button_locked.png");
        texButtonNormal = texBtnNormal;
        texButtonPressed = texBtnPressed;

        // --- UI Controls ---
        texDpadLeft = loadTextureSafe("images/ui/controls/dpad_left.png");
        texDpadLeftPressed = loadTextureSafe("images/ui/controls/dpad_left_pressed.png");
        texDpadRight = loadTextureSafe("images/ui/controls/dpad_right.png");
        texDpadRightPressed = loadTextureSafe("images/ui/controls/dpad_right_pressed.png");
        texBtnAttack = loadTextureSafe("images/ui/controls/btn_attack.png");
        texBtnAttackPressed = loadTextureSafe("images/ui/controls/btn_attack_pressed.png");
        texBtnSpecial = loadTextureSafe("images/ui/controls/btn_special.png");
        texBtnSpecialPressed = loadTextureSafe("images/ui/controls/btn_special_pressed.png");
        texBtnBlock = loadTextureSafe("images/ui/controls/btn_block.png");
        texBtnBlockPressed = loadTextureSafe("images/ui/controls/btn_block_pressed.png");
        texBtnPause = loadTextureSafe("images/ui/controls/btn_pause.png");

        // --- UI HUD ---
        texHpBarEmpty = loadTextureSafe("images/ui/hud/hp_bar_empty.png");
        texHpBarFillPlayer = loadTextureSafe("images/ui/hud/hp_bar_fill_player.png");
        texHpBarFillEnemy = loadTextureSafe("images/ui/hud/hp_bar_fill_enemy.png");
        texHpBarDamage = loadTextureSafe("images/ui/hud/hp_bar_damage_overlay.png");
        texSuperBarEmpty = loadTextureSafe("images/ui/hud/super_bar_empty.png");
        texSuperBarFill = loadTextureSafe("images/ui/hud/super_bar_fill.png");
        texPortraitFramePlayer = loadTextureSafe("images/ui/hud/portrait_frame_player.png");
        texPortraitFrameEnemy = loadTextureSafe("images/ui/hud/portrait_frame_enemy.png");
        texPortraitFrameP2 = loadTextureSafe("images/ui/hud/portrait_frame_p2.png");
        texVsBadge = loadTextureSafe("images/ui/hud/vs_badge.png");
        texRoundDotEmpty = loadTextureSafe("images/ui/hud/round_dot_empty.png");
        texRoundDotFilledP1 = loadTextureSafe("images/ui/hud/round_dot_filled_p1.png");
        texRoundDotFilledP2 = loadTextureSafe("images/ui/hud/round_dot_filled_p2.png");
        texTimerFrame = loadTextureSafe("images/ui/hud/timer_frame.png");
        texComboBg = loadTextureSafe("images/ui/hud/combo_bg.png");
        texPauseOverlay = loadTextureSafe("images/ui/hud/pause_overlay.png");

        // --- UI Screens ---
        texTitleLogo = loadTextureSafe("images/ui/screens/title_logo.png");
        texTitleLogoGlow = loadTextureSafe("images/ui/screens/title_logo_glow.png");
        texTitleLogoLarge = loadTextureSafe("images/ui/screens/title_logo_large.png");
        texTitleLogoIcon = loadTextureSafe("images/ui/screens/title_logo_icon.png");
        texMenuCharPreview = loadTextureSafe("images/ui/screens/menu_character_preview.png");
        texCharSelectFrame = loadTextureSafe("images/ui/screens/char_select_frame.png");
        texCharSelectFrameSelected = loadTextureSafe("images/ui/screens/char_select_frame_selected.png");
        texCharSelectFrameLocked = loadTextureSafe("images/ui/screens/char_select_frame_locked.png");
        texVsScreenPanel = loadTextureSafe("images/ui/screens/vs_screen_panel.png");
        texVsScreenDivider = loadTextureSafe("images/ui/screens/vs_screen_divider.png");
        texRoundBanner = loadTextureSafe("images/ui/screens/round_banner.png");
        texFightBanner = loadTextureSafe("images/ui/screens/fight_banner.png");
        texKoBanner = loadTextureSafe("images/ui/screens/ko_banner.png");
        texTimeOverBanner = loadTextureSafe("images/ui/screens/time_over_banner.png");
        texWinnerBanner = loadTextureSafe("images/ui/screens/winner_banner.png");
        texPanelSettings = loadTextureSafe("images/ui/screens/panel_settings.png");
        texPanelHowToPlay = loadTextureSafe("images/ui/screens/panel_howtoplay.png");
        texPanelGallery = loadTextureSafe("images/ui/screens/panel_gallery.png");
        texPanelGalleryDetail = loadTextureSafe("images/ui/screens/panel_gallery_detail.png");
        texScrollIndicator = loadTextureSafe("images/ui/screens/scroll_indicator.png");
        texCheckboxEmpty = loadTextureSafe("images/ui/screens/checkbox_empty.png");
        texCheckboxChecked = loadTextureSafe("images/ui/screens/checkbox_checked.png");
        texSliderTrack = loadTextureSafe("images/ui/screens/slider_track.png");
        texSliderKnob = loadTextureSafe("images/ui/screens/slider_knob.png");
        texLoadingBarEmpty = loadTextureSafe("images/ui/screens/loading_bar_empty.png");
        texLoadingBarFill = loadTextureSafe("images/ui/screens/loading_bar_fill.png");
        texAttractModeDim = loadTextureSafe("images/ui/screens/attract_mode_dim.png");
    }

    public void updateFontScales() {
        float h = Gdx.graphics.getHeight();
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

    public Sound loadSoundSafe(String path) {
        try {
            if (Gdx.files.internal(path).exists()) {
                return Gdx.audio.newSound(Gdx.files.internal(path));
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    public Music loadMusicSafe(String path) {
        try {
            if (Gdx.files.internal(path).exists()) {
                return Gdx.audio.newMusic(Gdx.files.internal(path));
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    public Texture loadTextureSafe(String path) {
        try {
            if (Gdx.files.internal(path).exists()) {
                return new Texture(Gdx.files.internal(path));
            }
            if (path.contains("images/characters/")) {
                String alt = path.replace("images/characters/", "images/");
                if (Gdx.files.internal(alt).exists()) {
                    return new Texture(Gdx.files.internal(alt));
                }
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    public void playSound(Sound sound, float volume) {
        if (sound != null && settingsScreen != null) {
            float master = settingsScreen.sfxVolume;
            if (master > 0.01f) {
                sound.play(volume * master);
            }
        } else if (sound != null) {
            sound.play(volume);
        }
    }

    public void playMusic(Music music, boolean loop) {
        if (currentMusic == music && music != null && music.isPlaying()) {
            return;
        }
        stopMusic();
        currentMusic = music;
        if (currentMusic != null) {
            float master = (settingsScreen != null) ? settingsScreen.sfxVolume : 1.0f;
            currentMusic.setLooping(loop);
            currentMusic.setVolume(master * 0.75f);
            currentMusic.play();
        }
    }

    public void stopMusic() {
        if (currentMusic != null) {
            try {
                if (currentMusic.isPlaying()) currentMusic.stop();
            } catch (Exception ignored) {}
            currentMusic = null;
        }
    }

    @Override
    public void render() {
        super.render();
    }

    @Override
    public void dispose() {
        stopMusic();
        batch.dispose();
        shapeRenderer.dispose();
        fontTitle.dispose();
        fontBold.dispose();
        font.dispose();
        fontSmall.dispose();

        // Dispose Sounds
        disposeSound(sndMenuNav);
        disposeSound(sndMenuSelect);
        disposeSound(sndMenuBack);
        disposeSound(sndRoundStart);
        disposeSound(sndAnnouncerFight);
        disposeSound(sndAnnouncerKo);
        disposeSound(sndAnnouncerWinner);
        disposeSound(sndGalleryUnlock);
        disposeSound(sndLowHpWarning);
        disposeSound(sndTimeOver);
        disposeSound(sndHitPunch);
        disposeSound(sndHitHeavy);
        disposeSound(sndBlockShield);
        disposeSound(sndSpecialCharge);
        disposeSound(sndSpecialFire);
        disposeSound(sndSpecialHit);
        disposeSound(sndDashWhoosh);
        disposeSound(sndDashAttackImpact);
        disposeSound(sndDamageByte);
        disposeSound(sndDamageWannacry);
        disposeSound(sndVictoryFanfare);
        disposeSound(sndDefeatJingle);
        disposeSound(sndGlitchTransition);

        // Dispose Music
        disposeMusic(musicMenu);
        disposeMusic(musicFight);
        disposeMusic(musicWin);

        // Dispose Backgrounds
        disposeTexture(texBgMenu);
        disposeTexture(texLayerSky);
        disposeTexture(texLayerFar);
        disposeTexture(texLayerMid);
        disposeTexture(texLayerFloor);
        disposeTexture(texLayerGlow);
        disposeTexture(texRun1);
        disposeTexture(texRun2);
        disposeTexture(texRun3);
        disposeTexture(texRun4);
        disposeTexture(texBgFight);
        disposeTexture(texBgCharSelect);
        disposeTexture(texBgVersus);
        disposeTexture(texBgGallery);
        disposeTexture(texBgWin);
        disposeTexture(texBgLose);
        disposeTexture(texBgLoading);

        // Dispose Character Byte
        disposeTexture(texByteIdle1);
        disposeTexture(texByteIdle2);
        disposeTexture(texByteWalk1);
        disposeTexture(texByteWalk2);
        disposeTexture(texByteAttack1);
        disposeTexture(texByteAttack2);
        disposeTexture(texByteAttack3);
        disposeTexture(texByteDash);
        disposeTexture(texByteDashAttack);
        disposeTexture(texByteBlock);
        disposeTexture(texByteSpecialCharge);
        disposeTexture(texByteSpecialFire);
        disposeTexture(texByteHit1);
        disposeTexture(texByteHit2);
        disposeTexture(texByteWin1);
        disposeTexture(texByteWin2);
        disposeTexture(texByteLose);
        disposeTexture(texBytePortrait);

        // Dispose Character WannaCry
        disposeTexture(texWannaCryIdle1);
        disposeTexture(texWannaCryIdle2);
        disposeTexture(texWannaCryWalk);
        disposeTexture(texWannaCryAttack1);
        disposeTexture(texWannaCryAttack2);
        disposeTexture(texWannaCryBlock);
        disposeTexture(texWannaCrySpecial);
        disposeTexture(texWannaCryHit1);
        disposeTexture(texWannaCryHit2);
        disposeTexture(texWannaCryLose);
        disposeTexture(texWannaCryWin);
        disposeTexture(texWannaCryPortrait);

        // Dispose Effects
        disposeTexture(texFxHitSpark);
        disposeTexture(texFxBlockSpark);
        disposeTexture(texFxDashTrail);
        disposeTexture(texFxDashDust);
        disposeTexture(texFxSpecialAura);
        disposeTexture(texFxProjectileTrail);
        disposeTexture(texFxEncryptBurst);
        disposeTexture(texFxFlashWhite);
        disposeTexture(texFxKnockbackLines);
        disposeTexture(texFxConfetti);
        disposeTexture(texGlitchNoise);

        // Dispose Projectiles
        disposeTexture(texProjBlast1);
        disposeTexture(texProjBlast2);
        disposeTexture(texProjBlastTrail);

        // Dispose UI Shared
        disposeTexture(texUiScanlines);
        disposeTexture(texUiVignette);
        disposeTexture(texUiDivider);
        disposeTexture(texUiCursor);

        // Dispose UI Buttons
        disposeTexture(texBtnNormal);
        disposeTexture(texBtnPressed);
        disposeTexture(texBtnSmall);
        disposeTexture(texBtnSmallPressed);
        disposeTexture(texBtnArrowLeft);
        disposeTexture(texBtnArrowRight);
        disposeTexture(texBtnClose);
        disposeTexture(texBtnBack);
        disposeTexture(texBtnReady);
        disposeTexture(texBtnLocked);

        // Dispose UI Controls
        disposeTexture(texDpadLeft);
        disposeTexture(texDpadLeftPressed);
        disposeTexture(texDpadRight);
        disposeTexture(texDpadRightPressed);
        disposeTexture(texBtnAttack);
        disposeTexture(texBtnAttackPressed);
        disposeTexture(texBtnSpecial);
        disposeTexture(texBtnSpecialPressed);
        disposeTexture(texBtnBlock);
        disposeTexture(texBtnBlockPressed);
        disposeTexture(texBtnPause);

        // Dispose UI HUD
        disposeTexture(texHpBarEmpty);
        disposeTexture(texHpBarFillPlayer);
        disposeTexture(texHpBarFillEnemy);
        disposeTexture(texHpBarDamage);
        disposeTexture(texSuperBarEmpty);
        disposeTexture(texSuperBarFill);
        disposeTexture(texPortraitFramePlayer);
        disposeTexture(texPortraitFrameEnemy);
        disposeTexture(texPortraitFrameP2);
        disposeTexture(texVsBadge);
        disposeTexture(texRoundDotEmpty);
        disposeTexture(texRoundDotFilledP1);
        disposeTexture(texRoundDotFilledP2);
        disposeTexture(texTimerFrame);
        disposeTexture(texComboBg);
        disposeTexture(texPauseOverlay);

        // Dispose UI Screens
        disposeTexture(texTitleLogo);
        disposeTexture(texTitleLogoGlow);
        disposeTexture(texTitleLogoLarge);
        disposeTexture(texTitleLogoIcon);
        disposeTexture(texMenuCharPreview);
        disposeTexture(texCharSelectFrame);
        disposeTexture(texCharSelectFrameSelected);
        disposeTexture(texCharSelectFrameLocked);
        disposeTexture(texVsScreenPanel);
        disposeTexture(texVsScreenDivider);
        disposeTexture(texRoundBanner);
        disposeTexture(texFightBanner);
        disposeTexture(texKoBanner);
        disposeTexture(texTimeOverBanner);
        disposeTexture(texWinnerBanner);
        disposeTexture(texPanelSettings);
        disposeTexture(texPanelHowToPlay);
        disposeTexture(texPanelGallery);
        disposeTexture(texPanelGalleryDetail);
        disposeTexture(texScrollIndicator);
        disposeTexture(texCheckboxEmpty);
        disposeTexture(texCheckboxChecked);
        disposeTexture(texSliderTrack);
        disposeTexture(texSliderKnob);
        disposeTexture(texLoadingBarEmpty);
        disposeTexture(texLoadingBarFill);
        disposeTexture(texAttractModeDim);
    }

    void disposeSound(Sound snd) {
        if (snd != null) snd.dispose();
    }

    void disposeMusic(Music m) {
        if (m != null) m.dispose();
    }

    void disposeTexture(Texture tex) {
        if (tex != null) tex.dispose();
    }
}
