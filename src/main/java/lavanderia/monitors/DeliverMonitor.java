package lavanderia.monitors;

import lavanderia.models.Lavanderia;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

import java.util.LinkedList;
import java.util.List;

public class DeliverMonitor {
    private List<ImageView> availableDelivers;

    public DeliverMonitor(List<ImageView> delivers) {
        this.availableDelivers = new LinkedList<>(delivers);
    }

    public synchronized void assignDeliverToLavanderia(Lavanderia lavanderia, Runnable onFinished) {
        if (!availableDelivers.isEmpty()) {
            ImageView deliver = availableDelivers.remove(0);
            Platform.runLater(() -> {
                animateDeliverToLavanderia(deliver, lavanderia, () -> {
                    availableDelivers.add(deliver);
                    animateDeliverBack(deliver);
                    onFinished.run();
                });
            });
        }
    }

    private void animateDeliverToLavanderia(ImageView deliver, Lavanderia lavanderia, Runnable onFinished) {
        TranslateTransition toLavanderia = new TranslateTransition(Duration.seconds(3), deliver);
        toLavanderia.setToX(lavanderia.getVisualRepresentation().getLayoutX() - deliver.getLayoutX());
        toLavanderia.setToY(lavanderia.getVisualRepresentation().getLayoutY() - deliver.getLayoutY());

        toLavanderia.setOnFinished(event -> {
            new Thread(() -> {
                Platform.runLater(() -> {
                    onFinished.run();
                    animateDeliverBack(deliver);
                });
            }).start();
        });
        toLavanderia.play();
    }

    private void animateDeliverBack(ImageView deliver) {
        TranslateTransition backToPlace = new TranslateTransition(Duration.seconds(3), deliver);
        backToPlace.setToX(0);
        backToPlace.setToY(0);
        backToPlace.play();
    }
}
