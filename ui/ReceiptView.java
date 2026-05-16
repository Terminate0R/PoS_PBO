package ui;

import model.IndividualItemInCart;
import model.Receipt;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.*;

public class ReceiptView {

    private final Stage owner;
    private final Receipt receipt;

    public ReceiptView(Stage owner, Receipt receipt) {
        this.owner   = owner;
        this.receipt = receipt;
    }

    public void show() {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(owner);
        dialog.setTitle("Struk Pembayaran");

        VBox root = new VBox();
        root.setStyle("-fx-background-color: " + StyleUtil.BG_PAGE + ";");

        ScrollPane sp = new ScrollPane(buildReceipt(dialog));
        sp.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        sp.setFitToWidth(true);
        VBox.setVgrow(sp, Priority.ALWAYS);
        root.getChildren().add(sp);

        dialog.setScene(new Scene(root, 460, 600));
        dialog.showAndWait();
    }

    private VBox buildReceipt(Stage dialog) {
        VBox box = new VBox(20);
        box.setPadding(new Insets(36, 36, 36, 36));

        
        VBox header = new VBox(8);
        header.setAlignment(Pos.CENTER);

       
        Label check = new Label("✓");
        check.setStyle("-fx-text-fill: " + StyleUtil.ACCENT_TEAL + ";" +
                       "-fx-font-size: 26px; -fx-font-weight: bold;" +
                       "-fx-background-color: " + StyleUtil.ACCENT_TEAL_LIGHT + ";" +
                       "-fx-background-radius: 50;" +
                       "-fx-padding: 12 20;");

        Label successLbl = new Label("Pembayaran Berhasil");
        successLbl.setStyle("-fx-text-fill: " + StyleUtil.TEXT_DARK + ";" +
                            "-fx-font-size: 20px; -fx-font-weight: bold;");

        Label storeLbl = new Label("ApotekPOS");
        storeLbl.setStyle(StyleUtil.label(StyleUtil.FONT_SIZE_MD, StyleUtil.TEXT_MUTED));

        header.getChildren().addAll(check, successLbl, storeLbl);

 
        VBox card = new VBox(14);
        card.setPadding(new Insets(22, 22, 22, 22));
        card.setStyle(StyleUtil.card());

     
        Label txLabel = new Label("ID Transaksi");
        txLabel.setStyle(StyleUtil.label(StyleUtil.FONT_SIZE_XS, StyleUtil.TEXT_MUTED));
        String txId = receipt.getTransactionId();
        if (txId.length() > 20) txId = txId.substring(0, 20) + "…";
        Label txValue = new Label(txId);
        txValue.setStyle("-fx-text-fill: " + StyleUtil.TEXT_MUTED + "; -fx-font-size: 15px; -fx-font-family: monospace;");

        Separator sep1 = sep();

  
        Label itemHeader = new Label("ITEM PEMBELIAN");
        itemHeader.setStyle("-fx-text-fill: " + StyleUtil.TEXT_MUTED + ";" +
                            "-fx-font-size: 15px; -fx-font-weight: bold;");

        VBox items = new VBox(10);
        for (IndividualItemInCart item : receipt.getListOfItems()) {
            HBox row = new HBox();
            row.setAlignment(Pos.CENTER_LEFT);

            VBox left = new VBox(2);
            Label nm = new Label(item.getMedicine().getNameOfMedicine());
            nm.setStyle("-fx-text-fill: " + StyleUtil.TEXT_DARK + "; -fx-font-size: 15px;");
            Label qty = new Label(item.getQuantityOfMedicineBought() + " unit  ×  " +
                                  StyleUtil.formatRupiah(item.getMedicine().getPriceOfIndividualMedicine()));
            qty.setStyle(StyleUtil.label(StyleUtil.FONT_SIZE_XS, StyleUtil.TEXT_MUTED));
            left.getChildren().addAll(nm, qty);

            Region spacer = new Region(); HBox.setHgrow(spacer, Priority.ALWAYS);

            Label sub = new Label(StyleUtil.formatRupiah(item.getTotalPriceOfCurrentMedicine()));
            sub.setStyle("-fx-text-fill: " + StyleUtil.TEXT_DARK + ";" +
                         "-fx-font-size: 15px; -fx-font-weight: bold;");
            row.getChildren().addAll(left, spacer, sub);
            items.getChildren().add(row);
        }

        Separator sep2 = sep();

     
        VBox summary = new VBox(8);
        summary.getChildren().addAll(
            payRow("Total Harga",    StyleUtil.formatRupiah(receipt.getTotalPrice()),    StyleUtil.TEXT_DARK, false),
            payRow("Metode Bayar",   receipt.getPaymentMethod(),                          StyleUtil.TEXT_DARK, false),
            payRow("Jumlah Dibayar", StyleUtil.formatRupiah(receipt.getPaidAmount()),     StyleUtil.ACCENT_BLUE, false),
            payRow("Kembalian",      StyleUtil.formatRupiah(receipt.getChange()),          StyleUtil.ACCENT_TEAL, true)
        );

        card.getChildren().addAll(txLabel, txValue, sep1, itemHeader, items, sep2, summary);

       
        HBox actions = new HBox(12);

        Button btnDash = new Button("Kembali ke Dashboard");
        btnDash.setStyle(StyleUtil.btnSecondary() + "-fx-padding: 11 18;");
        btnDash.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(btnDash, Priority.ALWAYS);
        btnDash.setOnAction(e -> {
            dialog.close();
            new DashboardView(owner).show();
        });

        Button btnNew = new Button("Transaksi Baru");
        btnNew.setStyle(StyleUtil.btnPrimary() + "-fx-padding: 11 18;");
        btnNew.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(btnNew, Priority.ALWAYS);
        btnNew.setOnAction(e -> {
            dialog.close();
            new TransactionView(owner).show();
        });

        actions.getChildren().addAll(btnDash, btnNew);

        box.getChildren().addAll(header, card, actions);
        return box;
    }

    private HBox payRow(String label, String value, String valueColor, boolean large) {
        HBox row = new HBox();
        row.setAlignment(Pos.CENTER_LEFT);
        Label lbl = new Label(label);
        lbl.setStyle(StyleUtil.label(large ? StyleUtil.FONT_SIZE_MD : StyleUtil.FONT_SIZE_SM, StyleUtil.TEXT_MUTED));
        Region spacer = new Region(); HBox.setHgrow(spacer, Priority.ALWAYS);
        Label val = new Label(value);
        val.setStyle("-fx-text-fill: " + valueColor + ";" +
                     "-fx-font-size: " + (large ? "18px" : "13px") + ";" +
                     "-fx-font-weight: " + (large ? "bold" : "normal") + ";");
        row.getChildren().addAll(lbl, spacer, val);
        return row;
    }

    private Separator sep() {
        Separator s = new Separator();
        s.setStyle("-fx-background-color: " + StyleUtil.BORDER + ";");
        return s;
    }

  
    private static final String FONT_SIZE_XS = StyleUtil.FONT_SIZE_XS;
    private static final String FONT_SIZE_MD = StyleUtil.FONT_SIZE_MD;
    private static final String FONT_SIZE_SM = StyleUtil.FONT_SIZE_SM;
}
