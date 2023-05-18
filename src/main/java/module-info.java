module project.tripaloski6quiprend {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.jetbrains.annotations;

    opens project.controllers to javafx.fxml;
    opens project to javafx.fxml;

    exports project;
    exports project.controllers;
}