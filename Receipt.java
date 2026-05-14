import java.util.List;


//Receipt class for printing receipts
public class Receipt{
    private String transactionId;
    private List<IndividualItemInCart> listOfItems;
    private int totalPrice;
    private String paymentMethod;

    public Receipt(String transactionId, List<IndividualItemInCart> listOfItems, int totalPrice, String paymentMethod) {
        this.transactionId = transactionId;
        this.listOfItems = listOfItems;
        this.totalPrice = totalPrice;
        this.paymentMethod = paymentMethod;
    }

    public void printReceipt(){
        System.out.println("============================== STRUK BELANJA ==============================");
        System.out.println("Transaction ID: " + transactionId);
        System.out.println("Payment Method: " + paymentMethod);
        System.out.println("--------------------------------------------------------------");
        System.out.println("Items Purchased:");
        for(IndividualItemInCart item : listOfItems){
           item.displayItemInCart();
        }
        System.out.println("--------------------------------------------------------------");
        System.out.println("Total Price: " + totalPrice);
    }







}