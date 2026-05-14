package model;
import java.util.UUID;
import java.util.List;
import java.util.ArrayList;
public class Transaction {
    private Cart cart;
    private int totalPrice;
    private String transactionId;
    private String paymentMethod;
    private boolean isProcessed = false;
    private List<IndividualItemInCart> itemSaves;

    public Transaction(Cart cart, String paymentMethod) {
        this.cart = cart;
        this.paymentMethod = paymentMethod;
        this.transactionId = generateTransactionId();
    }

    private String generateTransactionId() {
        return UUID.randomUUID().toString();
    }

    public void processTransaction() {
        if (isProcessed) throw new IllegalStateException("Transaction already processed.");

        for (IndividualItemInCart item : cart.getItems()) {
            Medicine medicine = item.getMedicine();
            int boughtQuantity = item.getQuantityOfMedicineBought();

            if (medicine.getStockOfMedicine() < boughtQuantity) { // fixed method name
                throw new IllegalStateException(
                    "Insufficient stock for: " + medicine.getNameOfMedicine() + // fixed method name
                    ". Available: " + medicine.getStockOfMedicine() +
                    ", Requested: " + boughtQuantity
                );
            }
        }

        isProcessed = true;
        totalPrice = cart.getTotalPriceOfCart();
        itemSaves = new ArrayList<>(cart.getItems());
        for (IndividualItemInCart item : cart.getItems()) {
            item.getMedicine().reduceCurrentStock(item.getQuantityOfMedicineBought());
        }
        cart.clearCart();
    }


    public List<IndividualItemInCart> getItemSaves() {
        if(!isProcessed) throw new IllegalStateException("Transaction not processed yet.");
        return itemSaves;
    }

    public String getPaymentMethod() { 
        return paymentMethod;
    }
    public int getTotalPrice() { 
        return totalPrice;
    }
    public String getTransactionId() {
         return transactionId;
    }
}