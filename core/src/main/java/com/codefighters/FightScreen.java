package com.codefighters;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class FightScreen implements Screen {
    final CodeFightersGame game;
    final Random rand = new Random();

    // Mode
    GameMode mode = GameMode.ARCADE;

    // Stage & Dimensions
    float stageWidth;
    float stageHeight;
    float groundY;

    // Cameras
    OrthographicCamera worldCamera;
    OrthographicCamera hudCamera;
    float camX;
    float camZoom = 1.0f;

    // --- Best of 3 Rounds System ---
    int p1RoundsWon = 0;
    int p2RoundsWon = 0;
    int currentRound = 1;
    static final int ROUNDS_TO_WIN = 2;
    float roundTimer = 99.0f; // 99s round timer

    // Round banner / State machine
    enum MatchState {
        ROUND_INTRO,   // "ROUND 1", "FIGHT!"
        FIGHTING,      // Active combat
        ROUND_OVER,    // "K.O." / "TIME OVER"
        MATCH_OVER     // "VICTORY" / Match End
    }
    MatchState matchState = MatchState.ROUND_INTRO;
    int introSubPhase = 0; // 0 = ROUND X, 1 = FIGHT!
    float bannerTimer = 0f;
    String bannerText = "ROUND 1";
    String matchWinnerText = "";

    // In-fight Pause Menu
    boolean isPaused = false;

    // --- Player 1 (Cat / Cyan) ---
    float p1X, p1Y, p1VX;
    int p1HP = 100, p1MaxHP = 100;
    float p1DisplayHP = 100f; // for smooth health drain animation
    float p1SuperGauge = 0f;  // 0 to 100
    int p1SuperLevel = 0;     // 0 to 3
    String p1State = "IDLE";
    int p1AnimTimer = 0;
    int p1IdleFrame = 0;
    float p1Facing = 1f;
    int p1InvincibleTimer = 0;
    boolean p1Block = false;
    int p1Flash = 0;
    int p1ComboCount = 0;
    int p1ComboTimer = 0;
    Hitbox p1Hitbox = null;
    // Dash system P1
    float p1LastLeftTapTime = -10f;
    float p1LastRightTapTime = -10f;
    float p1DashCooldown = 0f;
    boolean p1IsDashing = false;
    float p1DashTimer = 0f;
    boolean p1DashForward = true;
    boolean p1LowHpAlerted = false;

    // --- Player 2 / AI (WannaCry in Arcade, Cat Orange in Versus) ---
    float p2X, p2Y, p2VX;
    int p2HP = 100, p2MaxHP = 100;
    float p2DisplayHP = 100f;
    float p2SuperGauge = 0f;
    int p2SuperLevel = 0;
    String p2State = "IDLE";
    int p2AnimTimer = 0;
    int p2IdleFrame = 0;
    float p2Facing = -1f;
    int p2InvincibleTimer = 0;
    boolean p2Block = false;
    int p2Flash = 0;
    int p2ComboCount = 0;
    int p2ComboTimer = 0;
    Hitbox p2Hitbox = null;
    // AI specific
    int p2AttackCooldown = 50;
    // Dash system P2
    float p2LastLeftTapTime = -10f;
    float p2LastRightTapTime = -10f;
    float p2DashCooldown = 0f;
    boolean p2IsDashing = false;
    float p2DashTimer = 0f;
    boolean p2DashForward = true;

    // Combat effects & lists
    List<Projectile> projectiles = new ArrayList<>();
    List<Particle> particles = new ArrayList<>();
    List<FloatingNotice> notices = new ArrayList<>();
    List<GhostTrail> ghostTrails = new ArrayList<>();

    int hitPause = 0;
    int frame = 0;
    float shakeX = 0, shakeY = 0;
    int shakeTimer = 0;

    // Fight stats
    float totalFightTime = 0f;
    int maxComboAchieved = 0;
    int totalDamageDealtP1 = 0;

    // Touch button tracking
    boolean p1LeftWasPressed = false, p1RightWasPressed = false;
    boolean p1AtkWasPressed = false, p1SplWasPressed = false;
    boolean p2LeftWasPressed = false, p2RightWasPressed = false;
    boolean p2AtkWasPressed = false, p2SplWasPressed = false;

    public FightScreen(final CodeFightersGame game) {
        this.game = game;
        worldCamera = new OrthographicCamera();
        hudCamera = new OrthographicCamera();
    }

    public void setMode(GameMode mode) {
        this.mode = mode;
        this.p1RoundsWon = 0;
        this.p2RoundsWon = 0;
        this.currentRound = 1;
        this.totalFightTime = 0f;
        this.maxComboAchieved = 0;
        this.totalDamageDealtP1 = 0;
        resetRound(true);
    }

    public void resetFight() {
        setMode(this.mode != null ? this.mode : GameMode.ARCADE);
    }

    public void resetRound(boolean isNewMatch) {
        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();

        stageWidth = w * 1.85f;
        stageHeight = h;
        groundY = h * 0.20f;

        p1X = stageWidth * 0.36f;
        p2X = stageWidth * 0.64f;
        p1Y = groundY;
        p2Y = groundY;
        p1VX = 0; p2VX = 0;

        p1HP = 100; p1DisplayHP = 100f;
        p2HP = 100; p2DisplayHP = 100f;
        if (isNewMatch) {
            p1SuperGauge = 20f;
            p2SuperGauge = 20f;
        }

        p1State = "IDLE"; p1AnimTimer = 0; p1InvincibleTimer = 0; p1Block = false; p1Flash = 0;
        p1Facing = 1f; p1ComboCount = 0; p1Hitbox = null; p1IsDashing = false; p1DashCooldown = 0f;

        p2State = "IDLE"; p2AnimTimer = 0; p2InvincibleTimer = 0; p2Block = false; p2Flash = 0;
        p2Facing = -1f; p2ComboCount = 0; p2Hitbox = null; p2IsDashing = false; p2DashCooldown = 0f;
        p2AttackCooldown = 45;

        projectiles.clear();
        particles.clear();
        notices.clear();
        ghostTrails.clear();

        roundTimer = 99.0f;
        matchState = MatchState.ROUND_INTRO;
        introSubPhase = 0;
        bannerTimer = 0f;
        bannerText = (currentRound == 1) ? "ROUND 1" : (currentRound == 2 ? "ROUND 2" : "FINAL ROUND");
        isPaused = false;
        p1LowHpAlerted = false;
        game.playSound(game.sndRoundStart, 1.0f);

        camX = (p1X + p2X) / 2.0f;
        camZoom = 1.0f;

        worldCamera.setToOrtho(false, w, h);
        worldCamera.position.set(camX, h / 2f, 0);
        worldCamera.zoom = camZoom;
        worldCamera.update();

        hudCamera.setToOrtho(false, w, h);
        hudCamera.position.set(w / 2f, h / 2f, 0);
        hudCamera.update();
    }

    @Override
    public void show() {
        game.updateFontScales();
        if (matchState == null) resetFight();
        game.playMusic(game.musicFight, true);
    }

    @Override
    public void render(float delta) {
        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();

        // Handle window resizing
        if (worldCamera.viewportWidth != w || worldCamera.viewportHeight != h) {
            stageWidth = w * 1.85f;
            stageHeight = h;
            groundY = h * 0.20f;
            worldCamera.setToOrtho(false, w, h);
            hudCamera.setToOrtho(false, w, h);
            hudCamera.position.set(w / 2f, h / 2f, 0);
            hudCamera.update();
        }

        // Toggle pause via keyboard
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE) || Gdx.input.isKeyJustPressed(Input.Keys.P)) {
            if (matchState == MatchState.FIGHTING) {
                isPaused = !isPaused;
            }
        }

        // Smooth health bar drain animation (Street Fighter delayed yellow damage gauge)
        if (p1DisplayHP > p1HP) {
            p1DisplayHP -= (p1DisplayHP - p1HP) * 0.08f + 0.15f;
            if (p1DisplayHP < p1HP) p1DisplayHP = p1HP;
        }
        if (p2DisplayHP > p2HP) {
            p2DisplayHP -= (p2DisplayHP - p2HP) * 0.08f + 0.15f;
            if (p2DisplayHP < p2HP) p2DisplayHP = p2HP;
        }

        // Super Art Gauge calculation (100 gauge per bar)
        p1SuperLevel = (int) (p1SuperGauge / 33.34f);
        p2SuperLevel = (int) (p2SuperGauge / 33.34f);

        // State Machine Handling
        if (isPaused) {
            drawFightWorld(h);
            drawFightHUD(w, h);
            drawPauseOverlay(w, h);
            return;
        }

        if (matchState == MatchState.ROUND_INTRO) {
            bannerTimer += delta;
            if (introSubPhase == 0) {
                bannerText = (currentRound == 1) ? "ROUND 1" : (currentRound == 2 ? "ROUND 2" : "FINAL ROUND");
                if (bannerTimer >= 1.25f) {
                    introSubPhase = 1;
                    bannerTimer = 0f;
                    bannerText = "FIGHT!";
                    game.playSound(game.sndAnnouncerFight, 1.0f);
                }
            } else if (introSubPhase == 1) {
                if (bannerTimer >= 0.85f) {
                    matchState = MatchState.FIGHTING;
                    bannerTimer = 0f;
                }
            }
            updateCamera(w, h);
            drawFightWorld(h);
            drawFightHUD(w, h);
            drawRoundBanner(w, h);
            return;
        }

        if (matchState == MatchState.ROUND_OVER) {
            bannerTimer += delta;
            updateCamera(w, h);
            drawFightWorld(h);
            drawFightHUD(w, h);
            drawRoundBanner(w, h);

            if (bannerTimer >= 2.2f) {
                if (p1RoundsWon >= ROUNDS_TO_WIN) {
                    matchState = MatchState.MATCH_OVER;
                    matchWinnerText = (mode == GameMode.VERSUS) ? "PLAYER 1 WINS!" : "VICTORY!";
                    bannerTimer = 0f;
                    game.playSound(game.sndAnnouncerWinner, 1.0f);
                    if (mode == GameMode.ARCADE) {
                        game.progress.markDefeated("wannacry");
                        game.winScreen.setStats(totalFightTime, maxComboAchieved, totalDamageDealtP1);
                        game.setScreen(game.winScreen);
                        return;
                    }
                } else if (p2RoundsWon >= ROUNDS_TO_WIN) {
                    matchState = MatchState.MATCH_OVER;
                    matchWinnerText = (mode == GameMode.VERSUS) ? "PLAYER 2 WINS!" : "DEFEAT!";
                    bannerTimer = 0f;
                    game.playSound(game.sndAnnouncerWinner, 1.0f);
                    if (mode == GameMode.ARCADE) {
                        game.setScreen(game.loseScreen);
                        return;
                    }
                } else {
                    currentRound++;
                    resetRound(false);
                }
            }
            return;
        }

        if (matchState == MatchState.MATCH_OVER) {
            bannerTimer += delta;
            updateCamera(w, h);
            drawFightWorld(h);
            drawFightHUD(w, h);
            drawMatchOverBanner(w, h);
            return;
        }

        // --- Active Combat Loop ---
        totalFightTime += delta;

        // Timer decrement
        roundTimer -= delta;
        if (roundTimer <= 0) {
            roundTimer = 0;
            // Time Out!
            if (p1HP > p2HP) {
                p1RoundsWon++;
                bannerText = "TIME OVER\nPLAYER 1 WINS";
            } else if (p2HP > p1HP) {
                p2RoundsWon++;
                bannerText = (mode == GameMode.VERSUS) ? "TIME OVER\nPLAYER 2 WINS" : "TIME OVER\nWANNACRY WINS";
            } else {
                bannerText = "TIME OVER\nDRAW";
            }
            matchState = MatchState.ROUND_OVER;
            bannerTimer = 0f;
            game.playSound(game.sndTimeOver, 1.0f);
            return;
        }

        // Hit pause frame freeze (gives impactful weight to hits)
        if (hitPause > 0) {
            hitPause--;
            updateCamera(w, h);
            drawFightWorld(h);
            drawFightHUD(w, h);
            return;
        }

        frame++;
        updateCombat(w, h, delta);
        updateCamera(w, h);
        drawFightWorld(h);
        drawFightHUD(w, h);
    }

    void updateCamera(float w, float h) {
        float midX = (p1X + p2X) / 2.0f;
        float dist = Math.abs(p1X - p2X);

        // Street Fighter dynamic zoom: pull in when close, pull out when retreating
        float targetZoom = 1.0f;
        if (dist < w * 0.32f) {
            targetZoom = 0.90f;
        } else if (dist > w * 0.48f) {
            targetZoom = 1.0f + ((dist - w * 0.48f) / (w * 0.9f)) * 0.28f;
            targetZoom = Math.min(1.28f, targetZoom);
        }

        camZoom += (targetZoom - camZoom) * 0.10f;
        float halfVisW = (w * camZoom) / 2.0f;
        float targetCamX = Math.max(halfVisW, Math.min(stageWidth - halfVisW, midX));
        camX += (targetCamX - camX) * 0.22f; // Responsive camera tracking
        camX = Math.max(halfVisW, Math.min(stageWidth - halfVisW, camX));

        if (shakeTimer > 0) {
            shakeTimer--;
            boolean shake = (game.settingsScreen != null) ? game.settingsScreen.screenShake : true;
            if (shake) {
                shakeX = (rand.nextFloat() - 0.5f) * shakeTimer * 1.1f;
                shakeY = (rand.nextFloat() - 0.5f) * shakeTimer * 1.1f;
            }
        } else {
            shakeX = 0; shakeY = 0;
        }

        worldCamera.position.set(camX + shakeX, h / 2f + shakeY, 0);
        worldCamera.zoom = camZoom;
        worldCamera.update();
    }

    void updateCombat(float w, float h, float delta) {
        p1Y = groundY;
        p2Y = groundY;

        p1IdleFrame++;
        p2IdleFrame++;

        if (p1Flash > 0) p1Flash--;
        if (p2Flash > 0) p2Flash--;
        if (p1InvincibleTimer > 0) p1InvincibleTimer--;
        if (p2InvincibleTimer > 0) p2InvincibleTimer--;

        if (p1DashCooldown > 0) p1DashCooldown -= delta;
        if (p2DashCooldown > 0) p2DashCooldown -= delta;

        // Auto face each other when neutral (with deadzone so crossing over doesn't cause rapid flipping)
        if (!p1IsDashing && (p1State.equals("IDLE") || p1State.equals("WALK"))) {
            if (p1X < p2X - 12f) p1Facing = 1f;
            else if (p1X > p2X + 12f) p1Facing = -1f;
        }
        if (!p2IsDashing && (p2State.equals("IDLE") || p2State.equals("WALK"))) {
            if (p2X < p1X - 12f) p2Facing = 1f;
            else if (p2X > p1X + 12f) p2Facing = -1f;
        }

        float charW = h * 0.52f;
        float hitH = h * 0.35f;

        // Process Inputs for Player 1 and Player 2
        processPlayer1Input(w, h, delta, charW, hitH);

        if (mode == GameMode.VERSUS) {
            processPlayer2VersusInput(w, h, delta, charW, hitH);
        } else {
            processEnemyAI(charW, hitH);
        }

        // Apply velocities
        p1X += p1VX;
        p2X += p2VX;

        // Stage bounds (absolute physical walls of the arena)
        float stageLeft = charW * 0.25f;
        float stageRight = stageWidth - charW * 0.25f;
        p1X = Math.max(stageLeft, Math.min(stageRight, p1X));
        p2X = Math.max(stageLeft, Math.min(stageRight, p2X));

        // Update Projectiles
        Iterator<Projectile> pit = projectiles.iterator();
        while (pit.hasNext()) {
            Projectile p = pit.next();
            p.x += p.vx;
            // Check collision with opponent
            boolean isP1Bullet = (p.vx > 0 && p1Facing > 0) || (p.vx < 0 && p1Facing < 0);
            float targetX = isP1Bullet ? p2X : p1X;
            float targetY = isP1Bullet ? p2Y : p1Y;
            boolean targetInvincible = isP1Bullet ? (p2InvincibleTimer > 0) : (p1InvincibleTimer > 0);
            boolean targetBlock = isP1Bullet ? p2Block : p1Block;

            if (p.active && Math.abs(p.x - targetX) < charW * 0.32f && p.y >= targetY && p.y <= targetY + charW * 0.75f) {
                if (!targetInvincible) {
                    int dmg = p.damage;
                    if (targetBlock) {
                        dmg = (int)(dmg * 0.3f);
                        game.playSound(game.sndBlockShield, 0.7f * game.settingsScreen.sfxVolume);
                        spawnParticles(targetX, targetY + charW * 0.4f, 0xffff00, 8);
                        addNotice("BLOCKED", targetX, targetY + charW * 0.8f, 0xffff00);
                    } else {
                        game.playSound(game.sndDamageGlitch, 0.8f * game.settingsScreen.sfxVolume);
                        spawnParticles(targetX, targetY + charW * 0.4f, 0x00e5ff, 14);
                        hitPause = 7;
                        shakeTimer = 9;
                        addNotice("PUNISH!", targetX, targetY + charW * 0.8f, 0x00e5ff);
                    }

                    if (isP1Bullet) {
                        p2HP -= dmg;
                        p2State = "HIT"; p2AnimTimer = 0; p2InvincibleTimer = 14; p2Flash = 10;
                        p1SuperGauge = Math.min(100f, p1SuperGauge + 15f);
                        totalDamageDealtP1 += dmg;
                    } else {
                        p1HP -= dmg;
                        p1State = "HIT"; p1AnimTimer = 0; p1InvincibleTimer = 14; p1Flash = 10;
                        p2SuperGauge = Math.min(100f, p2SuperGauge + 15f);
                    }
                }
                p.active = false;
            }
            if (p.x < 0 || p.x > stageWidth || !p.active) pit.remove();
        }

        // Check P1 Melee Hitbox
        if (p1Hitbox != null && p2InvincibleTimer == 0) {
            float enemyLeft = p2X - charW * 0.26f;
            float enemyRight = p2X + charW * 0.26f;
            float enemyBottom = p2Y;
            float enemyTop = p2Y + charW * 0.75f;

            if (p1Hitbox.x < enemyRight && p1Hitbox.x + p1Hitbox.w > enemyLeft &&
                p1Hitbox.y < enemyTop && p1Hitbox.y + p1Hitbox.h > enemyBottom) {
                int dmg = p1Hitbox.damage;
                if (p2Block) {
                    dmg = (int) (dmg * 0.3f);
                    game.playSound(game.sndBlockShield, 0.65f * game.settingsScreen.sfxVolume);
                    spawnParticles(p2X, p2Y + charW * 0.45f, 0xffff00, 6);
                    addNotice("BLOCKED", p2X, p2Y + charW * 0.85f, 0xffff00);
                } else {
                    game.playSound(game.sndHitPunch, 0.85f * game.settingsScreen.sfxVolume);
                    spawnParticles(p2X, p2Y + charW * 0.45f, 0x00bcd4, 12);
                    hitPause = p1State.equals("DASH_ATTACK") ? 8 : 6;
                    shakeTimer = p1State.equals("DASH_ATTACK") ? 10 : 7;
                    p1ComboCount++;
                    p1ComboTimer = 60;
                    if (p1ComboCount > maxComboAchieved) maxComboAchieved = p1ComboCount;
                    if (p1ComboCount > 1) {
                        addNotice(p1ComboCount + " HITS COMBO!", p2X, p2Y + charW * 0.85f, 0xffd54f);
                    } else if (p1State.equals("DASH_ATTACK")) {
                        addNotice("DASH ATTACK!", p2X, p2Y + charW * 0.85f, 0x00e5ff);
                    }
                }
                p2HP -= dmg;
                totalDamageDealtP1 += dmg;
                p1SuperGauge = Math.min(100f, p1SuperGauge + 8f);
                p2State = "HIT"; p2AnimTimer = 0; p2InvincibleTimer = p1Hitbox.hitstun; p2Flash = 8;
                p1Hitbox = null;
            }
        }

        // Check P2 Melee Hitbox
        if (p2Hitbox != null && p1InvincibleTimer == 0) {
            float playerLeft = p1X - charW * 0.26f;
            float playerRight = p1X + charW * 0.26f;
            float playerBottom = p1Y;
            float playerTop = p1Y + charW * 0.75f;

            if (p2Hitbox.x < playerRight && p2Hitbox.x + p2Hitbox.w > playerLeft &&
                p2Hitbox.y < playerTop && p2Hitbox.y + p2Hitbox.h > playerBottom) {
                int dmg = p2Hitbox.damage;
                if (p1Block) {
                    dmg = (int) (dmg * 0.3f);
                    game.playSound(game.sndBlockShield, 0.65f * game.settingsScreen.sfxVolume);
                    spawnParticles(p1X, p1Y + charW * 0.45f, 0xffff00, 6);
                    addNotice("BLOCKED", p1X, p1Y + charW * 0.85f, 0xffff00);
                } else {
                    game.playSound(game.sndHitPunch, 0.85f * game.settingsScreen.sfxVolume);
                    spawnParticles(p1X, p1Y + charW * 0.45f, 0xff1744, 12);
                    hitPause = p2State.equals("DASH_ATTACK") ? 8 : 6;
                    shakeTimer = 8;
                    p2ComboCount++;
                    p2ComboTimer = 60;
                    if (p2ComboCount > 1) {
                        addNotice(p2ComboCount + " HITS COMBO!", p1X, p1Y + charW * 0.85f, 0xff5252);
                    }
                }
                p1HP -= dmg;
                p2SuperGauge = Math.min(100f, p2SuperGauge + 8f);
                p1State = "HIT"; p1AnimTimer = 0; p1InvincibleTimer = p2Hitbox.hitstun; p1Flash = 8;
                p2Hitbox = null;
            }
        }

        // Particles & Ghost Trails
        Iterator<Particle> partIt = particles.iterator();
        while (partIt.hasNext()) {
            Particle p = partIt.next();
            p.x += p.vx; p.y += p.vy; p.vy += 0.15f; p.life--;
            if (p.life <= 0) partIt.remove();
        }

        Iterator<GhostTrail> git = ghostTrails.iterator();
        while (git.hasNext()) {
            GhostTrail gt = git.next();
            gt.life--;
            if (gt.life <= 0) git.remove();
        }

        Iterator<FloatingNotice> nit = notices.iterator();
        while (nit.hasNext()) {
            FloatingNotice fn = nit.next();
            fn.y += 1.2f;
            fn.life--;
            if (fn.life <= 0) nit.remove();
        }

        // Check Round Over condition
        if (p2HP <= 0 || p1HP <= 0) {
            if (p2HP <= 0 && p1HP <= 0) {
                bannerText = "DOUBLE K.O.";
            } else if (p2HP <= 0) {
                p2HP = 0;
                p1RoundsWon++;
                bannerText = "K.O.\nPLAYER 1 WINS ROUND";
            } else {
                p1HP = 0;
                p2RoundsWon++;
                bannerText = (mode == GameMode.VERSUS) ? "K.O.\nPLAYER 2 WINS ROUND" : "K.O.\nWANNACRY WINS ROUND";
            }
            matchState = MatchState.ROUND_OVER;
            bannerTimer = 0f;
            game.playSound(game.sndAnnouncerKo, 1.0f);
            if (mode == GameMode.ARCADE && p2HP <= 0) {
                game.playSound(game.sndDamageWannacry, 1.0f);
            } else {
                game.playSound(game.sndDamageByte, 1.0f);
            }
        }
    }

    void processPlayer1Input(float w, float h, float delta, float charW, float hitH) {
        // Touch bounds for Player 1
        float btnSize = h * 0.135f;
        float padY = h * 0.045f;
        float p1LeftX = w * 0.04f;
        float p1RightX = w * 0.17f;

        // In Versus mode, P1 attack buttons are placed on left half of screen
        float p1AtkX = (mode == GameMode.VERSUS) ? w * 0.31f : w * 0.65f;
        float p1SplX = (mode == GameMode.VERSUS) ? w * 0.39f : w * 0.77f;
        float p1SplY = (mode == GameMode.VERSUS) ? padY + btnSize * 0.7f : padY + btnSize * 0.45f;
        float p1BlkX = (mode == GameMode.VERSUS) ? w * 0.42f : w * 0.88f;

        boolean touchLeft = false, touchRight = false;
        boolean touchAtk = false, touchSpl = false, touchBlk = false;

        // Multi-touch scan
        for (int i = 0; i < 6; i++) {
            if (Gdx.input.isTouched(i)) {
                float tx = Gdx.input.getX(i);
                float ty = Gdx.graphics.getHeight() - Gdx.input.getY(i);

                if (mode == GameMode.VERSUS && tx > w * 0.5f) continue; // P1 restricted to left half in Versus

                if (tx >= p1LeftX && tx <= p1LeftX + btnSize && ty >= padY && ty <= padY + btnSize) touchLeft = true;
                if (tx >= p1RightX && tx <= p1RightX + btnSize && ty >= padY && ty <= padY + btnSize) touchRight = true;
                if (tx >= p1AtkX && tx <= p1AtkX + btnSize && ty >= padY && ty <= padY + btnSize) touchAtk = true;
                if (tx >= p1SplX && tx <= p1SplX + btnSize && ty >= p1SplY && ty <= p1SplY + btnSize) touchSpl = true;
                if (tx >= p1BlkX && tx <= p1BlkX + btnSize && ty >= padY && ty <= padY + btnSize) touchBlk = true;
            }
        }

        boolean leftKey = Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT);
        boolean rightKey = Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT);
        if (mode == GameMode.VERSUS) {
            // In versus, P1 only uses A / D
            leftKey = Gdx.input.isKeyPressed(Input.Keys.A);
            rightKey = Gdx.input.isKeyPressed(Input.Keys.D);
        }

        boolean left = leftKey || touchLeft;
        boolean right = rightKey || touchRight;

        boolean p1LeftJustPressed = (mode == GameMode.VERSUS)
                ? (Gdx.input.isKeyJustPressed(Input.Keys.A) || (touchLeft && !p1LeftWasPressed))
                : (Gdx.input.isKeyJustPressed(Input.Keys.A) || Gdx.input.isKeyJustPressed(Input.Keys.LEFT) || (touchLeft && !p1LeftWasPressed));

        boolean p1RightJustPressed = (mode == GameMode.VERSUS)
                ? (Gdx.input.isKeyJustPressed(Input.Keys.D) || (touchRight && !p1RightWasPressed))
                : (Gdx.input.isKeyJustPressed(Input.Keys.D) || Gdx.input.isKeyJustPressed(Input.Keys.RIGHT) || (touchRight && !p1RightWasPressed));

        // Double-Tap Dash Detection (Left moves Left, Right moves Right regardless of facing)
        float now = totalFightTime;
        if (p1LeftJustPressed) {
            if (now - p1LastLeftTapTime < 0.32f && p1DashCooldown <= 0f && !p1IsDashing) {
                triggerP1Dash(p1Facing < 0);
            }
            p1LastLeftTapTime = now;
        }
        if (p1RightJustPressed) {
            if (now - p1LastRightTapTime < 0.32f && p1DashCooldown <= 0f && !p1IsDashing) {
                triggerP1Dash(p1Facing > 0);
            }
            p1LastRightTapTime = now;
        }

        p1LeftWasPressed = touchLeft;
        p1RightWasPressed = touchRight;

        boolean attackKey = (mode == GameMode.VERSUS) ? Gdx.input.isKeyJustPressed(Input.Keys.F) || Gdx.input.isKeyJustPressed(Input.Keys.J) : Gdx.input.isKeyJustPressed(Input.Keys.J);
        boolean specialKey = (mode == GameMode.VERSUS) ? Gdx.input.isKeyJustPressed(Input.Keys.G) || Gdx.input.isKeyJustPressed(Input.Keys.K) : Gdx.input.isKeyJustPressed(Input.Keys.K);
        boolean blockKey = (mode == GameMode.VERSUS) ? Gdx.input.isKeyPressed(Input.Keys.H) || Gdx.input.isKeyPressed(Input.Keys.L) : Gdx.input.isKeyPressed(Input.Keys.L);

        boolean attack = attackKey || (touchAtk && !p1AtkWasPressed);
        boolean special = specialKey || (touchSpl && !p1SplWasPressed);
        boolean block = blockKey || touchBlk;

        p1AtkWasPressed = touchAtk;
        p1SplWasPressed = touchSpl;

        p1Block = block && !p1State.startsWith("ATTACK") && !p1State.equals("SPECIAL") && !p1State.equals("HIT") && !p1IsDashing;

        // Handle Active Dash
        if (p1IsDashing) {
            p1DashTimer -= delta;
            // Ghost trail afterimages
            if (frame % 3 == 0) {
                ghostTrails.add(new GhostTrail(p1X, p1Y, charW, charW, p1Facing, 0x00bcd4, 12));
            }

            // Attack-Cancel during forward dash -> Dash Attack!
            if (p1DashForward && attack) {
                p1IsDashing = false;
                p1State = "DASH_ATTACK";
                p1AnimTimer = 0;
                float hitX = (p1Facing > 0) ? p1X + charW * 0.2f : p1X - charW * 0.55f;
                p1Hitbox = new Hitbox(hitX, p1Y, charW * 0.55f, hitH, 18, 22); // 1.5x damage!
                p1VX = p1Facing * 7.5f;
                game.playSound(game.sndHitPunch, 0.9f * game.settingsScreen.sfxVolume);
                return;
            }

            if (p1DashTimer <= 0) {
                p1IsDashing = false;
                p1VX = 0;
                p1State = "IDLE";
            }
            return;
        }

        // Movement & Normal States
        if (!p1Block && (p1State.equals("IDLE") || p1State.equals("WALK"))) {
            if (left) {
                p1VX = -5f;
                p1State = "WALK";
                if (p1IdleFrame % 14 == 0) spawnParticles(p1X, p1Y + 4, 0x00bcd4, 2);
            } else if (right) {
                p1VX = 5f;
                p1State = "WALK";
                if (p1IdleFrame % 14 == 0) spawnParticles(p1X, p1Y + 4, 0x00bcd4, 2);
            } else {
                p1VX = 0;
                p1State = "IDLE";
            }
        }

        // Attack animations & Hitboxes
        if (p1State.startsWith("ATTACK")) {
            p1AnimTimer++;
            if (p1AnimTimer <= 5) {
                p1VX = p1Facing * 2.5f;
            } else {
                p1VX = 0;
            }

            if (p1AnimTimer == 5) {
                float hitX = p1Facing > 0 ? p1X + charW * 0.2f : p1X - charW * 0.5f;
                int dmg = p1State.equals("ATTACK2") ? 10 : (p1State.equals("ATTACK3") ? 14 : 7);
                p1Hitbox = new Hitbox(hitX, p1Y, charW * 0.45f, hitH, dmg, 16);
            }
            if (p1AnimTimer == 13) p1Hitbox = null;
            if (p1AnimTimer > 19) { p1State = "IDLE"; p1AnimTimer = 0; }
        } else if (p1State.equals("DASH_ATTACK")) {
            p1AnimTimer++;
            p1VX *= 0.88f;
            if (p1AnimTimer > 18) { p1Hitbox = null; p1State = "IDLE"; p1AnimTimer = 0; }
        } else if (p1State.equals("SPECIAL")) {
            p1AnimTimer++;
            p1VX = 0;
            if (p1AnimTimer == 10) {
                float projX = p1X + p1Facing * (charW * 0.35f);
                float projY = p1Y + charW * 0.35f;
                projectiles.add(new Projectile(projX, projY, p1Facing * 11f, 18));
                game.playSound(game.sndSpecialFire, 0.8f * game.settingsScreen.sfxVolume);
                p1VX = -p1Facing * 4f;
            }
            if (p1AnimTimer > 28) { p1State = "IDLE"; p1AnimTimer = 0; }
        } else if (p1State.equals("HIT")) {
            p1AnimTimer++;
            p1VX *= 0.85f;
            if (p1AnimTimer > 16) { p1State = "IDLE"; p1AnimTimer = 0; }
        } else {
            // Trigger attacks from neutral
            if (attack) {
                if (p1ComboCount == 0 || p1ComboTimer <= 0) {
                    p1State = "ATTACK1"; p1AnimTimer = 0; p1ComboCount = 1; p1ComboTimer = 40;
                } else if (p1ComboCount == 1) {
                    p1State = "ATTACK2"; p1AnimTimer = 0; p1ComboCount = 2; p1ComboTimer = 40;
                } else {
                    p1State = "ATTACK3"; p1AnimTimer = 0; p1ComboCount = 3; p1ComboTimer = 40;
                }
            } else if (special && p1SuperGauge >= 25f) {
                p1SuperGauge -= 25f;
                p1State = "SPECIAL";
                p1AnimTimer = 0;
            }
        }
    }

    void triggerP1Dash(boolean forward) {
        p1IsDashing = true;
        p1DashForward = forward;
        p1DashCooldown = 0.5f;
        if (forward) {
            p1DashTimer = 0.25f;
            p1VX = p1Facing * 9.5f;
        } else {
            // Backdash has 3-4 i-frames
            p1DashTimer = 0.20f;
            p1VX = -p1Facing * 8.0f;
            p1InvincibleTimer = 10;
        }
        spawnParticles(p1X, p1Y + 10, 0x00bcd4, 6);
    }

    void processPlayer2VersusInput(float w, float h, float delta, float charW, float hitH) {
        float btnSize = h * 0.135f;
        float padY = h * 0.045f;

        // Player 2 controls on right half of screen (orange)
        float p2LeftX = w * 0.54f;
        float p2RightX = w * 0.67f;
        float p2AtkX = w * 0.80f;
        float p2SplX = w * 0.88f;
        float p2SplY = padY + btnSize * 0.7f;
        float p2BlkX = w * 0.91f;

        boolean touchLeft = false, touchRight = false;
        boolean touchAtk = false, touchSpl = false, touchBlk = false;

        for (int i = 0; i < 6; i++) {
            if (Gdx.input.isTouched(i)) {
                float tx = Gdx.input.getX(i);
                float ty = Gdx.graphics.getHeight() - Gdx.input.getY(i);
                if (tx < w * 0.5f) continue; // P2 restricted to right half

                if (tx >= p2LeftX && tx <= p2LeftX + btnSize && ty >= padY && ty <= padY + btnSize) touchLeft = true;
                if (tx >= p2RightX && tx <= p2RightX + btnSize && ty >= padY && ty <= padY + btnSize) touchRight = true;
                if (tx >= p2AtkX && tx <= p2AtkX + btnSize && ty >= padY && ty <= padY + btnSize) touchAtk = true;
                if (tx >= p2SplX && tx <= p2SplX + btnSize && ty >= p2SplY && ty <= p2SplY + btnSize) touchSpl = true;
                if (tx >= p2BlkX && tx <= p2BlkX + btnSize && ty >= padY && ty <= padY + btnSize) touchBlk = true;
            }
        }

        boolean left = Gdx.input.isKeyPressed(Input.Keys.LEFT) || touchLeft;
        boolean right = Gdx.input.isKeyPressed(Input.Keys.RIGHT) || touchRight;

        boolean p2LeftJustPressed = Gdx.input.isKeyJustPressed(Input.Keys.LEFT) || (touchLeft && !p2LeftWasPressed);
        boolean p2RightJustPressed = Gdx.input.isKeyJustPressed(Input.Keys.RIGHT) || (touchRight && !p2RightWasPressed);

        // P2 Double Tap Dash
        float now = totalFightTime;
        if (p2LeftJustPressed) {
            if (now - p2LastLeftTapTime < 0.32f && p2DashCooldown <= 0f && !p2IsDashing) {
                triggerP2Dash(p2Facing < 0);
            }
            p2LastLeftTapTime = now;
        }
        if (p2RightJustPressed) {
            if (now - p2LastRightTapTime < 0.32f && p2DashCooldown <= 0f && !p2IsDashing) {
                triggerP2Dash(p2Facing > 0);
            }
            p2LastRightTapTime = now;
        }

        p2LeftWasPressed = touchLeft;
        p2RightWasPressed = touchRight;

        boolean attackKey = Gdx.input.isKeyJustPressed(Input.Keys.NUMPAD_1) || Gdx.input.isKeyJustPressed(Input.Keys.NUM_1);
        boolean specialKey = Gdx.input.isKeyJustPressed(Input.Keys.NUMPAD_2) || Gdx.input.isKeyJustPressed(Input.Keys.NUM_2);
        boolean blockKey = Gdx.input.isKeyPressed(Input.Keys.NUMPAD_3) || Gdx.input.isKeyPressed(Input.Keys.NUM_3);

        boolean attack = attackKey || (touchAtk && !p2AtkWasPressed);
        boolean special = specialKey || (touchSpl && !p2SplWasPressed);
        boolean block = blockKey || touchBlk;

        p2AtkWasPressed = touchAtk;
        p2SplWasPressed = touchSpl;

        p2Block = block && !p2State.startsWith("ATTACK") && !p2State.equals("SPECIAL") && !p2State.equals("HIT") && !p2IsDashing;

        if (p2IsDashing) {
            p2DashTimer -= delta;
            if (frame % 3 == 0) {
                ghostTrails.add(new GhostTrail(p2X, p2Y, charW, charW, p2Facing, 0xff9800, 12));
            }
            if (p2DashForward && attack) {
                p2IsDashing = false;
                p2State = "DASH_ATTACK";
                p2AnimTimer = 0;
                float hitX = (p2Facing > 0) ? p2X + charW * 0.2f : p2X - charW * 0.55f;
                p2Hitbox = new Hitbox(hitX, p2Y, charW * 0.55f, hitH, 18, 22);
                p2VX = p2Facing * 7.5f;
                game.playSound(game.sndHitPunch, 0.9f * game.settingsScreen.sfxVolume);
                return;
            }
            if (p2DashTimer <= 0) {
                p2IsDashing = false;
                p2VX = 0;
                p2State = "IDLE";
            }
            return;
        }

        if (!p2Block && (p2State.equals("IDLE") || p2State.equals("WALK"))) {
            if (left) {
                p2VX = -5f;
                p2State = "WALK";
                if (p2IdleFrame % 14 == 0) spawnParticles(p2X, p2Y + 4, 0xff9800, 2);
            } else if (right) {
                p2VX = 5f;
                p2State = "WALK";
                if (p2IdleFrame % 14 == 0) spawnParticles(p2X, p2Y + 4, 0xff9800, 2);
            } else {
                p2VX = 0;
                p2State = "IDLE";
            }
        }

        if (p2State.startsWith("ATTACK")) {
            p2AnimTimer++;
            if (p2AnimTimer <= 5) {
                p2VX = p2Facing * 2.5f;
            } else {
                p2VX = 0;
            }

            if (p2AnimTimer == 5) {
                float hitX = p2Facing > 0 ? p2X + charW * 0.2f : p2X - charW * 0.5f;
                int dmg = p2State.equals("ATTACK2") ? 10 : (p2State.equals("ATTACK3") ? 14 : 7);
                p2Hitbox = new Hitbox(hitX, p2Y, charW * 0.45f, hitH, dmg, 16);
            }
            if (p2AnimTimer == 13) p2Hitbox = null;
            if (p2AnimTimer > 19) { p2State = "IDLE"; p2AnimTimer = 0; }
        } else if (p2State.equals("DASH_ATTACK")) {
            p2AnimTimer++;
            p2VX *= 0.88f;
            if (p2AnimTimer > 18) { p2Hitbox = null; p2State = "IDLE"; p2AnimTimer = 0; }
        } else if (p2State.equals("SPECIAL")) {
            p2AnimTimer++;
            p2VX = 0;
            if (p2AnimTimer == 10) {
                float projX = p2X + p2Facing * (charW * 0.35f);
                float projY = p2Y + charW * 0.35f;
                projectiles.add(new Projectile(projX, projY, p2Facing * 11f, 18));
                game.playSound(game.sndSpecialFire, 0.8f * game.settingsScreen.sfxVolume);
                p2VX = -p2Facing * 4f;
            }
            if (p2AnimTimer > 28) { p2State = "IDLE"; p2AnimTimer = 0; }
        } else if (p2State.equals("HIT")) {
            p2AnimTimer++;
            p2VX *= 0.85f;
            if (p2AnimTimer > 16) { p2State = "IDLE"; p2AnimTimer = 0; }
        } else {
            if (attack) {
                if (p2ComboCount == 0 || p2ComboTimer <= 0) {
                    p2State = "ATTACK1"; p2AnimTimer = 0; p2ComboCount = 1; p2ComboTimer = 40;
                } else if (p2ComboCount == 1) {
                    p2State = "ATTACK2"; p2AnimTimer = 0; p2ComboCount = 2; p2ComboTimer = 40;
                } else {
                    p2State = "ATTACK3"; p2AnimTimer = 0; p2ComboCount = 3; p2ComboTimer = 40;
                }
            } else if (special && p2SuperGauge >= 25f) {
                p2SuperGauge -= 25f;
                p2State = "SPECIAL";
                p2AnimTimer = 0;
            }
        }
    }

    void triggerP2Dash(boolean forward) {
        p2IsDashing = true;
        p2DashForward = forward;
        p2DashCooldown = 0.5f;
        if (forward) {
            p2DashTimer = 0.25f;
            p2VX = p2Facing * 9.5f;
        } else {
            p2DashTimer = 0.20f;
            p2VX = -p2Facing * 8.0f;
            p2InvincibleTimer = 10;
        }
        spawnParticles(p2X, p2Y + 10, 0xff9800, 6);
    }

    void processEnemyAI(float charW, float hitH) {
        p2AttackCooldown--;
        float distToPlayer = p1X - p2X;
        float absDist = Math.abs(distToPlayer);
        if (distToPlayer > 12f) p2Facing = 1f;
        else if (distToPlayer < -12f) p2Facing = -1f;

        // AI Difficulty adjustments
        float aiSpeed;
        int attackRate;
        String diff = (game.settingsScreen != null && game.settingsScreen.difficulty != null) ? game.settingsScreen.difficulty : "NORMAL";
        switch (diff) {
            case "EASY":
                aiSpeed = 1.8f;
                attackRate = 70;
                break;
            case "HARD":
                aiSpeed = 3.2f;
                attackRate = 35;
                break;
            default:
                aiSpeed = 2.4f;
                attackRate = 50;
                break;
        }

        if (p2State.equals("IDLE") || p2State.equals("WALK")) {
            if (absDist > charW * 0.36f) {
                p2VX = p2Facing * aiSpeed;
                p2State = "WALK";
                if (p2IdleFrame % 14 == 0) spawnParticles(p2X, p2Y + 4, 0xff1744, 2);
            } else {
                p2VX = 0;
                p2State = "IDLE";
                if (p2AttackCooldown <= 0) {
                    p2State = "ATTACK";
                    p2AnimTimer = 0;
                    p2AttackCooldown = attackRate + rand.nextInt(25);
                }
            }
        } else if (p2State.equals("ATTACK")) {
            p2AnimTimer++;
            if (p2AnimTimer >= 10 && p2AnimTimer <= 16) {
                p2VX = p2Facing * 3.2f;
            } else {
                p2VX = 0;
            }

            if (p2AnimTimer == 15) {
                float hitX = p2Facing > 0 ? p2X + charW * 0.2f : p2X - charW * 0.5f;
                p2Hitbox = new Hitbox(hitX, p2Y, charW * 0.45f, hitH, 10, 20);
                float masterSfx = (game.settingsScreen != null) ? game.settingsScreen.sfxVolume : 1.0f;
                game.playSound(game.sndHitPunch, 0.75f * masterSfx);
            }
            if (p2AnimTimer == 28) p2Hitbox = null;
            if (p2AnimTimer > 38) { p2State = "IDLE"; p2AnimTimer = 0; }
        } else if (p2State.equals("HIT")) {
            p2AnimTimer++;
            p2VX *= 0.85f;
            if (p2AnimTimer > 15) { p2State = "IDLE"; p2AnimTimer = 0; }
        }
    }

    void drawFightWorld(float h) {
        Gdx.gl.glClearColor(0.02f, 0.02f, 0.05f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.batch.setProjectionMatrix(worldCamera.combined);
        game.shapeRenderer.setProjectionMatrix(worldCamera.combined);

        // Background
        game.batch.begin();
        if (game.texBgFight != null) {
            float bgW = stageWidth * 1.15f;
            float bgX = -stageWidth * 0.075f;
            game.batch.draw(game.texBgFight, bgX, 0, bgW, h);
        }
        game.batch.end();

        // Street Fighter Cyberpunk Floor
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        game.shapeRenderer.setColor(0.04f, 0.05f, 0.09f, 0.95f);
        game.shapeRenderer.rect(0, 0, stageWidth, groundY);
        // Stage glow horizon line
        game.shapeRenderer.setColor(0.0f, 0.75f, 1.0f, 0.85f);
        game.shapeRenderer.rect(0, groundY - 3, stageWidth, 4);
        game.shapeRenderer.end();

        // Floor perspective grid
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        game.shapeRenderer.setColor(0.0f, 0.45f, 0.75f, 0.4f);
        for (float gy = groundY * 0.25f; gy < groundY; gy += groundY * 0.25f) {
            game.shapeRenderer.line(0, gy, stageWidth, gy);
        }
        for (float gx = 0; gx < stageWidth; gx += 80) {
            game.shapeRenderer.line(gx, groundY, gx - 40, 0);
        }
        game.shapeRenderer.end();

        // Ground contact shadows under fighters
        float charH = h * 0.52f;
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        game.shapeRenderer.setColor(0, 0, 0, 0.55f);
        game.shapeRenderer.ellipse(p1X - charH * 0.22f, groundY - h * 0.015f, charH * 0.44f, h * 0.035f);
        game.shapeRenderer.ellipse(p2X - charH * 0.22f, groundY - h * 0.015f, charH * 0.44f, h * 0.035f);
        game.shapeRenderer.end();

        // Draw Ghost Trails (Dash afterimages)
        game.batch.begin();
        for (GhostTrail gt : ghostTrails) {
            float alpha = (gt.life / 12f) * 0.4f;
            float r = ((gt.color >> 16) & 0xFF) / 255f;
            float g = ((gt.color >> 8) & 0xFF) / 255f;
            float b = (gt.color & 0xFF) / 255f;
            game.batch.setColor(r, g, b, alpha);
            if (game.texPlayerIdle != null) {
                float gx = gt.x - gt.w / 2f;
                if (gt.facing < 0) {
                    game.batch.draw(game.texPlayerIdle, gx, gt.y, gt.w, gt.h,
                        0, 0, game.texPlayerIdle.getWidth(), game.texPlayerIdle.getHeight(), true, false);
                } else {
                    game.batch.draw(game.texPlayerIdle, gx, gt.y, gt.w, gt.h);
                }
            }
        }
        game.batch.setColor(1, 1, 1, 1);
        game.batch.end();

        // Draw Fighters
        drawFighterSprite(p1X, p1Y, charH, charH, true);
        drawFighterSprite(p2X, p2Y, charH, charH, false);

        // Projectiles
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (Projectile p : projectiles) {
            game.shapeRenderer.setColor(0.31f, 0.76f, 0.97f, 1);
            game.shapeRenderer.circle(p.x, p.y, h * 0.024f, 16);
            game.shapeRenderer.setColor(1f, 1f, 1f, 0.9f);
            game.shapeRenderer.circle(p.x, p.y, h * 0.012f, 12);
        }
        game.shapeRenderer.end();

        // Particles
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (Particle p : particles) {
            float alpha = p.life / (float) p.maxLife;
            float r = ((p.color >> 16) & 0xFF) / 255f;
            float g = ((p.color >> 8) & 0xFF) / 255f;
            float b = (p.color & 0xFF) / 255f;
            game.shapeRenderer.setColor(r, g, b, alpha);
            game.shapeRenderer.rect(p.x - p.size / 2.0f, p.y - p.size / 2.0f, (float) p.size, (float) p.size);
        }
        game.shapeRenderer.end();

        // Floating Notices
        game.batch.begin();
        for (FloatingNotice fn : notices) {
            float alpha = Math.min(1.0f, fn.life / 20f);
            float r = ((fn.color >> 16) & 0xFF) / 255f;
            float g = ((fn.color >> 8) & 0xFF) / 255f;
            float b = (fn.color & 0xFF) / 255f;
            game.fontBold.setColor(r, g, b, alpha);
            game.drawTextCentered(game.fontBold, fn.text, fn.x, fn.y);
        }
        game.batch.end();
    }

    void drawFighterSprite(float x, float y, float w, float h, boolean isP1) {
        Texture currentTex = null;
        String state = isP1 ? p1State : p2State;
        int animTimer = isP1 ? p1AnimTimer : p2AnimTimer;
        int idleF = isP1 ? p1IdleFrame : p2IdleFrame;
        float facing = isP1 ? p1Facing : p2Facing;
        int flash = isP1 ? p1Flash : p2Flash;
        boolean block = isP1 ? p1Block : p2Block;

        float drawW = w;
        float drawH = h;
        float drawY = y;

        boolean isByte = isP1 || mode == GameMode.VERSUS;

        if (state.equals("DASH")) {
            currentTex = isByte ? game.texByteDash : game.texWannaCryWalk;
        } else if (state.equals("DASH_ATTACK")) {
            currentTex = isByte ? game.texByteDashAttack : game.texWannaCryAttack2;
        } else if (state.startsWith("ATTACK")) {
            if (isByte) {
                if (state.equals("ATTACK1")) currentTex = game.texByteAttack1;
                else if (state.equals("ATTACK2")) currentTex = game.texByteAttack2;
                else currentTex = game.texByteAttack3;
            } else {
                if (animTimer < 15) currentTex = game.texWannaCryAttack1;
                else currentTex = game.texWannaCryAttack2;
            }
        } else if (state.equals("SPECIAL")) {
            if (isByte) {
                currentTex = animTimer < 10 ? game.texByteSpecialCharge : game.texByteSpecialFire;
            } else {
                currentTex = game.texWannaCrySpecial;
            }
        } else if (block) {
            currentTex = isByte ? game.texByteBlock : game.texWannaCryBlock;
        } else if (state.equals("HIT")) {
            if (isByte) {
                currentTex = (animTimer < 8) ? game.texByteHit1 : game.texByteHit2;
            } else {
                currentTex = (animTimer < 8) ? game.texWannaCryHit1 : game.texWannaCryHit2;
            }
            drawY += (float) Math.sin(animTimer * 0.8f) * 6f;
        } else if (state.equals("WALK")) {
            boolean frame2 = ((idleF / 10) % 2 == 1);
            if (isByte) {
                currentTex = frame2 ? game.texByteWalk2 : game.texByteWalk1;
            } else {
                currentTex = game.texWannaCryWalk;
            }
            float stride = (float) Math.sin(idleF * 0.28f);
            drawY = y + Math.abs(stride) * (h * 0.022f);
        } else if (state.equals("WIN")) {
            if (isByte) {
                currentTex = ((idleF / 20) % 2 == 1) ? game.texByteWin2 : game.texByteWin1;
            } else {
                currentTex = game.texWannaCryWin;
            }
        } else if (state.equals("LOSE")) {
            currentTex = isByte ? game.texByteLose : game.texWannaCryLose;
        } else {
            // IDLE state with breathing animation
            boolean frame2 = ((idleF / 20) % 2 == 1);
            if (isByte) {
                currentTex = frame2 ? game.texByteIdle2 : game.texByteIdle1;
            } else {
                currentTex = frame2 ? game.texWannaCryIdle2 : game.texWannaCryIdle1;
            }
            float breath = (float) Math.sin(idleF * 0.09f);
            drawH = h * (1.0f + breath * 0.020f);
            drawW = w * (1.0f - breath * 0.012f);
            drawY = y + breath * (h * 0.005f);
        }

        if (currentTex == null) currentTex = isByte ? game.texPlayerIdle : game.texEnemyIdle;

        game.batch.begin();
        if (flash > 0) {
            game.batch.setColor(1f, 0.35f, 0.35f, 1f);
        } else if (!isP1 && mode == GameMode.VERSUS) {
            // Orange color tint for Player 2
            game.batch.setColor(1.0f, 0.65f, 0.2f, 1f);
        } else {
            game.batch.setColor(1f, 1f, 1f, 1f);
        }

        float drawX = x - drawW / 2f;
        if (currentTex != null) {
            if (facing < 0) {
                game.batch.draw(currentTex, drawX, drawY, drawW, drawH,
                    0, 0, currentTex.getWidth(), currentTex.getHeight(), true, false);
            } else {
                game.batch.draw(currentTex, drawX, drawY, drawW, drawH);
            }
        }
        game.batch.setColor(1f, 1f, 1f, 1f);
        game.batch.end();

        // Firewall shield effect
        if (block) {
            game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            game.shapeRenderer.setColor(isP1 ? 0f : 1f, isP1 ? 0.85f : 0.6f, isP1 ? 1f : 0.1f, 0.35f);
            float shieldX = (facing > 0) ? x + w * 0.1f : x - w * 0.5f;
            game.shapeRenderer.rect(shieldX, y, w * 0.4f, h * 0.88f);
            game.shapeRenderer.end();

            game.shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            game.shapeRenderer.setColor(isP1 ? 0.31f : 1f, isP1 ? 0.95f : 0.7f, isP1 ? 1f : 0.2f, 0.9f);
            game.shapeRenderer.rect(shieldX, y, w * 0.4f, h * 0.88f);
            game.shapeRenderer.end();
        }
    }

    void drawFightHUD(float w, float h) {
        game.batch.setProjectionMatrix(hudCamera.combined);
        game.shapeRenderer.setProjectionMatrix(hudCamera.combined);

        // Combined Street Fighter Top HUD (Health, Mana with Level Number, Round Dots, Timer)
        drawSF6HUD(w, h);

        // Touch Controls
        drawTouchControls(w, h);

        // Top Pause Button
        float pBtnSize = h * 0.065f;
        float pBtnX = w / 2f - pBtnSize / 2f;
        float pBtnY = h * 0.74f;

        if (game.texBtnPause != null) {
            game.batch.begin();
            game.batch.draw(game.texBtnPause, pBtnX, pBtnY, pBtnSize, pBtnSize);
            game.batch.end();
        } else {
            game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            game.shapeRenderer.setColor(0.08f, 0.12f, 0.20f, 0.85f);
            game.shapeRenderer.rect(pBtnX, pBtnY, pBtnSize, pBtnSize * 0.65f);
            game.shapeRenderer.end();

            game.shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            game.shapeRenderer.setColor(0.31f, 0.76f, 0.97f, 0.8f);
            game.shapeRenderer.rect(pBtnX, pBtnY, pBtnSize, pBtnSize * 0.65f);
            game.shapeRenderer.end();

            game.batch.begin();
            game.fontSmall.setColor(1, 1, 1, 1);
            game.drawTextCentered(game.fontSmall, "||", pBtnX + pBtnSize / 2f, pBtnY + pBtnSize * 0.325f);
            game.batch.end();
        }

        if (isButtonClicked(pBtnX, pBtnY, pBtnSize, pBtnSize)) {
            isPaused = true;
        }

        // Shared scanline and vignette overlay for arcade cyber-feel
        game.batch.begin();
        if (game.texUiScanlines != null) {
            game.batch.setColor(1, 1, 1, 0.35f);
            game.batch.draw(game.texUiScanlines, 0, 0, w, h);
        }
        if (game.texUiVignette != null) {
            game.batch.setColor(1, 1, 1, 0.50f);
            game.batch.draw(game.texUiVignette, 0, 0, w, h);
        }
        game.batch.setColor(1, 1, 1, 1);
        game.batch.end();
    }

    void drawSF6HUD(float w, float h) {
        float barH = h * 0.038f;
        float barMaxW = w * 0.38f;
        float barY = h * 0.90f;

        float p1BarX = w * 0.09f;
        float p2BarX = w * 0.53f;

        // =========================================================================
        // --- PLAYER 1 (Left - Cyan Theme) ---
        // =========================================================================

        // 1. Health Bar Container
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        game.shapeRenderer.setColor(0.12f, 0.12f, 0.16f, 0.92f);
        game.shapeRenderer.rect(p1BarX, barY, barMaxW, barH);
        // Yellow delayed drain bar
        float p1DrainW = (p1DisplayHP / (float) p1MaxHP) * barMaxW;
        game.shapeRenderer.setColor(1.0f, 0.88f, 0.2f, 0.85f);
        game.shapeRenderer.rect(p1BarX + barMaxW - p1DrainW, barY, p1DrainW, barH);
        // Health Fill (Cyan)
        float p1FillW = (p1HP / (float) p1MaxHP) * barMaxW;
        game.shapeRenderer.setColor(0.0f, 0.85f, 0.95f, 1f);
        game.shapeRenderer.rect(p1BarX + barMaxW - p1FillW, barY, p1FillW, barH);
        game.shapeRenderer.end();

        // Health Bar Border
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        game.shapeRenderer.setColor(0.31f, 0.95f, 1f, 0.9f);
        game.shapeRenderer.rect(p1BarX, barY, barMaxW, barH);
        game.shapeRenderer.end();

        // 2. Combined Mana / Special Meter (Directly below Health Bar)
        float manaH = h * 0.020f;
        float manaY = barY - manaH - 4;

        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        // Mana Bar Background
        game.shapeRenderer.setColor(0.08f, 0.10f, 0.15f, 0.95f);
        game.shapeRenderer.rect(p1BarX, manaY, barMaxW, manaH);
        // Mana Fill (Vibrant Electric Cyan / Emerald Green)
        float p1ManaFillW = (p1SuperGauge / 100f) * barMaxW;
        game.shapeRenderer.setColor(0.0f, 0.95f, 0.55f, 1f);
        game.shapeRenderer.rect(p1BarX + barMaxW - p1ManaFillW, manaY, p1ManaFillW, manaH);
        game.shapeRenderer.end();

        // Mana Bar Border
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        game.shapeRenderer.setColor(0.2f, 0.95f, 0.6f, 0.9f);
        game.shapeRenderer.rect(p1BarX, manaY, barMaxW, manaH);
        // 3 Segment Level Dividers (at 33.3% and 66.6%)
        game.shapeRenderer.setColor(0.05f, 0.2f, 0.15f, 0.85f);
        float seg1 = p1BarX + barMaxW * 0.333f;
        float seg2 = p1BarX + barMaxW * 0.666f;
        game.shapeRenderer.line(seg1, manaY, seg1, manaY + manaH);
        game.shapeRenderer.line(seg2, manaY, seg2, manaY + manaH);
        game.shapeRenderer.end();

        // 3. Round Win Dots (Under Mana bar)
        float dotY = manaY - 14;
        if (game.texRoundDotEmpty != null && game.texRoundDotFilledP1 != null) {
            game.batch.begin();
            for (int i = 0; i < ROUNDS_TO_WIN; i++) {
                float dotX = p1BarX + barMaxW - (i * 22) - 15;
                Texture dt = (i < p1RoundsWon) ? game.texRoundDotFilledP1 : game.texRoundDotEmpty;
                game.batch.draw(dt, dotX - 7, dotY - 7, 14, 14);
            }
            game.batch.end();
        } else {
            game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            for (int i = 0; i < ROUNDS_TO_WIN; i++) {
                float dotX = p1BarX + barMaxW - (i * 20) - 15;
                if (i < p1RoundsWon) {
                    game.shapeRenderer.setColor(1.0f, 0.85f, 0.1f, 1f); // Filled Gold
                } else {
                    game.shapeRenderer.setColor(0.2f, 0.25f, 0.3f, 0.8f);
                }
                game.shapeRenderer.circle(dotX, dotY, 6, 12);
            }
            game.shapeRenderer.end();
        }

        // 4. P1 Portrait Box & Mana Level Badge
        float portSize = h * 0.088f;
        float portX = p1BarX - portSize - 6;
        float portY = barY - (portSize - barH) / 2f;

        game.batch.begin();
        if (game.texBytePortrait != null) {
            game.batch.draw(game.texBytePortrait, portX, portY, portSize, portSize);
        } else if (game.texPlayerIdle != null) {
            game.batch.draw(game.texPlayerIdle, portX + 2, portY + 2, portSize - 4, portSize - 4);
        }
        if (game.texPortraitFramePlayer != null) {
            game.batch.draw(game.texPortraitFramePlayer, portX, portY, portSize, portSize);
        }
        game.batch.end();

        if (game.texPortraitFramePlayer == null) {
            game.shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            game.shapeRenderer.setColor(0.0f, 0.85f, 1f, 1f);
            game.shapeRenderer.rect(portX, portY, portSize, portSize);
            game.shapeRenderer.end();
        }

        // P1 Text Labels (Name + Mana Level Number)
        game.batch.begin();
        game.fontBold.setColor(1, 1, 1, 1);
        game.fontBold.draw(game.batch, "CAT", p1BarX + 8, barY + barH + 20);

        // P1 Mana Level Badge (Cyan / Emerald)
        String p1ManaLabel = (p1SuperLevel >= 3) ? "MANA: MAX" : ("MANA: LV." + p1SuperLevel);
        game.fontSmall.setColor(0.0f, 0.95f, 0.55f, 1f);
        game.drawTextCentered(game.fontSmall, p1ManaLabel, p1BarX + 60, manaY + manaH / 2f);
        game.batch.end();

        // =========================================================================
        // --- PLAYER 2 (Right - Orange / Crimson Theme) ---
        // =========================================================================

        // 1. Health Bar Container
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        game.shapeRenderer.setColor(0.12f, 0.12f, 0.16f, 0.92f);
        game.shapeRenderer.rect(p2BarX, barY, barMaxW, barH);
        // Yellow delayed drain bar
        float p2DrainW = (p2DisplayHP / (float) p2MaxHP) * barMaxW;
        game.shapeRenderer.setColor(1.0f, 0.88f, 0.2f, 0.85f);
        game.shapeRenderer.rect(p2BarX, barY, p2DrainW, barH);
        // Health Fill (Orange / Crimson)
        float p2FillW = (p2HP / (float) p2MaxHP) * barMaxW;
        game.shapeRenderer.setColor(1.0f, mode == GameMode.VERSUS ? 0.60f : 0.15f, 0.2f, 1f);
        game.shapeRenderer.rect(p2BarX, barY, p2FillW, barH);
        game.shapeRenderer.end();

        // Health Bar Border
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        game.shapeRenderer.setColor(1.0f, mode == GameMode.VERSUS ? 0.7f : 0.3f, 0.3f, 0.9f);
        game.shapeRenderer.rect(p2BarX, barY, barMaxW, barH);
        game.shapeRenderer.end();

        // 2. Combined Mana / Special Meter (Directly below Health Bar)
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        // Mana Bar Background
        game.shapeRenderer.setColor(0.15f, 0.10f, 0.08f, 0.95f);
        game.shapeRenderer.rect(p2BarX, manaY, barMaxW, manaH);
        // Mana Fill (Fiery Gold / Sun Orange)
        float p2ManaFillW = (p2SuperGauge / 100f) * barMaxW;
        game.shapeRenderer.setColor(1.0f, 0.70f, 0.15f, 1f);
        game.shapeRenderer.rect(p2BarX, manaY, p2ManaFillW, manaH);
        game.shapeRenderer.end();

        // Mana Bar Border
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        game.shapeRenderer.setColor(1.0f, 0.75f, 0.2f, 0.9f);
        game.shapeRenderer.rect(p2BarX, manaY, barMaxW, manaH);
        // 3 Segment Level Dividers (at 33.3% and 66.6%)
        game.shapeRenderer.setColor(0.25f, 0.15f, 0.05f, 0.85f);
        float p2Seg1 = p2BarX + barMaxW * 0.333f;
        float p2Seg2 = p2BarX + barMaxW * 0.666f;
        game.shapeRenderer.line(p2Seg1, manaY, p2Seg1, manaY + manaH);
        game.shapeRenderer.line(p2Seg2, manaY, p2Seg2, manaY + manaH);
        game.shapeRenderer.end();

        // 3. Round Win Dots (Under Mana bar)
        if (game.texRoundDotEmpty != null && game.texRoundDotFilledP2 != null) {
            game.batch.begin();
            for (int i = 0; i < ROUNDS_TO_WIN; i++) {
                float dotX = p2BarX + (i * 22) + 15;
                Texture dt = (i < p2RoundsWon) ? game.texRoundDotFilledP2 : game.texRoundDotEmpty;
                game.batch.draw(dt, dotX - 7, dotY - 7, 14, 14);
            }
            game.batch.end();
        } else {
            game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            for (int i = 0; i < ROUNDS_TO_WIN; i++) {
                float dotX = p2BarX + (i * 20) + 15;
                if (i < p2RoundsWon) {
                    game.shapeRenderer.setColor(1.0f, 0.85f, 0.1f, 1f);
                } else {
                    game.shapeRenderer.setColor(0.2f, 0.25f, 0.3f, 0.8f);
                }
                game.shapeRenderer.circle(dotX, dotY, 6, 12);
            }
            game.shapeRenderer.end();
        }

        // 4. P2 Portrait Box & Mana Level Badge
        float p2PortX = p2BarX + barMaxW + 6;

        game.batch.begin();
        if (mode == GameMode.VERSUS) {
            if (game.texBytePortrait != null) {
                game.batch.setColor(1.0f, 0.65f, 0.2f, 1f);
                game.batch.draw(game.texBytePortrait, p2PortX + portSize, portY, -portSize, portSize);
                game.batch.setColor(1, 1, 1, 1);
            } else if (game.texPlayerIdle != null) {
                game.batch.setColor(1.0f, 0.65f, 0.2f, 1f);
                game.batch.draw(game.texPlayerIdle, p2PortX + portSize - 2, portY + 2, -(portSize - 4), portSize - 4);
                game.batch.setColor(1, 1, 1, 1);
            }
            if (game.texPortraitFrameP2 != null) {
                game.batch.draw(game.texPortraitFrameP2, p2PortX, portY, portSize, portSize);
            }
            game.fontBold.setColor(1.0f, 0.7f, 0.2f, 1);
            game.drawTextCentered(game.fontBold, "CAT (P2)", p2BarX + barMaxW - 60, barY + barH + 20);
        } else {
            if (game.texWannaCryPortrait != null) {
                game.batch.draw(game.texWannaCryPortrait, p2PortX + portSize, portY, -portSize, portSize);
            } else if (game.texEnemyIdle != null) {
                game.batch.draw(game.texEnemyIdle, p2PortX + portSize - 2, portY + 2, -(portSize - 4), portSize - 4);
            }
            if (game.texPortraitFrameEnemy != null) {
                game.batch.draw(game.texPortraitFrameEnemy, p2PortX, portY, portSize, portSize);
            }
            game.fontBold.setColor(1.0f, 0.3f, 0.3f, 1);
            game.drawTextCentered(game.fontBold, "WANNACRY", p2BarX + barMaxW - 60, barY + barH + 20);
        }

        // P2 Mana Level Badge (Fiery Gold / Orange)
        String p2ManaLabel = (p2SuperLevel >= 3) ? "MANA: MAX" : ("MANA: LV." + p2SuperLevel);
        game.fontSmall.setColor(1.0f, 0.75f, 0.2f, 1f);
        game.drawTextCentered(game.fontSmall, p2ManaLabel, p2BarX + barMaxW - 60, manaY + manaH / 2f);
        game.batch.end();

        if (game.texPortraitFrameEnemy == null) {
            game.shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            game.shapeRenderer.setColor(1.0f, mode == GameMode.VERSUS ? 0.6f : 0.2f, 0.2f, 1f);
            game.shapeRenderer.rect(p2PortX, portY, portSize, portSize);
            game.shapeRenderer.end();
        }

        // =========================================================================
        // --- CENTER 99s FIGHT TIMER ---
        // =========================================================================
        float timerBoxW = w * 0.09f;
        float timerBoxH = barH * 1.55f;
        float timerBoxX = w / 2f - timerBoxW / 2f;
        float timerBoxY = barY - (timerBoxH - barH) / 2f;

        if (game.texTimerFrame != null) {
            game.batch.begin();
            game.batch.draw(game.texTimerFrame, timerBoxX - 4, timerBoxY - 4, timerBoxW + 8, timerBoxH + 8);
            game.batch.end();
        } else {
            game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            game.shapeRenderer.setColor(0.06f, 0.08f, 0.14f, 0.95f);
            game.shapeRenderer.rect(timerBoxX, timerBoxY, timerBoxW, timerBoxH);
            game.shapeRenderer.end();

            game.shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            game.shapeRenderer.setColor(1.0f, 0.85f, 0.2f, 1f);
            game.shapeRenderer.rect(timerBoxX, timerBoxY, timerBoxW, timerBoxH);
            game.shapeRenderer.end();
        }

        game.batch.begin();
        int tSecs = Math.max(0, (int) Math.ceil(roundTimer));
        game.fontBold.setColor(1.0f, 0.9f, 0.2f, 1);
        game.drawTextCentered(game.fontBold, String.valueOf(tSecs), w / 2f, timerBoxY + timerBoxH / 2f);

        if (game.texVsBadge != null) {
            float vsW = h * 0.055f;
            float vsH = vsW * 0.65f;
            game.batch.draw(game.texVsBadge, w / 2f - vsW / 2f, timerBoxY - vsH - 2, vsW, vsH);
        }
        game.batch.end();
    }

    void drawTouchControls(float w, float h) {
        float btnSize = h * 0.135f;
        float padY = h * 0.045f;

        // Player 1 Controls (Cyan)
        float p1LeftX = w * 0.04f;
        float p1RightX = w * 0.17f;
        float p1AtkX = (mode == GameMode.VERSUS) ? w * 0.31f : w * 0.65f;
        float p1SplX = (mode == GameMode.VERSUS) ? w * 0.39f : w * 0.77f;
        float p1SplY = (mode == GameMode.VERSUS) ? padY + btnSize * 0.7f : padY + btnSize * 0.45f;
        float p1BlkX = (mode == GameMode.VERSUS) ? w * 0.42f : w * 0.88f;

        drawArcadeButton(p1LeftX, padY, btnSize, "<", 0x00bcd4, p1LeftWasPressed ? game.texDpadLeftPressed : game.texDpadLeft);
        drawArcadeButton(p1RightX, padY, btnSize, ">", 0x00bcd4, p1RightWasPressed ? game.texDpadRightPressed : game.texDpadRight);
        drawArcadeButton(p1AtkX, padY, btnSize, "ATK", 0xff1744, p1AtkWasPressed ? game.texBtnAttackPressed : game.texBtnAttack);
        drawArcadeButton(p1SplX, p1SplY, btnSize, "SP", 0x00e5ff, p1SplWasPressed ? game.texBtnSpecialPressed : game.texBtnSpecial);
        drawArcadeButton(p1BlkX, padY, btnSize, "BLK", 0xffeb3b, p1Block ? game.texBtnBlockPressed : game.texBtnBlock);

        // Player 2 Controls (Orange - only in Versus Mode)
        if (mode == GameMode.VERSUS) {
            float p2LeftX = w * 0.54f;
            float p2RightX = w * 0.67f;
            float p2AtkX = w * 0.80f;
            float p2SplX = w * 0.88f;
            float p2SplY = padY + btnSize * 0.7f;
            float p2BlkX = w * 0.91f;

            drawArcadeButton(p2LeftX, padY, btnSize, "<", 0xff9800, p2LeftWasPressed ? game.texDpadLeftPressed : game.texDpadLeft);
            drawArcadeButton(p2RightX, padY, btnSize, ">", 0xff9800, p2RightWasPressed ? game.texDpadRightPressed : game.texDpadRight);
            drawArcadeButton(p2AtkX, padY, btnSize, "ATK", 0xff5722, p2AtkWasPressed ? game.texBtnAttackPressed : game.texBtnAttack);
            drawArcadeButton(p2SplX, p2SplY, btnSize, "SP", 0xffb74d, p2SplWasPressed ? game.texBtnSpecialPressed : game.texBtnSpecial);
            drawArcadeButton(p2BlkX, padY, btnSize, "BLK", 0xffeb3b, p2Block ? game.texBtnBlockPressed : game.texBtnBlock);
        }
    }

    void drawArcadeButton(float x, float y, float size, String label, int col, Texture tex) {
        if (tex != null) {
            game.batch.begin();
            game.batch.draw(tex, x, y, size, size);
            game.batch.end();
            return;
        }

        float r = ((col >> 16) & 0xFF) / 255f;
        float g = ((col >> 8) & 0xFF) / 255f;
        float b = (col & 0xFF) / 255f;

        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        game.shapeRenderer.setColor(r * 0.35f, g * 0.35f, b * 0.35f, 0.75f);
        game.shapeRenderer.circle(x + size / 2f, y + size / 2f, size / 2f, 24);
        game.shapeRenderer.end();

        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        game.shapeRenderer.setColor(r, g, b, 0.95f);
        game.shapeRenderer.circle(x + size / 2f, y + size / 2f, size / 2f, 24);
        game.shapeRenderer.end();

        game.batch.begin();
        game.fontBold.setColor(1, 1, 1, 1);
        game.drawTextCentered(game.fontBold, label, x + size / 2f, y + size / 2f);
        game.batch.end();
    }

    void drawRoundBanner(float w, float h) {
        float bannerH = h * 0.22f;
        float bannerY = h * 0.44f;

        Texture bannerTex = null;
        if (bannerText.startsWith("ROUND") || bannerText.contains("FINAL ROUND")) {
            bannerTex = game.texRoundBanner;
        } else if (bannerText.equals("FIGHT!")) {
            bannerTex = game.texFightBanner;
        } else if (bannerText.startsWith("K.O.") || bannerText.startsWith("DOUBLE K.O.")) {
            bannerTex = game.texKoBanner;
        } else if (bannerText.startsWith("TIME OVER")) {
            bannerTex = game.texTimeOverBanner;
        }

        if (bannerTex != null) {
            game.batch.begin();
            float bw = w * 0.65f;
            float bh = bannerH * 1.15f;
            game.batch.draw(bannerTex, w / 2f - bw / 2f, bannerY + bannerH / 2f - bh / 2f, bw, bh);
            game.batch.end();
        } else {
            game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            game.shapeRenderer.setColor(0, 0, 0, 0.78f);
            game.shapeRenderer.rect(0, bannerY, w, bannerH);
            game.shapeRenderer.end();

            game.shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            game.shapeRenderer.setColor(1.0f, 0.85f, 0.1f, 1f);
            game.shapeRenderer.rect(0, bannerY, w, bannerH);
            game.shapeRenderer.end();
        }

        game.batch.begin();
        game.fontTitle.setColor(1.0f, 0.88f, 0.2f, 1);
        String[] lines = bannerText.split("\n");
        if (lines.length == 1) {
            game.drawTextCentered(game.fontTitle, lines[0], w / 2f, bannerY + bannerH / 2f);
        } else {
            game.drawTextCentered(game.fontTitle, lines[0], w / 2f, bannerY + bannerH * 0.68f);
            game.fontBold.setColor(1, 1, 1, 1);
            game.drawTextCentered(game.fontBold, lines[1], w / 2f, bannerY + bannerH * 0.30f);
        }
        game.batch.end();
    }

    void drawMatchOverBanner(float w, float h) {
        float bannerH = h * 0.35f;
        float bannerY = h * 0.35f;

        if (game.texWinnerBanner != null) {
            game.batch.begin();
            float bw = w * 0.70f;
            float bh = bannerH * 0.85f;
            game.batch.draw(game.texWinnerBanner, w / 2f - bw / 2f, bannerY + bannerH / 2f - bh / 2f + 25, bw, bh);
            game.batch.end();
        } else {
            game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            game.shapeRenderer.setColor(0.04f, 0.08f, 0.14f, 0.92f);
            game.shapeRenderer.rect(0, bannerY, w, bannerH);
            game.shapeRenderer.end();

            game.shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            game.shapeRenderer.setColor(0.0f, 0.85f, 1f, 1f);
            game.shapeRenderer.rect(0, bannerY, w, bannerH);
            game.shapeRenderer.end();
        }

        game.batch.begin();
        game.fontTitle.setColor(1.0f, 0.88f, 0.2f, 1);
        game.drawTextCentered(game.fontTitle, matchWinnerText, w / 2f, bannerY + bannerH * 0.72f);
        game.batch.end();

        // Rematch & Menu Buttons
        float btnW = w * 0.22f;
        float btnH = h * 0.075f;
        float btnY = bannerY + bannerH * 0.22f;

        drawButton(w * 0.24f, btnY, btnW, btnH, "REMATCH", 0x00e676, 0x00b248);
        if (isButtonClicked(w * 0.24f, btnY, btnW, btnH)) {
            resetFight();
        }

        drawButton(w * 0.54f, btnY, btnW, btnH, "MAIN MENU", 0x546e7a, 0x263238);
        if (isButtonClicked(w * 0.54f, btnY, btnW, btnH)) {
            game.setScreen(game.menuScreen);
        }
    }

    void drawPauseOverlay(float w, float h) {
        if (game.texPauseOverlay != null) {
            game.batch.begin();
            game.batch.draw(game.texPauseOverlay, 0, 0, w, h);
            game.batch.end();
        } else {
            // Dark translucent shroud
            game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            game.shapeRenderer.setColor(0, 0, 0, 0.78f);
            game.shapeRenderer.rect(0, 0, w, h);
            game.shapeRenderer.end();
        }

        // Pause Menu Card
        float cardW = w * 0.44f;
        float cardH = h * 0.65f;
        float cardX = w / 2f - cardW / 2f;
        float cardY = h * 0.18f;

        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        game.shapeRenderer.setColor(0.05f, 0.08f, 0.14f, 0.95f);
        game.shapeRenderer.rect(cardX, cardY, cardW, cardH);
        game.shapeRenderer.end();

        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        game.shapeRenderer.setColor(0.31f, 0.76f, 0.97f, 1f);
        game.shapeRenderer.rect(cardX, cardY, cardW, cardH);
        game.shapeRenderer.end();

        game.batch.begin();
        game.fontTitle.setColor(1, 1, 1, 1);
        game.drawTextCentered(game.fontTitle, "SYSTEM PAUSED", w / 2f, cardY + cardH - 45);
        game.batch.end();

        float btnW = cardW * 0.82f;
        float btnH = h * 0.085f;
        float btnX = w / 2f - btnW / 2f;
        float startY = cardY + cardH - 120;
        float gap = btnH * 1.25f;

        // 1. Resume
        drawButton(btnX, startY, btnW, btnH, "RESUME COMBAT", 0x00bcd4, 0x006064);
        if (isButtonClicked(btnX, startY, btnW, btnH)) {
            isPaused = false;
        }

        // 2. Restart Match
        drawButton(btnX, startY - gap, btnW, btnH, "RESTART MATCH", 0xff9800, 0xbf360c);
        if (isButtonClicked(btnX, startY - gap, btnW, btnH)) {
            isPaused = false;
            resetFight();
        }

        // 3. How to Play
        drawButton(btnX, startY - gap * 2, btnW, btnH, "HOW TO PLAY", 0x26a69a, 0x0d473d);
        if (isButtonClicked(btnX, startY - gap * 2, btnW, btnH)) {
            game.setScreen(game.howToPlayScreen);
        }

        // 4. Quit
        drawButton(btnX, startY - gap * 3, btnW, btnH, "QUIT TO MENU", 0x546e7a, 0x263238);
        if (isButtonClicked(btnX, startY - gap * 3, btnW, btnH)) {
            isPaused = false;
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

    void spawnParticles(float x, float y, int color, int count) {
        for (int i = 0; i < count; i++) {
            float vx = (rand.nextFloat() - 0.5f) * 8f;
            float vy = (rand.nextFloat() - 0.5f) * 8f + 2f;
            particles.add(new Particle(x, y, vx, vy, color, 3 + rand.nextInt(3), 15 + rand.nextInt(12)));
        }
    }

    void addNotice(String text, float x, float y, int color) {
        notices.add(new FloatingNotice(text, x, y, color, 45));
    }

    @Override public void resize(int width, int height) { game.updateFontScales(); }
    @Override public void pause() { isPaused = true; }
    @Override public void resume() {}
    @Override public void hide() {
        game.stopMusic();
    }
    @Override public void dispose() {}

    // Inner Helper Classes
    static class Hitbox {
        float x, y, w, h;
        int damage, hitstun;
        Hitbox(float x, float y, float w, float h, int damage, int hitstun) {
            this.x = x; this.y = y; this.w = w; this.h = h;
            this.damage = damage; this.hitstun = hitstun;
        }
    }

    static class Projectile {
        float x, y, vx;
        int damage;
        boolean active = true;
        Projectile(float x, float y, float vx, int damage) {
            this.x = x; this.y = y; this.vx = vx; this.damage = damage;
        }
    }

    static class Particle {
        float x, y, vx, vy;
        int color, size, life, maxLife;
        Particle(float x, float y, float vx, float vy, int color, int size, int maxLife) {
            this.x = x; this.y = y; this.vx = vx; this.vy = vy;
            this.color = color; this.size = size; this.life = maxLife; this.maxLife = maxLife;
        }
    }

    static class GhostTrail {
        float x, y, w, h, facing;
        int color, life;
        GhostTrail(float x, float y, float w, float h, float facing, int color, int life) {
            this.x = x; this.y = y; this.w = w; this.h = h;
            this.facing = facing; this.color = color; this.life = life;
        }
    }

    static class FloatingNotice {
        String text;
        float x, y;
        int color, life;
        FloatingNotice(String text, float x, float y, int color, int life) {
            this.text = text; this.x = x; this.y = y; this.color = color; this.life = life;
        }
    }
}
