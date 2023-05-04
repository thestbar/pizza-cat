package com.junkiedan.ludumdare53.ai;

import com.badlogic.gdx.ai.pfa.Heuristic;
import com.badlogic.gdx.math.Vector2;

public class NodeHeuristic implements Heuristic<Node> {
    @Override
    public float estimate(Node currentNode, Node goalNode) {
        return Vector2.dst(currentNode.getPosition().x, currentNode.getPosition().y,
                goalNode.getPosition().x, goalNode.getPosition().y);
    }
}
