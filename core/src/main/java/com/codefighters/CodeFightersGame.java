package com.codefighters;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
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

    // Screen instances
    public MenuScreen menuScreen;
    public CharacterSelectScreen selectScreen;
    public FightScreen fightScreen;
    public GalleryScreen galleryScreen;
    public WinScreen winScreen;
    public LoseScreen loseScreen;

    @Override
    public void create() {
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        font = new BitmapFont();
        fontBold = new BitmapFont();
        fontSmall = new BitmapFont();
        font.getData().setScale(1.5f);
        fontBold.getData().setScale(2f);
        fontSmall.getData().setScale(1f);

        progress = new GameProgress();

        menuScreen = new MenuScreen(this);
        selectScreen = new CharacterSelectScreen(this);
        fightScreen = new FightScreen(this);
        galleryScreen = new GalleryScreen(this);
        winScreen = new WinScreen(this);
        loseScreen = new LoseScreen(this);

        setScreen(menuScreen);
    }

    @Override
    public void dispose() {
        batch.dispose();
        font.dispose();
        fontBold.dispose();
        fontSmall.dispose();
        shapeRenderer.dispose();
    }
}
