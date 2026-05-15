import model.*;
import service.InventoryManager;
import java.util.Scanner;
import java.util.ArrayList;
public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        boolean running = true;


        ArrayList<Medicine> medicines = new ArrayList<>();
        medicines.add(new Medicine(1, "Paracetamol", 100, 5000));
        medicines.add(new Medicine(2, "Amoxicillin", 50, 12000));
        medicines.add(new Medicine(3, "Ibuprofen", 80, 8000));


        InventoryManager inventoryManager = new InventoryManager(medicines);

        while(running){
            System.out.println("=====================================");
            System.out.println("Selamat datang di Sistem Point of Sale Apotek!        ");
            System.out.println("======================================");

            System.out.println("Pilih opsi:");
            System.out.println("1. Transaksi baru");
            System.out.println("2. Manajemen Stok Obat");
            System.out.println("3. Keluar");
            System.out.print("Masukkan pilihan Anda: ");
            int choice = scanner.nextInt();

            switch(choice){
                case 1:
                    setTransaction(scanner, medicines);
                    break;
                case 2:
                    setInventoryManagement(scanner, inventoryManager);
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

    private static void setInventoryManagement(Scanner scanner, InventoryManager inventoryManager) {
        boolean managing = true;
        while (managing){
            System.out.println("\n================================");
            System.out.println("MANAJEMEN STOK OBAT");
            System.out.println("================================");
            System.out.println("Pilih opsi:");
            System.out.println("1. Tampilkan daftar obat");
            System.out.println("2. Tambah obat baru");
            System.out.println("3. Update obat");
            System.out.println("4. Hapus obat");
            System.out.println("0. Selesai");
            System.out.print("Masukkan pilihan Anda:");
            int choice = scanner.nextInt();
            switch (choice){
                case 1:
                    inventoryManager.displayMedicines();
                    break;
                case 2:
                    System.out.print("Masukkan ID obat: ");
                    int id = scanner.nextInt();
                    scanner.nextLine(); 
                    System.out.print("Masukkan nama obat: ");
                    String name = scanner.nextLine();
                    System.out.print("Masukkan stok obat: ");
                    int stock = scanner.nextInt();
                    System.out.print("Masukkan harga obat: ");
                    int price = scanner.nextInt();
                    inventoryManager.addMedicine(id, name, stock, price);
                    break;
                case 3:
                    System.out.print("Masukkan ID obat yang ingin diupdate: ");
                    int updateId = scanner.nextInt();
                    scanner.nextLine();
                    System.out.print("Masukkan yang ingin diupdate (name/stock/price): ");
                    System.out.println("1. Nama");
                    System.out.println("2. Stok");
                    System.out.println("3. Harga");
                    System.out.print("Masukkan pilihan: ");
                    int updateSet = scanner.nextInt();
                    scanner.nextLine();

                    switch(updateSet){
                        case 1:
                            System.out.print("Masukkan nama baru: ");
                            String newName = scanner.nextLine();
                            inventoryManager.updateName(updateId, newName);
                            break;
                        case 2:
                            System.out.print("Masukkan stok baru: ");
                            int newStock = scanner.nextInt();
                            inventoryManager.updateStock(updateId, newStock);
                            break;
                        case 3:
                            System.out.print("Masukkan harga baru: ");
                            int newPrice = scanner.nextInt();
                            scanner.nextLine();
                            inventoryManager.updatePrice(updateId, newPrice);
                            break;
                        default:
                            System.out.println("Pilihan tidak valid. Silakan coba lagi.");
                        }
                    break;
                case 4:
                    System.out.print("Masukkan ID obat yang ingin dihapus: ");
                    int removeId = scanner.nextInt();
                    inventoryManager.removeMedicine(removeId);
                    break;
                case 0:
                    managing = false;
                    break;
                default:
                    System.out.println("Pilihan tidak valid. Silakan coba lagi.");           
            }
        }
    }

    private static void setTransaction(Scanner scanner, ArrayList<Medicine> medicines) {
        // Medicine paracetamol = new Medicine(1, "Paracetamol", 100, 5000);
        // Medicine amoxicillin = new Medicine(2, "Amoxicillin", 50,  12000);
        // Medicine ibuprofen   = new Medicine(3, "Ibuprofen",   80,  8000);

        Cart cart = new Cart();
        boolean addingItems = true;
        while(addingItems){
            System.out.println("Masukkan ID obat:");
            for(Medicine medicine : medicines){
                System.out.println(medicine.getIdOfMedicine() + ". " + medicine.getNameOfMedicine() + " (Stock: " + medicine.getStockOfMedicine() + ", Harga: " + medicine.getPriceOfIndividualMedicine() + ")");
            }
            System.out.println("0. Selesai menambahkan obat");
            System.out.print("Masukkan pilihan: ");
            int medicineId = scanner.nextInt();
            if(medicineId == 0){
                addingItems = false;
                continue;
            }
            Medicine selectedMedicine = null;
            for(Medicine medicine : medicines){
                if(medicine.getIdOfMedicine() == medicineId){
                    selectedMedicine = medicine;
                    break;
                }
            }
            if(selectedMedicine == null){
                System.out.println("ID obat tidak valid. Silakan coba lagi.");
                continue;
            }
            System.out.print("Masukkan jumlah obat: ");
            int quantity = scanner.nextInt();
            if(!selectedMedicine.isMedicineAvailable(quantity)){
                System.out.println("Stok obat tidak mencukupi. Silakan coba lagi.");
                continue;
            }
            cart.addItemToCart(new IndividualItemInCart(selectedMedicine, quantity));
            System.out.println("Obat berhasil ditambahkan ke keranjang.");
        }
        if(cart.getItems().isEmpty()){
            System.out.println("Keranjang kosong! Transaksi dibatalkan.");
            return;
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
        System.out.println("\n\n");
        receipt.printReceipt();
        System.out.println("\n\n");
    }
    

}