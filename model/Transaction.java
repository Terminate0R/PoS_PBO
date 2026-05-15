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
    private int paidAmount;

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
    public int getChange(int paidAmount){
        if(!isProcessed) throw new IllegalStateException("Transaction not processed yet.");
        if(paidAmount < totalPrice) throw new IllegalArgumentException("Paid amount is less than total price.");
        this.paidAmount = paidAmount;
        return paidAmount - totalPrice;
    }

    public List<IndividualItemInCart> getItemSaves() {
        if(!isProcessed) throw new IllegalStateException("Transaction not processed yet.");
        return itemSaves;
    }

    public void setAmountPaid(int paidAmount){
        if(paidAmount < 0) throw new IllegalArgumentException("Paid amount cannot be negative.");
        this.paidAmount = paidAmount;
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
    public int getPaidAmount() {
        if(!isProcessed) throw new IllegalStateException("Transaction not processed yet.");
        return paidAmount;
    }
    public int getChange() {
        if(!isProcessed) throw new IllegalStateException("Transaction not processed yet.");
        return paidAmount - totalPrice;
    }
}