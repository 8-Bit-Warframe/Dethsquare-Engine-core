package com.ezardlabs.dethsquare.tmx;

import com.ezardlabs.dethsquare.util.Utils;
import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.ArrayList;

/**
 * Created by Benjamin on 2016-04-16.
 */
public class TMXLoader {
    private String fileName;
    private Map map;

    public TMXLoader(String fileName) {
        this.fileName = fileName;
        this.map = null;
        loadMap(this.fileName);
    }

    public Map getMap() {
        return this.map;
    }

    public void loadMap(String fileName) {
        try {
            File mapFile = new File(Utils.assetsPath + "maps/" + fileName + ".tmx");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(mapFile);
            doc.getDocumentElement().normalize();
            Element root = doc.getDocumentElement();
            if (!root.getNodeName().equals("map")) {
                throw new Exception("Invalid map file.");
            }
            NamedNodeMap rootAttrs = root.getAttributes();

            // Load map properties
//			String version = rootAttrs.getNamedItem("version").getNodeValue();
//			String orientation = rootAttrs.getNamedItem("orientation").getNodeValue();
//			String renderOrder = rootAttrs.getNamedItem("renderorder").getNodeValue();

            int mapWidth = Integer.parseInt(rootAttrs.getNamedItem("width").getNodeValue());
            int mapHeight = Integer.parseInt(rootAttrs.getNamedItem("height").getNodeValue());
            int mapTileWidth = Integer.parseInt(rootAttrs.getNamedItem("tilewidth").getNodeValue());
            int mapTileHeight = Integer.parseInt(rootAttrs.getNamedItem("tileheight").getNodeValue());
            this.map = new Map(mapWidth, mapHeight, mapTileWidth, mapTileHeight);

            // Load tileSets
            NodeList tileSetNodes = root.getElementsByTagName("tileset");
            for (int i = 0; i < tileSetNodes.getLength(); i++) {
                Node node = tileSetNodes.item(i);
                NamedNodeMap nodeAttrs = node.getAttributes();
                int firstGid = Integer.parseInt(nodeAttrs.getNamedItem("firstgid").getNodeValue());
                Node source = nodeAttrs.getNamedItem("source");
                TileSet tileSet = null;
                if (source == null) {
                    tileSet = loadTMXTileSet(node, firstGid);
                } else {
                    tileSet = loadTMXTileSet(Utils.assetsPath + "/maps/" + source.getNodeValue(), firstGid);
                }
                if (tileSet != null) {
                    this.map.addTileSet(tileSet);
                }
            }

            // Load tile layers
            NodeList tileLayers = root.getElementsByTagName("layer");
            boolean isBackgroundLayer = true;
            for (int i = 0; i < tileLayers.getLength(); i++) {
                Node node = tileLayers.item(i);
                NamedNodeMap nodeAttrs = node.getAttributes();
                Node nodeIsVisible = nodeAttrs.getNamedItem("visible");
                boolean isVisible = nodeIsVisible == null ? true : Boolean.getBoolean(nodeIsVisible.getNodeValue());
                if (!isVisible) {
                    continue;
                }
                String layerName = nodeAttrs.getNamedItem("name").getNodeValue();
                int layerWidth = Integer.parseInt(nodeAttrs.getNamedItem("width").getNodeValue());
                int layerHeight = Integer.parseInt(nodeAttrs.getNamedItem("height").getNodeValue());
                NodeList childNodes = node.getChildNodes();
                Tile[] tiles = null;
                for (int j = 0; j < childNodes.getLength(); j++) {
                    if (childNodes.item(j).getNodeName().equals("data")) {
                        tiles = loadTMXTiles(childNodes.item(j), layerWidth, layerHeight);
                        break;
                    }
                }
                if (tiles == null) {
                    throw new Exception("Cannot load tiles for layer: " + layerName);
                }
                Layer layer = new Layer(layerName, layerWidth, layerHeight, tiles);
                if (layerName.equals("main")) {
                    this.map.setMainLayer(layer);
                    isBackgroundLayer = false;
                } else if (isBackgroundLayer) {
                    this.map.addBackgroundLayer(layer);
                } else if (!isBackgroundLayer) {
                    this.map.addForegroundLayer(layer);
                }
            }

            // Load object layers
            NodeList objectGroups = doc.getElementsByTagName("objectgroup");

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public TileSet loadTMXTileSet(String name, int firstGid) {
        TileSet tileSet = null;
        try {
            File tileSetFile = new File(name);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(tileSetFile);
            doc.getDocumentElement().normalize();
            Element root = doc.getDocumentElement();
            tileSet = loadTMXTileSet(root, firstGid);
        } catch(Exception ex) {
            ex.printStackTrace();
        }
        return tileSet;
    }

    public TileSet loadTMXTileSet(Node root, int firstGid) {
        NamedNodeMap rootAttrs = root.getAttributes();
        String name = rootAttrs.getNamedItem("name").getNodeValue();
        int tileWidth = Integer.parseInt(rootAttrs.getNamedItem("tilewidth").getNodeValue());
        int tileHeight = Integer.parseInt(rootAttrs.getNamedItem("tileheight").getNodeValue());
        int tileCount = Integer.parseInt(rootAttrs.getNamedItem("tilecount").getNodeValue());
        String imageSource = "";
        int imageWidth = 0;
        int imageHeight = 0;
        Tile[] tiles = new Tile[tileCount];
        NodeList rootChildren = root.getChildNodes();
        for(int i = 0; i < rootChildren.getLength(); i++) {
            if(rootChildren.item(i).getNodeName().equals("image")) {
                NamedNodeMap childAttrs = rootChildren.item(i).getAttributes();
                imageSource = childAttrs.getNamedItem("source").getNodeValue();
                imageWidth = Integer.parseInt(childAttrs.getNamedItem("width").getNodeValue());
                imageHeight = Integer.parseInt(childAttrs.getNamedItem("height").getNodeValue());
            } else if(rootChildren.item(i).getNodeName().equals("tile")) {
                NamedNodeMap childAttrs = rootChildren.item(i).getAttributes();
//				int gid = Integer.parseInt(childAttrs.getNamedItem("gid").getNodeValue());
//				tiles[gid] = new Tile(gid);
            }
        }
        TileSet tileSet = new TileSet(name, firstGid, tileWidth, tileHeight, tileCount);
        tileSet.setImage(imageSource, imageWidth, imageHeight);
        return tileSet;
    }

    public Tile[] loadTMXTiles(Node data, int layerWidth, int layerHeight) {
        NodeList childNodes = data.getChildNodes();
//		Tile[] mapTiles = new Tile[layerWidth * layerHeight];
        ArrayList<Tile> arrTiles = new ArrayList<>();
        for(int i = 0; i < childNodes.getLength(); i++) {
            String nodeName = childNodes.item(i).getNodeName();
            if(childNodes.item(i).getNodeName().equals("tile")) {
                Node node = childNodes.item(i);
                NamedNodeMap attrs = node.getAttributes();
                Node gid = childNodes.item(i).getAttributes().getNamedItem("gid");
//				mapTiles[i / 2] = new Tile(Integer.parseInt(gid.getNodeValue()));
                arrTiles.add(new Tile(Integer.parseInt(gid.getNodeValue())));
            }
        }
        return arrTiles.toArray(new Tile[layerWidth * layerHeight]);
    }
}
