package UI;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.stage.Stage;
import logic.Hexagon;
import logic.HexagonType;
import java.util.List;
import java.util.Optional;
import java.util.Random;

public class Main extends Application {

    private static Stage mainStage = null;
    private static Drawer drawer;

    public void start(Stage primaryStage) {
        startNewGame();
    }

    private static void startNewGame() {

        Group root = new Group();
        Scene scene = new Scene(root, 500, 500);
        Canvas canvas = new Canvas(500, 500);

        root.getChildren().addAll(canvas);

        mainStage = new Stage();
        mainStage.setScene(scene);
        mainStage.setResizable(false);
        mainStage.show();

        drawer = new Drawer(canvas.getGraphicsContext2D(), canvas.getHeight(), canvas.getWidth());

        List<Hexagon> polygonList = Hexagon.getHexagons();
        Random random = new Random();

        for (int i = 0; i < 25; i++) {
            int randomIndex = random.nextInt(polygonList.size());
            polygonList.get(randomIndex).setHexagonType(HexagonType.HIDDEN_BOMB);
        }

        canvas.setOnMouseClicked(event -> Hexagon.click(event, drawer));
    }

    public static void gameOver(boolean userLose) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setContentText("U " + (userLose ? "lose." : "win!") + " Again?");

        Optional<ButtonType> button = alert.showAndWait();

        if (button.get() == ButtonType.OK) {
            mainStage.close();
            startNewGame();
        } else {
            System.exit(0);
        }
    }
}
