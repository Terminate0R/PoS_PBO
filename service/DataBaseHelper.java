package service;
import model.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class DataBaseHelper{
    private static final String URL = "jdbc:mysql://localhost:3306/pos_pbo";
    private static final String USER = "root";
    private static String PASSWORD = "";
    public static void setPassword(String Password){
        PASSWORD = Password;
    }

    public static boolean login(String username, String password){
        String query = "SELECT * FROM users WHERE username = ? AND password = ?";
        try (Connection conn = getConnection();
            PreparedStatement stmt = conn.prepareStatement(query)){
                stmt.setString(1, username);
                stmt.setString(2, password);
                ResultSet rs = stmt.executeQuery();
                return rs.next();
            } catch (SQLException e){
                System.out.println("Error during login: " + e.getMessage());
                return false;
            }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static ArrayList<Medicine> loadMedicines(){
        ArrayList<Medicine> medicines = new ArrayList<>();
        String query = "SELECT * FROM medicine";
        try (Connection conn = getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query)){
                while (rs.next()){
                    medicines.add(new Medicine(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getInt("stock"),
                        rs.getInt("price")
                    ));
                }
            } catch (SQLException e){
                System.out.println("Error loading medicines: " + e.getMessage());
            }
            return medicines;
    }

    // ── Transaction history ──────────────────────────────────────────────────
    /**
     * Loads all transactions with their items from the DB.
     * Uses medicine_name_snapshot so deleted medicines still show their name.
     */
    public static ArrayList<TransactionRecord> loadTransactions(){
        ArrayList<TransactionRecord> records = new ArrayList<>();

        String txQuery = "SELECT transaction_id, payment_method, total_price, amount_paid, change_amount, created_at " +
                         "FROM `transaction` ORDER BY created_at DESC";
        String itemQuery = "SELECT medicine_id, medicine_name_snapshot, quantity, subtotal " +
                           "FROM transaction_items WHERE transaction_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement txStmt = conn.prepareStatement(txQuery);
             ResultSet txRs = txStmt.executeQuery()){

            while (txRs.next()){
                String txId       = txRs.getString("transaction_id");
                String method     = txRs.getString("payment_method");
                int    total      = txRs.getInt("total_price");
                int    paid       = txRs.getInt("amount_paid");
                int    change     = txRs.getInt("change_amount");
                String createdAt  = txRs.getString("created_at");

                ArrayList<TransactionRecord.ItemSnapshot> items = new ArrayList<>();
                try (PreparedStatement itemStmt = conn.prepareStatement(itemQuery)){
                    itemStmt.setString(1, txId);
                    ResultSet itemRs = itemStmt.executeQuery();
                    while (itemRs.next()){
                        // Use snapshot name; fall back gracefully if somehow null
                        String name  = itemRs.getString("medicine_name_snapshot");
                        if (name == null) name = "(Obat dihapus)";
                        int qty      = itemRs.getInt("quantity");
                        int subtotal = itemRs.getInt("subtotal");
                        items.add(new TransactionRecord.ItemSnapshot(name, qty, subtotal));
                    }
                }
                records.add(new TransactionRecord(txId, method, total, paid, change, createdAt, items));
            }
        } catch (SQLException e){
            System.out.println("Error loading transactions: " + e.getMessage());
        }
        return records;
    }

    public static void saveTransaction(Transaction transaction){
        // Saves medicine_name_snapshot alongside medicine_id so history
        // survives future medicine deletions.
        String insertTx   = "INSERT INTO `transaction` (transaction_id, payment_method, total_price, amount_paid, change_amount) VALUES (?, ?, ?, ?, ?)";
        String insertItem = "INSERT INTO transaction_items (transaction_id, medicine_id, medicine_name_snapshot, quantity, subtotal) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = getConnection()){
            conn.setAutoCommit(false);
            try (PreparedStatement txStmt = conn.prepareStatement(insertTx)){
                txStmt.setString(1, transaction.getTransactionId());
                txStmt.setString(2, transaction.getPaymentMethod());
                txStmt.setInt(3, transaction.getTotalPrice());
                txStmt.setInt(4, transaction.getPaidAmount());
                txStmt.setInt(5, transaction.getChange());
                txStmt.executeUpdate();
            }
            try (PreparedStatement itemStmt = conn.prepareStatement(insertItem)){
                for (IndividualItemInCart item : transaction.getItemSaves()){
                    itemStmt.setString(1, transaction.getTransactionId());
                    itemStmt.setInt(2, item.getMedicine().getIdOfMedicine());
                    itemStmt.setString(3, item.getMedicine().getNameOfMedicine()); // snapshot
                    itemStmt.setInt(4, item.getQuantityOfMedicineBought());
                    itemStmt.setInt(5, item.getTotalPriceOfCurrentMedicine());
                    itemStmt.addBatch();
                }
                itemStmt.executeBatch();
            }
            conn.commit();
            System.out.println("Transaksi berhasil disimpan!");
        } catch (SQLException e){
            System.out.println("Error saving transaction: " + e.getMessage());
        }
    }

    public static void saveMedicine(Medicine medicine){
        String query = "INSERT INTO medicine (id, name, stock, price) VALUES (?, ?, ?, ?)";
        try (Connection conn = getConnection();
            PreparedStatement stmt = conn.prepareStatement(query)){
                stmt.setInt(1, medicine.getIdOfMedicine());
                stmt.setString(2, medicine.getNameOfMedicine());
                stmt.setInt(3, medicine.getStockOfMedicine());
                stmt.setInt(4, medicine.getPriceOfIndividualMedicine());
                stmt.executeUpdate();
                System.out.println("Medicine saved successfully.");
            } catch (SQLException e){
                System.out.println("Error saving medicine: " + e.getMessage());
            }
    }

    public static void updatedMedicineStock(int id, int newStock){
        String query = "UPDATE medicine SET stock = ? WHERE id = ?";
        try (Connection conn = getConnection();
            PreparedStatement stmt = conn.prepareStatement(query)){
                stmt.setInt(1, newStock);
                stmt.setInt(2, id);
                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected > 0){
                    System.out.println("Medicine stock updated successfully.");
                } else {
                    System.out.println("Medicine with ID: " + id + " not found.");
                }
            } catch (SQLException e){
                System.out.println("Error updating medicine stock: " + e.getMessage());
            }
    }

    public static void updateMedicineName(int id, String newName){
        String query = "UPDATE medicine SET name = ? WHERE id = ?";
        try (Connection conn = getConnection();
            PreparedStatement stmt = conn.prepareStatement(query)){
                stmt.setString(1, newName);
                stmt.setInt(2, id);
                stmt.executeUpdate();
            } catch (SQLException e){
                System.out.println("Error updating medicine name: " + e.getMessage());
            }
    }

    public static void updateMedicinePrice(int id, int newPrice){
        String query = "UPDATE medicine SET price = ? WHERE id = ?";
        try (Connection conn = getConnection();
            PreparedStatement stmt = conn.prepareStatement(query)){
                stmt.setInt(1, newPrice);
                stmt.setInt(2, id);
                stmt.executeUpdate();
            } catch (SQLException e){
                System.out.println("Error updating medicine price: " + e.getMessage());
            }
    }
    
    /**
     * Deletes a medicine. Because transaction_items uses ON DELETE SET NULL,
     * existing transaction rows keep their medicine_name_snapshot intact.
     */
    public static void deleteMedicine(int id){
        String query = "DELETE FROM medicine WHERE id = ?";
        try (Connection conn = getConnection();
            PreparedStatement stmt = conn.prepareStatement(query)){
                stmt.setInt(1, id);
                stmt.executeUpdate();
            } catch (SQLException e){
                System.out.println("Error deleting medicine: " + e.getMessage());
            }
    }
}
