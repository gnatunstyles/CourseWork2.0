package logic;

import UI.Drawer;
import UI.Main;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.PI;
import static java.lang.Math.cos;
import static java.lang.StrictMath.sin;


public class Hexagon {

    private static final int COUNT_ANGLES = 6;
    private static int HEXAGON_RADIUS = 20;
    private static List<Hexagon> hexagons = new ArrayList<>();
    private static int currentCountCheckBomb = 0;
    private static int countBomb = 0;
    private List<Point> points = new ArrayList<>();
    private HexagonType hexagonType;
    private Point center;


    public Hexagon(HexagonType hexagonType, Point center) {
        this.hexagonType = hexagonType;
        this.center = center;
        createPointsOFHexagon(HEXAGON_RADIUS, center);
        hexagons.add(this);
        if (hexagonType == HexagonType.HIDDEN_BOMB) {
            countBomb++;
        }
    }

    private void createPointsOFHexagon(int radius, Point center) {
        int angle = 0;

        for (int i = 1; i < 14; i++) {
            if (i % 2 == 0) {
                Point point = new Point(center.getX() + radius * cos(angle * PI / 180),
                        center.getY() - radius * sin(angle * PI / 180));
                points.add(point);
            }
            angle += 180 / COUNT_ANGLES;
        }
    }

    public double[] getAllX() {
        double[] result = new double[COUNT_ANGLES];
        for (int i = 0; i < COUNT_ANGLES; i++) {
            result[i] = points.get(i).getX();
        }
        return result;
    }

    public double[] getAllY() {
        double[] result = new double[COUNT_ANGLES];
        for (int i = 0; i < COUNT_ANGLES; i++) {
            result[i] = points.get(i).getY();
        }
        return result;
    }

    private int getNumberOfBombNeardy() {
        List<Hexagon> adjacentHexagons = getAdjacentHexagon();
        int numberOfBomb = 0;
        for (Hexagon hexagon : adjacentHexagons) {
            if (hexagon.hexagonType == HexagonType.HIDDEN_BOMB) {
                numberOfBomb++;
            }
        }
        return numberOfBomb;
    }

    private List<Hexagon> getAdjacentHexagon() {
        List<Hexagon> adjacentHexagons = new ArrayList<>();
        Point[] hexagonsPoint = new Point[COUNT_ANGLES];
        int diameter = HEXAGON_RADIUS * 2;


        hexagonsPoint[0] = new Point(center.getX() + diameter / 2, center.getY() + diameter);
        hexagonsPoint[1] = new Point(center.getX() + diameter / 2, center.getY() - diameter);
        hexagonsPoint[2] = new Point(center.getX() - diameter / 2, center.getY() - diameter);
        hexagonsPoint[3] = new Point(center.getX() - diameter / 2, center.getY() + diameter);
        hexagonsPoint[4] = new Point(center.getX() + diameter, center.getY());
        hexagonsPoint[5] = new Point(center.getX() - diameter, center.getY());

        for (Point point : hexagonsPoint) {
            for (Hexagon hexagon : hexagons) {
                if (isPointInHexagon(point.getX(), point.getY(), hexagon)) {
                    adjacentHexagons.add(hexagon);
                }
            }
        }

        return adjacentHexagons;
    }

    public static int getCountAngles() {
        return COUNT_ANGLES;
    }

    public static void click(MouseEvent event, Drawer drawer) {


        Hexagon hexagonOfClick = null;
        for (Hexagon hexagon : hexagons) {
            if (isPointInHexagon(event.getX(), event.getY(), hexagon)) {
                hexagonOfClick = hexagon;
                break;
            }
        }
        if (hexagonOfClick == null) return;

        if (event.getButton() == MouseButton.SECONDARY) {
            checkBomb(hexagonOfClick, drawer);
            return;
        }

        checkHexagon(hexagonOfClick, drawer);
    }

    private static void checkBomb(Hexagon hexagon, Drawer drawer) {

        drawer.fillHexagonWithNumber(hexagon.center.getX(), hexagon.center.getY(), HexagonType.MARKED);
        if (hexagon.hexagonType == HexagonType.HIDDEN_BOMB) {
            currentCountCheckBomb++;
            if (currentCountCheckBomb == countBomb) {
                Main.gameOver(false);
            }
        } else if (hexagon.hexagonType == HexagonType.MARKED) {
            drawer.fillHexagonWithNumber(hexagon.center.getX(), hexagon.center.getY(), HexagonType.CLOSED);
        }
    }

    private static void checkHexagon(Hexagon hexagon, Drawer drawer) {
        if (hexagon.hexagonType == HexagonType.HIDDEN_BOMB) {
            hexagon.hexagonType = HexagonType.BOMB;
            drawer.fillHexagonWithNumber(hexagon.center.getX(), hexagon.center.getY(), HexagonType.BOMB);
            hexagons.clear();
            Main.gameOver(true);
        } else {
            int numberOfBomb = hexagon.getNumberOfBombNeardy();
            hexagon.hexagonType = HexagonType.OPEN;
            if (numberOfBomb == 0) {
                drawer.fillHexagonWithNumber(hexagon.center.getX(), hexagon.center.getY(), HexagonType.OPEN);
                hexagon.openCellsAround(drawer);
            } else {
                drawer.fillHexagonWithNumber(hexagon.center.getX(), hexagon.center.getY(), numberOfBomb);
            }
        }
    }

    private void openCellsAround(Drawer drawer) {
        List<Hexagon> polygonsAround = getAdjacentHexagon();
        for (Hexagon hexagon : polygonsAround) {
            if (hexagon.getNumberOfBombNeardy() == 0) {
                hexagon.hexagonType = HexagonType.OPEN;
                drawer.fillHexagonWithNumber(hexagon.center.getX(), hexagon.center.getY(), HexagonType.OPEN);
            }
        }
    }

    private static boolean isPointInHexagon(double x, double y, Hexagon hexagon) {

        int penultimateAngle = getCountAngles() - 1;
        double[] allXOfHexagon = hexagon.getAllX();
        double[] allYOfHexagon = hexagon.getAllY();
        boolean result = false;
        for (int i = 0; i < getCountAngles(); i++) {
            if (((allYOfHexagon[i] <= y && y < allYOfHexagon[penultimateAngle]) ||
                    (allYOfHexagon[penultimateAngle] <= y && y < allYOfHexagon[i])) &&
                    (x > (allXOfHexagon[penultimateAngle] - allXOfHexagon[i]) * (y - allYOfHexagon[i]) /
                            (allYOfHexagon[penultimateAngle] - allYOfHexagon[i]) + allXOfHexagon[i])) {
                result = !result;
            }
            penultimateAngle = i;
        }
        return result;
    }

    public void setHexagonType(HexagonType hexagonType) {
        this.hexagonType = hexagonType;
    }

    public static List<Hexagon> getHexagons() {
        return hexagons;
    }

    @Override
    public String toString() {
        return center.toString();
    }
}
