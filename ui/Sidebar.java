package ui;

import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class Sidebar {

    public static VBox build(Stage stage, String activeView) {
        VBox sidebar = new VBox(0);
        sidebar.setPrefWidth(210);
        sidebar.setStyle("-fx-background-color: " + StyleUtil.BG_SIDEBAR + ";");

        
        VBox logoBox = new VBox(4);
        logoBox.setPadding(new Insets(28, 20, 24, 20));
        logoBox.setStyle("-fx-border-color: rgba(255,255,255,0.08); -fx-border-width: 0 0 1 0;");

        Label cross = new Label("+  ApotekPOS");
        cross.setStyle("-fx-text-fill: #FFFFFF; -fx-font-size: 18px; -fx-font-weight: bold;");
        Label sub = new Label("Sistem Kasir Apotek");
        sub.setStyle("-fx-text-fill: " + StyleUtil.TEXT_SIDEBAR + "; -fx-font-size: 15px;");
        logoBox.getChildren().addAll(cross, sub);

   
        VBox nav = new VBox(4);
        nav.setPadding(new Insets(16, 10, 16, 10));

        Label navLabel = new Label("MENU");
        navLabel.setStyle("-fx-text-fill: rgba(184,197,208,0.45); -fx-font-size: 15px;" +
                          "-fx-font-weight: bold; -fx-padding: 4 10 8 10;");

        Button btnDash    = navBtn("  Dashboard",          activeView.equals("dashboard"));
        Button btnTx      = navBtn("  Transaksi",          activeView.equals("transaction"));
        Button btnInv     = navBtn("  Inventaris",         activeView.equals("inventory"));
        Button btnHistory = navBtn("  Riwayat Transaksi",  activeView.equals("history"));

        btnDash.setOnAction(e    -> { if (!activeView.equals("dashboard"))   new DashboardView(stage).show(); });
        btnTx.setOnAction(e      -> { if (!activeView.equals("transaction"))  new TransactionView(stage).show(); });
        btnInv.setOnAction(e     -> { if (!activeView.equals("inventory"))   new InventoryView(stage).show(); });
        btnHistory.setOnAction(e -> { if (!activeView.equals("history"))     new TransactionListView(stage).show(); });

        nav.getChildren().addAll(navLabel, btnDash, btnTx, btnInv, btnHistory);

       
        VBox footer = new VBox(4);
        footer.setPadding(new Insets(16, 20, 20, 20));
        footer.setStyle("-fx-border-color: rgba(255,255,255,0.08); -fx-border-width: 1 0 0 0;");
        Label ver = new Label("v1.0.0  •  © 2025 ApotekPOS");
        ver.setStyle("-fx-text-fill: rgba(184,197,208,0.35); -fx-font-size: 15px;");
        footer.getChildren().add(ver);

        VBox.setVgrow(nav, Priority.ALWAYS);
        sidebar.getChildren().addAll(logoBox, nav, footer);
        return sidebar;
    }

    private static Button navBtn(String text, boolean active) {
        Button btn = new Button(text);
        btn.setStyle(active ? StyleUtil.navBtnActive() : StyleUtil.navBtnIdle());
        btn.setMaxWidth(Double.MAX_VALUE);
        if (!active) {
            btn.setOnMouseEntered(e -> btn.setStyle(StyleUtil.navBtnHover()));
            btn.setOnMouseExited(e  -> btn.setStyle(StyleUtil.navBtnIdle()));
        }
        return btn;
    }
}
