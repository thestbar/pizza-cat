package com.junkiedan.ludumdare53;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.DelayedRemovalArray;
import com.junkiedan.ludumdare53.ai.Node;
import com.junkiedan.ludumdare53.ai.NodeGraph;

import java.util.Random;

public class CatSpawn {
    private final Vector2 startingPosition;
    private final Vector2 startingPositionGrid;
    private final Random random;
    private final DelayedRemovalArray<EvilCat> activeCats;
    private final NodeGraph nodeGraph;
    private final Node[][] mapNodes;
    private final Player player;
    private final Vector2 tileSize;
    private float time;
    private float diff;

    public CatSpawn(Vector2 startingPosition, DelayedRemovalArray<EvilCat> activeCats, NodeGraph nodeGraph,
                    Node[][] mapNodes, Player player, Vector2 tileSize) {
        this.startingPosition = startingPosition;
        startingPositionGrid = new Vector2(startingPosition.x / tileSize.x, startingPosition.y / tileSize.y);
        this.random = new Random();
        this.activeCats = activeCats;
        this.nodeGraph = nodeGraph;
        this.mapNodes = mapNodes;
        this.player = player;
        this.tileSize = tileSize;
        time = 0;
        diff = 0.9999f;
    }

    public void update() {
        time += Gdx.graphics.getDeltaTime();
        if(time > 30) {
            diff -= 0.0001f;
            time = 0;
        }
        if(random.nextFloat() > diff) {
            EvilCat cat = new EvilCat((int)startingPosition.x, (int)startingPosition.y, nodeGraph, player, mapNodes, tileSize);
            activeCats.add(cat);
        }
    }
}
