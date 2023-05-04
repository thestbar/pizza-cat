package com.junkiedan.ludumdare53;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.pfa.GraphPath;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Queue;
import com.junkiedan.ludumdare53.PlayerAnimationState;
import com.junkiedan.ludumdare53.ai.Node;
import com.junkiedan.ludumdare53.ai.NodeGraph;

public class EvilCat {
    private final Texture spritesheet;
    private final Animation<TextureRegion> runningLeftAnimation;
    private final Animation<TextureRegion> runningRightAnimation;
    private final Animation<TextureRegion> standingAnimation;

    private final Vector2 position;
    private Vector2 catDirection;
    private final float movementSpeed = 100;
    private final NodeGraph nodeGraph;

    private final Player player;
    private Vector2 playerGridPosition;
    private Vector2 tileSize;

    private Vector2 catGridPosition;
    private GraphPath<Node> nodePath;

    private final Node[][] mapNodes;

    private final float animationFrametime;

    private float stateTime;
    private PlayerAnimationState animationState;

    private final Rectangle hitbox;
    private boolean hitByPizza;

    public EvilCat(int x, int y, NodeGraph nodeGraph, Player player, Node[][] mapNodes, Vector2 tileSize) {
        this.position = new Vector2(x, y);
        this.nodeGraph = nodeGraph;
        catDirection = new Vector2(0, 0);
        this.player = player;
        hitbox = new Rectangle(x, y, tileSize.x / 2, tileSize.y / 2);
        hitByPizza = false;

        this.mapNodes = mapNodes;
        this.tileSize = tileSize;

        playerGridPosition = new Vector2(player.getPosition().x / tileSize.x, player.getPosition().y / tileSize.y);

        // Initialize animation
        spritesheet = new Texture(Gdx.files.internal("spritesheet/LudumDare53_cat.png"));

        TextureRegion[][] tmp = TextureRegion.split(spritesheet, 12, 12);

        TextureRegion[] runningLeftFrames = new TextureRegion[5];
        TextureRegion[] runningRightFrames = new TextureRegion[5];
        TextureRegion[] standingFrames = new TextureRegion[5];

        for(int i = 0; i < 5; i++) {
            tmp[0][i].flip(false, true);
            runningLeftFrames[i] = tmp[0][i];

            tmp[1][i].flip(false, true);
            runningRightFrames[i] = tmp[1][i];

            tmp[2][i].flip(false, true);
            standingFrames[i] = tmp[2][i];
        }

        animationFrametime = 0.33f;

        runningLeftAnimation = new Animation<>(animationFrametime, runningLeftFrames);
        runningRightAnimation = new Animation<>(animationFrametime, runningRightFrames);
        standingAnimation = new Animation<>(animationFrametime, standingFrames);
    }

    public Vector2 getPosition() {
        return position;
    }

    public void render(SpriteBatch batch, ShapeRenderer shapeRenderer) {
        // Draw hitbox for debugging purposes
//        shapeRenderer.begin();
//        shapeRenderer.setColor(Color.RED);
//        shapeRenderer.rect(hitbox.x, hitbox.y, hitbox.width, hitbox.height);
//        shapeRenderer.end();

        stateTime += Gdx.graphics.getDeltaTime();

        if(catDirection.x < 0) {
            animationState = PlayerAnimationState.RUNNING_LEFT;
        }
        else {
            animationState = PlayerAnimationState.RUNNING_RIGHT;
        }

        if(hitByPizza) {
            animationState = PlayerAnimationState.RESTING;
        }

        TextureRegion currentFrame;

        if(animationState == PlayerAnimationState.RESTING) {
            currentFrame = standingAnimation.getKeyFrame(stateTime, true);
        }
        else if(animationState == PlayerAnimationState.RUNNING_LEFT) {
            currentFrame = runningLeftAnimation.getKeyFrame(stateTime, true);
        }
        else {
            // Running right
            currentFrame = runningRightAnimation.getKeyFrame(stateTime, true);
        }

        batch.draw(currentFrame, position.x, position.y, 64, 64);

        if(!hitByPizza) {
            move();
        }
    }

    private void move() {
        // Update player position
        playerGridPosition = new Vector2(player.getPosition().x / tileSize.x, player.getPosition().y / tileSize.y);
        catGridPosition = new Vector2(position.x / tileSize.x, position.y / tileSize.y);

        nodePath = nodeGraph.findPath(mapNodes[(int)catGridPosition.y][(int)catGridPosition.x],
                mapNodes[(int)playerGridPosition.y][(int)playerGridPosition.x]);

        // Take only the second node, since the 1st is the current node
        if(nodePath.getCount() > 1) {
            Node targetNode = nodePath.get(1);
            Vector2 targetPosition = targetNode.getPosition();
            // Move cat towards this position
            catDirection = position.cpy().sub(targetPosition).nor();
            catDirection.rotateDeg(180);
            position.x += catDirection.x * movementSpeed * Gdx.graphics.getDeltaTime();
            position.y += catDirection.y * movementSpeed * Gdx.graphics.getDeltaTime();

            hitbox.x = position.x;
            hitbox.y = position.y;
        }
    }

    public void setAnimationState(PlayerAnimationState state) {
        this.animationState = state;
    }

    public Rectangle getHitbox() {
        return hitbox;
    }

    public void wasHitByPizza(boolean input) {
        hitByPizza = input;
    }

}
