package com.chichifood.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AddCommentController {

    @FXML
    private Button homeButton, backButton, uploadImagesButton, submitButton;

    @FXML
    private TextArea commentField;

    @FXML
    private TextField scoreField;

    @FXML
    private Label selectedImagesLabel;

    private final List<File> selectedImages = new ArrayList<>();

    @FXML
    public void initialize() {
        homeButton.setOnAction(e -> {
            Stage currentStage = (Stage) homeButton.getScene().getWindow();
            currentStage.close();

            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/BuyerPanel.fxml"));
                Parent root = loader.load();

                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.setTitle("خانه");
                stage.show();

            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
        backButton.setOnAction(e -> {
            Stage currentStage = (Stage) backButton.getScene().getWindow();
            currentStage.close();

            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/OrderBuyer.fxml"));
                Parent root = loader.load();

                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.setTitle("خانه");
                stage.show();

            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
        uploadImagesButton.setOnAction(e -> handleImageUpload());
        submitButton.setOnAction(e -> handleSubmit());
    }

    private void handleImageUpload() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("انتخاب عکس‌ها");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("تصاویر", "*.png", "*.jpg", "*.jpeg")
        );
        List<File> files = fileChooser.showOpenMultipleDialog(getWindow());

        if (files != null) {
            selectedImages.clear();
            selectedImages.addAll(files);

            StringBuilder names = new StringBuilder("عکس‌های انتخاب شده: ");
            for (File file : selectedImages) {
                names.append(file.getName()).append(", ");
            }
            selectedImagesLabel.setText(names.toString());
        }
    }

    private void handleSubmit() {
        String comment = commentField.getText().trim();
        String scoreText = scoreField.getText().trim();

        int score;
        try {
            score = Integer.parseInt(scoreText);
            if (score < 0 || score > 5) {
                throw new NumberFormatException("خارج از بازه");
            }
        } catch (NumberFormatException e) {
            showAlert("خطا", "امتیاز باید عددی بین ۰ تا ۵ باشد.");
            return;
        }

        submitRating(comment, score, selectedImages);
    }

    private void submitRating(String comment, int score, List<File> images) {
        System.out.println("نظر: " + comment);
        System.out.println("امتیاز: " + score);
        System.out.println("تعداد عکس: " + images.size());
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private Window getWindow() {
        return submitButton.getScene().getWindow();
    }
}