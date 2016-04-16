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

    private ArrayList<Layer> fgLayers;
    private ArrayList<Layer> bgLayers;
    private Layer mainLayer;

    private ArrayList<TileSet> tileSets;

    public Map(int width, int height, int tileWidth, int tileHeight) {
        this.width = width;
        this.height = height;
        this.tileWidth = tileWidth;
        this.tileHeight = tileHeight;
        this.fgLayers = new ArrayList<>();
        this.bgLayers = new ArrayList<>();
        this.mainLayer = null;
        this.tileSets = new ArrayList<>();
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

    public ArrayList<Layer> getForegroundLayers() {
        return this.fgLayers;
    }

    public ArrayList<Layer> getBackgroundLayers() {
        return this.bgLayers;
    }

    public Layer getMainLayer() {
        return this.mainLayer;
    }

    public ArrayList<TileSet> getTileSets() {
        return this.tileSets;
    }

    public void addForegroundLayer(Layer layer) {
        this.fgLayers.add(layer);
    }

    public void addBackgroundLayer(Layer layer) {
        this.bgLayers.add(layer);
    }

    public void setMainLayer(Layer layer) {
        this.mainLayer = layer;
    }

    public void addTileSet(TileSet tileSet) {
        this.tileSets.add(tileSet);
    }
}
