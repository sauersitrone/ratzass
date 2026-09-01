package de.simone.gui;

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
import bwem.ChokePoint;
import de.simone.Env;
import de.simone.RBWListener;
import de.simone.command.Command;
import de.simone.command.CommandQueueListener;
import de.simone.command.RUnit;
import de.simone.command.UnitsCenter;

/**
 * Tiled-map-backed GUI showing the ProxyBot's view of the game state.
 * Uses org.mapeditor.libtiled to represent terrain as a TileLayer
 * and game entities as ObjectGroups; rendering reads back from those
 * structures.
 */
public class StarCraftTileMap extends JPanel
        implements MouseWheelListener, MouseMotionListener, MouseListener, CommandQueueListener {

    private Game game;

    /** pixels per map tile in the display */
    private int tileSize = 6;
    /** height of the resource status bar at the top */
    private int panelHeight = 30;
    private int textSize = 10;

    private boolean influenceMap = false;

    // pan / zoom
    private double scale = 1.0;
    private double tx = 0, ty = 0;
    private int mx = 0, my = 0;
    private boolean mouseDown = false;
    private final Timer repaintTimer;
    private long lastRedraw = 0;
    double scaleAmount = 0.9;

    // -------------------------------------------------------------------------
    // libtiled data model
    // -------------------------------------------------------------------------

    /** Root Tiled map – owns all layers */
    private Map tiledMap;

    /** TileSet with four colour-coded terrain tile variants */
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
        tiledMap.addLayer(terrainLayer);

        // one ObjectGroup per entity category; colour is stored as hex string
        regionsGroup = addObjectGroup("regions", "#ffa500");
        chokepointsGroup = addObjectGroup("chokepoints", "#8b008b");
        startSpotsGroup = addObjectGroup("starting_locations", "#ffa500");
        mineralsGroup = addObjectGroup("minerals", "#00ffff");
        geysersGroup = addObjectGroup("geysers", "#008000");
        enemyUnitsGroup = addObjectGroup("enemy_units", "#ff0000");
        allyUnitsGroup = addObjectGroup("ally_units", "#ffff00");
        neutralUnitsGroup = addObjectGroup("neutral_units", "#808080");

        mapInitialized = true;
    }

    private ObjectGroup addObjectGroup(String name, String hexColor) {
        ObjectGroup group = new ObjectGroup(tiledMap);
        group.setName(name);
        group.setColor(hexColor);
        tiledMap.addLayer(group);
        return group;
    }

    // -------------------------------------------------------------------------
    // per-frame model refresh
    // -------------------------------------------------------------------------

    private void updateTiledMap() {
        if (Env.ignoreBases)
            return;

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
                    int brightness = 70 * (walkable ? 1 : 0)
                            + 60 * (buildable ? 1 : 0)
                            + 50 * height;
                    brightness = Math.max(0, Math.min(255, brightness));
                    idx = brightness < 50 ? T_DARK : brightness < 110 ? T_MID : T_LIGHT;
                }
                terrainLayer.setTileAt(x, y, terrainTiles[idx]);
            }
        }
    }

    /** Rebuilds every ObjectGroup from current game state. */
    private void refreshObjectGroups() {
        refreshRegions();
        refreshChokepoints();
        refreshStartSpots();
        refreshResources();
        refreshUnits();
    }

    private void refreshRegions() {
        regionsGroup.getObjects().clear();
        if (!Env.drawRegions)
            return;

        for (Region region : game.getAllRegions()) {
            // JBWAPI Region exposes a bounding box, not raw polygon points
            int x = region.getBoundsLeft() / 32 * tileSize;
            int y = region.getBoundsTop() / 32 * tileSize;
            int w = (region.getBoundsRight() - region.getBoundsLeft()) / 32 * tileSize;
            int h = (region.getBoundsBottom() - region.getBoundsTop()) / 32 * tileSize;
            MapObject obj = new MapObject(x, y, w, h, 0);
            obj.setType("region");
            regionsGroup.addObject(obj);
        }
    }

    private void refreshChokepoints() {
        chokepointsGroup.getObjects().clear();
        if (!Env.drawChokepoints)
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

            MapObject obj = new MapObject(cx, cy, diameter, diameter, 0);
            obj.setShape(new Ellipse2D.Double(0, 0, diameter, diameter));
            obj.setType("chokepoint");
            chokepointsGroup.addObject(obj);
        }
    }

    private void refreshStartSpots() {
        startSpotsGroup.getObjects().clear();
        if (!Env.drawStartSpots)
            return;

        for (TilePosition loc : game.getStartLocations()) {
            MapObject obj = new MapObject(loc.x * tileSize, loc.y * tileSize,
                    4.0 * tileSize, 3.0 * tileSize, 0);
            obj.setType("starting_location");
            startSpotsGroup.addObject(obj);
        }
    }

    private void refreshResources() {
        mineralsGroup.getObjects().clear();
        geysersGroup.getObjects().clear();
        if (!Env.drawResources)
            return;

        for (Unit mineral : game.getMinerals()) {
            MapObject obj = new MapObject(mineral.getX() * tileSize, mineral.getY() * tileSize,
                    tileSize, tileSize, 0);
            obj.setType("mineral");
            mineralsGroup.addObject(obj);
        }
        for (Unit geyser : game.getGeysers()) {
            MapObject obj = new MapObject(geyser.getX() * tileSize, geyser.getY() * tileSize,
                    geyser.getType().tileWidth() * tileSize,
                    geyser.getType().tileHeight() * tileSize, 0);
            obj.setType("geyser");
            geysersGroup.addObject(obj);
        }
    }

    private void refreshUnits() {
        enemyUnitsGroup.getObjects().clear();
        if (Env.drawEnemyUnits) {
            for (RUnit unit : UnitsCenter.getInstance().getEnemyUnits()) {
                MapObject obj = new MapObject(unit.position.x * tileSize, unit.position.y * tileSize,
                        unit.unitType.tileWidth() * tileSize,
                        unit.unitType.tileHeight() * tileSize, 0);
                obj.setType("enemy_unit");
                if (Env.drawIDs)
                    obj.setName(String.valueOf(unit.unitID));
                enemyUnitsGroup.addObject(obj);
            }
        }

        allyUnitsGroup.getObjects().clear();
        if (Env.drawPlayerUnits) {
            for (RUnit unit : UnitsCenter.getInstance().getUnits()) {
                MapObject obj = new MapObject(unit.position.x * tileSize, unit.position.y * tileSize,
                        unit.unitType.tileWidth() * tileSize,
                        unit.unitType.tileHeight() * tileSize, 0);
                obj.setType("ally_unit");
                if (Env.drawIDs)
                    obj.setName(String.valueOf(unit.unitID));
                allyUnitsGroup.addObject(obj);
            }
        }

        neutralUnitsGroup.getObjects().clear();
        if (Env.drawNeutralUnits) {
            for (Unit unit : game.getNeutralUnits()) {
                MapObject obj = new MapObject(unit.getX() * tileSize, unit.getY() * tileSize,
                        unit.getType().tileWidth() * tileSize,
                        unit.getType().tileHeight() * tileSize, 0);
                obj.setType("neutral_unit");
                if (Env.drawIDs)
                    obj.setName(String.valueOf(unit.getID()));
                neutralUnitsGroup.addObject(obj);
            }
        }
    }

    // -------------------------------------------------------------------------
    // rendering – reads from the libtiled model
    // -------------------------------------------------------------------------

    @Override
    public void paint(Graphics g) {
        if (System.currentTimeMillis() > (1000 + lastRedraw)) {
            orders.clear();
        }
        lastRedraw = System.currentTimeMillis();

        Graphics2D g2 = (Graphics2D) g;
        g2.setBackground(Color.BLACK);
        g2.clearRect(0, 0, getWidth(), getHeight());
        g2.translate(tx, ty);
        g2.scale(scale, scale);

        if (!influenceMap) {
            if (!Env.ignoreBases && mapInitialized) {
                paintTerrainLayer(g2);
                if (Env.drawRegions)
                    paintObjectGroupOutline(g2, regionsGroup);

                if (Env.drawChokepoints)
                    paintObjectGroupEllipses(g2, chokepointsGroup);

                if (Env.drawStartSpots)
                    paintObjectGroupFilled(g2, startSpotsGroup);
            }
            if (Env.drawResources && mapInitialized) {
                paintObjectGroupFilled(g2, mineralsGroup);
                paintObjectGroupFilled(g2, geysersGroup);
            }

            if (Env.drawEnemyUnits && mapInitialized)
                paintObjectGroupFilled(g2, enemyUnitsGroup);

            if (Env.drawPlayerUnits && mapInitialized)
                paintObjectGroupFilled(g2, allyUnitsGroup);

            if (Env.drawNeutralUnits && mapInitialized)
                paintObjectGroupFilled(g2, neutralUnitsGroup);

            if (Env.drawIDs && mapInitialized)
                paintUnitLabels(g2);

            if (Env.drawPings)
                paintPings(g2);
        } else {
            paintInfluenceMap(g2);
        }

        g2.scale(1.0 / scale, 1.0 / scale);
        g2.translate(-tx, -ty);
        paintStatusPanel(g);
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
                    if (hex != null)
                        c = Color.decode(hex);
                }
                g2.setColor(c);
                g2.fillRect(x * tileSize, panelHeight + y * tileSize, tileSize, tileSize);
            }
        }
    }

    /** Draws ObjectGroup objects as rectangle outlines (regions). */
    private void paintObjectGroupOutline(Graphics2D g2, ObjectGroup group) {
        Color c = decodeGroupColor(group, Color.ORANGE);
        for (MapObject mo : group) {
            if (Env.fillRegions) {
                g2.setColor(c.darker());
                g2.fillRect((int) mo.getX(), panelHeight + (int) mo.getY(),
                        mo.getWidth().intValue(), mo.getHeight().intValue());
            }
            g2.setColor(c);
            g2.drawRect((int) mo.getX(), panelHeight + (int) mo.getY(),
                    mo.getWidth().intValue(), mo.getHeight().intValue());
        }
    }

    /** Draws ObjectGroup objects whose shape is an Ellipse2D (chokepoints). */
    private void paintObjectGroupEllipses(Graphics2D g2, ObjectGroup group) {
        g2.setColor(decodeGroupColor(group, Color.MAGENTA.darker()));
        for (MapObject mo : group) {
            g2.drawArc((int) mo.getX(), panelHeight + (int) mo.getY(),
                    mo.getWidth().intValue(), mo.getHeight().intValue(), 0, 360);
        }
    }

    /**
     * Draws ObjectGroup objects as filled rectangles (units, resources, start
     * spots).
     */
    private void paintObjectGroupFilled(Graphics2D g2, ObjectGroup group) {
        g2.setColor(decodeGroupColor(group, Color.WHITE));
        for (MapObject mo : group) {
            g2.fillRect((int) mo.getX(), panelHeight + (int) mo.getY(),
                    mo.getWidth().intValue(), mo.getHeight().intValue());
        }
    }

    /** Draws the unit ID label stored in MapObject.getName() for unit groups. */
    private void paintUnitLabels(Graphics2D g2) {
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.PLAIN, textSize));
        for (ObjectGroup group : new ObjectGroup[] { allyUnitsGroup, enemyUnitsGroup, neutralUnitsGroup }) {
            for (MapObject mo : group) {
                String label = mo.getName();
                if (label != null && !label.isEmpty()) {
                    g2.drawString(label, (int) mo.getX(),
                            panelHeight + (int) mo.getY() + textSize - 2);
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
            int y = panelHeight + order.y * tileSize + tileSize / 2;
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

        for (RUnit unit : UnitsCenter.getInstance().getUnits()) {
            accumulateInfluence(playerInf, unit.position.x, unit.position.y, w);
        }
        for (RUnit unit : UnitsCenter.getInstance().getEnemyUnits()) {
            accumulateInfluence(enemyInf, unit.position.x, unit.position.y, w);
        }
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int pos = y * w + x;
                double pi = playerInf.getOrDefault(pos, 0.0);
                double ei = enemyInf.getOrDefault(pos, 0.0);
                g2.setColor(new Color((float) ei, (float) pi, 0f));
                g2.fillRect(x * tileSize, panelHeight + y * tileSize, tileSize, tileSize);
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

    private void paintStatusPanel(Graphics g) {
        if (game == null)
            return;

        g.setColor(Color.WHITE);
        g.fillRect(0, 0, getWidth(), panelHeight);
        g.setFont(new Font("Arial", Font.PLAIN, 12));

        g.setColor(new Color(125, 125, 125));
        g.drawLine(0, panelHeight, getWidth(), panelHeight);

        // minerals
        g.setColor(new Color(0, 0, 255));
        g.fillRect(5, 10, 10, 10);
        g.setColor(Color.BLACK);
        g.drawRect(5, 10, 10, 10);
        g.drawString("" + game.self().minerals(), 25, 20);

        // gas
        g.setColor(new Color(0, 255, 0));
        g.fillRect(105, 10, 10, 10);
        g.setColor(Color.BLACK);
        g.drawRect(105, 10, 10, 10);
        g.drawString("" + game.self().gas(), 125, 20);

        // supply
        g.setColor(Color.BLACK);
        g.drawString((game.self().supplyUsed() / 2) + "/"
                + (game.self().supplyTotal() / 2), 200, 20);
    }

    private static Color decodeGroupColor(ObjectGroup group, Color fallback) {
        try {
            String hex = group.getColor();
            return hex != null ? Color.decode(hex) : fallback;
        } catch (NumberFormatException e) {
            return fallback;
        }
    }

    // -------------------------------------------------------------------------
    // mouse interaction (identical logic to StarCraftFrame)
    // -------------------------------------------------------------------------

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
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
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
        //     int px = -1, py = -1;
        //     UnitCommandType order = command.order;

        //     if (isPositionBased(order)) {
        //         if (command.position != null) {
        //             px = command.position.x / 32;
        //             py = command.position.y / 32;
        //         }
        //     } else if (isTargetUnitBased(order)) {
        //         Unit target = game.getUnit(command.targetId);
        //         if (target != null) {
        //             px = target.getX();
        //             py = target.getY();
        //         }
        //     } else {
        //         // self-unit commands (train, siege, research, …)
        //         Unit unit = game.getUnit(command.unitId);
        //         if (unit != null) {
        //             px = unit.getX();
        //             py = unit.getY();
        //         }
        //     }

        //     if (px >= 0 && py >= 0)
        //         orders.add(new Order(px, py));
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
