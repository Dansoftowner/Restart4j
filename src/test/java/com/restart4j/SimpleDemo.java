package com.restart4j;

import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class SimpleDemo extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setScene(new Scene(buildRoot(), 500, 500));
        primaryStage.setTitle("Simple restartable app");
        primaryStage.centerOnScreen();
        primaryStage.show();
    }

    private Parent buildRoot() {
        Button btn = new Button("Restart the app");
        btn.setOnAction(e -> {
            ApplicationRestart.builder().build().restartApp();
        });
        return new StackPane(btn);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
