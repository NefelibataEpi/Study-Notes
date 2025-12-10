module MovieRecommendation {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;

    opens main.java.controller to javafx.fxml;
    opens main.java.app to javafx.fxml;

    exports main.java.util;
    exports main.java.model;
    exports main.java.model.user;
    exports main.java.model.recommendation;
    exports main.java.model.recommendation.basic;
    exports main.java.model.recommendation.advanced;

    exports main.java.commend;

    exports main.java.app;
}