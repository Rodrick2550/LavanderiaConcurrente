package lavanderia.models;

import javafx.scene.image.ImageView;

public class Lavanderia {
    private ImageView assignedClient;
    private ImageView visualRepresentation;
    private boolean isOccupied;
    private ImageView assignedLavador;

    public Lavanderia(int x, int y) {
        visualRepresentation = new ImageView("file:src/main/resources/assets/images/lavadora.png");
        visualRepresentation.setLayoutX(x);
        visualRepresentation.setLayoutY(y);
        visualRepresentation.setFitHeight(30);
        visualRepresentation.setFitWidth(30);
        isOccupied = false;
        assignedLavador = null;
    }

    public ImageView getVisualRepresentation() {
        return visualRepresentation;
    }

    public boolean isOccupied() {
        return isOccupied;
    }

    public void setOccupied(boolean occupied) {
        isOccupied = occupied;
    }

    public void setAssignedLavador(ImageView assignedLavador) {
        this.assignedLavador = assignedLavador;
    }

    public ImageView getAssignedClient() {
        return assignedClient;
    }

    public void setAssignedClient(ImageView assignedClient) {
        this.assignedClient = assignedClient;
    }
}