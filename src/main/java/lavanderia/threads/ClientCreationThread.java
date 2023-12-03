package lavanderia.threads;

import lavanderia.models.Lavanderia;
import lavanderia.monitors.ClienteLavadorMonitor;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.util.List;

public class ClientCreationThread implements Runnable {
    private final Pane mainLayout;
    private final Rectangle receptionist;
    private final ClienteLavadorMonitor monitor;
    private final List<Lavanderia> lavanderias;
    private final List<ImageView> lavadores;

    public ClientCreationThread(Pane mainLayout, Rectangle receptionist, ClienteLavadorMonitor monitor, List<Lavanderia> lavanderias, List<ImageView> lavadores) {
        this.mainLayout = mainLayout;
        this.receptionist = receptionist;
        this.monitor = monitor;
        this.lavanderias = lavanderias;
        this.lavadores = lavadores;
    }

    @Override
    public void run() {
        for (int i = 0; i < 50; i++) {
            try {
                Thread.sleep(2000);
                Platform.runLater(this::createAndAnimateClient);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private void createAndAnimateClient() {
        ImageView client = new ImageView("file:src/main/resources/assets/images/client.png");
        client.setLayoutX(10);
        client.setLayoutY(10);
        client.setFitHeight(30);
        client.setFitWidth(30);
        mainLayout.getChildren().add(client);

        animateClientToReception(client);
    }

    private void animateClientToReception(ImageView client) {
        TranslateTransition toLavanderia = new TranslateTransition(Duration.seconds(3), client);
        toLavanderia.setToX(receptionist.getLayoutX() - client.getLayoutX());
        toLavanderia.setToY(receptionist.getLayoutY() - client.getLayoutY());

        toLavanderia.setOnFinished(event -> monitor.assignLavadorToClient(client, lavanderias));
        toLavanderia.play();
    }
}
