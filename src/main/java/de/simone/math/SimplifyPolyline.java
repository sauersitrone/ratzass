package de.simone.math;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

/**
 * Simplification of a 2D-polyline.
 * Example
 * // create an instance of the simplifier (empty array needed by List.toArray)
 * Simplify<Point> simplify = new Simplify<Point>(new MyPoint[0]);
 *
 * // here we have an array with hundreds of points
 * Point[] allPoints = ...
 * double tolerance = ...
 * boolean highQuality = true; // Douglas-Peucker, false for Radial-Distance
 *
 * // run simplification process
 * Point[] lessPoints = simplify.simplify(allPoints, tolerance, highQuality);
 *
 * @author hgoebl
 * @since 06.07.13
 */
public class SimplifyPolyline {

    /**
     * Simplifies a list of points to a shorter list of points.
     * 
     * @param points         original list of points
     * @param tolerance      tolerance in the same measurement as the point
     *                       coordinates
     * @param highestQuality <tt>true</tt> for using Douglas-Peucker only,
     *                       <tt>false</tt> for using Radial-Distance algorithm
     *                       before
     *                       applying Douglas-Peucker (should be a bit faster)
     * @return simplified list of points
     */
    public static List<Point2D> simplify(List<Point2D> points, double tolerance, boolean highestQuality) {
        if (points == null || points.size() <= 2) 
            return points;

        double sqTolerance = tolerance * tolerance;

        if (!highestQuality) {
            points = simplifyRadialDistance(points, sqTolerance);
        }

        points = simplifyDouglasPeucker(points, sqTolerance);

        return points;
    }

    private static List<Point2D> simplifyRadialDistance(List<Point2D> points, double sqTolerance) {
        Point2D point = null;
        Point2D prevPoint = points.get(0);

        List<Point2D> newPoints = new ArrayList<Point2D>();
        newPoints.add(prevPoint);

        for (int i = 1; i < points.size(); ++i) {
            point = points.get(i);

            if (getSquareDistance(point, prevPoint) > sqTolerance) {
                newPoints.add(point);
                prevPoint = point;
            }
        }

        if (prevPoint != point) 
            newPoints.add(point);

        return newPoints;
    }

    private static List<Point2D> simplifyDouglasPeucker(List<Point2D> points, double sqTolerance) {
        BitSet bitSet = new BitSet(points.size());
        bitSet.set(0);
        bitSet.set(points.size() - 1);

        List<Range> stack = new ArrayList<Range>();
        stack.add(new Range(0, points.size() - 1));

        while (!stack.isEmpty()) {
            Range range = stack.remove(stack.size() - 1);

            int index = -1;
            double maxSqDist = 0f;

            // find index of point with maximum square distance from first and last point
            for (int i = range.first + 1; i < range.last; ++i) {
                double sqDist = getSquareSegmentDistance(points.get(i), points.get(range.first),
                        points.get(range.last));

                if (sqDist > maxSqDist) {
                    index = i;
                    maxSqDist = sqDist;
                }
            }

            if (maxSqDist > sqTolerance) {
                bitSet.set(index);
                stack.add(new Range(range.first, index));
                stack.add(new Range(index, range.last));
            }
        }

        List<Point2D> newPoints = new ArrayList<Point2D>(bitSet.cardinality());
        for (int index = bitSet.nextSetBit(0); index >= 0; index = bitSet.nextSetBit(index + 1)) {
            newPoints.add(points.get(index));
        }

        return newPoints;
    }

    private static double getSquareDistance(Point2D p1, Point2D p2) {
        double dx = p1.getX() - p2.getX();
        double dy = p1.getY() - p2.getY();

        return dx * dx + dy * dy;
    }

    private static double getSquareSegmentDistance(Point2D p0, Point2D p1, Point2D p2) {
        double x0, y0, x1, y1, x2, y2, dx, dy, t;

        x1 = p1.getX();
        y1 = p1.getY();
        x2 = p2.getX();
        y2 = p2.getY();
        x0 = p0.getX();
        y0 = p0.getY();

        dx = x2 - x1;
        dy = y2 - y1;

        if (dx != 0.0d || dy != 0.0d) {
            t = ((x0 - x1) * dx + (y0 - y1) * dy) / (dx * dx + dy * dy);

            if (t > 1.0d) {
                x1 = x2;
                y1 = y2;
            } else if (t > 0.0d) {
                x1 += dx * t;
                y1 += dy * t;
            }
        }

        dx = x0 - x1;
        dy = y0 - y1;

        return dx * dx + dy * dy;
    }

    private static class Range {
        private Range(int first, int last) {
            this.first = first;
            this.last = last;
        }

        int first;
        int last;
    }
}