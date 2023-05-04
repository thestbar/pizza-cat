package com.junkiedan.ludumdare53;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class MainMenuScreen implements Screen {
    private final LudumDare53 game;
    private final TextButton startGameButton;
    private final TextButton instructionsButton;
    private final Label instructionsText;
    private final Table root;

    public MainMenuScreen(LudumDare53 game) {
        this.game = game;

        game.skin = new Skin(Gdx.files.internal("ui/default/uiskin.json"));
        game.skin.getFont("default-font").getData().setScale(2);
        game.smallLettersSkin.getFont("default-font").getData().setScale(1f);
        game.stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(game.stage);

        root = new Table();
        root.setFillParent(true);
        game.stage.addActor(root);

//        root.setDebug(true);

        Label label = new Label("Pizza Cat", game.skin);
        root.add(label).padTop(150);

        root.row();

        startGameButton = new TextButton("Start", game.skin);
        root.add(startGameButton).padTop(50);

        root.row();

        instructionsButton = new TextButton("Instructions", game.skin);
        root.add(instructionsButton).padTop(50);

        root.row();

        String instructionsStr = "Use W, A, S, D to navigate, E to interact with objects and Left Mouse Click to fire pizza slices!\n" +
                "\nYou are the pizza delivery guy! Your task is to go to the pizza house, grab some pizza, then move to " +
                "the house that made the delivery.\n The neighborhood is full of terrors... Well, actually not \"real\" terrors, but.. Cats, which might be worse " +
                "than terrors but whatever.\n Smart and evil cats are spawning from the trashcans of the city and they start to run after you to get your pizza." +
                "\nYou can give them some slices from the ordered pizza, but remember the more pizza slices you give to the cats\n the less time you will be rewarded " +
                "at then end of the delivery and you have to consider that time is your friend, \nbecause if you run out of time you lose. \n\nLast but not least, " +
                "if a cat runs into you then it takes 2-4 pizza slices from you. \n\nThanks a lot for your time and have fun!!!" +
                "\n\nThis game was created for LudumDare 53 by JunkieDan (2023)";
        instructionsText = new Label(instructionsStr, game.smallLettersSkin);
        instructionsText.setSize(400, 800);
        root.add(instructionsText).expand().fill().padLeft(100).padRight(100).padBottom(100);
        instructionsText.setVisible(false);

        game.soundManager.startMusic();
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0, 1);

        game.stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        game.stage.draw();

        if(startGameButton.isPressed()) {
            game.soundManager.buttonSound.play();
            game.setScreen(new GameScreen(game));
        }

        if(instructionsButton.isPressed()) {
            game.soundManager.buttonSound.play();
            instructionsButton.setVisible(false);
            instructionsText.setVisible(true);
        }
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }
}
