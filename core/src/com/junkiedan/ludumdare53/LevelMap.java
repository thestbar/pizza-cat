package com.junkiedan.ludumdare53;

public class LevelMap {
    private final char[][] map;

    public LevelMap() {
        // Map is 13 x 20 tiles
        this.map = new char[][] {
                {'T', 'T', 'T', 'T', 'A', 'T', 'T', 'T', 'A', 'T', 'T', 'T', 'H', 'H', 'T', 'T', 'T', 'A', 'T', '8'},
                {'H', 'X', 'R', 'R', 'R', 'R', 'R', 'R', 'R', 'R', 'R', 'R', 'R', 'R', 'X', 'R', 'R', 'X', 'T', '8'},
                {'T', 'E', 'H', 'T', 'T', 'H', 'T', 'T', 'H', 'T', 'T', 'T', 'T', 'H', 'E', '5', '6', 'E', 'H', '8'},
                {'T', 'E', 'T', 'T', 'T', 'T', 'T', 'T', 'T', 'T', 'A', 'T', 'A', 'H', 'E', 'R', 'R', 'X', 'T', '8'},
                {'H', 'E', 'T', 'T', 'T', 'T', 'A', 'T', 'A', 'T', 'T', 'T', 'T', 'T', 'E' ,'T', 'A', 'T', 'T', '8'},
                {'5', 'E', 'T', 'T', 'T', 'T' ,'T', 'P', 'Z', 'T', 'H', 'T', 'T', 'T', 'E', 'T', 'T', 'T', 'T', '8'},
                {'G', 'X', 'R', 'R', 'R', 'R', 'R', 'R', 'R', 'R', 'R', 'R', 'R', 'R', 'X', 'R', 'R', 'X', 'H', '8'},
                {'6', 'E', '5', '1', '2', 'G', 'G', 'G', '5', 'G', 'G', '1', '2', 'G', 'E' ,'A', 'T', 'E' ,'T', '8'},
                {'H', 'E', 'G', '3', '4', '5', '6', 'G', 'G', '6', '5', '3', '4', '5', 'E', 'T', 'T', 'E', 'T', '8'},
                {'T', 'E', 'R', 'R', 'R', 'R', 'R', 'R', 'R', 'R', 'R', 'R', 'R', 'R', 'X', 'R', 'R', 'X', 'T', '8'},
                {'T', 'E', 'H', 'H', 'H', 'X', 'T', 'A', 'A', 'T', 'X', 'T', 'H', 'H', 'E', 'T', 'T', 'A', 'T', '8'},
                {'T', 'X', 'R', 'R', 'R', 'R', 'R', 'R', 'R', 'R', 'R', 'R', 'R', 'R', 'X', 'T', 'T', 'T', 'T', '8'},
                {'7', '7', '7', '7', '7', '7', '7', '7', '7', '7', '7', '7', '7', '7', '7', '7', '7', '7', '7', '9'}
        };
    }

    public char[][] getMap() {
        return this.map;
    }

    public int getWidth() {
        return map[0].length;
    }

    public int getHeight() {
        return map.length;
    }

    public char getTileValue(int x, int y) {
        return map[x][y];
    }
}
