# Java Geometry Cheatsheet (`java.awt.geom`)

Everything below ships with the JDK — no dependency. Use it instead of hand-rolling
vector math for directions, distances, intersections, formations and threat areas.

```java
import java.awt.geom.*;   // Point2D, Line2D, Rectangle2D, Ellipse2D, Arc2D, Area, Path2D, AffineTransform
import java.awt.Shape;
```

**Rule of thumb:** all `java.awt.geom` types come in `.Float` / `.Double` flavours
(`Line2D.Double`, `Point2D.Float`, …). Prefer `.Double`. Most useful methods are
**`static` and take raw coordinates** — you can call them without allocating anything,
which matters in a per-frame bot loop.

> **Y axis points DOWN** in this coordinate system (screen/map convention, same as BWAPI).
> So "counter-clockwise" in method names reads as *clockwise* on screen, and positive
> angles from `atan2` rotate downward. Be consistent and it does not matter.

---

## 1. `Point2D` — points and vectors

A `Point2D` doubles as a **vector** (an offset from origin). There is no `Vector2D` in the JDK.

| Call | Kind | Description |
|---|---|---|
| `new Point2D.Double(x, y)` | ctor | Create point/vector |
| `Point2D.distance(x1,y1,x2,y2)` | **static** | Euclidean distance, no allocation |
| `Point2D.distanceSq(x1,y1,x2,y2)` | **static** | Squared distance — **use this for comparisons**, avoids `sqrt` |
| `p.distance(q)` / `p.distanceSq(q)` | instance | Same, on objects |
| `p.setLocation(x, y)` | instance | Mutate in place (reuse objects in hot loops) |
| `p.getX()` / `p.getY()` | instance | Coordinates as `double` |
| `p.equals(q)` | instance | Exact coordinate equality (beware float error — prefer `distanceSq < eps`) |

### Vector ops the JDK does *not* give you (write once, reuse)

```java
public final class Vec {
    private Vec() {}

    public static Point2D of(double x, double y)      { return new Point2D.Double(x, y); }
    public static Point2D add(Point2D a, Point2D b)   { return of(a.getX()+b.getX(), a.getY()+b.getY()); }
    public static Point2D sub(Point2D a, Point2D b)   { return of(a.getX()-b.getX(), a.getY()-b.getY()); }
    public static Point2D scale(Point2D a, double s)  { return of(a.getX()*s, a.getY()*s); }
    public static double  len(Point2D a)              { return Math.hypot(a.getX(), a.getY()); }
    public static double  lenSq(Point2D a)            { return a.getX()*a.getX() + a.getY()*a.getY(); }

    /** Unit vector; returns (0,0) for a zero vector instead of NaN. */
    public static Point2D norm(Point2D a) {
        double l = len(a);
        return l < 1e-9 ? of(0, 0) : of(a.getX()/l, a.getY()/l);
    }
    /** Vector of exactly `length` pointing from `from` to `to`. */
    public static Point2D towards(Point2D from, Point2D to, double length) {
        return scale(norm(sub(to, from)), length);
    }

    // Practical Example: Enemy Line-of-Sight & Field of View (FOV)A classic application of the dot product is determining stealth mechanics in a game. We can check if a player is standing in front of an enemy (within their vision cone) or behind them (sneaking up for a stealth attack).
    // When analyzing the result of dot(VectorA, VectorB):
    // - Positive Result (> 0): The vectors point in the same general direction (acute angle, less than 90°).
    // - Zero Result (= 0): The vectors are exactly perpendicular (90° right angle).
    // - Negative Result (< 0): The vectors point in opposite directions (obtuse angle, greater than 90°).
    public static double dot(Point2D a, Point2D b)   { return a.getX()*b.getX() + a.getY()*b.getY(); }

    // Practical Example: Steering AI (Left/Right Steering Decisions)A classic application in game development is an AI-controlled vehicle or spaceship following a path. The AI needs to make a binary decision: "Do I steer left or right to face my waypoint?"
    // The 3 Core Rules of the 2D Cross Product: When evaluating cross(VectorA, VectorB) (where Vector A is your forward base heading):
    // - Positive Result (> 0): Vector B is rotated counterclockwise relative to Vector A. In standard Screen/Cartesian coordinates, this means Vector B lies to the left of Vector A.
    // - Negative Result (< 0): Vector B is rotated clockwise relative to Vector A. This means Vector B lies to the right of Vector A.
    // - Zero Result (= 0): The vectors are collinear (parallel or anti-parallel). They point in the exact same or exact opposite direction.
    /** 2D cross (z of the 3D cross): sign tells you which side, |v| = 2 * triangle area. */
    public static double cross(Point2D a, Point2D b) { return a.getX()*b.getY() - a.getY()*b.getX(); }

    // Practical Example: Obstacle Avoidance (Flanking Behavior)A classic use case in game development and robotics navigation is obstacle avoidance. When an AI character runs directly toward a wall, it cannot continue forward. It needs to calculate a vector that points perpendicularly away from the wall to slide along it or steer clear.
    // The Rules of 2D Perpendicular Vectors. For any given 2D vector, there are always two perfect perpendicular choices—one pointing left, and one pointing right:
    // - Left Turn (Your Method): (-y, x) rotates the vector 90° counterclockwise.
    // - Right Turn: (y, -x) rotates the vector 90° clockwise.
    /** Rotate 90 degrees: cheap perpendicular, no trig. */
    public static Point2D perp(Point2D a)            { return of(-a.getY(), a.getX()); }

    // The lerp (Linear Interpolation) method calculates a point that lies somewhere along a straight line between point a and point b, based on a control value t (typically ranging from 0.0 to 1.0).
    // - When t = 0.0, the method returns point a.
    // - When t = 1.0, the method returns point b.
    // - When t = 0.5, the method returns the exact midpoint between a and b.Practical Example: Smooth Camera Tracking (Camera Lerp)
    /** Linear interpolation, t in [0,1]. */
    public static Point2D lerp(Point2D a, Point2D b, double t) {
        return of(a.getX() + (b.getX()-a.getX())*t, a.getY() + (b.getY()-a.getY())*t);
    }
    //  calculates the reflection vector of an incoming vector d (direction) off a surface with a given normal vector n (a vector pointing straight out of the surface).
    //  Practical Example: Projectile / Ball DeflectionImagine a classic Pong game or a breakout brick game. When a ball hits a wall, the physics engine needs to instantly calculate the new direction the ball should travel after the bounce.
    public static Point2D reflect(Point2D d, Point2D n) {
        return sub(d, scale(n, 2 * dot(d, n)));
    }
    /** Project `a` onto `b` (the component of a along b). */
    public static Point2D project(Point2D a, Point2D b) {
        double bb = lenSq(b);
        return bb < 1e-9 ? of(0, 0) : scale(b, dot(a, b) / bb);
    }

    // Die Methode clampLen (Begrenzung der Vektorlänge) stellt sicher, dass ein Vektor eine bestimmte Maximallänge (max) nicht überschreitet.
    public static Point2D clampLen(Point2D a, double max) {
        double l = len(a);
        return l <= max ? a : scale(a, max / l);
    }
}
```

---

## 2. Angles, directions & speed — `java.lang.Math`

| Call | Description |
|---|---|
| `Math.atan2(dy, dx)` | **The** direction function. Radians in `(-π, π]`. Note the argument order: **y first**. |
| `Math.hypot(dx, dy)` | Length, overflow-safe. (`Point2D.distance` is the same thing.) |
| `Math.toDegrees(rad)` / `Math.toRadians(deg)` | Conversion |
| `Math.cos(a)`, `Math.sin(a)` | Angle → unit vector: `(cos a, sin a)` |
| `Math.IEEEremainder(a, 2*PI)` | Wrap an angle into `[-π, π]` |
| `Math.signum(v)` | Sign of a cross product / turn direction |
| `Math.clamp(v, lo, hi)` | **Java 21+** — this project targets 21, so use it |
| `Math.floorDiv` / `Math.floorMod` | Correct pixel→tile conversion for negative coords |

```java
// direction from A to B, in radians
// - 0 Radians (0°): The target is pointing straight East (positive X-axis).
// - π/2 Radians (90°): The target is pointing straight North (positive Y-axis).
// - π Radians (180°): The target is pointing straight West (negative X-axis).
// - -π/2 Radians (-90°): The target is pointing straight South (negative Y-axis).
double heading = Math.atan2(b.getY() - a.getY(), b.getX() - a.getX());

// step `speed` pixels per frame along a heading
double nx = x + Math.cos(heading) * speed;
double ny = y + Math.sin(heading) * speed;

// smallest signed angle from a to b, in (-PI, PI] -> how much to turn, and which way
// When you compute Math.IEEEremainder(b - a, 2 * Math.PI), it automatically wraps the angular difference into the range (-π, π] (or -180° to 180°). This is the industry-standard way to find the shortest path to turn from a current heading to a target heading, avoiding the problem where an object spins the long way around (e.g., turning 270° clockwise instead of just 90° counterclockwise).
double delta = Math.IEEEremainder(b - a, 2 * Math.PI);

// angle between two vectors, unsigned, numerically stable
// This expression calculates the unsigned, numerically stable angle in radians between two 2D vectors u and v, strictly within the range [0, π] (0° to 180°).
// It is the industry-standard way to find the exact opening angle between two trajectories without caring which vector is to the left or right of the other.
// Practical Example: Enemy AI Alertness (Peripheral Vision)
double ang = Math.atan2(Math.abs(Vec.cross(u, v)), Vec.dot(u, v));

// "is the target within my 90-degree firing cone?"  (no trig needed)
boolean inCone = Vec.dot(Vec.norm(facing), Vec.norm(toTarget)) >= Math.cos(Math.toRadians(45));

// speed / ETA
double dist   = Point2D.distance(x1, y1, x2, y2);
int    frames = (int) Math.ceil(dist / pixelsPerFrame);

// intercept a moving target (first-order lead, good enough for aiming)
Point2D lead = Vec.add(targetPos, Vec.scale(targetVel, dist / projectileSpeed));
```

**8-way direction bucket** (useful for tile-grid movement):

```java
int dir8 = (int) Math.round(heading / (Math.PI / 4)) & 7;   // 0=E, 2=S, 4=W, 6=N (y-down)
```

---

## 3. `Line2D` — segments, sides, distances

| Call | Kind | Description |
|---|---|---|
| `new Line2D.Double(x1,y1,x2,y2)` | ctor | Segment |
| `Line2D.relativeCCW(x1,y1,x2,y2,px,py)` | **static** | Which side is P on? `1` (left side) / `-1` (right side) / `0` (collinear, including beyond the ends). Sign follows the y-down axis. |
| `Line2D.linesIntersect(x1,y1,x2,y2,x3,y3,x4,y4)` | **static** | Do two **segments** cross? (boolean only) |
| `Line2D.ptSegDist(x1,y1,x2,y2,px,py)` | **static** | Distance P → **segment** (clamped to the endpoints) If the closest point on the line falls outside the segment's endpoints, it correctly measures the distance directly to the nearest endpoint. |
| `Line2D.ptSegDistSq(...)` | **static** | Squared version — use for comparisons |
| `Line2D.ptLineDist(...)` | **static** | Distance P → **infinite line** |
| `Line2D.ptLineDistSq(...)` | **static** | Squared version |
| `l.intersects(Rectangle2D r)` | instance | Segment vs rectangle (line-of-sight through a box) |
| `l.intersectsLine(other)` | instance | Segment vs segment |
| `l.getP1()` / `getP2()` / `getBounds2D()` | instance | Accessors |

> `ptSegDist` vs `ptLineDist` is the single most common bug here. **Segment** = clamped to
> the endpoints (what you almost always want). **Line** = infinite in both directions.

```java
// which side of the squad's advance axis is this unit on? (flank assignment)
int side = Line2D.relativeCCW(squadX, squadY, targetX, targetY, unitX, unitY);

// straight-line danger: does my path pass near an enemy?
double d = Line2D.ptSegDist(fromX, fromY, toX, toY, enemyX, enemyY);
if (d < enemyRange) { /* reroute */ }
```

**Closest point on a segment** (the JDK gives the distance, not the point):

```java
static Point2D closestOnSegment(double x1, double y1, double x2, double y2, double px, double py) {
    double dx = x2 - x1, dy = y2 - y1, len2 = dx*dx + dy*dy;
    if (len2 < 1e-9) return new Point2D.Double(x1, y1);
    double t = Math.clamp(((px - x1) * dx + (py - y1) * dy) / len2, 0.0, 1.0);
    return new Point2D.Double(x1 + t*dx, y1 + t*dy);
}
```

**Actual intersection point of two segments** (the JDK only says yes/no):

```java
static Point2D segIntersection(Line2D a, Line2D b) {
    double x1 = a.getX1(), y1 = a.getY1(), x2 = a.getX2(), y2 = a.getY2();
    double x3 = b.getX1(), y3 = b.getY1(), x4 = b.getX2(), y4 = b.getY2();
    double den = (x1-x2)*(y3-y4) - (y1-y2)*(x3-x4);
    if (Math.abs(den) < 1e-9) return null;              // parallel or collinear
    double t = ((x1-x3)*(y3-y4) - (y1-y3)*(x3-x4)) / den;
    double u = ((x1-x3)*(y1-y2) - (y1-y3)*(x1-x2)) / den;
    if (t < 0 || t > 1 || u < 0 || u > 1) return null;  // crossing lies outside the segments
    return new Point2D.Double(x1 + t*(x2-x1), y1 + t*(y2-y1));
}
```

---

## 4. `Rectangle2D` / `Rectangle` — boxes, regions, hit tests

`Rectangle` is the **integer** version (`java.awt.Rectangle`) — a natural fit for tile maps and
BWAPI pixel coords. `Rectangle2D.Double` is the floating-point one. Both implement `Shape`.

| Call | Description |
|---|---|
| `new Rectangle2D.Double(x, y, w, h)` | **x,y is the top-left corner**, not the center |
| `r.setFrameFromDiagonal(x1,y1,x2,y2)` | Build from two arbitrary corners (handles negative w/h) |
| `r.contains(x, y)` / `r.contains(Point2D)` | Point inside? |
| `r.contains(Rectangle2D)` | Fully enclosed? |
| `r.intersects(x, y, w, h)` | Boxes overlap? (fast broad-phase test) |
| `r.intersectsLine(x1,y1,x2,y2)` | Segment crosses the box (line-of-sight blocking) |
| `r.createIntersection(other)` | **Overlap rectangle**. Degenerate when they miss — check `isEmpty()` |
| `r.createUnion(other)` | Smallest box containing both |
| `Rectangle2D.union(a,b,dest)` / `Rectangle2D.intersect(a,b,dest)` | **static**, writes into `dest` — no allocation |
| `r.add(x, y)` / `r.add(Rectangle2D)` | Grow to include a point/rect (bounding-box accumulation) |
| `r.getCenterX()` / `getCenterY()` / `getMinX()` / `getMaxX()` … | Derived coordinates |
| `r.outcode(x, y)` | Bitmask `OUT_LEFT / OUT_TOP / OUT_RIGHT / OUT_BOTTOM` — which side(s) P lies out on |
| `r.isEmpty()` | true when width or height ≤ 0 |
| `rect.grow(h, v)` | *(`java.awt.Rectangle` only)* inflate in place — handy for range margins |

```java
// bounding box of a squad — no allocation per unit
Rectangle2D box = new Rectangle2D.Double(first.getX(), first.getY(), 0, 0);
for (Unit u : squad) box.add(u.getX(), u.getY());
Point2D center = new Point2D.Double(box.getCenterX(), box.getCenterY());

// squad "spread" — cheap cohesion metric
double spread = Math.hypot(box.getWidth(), box.getHeight());

// centered box (common mistake: forgetting the -w/2)
Rectangle2D around = new Rectangle2D.Double(cx - w/2, cy - h/2, w, h);

// do two threat zones overlap, and by how much?
Rectangle2D ov = a.createIntersection(b);
if (!ov.isEmpty()) { double area = ov.getWidth() * ov.getHeight(); }
```

---

## 5. `Ellipse2D`, `Arc2D`, `RoundRectangle2D` — ranges and cones

| Call | Description |
|---|---|
| `new Ellipse2D.Double(x, y, w, h)` | Takes a **bounding box**, not center+radius |
| circle of radius `r` at `(cx,cy)` | `new Ellipse2D.Double(cx-r, cy-r, 2*r, 2*r)` |
| `e.contains(px, py)` | Inside the ellipse |
| `e.intersects(Rectangle2D)` | Circle vs box (may be conservative — see gotchas) |
| `new Arc2D.Double(x,y,w,h, start, extent, Arc2D.PIE)` | Pie wedge = **firing arc / vision cone**. Angles in **degrees, counter-clockwise, 0° = east** |
| `Arc2D.OPEN` / `CHORD` / `PIE` | Closure type; only `PIE` and `CHORD` have a meaningful "inside" |
| `a.setAngleStart(Point2D)` / `a.setAngles(...)` | Aim an arc at a point |
| `a.containsAngle(deg)` | Is this bearing within the wedge? |
| `new RoundRectangle2D.Double(x,y,w,h,arcw,arch)` | Rounded box — a decent capsule approximation |

> **Circle-vs-circle is faster by hand** — do not build an `Ellipse2D` for it:
> ```java
> boolean overlap = Point2D.distanceSq(x1,y1,x2,y2) <= (r1+r2)*(r1+r2);
> ```
> Same for "in weapon range": compare squared distances, skip the `sqrt` and the allocation.

```java
// vision cone: 90-degree wedge facing `heading`, radius r
// (screen y-down: negate the heading to match Arc2D's CCW-in-math-coords convention)
Arc2D cone = new Arc2D.Double(cx - r, cy - r, 2*r, 2*r,
                              -Math.toDegrees(heading) - 45, 90, Arc2D.PIE);
boolean seen = cone.contains(enemyX, enemyY);
```

---

## 6. `Area` — real boolean set operations (union / intersection / difference)

`Area` is the only JDK type that does **true** shape booleans. It is the heavy hammer: correct
for arbitrary shapes, but **allocating and slow** — never build one per unit per frame.

| Call | Description |
|---|---|
| `new Area(Shape)` | Wrap any shape (`Rectangle2D`, `Ellipse2D`, `Path2D`, …) |
| `a.add(b)` | **Union**, mutates `a` |
| `a.intersect(b)` | **Intersection** |
| `a.subtract(b)` | **Difference** (a minus b) |
| `a.exclusiveOr(b)` | Symmetric difference (XOR) |
| `a.contains(x,y)` / `a.contains(Rectangle2D)` | Exact point/region containment |
| `a.intersects(Rectangle2D)` | Overlap test |
| `a.isEmpty()` | Nothing left — e.g. no safe ground remains |
| `a.getBounds2D()` | Bounding box (cheap summary) |
| `a.createTransformedArea(AffineTransform)` | Transformed copy |
| `a.transform(AffineTransform)` | Transform in place |
| `a.isSingular()` | One connected piece? (detects a split region) |
| `a.getPathIterator(null)` | Walk the outline — the way to extract vertices |

```java
// threat map: everything covered by any enemy weapon range
Area threat = new Area();
for (Enemy e : enemies) {
    double r = e.range();
    threat.add(new Area(new Ellipse2D.Double(e.x() - r, e.y() - r, 2*r, 2*r)));
}

// the safe part of the region we want to hold
Area safe = new Area(regionShape);
safe.subtract(threat);
if (safe.isEmpty()) { /* retreat */ }

// pick a target point inside the safe area (Area has no centroid — sample its bounds)
Rectangle2D b = safe.getBounds2D();
Point2D goal = new Point2D.Double(b.getCenterX(), b.getCenterY());
if (!safe.contains(goal)) { /* fall back to scanning a grid over b */ }
```

**Extracting the outline points of an `Area`:**

```java
List<Point2D> outline = new ArrayList<>();
double[] c = new double[6];
for (PathIterator it = area.getPathIterator(null, 1.0);   // 1.0 = flatness, curves -> lines
     !it.isDone(); it.next()) {
    int type = it.currentSegment(c);
    if (type != PathIterator.SEG_CLOSE) outline.add(new Point2D.Double(c[0], c[1]));
}
```

---

## 7. `Path2D` — arbitrary polygons & polylines

| Call | Description |
|---|---|
| `new Path2D.Double()` | Empty path |
| `p.moveTo(x,y)` / `p.lineTo(x,y)` / `p.closePath()` | Build a polygon |
| `p.quadTo(...)` / `p.curveTo(...)` | Bézier curves (smooth movement paths) |
| `p.append(Shape, connect)` | Merge shapes |
| `p.contains(x,y)` | Point-in-polygon (uses the current winding rule) |
| `p.intersects(Rectangle2D)` | Overlap test |
| `p.setWindingRule(Path2D.WIND_NON_ZERO / WIND_EVEN_ODD)` | Fill rule for self-intersecting paths |
| `p.getCurrentPoint()` | Last point added |
| `p.createTransformedShape(AffineTransform)` | Transformed copy |
| `java.awt.Polygon` | Integer-only, simpler; has `contains(int,int)` and the `xpoints`/`ypoints` arrays |
| `new Area(path)` | Promote to a boolean-capable shape |

```java
// chokepoint / hull polygon from waypoints
Path2D zone = new Path2D.Double();
zone.moveTo(pts.get(0).getX(), pts.get(0).getY());
for (int i = 1; i < pts.size(); i++) zone.lineTo(pts.get(i).getX(), pts.get(i).getY());
zone.closePath();
boolean inside = zone.contains(unitX, unitY);
```

**Polygon area & centroid** (shoelace — the JDK has neither):

```java
static double signedArea(List<Point2D> p) {
    double a = 0;
    for (int i = 0, n = p.size(); i < n; i++) {
        Point2D u = p.get(i), v = p.get((i + 1) % n);
        a += u.getX()*v.getY() - v.getX()*u.getY();
    }
    return a / 2;   // the sign tells you the winding order
}

static Point2D centroid(List<Point2D> p) {   // area-weighted, not the vertex average
    double a = 0, cx = 0, cy = 0;
    for (int i = 0, n = p.size(); i < n; i++) {
        Point2D u = p.get(i), v = p.get((i + 1) % n);
        double cr = u.getX()*v.getY() - v.getX()*u.getY();
        a  += cr;
        cx += (u.getX() + v.getX()) * cr;
        cy += (u.getY() + v.getY()) * cr;
    }
    a /= 2;
    return Math.abs(a) < 1e-9 ? p.get(0) : new Point2D.Double(cx / (6*a), cy / (6*a));
}
```

---

## 8. `AffineTransform` — rotate / scale / translate whole formations

| Call | Description |
|---|---|
| `AffineTransform.getRotateInstance(theta)` | Rotate about the origin |
| `AffineTransform.getRotateInstance(theta, cx, cy)` | Rotate about a pivot — **formation rotation** |
| `AffineTransform.getRotateInstance(vx, vy)` | Rotate so +X maps onto vector `(vx,vy)` — no `atan2` needed |
| `AffineTransform.getTranslateInstance(dx, dy)` | Translate |
| `AffineTransform.getScaleInstance(sx, sy)` | Scale (mirror with a negative factor) |
| `t.transform(src, dst)` | Apply to a `Point2D` (`dst` may be `null` → allocates) |
| `t.deltaTransform(src, dst)` | Apply **without** the translation — the correct one for *vectors*/velocities |
| `t.createTransformedShape(Shape)` | Transformed copy of any shape |
| `t.concatenate(other)` / `t.preConcatenate(other)` | Compose (order matters: `concatenate` applies `other` first) |
| `t.createInverse()` | Inverse — world→local coords (throws `NoninvertibleTransformException`) |

```java
// lay out a wedge formation around the squad center, facing `heading`
AffineTransform place = AffineTransform.getRotateInstance(heading, cx, cy);
for (int i = 0; i < units.size(); i++) {
    Point2D local = new Point2D.Double(cx - i*spacing,
                                       cy + (i % 2 == 0 ? 1 : -1) * i * spacing / 2);
    Point2D world = place.transform(local, null);
    units.get(i).move(world);
}
```

---

## 9. `Shape` — the common interface

Every type above implements `java.awt.Shape`, so you can write code against it generically:

| Method | Note |
|---|---|
| `contains(double x, double y)` | Exact for all standard shapes |
| `contains(Rectangle2D)` | Fully inside |
| `intersects(Rectangle2D)` | **May answer `true` conservatively** for some shapes (the spec permits a bounding-box answer). Use `Area` when you need exactness. |
| `getBounds2D()` | Bounding box — cheap broad-phase filter |
| `getPathIterator(AffineTransform)` | Walk the geometry |
| `getPathIterator(AffineTransform, double flatness)` | Curves → line segments |

Broad-phase then narrow-phase is the standard pattern:

```java
if (shapeA.getBounds2D().intersects(shapeB.getBounds2D())) {    // cheap
    Area a = new Area(shapeA);
    a.intersect(new Area(shapeB));                              // expensive, exact
    if (!a.isEmpty()) { /* real overlap */ }
}
```

---

## 10. Bridging BWAPI `Position` ↔ `java.awt.geom`

BWAPI's `Position` / `TilePosition` / `WalkPosition` are integer pixel/tile coords with
`add` / `subtract` / `multiply` / `divide` / `getDistance` built in. Convert only where you
need real geometry, and convert back before issuing orders.

```java
static Point2D  toPoint(Position p)    { return new Point2D.Double(p.getX(), p.getY()); }
static Position toPosition(Point2D p)  { return new Position((int) Math.round(p.getX()),
                                                             (int) Math.round(p.getY())); }

static final int TILE = 32, WALK = 8;   // BWAPI: 32 px per build tile, 8 px per walk tile

static Rectangle tileRect(TilePosition t) {
    return new Rectangle(t.getX() * TILE, t.getY() * TILE, TILE, TILE);
}
static Point2D tileCenter(TilePosition t) {
    return new Point2D.Double(t.getX() * TILE + TILE / 2.0, t.getY() * TILE + TILE / 2.0);
}
static int toTile(int px) { return Math.floorDiv(px, TILE); }   // correct for negatives
```

Recipes in the shape this bot already sketches (`isDangerousCondition`, `ComputePositionTask`):

```java
Point2D squad = toPoint(squadPos);
Point2D enemy = toPoint(enemyPos);

// approach point: halfway from squad to enemy (the commented-out idiom, in geom form)
Position approach = toPosition(Vec.lerp(squad, enemy, 0.5));

// stand-off point: exactly `range` pixels short of the enemy, on the approach line
Position standoff = toPosition(Vec.sub(enemy, Vec.towards(squad, enemy, range)));

// kite: step directly away from the enemy
Position kiteTo = toPosition(Vec.add(squad, Vec.towards(enemy, squad, retreatStep)));

// flank: offset perpendicular to the advance axis, `side` from relativeCCW
Point2D axis   = Vec.norm(Vec.sub(enemy, squad));
Position flank = toPosition(Vec.add(enemy, Vec.scale(Vec.perp(axis), side * flankOffset)));

// is a squad member lagging behind the advance line through the center?
boolean behind = Vec.dot(axis, Vec.sub(toPoint(unitPos), squad)) < 0;

// ring formation around a center
for (int i = 0; i < n; i++) {
    double a = 2 * Math.PI * i / n;
    positions.add(new Position((int) (cx + Math.cos(a) * radius),
                               (int) (cy + Math.sin(a) * radius)));
}
```

---

## 11. Gotchas

| Trap | Fix |
|---|---|
| `Ellipse2D` / `Arc2D` take a **bounding box**, not center+radius | `new Ellipse2D.Double(cx-r, cy-r, 2*r, 2*r)` |
| `Rectangle2D(x,y,w,h)` — `x,y` is the **top-left corner** | Subtract `w/2, h/2` for a centered box |
| `Math.atan2` takes **y first** | `atan2(dy, dx)` |
| `Shape.intersects` may answer conservatively (bounding-box `true`) | Use `Area` intersection when you need exactness |
| `Area` operations allocate and are slow | Cache them; filter with `getBounds2D()` first; never per-unit per-frame |
| `Line2D.ptLineDist` treats the line as **infinite** | Use `ptSegDist` for segments |
| `createIntersection` never returns `null` when the boxes miss | Check `isEmpty()` — it returns a degenerate rect |
| `sqrt` in a hot loop | Compare `distanceSq` against `range * range` |
| `norm()` of a zero vector → `NaN`, silently poisoning everything downstream | Guard `len < 1e-9` |
| Integer division when converting pixel→tile with negative coords | `Math.floorDiv` |
| `Point2D.Float` precision loss accumulating over frames | Prefer `.Double` |
| `Arc2D` angles are **degrees, CCW, 0° = east**, while the world is y-down | Negate the heading when constructing the arc |
| `java.awt.geom` classes are **not thread-safe** and mostly mutable | Do not share `Area` / `Path2D` across threads; copy before mutating |
| `Point2D.equals` on doubles | Compare `distanceSq(a, b) < eps * eps` |

---

## 12. Picking the right tool

| Question | Use |
|---|---|
| How far apart? | `Point2D.distance` / `distanceSq` |
| Which direction / heading? | `Math.atan2(dy, dx)` |
| Which side of a line? | `Line2D.relativeCCW` |
| Left or right turn / winding order? | sign of `Vec.cross` |
| Facing toward or away? | sign of `Vec.dot` |
| Distance from a point to a path segment? | `Line2D.ptSegDist` |
| Do these two paths cross? | `Line2D.linesIntersect` (+ the snippet for the actual point) |
| Is it in weapon range? | `distanceSq <= r * r` |
| Is it in my firing cone? | `dot(norm(facing), norm(toTarget)) >= cos(halfAngle)` |
| Bounding box of a group? | `Rectangle2D.add` in a loop |
| Do two zones overlap, and where? | `Rectangle2D.createIntersection` → `isEmpty()` |
| Union of many threat circles? | `Area.add` |
| Safe ground = region minus threat? | `Area.subtract` |
| Rotate a whole formation? | `AffineTransform.getRotateInstance(theta, cx, cy)` |
| Point inside an arbitrary zone? | `Path2D.contains` / `Area.contains` |
| Smooth movement curve? | `Path2D.curveTo` + `getPathIterator(null, flatness)` |
