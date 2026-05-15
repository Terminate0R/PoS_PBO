package ui;

import model.Medicine;
import javafx.beans.property.*;
import javafx.collections.*;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.*;

public class InventoryView {

    private final Stage stage;
    private TableView<MedicineRow> table;
    private ObservableList<MedicineRow> data;

    public InventoryView(Stage stage) {
        this.stage = stage;
    }

    public void show() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + StyleUtil.BG_PAGE + ";");
        root.setLeft(Sidebar.build(stage, "inventory"));
        root.setCenter(buildCenter());

        stage.setScene(new Scene(root, 1100, 700));
        stage.setTitle("ApotekPOS — Inventaris");
        stage.setMaximized(true);
    }

    private VBox buildCenter() {
        VBox center = new VBox(22);
        center.setPadding(new Insets(36, 40, 36, 40));

    
        HBox header = new HBox(12);
        header.setAlignment(Pos.CENTER_LEFT);

        VBox titleBox = new VBox(4);
        Label title = new Label("Manajemen Inventaris");
        title.setStyle("-fx-text-fill: " + StyleUtil.TEXT_DARK + ";" +
                       "-fx-font-size: 24px; -fx-font-weight: bold;");
        Label sub = new Label("Kelola stok, harga, dan data obat");
        sub.setStyle(StyleUtil.label(StyleUtil.FONT_SIZE_SM, StyleUtil.TEXT_MUTED));
        titleBox.getChildren().addAll(title, sub);

        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);

        Button addBtn = new Button("+ Tambah Obat");
        addBtn.setStyle(StyleUtil.btnPrimary());
        addBtn.setOnAction(e -> showAddDialog());

        header.getChildren().addAll(titleBox, sp, addBtn);

       
        HBox searchRow = new HBox(12);
        searchRow.setAlignment(Pos.CENTER_LEFT);
        TextField search = new TextField();
        search.setPromptText("Cari nama obat...");
        search.setStyle(StyleUtil.inputField() + "-fx-font-size: 13px; -fx-padding: 10 14;");
        search.setPrefWidth(300);

        Label countLbl = new Label(ApotekApp.medicines.size() + " obat terdaftar");
        countLbl.setStyle(StyleUtil.label(StyleUtil.FONT_SIZE_SM, StyleUtil.TEXT_MUTED));
        searchRow.getChildren().addAll(search, countLbl);

      
        table = buildTable();
        VBox.setVgrow(table, Priority.ALWAYS);

        search.textProperty().addListener((obs, o, n) -> {
            String q = n.toLowerCase();
            data.setAll(ApotekApp.medicines.stream()
                .filter(m -> m.getNameOfMedicine().toLowerCase().contains(q))
                .map(MedicineRow::new)
                .toArray(MedicineRow[]::new));
        });

        center.getChildren().addAll(header, searchRow, table);
        return center;
    }

    private TableView<MedicineRow> buildTable() {
        data = FXCollections.observableArrayList(
            ApotekApp.medicines.stream()
                .map(MedicineRow::new)
                .collect(java.util.stream.Collectors.toList()));

        TableView<MedicineRow> tv = new TableView<>(data);
        tv.setStyle("-fx-background-color: " + StyleUtil.BG_SURFACE + ";" +
                    "-fx-border-color: " + StyleUtil.BORDER + ";" +
                    "-fx-border-width: 1; -fx-background-radius: 10; -fx-border-radius: 10;");
        tv.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<MedicineRow, Integer> colId    = col("ID",        "id",    70);
        TableColumn<MedicineRow, String>  colName  = col("Nama Obat", "name",  240);
        TableColumn<MedicineRow, Integer> colStock = col("Stok",      "stock", 110);
        TableColumn<MedicineRow, String>  colPrice = col("Harga",     "price", 160);
        TableColumn<MedicineRow, String>  colAct   = new TableColumn<>("Aksi");
        colAct.setMinWidth(160);

        
        colStock.setCellFactory(c -> new TableCell<MedicineRow, Integer>() {
            @Override protected void updateItem(Integer v, boolean empty) {
                super.updateItem(v, empty);
                if (empty || v == null) { setText(null); setGraphic(null); return; }
                Label badge = new Label(v + " unit");
                String bg  = v < 20 ? StyleUtil.ACCENT_AMBER_LIGHT : StyleUtil.ACCENT_TEAL_LIGHT;
                String clr = v < 20 ? StyleUtil.ACCENT_AMBER : StyleUtil.ACCENT_TEAL;
                badge.setStyle(StyleUtil.badge(bg, clr));
                setGraphic(badge);
                setText(null);
            }
        });

       
        colAct.setCellFactory(c -> new TableCell<MedicineRow, String>() {
            final Button edit = new Button("Edit");
            final Button del  = new Button("Hapus");
            final HBox box    = new HBox(8, edit, del);
            {
                box.setAlignment(Pos.CENTER_LEFT);
                edit.setStyle(StyleUtil.btnAmber() + "-fx-padding: 5 14;");
                del.setStyle(StyleUtil.btnDanger() + "-fx-padding: 5 14;");
                edit.setOnAction(e -> showEditDialog(getTableView().getItems().get(getIndex()).getMedicine()));
                del.setOnAction(e  -> confirmDelete(getTableView().getItems().get(getIndex()).getMedicine()));
            }
            @Override protected void updateItem(String v, boolean empty) {
                super.updateItem(v, empty);
                setGraphic(empty ? null : box);
            }
        });

        tv.getColumns().addAll(colId, colName, colStock, colPrice, colAct);
        return tv;
    }

    @SuppressWarnings("unchecked")
    private <T> TableColumn<MedicineRow, T> col(String header, String prop, double width) {
        TableColumn<MedicineRow, T> c = new TableColumn<>(header);
        c.setCellValueFactory(new PropertyValueFactory<>(prop));
        c.setMinWidth(width);
        c.setStyle("-fx-alignment: CENTER-LEFT;");
        return c;
    }

    private void refreshTable() {
        data.setAll(ApotekApp.medicines.stream()
            .map(MedicineRow::new)
            .collect(java.util.stream.Collectors.toList()));
    }

  
    private void showAddDialog() {
        Stage d = dialogStage("Tambah Obat Baru");

        VBox box = dialogBox();

        Label title = dialogTitle("Tambah Obat Baru");

        TextField idFld    = styledField("ID Obat (angka)");
        TextField nameFld  = styledField("Nama Obat");
        TextField stockFld = styledField("Stok Awal");
        TextField priceFld = styledField("Harga Satuan (Rp)");

        Label errLbl = new Label("");
        errLbl.setStyle("-fx-text-fill: " + StyleUtil.ACCENT_RED + "; -fx-font-size: 12px;");
        errLbl.setVisible(false);

        HBox btnRow = new HBox(10);
        Button cancel = new Button("Batal");
        Button save   = new Button("Simpan");
        cancel.setStyle(StyleUtil.btnSecondary());
        save.setStyle(StyleUtil.btnPrimary() + "-fx-padding: 11 20;");
        HBox.setHgrow(save, Priority.ALWAYS);
        save.setMaxWidth(Double.MAX_VALUE);
        cancel.setOnAction(e -> d.close());
        save.setOnAction(e -> {
            try {
                int id = Integer.parseInt(idFld.getText().trim());
                String nm = nameFld.getText().trim();
                int st = Integer.parseInt(stockFld.getText().trim());
                int pr = Integer.parseInt(priceFld.getText().trim());
                if (nm.isEmpty()) throw new Exception("Nama tidak boleh kosong.");
                if (st < 0 || pr < 0) throw new Exception("Stok dan harga tidak boleh negatif.");
                ApotekApp.inventoryManager.addMedicine(id, nm, st, pr);
                service.DataBaseHelper.saveMedicine(new Medicine(id, nm, st, pr));
                refreshTable();
                d.close();
            } catch (NumberFormatException ex) {
                errLbl.setText("ID, Stok, dan Harga harus berupa angka.");
                errLbl.setVisible(true);
            } catch (Exception ex) {
                errLbl.setText(ex.getMessage());
                errLbl.setVisible(true);
            }
        });
        btnRow.getChildren().addAll(cancel, save);

        box.getChildren().addAll(title, idFld, nameFld, stockFld, priceFld, errLbl, btnRow);
        d.setScene(new Scene(box));
        d.showAndWait();
    }

  
    private void showEditDialog(Medicine m) {
        Stage d = dialogStage("Edit Obat");

        VBox box = dialogBox();

        Label title = dialogTitle("Edit: " + m.getNameOfMedicine());

        TextField nameFld  = styledField("Nama Obat");
        TextField stockFld = styledField("Stok");
        TextField priceFld = styledField("Harga (Rp)");
        nameFld.setText(m.getNameOfMedicine());
        stockFld.setText(String.valueOf(m.getStockOfMedicine()));
        priceFld.setText(String.valueOf(m.getPriceOfIndividualMedicine()));

        Label idNote = new Label("ID: " + m.getIdOfMedicine() + "  (tidak dapat diubah)");
        idNote.setStyle(StyleUtil.label(StyleUtil.FONT_SIZE_XS, StyleUtil.TEXT_MUTED));

        Label errLbl = new Label("");
        errLbl.setStyle("-fx-text-fill: " + StyleUtil.ACCENT_RED + "; -fx-font-size: 12px;");
        errLbl.setVisible(false);

        HBox btnRow = new HBox(10);
        Button cancel = new Button("Batal");
        Button save   = new Button("Simpan Perubahan");
        cancel.setStyle(StyleUtil.btnSecondary());
        save.setStyle(StyleUtil.btnPrimary() + "-fx-padding: 11 20;");
        HBox.setHgrow(save, Priority.ALWAYS);
        save.setMaxWidth(Double.MAX_VALUE);
        cancel.setOnAction(e -> d.close());
        save.setOnAction(e -> {
            try {
                String nm = nameFld.getText().trim();
                int st    = Integer.parseInt(stockFld.getText().trim());
                int pr    = Integer.parseInt(priceFld.getText().trim());
                if (nm.isEmpty()) throw new Exception("Nama tidak boleh kosong.");
                if (st < 0 || pr < 0) throw new Exception("Stok dan harga tidak boleh negatif.");
                ApotekApp.inventoryManager.updateName(m.getIdOfMedicine(), nm);
                ApotekApp.inventoryManager.updateStock(m.getIdOfMedicine(), st);
                ApotekApp.inventoryManager.updatePrice(m.getIdOfMedicine(), pr);
                service.DataBaseHelper.updateMedicineName(m.getIdOfMedicine(), nm);
                service.DataBaseHelper.updatedMedicineStock(m.getIdOfMedicine(), st);
                service.DataBaseHelper.updateMedicinePrice(m.getIdOfMedicine(), pr);
                refreshTable();
                d.close();
            } catch (NumberFormatException ex) {
                errLbl.setText("Stok dan Harga harus berupa angka.");
                errLbl.setVisible(true);
            } catch (Exception ex) {
                errLbl.setText(ex.getMessage());
                errLbl.setVisible(true);
            }
        });
        btnRow.getChildren().addAll(cancel, save);

        box.getChildren().addAll(title, idNote, nameFld, stockFld, priceFld, errLbl, btnRow);
        d.setScene(new Scene(box));
        d.showAndWait();
    }

    private void confirmDelete(Medicine m) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Hapus Obat");
        alert.setHeaderText("Hapus \"" + m.getNameOfMedicine() + "\"?");
        alert.setContentText("Data akan dihapus permanen dari database.");
        alert.showAndWait().ifPresent(r -> {
            if (r == ButtonType.OK) {
                ApotekApp.inventoryManager.removeMedicine(m.getIdOfMedicine());
                service.DataBaseHelper.deleteMedicine(m.getIdOfMedicine());
                refreshTable();
            }
        });
    }

  
    private Stage dialogStage(String title) {
        Stage d = new Stage();
        d.initModality(Modality.APPLICATION_MODAL);
        d.initOwner(stage);
        d.setTitle(title);
        return d;
    }

    private VBox dialogBox() {
        VBox box = new VBox(14);
        box.setPadding(new Insets(28));
        box.setPrefWidth(380);
        box.setStyle("-fx-background-color: " + StyleUtil.BG_SURFACE + ";");
        return box;
    }

    private Label dialogTitle(String text) {
        Label l = new Label(text);
        l.setStyle("-fx-text-fill: " + StyleUtil.TEXT_DARK + ";" +
                   "-fx-font-size: 17px; -fx-font-weight: bold;");
        return l;
    }

    private TextField styledField(String prompt) {
        TextField tf = new TextField();
        tf.setPromptText(prompt);
        tf.setStyle(StyleUtil.inputField() + "-fx-padding: 10 14; -fx-font-size: 13px;");
        return tf;
    }

    private void alert(String title, String msg) {
        Alert a = new Alert(Alert.AlertType.WARNING);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }

    
    public static class MedicineRow {
        private final Medicine medicine;
        private final IntegerProperty id;
        private final StringProperty  name;
        private final IntegerProperty stock;
        private final StringProperty  price;

        public MedicineRow(Medicine m) {
            this.medicine = m;
            this.id    = new SimpleIntegerProperty(m.getIdOfMedicine());
            this.name  = new SimpleStringProperty(m.getNameOfMedicine());
            this.stock = new SimpleIntegerProperty(m.getStockOfMedicine());
            this.price = new SimpleStringProperty(StyleUtil.formatRupiah(m.getPriceOfIndividualMedicine()));
        }
        public Medicine getMedicine()      { return medicine; }
        public IntegerProperty idProperty()    { return id; }
        public StringProperty  nameProperty()  { return name; }
        public IntegerProperty stockProperty() { return stock; }
        public StringProperty  priceProperty() { return price; }
        public int    getId()    { return id.get(); }
        public String getName()  { return name.get(); }
        public int    getStock() { return stock.get(); }
        public String getPrice() { return price.get(); }
    }
}
