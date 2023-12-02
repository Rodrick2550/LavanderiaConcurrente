package lavanderia;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
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
        mainLayout.setStyle("-fx-background-color: #4682B4;");
        Label titleLabel = new Label("Lavandería 'El Diego'");
        titleLabel.setFont(new Font("Arial", 24));
        titleLabel.setTextFill(Color.WHITE); // Color del texto en blanco para contraste

        mainLayout.getChildren().add(titleLabel);
        StackPane.setAlignment(titleLabel, Pos.CENTER);


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
        Thread clientProcessingThreadInstance = new Thread(clientProcessingThread);
        clientProcessingThreadInstance.start();

        ClienteLavadorMonitor monitor = new ClienteLavadorMonitor(lavadores, lavanderias, clientBuffer);

        ClientCreationThread clientCreationThread = new ClientCreationThread(mainLayout, receptionist, monitor, lavanderias, lavadores);
        Thread clientCreationThreadInstance = new Thread(clientCreationThread);
        clientCreationThreadInstance.start();

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
        int windowWidth = 600;
        int windowHeight = 400;
        int imageWidth = 20;
        int imageHeight = 20;
        int startY = 10;
        int yIncrement = (windowHeight - startY * 2 - imageHeight) / (deliverCount - 1);


        int startX = windowWidth - imageWidth - 10;

        for (int i = 0; i < deliverCount; i++) {
            ImageView deliver = new ImageView(imagePath);
            deliver.setLayoutX(startX);
            int yPos = startY + (i * yIncrement);
            deliver.setLayoutY(yPos);
            deliver.setFitWidth(imageWidth);
            deliver.setFitHeight(imageHeight);


            if (yPos + imageHeight <= windowHeight) {
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
        int lavanderiaCount = 20;
        int itemsPerRow = 5;
        int initialX = 100;
        int initialY = 100;
        int xIncrement = 80;
        int yIncrement = 60;

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
        String imagePath = "file:src/main/resources/assets/images/lavador.png";
        int lavadorCount = 2;
        int windowWidth = 600;
        int windowHeight = 400;
        int imageWidth = 25;
        int imageHeight = 25;
        int startX = windowWidth / 2 - (lavadorCount * imageWidth) / 2;
        int startY = windowHeight - imageHeight - 20;

        for (int i = 0; i < lavadorCount; i++) {
            ImageView lavador = new ImageView(imagePath);
            lavador.setLayoutX(startX + imageWidth * i);
            lavador.setLayoutY(startY);
            lavador.setFitHeight(imageHeight);
            lavador.setFitWidth(imageWidth);
            lavadores.add(lavador);

            mainLayout.getChildren().add(lavador);
        }

        return lavadores;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
