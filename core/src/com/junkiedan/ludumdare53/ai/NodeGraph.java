package com.junkiedan.ludumdare53.ai;

import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.ai.pfa.DefaultGraphPath;
import com.badlogic.gdx.ai.pfa.GraphPath;
import com.badlogic.gdx.ai.pfa.indexed.IndexedAStarPathFinder;
import com.badlogic.gdx.ai.pfa.indexed.IndexedGraph;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

public class NodeGraph implements IndexedGraph<Node> {
    private NodeHeuristic nodeHeuristic = new NodeHeuristic();
    private Array<Node> nodes = new Array<>();
    private Array<Graph> graphs = new Array<>();

    private ObjectMap<Node, Array<Connection<Node>>> connectionsMap = new ObjectMap<>();

    private int lastNodeIndex = 0;

    public Array<Node> getNodes() {
        return nodes;
    }

    public Array<Graph> getGraphs() {
        return graphs;
    }

    public void addNode(Node node) {
        node.setIndex(lastNodeIndex);
        lastNodeIndex++;

        nodes.add(node);
    }

    public void connectNodes(Node fromNode, Node toNode) {
        Graph graph = new Graph(fromNode, toNode);
        if(!connectionsMap.containsKey(fromNode)) {
            connectionsMap.put(fromNode, new Array<Connection<Node>>());
        }
        connectionsMap.get(fromNode).add(graph);
        graphs.add(graph);

        graph = new Graph(toNode, fromNode);
        if(!connectionsMap.containsKey(toNode)) {
            connectionsMap.put(toNode, new Array<Connection<Node>>());
        }
        connectionsMap.get(toNode).add(graph);
        graphs.add(graph);
    }

    public GraphPath<Node> findPath(Node startNode, Node goalNode) {
        GraphPath<Node> nodePath = new DefaultGraphPath<>();
        new IndexedAStarPathFinder<>(this).searchNodePath(startNode, goalNode, nodeHeuristic, nodePath);
        return nodePath;
    }

    @Override
    public int getIndex(Node node) {
        return node.getIndex();
    }

    @Override
    public int getNodeCount() {
        return lastNodeIndex;
    }

    @Override
    public Array<Connection<Node>> getConnections(Node fromNode) {
        if(connectionsMap.containsKey(fromNode)) {
            return connectionsMap.get(fromNode);
        }
        return new Array<>(0);
    }
}
