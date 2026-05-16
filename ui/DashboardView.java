package ui;

import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class DashboardView {

    private final Stage stage;

    public DashboardView(Stage stage) {
        this.stage = stage;
    }

    public void show() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + StyleUtil.BG_PAGE + ";");
        root.setLeft(Sidebar.build(stage, "dashboard"));
        root.setCenter(buildContent());

        stage.setTitle("ApotekPOS — Dashboard");
        Scene existing = stage.getScene();
        if (existing != null) {
            existing.setRoot(root);
        } else {
            stage.setScene(new Scene(root, 1200, 720));
            stage.setMaximized(true);
            stage.show();
        }
    }

    private VBox buildContent() {
        VBox content = new VBox(28);
        content.setPadding(new Insets(36, 40, 36, 40));

        
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);

        VBox titleBox = new VBox(4);
        Label title = new Label("Dashboard");
        title.setStyle("-fx-text-fill: " + StyleUtil.TEXT_DARK + ";" +
                       "-fx-font-size: 26px; -fx-font-weight: bold;");
        Label sub = new Label("Selamat datang kembali");
        sub.setStyle(StyleUtil.label(StyleUtil.FONT_SIZE_MD, StyleUtil.TEXT_MUTED));
        titleBox.getChildren().addAll(title, sub);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button newTxBtn = new Button("+ Transaksi Baru");
        newTxBtn.setStyle(StyleUtil.btnPrimary());
        newTxBtn.setOnAction(e -> new TransactionView(stage).show());

        header.getChildren().addAll(titleBox, spacer, newTxBtn);

        
        HBox statsRow = buildStatsRow();

      
        HBox lower = buildLowerSection();
        VBox.setVgrow(lower, Priority.ALWAYS);

        content.getChildren().addAll(header, statsRow, lower);
        return content;
    }

    private HBox buildStatsRow() {
        int totalMeds  = ApotekApp.medicines.size();
        int totalStock = ApotekApp.medicines.stream().mapToInt(m -> m.getStockOfMedicine()).sum();
        int lowStock   = (int) ApotekApp.medicines.stream().filter(m -> m.getStockOfMedicine() < 20).count();
        int totalValue = ApotekApp.medicines.stream()
                .mapToInt(m -> m.getStockOfMedicine() * m.getPriceOfIndividualMedicine()).sum();

        HBox row = new HBox(16);
        row.getChildren().addAll(
            statCard("Jenis Obat",      String.valueOf(totalMeds),
                     "produk tersedia", StyleUtil.ACCENT_TEAL,       StyleUtil.ACCENT_TEAL_LIGHT),
            statCard("Total Stok",      String.valueOf(totalStock),
                     "unit tersedia",   StyleUtil.ACCENT_BLUE,       StyleUtil.ACCENT_BLUE_LIGHT),
            statCard("Stok Menipis",    String.valueOf(lowStock),
                     "perlu diisi ulang", StyleUtil.ACCENT_AMBER,   StyleUtil.ACCENT_AMBER_LIGHT),
            statCard("Nilai Inventaris", StyleUtil.formatRupiah(totalValue),
                     "estimasi nilai stok", StyleUtil.ACCENT_RED,   StyleUtil.ACCENT_RED_LIGHT)
        );
        for (javafx.scene.Node n : row.getChildren()) {
            HBox.setHgrow(n, Priority.ALWAYS);
        }
        return row;
    }

    private VBox statCard(String label, String value, String sub,
                          String accentColor, String bgColor) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(20, 22, 20, 22));
        card.setStyle("-fx-background-color: " + bgColor + ";" +
                      "-fx-background-radius: 10;" +
                      "-fx-border-color: " + accentColor + ";" +
                      "-fx-border-radius: 10;" +
                      "-fx-border-width: 1;");

     
        Rectangle dot = new Rectangle(6, 6);
        dot.setArcWidth(6);
        dot.setArcHeight(6);
        dot.setStyle("-fx-fill: " + accentColor + ";");

        Label valueLbl = new Label(value);
        valueLbl.setStyle("-fx-text-fill: " + accentColor + ";" +
                          "-fx-font-size: 32px; -fx-font-weight: bold;");

        Label nameLbl = new Label(label);
        nameLbl.setStyle("-fx-text-fill: " + StyleUtil.TEXT_DARK + ";" +
                         "-fx-font-size: 20px; -fx-font-weight: bold;");

        Label subLbl = new Label(sub);
        subLbl.setStyle(StyleUtil.label(StyleUtil.FONT_SIZE_XS, StyleUtil.TEXT_MUTED));

        card.getChildren().addAll(dot, valueLbl, nameLbl, subLbl);
        return card;
    }

    private HBox buildLowerSection() {
        HBox row = new HBox(20);

     
        VBox actCard = new VBox(14);
        actCard.setPrefWidth(260);
        actCard.setPadding(new Insets(22, 24, 22, 24));
        actCard.setStyle(StyleUtil.card());

        Label actTitle = new Label("Akses Cepat");
        actTitle.setStyle("-fx-text-fill: " + StyleUtil.TEXT_DARK + ";" +
                          "-fx-font-size: 20px; -fx-font-weight: bold;");

        Button btnTx  = new Button("Mulai Transaksi Baru");
        Button btnInv = new Button("Kelola Inventaris");
        btnTx.setStyle(StyleUtil.btnPrimary() + "-fx-font-size: 20px; -fx-padding: 11 18;");
        btnInv.setStyle(StyleUtil.btnSecondary() + "-fx-font-size: 20px; -fx-padding: 11 18;");
        btnTx.setMaxWidth(Double.MAX_VALUE);
        btnInv.setMaxWidth(Double.MAX_VALUE);
        btnTx.setOnAction(e  -> new TransactionView(stage).show());
        btnInv.setOnAction(e -> new InventoryView(stage).show());


        long lowCount = ApotekApp.medicines.stream().filter(m -> m.getStockOfMedicine() < 20).count();
        if (lowCount > 0) {
            VBox warnBox = new VBox(4);
            warnBox.setPadding(new Insets(10, 12, 10, 12));
            warnBox.setStyle("-fx-background-color: " + StyleUtil.ACCENT_AMBER_LIGHT + ";" +
                             "-fx-background-radius: 7;" +
                             "-fx-border-color: " + StyleUtil.ACCENT_AMBER + ";" +
                             "-fx-border-radius: 7; -fx-border-width: 1;");
            Label warnTitle = new Label("Peringatan Stok");
            warnTitle.setStyle("-fx-text-fill: " + StyleUtil.ACCENT_AMBER + "; -fx-font-size: 32px; -fx-font-weight: bold;");
            Label warnBody  = new Label(lowCount + " obat memiliki stok < 20 unit");
            warnBody.setStyle(StyleUtil.label(StyleUtil.FONT_SIZE_XS, StyleUtil.TEXT_MUTED));
            warnBox.getChildren().addAll(warnTitle, warnBody);
            actCard.getChildren().addAll(actTitle, btnTx, btnInv, warnBox);
        } else {
            actCard.getChildren().addAll(actTitle, btnTx, btnInv);
        }

        
        VBox tableCard = new VBox(16);
        tableCard.setPadding(new Insets(22, 24, 22, 24));
        tableCard.setStyle(StyleUtil.card());
        HBox.setHgrow(tableCard, Priority.ALWAYS);

        HBox tableHeader = new HBox(12);
        tableHeader.setAlignment(Pos.CENTER_LEFT);
        Label tableTitle = new Label("Daftar Obat");
        tableTitle.setStyle("-fx-text-fill: " + StyleUtil.TEXT_DARK + ";" +
                            "-fx-font-size: 20px; -fx-font-weight: bold;");
        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);
        Label totalLbl = new Label(ApotekApp.medicines.size() + " item");
        totalLbl.setStyle(StyleUtil.label(StyleUtil.FONT_SIZE_SM, StyleUtil.TEXT_MUTED));
        tableHeader.getChildren().addAll(tableTitle, sp, totalLbl);


        HBox thead = tableRow("ID", "Nama Obat", "Stok", "Harga Satuan", true);

        VBox tbody = new VBox(0);
        for (model.Medicine m : ApotekApp.medicines) {
            HBox tr = tableRow(
                String.valueOf(m.getIdOfMedicine()),
                m.getNameOfMedicine(),
                m.getStockOfMedicine() + " unit",
                StyleUtil.formatRupiah(m.getPriceOfIndividualMedicine()),
                false
            );
    
            String stockClr = m.getStockOfMedicine() < 20 ? StyleUtil.ACCENT_AMBER : StyleUtil.ACCENT_TEAL;
            ((Label) tr.getChildren().get(2)).setStyle("-fx-text-fill: " + stockClr + "; -fx-font-size: 20px; -fx-font-weight: bold;");
            tbody.getChildren().add(tr);
        }

        ScrollPane sp2 = new ScrollPane(tbody);
        sp2.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        sp2.setFitToWidth(true);
        sp2.setPrefHeight(220);
        VBox.setVgrow(sp2, Priority.ALWAYS);

        tableCard.getChildren().addAll(tableHeader, thead, sp2);
        row.getChildren().addAll(actCard, tableCard);
        VBox.setVgrow(tableCard, Priority.ALWAYS);
        return row;
    }

    private HBox tableRow(String id, String name, String stock, String price, boolean header) {
        HBox row = new HBox();
        row.setPadding(new Insets(10, 6, 10, 6));
        String style = header
            ? "-fx-text-fill: " + StyleUtil.TEXT_MUTED + "; -fx-font-size: 32px; -fx-font-weight: bold;"
            : "-fx-text-fill: " + StyleUtil.TEXT_DARK + "; -fx-font-size: 20px;";
        if (!header) {
            row.setStyle("-fx-border-color: " + StyleUtil.BORDER + "; -fx-border-width: 0 0 1 0;");
        } else {
            row.setStyle("-fx-border-color: " + StyleUtil.BORDER_DARK + "; -fx-border-width: 0 0 1 0;");
        }
        row.getChildren().addAll(
            cell(id,    style, 44),
            cell(name,  style, 220),
            cell(stock, style, 100),
            cell(price, style, 140)
        );
        return row;
    }

    private Label cell(String text, String style, double w) {
        Label l = new Label(text);
        l.setStyle(style);
        l.setPrefWidth(w);
        return l;
    }
}
