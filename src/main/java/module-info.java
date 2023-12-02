module lavanderia {
    requires javafx.controls;
    requires javafx.fxml;


    opens lavanderia to javafx.fxml;
    exports lavanderia;
    exports lavanderia.controllers;
    opens lavanderia.controllers to javafx.fxml;
}