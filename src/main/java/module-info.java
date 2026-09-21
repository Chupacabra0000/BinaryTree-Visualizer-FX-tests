module org.example.kitpo_l1 {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.example.kitpo_l1 to javafx.fxml;
    exports org.example.kitpo_l1;
}