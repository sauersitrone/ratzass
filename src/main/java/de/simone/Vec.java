package de.simone;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for 2D vector operations using Point2D.
 */
public class Vec {

    private Vec() {
        //
    }

    public static Point2D of(double x, double y) {
        return new Point2D.Double(x, y);
    }

    public static Point2D add(Point2D a, Point2D b) {
        return of(a.getX() + b.getX(), a.getY() + b.getY());
    }

    public static Point2D sub(Point2D a, Point2D b) {
        return of(a.getX() - b.getX(), a.getY() - b.getY());
    }

    public static Point2D scale(Point2D a, double s) {
        return of(a.getX() * s, a.getY() * s);
    }

    public static double len(Point2D a) {
        return Math.hypot(a.getX(), a.getY());
    }

    public static double lenSq(Point2D a) {
        return a.getX() * a.getX() + a.getY() * a.getY();
    }

    /**
     * Unit vector; returns (0,0) for a zero vector instead of NaN.
     * 
     * @param a - a point
     * @return the unit vector
     */
    public static Point2D norm(Point2D a) {
        double l = len(a);
        return l < 1e-9 ? of(0, 0) : of(a.getX() / l, a.getY() / l);
    }

    /**
     * Vector of exactly `length` pointing from `from` to `to`.
     * 
     * @param from   - from
     * @param to     - to
     * @param length - the length
     * @return the vector
     */
    public static Point2D towards(Point2D from, Point2D to, double length) {
        return scale(norm(sub(to, from)), length);
    }

    /**
     * Practical Example: Enemy Line-of-Sight & Field of View (FOV)A classic
     * application of the dot product is determining stealth mechanics in a game. We
     * can check if a player is standing in front of an enemy (within their vision
     * cone) or behind them (sneaking up for a stealth attack). When analyzing the
     * result of dot(VectorA, VectorB):
     * - Positive Result (> 0): The vectors point in the same general direction
     * (acute angle, less than 90°).
     * - Zero Result (= 0): The vectors are exactly perpendicular (90° right angle).
     * - Negative Result (< 0): The vectors point in opposite directions (obtuse
     * angle, greater than 90°).
     *
     * @param a - a point
     * @param b - another point
     * @return the dot product
     */
    public static double dot(Point2D a, Point2D b) {
        return a.getX() * b.getX() + a.getY() * b.getY();
    }

    /**
     * 2D cross (z of the 3D cross): sign tells you which side, |v| = 2 * triangle
     * area.
     * Practical Example: Steering AI (Left/Right Steering Decisions)A classic
     * application in game development is an AI-controlled vehicle or spaceship
     * following a path. The AI needs to make a binary decision: "Do I steer left or
     * right to face my waypoint?" The 3 Core Rules of the 2D Cross Product: When
     * evaluating cross(VectorA, VectorB) (where Vector A is your forward base
     * heading):
     * - Positive Result (> 0): Vector B is rotated counterclockwise relative to
     * Vector A. In standard Screen/Cartesian coordinates, this means Vector B lies
     * to the left of Vector A.
     * - Negative Result (< 0): Vector B is rotated clockwise relative to Vector A.
     * This means Vector B lies to the right of Vector A.
     * - Zero Result (= 0): The vectors are collinear (parallel or anti-parallel).
     * They point in the exact same or exact opposite direction.
     * 
     * @param a - a point
     * @param b - another point
     * @return the z-component of the 3D cross product
     */
    public static double cross(Point2D a, Point2D b) {
        return a.getX() * b.getY() - a.getY() * b.getX();
    }

    /**
     * Rotate 90 degrees: cheap perpendicular, no trig.
     * Practical Example: Obstacle Avoidance (Flanking Behavior)A classic use case
     * in game development and robotics navigation is obstacle avoidance. When an AI
     * character runs directly toward a wall, it cannot continue forward. It needs
     * to calculate a vector that points perpendicularly away from the wall to slide
     * along it or steer clear. The Rules of 2D Perpendicular Vectors. For any given
     * 2D vector, there are always two perfect perpendicular choices—one pointing
     * left, and one pointing right:
     * - Left Turn (Your Method): (-y, x) rotates the vector 90° counterclockwise.
     * - Right Turn: (y, -x) rotates the vector 90° clockwise.
     * 
     * @param a - a point
     * @return a point that is perpendicular to the input point
     */
    public static Point2D perp(Point2D a) {
        return of(-a.getY(), a.getX());
    }

    /**
     * Linear interpolation, t in [0,1]. The method
     * calculates a point that lies somewhere along a straight line between point a
     * and point b, based on a control value t (typically ranging from 0.0 to 1.0).
     * - When t = 0.0, the method returns point a.
     * - When t = 1.0, the method returns point b.
     * - When t = 0.5, the method returns the exact midpoint between a and b.
     * Practical Example: Smooth Camera Tracking (Camera Lerp)
     * 
     * @param a - a point
     * @param b - another point
     * @param t - the interpolation factor, typically in the range [0, 1]
     * @return the interpolated point between a and b based on t
     */
    public static Point2D lerp(Point2D a, Point2D b, double t) {
        double x = a.getX() + (b.getX() - a.getX()) * t;
        double y = a.getY() + (b.getY() - a.getY()) * t;
        return of(x, y);
    }

    /**
     * Calculates the reflection vector of an incoming vector d (direction) off a
     * surface with a given normal vector n (a vector pointing straight out of the
     * surface).
     * Practical Example: Projectile / Ball DeflectionImagine a classic Pong game or
     * a breakout brick game. When a ball hits a wall, the physics engine needs to
     * instantly calculate the new direction the ball should travel after the
     * bounce.
     * 
     * @param d - the incoming direction vector
     * @param n - the normal vector of the surface
     * @return the reflected vector
     */
    public static Point2D reflect(Point2D d, Point2D n) {
        return sub(d, scale(n, 2 * dot(d, n)));
    }

    /**
     * Project `a` onto `b` (the component of a along b).
     * 
     * @param a - the vector to be projected
     * @param b - the vector onto which a is projected
     * @return the projection of a onto b
     */
    public static Point2D project(Point2D a, Point2D b) {
        double bb = lenSq(b);
        return bb < 1e-9 ? of(0, 0) : scale(b, dot(a, b) / bb);
    }

    /**
     * Die Methode clampLen (Begrenzung der Vektorlänge) stellt sicher, dass ein
     * Vektor eine bestimmte Maximallänge (max) nicht überschreitet.
     * 
     * @param a   - the vector
     * @param max - the maximum allowed length
     * @return the vector with its length clamped
     */
    public static Point2D clampLen(Point2D a, double max) {
        double l = len(a);
        return l <= max ? a : scale(a, max / l);
    }

    /**
     * return the list of intersection points of a circle with the given center and
     * radius.
     * 
     * @param center - the center
     * @param r      - the radius
     * @return the list of
     */
    public static List<Point2D> getIntersectionPoints(Point2D center, int r) {
        if (r < 0) {
            throw new IllegalArgumentException("Radius must not be negative");
        }

        List<Point2D> points = new ArrayList<>(6);
        for (int degree = 0; degree < 360; degree += 60) {
            double angle = Math.toRadians(degree);
            Point2D point2d = of(center.getX() + r * Math.cos(angle), center.getY() + r * Math.sin(angle));
            points.add(point2d);
        }
        return points;
    }
}
