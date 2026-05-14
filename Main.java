import java.util.ArrayList;
public class Main {
    public static void main(String[] args) {

        // Masukkan obat
        Medicine paracetamol = new Medicine(1, "Paracetamol", 100, 5000);
        Medicine amoxicillin = new Medicine(2, "Amoxicillin", 50,  12000);
        Medicine ibuprofen   = new Medicine(3, "Ibuprofen",   80,  8000);
        
        // Buat sebuah sistem keranjang
        Cart cart = new Cart();
        cart.addItemToCart(new IndividualItemInCart(paracetamol, 3));
        cart.addItemToCart(new IndividualItemInCart(amoxicillin, 2));
        cart.addItemToCart(new IndividualItemInCart(ibuprofen,   1));

        // Munculkan kerajang belanja
        // cart.displayCart();

        // Jalankan transaksi
        Transaction transaction = new Transaction(cart, "Cash");

        // Buat struk
        Receipt receipt = new Receipt(
            transaction.getTransactionId(),
            new ArrayList<>(cart.getItems()), 
            cart.getTotalPriceOfCart(),
            transaction.getPaymentMethod()
        );

        // Proses transaksi
        transaction.processTransaction();

        // Print struk
        receipt.printReceipt();
    }
}