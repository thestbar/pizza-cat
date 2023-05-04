package com.junkiedan.ludumdare53;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.junkiedan.ludumdare53.ui.AmmoUI;
import com.junkiedan.ludumdare53.ui.ClockUI;
import jdk.tools.jmod.Main;

public class GameOverScreen implements Screen{
    private final LudumDare53 game;
    private final TextButton playAgainButton;
    private final TextButton mainMenuButton;

    public GameOverScreen(LudumDare53 game, int score) {
        this.game = game;

        game.skin = new Skin(Gdx.files.internal("ui/default/uiskin.json"));
        game.skin.getFont("default-font").getData().setScale(2);
        game.stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(game.stage);

        Table root = new Table();
        root.setFillParent(true);
        game.stage.addActor(root);

//        root.setDebug(true);

        Label label = new Label("Game Over", game.skin);
        root.add(label).padTop(200);

        root.row();

        label = new Label("You delivered " + score + " pizzas!", game.skin);
        root.add(label).padTop(50);

        root.row();

        playAgainButton = new TextButton("Play Again", game.skin);
        root.add(playAgainButton).padTop(50);

        root.row();

        mainMenuButton = new TextButton("Main Menu", game.skin);
        root.add(mainMenuButton).expandY().top().padTop(50);

        game.soundManager.stop();
        game.soundManager.gameOverSound.play();
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0, 1);

        game.stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        game.stage.draw();

        if(playAgainButton.isPressed()) {
            game.soundManager.buttonSound.play();
            game.setScreen(new GameScreen(game));
        }

        if(mainMenuButton.isPressed()) {
            game.soundManager.buttonSound.play();
            game.setScreen(new MainMenuScreen(game));
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
