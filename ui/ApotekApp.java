package ui;

import service.DataBaseHelper;
import model.Medicine;
import service.InventoryManager;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.*;
import javafx.stage.Stage;
import java.util.ArrayList;
import java.util.Optional;

public class ApotekApp extends Application {

    public static ArrayList<Medicine> medicines = new ArrayList<>();
    public static InventoryManager inventoryManager;

    @Override
    public void start(Stage primaryStage) {
        showLoginScreen(primaryStage);
    }

    private void showLoginScreen(Stage stage) {
        // ── Root ─────────────────────────────────────────────────────────────
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + StyleUtil.BG_PAGE + ";");

        // ── Left branding panel ───────────────────────────────────────────────
        VBox brand = new VBox(16);
        brand.setPrefWidth(380);
        brand.setAlignment(Pos.CENTER);
        brand.setPadding(new Insets(60));
        brand.setStyle("-fx-background-color: " + StyleUtil.BG_SIDEBAR + ";");

        Label cross = new Label("+");
        cross.setStyle("-fx-text-fill: " + StyleUtil.ACCENT_TEAL + ";" +
                       "-fx-font-size: 64px; -fx-font-weight: bold;");

        Label appName = new Label("ApotekPOS");
        appName.setStyle("-fx-text-fill: #FFFFFF; -fx-font-size: 32px; -fx-font-weight: bold;");

        Label tagline = new Label("Sistem Kasir Apotek");
        tagline.setStyle("-fx-text-fill: " + StyleUtil.TEXT_SIDEBAR + "; -fx-font-size: 14px;");

        Separator sep = new Separator();
        sep.setStyle("-fx-background-color: rgba(255,255,255,0.15);");
        sep.setMaxWidth(120);

        Label version = new Label("v1.0.0");
        version.setStyle("-fx-text-fill: rgba(184,197,208,0.5); -fx-font-size: 11px;");

        brand.getChildren().addAll(cross, appName, tagline, sep, version);
        root.setLeft(brand);

        // ── Right login form ──────────────────────────────────────────────────
        VBox formArea = new VBox();
        formArea.setAlignment(Pos.CENTER);
        formArea.setStyle("-fx-background-color: " + StyleUtil.BG_PAGE + ";");

        VBox form = new VBox(20);
        form.setPrefWidth(340);
        form.setPadding(new Insets(40));
        form.setStyle(StyleUtil.card());

        Label formTitle = new Label("Masuk ke Sistem");
        formTitle.setStyle("-fx-text-fill: " + StyleUtil.TEXT_DARK + ";" +
                           "-fx-font-size: 20px; -fx-font-weight: bold;");

        Label formSub = new Label("Masukkan kredensial untuk melanjutkan");
        formSub.setStyle(StyleUtil.label(StyleUtil.FONT_SIZE_SM, StyleUtil.TEXT_MUTED));

        // DB Password
        VBox dbGroup = fieldGroup("Password Database MySQL");
        PasswordField dbPass = new PasswordField();
        dbPass.setPromptText("Password MySQL...");
        dbPass.setStyle(StyleUtil.inputField());
        dbPass.setMaxWidth(Double.MAX_VALUE);
        dbGroup.getChildren().add(dbPass);

        // Username
        VBox userGroup = fieldGroup("Username");
        TextField userField = new TextField();
        userField.setPromptText("Username...");
        userField.setStyle(StyleUtil.inputField());
        userField.setMaxWidth(Double.MAX_VALUE);
        userGroup.getChildren().add(userField);

        // Password
        VBox passGroup = fieldGroup("Password");
        PasswordField passField = new PasswordField();
        passField.setPromptText("Password...");
        passField.setStyle(StyleUtil.inputField());
        passField.setMaxWidth(Double.MAX_VALUE);
        passGroup.getChildren().add(passField);

        Label errorLbl = new Label("");
        errorLbl.setStyle("-fx-text-fill: " + StyleUtil.ACCENT_RED + "; -fx-font-size: 12px;");
        errorLbl.setVisible(false);

        Button loginBtn = new Button("Masuk");
        loginBtn.setStyle(StyleUtil.btnPrimary() + "-fx-font-size: 14px; -fx-padding: 12 0;");
        loginBtn.setMaxWidth(Double.MAX_VALUE);

        loginBtn.setOnAction(e -> {
            String dbPassword = dbPass.getText();
            String username   = userField.getText().trim();
            String password   = passField.getText();

            if (dbPassword.isEmpty() || username.isEmpty() || password.isEmpty()) {
                errorLbl.setText("Semua kolom wajib diisi.");
                errorLbl.setVisible(true);
                return;
            }

            DataBaseHelper.setPassword(dbPassword);
            try {
                DataBaseHelper.getConnection();
            } catch (Exception ex) {
                errorLbl.setText("Gagal terhubung ke database: " + ex.getMessage());
                errorLbl.setVisible(true);
                return;
            }

            if (!DataBaseHelper.login(username, password)) {
                errorLbl.setText("Username atau password salah.");
                errorLbl.setVisible(true);
                passField.clear();
                return;
            }

            medicines = DataBaseHelper.loadMedicines();
            inventoryManager = new InventoryManager(medicines);
            new DashboardView(stage).show();
        });

        // Allow Enter key to submit
        passField.setOnAction(e -> loginBtn.fire());
        userField.setOnAction(e -> passField.requestFocus());
        dbPass.setOnAction(e -> userField.requestFocus());

        form.getChildren().addAll(formTitle, formSub, dbGroup, userGroup, passGroup, errorLbl, loginBtn);
        formArea.getChildren().add(form);

        root.setCenter(formArea);

        Scene scene = new Scene(root, 860, 560);
        stage.setTitle("ApotekPOS — Login");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    private VBox fieldGroup(String labelText) {
        VBox group = new VBox(6);
        Label lbl = new Label(labelText);
        lbl.setStyle("-fx-text-fill: " + StyleUtil.TEXT_DARK + "; -fx-font-size: 12px; -fx-font-weight: bold;");
        group.getChildren().add(lbl);
        return group;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
