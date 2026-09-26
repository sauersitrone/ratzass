package de.simone.ui.forms;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.Timer;

import org.mapeditor.core.Map;
import org.mapeditor.core.MapObject;
import org.mapeditor.core.ObjectGroup;
import org.mapeditor.core.Orientation;
import org.mapeditor.core.Properties;
import org.mapeditor.core.Tile;
import org.mapeditor.core.TileLayer;
import org.mapeditor.core.TileSet;

import bwapi.Game;
import bwapi.Region;
import bwapi.TilePosition;
import bwapi.Unit;
import bwapi.UnitCommandType;
import bwapi.WalkPosition;
import bwem.Area;
import bwem.ChokePoint;
import de.simone.Config;
import de.simone.RBWListener;
import de.simone.command.Command;
import de.simone.command.CommandQueueListener;

/**
 * Tiled-map-backed GUI showing the ProxyBot's view of the game state.
 * Uses org.mapeditor.libtiled to represent terrain as a TileLayer
 * and game entities as ObjectGroups; rendering reads back from those
 * structures.
 */
public class StarCraftTileMap extends JPanel
        implements MouseWheelListener, MouseMotionListener, MouseListener, CommandQueueListener {

    private Game game;

    private int tileSize = 32;
    private int textSize = 20;
    private boolean influenceMap = false;

    // pan / zoom
    private double scale = 1.0;
    private double tx = 0, ty = 0;
    private int mx = 0, my = 0;
    private boolean mouseDown = false;
    private int hoveredTileX = -1;
    private int hoveredTileY = -1;
    private final Timer repaintTimer;
    private long lastRedraw = 0;
    double scaleAmount = 0.9;

    private Map tiledMap;
    private TileSet tileSet;

    /**
     * Cached terrain tiles indexed by T_* constants.
     * Stored separately so we can look them up by index without relying
     * on the TileSet's internal indexing strategy.
     */
    private final Tile[] terrainTiles = new Tile[4];

    /** Terrain tile indices */
    private static final int T_DARK = 0; // low/unwalkable
    private static final int T_MID = 1; // medium elevation
    private static final int T_LIGHT = 2; // high/buildable
    private static final int T_CREEP = 3; // Zerg creep

    private static final Color[] TERRAIN_COLORS = {
            new Color(30, 30, 22), // T_DARK
            new Color(70, 70, 53), // T_MID
            new Color(130, 130, 98), // T_LIGHT
            new Color(148, 0, 211), // T_CREEP
    };

    private TileLayer terrainLayer;
    private ObjectGroup regionsGroup;
    private ObjectGroup areasGroup;
    private ObjectGroup chokepointsGroup;
    private ObjectGroup startSpotsGroup;
    private ObjectGroup mineralsGroup;
    private ObjectGroup geysersGroup;
    private ObjectGroup enemyUnitsGroup;
    private ObjectGroup allyUnitsGroup;
    private ObjectGroup neutralUnitsGroup;
    private boolean mapInitialized = false;

    public StarCraftTileMap() {
        this.game = RBWListener.game;
        addMouseWheelListener(this);
        addMouseMotionListener(this);
        addMouseListener(this);

        repaintTimer = new Timer(200, e -> {
            if (game == null) {
                return;
            }
            updateTiledMap();
            repaint();
        });
        repaintTimer.start();
    }

    private void initTiledMap() {
        int w = game.mapWidth();
        int h = game.mapHeight();

        tiledMap = new Map();
        tiledMap.setVersion("1.0");
        tiledMap.setTiledversion("1.4.3");
        tiledMap.setInfinite(0);
        tiledMap.setNextlayerid(1);
        tiledMap.setNextobjectid(1);
        tiledMap.setOrientation(Orientation.ORTHOGONAL);
        tiledMap.setTileWidth(tileSize);
        tiledMap.setTileHeight(tileSize);
        tiledMap.setWidth(w);
        tiledMap.setHeight(h);

        // four terrain tile variants – no images, colour encoded in property
        tileSet = new TileSet();
        tileSet.setName("terrain");
        tileSet.setTileWidth(tileSize);
        tileSet.setTileHeight(tileSize);
        for (int i = 0; i < TERRAIN_COLORS.length; i++) {
            Tile tile = new Tile();
            tile.setId(i);
            tile.setType("");
            Properties props = new Properties();
            Color c = TERRAIN_COLORS[i];
            String hex = String.format("#%02x%02x%02x", c.getRed(), c.getGreen(), c.getBlue());
            props.setProperty("color", hex);
            tile.setProperties(props);
            tileSet.addTile(tile);
            terrainTiles[i] = tile;
        }
        tiledMap.addTileset(tileSet);

        // terrain tile layer
        terrainLayer = new TileLayer(tiledMap, w, h);
        terrainLayer.setName("terrain");
        terrainLayer.setId(tiledMap.getNextlayerid());
        tiledMap.setNextlayerid(tiledMap.getNextlayerid() + 1);
        tiledMap.addLayer(terrainLayer);

        // one ObjectGroup per entity category; colour is stored as hex string
        regionsGroup = addObjectGroup("regions", "#ffa500");
        areasGroup = addObjectGroup("areas", "#00ff00");
        chokepointsGroup = addObjectGroup("chokepoints", "#ffffff");
        startSpotsGroup = addObjectGroup("starting_locations", "#8A2BE2");
        mineralsGroup = addObjectGroup("minerals", "#00ffff");
        geysersGroup = addObjectGroup("geysers", "#008000");
        enemyUnitsGroup = addObjectGroup("enemy_units", "#ff0000");
        allyUnitsGroup = addObjectGroup("ally_units", "#0000FF");
        neutralUnitsGroup = addObjectGroup("neutral_units", "#808080");

        mapInitialized = true;
    }

    private ObjectGroup addObjectGroup(String name, String hexColor) {
        ObjectGroup group = new ObjectGroup(tiledMap);
        group.setId(tiledMap.getNextlayerid());
        tiledMap.setNextlayerid(tiledMap.getNextlayerid() + 1);
        group.setName(name);
        group.setColor(hexColor);
        tiledMap.addLayer(group);
        return group;
    }

    private MapObject createMapObject(double x, double y, double width, double height, double rotation) {
        MapObject object = new MapObject(x, y, width, height, rotation);
        object.setId(tiledMap.getNextobjectid());
        tiledMap.setNextobjectid(tiledMap.getNextobjectid() + 1);
        return object;
    }

    private void updateTiledMap() {
        try {
            if (!mapInitialized)
                initTiledMap();

            refreshTerrainLayer();
            refreshObjectGroups();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Writes a terrain tile for every map tile based on BWAPI walkability / height
     * data.
     */
    private void refreshTerrainLayer() {
        int w = game.mapWidth();
        int h = game.mapHeight();
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                // isWalkable uses walk-tile coordinates (4 walk tiles per map tile)
                boolean walkable = game.isWalkable(x * 4, y * 4);
                boolean buildable = game.isBuildable(x, y);
                boolean creep = game.hasCreep(x, y);
                int height = game.getGroundHeight(x, y);

                int idx;
                if (creep) {
                    idx = T_CREEP;
                } else {
                    int brightness = 70 * (walkable ? 1 : 0) + 60 * (buildable ? 1 : 0) + 50 * height;
                    brightness = Math.max(0, Math.min(255, brightness));
                    idx = brightness < 50 ? T_DARK : brightness < 110 ? T_MID : T_LIGHT;
                }
                terrainLayer.setTileAt(x, y, terrainTiles[idx]);
            }
        }
    }

    /** Rebuilds every ObjectGroup from current game state. */
    private void refreshObjectGroups() {
        refreshResources();
        refreshUnits();
        refreshRegions();
        refreshAreas();
        refreshChokepoints();
        refreshStartSpots();
    }

    private void refreshRegions() {
        regionsGroup.getObjects().clear();
        if (!Config.drawRegions)
            return;

        for (Region region : game.getAllRegions()) {
            // JBWAPI Region exposes a bounding box, not raw polygon points
            int x = (int) region.getBoundsLeft();
            int y = (int) region.getBoundsTop();
            int w = (int) region.getBoundsRight() - region.getBoundsLeft();
            int h = (int) region.getBoundsBottom() - region.getBoundsTop();
            MapObject obj = createMapObject(x, y, w, h, 0);
            obj.setType("region");
            regionsGroup.addObject(obj);
        }
    }

    private void refreshAreas() {
        areasGroup.getObjects().clear();
        if (!Config.drawAreas || RBWListener.bwem == null)
            return;

        for (Area area : RBWListener.bwem.getMap().getAreas()) {
            TilePosition topLeft = area.getTopLeft();
            TilePosition bottomRight = area.getBottomRight();
            int x = topLeft.x * tileSize;
            int y = topLeft.y * tileSize;
            int width = (bottomRight.x - topLeft.x + 1) * tileSize;
            int height = (bottomRight.y - topLeft.y + 1) * tileSize;
            MapObject obj = createMapObject(x, y, width, height, 0);
            obj.setType("area");
            areasGroup.addObject(obj);
        }
    }

    private void refreshChokepoints() {
        chokepointsGroup.getObjects().clear();
        if (!Config.drawChokepoints || RBWListener.bwem == null)
            return;

        for (ChokePoint cp : RBWListener.bwem.getMap().getChokePoints()) {
            // Center is a WalkPosition (8px units); 4 walk tiles = 1 map tile
            WalkPosition center = cp.getCenter();
            List<WalkPosition> geom = cp.getGeometry();

            // compute radius from geometry extent (in walk tiles), convert to display
            // pixels
            int extentWalk = geom.stream()
                    .mapToInt(p -> Math.max(Math.abs(p.x - center.x), Math.abs(p.y - center.y)))
                    .max().orElse(4);
            int radius = Math.max(1, extentWalk / 4) * tileSize;

            double cx = (double) center.x / 4 * tileSize - radius;
            double cy = (double) center.y / 4 * tileSize - radius;
            int diameter = radius * 2;

            MapObject obj = createMapObject(cx, cy, diameter, diameter, 0);
            obj.setShape(new Ellipse2D.Double(0, 0, diameter, diameter));
            obj.setType("chokepoint");
            chokepointsGroup.addObject(obj);
        }
    }

    private void refreshStartSpots() {
        startSpotsGroup.getObjects().clear();
        if (!Config.drawStartSpots)
            return;

        for (TilePosition loc : game.getStartLocations()) {
            MapObject obj = createMapObject(loc.x * tileSize, loc.y * tileSize,
                    4.0 * tileSize, 3.0 * tileSize, 0);
            obj.setType("starting_location");
            startSpotsGroup.addObject(obj);
        }
    }

    private void refreshResources() {
        mineralsGroup.getObjects().clear();
        geysersGroup.getObjects().clear();
        if (!Config.drawResources)
            return;

        List<Unit> list = game.getMinerals();
        for (Unit mineral : list) {
            MapObject obj = createMapObject(mineral.getX(), mineral.getY(), tileSize, tileSize, 0);
            obj.setType("mineral");
            mineralsGroup.addObject(obj);
        }
        for (Unit geyser : game.getGeysers()) {
            MapObject obj = createMapObject(geyser.getX(), geyser.getY(), geyser.getType().tileWidth() * tileSize,
                    geyser.getType().tileHeight() * tileSize, 0);
            obj.setType("geyser");
            geysersGroup.addObject(obj);
        }
    }

    private void refreshUnits() {
        enemyUnitsGroup.getObjects().clear();
        if (Config.drawEnemyUnits) {
            for (Unit unit : game.getAllUnits()) {
                if (unit.getPlayer() == null || !game.self().isEnemy(unit.getPlayer()))
                    continue;
                MapObject obj = createMapObject(unit.getX(), unit.getY(), unit.getType().tileWidth() * tileSize,
                        unit.getType().tileHeight() * tileSize, 0);
                obj.setType("enemy_unit");
                if (Config.drawIDs)
                    obj.setName(String.valueOf(unit.getID()));
                enemyUnitsGroup.addObject(obj);
            }
        }

        allyUnitsGroup.getObjects().clear();
        if (Config.drawPlayerUnits) {
            for (Unit unit : game.getAllUnits()) {
                if (unit.getPlayer() == null || unit.getPlayer().getID() != game.self().getID())
                    continue;
                MapObject obj = createMapObject(unit.getX(), unit.getY(), unit.getType().tileWidth() * tileSize,
                        unit.getType().tileHeight() * tileSize, 0);
                obj.setType("ally_unit");
                if (Config.drawIDs)
                    obj.setName(String.valueOf(unit.getID()));
                allyUnitsGroup.addObject(obj);
            }
        }

        neutralUnitsGroup.getObjects().clear();
        if (Config.drawNeutralUnits) {
            for (Unit unit : game.getNeutralUnits()) {
                MapObject obj = createMapObject(unit.getX(), unit.getY(), unit.getType().tileWidth() * tileSize,
                        unit.getType().tileHeight() * tileSize, 0);
                obj.setType("neutral_unit");
                if (Config.drawIDs)
                    obj.setName(String.valueOf(unit.getID()));
                neutralUnitsGroup.addObject(obj);
            }
        }
    }

    @Override
    public void paint(Graphics g) {
        if (System.currentTimeMillis() > (1000 + lastRedraw)) {
            orders.clear();
        }
        lastRedraw = System.currentTimeMillis();

        Graphics2D g2 = (Graphics2D) g;
        g2.setBackground(Color.BLACK);
        g2.clearRect(0, 0, getWidth(), getHeight());
        // System.out.println(getWidth() + " " + getHeight());
        g2.translate(tx, ty);
        g2.scale(scale, scale);

        if (!influenceMap) {
            if (mapInitialized) {
                paintTerrainLayer(g2);
                if (Config.drawRegions)
                    paintObjectGroupOutline(g2, regionsGroup);

                if (Config.drawAreas)
                    paintObjectGroupOutline(g2, areasGroup);

                if (Config.drawChokepoints)
                    paintObjectGroupEllipses(g2, chokepointsGroup);

                if (Config.drawStartSpots)
                    paintObjectGroupFilled(g2, startSpotsGroup);
            }
            if (Config.drawResources && mapInitialized) {
                paintObjectGroupFilled(g2, mineralsGroup);
                paintObjectGroupFilled(g2, geysersGroup);
            }

            if (Config.drawEnemyUnits && mapInitialized)
                paintObjectGroupFilled(g2, enemyUnitsGroup);

            if (Config.drawPlayerUnits && mapInitialized)
                paintObjectGroupFilled(g2, allyUnitsGroup);

            if (Config.drawNeutralUnits && mapInitialized)
                paintObjectGroupFilled(g2, neutralUnitsGroup);

            if (Config.drawIDs && mapInitialized)
                paintUnitLabels(g2);

            if (Config.drawPings)
                paintPings(g2);
        } else {
            paintInfluenceMap(g2);
        }

        paintHoveredTile(g2);

        g2.scale(1.0 / scale, 1.0 / scale);
        g2.translate(-tx, -ty);
    }

    private void paintHoveredTile(Graphics2D g2) {
        if (game == null || hoveredTileX < 0 || hoveredTileY < 0
                || hoveredTileX >= game.mapWidth() || hoveredTileY >= game.mapHeight())
            return;

        int x = hoveredTileX * tileSize;
        int y = hoveredTileY * tileSize;
        String coordinates = hoveredTileX + "," + hoveredTileY;
        int fontSize = 10;
        Font font = new Font("Arial", Font.PLAIN, fontSize);
        while (fontSize > 6 && g2.getFontMetrics(font).stringWidth(coordinates) > tileSize - 4) {
            font = new Font("Arial", Font.PLAIN, --fontSize);
        }

        g2.setColor(new Color(255, 255, 0, 55));
        g2.fillRect(x, y, tileSize, tileSize);
        g2.setColor(Color.YELLOW);
        g2.drawRect(x, y, tileSize, tileSize);
        g2.setFont(font);
        g2.drawString(coordinates, x + 2, y + g2.getFontMetrics().getAscent() + 2);
    }

    /**
     * Iterates the TileLayer and draws each tile as a filled rectangle
     * using the "color" property stored in the tile's Properties.
     */
    private void paintTerrainLayer(Graphics2D g2) {
        int w = game.mapWidth();
        int h = game.mapHeight();
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                Tile tile = terrainLayer.getTileAt(x, y);
                Color c = Color.BLACK;
                if (tile != null) {
                    String hex = tile.getProperties().getProperty("color");
                    c = Color.decode(hex);
                }
                g2.setColor(c);
                g2.fillRect(x * tileSize, y * tileSize, tileSize, tileSize);
            }
        }
    }

    private void paintObjectGroupOutline(Graphics2D g2, ObjectGroup group) {
        String hex = group.getColor();
        Color c = Color.decode(hex);
        for (MapObject mo : group) {
            if (Config.fillRegions) {
                g2.setColor(c.darker());
                g2.fillRect((int) mo.getX(), (int) mo.getY(),
                        mo.getWidth().intValue(), mo.getHeight().intValue());
            }
            g2.setColor(c);
            g2.drawRect((int) mo.getX(), (int) mo.getY(),
                    mo.getWidth().intValue(), mo.getHeight().intValue());
        }
    }

    private void paintObjectGroupEllipses(Graphics2D g2, ObjectGroup group) {
        String hex = group.getColor();
        Color c = Color.decode(hex);
        g2.setColor(c);
        for (MapObject mo : group) {
            g2.drawArc((int) mo.getX(), (int) mo.getY(),
                    mo.getWidth().intValue(), mo.getHeight().intValue(), 0, 360);
        }
    }

    private void paintObjectGroupFilled(Graphics2D g2, ObjectGroup group) {
        String hex = group.getColor();
        Color c = Color.decode(hex);
        g2.setColor(c);
        for (MapObject mo : group) {
            g2.fillRect((int) mo.getX(), (int) mo.getY(),
                    mo.getWidth().intValue(), mo.getHeight().intValue());
        }
    }

    private void paintUnitLabels(Graphics2D g2) {
        g2.setColor(Color.DARK_GRAY);
        g2.setFont(new Font("Arial", Font.PLAIN, textSize));
        for (ObjectGroup group : new ObjectGroup[] { allyUnitsGroup, enemyUnitsGroup, neutralUnitsGroup }) {
            for (MapObject mo : group) {
                String label = mo.getName();
                if (label != null && !label.isEmpty()) {
                    g2.drawString(label, (int) mo.getX() + 2,
                            (int) mo.getY() + 2 + textSize);
                }
            }
        }
    }

    private void paintPings(Graphics2D g2) {
        g2.setColor(Color.YELLOW);
        ArrayList<Order> remove = new ArrayList<>();
        for (Order order : new ArrayList<>(orders)) {
            order.timer++;
            if (order.timer > 15) {
                remove.add(order);
                continue;
            }
            int x = order.x * tileSize + tileSize / 2;
            int y = order.y * tileSize + tileSize / 2;
            g2.drawLine(x + 3 * order.timer, y - (25 - order.timer), x + 3 * order.timer, y + (25 - order.timer));
            g2.drawLine(x - 3 * order.timer, y - (25 - order.timer), x - 3 * order.timer, y + (25 - order.timer));
            g2.drawLine(x - 20, y + 3 * order.timer, x + (25 - order.timer), y + 3 * order.timer);
            g2.drawLine(x - 20, y - 3 * order.timer, x + (25 - order.timer), y - 3 * order.timer);
        }
        synchronized (orders) {
            orders.removeAll(remove);
        }
    }

    /** Simple two-faction influence map rendered without the libtiled model. */
    private void paintInfluenceMap(Graphics2D g2) {
        int w = game.mapWidth();
        int h = game.mapHeight();
        HashMap<Integer, Double> playerInf = new HashMap<>();
        HashMap<Integer, Double> enemyInf = new HashMap<>();

        for (Unit unit : game.getAllUnits()) {
            accumulateInfluence(playerInf, unit.getX() / tileSize, unit.getY() / tileSize, w);
        }
        for (Unit unit : game.getAllUnits()) {
            accumulateInfluence(enemyInf, unit.getX() / tileSize, unit.getY() / tileSize, w);
        }
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int pos = y * w + x;
                double pi = playerInf.getOrDefault(pos, 0.0);
                double ei = enemyInf.getOrDefault(pos, 0.0);
                g2.setColor(new Color((float) ei, (float) pi, 0f));
                g2.fillRect(x * tileSize, y * tileSize, tileSize, tileSize);
            }
        }
    }

    private void accumulateInfluence(HashMap<Integer, Double> map, int cx, int cy, int mapW) {
        for (int dy = -5; dy <= 5; dy++) {
            for (int dx = -5; dx <= 5; dx++) {
                double dist = Math.sqrt(dx * dx + dy * dy);
                double delta = dist == 0 ? 0.5 : (dist < 5 ? 0.5 / dist : 0);
                if (delta == 0)
                    continue;
                int key = (cy + dy) * mapW + (cx + dx);
                map.put(key, Math.min(1.0, map.getOrDefault(key, 0.0) + delta));
            }
        }
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        try {
            double screenX = e.getX(), screenY = e.getY();
            AffineTransform t = new AffineTransform();
            t.translate(tx, ty);
            t.scale(scale, scale);
            double[] src = { screenX, screenY }, dest1 = new double[2];
            t.inverseTransform(src, 0, dest1, 0, 1);

            scale = e.getWheelRotation() > 0 ? scale * scaleAmount : scale / scaleAmount;

            t = new AffineTransform();
            t.translate(tx, ty);
            t.scale(scale, scale);
            src = new double[] { screenX, screenY };
            double[] dest2 = new double[2];
            t.inverseTransform(src, 0, dest2, 0, 1);

            tx += (dest2[0] - dest1[0]) * scale;
            ty += (dest2[1] - dest1[1]) * scale;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        repaint();
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (mouseDown) {
            tx += e.getX() - mx;
            ty += e.getY() - my;
            mx = e.getX();
            my = e.getY();
        }
        repaint();
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        if (game == null || scale == 0) {
            hoveredTileX = -1;
            hoveredTileY = -1;
        } else {
            double mapX = (e.getX() - tx) / scale;
            double mapY = (e.getY() - ty) / scale;
            hoveredTileX = (int) Math.floor(mapX / tileSize);
            hoveredTileY = (int) Math.floor(mapY / tileSize);
            if (hoveredTileX < 0 || hoveredTileY < 0
                    || hoveredTileX >= game.mapWidth() || hoveredTileY >= game.mapHeight()) {
                hoveredTileX = -1;
                hoveredTileY = -1;
            }
        }
        repaint();
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
        hoveredTileX = -1;
        hoveredTileY = -1;
        repaint();
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON3) {
            mouseDown = true;
            mx = e.getX();
            my = e.getY();
        }
        repaint();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON3)
            mouseDown = false;
        repaint();
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    // -------------------------------------------------------------------------
    // command queue pings (adapted from original, using new Command fields)
    // -------------------------------------------------------------------------

    private static class Order {
        int timer = -5;
        final int x, y;

        Order(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    private final ArrayList<Order> orders = new ArrayList<>();

    @Override
    public void update(List<Command> command) {
        // for (Command c : command) {
        // int px = -1, py = -1;
        // UnitCommandType order = command.order;

        // if (isPositionBased(order)) {
        // if (command.position != null) {
        // px = command.position.x / 32;
        // py = command.position.y / 32;
        // }
        // } else if (isTargetUnitBased(order)) {
        // Unit target = game.getUnit(command.targetId);
        // if (target != null) {
        // px = target.getX();
        // py = target.getY();
        // }
        // } else {
        // // self-unit commands (train, siege, research, …)
        // Unit unit = game.getUnit(command.unitId);
        // if (unit != null) {
        // px = unit.getX();
        // py = unit.getY();
        // }
        // }

        // if (px >= 0 && py >= 0)
        // orders.add(new Order(px, py));
        // }
    }

    private static boolean isPositionBased(UnitCommandType o) {
        return o == UnitCommandType.Attack_Move
                || o == UnitCommandType.Move
                || o == UnitCommandType.Patrol
                || o == UnitCommandType.Right_Click_Position
                || o == UnitCommandType.Use_Tech_Position
                || o == UnitCommandType.Build;
    }

    private static boolean isTargetUnitBased(UnitCommandType o) {
        return o == UnitCommandType.Attack_Unit
                || o == UnitCommandType.Right_Click_Unit
                || o == UnitCommandType.Follow
                || o == UnitCommandType.Use_Tech_Unit;
    }
}
