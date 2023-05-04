package com.junkiedan.ludumdare53.ai;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

import java.util.Vector;


public class Node {
    private Vector2 gridPosition;
    private Vector2 position;
    private int index; // Used by A* pathfinding algorithm
    private final Vector2 tileSize = new Vector2(128, 128);

    public Node(float x, float y)
    {
        gridPosition = new Vector2(x, y);
        position = new Vector2(tileSize.y * y + tileSize.y / 2, tileSize.x * x + tileSize.x / 2);
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public void render(ShapeRenderer shapeRenderer, SpriteBatch batch, BitmapFont font, boolean inPath) {
        shapeRenderer.begin();
        if(inPath) {
            // Green
            shapeRenderer.setColor(0, 1, 0, 1);
        }
        else {
            // Blue
            shapeRenderer.setColor(0, 0, 1, 1);
        }

        shapeRenderer.circle(position.x, position.y, 20);
        shapeRenderer.end();

//        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
//        shapeRenderer.setColor(1, 0, 0, 1);
//        shapeRenderer.circle(position.x, position.y, 20);
//        shapeRenderer.end();

        batch.begin();
        font.setColor(255, 255, 255, 255);
        font.getData().setScale(2.5f);
        font.draw(batch, "[" + (int)(position.y / tileSize.y) + "," + (int)(position.x / tileSize.x) + "]", position.x - 5, position.y + 5);
        batch.end();
    }

    public Vector2 getPosition() {
        return position;
    }

    public Vector2 getGridPosition() {
        return gridPosition;
    }

    public int getIndex() {
        return index;
    }
}
