package com.junkiedan.ludumdare53;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

import java.util.Random;

public class PizzaTarget {
    private final int tileX;
    private final int tileY;

    private final float positionX;
    private final float positionY;

    private Random random;

    private final Animation<TextureRegion> particlesEffect;
    private float stateTime;

    public PizzaTarget(Vector2 tileSize) {
        random = new Random();
        int val = random.nextInt(TargetHouseMap.size()) + 1;
        int[] targetHouseTilePosition = TargetHouseMap.get(val);

        tileX = targetHouseTilePosition[1];
        tileY = targetHouseTilePosition[0];

        positionX = tileX * tileSize.x;
        positionY = tileY * tileSize.y;

        final Texture spritesheet = new Texture(Gdx.files.internal("spritesheet/LudumDare53_particles.png"));

        TextureRegion[] frames = TextureRegion.split(spritesheet, 64, 64)[0];
        for(TextureRegion frame : frames) {
            frame.flip(false, true);
        }
        particlesEffect = new Animation<>(0.66f, frames);

        stateTime = 0;
    }

    public void render(SpriteBatch batch, BitmapFont font) {
        stateTime += Gdx.graphics.getDeltaTime();
        TextureRegion currentFrame = particlesEffect.getKeyFrame(stateTime, true);
        batch.begin();
        batch.draw(currentFrame, positionX, positionY, 128, 128);
        batch.end();
    }

    public int getTileX() {
        return tileX;
    }

    public int getTileY() {
        return tileY;
    }

    public float getPositionX() {
        return positionX;
    }

    public float getPositionY() {
        return positionY;
    }
}
