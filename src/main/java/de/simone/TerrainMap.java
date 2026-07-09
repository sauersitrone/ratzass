package de.simone;

import org.mapeditor.core.Map;
import org.mapeditor.core.Orientation;
import org.mapeditor.core.Properties;
import org.mapeditor.core.Tile;
import org.mapeditor.core.MapObject;
import org.mapeditor.core.ObjectGroup;
import org.mapeditor.core.TileLayer;
import org.mapeditor.core.TileSet;

public class TerrainMap {

    private int tileSize = 32;
    private int mapSize = 100;
    private Map map;
    private TileLayer tileLayer;
    private ObjectGroup objectLayer;

    public TerrainMap() {
        map = new Map();
        map.setOrientation(Orientation.ORTHOGONAL);
        map.setTileWidth(tileSize);
        map.setTileHeight(tileSize);

        // the tiles of the map
        TileSet tileSet = new TileSet();
        tileSet.setName("desert");
        tileSet.setTileWidth(tileSize);
        tileSet.setTileHeight(tileSize);

        objectLayer = new ObjectGroup(map);
        objectLayer.setName("objects");
        map.addLayer(objectLayer);

        // simulate a cliff
        Tile cliffTile = createCliffTile();
        tileSet.addTile(cliffTile);

        map.addTileset(tileSet);

        // create the terrain layer
        tileLayer = new TileLayer(map, mapSize, mapSize);
        for (int i = 0; i < mapSize; i++) {
            for (int j = 0; j < mapSize; j++) {
                if (j < mapSize / 2) {
                    tileLayer.setTileAt(i, j, cliffTile);
                } else {
                    Tile groundTile = getGroundTile();
                    tileLayer.setTileAt(i, j, groundTile);
                }
            }
        }
        map.addLayer(tileLayer);
    }

    public boolean isBuildable(int x, int y) {
        String buildability = tileLayer.getTileAt(x, y).getProperties().getProperty("Buildability");
        return "true".equals(buildability);
    }

    public boolean isWalkable(int x, int y) {
        String walkability = tileLayer.getTileAt(x, y).getProperties().getProperty("SubtileWalkability");
        String subtileWalkability = walkability.substring(x * 4 + y, x * 4 + y + 1);
        return "1".equals(subtileWalkability);
    }

    public void putObject(int x, int y, int width, int height, String objectType) {
        MapObject mapObject = new MapObject(x * tileSize, y * tileSize, width * tileSize, height * tileSize, 0.0);
        mapObject.setType(objectType);
        objectLayer.addObject(mapObject);
    }

    private Tile getGroundTile() {
        Tile groundTile = new Tile();
        groundTile.setId(0);
        Properties tileProps = new Properties();
        tileProps.setProperty("Buildability", "true");
        tileProps.setProperty("TerrainElevation", "Low");
        tileProps.setProperty("VisionBlock", "false");
        tileProps.setProperty("SubtileWalkability", "1111111111111111");  
        groundTile.setProperties(tileProps);
        return groundTile;
    }

    private Tile createCliffTile() {
        Properties tileProps = new Properties();
        tileProps.setProperty("Buildability", "false");
        tileProps.setProperty("TerrainElevation", "High");
        tileProps.setProperty("VisionBlock", "true");
        tileProps.setProperty("SubtileWalkability", "0011001100110011");

        Tile cliffTile = new Tile();
        cliffTile.setId(1);
        cliffTile.setProperties(tileProps);
        return cliffTile;
    }
}
