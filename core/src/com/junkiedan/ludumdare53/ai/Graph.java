package com.junkiedan.ludumdare53.ai;

import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

public class Graph implements Connection<Node> {
    private final Node fromNode;
    private final Node toNode;
    private float cost;

    public Graph(Node fromNode, Node toNode) {
        this.fromNode = fromNode;
        this.toNode = toNode;

        cost = Vector2.dst(fromNode.getPosition().x, fromNode.getPosition().y,
                toNode.getPosition().x, toNode.getPosition().y);
    }

    public void render(ShapeRenderer shapeRenderer) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(1, 1, 1, 1);
        shapeRenderer.rectLine(fromNode.getPosition().x, fromNode.getPosition().y,
                toNode.getPosition().x, toNode.getPosition().y, 4);
        shapeRenderer.end();
    }

    @Override
    public float getCost() {
        return cost;
    }

    @Override
    public Node getFromNode() {
        return fromNode;
    }

    @Override
    public Node getToNode() {
        return toNode;
    }
}
