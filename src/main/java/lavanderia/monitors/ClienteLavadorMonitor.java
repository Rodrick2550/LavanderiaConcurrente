package lavanderia.monitors;

import lavanderia.models.Lavanderia;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.BlockingQueue;

public class ClienteLavadorMonitor {
    private Queue<ImageView> availableLavadores;
    private List<Lavanderia> lavanderias;
    private BlockingQueue<ImageView> clientBuffer;
    private Random random = new Random();

    public ClienteLavadorMonitor(List<ImageView> lavadores, List<Lavanderia> lavanderias, BlockingQueue<ImageView> clientBuffer) {
        this.availableLavadores = new LinkedList<>(lavadores);
        this.lavanderias = lavanderias;
        this.clientBuffer = clientBuffer;
    }

    public synchronized void assignLavadorToClient(ImageView client, List<Lavanderia> lavanderias) {
        int index = random.nextInt(this.lavanderias.size());
        Lavanderia lavanderia = this.lavanderias.get(index);

        if (!lavanderia.isOccupied() && !availableLavadores.isEmpty()) {
            ImageView lavador = availableLavadores.remove();
            animateClientToLavanderia(client, lavanderia);
            animateLavadorToLavanderia(lavador, lavanderia, () -> {
                availableLavadores.add(lavador);
                animateLavadorBack(lavador);
            });
            lavanderia.setOccupied(true);
            lavanderia.setAssignedLavador(lavador);
            lavanderia.setAssignedClient(client);
        }
    }

    public synchronized void assignLavadorToTask(Lavanderia lavanderia) {
        if (!availableLavadores.isEmpty()) {
            ImageView lavador = availableLavadores.remove();


            Platform.runLater(() -> {

                animateLavadorToLavanderia(lavador, lavanderia, () -> {

                    animateClientOut(lavanderia.getAssignedClient());


                    availableLavadores.add(lavador);


                    animateLavadorBack(lavador);


                    markLavanderiaAsAvailable(lavanderia);
                });
            });
        }
    }

    private void markLavanderiaAsAvailable(Lavanderia lavanderia) {
        lavanderia.setOccupied(false);
        lavanderia.setAssignedLavador(null);
        lavanderia.setAssignedClient(null);
    }

    private void animateClientOut(ImageView client) {
        if (client != null) {
            TranslateTransition exitTransition = new TranslateTransition(Duration.seconds(3), client);
            exitTransition.setToY(-50);
            exitTransition.setOnFinished(event -> client.setVisible(false));
            exitTransition.play();
        }
    }

    private void animateClientToLavanderia(ImageView client, Lavanderia lavanderia) {
        TranslateTransition toLavanderia = new TranslateTransition(Duration.seconds(3), client);
        toLavanderia.setToX(lavanderia.getVisualRepresentation().getLayoutX() - client.getLayoutX());
        toLavanderia.setToY(lavanderia.getVisualRepresentation().getLayoutY() - client.getLayoutY());
        toLavanderia.play();
    }

    private void animateLavadorToLavanderia(ImageView lavador, Lavanderia lavanderia, Runnable onFinished) {
        TranslateTransition toLavanderia = new TranslateTransition(Duration.seconds(3), lavador);
        toLavanderia.setDelay(Duration.seconds(2)); // 2 segundos antes de moverse
        toLavanderia.setToX(lavanderia.getVisualRepresentation().getLayoutX() - lavador.getLayoutX());
        toLavanderia.setToY(lavanderia.getVisualRepresentation().getLayoutY() - lavador.getLayoutY());

        toLavanderia.setOnFinished(event -> {
            new Thread(() -> {
                try {
                    Thread.sleep(2000);
                    Platform.runLater(() -> {
                        onFinished.run();
                        animateLavadorBack(lavador);
                        sendClientToBuffer(lavanderia);
                    });
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }).start();
        });
        toLavanderia.play();
    }

    private void animateLavadorBack(ImageView lavador) {
        TranslateTransition backToPlace = new TranslateTransition(Duration.seconds(3), lavador);
        backToPlace.setToX(0);
        backToPlace.setToY(0);
        backToPlace.setOnFinished(event -> {
        });
        backToPlace.play();
    }

    private void sendClientToBuffer(Lavanderia lavanderia) {
        try {
            clientBuffer.put(lavanderia.getAssignedClient());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}