package UI;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import logic.Hexagon;
import logic.HexagonType;
import logic.Point;

public class Drawer {

    private static GraphicsContext gc;
    private static int POLYGON_RADIUS = 20;
    private static int POLYGON_DIAMETR = POLYGON_RADIUS * 2;

    Drawer(GraphicsContext graphicsContext, double height, double width) {
        gc = graphicsContext;
        fillField((int) height, (int) width);
    }

    private void fillField(int height, int width) {
        for (int x = POLYGON_RADIUS; x <= width - POLYGON_RADIUS; x += POLYGON_DIAMETR) {
            for (int y = POLYGON_RADIUS; y <= height - POLYGON_RADIUS; y += POLYGON_DIAMETR) {
                if ((y / POLYGON_DIAMETR) % 2 == 0) {
                    fillHexagonWithNumber(x + POLYGON_RADIUS, y, HexagonType.CLOSED);
                } else {
                    fillHexagonWithNumber(x, y, HexagonType.CLOSED);
                }
            }
        }
    }

    public void fillHexagonWithNumber(double x, double y, HexagonType hexagonType) {
        gc.setStroke(Color.GRAY);
        gc.setFill(getHexagonColor(hexagonType));

        Hexagon hexagon = new Hexagon(hexagonType, new Point(x, y));

        gc.strokePolygon(hexagon.getAllX(), hexagon.getAllY(), Hexagon.getCountAngles());
        gc.fillPolygon(hexagon.getAllX(), hexagon.getAllY(), Hexagon.getCountAngles());
    }

    private Color getHexagonColor(HexagonType hexagonType) {
        switch (hexagonType) {
            case BOMB:
                return Color.RED;
            case OPEN:
                return Color.WHITESMOKE;
            case MARKED:
                return Color.LIGHTGREEN;
            default:
                return Color.LIGHTGRAY;
        }
    }

    public void fillHexagonWithNumber(double x, double y, int numberOfBomb) {
        fillHexagonWithNumber(x, y, HexagonType.OPEN);
        gc.setFill(Color.BLACK);
        gc.setFont(Font.getDefault());
        gc.fillText(Integer.toString(numberOfBomb), x, y, 20);
    }
}
