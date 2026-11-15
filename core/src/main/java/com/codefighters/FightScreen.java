package com.codefighters;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class FightScreen implements Screen {
    final CodeFightersGame game;
    Random rand = new Random();

    // Player
    float playerX = 200, playerY = 380;
    float playerVX = 0;
    int playerHP = 100, playerMaxHP = 100;
    String playerState = "IDLE";
    int playerAnimTimer = 0;
    int playerComboCount = 0;
    int playerComboTimer = 0;
    int playerInvuln = 0;
    boolean playerBlock = false;
    int playerSpecialCooldown = 0;
    boolean playerAttackHeld = false;
    boolean playerSpecialHeld = false;
    float playerFacing = 1;
    Hitbox playerHitbox = null;

    // Enemy
    float enemyX = 700, enemyY = 380;
    float enemyVX = 0;
    int enemyHP = 100, enemyMaxHP = 100;
    String enemyState = "IDLE";
    int enemyAnimTimer = 0;
    float enemyFacing = -1;
    Hitbox enemyHitbox = null;
    int enemyInvuln = 0;
    int enemyAttackTimer = 60;

    // Projectiles
    List<Projectile> projectiles = new ArrayList<>();

    // Particles
    List<Particle> particles = new ArrayList<>();

    // Combo display
    int comboCount = 0;
    int comboTimer = 0;
    float comboX = 0, comboY = 0;

    // Hit pause
    int hitPause = 0;
    int frame = 0;

    // Touch buttons
    boolean touchLeft = false, touchRight = false;
    boolean touchAttack = false, touchSpecial = false, touchBlock = false;

    public FightScreen(final CodeFightersGame game) {
        this.game = game;
    }

    public void resetFight() {
        playerX = 200; playerY = 380; playerHP = 100; playerVX = 0;
        playerState = "IDLE"; playerComboCount = 0; playerInvuln = 0;
        playerBlock = false; playerSpecialCooldown = 0;
        enemyX = 700; enemyY = 380; enemyHP = 100; enemyVX = 0;
        enemyState = "IDLE"; enemyInvuln = 0; enemyAttackTimer = 60;
        projectiles.clear(); particles.clear();
        comboCount = 0; comboTimer = 0; hitPause = 0; frame = 0;
    }

    @Override
    public void render(float delta) {
        if (hitPause > 0) {
            hitPause--;
            drawFight();
            return;
        }
        frame++;
        updateFight();
        drawFight();
    }

    void updateFight() {
        float w = Gdx.graphics.getWidth();
        float groundY = Gdx.graphics.getHeight() * 0.65f;
        playerY = groundY;
        enemyY = groundY;

        // Input
        boolean left = Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.LEFT) || touchLeft;
        boolean right = Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.RIGHT) || touchRight;
        boolean attack = Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.A) || touchAttack;
        boolean special = Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.S) || touchSpecial;
        boolean block = Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.D) || touchBlock;

        if (attack) touchAttack = false;
        if (special) touchSpecial = false;

        playerBlock = block && !playerState.startsWith("ATTACK") && !playerState.equals("SPECIAL") && !playerState.equals("HIT");

        if (playerInvuln > 0) playerInvuln--;
        if (playerSpecialCooldown > 0) playerSpecialCooldown--;
        if (playerComboTimer > 0) {
            playerComboTimer--;
            if (playerComboTimer <= 0) playerComboCount = 0;
        }

        // Player movement
        if (!playerBlock && (playerState.equals("IDLE") || playerState.equals("WALK"))) {
            if (left) { playerVX = -3; playerFacing = -1; playerState = "WALK"; }
            else if (right) { playerVX = 3; playerFacing = 1; playerState = "WALK"; }
            else { playerVX = 0; playerState = "IDLE"; }
        }

        // Player state machine
        if (playerState.equals("ATTACK1") || playerState.equals("ATTACK2") || playerState.equals("ATTACK3")) {
            playerAnimTimer++;
            playerVX = 0;
            if (playerAnimTimer == 5) {
                playerHitbox = new Hitbox(playerX + playerFacing * 40, playerY - 60, 50, 50, 5, 15);
            }
            if (playerAnimTimer == 12) playerHitbox = null;
            if (playerAnimTimer > 20) { playerState = "IDLE"; playerAnimTimer = 0; }
        } else if (playerState.equals("SPECIAL")) {
            playerAnimTimer++;
            playerVX = 0;
            if (playerAnimTimer == 10) {
                projectiles.add(new Projectile(playerX + playerFacing * 40, playerY - 60, playerFacing * 6, 15));
            }
            if (playerAnimTimer > 35) { playerState = "IDLE"; playerAnimTimer = 0; }
        } else if (playerState.equals("HIT")) {
            playerAnimTimer++;
            if (playerAnimTimer > 20) { playerState = "IDLE"; playerAnimTimer = 0; }
        } else {
            // Trigger attacks
            if (attack && !playerAttackHeld) {
                playerAttackHeld = true;
                if (playerComboCount == 0) {
                    playerState = "ATTACK1"; playerAnimTimer = 0; playerComboCount = 1; playerComboTimer = 40;
                } else if (playerComboCount == 1 && playerComboTimer > 0) {
                    playerState = "ATTACK2"; playerAnimTimer = 0; playerComboCount = 2; playerComboTimer = 40;
                } else if (playerComboCount == 2 && playerComboTimer > 0) {
                    playerState = "ATTACK3"; playerAnimTimer = 0; playerComboCount = 3; playerComboTimer = 40;
                } else {
                    playerState = "ATTACK1"; playerAnimTimer = 0; playerComboCount = 1; playerComboTimer = 40;
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
        playerX = Math.max(50, Math.min(w - 50, playerX));

        // Enemy AI
        if (enemyInvuln > 0) enemyInvuln--;
        enemyAttackTimer--;

        float dist = playerX - enemyX;
        enemyFacing = dist > 0 ? 1 : -1;

        if (enemyState.equals("IDLE") || enemyState.equals("WALK")) {
            if (Math.abs(dist) > 80) {
                enemyVX = enemyFacing * 1.5f;
                enemyState = "WALK";
            } else {
                enemyVX = 0;
                enemyState = "IDLE";
                if (enemyAttackTimer <= 0) {
                    enemyState = "ATTACK"; enemyAnimTimer = 0;
                    enemyAttackTimer = 60 + rand.nextInt(40);
                }
            }
        } else if (enemyState.equals("ATTACK")) {
            enemyAnimTimer++; enemyVX = 0;
            if (enemyAnimTimer == 15) {
                enemyHitbox = new Hitbox(enemyX + enemyFacing * 40, enemyY - 60, 50, 60, 8, 20);
            }
            if (enemyAnimTimer == 30) enemyHitbox = null;
            if (enemyAnimTimer > 40) { enemyState = "IDLE"; enemyAnimTimer = 0; }
        } else if (enemyState.equals("HIT")) {
            enemyAnimTimer++;
            if (enemyAnimTimer > 15) { enemyState = "IDLE"; enemyAnimTimer = 0; }
        }

        enemyX += enemyVX;
        enemyX = Math.max(50, Math.min(w - 50, enemyX));

        // Projectiles
        Iterator<Projectile> pit = projectiles.iterator();
        while (pit.hasNext()) {
            Projectile p = pit.next();
            p.x += p.vx;
            if (p.active && p.x > enemyX - 40 && p.x < enemyX + 40 && p.y > enemyY - 100 && p.y < enemyY) {
                if (enemyInvuln == 0) {
                    enemyHP -= p.damage;
                    enemyState = "HIT"; enemyAnimTimer = 0; enemyInvuln = 10;
                    spawnCodeBurst(enemyX, enemyY - 50);
                    hitPause = 8;
                    comboCount++; comboTimer = 60; comboX = enemyX; comboY = enemyY - 120;
                }
                p.active = false;
            }
            if (p.x < 0 || p.x > w || !p.active) pit.remove();
        }

        // Player hits enemy
        if (playerHitbox != null && enemyInvuln == 0) {
            if (playerHitbox.x < enemyX + 30 && playerHitbox.x + playerHitbox.w > enemyX - 30 &&
                playerHitbox.y < enemyY && playerHitbox.y + playerHitbox.h > enemyY - 100) {
                if (!enemyState.equals("BLOCK")) {
                    enemyHP -= playerHitbox.damage;
                    enemyState = "HIT"; enemyAnimTimer = 0; enemyInvuln = playerHitbox.hitstun;
                    spawnParticles(enemyX, enemyY - 50, 0x00ffff, 10);
                    spawnCodeBurst(enemyX, enemyY - 60);
                    hitPause = 5;
                    comboCount++; comboTimer = 60; comboX = enemyX; comboY = enemyY - 120;
                } else {
                    enemyHP -= playerHitbox.damage * 0.2f;
                    spawnParticles(enemyX, enemyY - 50, 0xffff00, 4);
                }
                playerHitbox = null;
            }
        }

        // Enemy hits player
        if (enemyHitbox != null && playerInvuln == 0) {
            if (enemyHitbox.x < playerX + 25 && enemyHitbox.x + enemyHitbox.w > playerX - 25 &&
                enemyHitbox.y < playerY && enemyHitbox.y + enemyHitbox.h > playerY - 90) {
                if (playerBlock) {
                    playerHP -= enemyHitbox.damage * 0.3f;
                    spawnParticles(playerX, playerY - 50, 0xffff00, 4);
                } else {
                    playerHP -= enemyHitbox.damage;
                    playerState = "HIT"; playerAnimTimer = 0; playerInvuln = enemyHitbox.hitstun;
                    spawnParticles(playerX, playerY - 50, 0xff4444, 8);
                    hitPause = 6;
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

        // Win/Lose
        if (enemyHP <= 0) {
            enemyHP = 0;
            game.progress.markDefeated("wannacry");
            game.setScreen(game.winScreen);
        }
        if (playerHP <= 0) {
            playerHP = 0;
            game.setScreen(game.loseScreen);
        }
    }

    void drawFight() {
        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();
        float groundY = h * 0.65f;

        Gdx.gl.glClearColor(0.03f, 0.03f, 0.06f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Background - server room
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        game.shapeRenderer.setColor(0.03f, 0.03f, 0.06f, 1);
        game.shapeRenderer.rect(0, 0, w, h);

        // Server racks
        game.shapeRenderer.setColor(0.06f, 0.06f, 0.12f, 1);
        for (int i = 0; i < 12; i++) {
            float rx = w * 0.05f + i * (w * 0.08f);
            game.shapeRenderer.rect(rx, h * 0.2f, w * 0.05f, h * 0.5f);
        }
        game.shapeRenderer.end();

        // Blinking lights on servers
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (int i = 0; i < 12; i++) {
            for (int j = 0; j < 6; j++) {
                if ((frame + i * 3 + j * 7) % 30 < 5) {
                    game.shapeRenderer.setColor(rand.nextBoolean() ? 0 : 1, 1, rand.nextBoolean() ? 0 : 0.5f, 1);
                    float rx = w * 0.055f + i * (w * 0.08f);
                    float ry = h * 0.25f + j * (h * 0.07f);
                    game.shapeRenderer.rect(rx, ry, w * 0.008f, h * 0.01f);
                }
            }
        }
        game.shapeRenderer.end();

        // Floor
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        game.shapeRenderer.setColor(0.07f, 0.07f, 0.07f, 1);
        game.shapeRenderer.rect(0, groundY, w, h - groundY);
        game.shapeRenderer.end();

        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        game.shapeRenderer.setColor(0.13f, 0.13f, 0.13f, 1);
        for (int i = 0; i < w; i += 60) {
            game.shapeRenderer.line(i, groundY, i - w * 0.04f, h);
        }
        game.shapeRenderer.end();

        // Health bars
        drawHealthBar(w * 0.04f, h * 0.88f, w * 0.3f, h * 0.045f, playerHP, 100,
            0.31f, 0.76f, 0.97f, "YOU (Team Lead)");
        drawHealthBar(w * 0.66f, h * 0.88f, w * 0.3f, h * 0.045f, enemyHP, 100,
            1f, 0.09f, 0.27f, "WANNACRY");

        // VS
        game.batch.begin();
        game.fontBold.setColor(1, 0.92f, 0.23f, 1);
        game.fontBold.draw(game.batch, "VS", w/2 - 30, h * 0.91f);
        game.batch.end();

        // Characters
        drawPlayer(playerX, playerY, 1.2f);
        drawEnemy(enemyX, enemyY, 1.2f);

        // Projectiles
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (Projectile p : projectiles) {
            game.shapeRenderer.setColor(0.31f, 0.76f, 0.97f, 1);
            game.shapeRenderer.circle(p.x, p.y, 10, 12);
        }
        game.shapeRenderer.end();

        // Particles
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (Particle p : particles) {
            float alpha = p.life / (float)p.maxLife;
            float r = ((p.color >> 16) & 0xFF) / 255f;
            float g = ((p.color >> 8) & 0xFF) / 255f;
            float b = (p.color & 0xFF) / 255f;
            game.shapeRenderer.setColor(r, g, b, alpha);
            game.shapeRenderer.rect(p.x - p.size/2, p.y - p.size/2, p.size, p.size);
        }
        game.shapeRenderer.end();

        // Combo display
        if (comboTimer > 0 && comboCount > 1) {
            game.batch.begin();
            float alpha = comboTimer / 60f;
            game.fontBold.setColor(1, 0.92f, 0.23f, alpha);
            game.fontBold.draw(game.batch, comboCount + " HIT COMBO!", comboX - 80, comboY);
            game.batch.end();
        }

        // Touch controls
        drawTouchControls(w, h);

        // Pause/Back button
        drawButton(w * 0.02f, h * 0.02f, w * 0.12f, h * 0.06f, "MENU", 0x555555, 0x222222);
        if (isButtonClicked(w * 0.02f, h * 0.02f, w * 0.12f, h * 0.06f)) {
            game.setScreen(game.menuScreen);
        }
    }

    void drawHealthBar(float x, float y, float bw, float bh, int hp, int maxHp, float r, float g, float b, String name) {
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        game.shapeRenderer.setColor(0.2f, 0.2f, 0.2f, 1);
        game.shapeRenderer.rect(x, y, bw, bh);
        game.shapeRenderer.setColor(r, g, b, 1);
        game.shapeRenderer.rect(x + bw * 0.015f, y + bh * 0.15f, (bw * 0.97f) * (hp / (float)maxHp), bh * 0.7f);
        game.shapeRenderer.end();

        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        game.shapeRenderer.setColor(1, 1, 1, 1);
        game.shapeRenderer.rect(x, y, bw, bh);
        game.shapeRenderer.end();

        game.batch.begin();
        game.fontSmall.setColor(1, 1, 1, 1);
        game.fontSmall.draw(game.batch, name, x + 5, y + bh + 15);
        game.fontSmall.draw(game.batch, hp + "/" + maxHp, x + bw - 50, y + bh * 0.6f);
        game.batch.end();
    }

    void drawPlayer(float x, float y, float scale) {
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        // Body
        game.shapeRenderer.setColor(0.31f, 0.76f, 0.97f, 1);
        game.shapeRenderer.rect(x - 25*scale, y - 70*scale, 50*scale, 50*scale);
        // Head
        game.shapeRenderer.setColor(1, 0.8f, 0.67f, 1);
        game.shapeRenderer.rect(x - 18*scale, y - 100*scale, 36*scale, 35*scale);
        // Headphones
        game.shapeRenderer.setColor(0.3f, 0.3f, 0.3f, 1);
        game.shapeRenderer.rect(x - 22*scale, y - 105*scale, 44*scale, 8*scale);
        // Legs
        game.shapeRenderer.setColor(0.08f, 0.35f, 0.75f, 1);
        game.shapeRenderer.rect(x - 18*scale, y - 20*scale, 15*scale, 25*scale);
        game.shapeRenderer.rect(x + 3*scale, y - 20*scale, 15*scale, 25*scale);
        game.shapeRenderer.end();

        // Attack arm
        if (playerState.startsWith("ATTACK")) {
            game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            game.shapeRenderer.setColor(0.31f, 0.76f, 0.97f, 1);
            game.shapeRenderer.rect(x + playerFacing * 15*scale, y - 75*scale, playerFacing * 35*scale, 10*scale);
            game.shapeRenderer.end();
        }

        // Block shield
        if (playerBlock) {
            game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            game.shapeRenderer.setColor(1, 0.92f, 0.23f, 0.5f);
            game.shapeRenderer.rect(x + playerFacing * 30*scale, y - 80*scale, playerFacing * 15*scale, 70*scale);
            game.shapeRenderer.end();
        }
    }

    void drawEnemy(float x, float y, float scale) {
        float glitchX = (rand.nextFloat() - 0.5f) * 4;
        float glitchY = (rand.nextFloat() - 0.5f) * 4;
        x += glitchX; y += glitchY;

        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        // Body
        game.shapeRenderer.setColor(1, 0.09f, 0.27f, 0.9f);
        game.shapeRenderer.triangle(
            x, y - 90*scale,
            x + 30*scale, y - 30*scale,
            x - 30*scale, y - 30*scale
        );
        // Lock head
        game.shapeRenderer.setColor(0.8f, 0, 0, 1);
        game.shapeRenderer.rect(x - 20*scale, y - 130*scale, 40*scale, 35*scale);
        // Keyhole
        game.shapeRenderer.setColor(1, 0.92f, 0.23f, 1);
        game.shapeRenderer.circle(x, y - 115*scale, 5*scale, 12);
        game.shapeRenderer.rect(x - 3*scale, y - 115*scale, 6*scale, 12*scale);
        game.shapeRenderer.end();
    }

    void drawTouchControls(float w, float h) {
        float btnSize = h * 0.1f;
        float padY = h * 0.08f;

        // D-pad area
        drawTouchBtn(w * 0.08f, padY, btnSize, btnSize, "LEFT", 0x333333, 0x111111);
        drawTouchBtn(w * 0.18f, padY, btnSize, btnSize, "RIGHT", 0x333333, 0x111111);

        // Action buttons
        drawTouchBtn(w * 0.72f, padY, btnSize, btnSize, "ATK", 0xff1744, 0xb71c1c);
        drawTouchBtn(w * 0.82f, padY + btnSize * 0.5f, btnSize, btnSize, "SP", 0x00e5ff, 0x0091ea);
        drawTouchBtn(w * 0.90f, padY, btnSize, btnSize, "BLK", 0xffeb3b, 0xfbc02d);
    }

    void drawTouchBtn(float x, float y, float bw, float bh, String label, int hoverCol, int col) {
        float mx = Gdx.input.getX();
        float my = Gdx.graphics.getHeight() - Gdx.input.getY();
        boolean pressed = Gdx.input.isTouched() && mx >= x && mx <= x + bw && my >= y && my <= y + bh;

        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        if (pressed) {
            game.shapeRenderer.setColor(((hoverCol >> 16) & 0xFF)/255f,
                ((hoverCol >> 8) & 0xFF)/255f, (hoverCol & 0xFF)/255f, 0.8f);
        } else {
            game.shapeRenderer.setColor(((col >> 16) & 0xFF)/255f,
                ((col >> 8) & 0xFF)/255f, (col & 0xFF)/255f, 0.5f);
        }
        game.shapeRenderer.rect(x, y, bw, bh);
        game.shapeRenderer.end();

        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        game.shapeRenderer.setColor(1, 1, 1, 0.5f);
        game.shapeRenderer.rect(x, y, bw, bh);
        game.shapeRenderer.end();

        game.batch.begin();
        game.fontSmall.setColor(1, 1, 1, 0.8f);
        game.fontSmall.draw(game.batch, label, x + bw/2 - 20, y + bh/2 + 5);
        game.batch.end();
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
        game.font.draw(game.batch, text, x + bw/2 - 40, y + bh/2 + 10);
        game.batch.end();
    }

    boolean isButtonClicked(float x, float y, float w, float h) {
        float mx = Gdx.input.getX();
        float my = Gdx.graphics.getHeight() - Gdx.input.getY();
        return mx >= x && mx <= x + w && my >= y && my <= y + h && Gdx.input.justTouched();
    }

    void spawnParticles(float x, float y, int color, int count) {
        for (int i = 0; i < count; i++) {
            particles.add(new Particle(x, y, (rand.nextFloat() - 0.5f) * 6, (rand.nextFloat() - 0.5f) * 6, color, 2 + rand.nextInt(4), 30 + rand.nextInt(20)));
        }
    }

    void spawnCodeBurst(float x, float y) {
        String[] codes = {"0xFF", "ERR", "NULL", "BUG", "//", "{}", "[]", "&&", "||", "!="};
        for (int i = 0; i < 6; i++) {
            particles.add(new Particle(x, y, (rand.nextFloat() - 0.5f) * 4, -2 - rand.nextFloat() * 3, 0x00ffff, 12, 40, codes[rand.nextInt(codes.length)], true));
        }
    }

    @Override public void show() {}
    @Override public void resize(int width, int height) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override public void dispose() {}

    // Inner classes
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
