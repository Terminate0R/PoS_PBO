package model;
import java.util.ArrayList;

public class Cart{
    private ArrayList<IndividualItemInCart> items;

    public Cart(){
        items = new ArrayList<>();
    }


    //ADD ITEM TO CART
    public void addItemToCart(IndividualItemInCart item){
        boolean merged = false;
        for(IndividualItemInCart existingItem : items){
            if(existingItem.getMedicine().getidOfMedicine() == item.getMedicine().getidOfMedicine()){
                int newQuantity = existingItem.getQuantityOfMedicineBought() + item.getQuantityOfMedicineBought();
                if(!existingItem.getMedicine().isMedicineAvailable(newQuantity)){
                    throw new IllegalArgumentException("Not enough stock for " + existingItem.getMedicine().getNameOfMedicine());
                }
                existingItem.setQuantityOfMedicineBought(newQuantity);
                merged = true;
                break;
            }
        }
        if(!merged){
            if(!item.getMedicine().isMedicineAvailable(item.getQuantityOfMedicineBought())){
                throw new IllegalArgumentException("Not enough stock for " + item.getMedicine().getNameOfMedicine());
            }
            items.add(item);
        }
    } //CHECKED IF THE SAME MEDICINE THEN ADD THEN CHECK THE STOCK AGAIN


    public void removeItemFromCart(IndividualItemInCart item){
        items.remove(item);
    } //REMOVE SPESIFIC ITEM IN CART


    public void clearCart(){
        items.clear();
    } // REMOVE ALL ITEMS IN CART

    public int getTotalPriceOfCart(){
        int total = 0;
        for(IndividualItemInCart item : items){
            total += item.getTotalPriceOfCurrentMedicine();
        }
        return total;
    } //TOTAL PRICE OF CART

    public void displayCart(){
        System.out.println("=========================== CART =========================");
        for(IndividualItemInCart item : items){
            item.displayItemInCart();
        }
        System.out.println("Total Price: " + getTotalPriceOfCart()); // fixed method name
        System.out.println("========================================================");
    } //DISPLAY CART INFORMATION

    public ArrayList<IndividualItemInCart> getItems() {
        return items;
    } //GETTERS FOR CART ITEMS
}