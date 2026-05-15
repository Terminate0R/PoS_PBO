package service;
import model.Medicine;
import java.util.ArrayList;

public class InventoryManager {
    
    private ArrayList<Medicine> medicines;
    public InventoryManager(ArrayList<Medicine> medicines){
        this.medicines = medicines;
    }

    public void displayMedicines(){
        System.out.println("=========================== DAFTAR OBAT =========================");
        for(Medicine medicine : medicines){
            System.out.println(medicine.getIdOfMedicine() + ". " + medicine.getNameOfMedicine() + " (Stock: " + medicine.getStockOfMedicine() + ", Harga: " + medicine.getPriceOfIndividualMedicine() + ")");
        }
        System.out.println("=================================================================");
    }

    public void addMedicine(int id, String name, int stock, int price){
        for(Medicine m : medicines){
            if(m.getIdOfMedicine() == id){
                System.out.println("Medicine with ID: " + id + " already exists. Please use a different ID.");
                return;
            }
        }
         medicines.add(new Medicine(id, name, stock, price));
         System.out.println("Medicine added successfully.");
    }
    
    public void updateName(int id, String newName){
        for(Medicine m : medicines){
            if(m.getIdOfMedicine() == id){
                m.setNameOfMedicine(newName);
                System.out.println("Medicine name updated successfully.");
                return;
            }
        }
        System.out.println("Medicine with ID: " + id + " not found.");
    }

    public void updateStock(int id, int newStock){
        for(Medicine m : medicines){
            if(m.getIdOfMedicine() == id){
                m.setStockOfMedicine(newStock);
                System.out.println("Medicine stock updated successfully.");
                return;
            }
        }
        System.out.println("Medicine with ID: " + id + " not found.");
    }

    public void updatePrice(int id, int newPrice){
        for(Medicine m : medicines){
            if(m.getIdOfMedicine() == id){
                m.setPriceOfMedicine(newPrice);
                System.out.println("Medicine price updated successfully.");
                return;
            }
        }
        System.out.println("Medicine with ID: " + id + " not found.");
    }

    public void removeMedicine(int id){
        medicines.removeIf(m -> m.getIdOfMedicine() == id);
        System.out.println("Medicine removed successfully.");
        }

}
        



