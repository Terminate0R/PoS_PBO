import model.*;
import java.sql.*;
import java.util.ArrayList;

public class DataBaseHelper{
    private static final String URL = "jdbc:mysql://localhost:3306/pos_pbo";
    private static final String USER = "root";
    private static String PASSWORD = "";
    public static void setPassword(String Password){
        PASSWORD = Password;
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


    public static void saveTransaction(Transaction transaction){
        String insertTx = "INSERT INTO transaction (transaction_id, payment_method, total_price, amount_paid, change_amount) VALUES (?, ?, ?, ?, ?)";
        String insertItem = "INSERT INTO transaction_items (transaction_id, medicine_id, quantity, subtotal) VALUES (?, ?, ?, ?)";
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
                    itemStmt.setInt(3, item.getQuantityOfMedicineBought());
                    itemStmt.setInt(4, item.getTotalPriceOfCurrentMedicine());
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
    
    public static void deleteMedicine(int id){
        String query = "DELETE FROM medicine where id = ?";
        try (Connection conn = getConnection();
            PreparedStatement stmt = conn.prepareStatement(query)){
                stmt.setInt(1,id);
                stmt.executeUpdate();
            } catch (SQLException e){
                System.out.println("Error deleting medicine: " + e.getMessage());
            }
    }
}