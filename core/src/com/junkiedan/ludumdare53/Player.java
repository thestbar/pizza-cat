package com.junkiedan.ludumdare53;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import java.util.ArrayList;
import java.util.Random;

public class Player {
    private final Vector2 position;
    private final Rectangle rectangle;
    private final float movementSpeed;
    private Vector2 weaponDirection;
    private final OrthographicCamera camera;
    private final Vector2 playerSize;
    private final Texture playerSpritesheet;

    private final Animation<TextureRegion> runningLeftAnimation;
    private final Animation<TextureRegion> runningRightAnimation;
    private final Animation<TextureRegion> restingAnimation;
    private float stateTime;

    private PlayerAnimationState playerAnimationState;

    private final TextureRegion[] pizzaSliceFrames;
    private final float animationFrameDuration;

    private int ammo;
    private float remainingTime;
    private int score;

    private final Random random;


    public Player(float x, float y, float w, float h, float movementSpeed, OrthographicCamera camera, int startingAmmo, float startingTime) {
        // Load the spritesheet as a texture
        playerSpritesheet = new Texture(Gdx.files.internal("spritesheet/LudumDare53_char.png"));
        ammo = startingAmmo;
        remainingTime = startingTime + 1;
        score = 0;
        animationFrameDuration = 0.33f;

        position = new Vector2(x, y);
        // Remove playerSize
        // TODO
        playerSize = new Vector2(w, h);
        rectangle = new Rectangle(x, y, 64, 64);
        this.movementSpeed = movementSpeed;
        weaponDirection = new Vector2();
        this.camera = camera;

        // Use the split utility method to create a 2D array of TextureRegions. This is
        // possible because this sprite sheet contains frames of equal size and they are
        // all aligned.
        TextureRegion[][] tmp = TextureRegion.split(playerSpritesheet, 12, 12);

        // Place the regions into a 1D array in the correct order, starting from the top
        // left, going across first. The Animation constructor requires a 1D array.
        TextureRegion[] runningLeftFrames = new TextureRegion[5];
        TextureRegion[] runningRightFrames = new TextureRegion[5];
        TextureRegion[] restingFrames = new TextureRegion[5];
        pizzaSliceFrames = new TextureRegion[4];
        for(int i = 0; i < 5; i++) {
            tmp[0][i].flip(false, true);
            runningLeftFrames[i] = tmp[0][i];

            tmp[1][i].flip(false, true);
            runningRightFrames[i] = tmp[1][i];

            tmp[2][i].flip(false, true);
            restingFrames[i] = tmp[2][i];

            if(i < 4) {
                tmp[3][i].flip(false, true);
                pizzaSliceFrames[i] = tmp[3][i];
            }
        }

        // Initialize the Animation with the frame interval and array of frames
        runningLeftAnimation = new Animation<>(animationFrameDuration, runningLeftFrames);
        runningRightAnimation = new Animation<>(animationFrameDuration, runningRightFrames);
        restingAnimation = new Animation<>(animationFrameDuration, restingFrames);

        // Initialize state time
        stateTime = 0f;

        playerAnimationState = PlayerAnimationState.RESTING;

        random = new Random();
    }

    public void calculateWeaponDirection() {
        Vector3 screenMouseCoordinates = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
        Vector3 worldMouseCoordinates = camera.unproject(screenMouseCoordinates);
        Vector2 mousePosition = new Vector2(worldMouseCoordinates.x, worldMouseCoordinates.y);

        weaponDirection = mousePosition.sub(position).nor();
    }

    public void move(float x, float y, float delta, ArrayList<Rectangle> colliders) {
        // Check for collisions
        Rectangle newPositionRectangleXAxis = new Rectangle(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
        Rectangle newPositionRectangleYAxis = new Rectangle(rectangle.x, rectangle.y, rectangle.width, rectangle.height);

        // Check if the new rectangle will collide
        newPositionRectangleXAxis.x += x * delta * movementSpeed;
        newPositionRectangleYAxis.y += y * delta * movementSpeed;

        boolean collidedOnX = false;
        boolean collidedOnY = false;

        for(Rectangle collider : colliders) {
            if(collider.overlaps(newPositionRectangleXAxis)) {
                collidedOnX = true;
            }
            if(collider.overlaps(newPositionRectangleYAxis)) {
                collidedOnY = true;
            }
        }

        if(!collidedOnX) {
            position.x = newPositionRectangleXAxis.x;
        }
        if(!collidedOnY) {
            position.y = newPositionRectangleYAxis.y;
        }
        rectangle.setPosition(position.x, position.y);
    }

    public void draw(SpriteBatch batch, ShapeRenderer shapeRenderer) {
        calculateWeaponDirection();
//        rectangle.draw(batch, 1f);
        // Accumulate elapsed animation time
        stateTime += Gdx.graphics.getDeltaTime();

        remainingTime -= Gdx.graphics.getDeltaTime();
        remainingTime = Math.max(0, remainingTime);

        // Get current frame of animation for the current stateTime
        TextureRegion currentFrame;

        if(playerAnimationState == PlayerAnimationState.RESTING) {
            currentFrame = restingAnimation.getKeyFrame(stateTime, true);
        }
        else if(playerAnimationState == PlayerAnimationState.RUNNING_LEFT) {
            currentFrame = runningLeftAnimation.getKeyFrame(stateTime, true);
        }
        else {  // Running right
            currentFrame = runningRightAnimation.getKeyFrame(stateTime, true);
        }

        batch.draw(currentFrame, position.x, position.y, 64, 64);

//        shapeRenderer.begin();
//        shapeRenderer.rect(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
//        shapeRenderer.end();
    }

    public float getMovementSpeed() {
        return movementSpeed;
    }

    public Vector2 getPosition() {
        return position;
    }

    public Vector2 getWeaponDirection() {
        return weaponDirection;
    }

    public void setAnimationState(PlayerAnimationState state) {
        playerAnimationState = state;
    }

    public TextureRegion[] getPizzaSliceFrames() {
        return pizzaSliceFrames;
    }

    public void fire() {
        ammo--;
        ammo = Math.max(0, ammo);
    }

    public int getAmmo() {
        return ammo;
    }

    public void setAmmo(int ammo) {
        this.ammo = ammo;
    }

    public void addAmmo(int extraAmmo) {
        ammo += extraAmmo;
    }

    public float getTime() {
        return remainingTime;
    }

    public void addTime(float time) {
        remainingTime += time;
        // Each time is added means that a pizza was delivered
        // therefore we add 1 to the total score
        score++;
    }

    public int getScore() {
        return score;
    }

    public Rectangle getHitbox() {
        return rectangle;
    }

    public void removeAmmo() {
        int ammoToBeRemoved = random.nextInt(3) + 2;
        ammo -= ammoToBeRemoved;
        ammo = Math.max(0, ammo);
    }
}
