package lavanderia.threads;

import lavanderia.models.Lavanderia;
import lavanderia.monitors.DeliverMonitor;
import javafx.scene.image.ImageView;

import java.util.List;
import java.util.concurrent.BlockingQueue;

public class ClientProcessingThread implements Runnable {
    private final DeliverMonitor deliverMonitor;
    private final List<Lavanderia> lavanderias;
    private final BlockingQueue<ImageView> clientBuffer;
    private final BlockingQueue<Lavanderia> lavadorTaskBuffer;

    public ClientProcessingThread(DeliverMonitor deliverMonitor, List<Lavanderia> lavanderias, BlockingQueue<ImageView> clientBuffer, BlockingQueue<Lavanderia> lavadorTaskBuffer) {
        this.deliverMonitor = deliverMonitor;
        this.lavanderias = lavanderias;
        this.clientBuffer = clientBuffer;
        this.lavadorTaskBuffer = lavadorTaskBuffer;
    }

    @Override
    public void run() {
        while (true) {
            try {
                ImageView client = clientBuffer.take();
                Lavanderia assignedLavanderia = findLavanderiaWithClient(lavanderias, client);
                if (assignedLavanderia != null) {
                    deliverMonitor.assignDeliverToLavanderia(assignedLavanderia, () -> {
                        try {
                            lavadorTaskBuffer.put(assignedLavanderia);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                    });
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private Lavanderia findLavanderiaWithClient(List<Lavanderia> lavanderias, ImageView client) {
        for (Lavanderia lavanderia : lavanderias) {
            if (lavanderia.getAssignedClient() == client) {
                return lavanderia;
            }
        }
        return null;
    }
}
