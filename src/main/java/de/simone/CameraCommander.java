package de.simone;

import bwapi.Position;
import bwapi.Unit;
import bwapi.UnitType;

public class CameraCommander {

    protected static int SCREEN_WIDTH = 640;
    protected static int SCREEN_HEIGHT = 480;
    protected static boolean focusCameraOnFirstCombatUnit = false;


    /**
     * Terry: todo: incorporate this method to handle the camera focus
     */
    public static void handle() {
        Unit cameraUnit = centerCameraOnUnit(UnitType.Terran_Marine); // Example unit type
        if (Ratzass._framesNow <= 1 || CameraCommander.isFocusCameraOnUnit()) {
            CameraCommander.centerCameraOn(cameraUnit);
        }
    }

    private static Unit centerCameraOnUnit(UnitType unitType) {
        Unit cameraUnit = Ratzass.game.getAllUnits().stream().filter(unit -> unit.getType() == unitType)
                .findFirst().orElse(null);

        if (cameraUnit == null) {
            System.out.println("No unit of type " + unitType + " found to center camera on.");
            return null;
        }
        return cameraUnit;

    }

    public static void centerCameraOn(Unit unit) {
        if (unit == null)
            return;

        Position position = RUtils.translateByPixels(unit, -SCREEN_WIDTH / 2, -SCREEN_HEIGHT * 3 / 7);
        Ratzass.game.setScreenPosition(position);
    }

    public static void toggleFocusCameraOnInterestingCombatUnit() {
        focusCameraOnFirstCombatUnit = !focusCameraOnFirstCombatUnit;
    }

    public static void focusCameraOnInterestingCombatUnit() {
        focusCameraOnFirstCombatUnit = true;
    }

    public static boolean isFocusCameraOnUnit() {
        return focusCameraOnFirstCombatUnit;
    }
}
