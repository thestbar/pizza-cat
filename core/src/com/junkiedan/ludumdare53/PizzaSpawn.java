package com.junkiedan.ludumdare53;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class PizzaSpawn {
    private final Texture spritesheet;
    private final Animation<TextureRegion> animation;
    private final Vector2 position;

    private float stateTime;

    public PizzaSpawn(Vector2 position) {
        spritesheet = new Texture(Gdx.files.internal("spritesheet/LudumDare53_pizza.png"));

        TextureRegion[] temp = TextureRegion.split(spritesheet, 12, 12)[0];
        for(TextureRegion region : temp) {
            region.flip(false, true);
        }
        animation = new Animation<>(0.33f, temp);

        this.position = position;
    }

    public void render(SpriteBatch batch) {
        stateTime += Gdx.graphics.getDeltaTime();

        TextureRegion currentFrame = animation.getKeyFrame(stateTime, true);

        batch.begin();
        batch.draw(currentFrame, position.x, position.y, 64, 64);
        batch.end();
    }

    public float getX() {
        return position.x;
    }

    public float getY() {
        return position.y;
    }
}
