package de.simone.command;

import java.awt.Point;
import java.util.List;

import bwapi.UnitType;

public class StaffUtils {

    // terry: i donw nknow how to work with map. commentent for now implement laerr
    public static Point getRallyPoint(int x, int y) {

        List<RUnit> expos = UnitsCenter.getInstance().getUnits(UnitType.Terran_Command_Center);
        RUnit base = expos.get(0);

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
}
