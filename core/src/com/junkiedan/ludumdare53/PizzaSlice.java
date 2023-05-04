package com.junkiedan.ludumdare53;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.junkiedan.ludumdare53.util.CustomRectangle;

public class PizzaSlice {
    private final Vector2 position;
    private final Vector2 direction;
    private final Rectangle rectangle;
    private final float movementSpeed;

    private final Animation<TextureRegion> pizzaSliceAnimation;

    private float stateTime;

    private boolean isMoving;

    public PizzaSlice(Vector2 position, Vector2 direction, float movementSpeed, TextureRegion[] textureRegions) {
        this.position = position.cpy();
        this.direction = direction.cpy();
        rectangle = new Rectangle(position.x, position.y, 32, 32);
        init();
        this.movementSpeed = movementSpeed;
        isMoving = false;

        pizzaSliceAnimation = new Animation<>(0.33f, textureRegions);
        stateTime = 0;
    }

    private void init() {

    }

    public void render(float delta, SpriteBatch batch) {
        if(!isMoving) {
            position.x += delta * movementSpeed * direction.x;
            position.y += delta * movementSpeed * direction.y;
            rectangle.setPosition(position.x, position.y);
        }

        stateTime += delta;

        TextureRegion currentFrame = pizzaSliceAnimation.getKeyFrame(stateTime, true);
        if(currentFrame != null) {
            batch.draw(currentFrame, position.x, position.y, 32, 32);
        }
    }

    public Vector2 getPosition() {
        return position;
    }

    public Rectangle getColliderRectangle() {
        return rectangle;
    }

    public void catWasHit(boolean input) {
        isMoving = input;
    }
}
