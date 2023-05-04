package com.junkiedan.ludumdare53;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class LudumDare53 extends Game {
	public SpriteBatch batch;
	public Texture img;
	public BitmapFont font;
	public ShapeRenderer shapeRenderer;
	public Stage stage;
	public Skin skin;
	public Skin smallLettersSkin;
	public SoundManager soundManager;
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		font = new BitmapFont(true);
		shapeRenderer = new ShapeRenderer();
		shapeRenderer.setAutoShapeType(true);
		img = new Texture("badlogic.jpg");
		skin = new Skin(Gdx.files.internal("ui/default/uiskin.json"));
		smallLettersSkin = new Skin(Gdx.files.internal("ui/default/uiskin.json"));

		stage = new Stage(new ScreenViewport());
		Gdx.input.setInputProcessor(stage);
		soundManager = new SoundManager();

		this.setScreen(new MainMenuScreen(this));
	}

	@Override
	public void render () {
		super.render();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		font.dispose();
		shapeRenderer.dispose();
		img.dispose();
		stage.dispose();
		skin.dispose();
		soundManager.dispose();
	}
}
