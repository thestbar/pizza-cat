package com.junkiedan.ludumdare53.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.junkiedan.ludumdare53.Player;

public class TargetArrow {
    private final Texture texture;
    private final TextureRegion textureRegion;
    private final Player player;
    private Vector2 targetHousePosition;
    private boolean showArrow;

    public TargetArrow(Player player) {
        texture = new Texture(Gdx.files.internal("spritesheet/LudumDare53_arrow_v2.png"));
        textureRegion = TextureRegion.split(texture, 12, 12)[0][0];
        this.player = player;
        targetHousePosition = null;
        showArrow = false;
    }

    public void displayArrow(boolean input, Vector2 targetHousePosition) {
        this.targetHousePosition = targetHousePosition;
        showArrow = input;
    }

    public void render(SpriteBatch batch, ShapeRenderer shapeRenderer) {
        if(showArrow) {
            Vector2 dir = player.getPosition().cpy().sub(targetHousePosition).nor();

            Vector2 playerPosition = player.getPosition().cpy();
            playerPosition.x += 16;
            playerPosition.y -= 32;

//            shapeRenderer.begin();
//            shapeRenderer.line(player.getPosition(), targetHousePosition);
//            shapeRenderer.end();

            batch.begin();
//            batch.draw(texture, player.getPosition().x, player.getPosition().y, 64, 64);
            batch.draw(textureRegion, playerPosition.x, playerPosition.y, 16, 16, 32, 32,
            1, 1, dir.angleDeg() + 90);
            batch.end();
        }
    }
}
