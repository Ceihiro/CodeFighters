package com.codefighters;

import com.badlogic.gdx.Gdx;
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

    // Stage dimensions (wider than single screen for SF-style camera panning)
    float stageWidth;
    float stageHeight;
    float groundY;

    // Cameras: world camera follows fighters with dynamic zoom/pan; hudCamera is fixed to screen
    OrthographicCamera worldCamera;
    OrthographicCamera hudCamera;
    float camX;
    float camZoom = 1.0f;

    // Player
    float playerX;
    float playerY;
    float playerVX = 0;
    int playerHP = 100, playerMaxHP = 100;
    String playerState = "IDLE";
    int playerAnimTimer = 0;
    int playerIdleFrame = 0;
    int playerComboCount = 0;
    int playerComboTimer = 0;
    int playerInvuln = 0;
    boolean playerBlock = false;
    int playerSpecialCooldown = 0;
    boolean playerAttackHeld = false;
    boolean playerSpecialHeld = false;
    float playerFacing = 1;
    Hitbox playerHitbox = null;
    int playerFlash = 0;

    // Enemy
    float enemyX;
    float enemyY;
    float enemyVX = 0;
    int enemyHP = 100, enemyMaxHP = 100;
    String enemyState = "IDLE";
    int enemyAnimTimer = 0;
    int enemyIdleFrame = 0;
    float enemyFacing = -1;
    Hitbox enemyHitbox = null;
    int enemyInvuln = 0;
    int enemyAttackTimer = 60;
    int enemyFlash = 0;

    List<Projectile> projectiles = new ArrayList<>();
    List<Particle> particles = new ArrayList<>();

    int comboCount = 0;
    int comboTimer = 0;
    float comboX = 0, comboY = 0;
    float comboScale = 1f;

    int hitPause = 0;
    int frame = 0;

    // Screen shake
    float shakeX = 0, shakeY = 0;
    int shakeTimer = 0;

    // Touch button bounds (in screen coordinates)
    float btnLeftX, btnLeftY, btnRightX, btnRightY;
    float btnAtkX, btnAtkY, btnSplX, btnSplY, btnBlkX, btnBlkY;
    float btnSize;

    // Touch state tracking
    boolean attackWasPressed = false;
    boolean specialWasPressed = false;

    // Round announcement / intro phase
    int introPhase = 0; // 0 = ROUND 1, 1 = FIGHT!, 2 = fighting
    float introTimer = 0;
    static final float ROUND_BANNER_DURATION = 1.3f;
    static final float FIGHT_BANNER_DURATION = 0.85f;

    // Fight stats
    float fightTime = 0;
    int maxCombo = 0;
    int totalDamageDealt = 0;

    public FightScreen(final CodeFightersGame game) {
        this.game = game;
        worldCamera = new OrthographicCamera();
        hudCamera = new OrthographicCamera();
    }

    public void resetFight() {
        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();

        stageWidth = w * 1.8f;
        stageHeight = h;
        groundY = h * 0.20f; // Ground plane at 20% height, firmly on floor

        playerX = stageWidth * 0.38f;
        enemyX = stageWidth * 0.62f;
        playerY = groundY;
        enemyY = groundY;

        playerHP = 100; playerVX = 0;
        playerState = "IDLE"; playerComboCount = 0; playerInvuln = 0;
        playerBlock = false; playerSpecialCooldown = 0; playerFlash = 0;
        playerFacing = 1; playerIdleFrame = 0;

        enemyHP = 100; enemyVX = 0;
        enemyState = "IDLE"; enemyInvuln = 0; enemyAttackTimer = 60; enemyFlash = 0;
        enemyFacing = -1; enemyIdleFrame = 0;

        projectiles.clear();
        particles.clear();
        comboCount = 0; comboTimer = 0; hitPause = 0; frame = 0;
        shakeX = 0; shakeY = 0; shakeTimer = 0;

        camX = (playerX + enemyX) / 2.0f;
        camZoom = 1.0f;

        worldCamera.setToOrtho(false, w, h);
        worldCamera.position.set(camX, h / 2f, 0);
        worldCamera.zoom = camZoom;
        worldCamera.update();

        hudCamera.setToOrtho(false, w, h);
        hudCamera.position.set(w / 2f, h / 2f, 0);
        hudCamera.update();

        introPhase = 0;
        introTimer = 0;
        fightTime = 0;
        maxCombo = 0;
        totalDamageDealt = 0;
    }

    @Override
    public void show() {
        game.updateFontScales();
        resetFight();
    }

    @Override
    public void render(float delta) {
        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();

        // Update camera sizing if window dimensions change
        if (worldCamera.viewportWidth != w || worldCamera.viewportHeight != h) {
            stageWidth = w * 1.8f;
            stageHeight = h;
            groundY = h * 0.20f;
            worldCamera.setToOrtho(false, w, h);
            hudCamera.setToOrtho(false, w, h);
            hudCamera.position.set(w / 2f, h / 2f, 0);
            hudCamera.update();
        }

        // --- Intro announcement phase ---
        if (introPhase < 2) {
            introTimer += delta;
            if (introPhase == 0 && introTimer >= ROUND_BANNER_DURATION) {
                introPhase = 1;
                introTimer = 0;
            } else if (introPhase == 1 && introTimer >= FIGHT_BANNER_DURATION) {
                introPhase = 2;
                introTimer = 0;
            }
            updateCamera(w, h);
            drawFightWorld(w, h);
            drawFightHUD(w, h);
            drawIntroBanner(w, h);
            return;
        }

        // --- Normal fight loop ---
        fightTime += delta;
        if (hitPause > 0) {
            hitPause--;
            updateCamera(w, h);
            drawFightWorld(w, h);
            drawFightHUD(w, h);
            return;
        }

        frame++;
        updateFight(w, h);
        updateCamera(w, h);
        drawFightWorld(w, h);
        drawFightHUD(w, h);
    }

    void updateCamera(float w, float h) {
        // Dynamic midpoint tracking
        float midX = (playerX + enemyX) / 2.0f;
        float dist = Math.abs(playerX - enemyX);

        // Dynamic Zoom (Street Fighter style: zooms in when close, zooms out when far)
        float targetZoom = 1.0f;
        if (dist < w * 0.35f) {
            targetZoom = 0.90f; // Zoom in close for brawl
        } else if (dist > w * 0.55f) {
            targetZoom = 1.0f + ((dist - w * 0.55f) / (w * 0.8f)) * 0.22f;
            targetZoom = Math.min(1.18f, targetZoom);
        }

        // Smooth camera lerp
        camX += (midX - camX) * 0.08f;
        camZoom += (targetZoom - camZoom) * 0.05f;

        // Camera clamp inside stage boundaries
        float halfVisibleW = (w * camZoom) / 2.0f;
        camX = Math.max(halfVisibleW, Math.min(stageWidth - halfVisibleW, camX));

        // Screen shake decay
        if (shakeTimer > 0) {
            shakeTimer--;
            shakeX = (rand.nextFloat() - 0.5f) * shakeTimer * 0.9f;
            shakeY = (rand.nextFloat() - 0.5f) * shakeTimer * 0.9f;
        } else {
            shakeX = 0;
            shakeY = 0;
        }

        worldCamera.position.set(camX + shakeX, h / 2f + shakeY, 0);
        worldCamera.zoom = camZoom;
        worldCamera.update();
    }

    void updateFight(float w, float h) {
        playerY = groundY;
        enemyY = groundY;

        playerIdleFrame++;
        enemyIdleFrame++;

        if (playerFlash > 0) playerFlash--;
        if (enemyFlash > 0) enemyFlash--;

        // Touch button layout (ergonomic arcade layout)
        btnSize = h * 0.135f;
        float padY = h * 0.045f;
        btnLeftX = w * 0.04f; btnLeftY = padY;
        btnRightX = w * 0.17f; btnRightY = padY;

        btnAtkX = w * 0.65f; btnAtkY = padY;
        btnSplX = w * 0.77f; btnSplY = padY + btnSize * 0.45f;
        btnBlkX = w * 0.88f; btnBlkY = padY;

        // Handle touch input
        boolean touchLeft = false, touchRight = false;
        boolean touchAttack = false, touchSpecial = false, touchBlock = false;

        for (int i = 0; i < 5; i++) {
            if (Gdx.input.isTouched(i)) {
                float tx = Gdx.input.getX(i);
                float ty = Gdx.graphics.getHeight() - Gdx.input.getY(i);

                if (tx >= btnLeftX && tx <= btnLeftX + btnSize && ty >= btnLeftY && ty <= btnLeftY + btnSize) touchLeft = true;
                if (tx >= btnRightX && tx <= btnRightX + btnSize && ty >= btnRightY && ty <= btnRightY + btnSize) touchRight = true;
                if (tx >= btnAtkX && tx <= btnAtkX + btnSize && ty >= btnAtkY && ty <= btnAtkY + btnSize) touchAttack = true;
                if (tx >= btnSplX && tx <= btnSplX + btnSize && ty >= btnSplY && ty <= btnSplY + btnSize) touchSpecial = true;
                if (tx >= btnBlkX && tx <= btnBlkX + btnSize && ty >= btnBlkY && ty <= btnBlkY + btnSize) touchBlock = true;
            }
        }

        boolean left = Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.LEFT) || Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.A) || touchLeft;
        boolean right = Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.RIGHT) || Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.D) || touchRight;
        boolean attack = Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.J) || (touchAttack && !attackWasPressed);
        boolean special = Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.K) || (touchSpecial && !specialWasPressed);
        boolean block = Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.L) || touchBlock;

        if (touchAttack && !attackWasPressed) attack = true;
        if (touchSpecial && !specialWasPressed) special = true;
        attackWasPressed = touchAttack;
        specialWasPressed = touchSpecial;

        playerBlock = block && !playerState.startsWith("ATTACK") && !playerState.equals("SPECIAL") && !playerState.equals("HIT");

        if (playerInvuln > 0) playerInvuln--;
        if (playerSpecialCooldown > 0) playerSpecialCooldown--;
        if (playerComboTimer > 0) {
            playerComboTimer--;
            if (playerComboTimer <= 0) playerComboCount = 0;
        }

        // Auto-facing: turn toward enemy when neutral
        if (playerState.equals("IDLE") || playerState.equals("WALK")) {
            playerFacing = (playerX <= enemyX) ? 1 : -1;
        }

        float charW = h * 0.52f;
        float hitH = h * 0.35f;

        // Movement with footsteps
        if (!playerBlock && (playerState.equals("IDLE") || playerState.equals("WALK"))) {
            if (left) {
                playerVX = -5f;
                playerState = "WALK";
                if (playerIdleFrame % 14 == 0) spawnParticles(playerX, playerY + 4, 0x00bcd4, 2);
            } else if (right) {
                playerVX = 5f;
                playerState = "WALK";
                if (playerIdleFrame % 14 == 0) spawnParticles(playerX, playerY + 4, 0x00bcd4, 2);
            } else {
                playerVX = 0;
                playerState = "IDLE";
            }
        }

        // Player state machine with kinetic animation lunges
        if (playerState.startsWith("ATTACK")) {
            playerAnimTimer++;
            // Kinetic forward surge during startup
            if (playerAnimTimer <= 5) {
                playerVX = playerFacing * 2.5f;
            } else {
                playerVX = 0;
            }

            if (playerAnimTimer == 5) {
                float hitX = playerFacing > 0 ? playerX + charW * 0.2f : playerX - charW * 0.5f;
                int dmg = playerState.equals("ATTACK2") ? 10 : (playerState.equals("ATTACK3") ? 14 : 7);
                playerHitbox = new Hitbox(hitX, playerY, charW * 0.45f, hitH, dmg, 16);
            }
            if (playerAnimTimer == 13) playerHitbox = null;
            if (playerAnimTimer > 19) { playerState = "IDLE"; playerAnimTimer = 0; }
        } else if (playerState.equals("SPECIAL")) {
            playerAnimTimer++;
            // Charging particles gathering at hands
            if (playerAnimTimer < 10) {
                float handX = playerX + playerFacing * (charW * 0.30f);
                float handY = playerY + charW * 0.35f;
                particles.add(new Particle(
                    handX + (rand.nextFloat() - 0.5f) * 35,
                    handY + (rand.nextFloat() - 0.5f) * 35,
                    (handX - (handX + (rand.nextFloat() - 0.5f) * 35)) * 0.2f,
                    (handY - (handY + (rand.nextFloat() - 0.5f) * 35)) * 0.2f,
                    0x00e5ff, 4, 15
                ));
                playerVX = 0;
            } else if (playerAnimTimer == 10) {
                float projX = playerX + playerFacing * (charW * 0.35f);
                float projY = playerY + charW * 0.35f;
                projectiles.add(new Projectile(projX, projY, playerFacing * 10f, 18));
                game.playSound(game.sndSpecialFire, 0.75f);
                // Blast recoil impulse!
                playerVX = -playerFacing * 4f;
            } else {
                playerVX *= 0.8f;
            }
            if (playerAnimTimer > 30) { playerState = "IDLE"; playerAnimTimer = 0; }
        } else if (playerState.equals("HIT")) {
            playerAnimTimer++;
            playerVX *= 0.85f;
            if (playerAnimTimer > 16) { playerState = "IDLE"; playerAnimTimer = 0; }
        } else {
            if (attack && !playerAttackHeld) {
                playerAttackHeld = true;
                if (playerComboCount == 0) {
                    playerState = "ATTACK1"; playerAnimTimer = 0; playerComboCount = 1; playerComboTimer = 40;
                } else if (playerComboCount == 1 && playerComboTimer > 0) {
                    playerState = "ATTACK2"; playerAnimTimer = 0; playerComboCount = 2; playerComboTimer = 40;
                } else {
                    playerState = "ATTACK3"; playerAnimTimer = 0; playerComboCount = 3; playerComboTimer = 40;
                }
            }
            if (!attack) playerAttackHeld = false;

            if (special && !playerSpecialHeld && playerSpecialCooldown == 0) {
                playerSpecialHeld = true;
                playerState = "SPECIAL"; playerAnimTimer = 0; playerSpecialCooldown = 80;
            }
            if (!special) playerSpecialHeld = false;
        }

        playerX += playerVX;

        // Enemy AI & Animation states
        if (enemyInvuln > 0) enemyInvuln--;
        enemyAttackTimer--;

        float distToPlayer = playerX - enemyX;
        enemyFacing = distToPlayer >= 0 ? 1 : -1;

        if (enemyState.equals("IDLE") || enemyState.equals("WALK")) {
            if (Math.abs(distToPlayer) > charW * 0.45f) {
                enemyVX = enemyFacing * 2.4f;
                enemyState = "WALK";
                if (enemyIdleFrame % 14 == 0) spawnParticles(enemyX, enemyY + 4, 0xff1744, 2);
            } else {
                enemyVX = 0;
                enemyState = "IDLE";
                if (enemyAttackTimer <= 0) {
                    enemyState = "ATTACK"; enemyAnimTimer = 0;
                    enemyAttackTimer = 55 + rand.nextInt(35);
                }
            }
        } else if (enemyState.equals("ATTACK")) {
            enemyAnimTimer++;
            // Enemy forward step into strike
            if (enemyAnimTimer >= 10 && enemyAnimTimer <= 16) {
                enemyVX = enemyFacing * 3.0f;
            } else {
                enemyVX = 0;
            }

            if (enemyAnimTimer == 15) {
                float hitX = enemyFacing > 0 ? enemyX + charW * 0.2f : enemyX - charW * 0.5f;
                enemyHitbox = new Hitbox(hitX, enemyY, charW * 0.45f, hitH, 10, 20);
                game.playSound(game.sndHitPunch, 0.7f);
            }
            if (enemyAnimTimer == 28) enemyHitbox = null;
            if (enemyAnimTimer > 38) { enemyState = "IDLE"; enemyAnimTimer = 0; }
        } else if (enemyState.equals("HIT")) {
            enemyAnimTimer++;
            enemyVX *= 0.85f;
            if (enemyAnimTimer > 15) { enemyState = "IDLE"; enemyAnimTimer = 0; }
        }

        enemyX += enemyVX;

        // Corner boundaries and stage clamping
        float halfVisibleW = (w * camZoom) / 2.0f;
        float leftCorner = Math.max(charW * 0.35f, camX - halfVisibleW + charW * 0.25f);
        float rightCorner = Math.min(stageWidth - charW * 0.35f, camX + halfVisibleW - charW * 0.25f);

        playerX = Math.max(leftCorner, Math.min(rightCorner, playerX));
        enemyX = Math.max(leftCorner, Math.min(rightCorner, enemyX));

        // Projectiles update
        Iterator<Projectile> pit = projectiles.iterator();
        while (pit.hasNext()) {
            Projectile p = pit.next();
            p.x += p.vx;
            float hitBoxW = charW * 0.3f;
            if (p.active && Math.abs(p.x - enemyX) < hitBoxW && p.y >= enemyY && p.y <= enemyY + charW * 0.7f) {
                if (enemyInvuln == 0) {
                    enemyHP -= p.damage;
                    enemyState = "HIT"; enemyAnimTimer = 0; enemyInvuln = 12; enemyFlash = 10;
                    game.playSound(game.sndDamageGlitch, 0.7f);
                    spawnCodeBurst(enemyX, enemyY + charW * 0.4f);
                    hitPause = 8; shakeTimer = 10;
                    comboCount++; comboTimer = 65; comboX = enemyX; comboY = enemyY + charW * 0.8f;
                    comboScale = 1.5f;
                    totalDamageDealt += p.damage;
                    if (comboCount > maxCombo) maxCombo = comboCount;
                }
                p.active = false;
            }
            if (p.x < 0 || p.x > stageWidth || !p.active) pit.remove();
        }

        // Player hits enemy
        if (playerHitbox != null && enemyInvuln == 0) {
            float enemyLeft = enemyX - charW * 0.25f;
            float enemyRight = enemyX + charW * 0.25f;
            float enemyBottom = enemyY;
            float enemyTop = enemyY + charW * 0.75f;

            if (playerHitbox.x < enemyRight && playerHitbox.x + playerHitbox.w > enemyLeft &&
                playerHitbox.y < enemyTop && playerHitbox.y + playerHitbox.h > enemyBottom) {
                enemyHP -= playerHitbox.damage;
                enemyState = "HIT"; enemyAnimTimer = 0; enemyInvuln = playerHitbox.hitstun; enemyFlash = 8;
                game.playSound(game.sndHitPunch, 0.8f);
                spawnParticles(enemyX, enemyY + charW * 0.45f, 0x00ffff, 12);
                spawnCodeBurst(enemyX, enemyY + charW * 0.4f);
                hitPause = 6; shakeTimer = 7;
                comboCount++; comboTimer = 65; comboX = enemyX; comboY = enemyY + charW * 0.8f;
                comboScale = 1.4f;
                totalDamageDealt += playerHitbox.damage;
                if (comboCount > maxCombo) maxCombo = comboCount;
                playerHitbox = null;
            }
        }

        // Enemy hits player
        if (enemyHitbox != null && playerInvuln == 0) {
            float playerLeft = playerX - charW * 0.25f;
            float playerRight = playerX + charW * 0.25f;
            float playerBottom = playerY;
            float playerTop = playerY + charW * 0.75f;

            if (enemyHitbox.x < playerRight && enemyHitbox.x + enemyHitbox.w > playerLeft &&
                enemyHitbox.y < playerTop && enemyHitbox.y + enemyHitbox.h > playerBottom) {
                if (playerBlock) {
                    playerHP -= enemyHitbox.damage * 0.3f;
                    game.playSound(game.sndBlockShield, 0.6f);
                    spawnParticles(playerX, playerY + charW * 0.45f, 0xffff00, 6);
                } else {
                    playerHP -= enemyHitbox.damage;
                    playerState = "HIT"; playerAnimTimer = 0; playerInvuln = enemyHitbox.hitstun; playerFlash = 10;
                    game.playSound(game.sndDamageGlitch, 0.7f);
                    spawnParticles(playerX, playerY + charW * 0.45f, 0xff4444, 10);
                    hitPause = 6; shakeTimer = 8;
                    comboCount = 0;
                }
                enemyHitbox = null;
            }
        }

        // Particles
        Iterator<Particle> partIt = particles.iterator();
        while (partIt.hasNext()) {
            Particle p = partIt.next();
            p.x += p.vx; p.y += p.vy; p.vy += 0.15f; p.life--;
            if (p.life <= 0) partIt.remove();
        }
        if (comboTimer > 0) comboTimer--;
        if (comboScale > 1f) comboScale -= 0.02f;

        // Win/Lose condition
        if (enemyHP <= 0) {
            enemyHP = 0;
            game.progress.markDefeated("wannacry");
            game.winScreen.setStats(fightTime, maxCombo, totalDamageDealt);
            game.setScreen(game.winScreen);
        }
        if (playerHP <= 0) {
            playerHP = 0;
            game.setScreen(game.loseScreen);
        }
    }

    void drawFightWorld(float w, float h) {
        Gdx.gl.glClearColor(0.02f, 0.02f, 0.05f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Apply world camera (handles panning and dynamic zoom)
        game.batch.setProjectionMatrix(worldCamera.combined);
        game.shapeRenderer.setProjectionMatrix(worldCamera.combined);

        // Parallax background (spans stage width)
        game.batch.begin();
        if (game.texBgFight != null) {
            float bgW = stageWidth * 1.15f;
            float bgX = -stageWidth * 0.075f;
            game.batch.draw(game.texBgFight, bgX, 0, bgW, h);
        }
        game.batch.end();

        // Cyberpunk Floor (Street Fighter style floor grid below groundY)
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        // Floor base dark fill
        game.shapeRenderer.setColor(0.04f, 0.05f, 0.09f, 0.95f);
        game.shapeRenderer.rect(0, 0, stageWidth, groundY);
        // Stage glow horizon line
        game.shapeRenderer.setColor(0.0f, 0.75f, 1.0f, 0.85f);
        game.shapeRenderer.rect(0, groundY - 3, stageWidth, 4);
        game.shapeRenderer.end();

        // Floor perspective grid lines (giving authentic 3D depth to the floor plane)
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        game.shapeRenderer.setColor(0.0f, 0.45f, 0.75f, 0.4f);
        for (float gy = groundY * 0.25f; gy < groundY; gy += groundY * 0.25f) {
            game.shapeRenderer.line(0, gy, stageWidth, gy);
        }
        for (float gx = 0; gx < stageWidth; gx += 80) {
            game.shapeRenderer.line(gx, groundY, gx - 40, 0);
        }
        game.shapeRenderer.end();

        // Ground shadow under each fighter (firmly on the floor)
        float charH = h * 0.52f;
        float charW = charH;
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        game.shapeRenderer.setColor(0, 0, 0, 0.55f);
        game.shapeRenderer.ellipse(playerX - charW * 0.22f, groundY - h * 0.015f, charW * 0.44f, h * 0.035f);
        game.shapeRenderer.ellipse(enemyX - charW * 0.22f, groundY - h * 0.015f, charW * 0.44f, h * 0.035f);
        game.shapeRenderer.end();

        // Draw Player Sprite (half of screen height!)
        drawPlayerSprite(playerX, playerY, charW, charH);

        // Draw Enemy Sprite (half of screen height!)
        drawEnemySprite(enemyX, enemyY, charW, charH);

        // Projectiles
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (Projectile p : projectiles) {
            game.shapeRenderer.setColor(0.31f, 0.76f, 0.97f, 1);
            game.shapeRenderer.circle(p.x, p.y, h * 0.024f, 16);
            game.shapeRenderer.setColor(1f, 1f, 1f, 0.9f);
            game.shapeRenderer.circle(p.x, p.y, h * 0.012f, 12);
        }
        game.shapeRenderer.end();

        // Particles & code bursts
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (Particle p : particles) {
            if (!p.isText) {
                float alpha = p.life / (float) p.maxLife;
                float r = ((p.color >> 16) & 0xFF) / 255f;
                float g = ((p.color >> 8) & 0xFF) / 255f;
                float b = (p.color & 0xFF) / 255f;
                game.shapeRenderer.setColor(r, g, b, alpha);
                game.shapeRenderer.rect(p.x - p.size / 2, p.y - p.size / 2, p.size, p.size);
            }
        }
        game.shapeRenderer.end();

        // Text particles
        game.batch.begin();
        for (Particle p : particles) {
            if (p.isText && p.text != null) {
                float alpha = p.life / (float) p.maxLife;
                game.fontSmall.setColor(0.31f, 0.76f, 0.97f, alpha);
                game.fontSmall.draw(game.batch, p.text, p.x, p.y);
            }
        }
        game.batch.end();

        // In-world Combo banner near the hit
        if (comboTimer > 0 && comboCount > 1) {
            game.batch.begin();
            float alpha = Math.min(1.0f, comboTimer / 45f);
            game.fontBold.setColor(1, 0.92f, 0.23f, alpha);
            game.fontBold.getData().setScale(comboScale * 1.3f);
            game.drawTextCentered(game.fontBold, comboCount + " HITS!", comboX, comboY);
            game.fontBold.getData().setScale(1.55f);
            game.batch.end();
        }
    }

    void drawPlayerSprite(float x, float y, float w, float h) {
        Texture currentTex = game.texPlayerIdle;
        float drawW = w;
        float drawH = h;
        float drawY = y;

        if (playerState.equals("ATTACK1")) {
            if (playerAnimTimer < 5) currentTex = game.texPlayerAttack1;
            else if (playerAnimTimer <= 13) currentTex = game.texPlayerAttack2;
            else currentTex = game.texPlayerAttack1;
        } else if (playerState.equals("ATTACK2")) {
            if (playerAnimTimer < 5) currentTex = game.texPlayerAttack2;
            else if (playerAnimTimer <= 13) currentTex = game.texPlayerAttack1;
            else currentTex = game.texPlayerAttack2;
        } else if (playerState.equals("ATTACK3")) {
            if (playerAnimTimer < 5) currentTex = game.texPlayerAttack1;
            else if (playerAnimTimer <= 14) currentTex = game.texPlayerAttack2;
            else currentTex = game.texPlayerAttack1;
        } else if (playerState.equals("SPECIAL")) {
            if (playerAnimTimer < 10) currentTex = game.texPlayerAttack1;
            else if (playerAnimTimer <= 22) currentTex = game.texPlayerAttack2;
            else currentTex = game.texPlayerAttack1;
        } else if (playerState.equals("HIT")) {
            currentTex = (game.texPlayerAttack1 != null) ? game.texPlayerAttack1 : game.texPlayerIdle;
            drawY += (float) Math.sin(playerAnimTimer * 0.8f) * 6f;
        } else if (playerState.equals("WALK")) {
            // Footstep stride bobbing
            boolean frame2 = ((playerIdleFrame / 12) % 2 == 1);
            currentTex = (frame2 && game.texPlayerIdle2 != null) ? game.texPlayerIdle2 : game.texPlayerIdle;
            float stride = (float) Math.sin(playerIdleFrame * 0.28f);
            drawY = y + Math.abs(stride) * (h * 0.022f);
        } else {
            // Dynamic breathing idle loop (alternating frames + organic breathing)
            boolean frame2 = ((playerIdleFrame / 20) % 2 == 1);
            currentTex = (frame2 && game.texPlayerIdle2 != null) ? game.texPlayerIdle2 : game.texPlayerIdle;
            float breath = (float) Math.sin(playerIdleFrame * 0.09f);
            drawH = h * (1.0f + breath * 0.020f);
            drawW = w * (1.0f - breath * 0.012f);
            drawY = y + breath * (h * 0.005f);
        }

        if (currentTex == null) currentTex = game.texPlayerIdle;

        game.batch.begin();
        if (playerFlash > 0) {
            game.batch.setColor(1f, 0.35f, 0.35f, 1f);
        } else {
            game.batch.setColor(1f, 1f, 1f, 1f);
        }

        float drawX = x - drawW / 2f;

        if (currentTex != null) {
            if (playerFacing < 0) {
                game.batch.draw(currentTex, drawX, drawY, drawW, drawH,
                    0, 0, currentTex.getWidth(), currentTex.getHeight(), true, false);
            } else {
                game.batch.draw(currentTex, drawX, drawY, drawW, drawH);
            }
        }
        game.batch.setColor(1f, 1f, 1f, 1f);
        game.batch.end();

        // Shield barrier effect when blocking
        if (playerBlock) {
            game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            game.shapeRenderer.setColor(0f, 0.85f, 1f, 0.40f);
            float shieldX = (playerFacing > 0) ? x + w * 0.1f : x - w * 0.5f;
            game.shapeRenderer.rect(shieldX, y, w * 0.4f, h * 0.88f);
            game.shapeRenderer.end();

            game.shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            game.shapeRenderer.setColor(0.31f, 0.95f, 1f, 0.9f);
            game.shapeRenderer.rect(shieldX, y, w * 0.4f, h * 0.88f);
            game.shapeRenderer.end();
        }
    }

    void drawEnemySprite(float x, float y, float w, float h) {
        Texture currentTex = game.texEnemyIdle;
        float drawW = w;
        float drawH = h;
        float drawY = y;

        if (enemyState.equals("ATTACK")) {
            if (enemyAnimTimer < 15) {
                currentTex = (game.texEnemyAttack1 != null) ? game.texEnemyAttack1 : game.texEnemyIdle;
            } else if (enemyAnimTimer <= 28) {
                currentTex = (game.texEnemyAttack2 != null) ? game.texEnemyAttack2 : game.texEnemyAttack1;
            } else {
                currentTex = (game.texEnemyAttack1 != null) ? game.texEnemyAttack1 : game.texEnemyIdle;
            }
        } else if (enemyState.equals("HIT")) {
            currentTex = (game.texEnemyAttack1 != null) ? game.texEnemyAttack1 : game.texEnemyIdle;
            drawY += (float) Math.sin(enemyAnimTimer * 0.8f) * 6f;
        } else if (enemyState.equals("WALK")) {
            boolean frame2 = ((enemyIdleFrame / 12) % 2 == 1);
            currentTex = (frame2 && game.texEnemyIdle2 != null) ? game.texEnemyIdle2 : game.texEnemyIdle;
            float stride = (float) Math.sin(enemyIdleFrame * 0.26f);
            drawY = y + Math.abs(stride) * (h * 0.020f);
        } else {
            // Idle loop: 2-frame sprite cycle + subtle cybernetic breathing sway
            boolean frame2 = ((enemyIdleFrame / 20) % 2 == 1);
            currentTex = (frame2 && game.texEnemyIdle2 != null) ? game.texEnemyIdle2 : game.texEnemyIdle;
            float breath = (float) Math.sin(enemyIdleFrame * 0.08f);
            drawH = h * (1.0f + breath * 0.022f);
            drawW = w * (1.0f - breath * 0.014f);
            drawY = y + breath * (h * 0.006f);
        }

        if (currentTex == null) currentTex = game.texEnemyIdle;

        game.batch.begin();
        if (enemyFlash > 0) {
            game.batch.setColor(1f, 1f, 1f, 0.7f);
        } else {
            game.batch.setColor(1f, 1f, 1f, 1f);
        }

        float drawX = x - drawW / 2f;

        if (currentTex != null) {
            if (enemyFacing > 0) {
                game.batch.draw(currentTex, drawX, drawY, drawW, drawH,
                    0, 0, currentTex.getWidth(), currentTex.getHeight(), true, false);
            } else {
                game.batch.draw(currentTex, drawX, drawY, drawW, drawH);
            }
        }
        game.batch.setColor(1f, 1f, 1f, 1f);
        game.batch.end();
    }

    void drawFightHUD(float w, float h) {
        // Reset projection matrix to fixed screen space
        game.batch.setProjectionMatrix(hudCamera.combined);
        game.shapeRenderer.setProjectionMatrix(hudCamera.combined);

        // Street Fighter 6 style top bar
        drawSF6TopBar(w, h);

        // Ergonomic touch controls
        drawTouchControls(w, h);

        // Menu / Back button in top-left
        float mBtnW = w * 0.11f;
        float mBtnH = h * 0.055f;
        drawButton(w * 0.02f, h * 0.92f, mBtnW, mBtnH, "MENU", 0x546e7a, 0x263238);
        if (isButtonClicked(w * 0.02f, h * 0.92f, mBtnW, mBtnH)) {
            game.setScreen(game.menuScreen);
        }
    }

    void drawSF6TopBar(float w, float h) {
        float barH = h * 0.042f;
        float barMaxW = w * 0.38f;
        float barY = h * 0.88f;

        float pBarX = w * 0.10f;
        float eBarX = w * 0.52f;

        // --- Player 1 Health Bar (Left) ---
        // Background container
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        game.shapeRenderer.setColor(0.12f, 0.12f, 0.15f, 0.9f);
        game.shapeRenderer.rect(pBarX, barY, barMaxW, barH);

        // Health fill (cyan/magenta SF6 gradient look)
        float pFillW = barMaxW * (Math.max(0, playerHP) / 100f);
        game.shapeRenderer.setColor(0.0f, 0.85f, 1.0f, 1.0f);
        game.shapeRenderer.rect(pBarX + 2, barY + 2, pFillW - 4, barH - 4);
        game.shapeRenderer.end();

        // Border
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        game.shapeRenderer.setColor(0.31f, 0.95f, 1.0f, 1f);
        game.shapeRenderer.rect(pBarX, barY, barMaxW, barH);
        game.shapeRenderer.end();

        // Player Name & HP Numbers
        game.batch.begin();
        game.fontBold.setColor(0.31f, 0.95f, 1.0f, 1f);
        game.fontBold.draw(game.batch, "CAT", pBarX, barY + barH + 24);

        game.fontSmall.setColor(1f, 1f, 1f, 0.95f);
        game.fontSmall.draw(game.batch, playerHP + " HP", pBarX + 10, barY + barH * 0.7f);
        game.batch.end();

        // --- Center VS / Round Badge ---
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        game.shapeRenderer.setColor(0.08f, 0.08f, 0.12f, 0.9f);
        float badgeW = w * 0.07f;
        float badgeH = barH * 1.4f;
        game.shapeRenderer.rect(w / 2f - badgeW / 2f, barY - (badgeH - barH) / 2f, badgeW, badgeH);
        game.shapeRenderer.end();
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        game.shapeRenderer.setColor(1f, 0.85f, 0.1f, 1f);
        game.shapeRenderer.rect(w / 2f - badgeW / 2f, barY - (badgeH - barH) / 2f, badgeW, badgeH);
        game.shapeRenderer.end();

        game.batch.begin();
        game.fontBold.setColor(1f, 0.92f, 0.2f, 1f);
        game.drawTextCentered(game.fontBold, "VS", w / 2f, barY + barH / 2f);
        game.batch.end();

        // --- Enemy Health Bar (Right) ---
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        game.shapeRenderer.setColor(0.12f, 0.12f, 0.15f, 0.9f);
        game.shapeRenderer.rect(eBarX, barY, barMaxW, barH);

        // Fill from right to left
        float eFillW = barMaxW * (Math.max(0, enemyHP) / 100f);
        game.shapeRenderer.setColor(1.0f, 0.08f, 0.25f, 1.0f);
        game.shapeRenderer.rect(eBarX + barMaxW - eFillW + 2, barY + 2, eFillW - 4, barH - 4);
        game.shapeRenderer.end();

        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        game.shapeRenderer.setColor(1.0f, 0.2f, 0.4f, 1f);
        game.shapeRenderer.rect(eBarX, barY, barMaxW, barH);
        game.shapeRenderer.end();

        game.batch.begin();
        game.fontBold.setColor(1.0f, 0.2f, 0.4f, 1f);
        float eNameW = game.getTextWidth(game.fontBold, "WANNACRY");
        game.fontBold.draw(game.batch, "WANNACRY", eBarX + barMaxW - eNameW, barY + barH + 24);

        game.fontSmall.setColor(1f, 1f, 1f, 0.95f);
        game.fontSmall.draw(game.batch, enemyHP + " HP", eBarX + barMaxW - 75, barY + barH * 0.7f);
        game.batch.end();
    }

    void drawTouchControls(float w, float h) {
        float size = btnSize;
        float padY = h * 0.045f;

        drawTouchBtn(w * 0.04f, padY, size, size, "LEFT", 0x00bcd4, 0x1a3344);
        drawTouchBtn(w * 0.17f, padY, size, size, "RIGHT", 0x00bcd4, 0x1a3344);

        drawTouchBtn(w * 0.65f, padY, size, size, "ATK", 0xff1744, 0x4a121d);
        drawTouchBtn(w * 0.77f, padY + size * 0.45f, size, size, "SP", 0x00e5ff, 0x0c3b52);
        drawTouchBtn(w * 0.88f, padY, size, size, "BLK", 0xffeb3b, 0x423d0a);
    }

    void drawTouchBtn(float x, float y, float bw, float bh, String label, int hoverCol, int col) {
        boolean pressed = false;
        for (int i = 0; i < 5; i++) {
            if (Gdx.input.isTouched(i)) {
                float tx = Gdx.input.getX(i);
                float ty = Gdx.graphics.getHeight() - Gdx.input.getY(i);
                if (tx >= x && tx <= x + bw && ty >= y && ty <= y + bh) {
                    pressed = true;
                    break;
                }
            }
        }

        int c = pressed ? hoverCol : col;
        float r = ((c >> 16) & 0xFF) / 255f;
        float g = ((c >> 8) & 0xFF) / 255f;
        float b = (c & 0xFF) / 255f;

        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        game.shapeRenderer.setColor(r, g, b, pressed ? 0.9f : 0.65f);
        game.shapeRenderer.rect(x, y, bw, bh);
        game.shapeRenderer.end();

        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        game.shapeRenderer.setColor(r * 1.4f, g * 1.4f, b * 1.4f, pressed ? 1f : 0.75f);
        game.shapeRenderer.rect(x, y, bw, bh);
        game.shapeRenderer.end();

        game.batch.begin();
        game.fontBold.setColor(1f, 1f, 1f, pressed ? 1.0f : 0.9f);
        game.drawTextCentered(game.fontBold, label, x + bw / 2f, y + bh / 2f);
        game.batch.end();
    }

    void drawIntroBanner(float w, float h) {
        String bannerText = (introPhase == 0) ? "ROUND 1" : "FIGHT!";
        float progress = introTimer / (introPhase == 0 ? ROUND_BANNER_DURATION : FIGHT_BANNER_DURATION);

        float scale = (progress < 0.2f) ? 1f + (1f - progress / 0.2f) * 0.5f : 1f;
        float alpha = (progress < 0.15f) ? progress / 0.15f : (progress > 0.85f) ? (1f - progress) / 0.15f : 1f;

        float barH = h * 0.18f;
        float barY = h / 2f - barH / 2f;

        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        game.shapeRenderer.setColor(0, 0, 0, alpha * 0.85f);
        game.shapeRenderer.rect(0, barY, w, barH);
        game.shapeRenderer.end();

        int lineCol = (introPhase == 0) ? 0x00bcd4 : 0xff1744;
        float lr = ((lineCol >> 16) & 0xFF) / 255f;
        float lg = ((lineCol >> 8) & 0xFF) / 255f;
        float lb = (lineCol & 0xFF) / 255f;

        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        game.shapeRenderer.setColor(lr, lg, lb, alpha);
        game.shapeRenderer.rect(0, barY + barH - 4, w, 4);
        game.shapeRenderer.rect(0, barY, w, 4);
        game.shapeRenderer.end();

        game.batch.begin();
        game.fontTitle.getData().setScale(game.fontTitle.getData().scaleX * scale);
        game.fontTitle.setColor(lr, lg, lb, alpha);
        game.drawTextCentered(game.fontTitle, bannerText, w / 2f, barY + barH / 2f);
        game.fontTitle.getData().setScale(game.fontTitle.getData().scaleX / scale);
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
        game.shapeRenderer.setColor(r, g, b, hover ? 1f : 0.75f);
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

    void spawnParticles(float x, float y, int color, int count) {
        for (int i = 0; i < count; i++) {
            particles.add(new Particle(x, y, (rand.nextFloat() - 0.5f) * 8, (rand.nextFloat() - 0.5f) * 8, color, 3 + rand.nextInt(5), 30 + rand.nextInt(20)));
        }
    }

    void spawnCodeBurst(float x, float y) {
        String[] codes = {"0xFF", "ERR", "NULL", "BUG", "//", "{}", "[]", "&&", "||", "!="};
        for (int i = 0; i < 6; i++) {
            particles.add(new Particle(x, y, (rand.nextFloat() - 0.5f) * 5, 2 + rand.nextFloat() * 4, 0x00ffff, 14, 45, codes[rand.nextInt(codes.length)], true));
        }
    }

    @Override public void resize(int width, int height) {
        game.updateFontScales();
        resetFight();
    }
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override public void dispose() {}

    class Hitbox {
        float x, y, w, h;
        int damage, hitstun;
        Hitbox(float x, float y, float w, float h, int damage, int hitstun) {
            this.x = x; this.y = y; this.w = w; this.h = h;
            this.damage = damage; this.hitstun = hitstun;
        }
    }

    class Projectile {
        float x, y, vx;
        int damage;
        boolean active = true;
        Projectile(float x, float y, float vx, int damage) {
            this.x = x; this.y = y; this.vx = vx; this.damage = damage;
        }
    }

    class Particle {
        float x, y, vx, vy, size;
        int color, life, maxLife;
        String text;
        boolean isText;
        Particle(float x, float y, float vx, float vy, int color, float size, int life) {
            this.x = x; this.y = y; this.vx = vx; this.vy = vy;
            this.color = color; this.size = size; this.life = life; this.maxLife = life;
            this.isText = false;
        }
        Particle(float x, float y, float vx, float vy, int color, float size, int life, String text, boolean isText) {
            this(x, y, vx, vy, color, size, life);
            this.text = text; this.isText = isText;
        }
    }
}
