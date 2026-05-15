import model.*;
import java.sql.*;
import java.util.ArrayList;

public class DataBaseHelper{
    private static final String URL = "jdbc:mysql://localhost:3306/pos_apotek";
    private static final String USER = "root";
    private static final String PASSWORD = "password";


    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static ArrayList<Medicine> loadMedicines(){
        ArrayList<medicine> medicines = new ArrayList<>();
        String query = "SELECT * FROM medicines";
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
        String insertTx = "INSERT INTO transactions (transaction_id, payment_method, total_amount, transaction_date, customer_id) VALUES (?, ?, ?, ?, ?)";
        String insertItem = "INSERT INTO transaction_items (transaction_id, medicine_id, quantity, subtotal) VALUES (?, ?, ?, ?)";
        try (Connection conn = getConnection(){
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
        String query = "INSERT INTO medicines (id, name, stock, price) VALUES (?, ?, ?, ?)";
        
    }


}