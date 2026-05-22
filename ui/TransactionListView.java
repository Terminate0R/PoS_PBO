package ui;

import model.TransactionRecord;
import service.DataBaseHelper;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.util.ArrayList;

/**
 * Transaction history view.
 * Medicines that have been deleted still appear by their name snapshot.
 */
public class TransactionListView {

    private final Stage stage;

    public TransactionListView(Stage stage){
        this.stage = stage;
    }

    public void show(){
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + StyleUtil.BG_PAGE + ";");
        root.setLeft(Sidebar.build(stage, "history"));
        root.setCenter(buildCenter());

        stage.setTitle("ApotekPOS — Riwayat Transaksi");
        Scene existing = stage.getScene();
        if (existing != null){
            existing.setRoot(root);
        } else {
            stage.setScene(new Scene(root, 1200, 720));
            stage.setMaximized(true);
        }
    }

    // ── Center ───────────────────────────────────────────────────────────────
    private VBox buildCenter(){
        VBox center = new VBox(22);
        center.setPadding(new Insets(36, 40, 36, 40));

        // Header
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        VBox titleBox = new VBox(4);
        Label title = new Label("Riwayat Transaksi");
        title.setStyle("-fx-text-fill: " + StyleUtil.TEXT_DARK + "; -fx-font-size: 32px; -fx-font-weight: bold;");
        Label sub = new Label("Semua transaksi tersimpan — obat yang dihapus tetap tercatat");
        sub.setStyle(StyleUtil.label(StyleUtil.FONT_SIZE_SM, StyleUtil.TEXT_MUTED));
        titleBox.getChildren().addAll(title, sub);
        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);

        Button refreshBtn = new Button("↻  Muat Ulang");
        refreshBtn.setStyle(StyleUtil.btnSecondary());
        refreshBtn.setOnAction(e -> new TransactionListView(stage).show());
        header.getChildren().addAll(titleBox, sp, refreshBtn);

        // Search
        TextField search = new TextField();
        search.setPromptText("Cari ID transaksi atau nama obat...");
        search.setStyle(StyleUtil.inputField() + "-fx-font-size: 14px; -fx-padding: 10 14;");
        search.setMaxWidth(400);

        // List
        ArrayList<TransactionRecord> records = DataBaseHelper.loadTransactions();

        ScrollPane scroll = buildList(records, search);
        VBox.setVgrow(scroll, Priority.ALWAYS);

        center.getChildren().addAll(header, search, scroll);
        return center;
    }

    private ScrollPane buildList(ArrayList<TransactionRecord> allRecords, TextField search){
        VBox list = new VBox(12);
        list.setPadding(new Insets(4, 0, 4, 0));

        Runnable refresh = () -> {
            String q = search.getText().toLowerCase();
            list.getChildren().clear();
            boolean any = false;
            for (TransactionRecord r : allRecords){
                if (!q.isEmpty()){
                    boolean matchId   = r.getTransactionId().toLowerCase().contains(q);
                    boolean matchItem = r.getItems().stream()
                        .anyMatch(i -> i.getMedicineName().toLowerCase().contains(q));
                    if (!matchId && !matchItem) continue;
                }
                list.getChildren().add(buildTransactionCard(r));
                any = true;
            }
            if (!any){
                Label empty = new Label(allRecords.isEmpty()
                    ? "Belum ada transaksi."
                    : "Tidak ada transaksi yang cocok.");
                empty.setStyle("-fx-text-fill: " + StyleUtil.TEXT_MUTED + "; -fx-font-size: 14px;");
                list.getChildren().add(empty);
            }
        };

        search.textProperty().addListener((obs, o, n) -> refresh.run());
        refresh.run();

        ScrollPane sp = new ScrollPane(list);
        sp.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        sp.setFitToWidth(true);
        return sp;
    }

    // ── Transaction card ─────────────────────────────────────────────────────
    private VBox buildTransactionCard(TransactionRecord r){
        VBox card = new VBox(0);
        card.setStyle(StyleUtil.card() + "-fx-padding: 0;");

        // ── Card header ──
        HBox hdr = new HBox(10);
        hdr.setPadding(new Insets(14, 16, 12, 16));
        hdr.setAlignment(Pos.CENTER_LEFT);
        hdr.setStyle("-fx-border-color: " + StyleUtil.BORDER + "; -fx-border-width: 0 0 1 0;");

        // Short TX ID
        String shortId = r.getTransactionId().length() > 16
            ? r.getTransactionId().substring(0, 16) + "…"
            : r.getTransactionId();
        Label idLbl = new Label("# " + shortId);
        idLbl.setStyle("-fx-text-fill: " + StyleUtil.TEXT_MUTED + "; -fx-font-size: 12px; -fx-font-family: monospace;");

        Region gap = new Region(); HBox.setHgrow(gap, Priority.ALWAYS);

        // Date
        Label dateLbl = new Label(r.getCreatedAt() != null ? r.getCreatedAt().replace("T", " ") : "");
        dateLbl.setStyle("-fx-text-fill: " + StyleUtil.TEXT_MUTED + "; -fx-font-size: 12px;");

        // Payment method badge
        Label methodBadge = new Label(r.getPaymentMethod());
        methodBadge.setStyle(StyleUtil.badge(StyleUtil.ACCENT_BLUE_LIGHT, StyleUtil.ACCENT_BLUE));

        hdr.getChildren().addAll(idLbl, gap, dateLbl, methodBadge);

        // ── Item rows ──
        VBox items = new VBox(0);
        for (TransactionRecord.ItemSnapshot item : r.getItems()){
            HBox row = new HBox();
            row.setPadding(new Insets(8, 16, 8, 16));
            row.setAlignment(Pos.CENTER_LEFT);
            row.setStyle("-fx-border-color: " + StyleUtil.BORDER + "; -fx-border-width: 0 0 1 0;");

            Label nameLbl = new Label(item.getMedicineName());
            nameLbl.setStyle("-fx-text-fill: " + StyleUtil.TEXT_DARK + "; -fx-font-size: 13px;");

            // Show "(dihapus)" tag if this was a deleted medicine
            // We can't know for sure from just the name, but we can flag "(Obat dihapus)" pattern
            if (item.getMedicineName().equals("(Obat dihapus)")){
                nameLbl.setStyle("-fx-text-fill: " + StyleUtil.ACCENT_RED + "; -fx-font-size: 13px; -fx-font-style: italic;");
            }

            Region rSp = new Region(); HBox.setHgrow(rSp, Priority.ALWAYS);

            Label qtyLbl = new Label(item.getQuantity() + " ×");
            qtyLbl.setStyle("-fx-text-fill: " + StyleUtil.TEXT_MUTED + "; -fx-font-size: 13px;");

            Label subLbl = new Label(StyleUtil.formatRupiah(item.getSubtotal()));
            subLbl.setStyle("-fx-text-fill: " + StyleUtil.TEXT_DARK + "; -fx-font-size: 13px; -fx-font-weight: bold;");
            subLbl.setMinWidth(100);
            subLbl.setAlignment(Pos.CENTER_RIGHT);

            row.getChildren().addAll(nameLbl, rSp, qtyLbl, subLbl);
            items.getChildren().add(row);
        }

        // ── Card footer: totals ──
        HBox footer = new HBox(24);
        footer.setPadding(new Insets(12, 16, 12, 16));
        footer.setAlignment(Pos.CENTER_RIGHT);
        footer.setStyle("-fx-background-color: " + StyleUtil.BG_PAGE + "; -fx-background-radius: 0 0 10 10;");

        Label totalLbl  = summaryLabel("Total: ",  StyleUtil.formatRupiah(r.getTotalPrice()),  StyleUtil.ACCENT_TEAL,  true);
        Label paidLbl   = summaryLabel("Bayar: ",  StyleUtil.formatRupiah(r.getAmountPaid()),  StyleUtil.TEXT_DARK,    false);
        Label changeLbl = summaryLabel("Kembali: ",StyleUtil.formatRupiah(r.getChangeAmount()),StyleUtil.TEXT_MUTED,   false);
        footer.getChildren().addAll(changeLbl, paidLbl, totalLbl);

        card.getChildren().addAll(hdr, items, footer);
        return card;
    }

    private Label summaryLabel(String prefix, String value, String color, boolean bold){
        Label l = new Label(prefix + value);
        l.setStyle("-fx-text-fill: " + color + "; -fx-font-size: " + (bold ? "15px" : "13px") +
                   "; -fx-font-weight: " + (bold ? "bold" : "normal") + ";");
        return l;
    }
}
