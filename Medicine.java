public class Medicine{
    private String nameOfMedicine; // name of the medicine (should be full name)
    private final int idOfMedicine; //id will be constant
    private int stockOfMedicine; //stock should change after transaction or after stock update
    private int priceOfIndividualMedicine; // price (idk what you want me to say)

    public Medicine(int id, String name, int stock, int price){
        this.idOfMedicine = id;
        this.nameOfMedicine = name;
        this.stockOfMedicine = stock;
        this.priceOfIndividualMedicine = price;
    } //medicine constructor

    //Getters
    public int getidOfMedicine(){
        return idOfMedicine;
    } //Getters for medicine id
    public String getNameOfMedicine(){
        return nameOfMedicine;
    } ///Getters for medicine name
    public int getStockOfMedicine(){
        return stockOfMedicine;
    } //Getters for medicine stock
    public int getPriceOfIndividualMedicine(){
        return priceOfIndividualMedicine;
    } //Getters for medicine price
    //Setters
    public void setNameOfMedicine(String name){
        this.nameOfMedicine = name;
    } //Setters for updating medicine information
    public void setStockOfMedicine(int stock){
        this.stockOfMedicine = stock;
    } //Setters for updating medicine information
    public void setPriceOfMedicine(int price){
        this.priceOfIndividualMedicine = price;
    } //Setters for updating medicine information

    public void addCurrentStock(int stock){
        this.stockOfMedicine += stock;
    } // add stock after stock update

    public void reduceCurrentStock(int requiredQuantityMedicine){
        if(isMedicineAvailable(requiredQuantityMedicine)){
            this.stockOfMedicine -= requiredQuantityMedicine;
        } else {
            System.out.println("Insufficient stock of medicine.");
        }
    } //reduce stock after transaction

    //Validation of available stock of medicine
    public boolean isMedicineAvailable(int requiredQuantityMedicine){
        return requiredQuantityMedicine > 0 && requiredQuantityMedicine <= stockOfMedicine;
    }
    //Information display
    public void displayMedicineInformation(){
        System.out.println("ID: " + idOfMedicine);
        System.out.println("Name: " + nameOfMedicine);
        System.out.println("Stock: " + stockOfMedicine);
        System.out.println("Price: " + priceOfIndividualMedicine);
    }
}