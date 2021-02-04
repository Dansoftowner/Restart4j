package com.restart4j;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ComplexDemo extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setScene(new Scene(buildRoot(), 500, 500));
        primaryStage.setTitle("Restartable app");
        primaryStage.show();
    }

    private Parent buildRoot() {
        Button btn = new Button("Restart the app");
        btn.setOnAction(e -> {
            ApplicationRestart.builder()
                    .beforeNewProcessCreated(() -> System.out.println("Pre - new process created"))
                    .beforeCurrentProcessTerminated(() -> System.out.println("Pre - current process terminated"))
                    .terminationPolicy(Platform::exit)
                    .modifyCmd(cmd -> cmd + " -restarted")
                    .build().restartApp();
        });

        long restartCount = getParameters().getRaw().stream()
                .filter(it -> it.contains("-restarted"))
                .count();

        return new StackPane(new Group(
                new VBox(5.0,
                        new Label(String.format("The app is restarted %d times", restartCount)),
                        new StackPane(btn)
                )
        ));
    }

    public static void main(String[] args) {
        launch(args);
    }
}
