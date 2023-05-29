/**
 * This module contains all the classes and resources for the project.
 */
module project {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.jetbrains.annotations;
    requires lombok;

    opens project.controllers to javafx.fxml;

    opens project to javafx.fxml;

    exports project;
    exports project.classes;
    exports project.abstractClasses;
    exports project.network;
    exports project.controllers;
    exports project.enums;
}