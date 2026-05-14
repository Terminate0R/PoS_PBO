public class IndividualItemInCart{
    private Medicine medicine;
    private int quantityOfMedicineBought;

    public IndividualItemInCart(Medicine medicine, int quantity){
        this.medicine = medicine;
        if(!medicine.isMedicineAvailable(quantity)){
        throw new IllegalArgumentException("Not enough stock or illegal quantity " + medicine.getNameOfMedicine());
        }
        this.quantityOfMedicineBought = quantity;
    }

    //Getters
    public Medicine getMedicine(){
        return medicine;
    }
    public int getQuantityOfMedicineBought(){
        return quantityOfMedicineBought;
    }

    //Setters
    public void setQuantityOfMedicineBought(int quantity){
        if(!medicine.isMedicineAvailable(quantity)){
            throw new IllegalArgumentException("Not enough stock or illegal quantity " + medicine.getNameOfMedicine());
        }
        this.quantityOfMedicineBought = quantity;
    }

    //Total of current medicine
    public int getTotalPriceOfCurrentMedicine(){
        return medicine.getPriceOfIndividualMedicine() * quantityOfMedicineBought;
    }


    //Display information of current class
    public void displayItemInCart(){
        System.out.println("Medicine: " + medicine.getNameOfMedicine());
        System.out.println("Quantity: " + quantityOfMedicineBought);
        System.out.println("Total Price: " + getTotalPriceOfCurrentMedicine());
    }







}