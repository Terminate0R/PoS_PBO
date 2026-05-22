package model;

import java.util.ArrayList;

/**
 * Read-only snapshot of a completed transaction loaded from the DB.
 * Carries medicine_name_snapshot so names survive medicine deletions.
 */
public class TransactionRecord {

    public static class ItemSnapshot {
        private final String medicineName;
        private final int    quantity;
        private final int    subtotal;

        public ItemSnapshot(String medicineName, int quantity, int subtotal){
            this.medicineName = medicineName;
            this.quantity     = quantity;
            this.subtotal     = subtotal;
        }

        public String getMedicineName(){ return medicineName; }
        public int    getQuantity()    { return quantity; }
        public int    getSubtotal()    { return subtotal; }
    }

    private final String                  transactionId;
    private final String                  paymentMethod;
    private final int                     totalPrice;
    private final int                     amountPaid;
    private final int                     changeAmount;
    private final String                  createdAt;
    private final ArrayList<ItemSnapshot> items;

    public TransactionRecord(String transactionId, String paymentMethod,
                              int totalPrice, int amountPaid, int changeAmount,
                              String createdAt, ArrayList<ItemSnapshot> items){
        this.transactionId = transactionId;
        this.paymentMethod = paymentMethod;
        this.totalPrice    = totalPrice;
        this.amountPaid    = amountPaid;
        this.changeAmount  = changeAmount;
        this.createdAt     = createdAt;
        this.items         = items;
    }

    public String                  getTransactionId(){ return transactionId; }
    public String                  getPaymentMethod(){ return paymentMethod; }
    public int                     getTotalPrice()   { return totalPrice; }
    public int                     getAmountPaid()   { return amountPaid; }
    public int                     getChangeAmount() { return changeAmount; }
    public String                  getCreatedAt()    { return createdAt; }
    public ArrayList<ItemSnapshot> getItems()        { return items; }
}
