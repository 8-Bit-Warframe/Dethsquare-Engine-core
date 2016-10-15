package com.ezardlabs.dethsquare.tmx;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.net.URI;
import java.util.ArrayList;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

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

    public void loadMap(String filePath) {
        try {
            int lastSlash = filePath.lastIndexOf("/");
            String folder = "";
            String file = filePath;
            if(lastSlash >= 0) {
                folder = filePath.substring(0, lastSlash);
                file = filePath.substring(lastSlash + 1, filePath.length());
            }

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(Thread.currentThread().getContextClassLoader()
                                 .getResourceAsStream(filePath));
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
            this.map = new Map(filePath, mapWidth, mapHeight, mapTileWidth, mapTileHeight);

            // Load tileSets
            NodeList tileSetNodes = root.getElementsByTagName("tileset");
            for (int i = 0; i < tileSetNodes.getLength(); i++) {
                Node node = tileSetNodes.item(i);
                NamedNodeMap nodeAttrs = node.getAttributes();
                int firstGid = Integer.parseInt(nodeAttrs.getNamedItem("firstgid").getNodeValue());
                Node source = nodeAttrs.getNamedItem("source");
                TileSet tileSet = null;
                if (source == null) {
                    tileSet = loadTMXTileSet(null, node, firstGid);
                } else {
                    tileSet = loadTMXTileSet(folder + "/" + source.getNodeValue(), firstGid);
                }
                if (tileSet != null) {
                    this.map.addTileSet(tileSet);
                }
            }

            // Load tile layers
            NodeList tileLayers = root.getElementsByTagName("layer");
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
                this.map.addTileLayer(new Layer(layerName, layerWidth, layerHeight, tiles));
            }

            // Load object layers
            NodeList nodeListObjectGroups = doc.getElementsByTagName("objectgroup");
            for(int i = 0; i < nodeListObjectGroups.getLength(); i++) {
                Node nodeObjectGroup = nodeListObjectGroups.item(i);
                NamedNodeMap objectGroupAttrs = nodeObjectGroup.getAttributes();
                Node nodeObjectGroupName = objectGroupAttrs.getNamedItem("name");
                String objectGroupName = "";
                if(nodeObjectGroupName != null) {
                    objectGroupName = nodeObjectGroupName.getNodeValue();
                }
                ArrayList<TMXObject> tmxObjects = new ArrayList<>();
                NodeList nodeObjects = nodeObjectGroup.getChildNodes();
                for(int j = 0; j < nodeObjects.getLength(); j++) {
                    Node node = nodeObjects.item(j);
                    if(!node.getNodeName().equals("object")) {
                        continue;
                    }
                    NamedNodeMap attrs = node.getAttributes();
                    Node nodeId = attrs.getNamedItem("id");
                    int id = 0;
                    if (nodeId != null) {
                        id = Integer.parseUnsignedInt(nodeId.getNodeValue());
                    }
                    Node nodeName = attrs.getNamedItem("name");
                    String name = "";
                    if (nodeName != null) {
                        name = nodeName.getNodeValue();
                    }
                    Node nodeType = attrs.getNamedItem("type");
                    String type = "";
                    if (nodeType != null) {
                        type = nodeType.getNodeValue();
                    }
                    int x = Integer.parseInt(attrs.getNamedItem("x").getNodeValue());
                    int y = Integer.parseInt(attrs.getNamedItem("y").getNodeValue());
                    int width = Integer.parseInt(attrs.getNamedItem("width").getNodeValue());
                    int height = Integer.parseInt(attrs.getNamedItem("height").getNodeValue());
                    Properties props = new Properties();
                    if(node instanceof Element) {
                        NodeList nlProperties = ((Element) node).getElementsByTagName("properties");
                        if(nlProperties != null && nlProperties.getLength() > 0) {
                            Node nodeProperties = nlProperties.item(0);
                            NodeList nlPropItems = nodeProperties.getChildNodes();
                            for(int k = 0; k < nlPropItems.getLength(); k++) {
                                Node p = nlPropItems.item(k);
                                if(p.getNodeName().equals("property")) {
                                    NamedNodeMap pAttrs = p.getAttributes();
                                    Node nodePropName = pAttrs.getNamedItem("name");
                                    Node nodePropValue = pAttrs.getNamedItem("value");
                                    if(nodePropName != null && nodePropValue != null) {
                                        props.setProperty(nodePropName.getNodeValue(), nodePropValue.getNodeValue());
                                    }
                                }
                            }
                        }
                    }
                    tmxObjects.add(new TMXObject(id, name, type, x, y, width, height, props));
                }
                map.addObjectGroup(new ObjectGroup(objectGroupName, tmxObjects.toArray(new TMXObject[tmxObjects.size()])));
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public TileSet loadTMXTileSet(String filePath, int firstGid) {
        TileSet tileSet = null;
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(Thread.currentThread().getContextClassLoader()
                    .getResourceAsStream(new URI(filePath).normalize().getPath()));
            doc.getDocumentElement().normalize();
            Element root = doc.getDocumentElement();
            tileSet = loadTMXTileSet(filePath, root, firstGid);
        } catch(Exception ex) {
            ex.printStackTrace();
        }
        return tileSet;
    }

    public TileSet loadTMXTileSet(String filePath, Node root, int firstGid) {
        if(filePath == null)
            filePath = "";
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
                Node nodeGid = childAttrs.getNamedItem("gid");
                if(nodeGid == null) {
                    nodeGid = childAttrs.getNamedItem("id");
                }
				int gid = Integer.parseInt(nodeGid.getNodeValue());
				tiles[gid] = new Tile(gid);
            }
        }
        TileSet tileSet = new TileSet(filePath, name, firstGid, tileWidth, tileHeight, tileCount);
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
                if(gid == null) {
                    gid = childNodes.item(i).getAttributes().getNamedItem("id");
                }
//				mapTiles[i / 2] = new Tile(Integer.parseInt(gid.getNodeValue()));
                arrTiles.add(new Tile(Integer.parseUnsignedInt(gid.getNodeValue())));
            }
        }
        return arrTiles.toArray(new Tile[layerWidth * layerHeight]);
    }
}
