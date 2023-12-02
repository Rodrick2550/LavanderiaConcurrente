package lavanderia;
import lavanderia.models.Lavanderia;
import lavanderia.monitors.ClienteLavadorMonitor;
import lavanderia.monitors.DeliverMonitor;
import lavanderia.threads.LavadorThread;
import lavanderia.threads.ClientCreationThread;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import lavanderia.threads.ClientProcessingThread;

public class GameRunner extends Application {
    Pane mainLayout = new Pane();
    private final BlockingQueue<Lavanderia> lavadorTaskBuffer = new LinkedBlockingQueue<>();
    private final BlockingQueue<ImageView> clientBuffer = new LinkedBlockingQueue<>();


    @Override
    public void start(Stage primaryStage) {
        Pane mainLayout = new Pane();

            Rectangle receptionist = createReceptionist();
        mainLayout.getChildren().add(receptionist);

        List<Lavanderia> lavanderias = createLavanderias();
        for (Lavanderia lavanderia : lavanderias) {
            mainLayout.getChildren().add(lavanderia.getVisualRepresentation());
        }

        List<ImageView> lavadores = createLavadores();
        for (ImageView lavador : lavadores) {
            mainLayout.getChildren().add(lavador);
        }

        List<ImageView> delivers = createDelivers();
        delivers.forEach(deliver -> mainLayout.getChildren().add(deliver));

        DeliverMonitor deliverMonitor = new DeliverMonitor(delivers);

        ClientProcessingThread clientProcessingThread = new ClientProcessingThread(deliverMonitor, lavanderias, clientBuffer, lavadorTaskBuffer);
        Thread clienttProcessingThreadInstance = new Thread(clientProcessingThread);
        clienttProcessingThreadInstance.start();

        ClienteLavadorMonitor monitor = new ClienteLavadorMonitor(lavadores, lavanderias, clientBuffer);

        ClientCreationThread clientCreationThread = new ClientCreationThread(mainLayout, receptionist, monitor, lavanderias, lavadores);
        Thread clienttCreationThreadInstance = new Thread(clientCreationThread);
        clienttCreationThreadInstance.start();

        LavadorThread lavadorThread = new LavadorThread(monitor, lavadorTaskBuffer);
        Thread lavadorTaskThreadInstance = new Thread(lavadorThread);
        lavadorTaskThreadInstance.start();

        Scene scene = new Scene(mainLayout, 600, 400);
        primaryStage.setTitle("Laundry Simulator");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private List<ImageView> createDelivers() {
        List<ImageView> delivers = new ArrayList<>();
        String imagePath = "file:src/main/resources/assets/images/deliver.png";
        int deliverCount = 6;
        int startX = 10; // Posición inicial X ajustada para que quepa en la ventana
        int startY = 10;
        int yIncrement = 80;
        int imageWidth = 20;
        int imageHeight = 20;

        for (int i = 0; i < deliverCount; i++) {
            ImageView deliver = new ImageView(imagePath);
            deliver.setLayoutX(startX);
            deliver.setLayoutY(startY + (i * yIncrement));
            deliver.setFitWidth(imageWidth);
            deliver.setFitHeight(imageHeight);

            // Comprobar si el elemento se sale de la ventana antes de agregarlo
            if (startY + (i * yIncrement) + imageHeight <= 500) {
                delivers.add(deliver);
            }
        }

        return delivers;
    }


    private Rectangle createReceptionist() {
        Rectangle receptionist = new Rectangle(50, 20, Color.RED);
        receptionist.setLayoutX(10);
        receptionist.setLayoutY(200);
        return receptionist;
    }

    private List<Lavanderia> createLavanderias() {
        int lavanderiaCount = 20; // Total de lavanderías
        int itemsPerRow = 5;      // Número de elementos por fila
        int initialX = 100;       // Posición inicial X
        int initialY = 100;       // Posición inicial Y
        int xIncrement = 80;      // Incremento en X entre lavanderías
        int yIncrement = 60;      // Incremento en Y entre filas

        List<Lavanderia> lavanderiaRooms = new ArrayList<>();
        for (int i = 0; i < lavanderiaCount; i++) {
            int xPosition = initialX + (i % itemsPerRow) * xIncrement;
            int yPosition = initialY + (i / itemsPerRow) * yIncrement;

            Lavanderia lavanderia = new Lavanderia(xPosition, yPosition);
            lavanderiaRooms.add(lavanderia);
        }

        return lavanderiaRooms;
    }

    private List<ImageView> createLavadores() {
        List<ImageView> lavadores = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            ImageView lavador = new ImageView("file:src/main/resources/assets/images/lavador.png");
            lavador.setLayoutX(250 + 20 * i);
            lavador.setLayoutY(720);
            lavador.setFitHeight(25);
            lavador.setFitWidth(25);
            lavadores.add(lavador);
            mainLayout.getChildren().add(lavador);
        }
        return lavadores;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
