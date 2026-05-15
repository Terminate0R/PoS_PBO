package ui;

import model.*;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.*;
import java.util.List;

public class TransactionView {

    private final Stage stage;
    private final Cart cart = new Cart();
    private VBox cartContent;
    private Label totalLabel;
    private Label itemCountLabel;

    public TransactionView(Stage stage) {
        this.stage = stage;
    }

    public void show() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + StyleUtil.BG_PAGE + ";");
        root.setLeft(Sidebar.build(stage, "transaction"));
        root.setCenter(buildCenter());
        root.setRight(buildCartPanel());

        stage.setScene(new Scene(root, 1200, 720));
        stage.setTitle("ApotekPOS — Transaksi");
        stage.setMaximized(true);
    }

    
    private VBox buildCenter() {
        VBox center = new VBox(18);
        center.setPadding(new Insets(32, 24, 32, 32));

       
        VBox titleBox = new VBox(4);
        Label title = new Label("Pilih Obat");
        title.setStyle("-fx-text-fill: " + StyleUtil.TEXT_DARK + ";" +
                       "-fx-font-size: 22px; -fx-font-weight: bold;");
        Label sub = new Label("Klik kartu obat untuk menambahkan ke keranjang");
        sub.setStyle(StyleUtil.label(StyleUtil.FONT_SIZE_SM, StyleUtil.TEXT_MUTED));
        titleBox.getChildren().addAll(title, sub);

        
        TextField search = new TextField();
        search.setPromptText("Cari nama obat...");
        search.setStyle(StyleUtil.inputField() + "-fx-font-size: 13px; -fx-padding: 10 14;");
        search.setMaxWidth(Double.MAX_VALUE);

        
        ScrollPane grid = buildMedicineGrid(search);
        VBox.setVgrow(grid, Priority.ALWAYS);

        center.getChildren().addAll(titleBox, search, grid);
        return center;
    }

    private ScrollPane buildMedicineGrid(TextField searchField) {
        FlowPane flow = new FlowPane(14, 14);
        flow.setPadding(new Insets(4));
        flow.setStyle("-fx-background-color: transparent;");

        Runnable refresh = () -> {
            String q = searchField.getText().toLowerCase();
            flow.getChildren().clear();
            for (Medicine m : ApotekApp.medicines) {
                if (q.isEmpty() || m.getNameOfMedicine().toLowerCase().contains(q)) {
                    flow.getChildren().add(buildMedicineCard(m));
                }
            }
        };

        searchField.textProperty().addListener((obs, o, n) -> refresh.run());
        refresh.run();

        ScrollPane sp = new ScrollPane(flow);
        sp.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        sp.setFitToWidth(true);
        VBox.setVgrow(sp, Priority.ALWAYS);
        return sp;
    }

    private VBox buildMedicineCard(Medicine m) {
        boolean inStock = m.getStockOfMedicine() > 0;
        boolean lowStock = m.getStockOfMedicine() > 0 && m.getStockOfMedicine() < 20;

        VBox card = new VBox(10);
        card.setPadding(new Insets(18, 18, 16, 18));
        card.setPrefWidth(195);
        card.setStyle(StyleUtil.card() + (inStock ? "-fx-cursor: hand;" : "-fx-opacity: 0.6;"));

        
        HBox topRow = new HBox(6);
        topRow.setAlignment(Pos.CENTER_LEFT);
        Label idBadge = new Label("ID " + m.getIdOfMedicine());
        idBadge.setStyle(StyleUtil.badge(StyleUtil.BG_INPUT, StyleUtil.TEXT_MUTED));

        Region topSp = new Region();
        HBox.setHgrow(topSp, Priority.ALWAYS);

        String stockBg  = lowStock ? StyleUtil.ACCENT_AMBER_LIGHT : (inStock ? StyleUtil.ACCENT_TEAL_LIGHT : StyleUtil.ACCENT_RED_LIGHT);
        String stockClr = lowStock ? StyleUtil.ACCENT_AMBER : (inStock ? StyleUtil.ACCENT_TEAL : StyleUtil.ACCENT_RED);
        String stockTxt = !inStock ? "Habis" : (lowStock ? "Menipis" : "Tersedia");
        Label stockBadge = new Label(stockTxt);
        stockBadge.setStyle(StyleUtil.badge(stockBg, stockClr));
        topRow.getChildren().addAll(idBadge, topSp, stockBadge);

        
        Label nameLbl = new Label(m.getNameOfMedicine());
        nameLbl.setStyle("-fx-text-fill: " + StyleUtil.TEXT_DARK + ";" +
                         "-fx-font-size: 14px; -fx-font-weight: bold;");
        nameLbl.setWrapText(true);

        
        Label stockLbl = new Label("Stok: " + m.getStockOfMedicine() + " unit");
        stockLbl.setStyle("-fx-text-fill: " + stockClr + "; -fx-font-size: 12px;");

        
        Label priceLbl = new Label(StyleUtil.formatRupiah(m.getPriceOfIndividualMedicine()));
        priceLbl.setStyle("-fx-text-fill: " + StyleUtil.ACCENT_TEAL + ";" +
                          "-fx-font-size: 15px; -fx-font-weight: bold;");

        
        Button addBtn = new Button(inStock ? "+ Tambah" : "Stok Habis");
        addBtn.setStyle(inStock ? StyleUtil.btnPrimary() + "-fx-padding: 7 14; -fx-font-size: 12px;"
                                : StyleUtil.btnSecondary() + "-fx-padding: 7 14; -fx-font-size: 12px;");
        addBtn.setMaxWidth(Double.MAX_VALUE);
        addBtn.setDisable(!inStock);
        addBtn.setOnAction(e -> showQuantityDialog(m));

        card.getChildren().addAll(topRow, nameLbl, stockLbl, priceLbl, addBtn);

        if (inStock) {
            card.setOnMouseEntered(ev -> card.setStyle(StyleUtil.card() + "-fx-cursor: hand;" +
                    "-fx-border-color: " + StyleUtil.ACCENT_TEAL + ";"));
            card.setOnMouseExited(ev -> card.setStyle(StyleUtil.card() + "-fx-cursor: hand;"));
        }
        return card;
    }

    private void showQuantityDialog(Medicine m) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(stage);
        dialog.setTitle("Tambah ke Keranjang");

        VBox box = new VBox(16);
        box.setPadding(new Insets(28));
        box.setStyle("-fx-background-color: " + StyleUtil.BG_SURFACE + ";");
        box.setPrefWidth(340);

        Label title = new Label(m.getNameOfMedicine());
        title.setStyle("-fx-text-fill: " + StyleUtil.TEXT_DARK + "; -fx-font-size: 17px; -fx-font-weight: bold;");
        title.setWrapText(true);

        Label info = new Label("Stok tersedia: " + m.getStockOfMedicine() + " unit  •  " +
                               StyleUtil.formatRupiah(m.getPriceOfIndividualMedicine()) + " / unit");
        info.setStyle(StyleUtil.label(StyleUtil.FONT_SIZE_SM, StyleUtil.TEXT_MUTED));

        Separator sep = new Separator();
        sep.setStyle("-fx-background-color: " + StyleUtil.BORDER + ";");

        Label qtyLbl = new Label("Jumlah");
        qtyLbl.setStyle("-fx-text-fill: " + StyleUtil.TEXT_DARK + "; -fx-font-size: 12px; -fx-font-weight: bold;");

        Spinner<Integer> spinner = new Spinner<>(1, m.getStockOfMedicine(), 1);
        spinner.setStyle(StyleUtil.inputField());
        spinner.setMaxWidth(Double.MAX_VALUE);
        spinner.setEditable(true);

        Label subtotalLbl = new Label(StyleUtil.formatRupiah(m.getPriceOfIndividualMedicine()));
        subtotalLbl.setStyle("-fx-text-fill: " + StyleUtil.ACCENT_TEAL + ";" +
                             "-fx-font-size: 20px; -fx-font-weight: bold;");

        spinner.valueProperty().addListener((obs, o, n) ->
            subtotalLbl.setText("Subtotal: " + StyleUtil.formatRupiah(n * m.getPriceOfIndividualMedicine())));
        subtotalLbl.setText("Subtotal: " + StyleUtil.formatRupiah(m.getPriceOfIndividualMedicine()));

        HBox btnRow = new HBox(10);
        Button cancel  = new Button("Batal");
        Button confirm = new Button("Tambahkan ke Keranjang");
        cancel.setStyle(StyleUtil.btnSecondary());
        confirm.setStyle(StyleUtil.btnPrimary());
        cancel.setOnAction(e -> dialog.close());
        confirm.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(confirm, Priority.ALWAYS);
        confirm.setOnAction(e -> {
            try {
                cart.addItemToCart(new IndividualItemInCart(m, spinner.getValue()));
                refreshCart();
                dialog.close();
            } catch (Exception ex) {
                alert("Tidak Dapat Ditambahkan", ex.getMessage());
            }
        });
        btnRow.getChildren().addAll(cancel, confirm);

        box.getChildren().addAll(title, info, sep, qtyLbl, spinner, subtotalLbl, btnRow);
        dialog.setScene(new Scene(box));
        dialog.showAndWait();
    }

    
    private VBox buildCartPanel() {
        VBox panel = new VBox(16);
        panel.setPrefWidth(290);
        panel.setPadding(new Insets(28, 20, 24, 20));
        panel.setStyle("-fx-background-color: " + StyleUtil.BG_SURFACE + ";" +
                       "-fx-border-color: " + StyleUtil.BORDER + ";" +
                       "-fx-border-width: 0 0 0 1;");

    
        HBox headerRow = new HBox(8);
        headerRow.setAlignment(Pos.CENTER_LEFT);
        Label cartTitle = new Label("Keranjang");
        cartTitle.setStyle("-fx-text-fill: " + StyleUtil.TEXT_DARK + ";" +
                           "-fx-font-size: 17px; -fx-font-weight: bold;");
        itemCountLabel = new Label("(0 item)");
        itemCountLabel.setStyle(StyleUtil.label(StyleUtil.FONT_SIZE_SM, StyleUtil.TEXT_MUTED));
        headerRow.getChildren().addAll(cartTitle, itemCountLabel);

        
        cartContent = new VBox(8);
        Label emptyLbl = new Label("Belum ada item\nTambahkan obat dari daftar");
        emptyLbl.setStyle(StyleUtil.label(StyleUtil.FONT_SIZE_SM, StyleUtil.TEXT_MUTED) +
                          "-fx-text-alignment: center;");
        emptyLbl.setAlignment(Pos.CENTER);
        emptyLbl.setMaxWidth(Double.MAX_VALUE);
        cartContent.getChildren().add(emptyLbl);

        ScrollPane sp = new ScrollPane(cartContent);
        sp.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        sp.setFitToWidth(true);
        VBox.setVgrow(sp, Priority.ALWAYS);

        
        VBox footer = new VBox(12);
        footer.setStyle("-fx-border-color: " + StyleUtil.BORDER + ";" +
                        "-fx-border-width: 1 0 0 0; -fx-padding: 16 0 0 0;");

        HBox totalRow = new HBox();
        totalRow.setAlignment(Pos.CENTER_LEFT);
        Label totalText = new Label("TOTAL");
        totalText.setStyle("-fx-text-fill: " + StyleUtil.TEXT_MUTED + ";" +
                           "-fx-font-size: 11px; -fx-font-weight: bold;");
        Region sp2 = new Region(); HBox.setHgrow(sp2, Priority.ALWAYS);
        totalLabel = new Label(StyleUtil.formatRupiah(0));
        totalLabel.setStyle("-fx-text-fill: " + StyleUtil.ACCENT_TEAL + ";" +
                            "-fx-font-size: 20px; -fx-font-weight: bold;");
        totalRow.getChildren().addAll(totalText, sp2, totalLabel);

        Button checkoutBtn = new Button("Proses Pembayaran");
        checkoutBtn.setStyle(StyleUtil.btnPrimary() + "-fx-font-size: 13px; -fx-padding: 12 18;");
        checkoutBtn.setMaxWidth(Double.MAX_VALUE);
        checkoutBtn.setOnAction(e -> showCheckoutDialog());

        Button clearBtn = new Button("Kosongkan Keranjang");
        clearBtn.setStyle(StyleUtil.btnSecondary() + "-fx-font-size: 12px;");
        clearBtn.setMaxWidth(Double.MAX_VALUE);
        clearBtn.setOnAction(e -> { cart.clearCart(); refreshCart(); });

        footer.getChildren().addAll(totalRow, checkoutBtn, clearBtn);
        panel.getChildren().addAll(headerRow, sp, footer);
        return panel;
    }

    private void refreshCart() {
        cartContent.getChildren().clear();
        List<IndividualItemInCart> items = cart.getItems();

        if (items.isEmpty()) {
            Label empty = new Label("Belum ada item\nTambahkan obat dari daftar");
            empty.setStyle(StyleUtil.label(StyleUtil.FONT_SIZE_SM, StyleUtil.TEXT_MUTED) +
                           "-fx-text-alignment: center;");
            empty.setMaxWidth(Double.MAX_VALUE);
            empty.setAlignment(Pos.CENTER);
            cartContent.getChildren().add(empty);
        } else {
            for (IndividualItemInCart item : items) {
                cartContent.getChildren().add(buildCartRow(item));
            }
        }
        totalLabel.setText(StyleUtil.formatRupiah(cart.getTotalPriceOfCart()));
        itemCountLabel.setText("(" + items.size() + " item)");
    }

    private VBox buildCartRow(IndividualItemInCart item) {
        VBox row = new VBox(4);
        row.setPadding(new Insets(10, 12, 10, 12));
        row.setStyle("-fx-background-color: " + StyleUtil.BG_PAGE + ";" +
                     "-fx-background-radius: 8;");

        HBox topLine = new HBox(6);
        topLine.setAlignment(Pos.CENTER_LEFT);
        Label name = new Label(item.getMedicine().getNameOfMedicine());
        name.setStyle("-fx-text-fill: " + StyleUtil.TEXT_DARK + ";" +
                      "-fx-font-size: 13px; -fx-font-weight: bold;");
        name.setMaxWidth(180);

        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);

        Button del = new Button("×");
        del.setStyle("-fx-background-color: transparent; -fx-text-fill: " + StyleUtil.ACCENT_RED + ";" +
                     "-fx-font-size: 16px; -fx-cursor: hand; -fx-padding: 0 4;");
        del.setOnAction(e -> { cart.removeItemFromCart(item); refreshCart(); });
        topLine.getChildren().addAll(name, sp, del);

        HBox bottomLine = new HBox(8);
        bottomLine.setAlignment(Pos.CENTER_LEFT);
        Label qty = new Label(item.getQuantityOfMedicineBought() + " ×  " +
                              StyleUtil.formatRupiah(item.getMedicine().getPriceOfIndividualMedicine()));
        qty.setStyle(StyleUtil.label(StyleUtil.FONT_SIZE_XS, StyleUtil.TEXT_MUTED));
        Region sp2 = new Region(); HBox.setHgrow(sp2, Priority.ALWAYS);
        Label sub = new Label(StyleUtil.formatRupiah(item.getTotalPriceOfCurrentMedicine()));
        sub.setStyle("-fx-text-fill: " + StyleUtil.ACCENT_TEAL + ";" +
                     "-fx-font-size: 12px; -fx-font-weight: bold;");
        bottomLine.getChildren().addAll(qty, sp2, sub);

        row.getChildren().addAll(topLine, bottomLine);
        return row;
    }

    
    private void showCheckoutDialog() {
        if (cart.getItems().isEmpty()) {
            alert("Keranjang Kosong", "Tambahkan obat terlebih dahulu.");
            return;
        }

        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(stage);
        dialog.setTitle("Proses Pembayaran");

        VBox box = new VBox(16);
        box.setPadding(new Insets(28));
        box.setStyle("-fx-background-color: " + StyleUtil.BG_SURFACE + ";");
        box.setPrefWidth(400);

        Label title = new Label("Proses Pembayaran");
        title.setStyle("-fx-text-fill: " + StyleUtil.TEXT_DARK + ";" +
                       "-fx-font-size: 18px; -fx-font-weight: bold;");

        
        VBox summary = new VBox(8);
        summary.setPadding(new Insets(14));
        summary.setStyle("-fx-background-color: " + StyleUtil.BG_PAGE + ";" +
                         "-fx-background-radius: 8;" +
                         "-fx-border-color: " + StyleUtil.BORDER + ";" +
                         "-fx-border-radius: 8; -fx-border-width: 1;");

        for (IndividualItemInCart item : cart.getItems()) {
            HBox r = new HBox();
            Label n = new Label(item.getMedicine().getNameOfMedicine() +
                                "  ×" + item.getQuantityOfMedicineBought());
            n.setStyle(StyleUtil.label(StyleUtil.FONT_SIZE_MD, StyleUtil.TEXT_DARK));
            Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);
            Label p = new Label(StyleUtil.formatRupiah(item.getTotalPriceOfCurrentMedicine()));
            p.setStyle("-fx-text-fill: " + StyleUtil.TEXT_DARK + "; -fx-font-size: 13px;");
            r.getChildren().addAll(n, sp, p);
            summary.getChildren().add(r);
        }

        Separator sep = new Separator();
        sep.setStyle("-fx-background-color: " + StyleUtil.BORDER + ";");
        summary.getChildren().add(sep);

        int total = cart.getTotalPriceOfCart();
        HBox totalRow = new HBox();
        Label totalLbl = new Label("Total");
        totalLbl.setStyle("-fx-text-fill: " + StyleUtil.TEXT_DARK + "; -fx-font-size: 13px; -fx-font-weight: bold;");
        Region sp3 = new Region(); HBox.setHgrow(sp3, Priority.ALWAYS);
        Label totalVal = new Label(StyleUtil.formatRupiah(total));
        totalVal.setStyle("-fx-text-fill: " + StyleUtil.ACCENT_TEAL + ";" +
                          "-fx-font-size: 16px; -fx-font-weight: bold;");
        totalRow.getChildren().addAll(totalLbl, sp3, totalVal);
        summary.getChildren().add(totalRow);

       
        Label paidLbl = new Label("Jumlah Dibayar (Rp)");
        paidLbl.setStyle("-fx-text-fill: " + StyleUtil.TEXT_DARK + "; -fx-font-size: 12px; -fx-font-weight: bold;");
        TextField paidField = new TextField();
        paidField.setPromptText("Masukkan nominal...");
        paidField.setStyle(StyleUtil.inputField() + "-fx-font-size: 14px; -fx-padding: 11 14;");

        Label changeLbl = new Label("Kembalian: —");
        changeLbl.setStyle(StyleUtil.label(StyleUtil.FONT_SIZE_LG, StyleUtil.TEXT_MUTED));

        paidField.textProperty().addListener((obs, o, n) -> {
            try {
                int paid = Integer.parseInt(n.replaceAll("[^0-9]", ""));
                if (paid >= total) {
                    changeLbl.setText("Kembalian: " + StyleUtil.formatRupiah(paid - total));
                    changeLbl.setStyle("-fx-text-fill: " + StyleUtil.ACCENT_TEAL + ";" +
                                       "-fx-font-size: 16px; -fx-font-weight: bold;");
                } else {
                    changeLbl.setText("Kurang: " + StyleUtil.formatRupiah(total - paid));
                    changeLbl.setStyle("-fx-text-fill: " + StyleUtil.ACCENT_RED + ";" +
                                       "-fx-font-size: 15px; -fx-font-weight: bold;");
                }
            } catch (NumberFormatException ignored) {
                changeLbl.setText("Kembalian: —");
                changeLbl.setStyle(StyleUtil.label(StyleUtil.FONT_SIZE_LG, StyleUtil.TEXT_MUTED));
            }
        });

        HBox btnRow = new HBox(10);
        Button cancel = new Button("Batal");
        Button pay    = new Button("Bayar Sekarang");
        cancel.setStyle(StyleUtil.btnSecondary());
        pay.setStyle(StyleUtil.btnPrimary() + "-fx-padding: 11 20;");
        HBox.setHgrow(pay, Priority.ALWAYS);
        pay.setMaxWidth(Double.MAX_VALUE);
        cancel.setOnAction(e -> dialog.close());
        pay.setOnAction(e -> {
            try {
                int paid = Integer.parseInt(paidField.getText().replaceAll("[^0-9]", ""));
                if (paid < total) {
                    alert("Pembayaran Kurang", "Jumlah yang dibayar kurang dari total.");
                    return;
                }
                Transaction tx = new Transaction(cart, "Cash");
                tx.setAmountPaid(paid);
                tx.processTransaction();
                
                for (IndividualItemInCart item : tx.getItemSaves()) {
                    service.DataBaseHelper.updatedMedicineStock(
                        item.getMedicine().getIdOfMedicine(),
                        item.getMedicine().getStockOfMedicine()
                    );
                }
                service.DataBaseHelper.saveTransaction(tx);

                Receipt receipt = new Receipt(
                    tx.getTransactionId(), tx.getItemSaves(),
                    tx.getTotalPrice(), tx.getPaymentMethod(),
                    tx.getPaidAmount(), tx.getChange()
                );
                dialog.close();
                new ReceiptView(stage, receipt).show();
                refreshCart();
            } catch (Exception ex) {
                alert("Error", ex.getMessage());
            }
        });
        btnRow.getChildren().addAll(cancel, pay);

        box.getChildren().addAll(title, summary, paidLbl, paidField, changeLbl, btnRow);
        dialog.setScene(new Scene(box));
        dialog.showAndWait();
    }

    private void alert(String title, String msg) {
        Alert a = new Alert(Alert.AlertType.WARNING);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }
}
