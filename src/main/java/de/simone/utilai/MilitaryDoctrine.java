package de.simone.utilai;

import java.util.List;
import java.util.Optional;

import bwapi.TilePosition;
import bwapi.WalkPosition;
import bwem.Area;
import bwem.BWMap;
import bwem.ChokePoint;

/**
 * Decides, with a {@link UtilitySelector}, whether building bunkers is viable
 * for the current base. Bunkers pay off when the base is isolated behind one or
 * two narrow choke points, and are a poor investment in an open region.
 */
public class MilitaryDoctrine {

    /** Terrain summary of the base area; independent of BWEM to ease testing. */
    public record BaseTerrain(int chokePointCount, double averageChokeWidthTiles) {
    }

    public enum Decision {
        BUILD_BUNKERS, SKIP_BUNKERS
    }

    /** Score the "skip" option gets; bunkers must beat it to be viable. */
    private static final double SKIP_BASELINE = 0.35;
    /** A choke wider than this (in tiles) is practically open ground. */
    private static final double MAX_CHOKE_WIDTH_TILES = 24;

    private final UtilitySelector<BaseTerrain> selector = new UtilitySelector<>();

    public MilitaryDoctrine() {
        UtilityAction<BaseTerrain> buildBunkers = new UtilityAction<>(Decision.BUILD_BUNKERS.name(), t -> {
        });
        Consideration<BaseTerrain> c1 = new Consideration<>("chokePointCount",
                t -> chokeCountSuitability(t.chokePointCount()), 0, 1, Curve.linear());
        Consideration<BaseTerrain> c2 = new Consideration<>("chokeWidth", BaseTerrain::averageChokeWidthTiles, 0,
                MAX_CHOKE_WIDTH_TILES, Curve.logistic(10, 0.5).inverted());

        buildBunkers.add(c1);
        buildBunkers.add(c2);
        selector.add(buildBunkers);

        selector.add(new UtilityAction<BaseTerrain>(Decision.SKIP_BUNKERS.name(), t -> {
        }).weight(SKIP_BASELINE));
    }

    /**
     * 1 or 2 chokes: ideal (2 slightly less). 0 means no usable chokes
     * (open/unknown), many chokes means an open region.
     */
    private static double chokeCountSuitability(int count) {
        return switch (count) {
            case 1 -> 1.0;
            case 2 -> 0.85;
            case 3 -> 0.35;
            case 4 -> 0.15;
            default -> 0.0;
        };
    }

    public Decision evaluate(BaseTerrain terrain) {
        return selector.select(terrain).map(a -> Decision.valueOf(a.getName())).orElse(Decision.SKIP_BUNKERS);
    }

    public boolean shouldBuildBunkers(BaseTerrain terrain) {
        return evaluate(terrain) == Decision.BUILD_BUNKERS;
    }

    /** Analyzes the area containing the given start location using BWEM. */
    public static Optional<BaseTerrain> analyze(BWMap map, TilePosition startLocation) {
        Area area = map.getArea(startLocation);
        if (area == null) {
            return Optional.empty();
        }
        List<ChokePoint> chokes = area.getChokePoints().stream().filter(c -> !c.isBlocked()).toList();
        double avgWidth = chokes.stream().mapToDouble(MilitaryDoctrine::widthInTiles).average().orElse(0);
        return Optional.of(new BaseTerrain(chokes.size(), avgWidth));
    }

    /** Evaluates bunker viability for the area around the given start location. */
    public Decision evaluate(BWMap map, TilePosition startLocation) {
        return analyze(map, startLocation).map(this::evaluate).orElse(Decision.SKIP_BUNKERS);
    }

    private static double widthInTiles(ChokePoint choke) {
        List<WalkPosition> geometry = choke.getGeometry();
        if (geometry.size() < 2) {
            return 0;
        }
        // geometry end points are the two sides of the choke; 4 walk positions = 1 tile
        return geometry.get(0).getDistance(geometry.get(geometry.size() - 1)) / 4.0;
    }
}
