module com.three.avs_2 {
    requires javafx.controls;
    requires javafx.fxml;
    exports com.three.ars_2.gui;
    opens com.three.ars_2.gui to javafx.fxml;
}