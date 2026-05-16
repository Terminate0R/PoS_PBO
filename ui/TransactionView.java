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
    private StackPane rightStack;
    private VBox cartPanel;

    public TransactionView(Stage stage) {
        this.stage = stage;
    }

    public void show() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + StyleUtil.BG_PAGE + ";");
        root.setLeft(Sidebar.build(stage, "transaction"));
        root.setCenter(buildCenter());

        cartPanel = buildCartPanel();
        rightStack = new StackPane();
        rightStack.getChildren().add(cartPanel);
        root.setRight(rightStack);

        Scene existing = stage.getScene();
        if (existing != null) {
            existing.setRoot(root);
        } else {
            stage.setScene(new Scene(root, 1200, 720));
            stage.setMaximized(true);
        }
        stage.setTitle("ApotekPOS — Transaksi");
    }

    // ── Center: medicine grid ────────────────────────────────────────────────
    private VBox buildCenter() {
        VBox center = new VBox(16);
        center.setPadding(new Insets(28, 24, 28, 28));

        Label title = new Label("Pilih Obat");
        title.setStyle("-fx-text-fill: " + StyleUtil.TEXT_DARK + "; -fx-font-size: 22px; -fx-font-weight: bold;");
        Label sub = new Label("Klik kartu obat untuk menambahkan ke keranjang");
        sub.setStyle("-fx-text-fill: " + StyleUtil.TEXT_MUTED + "; -fx-font-size: 14px;");

        TextField search = new TextField();
        search.setPromptText("Cari nama obat...");
        search.setStyle(StyleUtil.inputField() + "-fx-font-size: 14px; -fx-padding: 10 14;");
        search.setMaxWidth(Double.MAX_VALUE);

        ScrollPane grid = buildMedicineGrid(search);
        VBox.setVgrow(grid, Priority.ALWAYS);

        center.getChildren().addAll(title, sub, search, grid);
        return center;
    }

    private ScrollPane buildMedicineGrid(TextField searchField) {
        FlowPane flow = new FlowPane(12, 12);
        flow.setPadding(new Insets(4));
        flow.setStyle("-fx-background-color: transparent;");

        Runnable refresh = () -> {
            String q = searchField.getText().toLowerCase();
            flow.getChildren().clear();
            for (Medicine m : ApotekApp.medicines) {
                if (q.isEmpty() || m.getNameOfMedicine().toLowerCase().contains(q))
                    flow.getChildren().add(buildMedicineCard(m));
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
        boolean inStock  = m.getStockOfMedicine() > 0;
        boolean lowStock = inStock && m.getStockOfMedicine() < 20;

        VBox card = new VBox(8);
        card.setPadding(new Insets(14));
        card.setPrefWidth(210);
        card.setStyle(StyleUtil.card() + (inStock ? "-fx-cursor: hand;" : "-fx-opacity: 0.55;"));

        // Top: ID badge + stock status badge
        HBox topRow = new HBox(6);
        topRow.setAlignment(Pos.CENTER_LEFT);
        Label idBadge = new Label("ID " + m.getIdOfMedicine());
        idBadge.setStyle(StyleUtil.badge(StyleUtil.BG_INPUT, StyleUtil.TEXT_MUTED));
        Region topSp = new Region(); HBox.setHgrow(topSp, Priority.ALWAYS);

        String stockBg  = lowStock ? StyleUtil.ACCENT_AMBER_LIGHT : (inStock ? StyleUtil.ACCENT_TEAL_LIGHT : StyleUtil.ACCENT_RED_LIGHT);
        String stockClr = lowStock ? StyleUtil.ACCENT_AMBER        : (inStock ? StyleUtil.ACCENT_TEAL       : StyleUtil.ACCENT_RED);
        String stockTxt = !inStock ? "Habis" : (lowStock ? "Menipis" : "Tersedia");
        Label stockBadge = new Label(stockTxt);
        stockBadge.setStyle(StyleUtil.badge(stockBg, stockClr));
        topRow.getChildren().addAll(idBadge, topSp, stockBadge);

        // Name
        Label nameLbl = new Label(m.getNameOfMedicine());
        nameLbl.setStyle("-fx-text-fill: " + StyleUtil.TEXT_DARK + "; -fx-font-size: 15px; -fx-font-weight: bold;");
        nameLbl.setWrapText(true);

        // Stock count
        Label stockLbl = new Label("Stok: " + m.getStockOfMedicine() + " unit");
        stockLbl.setStyle("-fx-text-fill: " + stockClr + "; -fx-font-size: 13px;");

        // Price
        Label priceLbl = new Label(StyleUtil.formatRupiah(m.getPriceOfIndividualMedicine()));
        priceLbl.setStyle("-fx-text-fill: " + StyleUtil.ACCENT_TEAL + "; -fx-font-size: 16px; -fx-font-weight: bold;");

        // Add button
        Button addBtn = new Button(inStock ? "+ Tambah" : "Stok Habis");
        addBtn.setStyle(inStock
            ? StyleUtil.btnPrimary()   + "-fx-padding: 7 12; -fx-font-size: 13px;"
            : StyleUtil.btnSecondary() + "-fx-padding: 7 12; -fx-font-size: 13px;");
        addBtn.setMaxWidth(Double.MAX_VALUE);
        addBtn.setDisable(!inStock);
        addBtn.setOnAction(e -> showAddPanel(m));

        card.getChildren().addAll(topRow, nameLbl, stockLbl, priceLbl, addBtn);
        if (inStock) {
            card.setOnMouseEntered(ev -> card.setStyle(StyleUtil.card() + "-fx-cursor: hand; -fx-border-color: " + StyleUtil.ACCENT_TEAL + ";"));
            card.setOnMouseExited(ev  -> card.setStyle(StyleUtil.card() + "-fx-cursor: hand;"));
        }
        return card;
    }

    // ── Right panel: add to cart ─────────────────────────────────────────────
    private void showAddPanel(Medicine m) {
        VBox panel = rightPanel();

        Button back = backBtn();
        back.setOnAction(e -> swapRight(cartPanel));

        Label title = new Label(m.getNameOfMedicine());
        title.setStyle("-fx-text-fill: " + StyleUtil.TEXT_DARK + "; -fx-font-size: 17px; -fx-font-weight: bold;");
        title.setWrapText(true);

        Label info = new Label("Stok: " + m.getStockOfMedicine() + " unit  •  " +
                               StyleUtil.formatRupiah(m.getPriceOfIndividualMedicine()) + "/unit");
        info.setStyle("-fx-text-fill: " + StyleUtil.TEXT_MUTED + "; -fx-font-size: 13px;");

        Label qtyLbl = new Label("Jumlah");
        qtyLbl.setStyle("-fx-text-fill: " + StyleUtil.TEXT_DARK + "; -fx-font-size: 14px; -fx-font-weight: bold;");

        Spinner<Integer> spinner = new Spinner<Integer>(1, m.getStockOfMedicine(), 1);
        spinner.setMaxWidth(Double.MAX_VALUE);
        spinner.setEditable(true);

        Label subtotalLbl = new Label("Subtotal: " + StyleUtil.formatRupiah(m.getPriceOfIndividualMedicine()));
        subtotalLbl.setStyle("-fx-text-fill: " + StyleUtil.ACCENT_TEAL + "; -fx-font-size: 18px; -fx-font-weight: bold;");
        spinner.valueProperty().addListener((obs, o, n) ->
            subtotalLbl.setText("Subtotal: " + StyleUtil.formatRupiah(n * m.getPriceOfIndividualMedicine())));

        Button confirm = new Button("Tambahkan ke Keranjang");
        confirm.setStyle(StyleUtil.btnPrimary() + "-fx-padding: 11 18; -fx-font-size: 14px;");
        confirm.setMaxWidth(Double.MAX_VALUE);
        confirm.setOnAction(e -> {
            try {
                cart.addItemToCart(new IndividualItemInCart(m, spinner.getValue()));
                refreshCart();
                swapRight(cartPanel);
            } catch (Exception ex) {
                alert("Tidak Dapat Ditambahkan", ex.getMessage());
            }
        });

        panel.getChildren().addAll(back, title, info, new Separator(), qtyLbl, spinner, subtotalLbl, confirm);
        swapRight(panel);
    }

    // ── Right panel: cart ────────────────────────────────────────────────────
    private VBox buildCartPanel() {
        VBox panel = rightPanel();

        HBox headerRow = new HBox(8);
        headerRow.setAlignment(Pos.CENTER_LEFT);
        Label cartTitle = new Label("Keranjang");
        cartTitle.setStyle("-fx-text-fill: " + StyleUtil.TEXT_DARK + "; -fx-font-size: 18px; -fx-font-weight: bold;");
        itemCountLabel = new Label("(0 item)");
        itemCountLabel.setStyle("-fx-text-fill: " + StyleUtil.TEXT_MUTED + "; -fx-font-size: 13px;");
        headerRow.getChildren().addAll(cartTitle, itemCountLabel);

        cartContent = new VBox(8);
        Label emptyLbl = new Label("Belum ada item\nTambahkan obat dari daftar");
        emptyLbl.setStyle("-fx-text-fill: " + StyleUtil.TEXT_MUTED + "; -fx-font-size: 13px; -fx-text-alignment: center;");
        emptyLbl.setAlignment(Pos.CENTER);
        emptyLbl.setMaxWidth(Double.MAX_VALUE);
        cartContent.getChildren().add(emptyLbl);

        ScrollPane sp = new ScrollPane(cartContent);
        sp.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        sp.setFitToWidth(true);
        VBox.setVgrow(sp, Priority.ALWAYS);

        // Footer
        VBox footer = new VBox(10);
        footer.setStyle("-fx-border-color: " + StyleUtil.BORDER + "; -fx-border-width: 1 0 0 0; -fx-padding: 14 0 0 0;");

        HBox totalRow = new HBox();
        totalRow.setAlignment(Pos.CENTER_LEFT);
        Label totalText = new Label("TOTAL");
        totalText.setStyle("-fx-text-fill: " + StyleUtil.TEXT_MUTED + "; -fx-font-size: 12px; -fx-font-weight: bold;");
        Region sp2 = new Region(); HBox.setHgrow(sp2, Priority.ALWAYS);
        totalLabel = new Label(StyleUtil.formatRupiah(0));
        totalLabel.setStyle("-fx-text-fill: " + StyleUtil.ACCENT_TEAL + "; -fx-font-size: 22px; -fx-font-weight: bold;");
        totalRow.getChildren().addAll(totalText, sp2, totalLabel);

        Button checkoutBtn = new Button("Proses Pembayaran");
        checkoutBtn.setStyle(StyleUtil.btnPrimary() + "-fx-font-size: 14px; -fx-padding: 11 18;");
        checkoutBtn.setMaxWidth(Double.MAX_VALUE);
        checkoutBtn.setOnAction(e -> showCheckoutPanel());

        Button clearBtn = new Button("Kosongkan Keranjang");
        clearBtn.setStyle(StyleUtil.btnSecondary() + "-fx-font-size: 13px; -fx-padding: 8 14;");
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
            empty.setStyle("-fx-text-fill: " + StyleUtil.TEXT_MUTED + "; -fx-font-size: 13px; -fx-text-alignment: center;");
            empty.setMaxWidth(Double.MAX_VALUE);
            empty.setAlignment(Pos.CENTER);
            cartContent.getChildren().add(empty);
        } else {
            for (IndividualItemInCart item : items)
                cartContent.getChildren().add(buildCartRow(item));
        }
        totalLabel.setText(StyleUtil.formatRupiah(cart.getTotalPriceOfCart()));
        itemCountLabel.setText("(" + items.size() + " item)");
    }

    private VBox buildCartRow(IndividualItemInCart item) {
        VBox row = new VBox(4);
        row.setPadding(new Insets(10, 12, 10, 12));
        row.setStyle("-fx-background-color: " + StyleUtil.BG_PAGE + "; -fx-background-radius: 8;");

        HBox topLine = new HBox(6);
        topLine.setAlignment(Pos.CENTER_LEFT);
        Label name = new Label(item.getMedicine().getNameOfMedicine());
        name.setStyle("-fx-text-fill: " + StyleUtil.TEXT_DARK + "; -fx-font-size: 14px; -fx-font-weight: bold;");
        name.setMaxWidth(185);
        name.setWrapText(true);
        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);
        Button del = new Button("×");
        del.setStyle("-fx-background-color: transparent; -fx-text-fill: " + StyleUtil.ACCENT_RED + ";" +
                     "-fx-font-size: 18px; -fx-cursor: hand; -fx-padding: 0 4;");
        del.setOnAction(e -> { cart.removeItemFromCart(item); refreshCart(); });
        topLine.getChildren().addAll(name, sp, del);

        HBox bottomLine = new HBox(8);
        bottomLine.setAlignment(Pos.CENTER_LEFT);
        Label qty = new Label(item.getQuantityOfMedicineBought() + " ×  " +
                              StyleUtil.formatRupiah(item.getMedicine().getPriceOfIndividualMedicine()));
        qty.setStyle("-fx-text-fill: " + StyleUtil.TEXT_MUTED + "; -fx-font-size: 12px;");
        Region sp2 = new Region(); HBox.setHgrow(sp2, Priority.ALWAYS);
        Label sub = new Label(StyleUtil.formatRupiah(item.getTotalPriceOfCurrentMedicine()));
        sub.setStyle("-fx-text-fill: " + StyleUtil.ACCENT_TEAL + "; -fx-font-size: 14px; -fx-font-weight: bold;");
        bottomLine.getChildren().addAll(qty, sp2, sub);

        row.getChildren().addAll(topLine, bottomLine);
        return row;
    }

    // ── Right panel: checkout ────────────────────────────────────────────────
    private void showCheckoutPanel() {
        if (cart.getItems().isEmpty()) { alert("Keranjang Kosong", "Tambahkan obat terlebih dahulu."); return; }

        VBox panel = rightPanel();

        Button back = backBtn();
        back.setOnAction(e -> swapRight(cartPanel));

        Label title = new Label("Proses Pembayaran");
        title.setStyle("-fx-text-fill: " + StyleUtil.TEXT_DARK + "; -fx-font-size: 18px; -fx-font-weight: bold;");

        // Order summary
        VBox summary = new VBox(6);
        summary.setPadding(new Insets(12));
        summary.setStyle("-fx-background-color: " + StyleUtil.BG_PAGE + "; -fx-background-radius: 8;" +
                         "-fx-border-color: " + StyleUtil.BORDER + "; -fx-border-radius: 8; -fx-border-width: 1;");

        for (IndividualItemInCart item : cart.getItems()) {
            HBox r = new HBox();
            Label n = new Label(item.getMedicine().getNameOfMedicine() + " ×" + item.getQuantityOfMedicineBought());
            n.setStyle("-fx-text-fill: " + StyleUtil.TEXT_DARK + "; -fx-font-size: 13px;");
            n.setMaxWidth(155); n.setWrapText(true);
            Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);
            Label p = new Label(StyleUtil.formatRupiah(item.getTotalPriceOfCurrentMedicine()));
            p.setStyle("-fx-text-fill: " + StyleUtil.TEXT_DARK + "; -fx-font-size: 13px;");
            r.getChildren().addAll(n, sp, p);
            summary.getChildren().add(r);
        }

        summary.getChildren().add(new Separator());

        int total = cart.getTotalPriceOfCart();
        HBox totalRow = new HBox();
        Label tl = new Label("Total");
        tl.setStyle("-fx-text-fill: " + StyleUtil.TEXT_DARK + "; -fx-font-size: 14px; -fx-font-weight: bold;");
        Region sp3 = new Region(); HBox.setHgrow(sp3, Priority.ALWAYS);
        Label tv = new Label(StyleUtil.formatRupiah(total));
        tv.setStyle("-fx-text-fill: " + StyleUtil.ACCENT_TEAL + "; -fx-font-size: 16px; -fx-font-weight: bold;");
        totalRow.getChildren().addAll(tl, sp3, tv);
        summary.getChildren().add(totalRow);

        // Payment input
        Label paidLbl = new Label("Jumlah Dibayar (Rp)");
        paidLbl.setStyle("-fx-text-fill: " + StyleUtil.TEXT_DARK + "; -fx-font-size: 14px; -fx-font-weight: bold;");

        TextField paidField = new TextField();
        paidField.setPromptText("Masukkan nominal...");
        paidField.setStyle(StyleUtil.inputField() + "-fx-font-size: 14px; -fx-padding: 10 12;");
        paidField.setMaxWidth(Double.MAX_VALUE);

        Label changeLbl = new Label("Kembalian: —");
        changeLbl.setStyle("-fx-text-fill: " + StyleUtil.TEXT_MUTED + "; -fx-font-size: 14px;");

        paidField.textProperty().addListener((obs, o, n) -> {
            try {
                int paid = Integer.parseInt(n.replaceAll("[^0-9]", ""));
                if (paid >= total) {
                    changeLbl.setText("Kembalian: " + StyleUtil.formatRupiah(paid - total));
                    changeLbl.setStyle("-fx-text-fill: " + StyleUtil.ACCENT_TEAL + "; -fx-font-size: 16px; -fx-font-weight: bold;");
                } else {
                    changeLbl.setText("Kurang: " + StyleUtil.formatRupiah(total - paid));
                    changeLbl.setStyle("-fx-text-fill: " + StyleUtil.ACCENT_RED + "; -fx-font-size: 16px; -fx-font-weight: bold;");
                }
            } catch (NumberFormatException ignored) {
                changeLbl.setText("Kembalian: —");
                changeLbl.setStyle("-fx-text-fill: " + StyleUtil.TEXT_MUTED + "; -fx-font-size: 14px;");
            }
        });

        Button payBtn = new Button("Bayar Sekarang");
        payBtn.setStyle(StyleUtil.btnPrimary() + "-fx-padding: 11 18; -fx-font-size: 14px;");
        payBtn.setMaxWidth(Double.MAX_VALUE);
        payBtn.setOnAction(e -> {
            try {
                int paid = Integer.parseInt(paidField.getText().replaceAll("[^0-9]", ""));
                if (paid < total) { alert("Pembayaran Kurang", "Jumlah yang dibayar kurang dari total."); return; }
                Transaction tx = new Transaction(cart, "Cash");
                tx.setAmountPaid(paid);
                tx.processTransaction();
                for (IndividualItemInCart item : tx.getItemSaves())
                    service.DataBaseHelper.updatedMedicineStock(
                        item.getMedicine().getIdOfMedicine(), item.getMedicine().getStockOfMedicine());
                service.DataBaseHelper.saveTransaction(tx);
                Receipt receipt = new Receipt(tx.getTransactionId(), tx.getItemSaves(),
                    tx.getTotalPrice(), tx.getPaymentMethod(), tx.getPaidAmount(), tx.getChange());
                cart.clearCart();
                refreshCart();
                showReceiptPanel(receipt);
            } catch (Exception ex) { alert("Error", ex.getMessage()); }
        });

        ScrollPane sp = new ScrollPane();
        VBox inner = new VBox(12, back, title, summary, paidLbl, paidField, changeLbl, payBtn);
        sp.setContent(inner);
        sp.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        sp.setFitToWidth(true);
        VBox.setVgrow(sp, Priority.ALWAYS);
        panel.getChildren().add(sp);
        swapRight(panel);
    }

    // ── Right panel: receipt ─────────────────────────────────────────────────
    private void showReceiptPanel(Receipt receipt) {
        VBox panel = rightPanel();

        // Success banner
        Label check = new Label("✓  Pembayaran Berhasil!");
        check.setStyle("-fx-text-fill: " + StyleUtil.ACCENT_TEAL + "; -fx-font-size: 15px; -fx-font-weight: bold;" +
                       "-fx-background-color: " + StyleUtil.ACCENT_TEAL_LIGHT + "; -fx-background-radius: 8; -fx-padding: 10 14;");
        check.setMaxWidth(Double.MAX_VALUE);

        // Receipt card
        VBox card = new VBox(8);
        card.setPadding(new Insets(14));
        card.setStyle(StyleUtil.card());

        // TX ID
        String txId = receipt.getTransactionId();
        if (txId.length() > 18) txId = txId.substring(0, 18) + "…";
        Label txLbl = new Label("ID: " + txId);
        txLbl.setStyle("-fx-text-fill: " + StyleUtil.TEXT_MUTED + "; -fx-font-size: 11px;");

        // Items
        VBox items = new VBox(6);
        for (IndividualItemInCart item : receipt.getListOfItems()) {
            HBox r = new HBox();
            Label n = new Label(item.getMedicine().getNameOfMedicine() + " ×" + item.getQuantityOfMedicineBought());
            n.setStyle("-fx-text-fill: " + StyleUtil.TEXT_DARK + "; -fx-font-size: 13px;");
            n.setMaxWidth(148); n.setWrapText(true);
            Region sp2 = new Region(); HBox.setHgrow(sp2, Priority.ALWAYS);
            Label p = new Label(StyleUtil.formatRupiah(item.getTotalPriceOfCurrentMedicine()));
            p.setStyle("-fx-text-fill: " + StyleUtil.TEXT_DARK + "; -fx-font-size: 13px;");
            r.getChildren().addAll(n, sp2, p);
            items.getChildren().add(r);
        }

        card.getChildren().addAll(
            txLbl, new Separator(), items, new Separator(),
            payRow("Total Harga",  StyleUtil.formatRupiah(receipt.getTotalPrice()),  StyleUtil.TEXT_DARK,    false),
            payRow("Dibayar",      StyleUtil.formatRupiah(receipt.getPaidAmount()),  StyleUtil.ACCENT_BLUE,  false),
            payRow("Kembalian",    StyleUtil.formatRupiah(receipt.getChange()),       StyleUtil.ACCENT_TEAL,  true)
        );

        // Action buttons
        Button btnNew  = new Button("Transaksi Baru");
        Button btnDash = new Button("Dashboard");
        btnNew.setStyle(StyleUtil.btnPrimary()   + "-fx-padding: 10 12; -fx-font-size: 13px;");
        btnDash.setStyle(StyleUtil.btnSecondary() + "-fx-padding: 10 12; -fx-font-size: 13px;");
        btnNew.setMaxWidth(Double.MAX_VALUE);
        btnDash.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(btnNew, Priority.ALWAYS);
        HBox.setHgrow(btnDash, Priority.ALWAYS);
        btnNew.setOnAction(e  -> new TransactionView(stage).show());
        btnDash.setOnAction(e -> new DashboardView(stage).show());
        HBox actions = new HBox(10, btnDash, btnNew);

        ScrollPane sp = new ScrollPane();
        VBox inner = new VBox(14, check, card, actions);
        sp.setContent(inner);
        sp.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        sp.setFitToWidth(true);
        VBox.setVgrow(sp, Priority.ALWAYS);
        panel.getChildren().add(sp);
        swapRight(panel);
    }

    private HBox payRow(String label, String value, String color, boolean large) {
        HBox row = new HBox();
        row.setAlignment(Pos.CENTER_LEFT);
        Label l = new Label(label);
        l.setStyle("-fx-text-fill: " + StyleUtil.TEXT_MUTED + "; -fx-font-size: 13px;");
        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);
        Label v = new Label(value);
        v.setStyle("-fx-text-fill: " + color + "; -fx-font-size: " + (large ? "18px" : "14px") +
                   "; -fx-font-weight: " + (large ? "bold" : "normal") + ";");
        row.getChildren().addAll(l, sp, v);
        return row;
    }

    // ── Helpers ──────────────────────────────────────────────────────────────
    private VBox rightPanel() {
        VBox p = new VBox(14);
        p.setPrefWidth(300);
        p.setPadding(new Insets(24, 20, 20, 20));
        p.setStyle("-fx-background-color: " + StyleUtil.BG_SURFACE + ";" +
                   "-fx-border-color: " + StyleUtil.BORDER + "; -fx-border-width: 0 0 0 1;");
        return p;
    }

    private Button backBtn() {
        Button b = new Button("← Kembali ke Keranjang");
        b.setStyle("-fx-background-color: transparent; -fx-text-fill: " + StyleUtil.ACCENT_TEAL +
                   "; -fx-font-size: 13px; -fx-cursor: hand; -fx-padding: 0;");
        return b;
    }

    private void swapRight(javafx.scene.Node node) {
        rightStack.getChildren().setAll(node);
    }

    private void alert(String title, String msg) {
        Alert a = new Alert(Alert.AlertType.WARNING);
        a.setTitle(title); a.setHeaderText(null); a.setContentText(msg);
        a.showAndWait();
    }
}
