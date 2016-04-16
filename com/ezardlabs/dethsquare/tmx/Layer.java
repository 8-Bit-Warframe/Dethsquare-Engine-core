package com.ezardlabs.dethsquare.tmx;

/**
 * Created by Benjamin on 2016-04-09.
 */
public class Layer {
    private String name;
    private int width;
    private int height;

    private Tile[] tiles;

    public Layer(String name, int width, int height, Tile[] tiles) {
        this.name = name;
        this.width = width;
        this.height = height;
        this.tiles = tiles;
    }

    public String getName() {
        return this.name;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public Tile[] getTiles() {
        return this.tiles;
    }

    public Tile getTile(int idx) {
        return this.tiles[idx];
    }
}
