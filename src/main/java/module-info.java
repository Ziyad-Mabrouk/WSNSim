module com.wsnsim.wsnsim {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.wsnsim.wsnsim to javafx.fxml;
    exports com.wsnsim.wsnsim;
}