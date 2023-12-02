package lavanderia.threads;

import lavanderia.models.Lavanderia;
import lavanderia.monitors.ClienteLavadorMonitor;

import java.util.concurrent.BlockingQueue;

public class LavadorThread implements Runnable {
    private final ClienteLavadorMonitor monitor;
    private final BlockingQueue<Lavanderia> lavadorTaskBuffer;

    public LavadorThread(ClienteLavadorMonitor monitor, BlockingQueue<Lavanderia> lavadorTaskBuffer) {
        this.monitor = monitor;
        this.lavadorTaskBuffer = lavadorTaskBuffer;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Lavanderia lavanderia = lavadorTaskBuffer.take();
                monitor.assignLavadorToTask(lavanderia);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
