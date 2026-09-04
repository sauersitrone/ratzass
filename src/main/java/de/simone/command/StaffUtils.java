package de.simone.command;

import java.awt.Point;
import java.util.List;

import bwapi.UnitType;

public class StaffUtils {

    // terry: i donw nknow how to work with map. commentent for now implement laerr
    public static Point getRallyPoint(int x, int y) {

        List<UnitDocument> expos = UnitsCenter.getInstance().getDocuments(UnitType.Terran_Command_Center);
        UnitDocument base = expos.get(0);

        return new Point(base.position.x, base.position.y);
    }

    public static Squad getNearestSquad(Point point) {
        Squad nearest = null;
        double nearestDistance = Double.MAX_VALUE;

        for (Squad squad : UnitsCenter.getInstance().getSquads()) {
            Point center = squad.getCenter(false);
            if (center == null) {
                continue;
            }
            double dx = center.x - point.x;
            double dy = center.y - point.y;
            double distance = Math.sqrt(dx * dx + dy * dy);
            if (distance < nearestDistance) {
                nearestDistance = distance;
                nearest = squad;
            }
        }

        return nearest;
    }

        public static double distance(double x1, double y1, double x2, double y2) {
        final double dx = x1 - x2;
        final double dy = y1 - y2;
        return Math.sqrt(dx * dx + dy * dy);
    }
}
