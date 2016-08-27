package com.ezardlabs.dethsquare.tmx;

/**
 * Created by Benjamin on 2016-04-09.
 */
public class TileSet {
    private String filePath;
    private String name;
    private int firstGid;
    private int tileWidth;
    private int tileHeight;
    private int tileCount;
    private String imageSource;
    private int imageWidth;
    private int imageHeight;

    private Tile[] tiles;

    public TileSet(String filePath, String name, int firstGid, int tileWidth, int tileHeight, int tileCount) {
        this.filePath = filePath;
        this.name = name;
        this.firstGid = firstGid;
        this.tileWidth = tileWidth;
        this.tileHeight = tileHeight;
        this.tileCount = tileCount;
        this.imageSource = "";
        this.imageWidth = 0;
        this.imageHeight = 0;
    }
    
    public void setImage(String imageSource, int width, int height) {
        this.imageSource = imageSource;
        this.imageWidth = width;
        this.imageHeight = height;
    }

    public String getFilePath() {
        return this.filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getFirstGid() {
        return firstGid;
    }

    public int getTileWidth() {
        return tileWidth;
    }

    public int getTileHeight() {
        return tileHeight;
    }

    public int getTileCount() {
        return tileCount;
    }

    public String getImageSource() {
        return imageSource;
    }

    public int getImageWidth() {
        return imageWidth;
    }

    public int getImageHeight() {
        return imageHeight;
    }

    public Tile[] getTiles() {
        return tiles;
    }
}
