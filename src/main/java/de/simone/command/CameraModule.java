package de.simone.command;

import java.util.List;

import bwapi.Game;
import bwapi.Position;
import bwapi.TilePosition;
import bwapi.Unit;
import bwapi.UnitType;
import de.simone.Config;

public class CameraModule {

    private static Position myStartLocation;
    private static int cameraMoveTime = 150;
    private static int cameraMoveTimeMin = 50;
    private static int watchScoutWorkerUntil = 7500;
    private static int lastMoved = 0;
    private static int lastMovedPriority = 0;
    private static Position currentCameraPosition;
    private static Position cameraFocusPosition;
    private static Unit cameraFocusUnit = null;
    private static boolean followUnit = false;
    private static Game game;

    private CameraModule() {
        //
    }
    
    public static void init() {
        TilePosition startPos = RBWListener.game.self().getStartLocation();
        myStartLocation = startPos.toPosition();
        cameraFocusPosition = startPos.toPosition();
        currentCameraPosition = startPos.toPosition();
        game = RBWListener.game;
    }

    public static void onFrame() {
        if (Config.autoCamera) {
            moveCameraFallingNuke();
            moveCameraIsUnderAttack();
            moveCameraIsAttacking();
            if (game.getFrameCount() <= watchScoutWorkerUntil)
                moveCameraScoutWorker();
            moveCameraArmy();
            moveCameraDrop();
            updateCameraPosition();
        }
    }

    private static boolean isNearStartLocation(Position pos) {
        int distance = 1000;
        List<TilePosition> startLocations = game.getStartLocations();
        for (TilePosition it : startLocations) {
            Position startLocation = it.toPosition();
            // if the start position is not our own home, and the start position is closer
            // than distance
            if (!isNearOwnStartLocation(startLocation) && startLocation.getDistance(pos) <= distance)
                return true;
        }
        return false;
    }

    private static boolean isNearOwnStartLocation(Position pos) {
        int distance = 10 * TilePosition.SIZE_IN_PIXELS;
        return (myStartLocation.getDistance(pos) <= distance);
    }

    private static boolean isArmyUnit(Unit unit) {
        UnitType type = unit.getType();
        return !(type.isWorker() || type.isBuilding() || type == UnitType.Terran_Vulture_Spider_Mine
                || type == UnitType.Zerg_Overlord || type == UnitType.Zerg_Larva);
    }

    private static boolean shouldMoveCamera(int priority) {
        boolean isTimeToMove = game.getFrameCount() - lastMoved >= cameraMoveTime;
        boolean isTimeToMoveIfHigherPrio = game.getFrameCount() - lastMoved >= cameraMoveTimeMin;
        boolean isHigherPrio = lastMovedPriority < priority;
        // camera should move IF: enough time has passed OR (minimum time has passed AND
        // new prio is higher)
        return isTimeToMove || (isHigherPrio && isTimeToMoveIfHigherPrio);
    }

    private static void moveCamera(Position pos, int priority) {
        if (!shouldMoveCamera(priority))
            return;
        // don't register a camera move if the position is the same
        if (!followUnit && cameraFocusPosition == pos)
            return;
        cameraFocusPosition = pos;
        lastMoved = game.getFrameCount();
        lastMovedPriority = priority;
        followUnit = false;
    }

    private static void moveCamera(Unit unit, int priority) {
        if (!shouldMoveCamera(priority))
            return;
        // don't register a camera move if we follow the same unit
        if (followUnit && cameraFocusUnit == unit)
            return;
        cameraFocusUnit = unit;
        lastMoved = game.getFrameCount();
        lastMovedPriority = priority;
        followUnit = true;
    }

    private static void moveCameraIsAttacking() {
        int prio = 3;
        if (!shouldMoveCamera(prio))
            return;
        for (Unit unit : UnitsCenter.getUnits()) {
            if (unit.isAttacking())
                moveCamera(unit, prio);
        }
    }

    private static void moveCameraIsUnderAttack() {
        int prio = 3;
        if (!shouldMoveCamera(prio))
            return;
        for (Unit unit : UnitsCenter.getUnits()) {
            if (unit.isUnderAttack())
                moveCamera(unit, prio);
        }
    }

    // terry: mybe set the squad to status = scouting to allow this method in the
    // queque from onframe()
    private static void moveCameraScoutWorker() {
        // int highPrio = 2;
        // int lowPrio = 0;
        // if (!shouldMoveCamera(lowPrio)) return;
        // for (Unit unit :UnitsCenter.getUnits()) {
        // if (!unit.exists() || !(unit instanceof Worker) || !unit.isCompleted())
        // continue;
        // if (isNearStartLocation(unit.getPosition())) moveCamera(unit, highPrio);
        // else if (!isNearOwnStartLocation(unit.getPosition())) moveCamera(unit,
        // lowPrio);
        // }
    }

    private static void moveCameraFallingNuke() {
        int prio = 5;
        if (!shouldMoveCamera(prio))
            return;
        for (Unit unit : game.getAllUnits()) {
            if (unit.getType() == UnitType.Terran_Nuclear_Missile && unit.getVelocityY() > 0) {
                moveCamera(unit, prio);
                return;
            }
        }
    }

    // terry: ????
    public static void moveCameraNukeDetect(Position target) {
        // int prio = 4;
        // if (shouldMoveCamera(prio))
        // moveCamera(target, prio);
    }

    private static void moveCameraDrop() {
        int prio = 2;
        if (!shouldMoveCamera(prio))
            return;
        for (Unit unit : UnitsCenter.getUnits()) {
            if (!unit.exists() || unit.isCompleted())
                continue;
            if (unit.getType() == UnitType.Terran_Dropship && isNearStartLocation(unit.getPosition())
                    && unit.getLoadedUnits().size() > 0) {
                moveCamera(unit, prio);
            }
        }
    }

    // terry: i need to set this method to squad. idk yet
    private static void moveCameraArmy() {
        // int prio = 1;
        // if (!shouldMoveCamera(prio))
        // return;
        // // Double loop, check if army units are close to each other
        // int radius = 50;
        // Unit bestPosUnit = null;
        // int mostUnitsNearby = 0;
        // for (Unit unit1 : game.getAllUnits()) {
        // if (!unit1.exists() || !(unit1 instanceof PlayerUnit) || !isArmyUnit(unit1))
        // continue;
        // int nrUnitsNearby = 0;
        // for (Unit unit2 : unit1.getUnitsInRadius(radius, game.getAllUnits())) {
        // if (!unit2.exists() || !(unit2 instanceof PlayerUnit) || !isArmyUnit(unit2))
        // continue;
        // nrUnitsNearby++;
        // }
        // if (nrUnitsNearby > mostUnitsNearby) {
        // mostUnitsNearby = nrUnitsNearby;
        // bestPosUnit = unit1;
        // }
        // }
        // if (mostUnitsNearby > 1)
        // moveCamera(bestPosUnit, prio);
    }

    // terry: public methods should not exist. only controled by autocamera config
    // variable
    public static void moveCameraUnitCompleted(Unit unit) {
        // if (enabled && unit != null) {
        // int prio = 1;
        // if (shouldMoveCamera(prio) && unit instanceof PlayerUnit && ((PlayerUnit)
        // unit).getPlayer().equals(self)
        // && !(unit instanceof Worker)) {
        // moveCamera(unit, prio);
        // }
        // }
    }

    private static void updateCameraPosition() {
        double moveFactor = 0.1;
        if (followUnit && cameraFocusUnit.getPosition().isValid(game)) {
            cameraFocusPosition = cameraFocusUnit.getPosition();
        }
        Position nexPostition = new Position(
                (int) (moveFactor * (cameraFocusPosition.getX() - currentCameraPosition.getX())),
                (int) (moveFactor * (cameraFocusPosition.getY() - currentCameraPosition.getY())));

        currentCameraPosition = currentCameraPosition.add(nexPostition);
        Position currentMovedPosition = currentCameraPosition
                .subtract(new Position(StarCraftConstants.scrWidth / 2, StarCraftConstants.scrHeight / 2 - 40));
        // Position currentMovedPosition = new Position(currentCameraPosition.getX() -
        // scrWidth / 2, currentCameraPosition.getY() - scrHeight / 2 - 40); // -40 to
        // account for HUD
        if (currentCameraPosition.isValid(game))
            game.setScreenPosition(currentMovedPosition);
    }
}
