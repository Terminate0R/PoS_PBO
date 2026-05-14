import model.*;
import java.util.Scanner;

import model.Medicine;
public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        while(running){
            System.out.println("=====================================");
            System.out.println("        Selamat datang di Sistem Point of Sale Apotek!        ");
            System.out.println("======================================");

            System.out.println("Pilih opsi:");
            System.out.println("1. Transaksi baru");
            System.out.println("2. Manajemen Stok Obat");
            System.out.println("3. Keluar");
            System.out.print("Masukkan pilihan Anda: ");
            int choice = scanner.nextInt();

            switch(choice){
                case 1:
                    System.out.println("TRANSAKSI!");
                    //Nanti lanjut masukin sistem sini
                    setTransaction(scanner);
                    break;
                case 2:
                    System.out.println("MANAJEMEN STOK OBAT!");
                    //Nanti lanjut masukin sistem sini
                    break;
                case 3:
                    running = false;
                    System.out.println("Sistem Selesai!");
                    break;
                default:
                    System.out.println("Pilihan tidak valid. Silakan coba lagi.");
            }
        }
        scanner.close();
    }


    private static void setTransaction(Scanner scanner) {
        Medicine paracetamol = new Medicine(1, "Paracetamol", 100, 5000);
        Medicine amoxicillin = new Medicine(2, "Amoxicillin", 50,  12000);
        Medicine ibuprofen   = new Medicine(3, "Ibuprofen",   80,  8000);

        Cart cart = new Cart();
        boolean addingItems = true;
        while(addingItems){
            System.out.print("Masukkan Obat yang ingin dibeli (1. Paracetamol, 2. Amoxicillin, 3. Ibuprofen, 0 untuk selesai): ");
            int medicineId = scanner.nextInt();
            if(medicineId == 0){
                addingItems = false;
                continue;
            }
            System.out.print("Masukkan jumlah yang ingin dibeli: ");
            int quantity = scanner.nextInt();
            switch (medicineId){
                case 1:
                    cart.addItemToCart(new IndividualItemInCart(paracetamol, quantity));
                    break;
                case 2:
                    cart.addItemToCart(new IndividualItemInCart(amoxicillin, quantity));
                    break;
                case 3:
                    cart.addItemToCart(new IndividualItemInCart(ibuprofen, quantity));
                    break;
                default:
                    System.out.println("ID Obat tidak valid. Silakan coba lagi.");
            }
        }
        cart.displayCart();
        int amountPaid = 0;
        int totalPrice = cart.getTotalPriceOfCart();
        while(amountPaid < totalPrice){
            System.out.print("Masukkan jumlah yang dibayar: ");
            amountPaid = scanner.nextInt();
            if(amountPaid < totalPrice){
                System.out.println("Jumlah yang dibayar kurang dari total harga. Silakan coba lagi.");
            }
        }

        Transaction transaction = new Transaction(cart, "Cash");
        transaction.setAmountPaid(amountPaid);
        transaction.processTransaction();

        Receipt receipt = new Receipt(
                transaction.getTransactionId(),
                transaction.getItemSaves(),
                transaction.getTotalPrice(),
                transaction.getPaymentMethod(),
                transaction.getPaidAmount(),
                transaction.getChange()
        );
        receipt.printReceipt();
    }

}