package com.junkiedan.ludumdare53;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class CatEatingPizza {
    private final Vector2 position;

    private final Animation<TextureRegion> catEatingPizzaAnimation;

    private final Texture spritesheet;

    private float stateTime;

    public CatEatingPizza(Vector2 position) {
        this.position = position;
        spritesheet = new Texture(Gdx.files.internal("spritesheet/LudumDare53_cat.png"));
        TextureRegion[][] tmp = TextureRegion.split(spritesheet, 12, 12);
        TextureRegion[] frames = new TextureRegion[5];
        for(int i = 0; i < 5; i++) {
            tmp[3][i].flip(false, true);
            frames[i] = tmp[3][i];
        }
        this.catEatingPizzaAnimation = new Animation<>(0.33f, frames);
        this.stateTime = 0;
    }

    public void render(SpriteBatch batch) {
        stateTime += Gdx.graphics.getDeltaTime();
        TextureRegion currentFrame = catEatingPizzaAnimation.getKeyFrame(stateTime, true);

        batch.begin();
        batch.draw(currentFrame, position.x, position.y, 64, 64);
        batch.end();
    }

    public float getStateTime() {
        return stateTime;
    }

}
