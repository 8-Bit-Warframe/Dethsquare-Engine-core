package com.ezardlabs.dethsquare.tmx;

import java.util.ArrayList;

/**
 * Created by Benjamin on 2016-04-09.
 */
public class Map {
    private int width;
    private int height;
    private int tileWidth;
    private int tileHeight;

    private ArrayList<TileSet> tileSets;

    private ArrayList<Layer> tileLayers;
    private ArrayList<ObjectGroup> objectGroups;

    public Map(int width, int height, int tileWidth, int tileHeight) {
        this.width = width;
        this.height = height;
        this.tileWidth = tileWidth;
        this.tileHeight = tileHeight;
        this.tileSets = new ArrayList<>();
        this.tileLayers = new ArrayList<>();
        this.objectGroups = new ArrayList<>();
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public int getTileWidth() {
        return this.tileWidth;
    }

    public int getTileHeight() {
        return this.tileHeight;
    }

    public ArrayList<TileSet> getTileSets() {
        return this.tileSets;
    }

    public ArrayList<Layer> getTileLayers() {
        return this.tileLayers;
    }

    public ArrayList<ObjectGroup> getObjectGroups() {
        return this.objectGroups;
    }

    public void addTileSet(TileSet tileSet) {
        this.tileSets.add(tileSet);
    }

    public void addTileLayer(Layer layer) {
        this.tileLayers.add(layer);
    }

    public void addObjectGroup(ObjectGroup objectGroup) {
        this.objectGroups.add(objectGroup);
    }
}
