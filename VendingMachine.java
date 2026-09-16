//core classes and entities
/* 1. Item
2. ItemShelf
3. Inventory
4. Vending Machine
5. Payment Strategy
6. enum Coins
7. enum ItemType
8. Machine State
 */

import java.util.*;

//enum coins
enum Coins{
    ONE(1),
    TWO(2),
    FIVE(5),
    TEN(10);

    private int val;

    Coins(int val){
        this.val=val;
    }
    public int getVal(){
        return val;
    }
}

//enum ItemType
enum ItemType{
    COLD_DRINK,SNACK,CHOCOLATE,OTHER
}

//item class

class Item{
    private  String id;
    private ItemType type;
    private double price;
    private String name;

    public Item(String id,ItemType type,double price,String name){
        this.id=id;
        this.type=type;
        this.price=price;
        this.name=name;

    }
    public String getName(){
        return name;
    }
    public String getId(){
        return id;
    }
    public ItemType getType(){
        return type;
    }
    public double getPrice(){
        return price;
    }

}

//item shelf class
public class ItemShelf{
    private int code;
    private Item item;
    private int quantity;

    public ItemShelf(int code,Item item,int quantity){
        this.code=code;
        this.item=item;
        this.quantity=quantity;
    }
    public int getCode(){
        return code;
    }
    public Item getItem(){
        return item;
    }
    public int getQuantity(){
        return quantity;
    }
    public void decQuantity(){
        quantity--;

    }
    public void incQuantity(int amount){
        quantity+=amount;
    }

}

//inventory class
public class Inventory{
    Map<Integer,ItemShelf> map=new HashMap<>();
    public void addShelf(ItemShelf shelf){
        map.put(shelf.getCode(),shelf);
    }
    public ItemShelf getShelf(int code){
        return map.get(code);
    }
    public void restockShelf(int code,int quantity){
        ItemShelf shelf=map.get(code);
        if(shelf!=null){
            shelf.incQuantity(quantity);
        }
    }
}

//payment strategy
interface PaymentStrategy{
    void collectPayment(List<Coins> coins);
    double getAmount();
    void reset();
}

//concrete class
public class CoinPayment implements PaymentStrategy{
    private double amount=0;
    @Override
    public void collectPayment(List<Coins> coins){
        for(Coins coin:coins){
            amount+=coin.getVal();
        }
    }
    @Override
    public double getAmount(){
        return amount;
    }

    @Override
    public void reset(){
        amount=0;
    }
}

//state pattern usage
//state interface
interface State{
    void insertCoin(List<Coins> coins);
    void selectProduct(int code);
    void dispense();
    void refund();
    void cancel();
    String getStateName();
}

// VendingMachine class
public class VendingMachine{
    private  Inventory inventory;
    private State state;
    private PaymentStrategy strategy;

    private ItemShelf selectedshelf;

    public VendingMachine(){
        inventory=new Inventory();
        state=new IdleState(this);
        strategy=new CoinPayment();
    }

    //state
    public void setState(State state){
        this.state=state;
        System.out.println("State-->" + state.getStateName());
    }
    public State getState(){
        return state;
    }

    //inventory
    public Inventory getInventory(){
        return inventory;
    }
    //payment starategy
    public PaymentStrategy getStrategy(){
        return strategy;
    }
    //ItemShelf
    public void setSelectedShelf(ItemShelf shelf){
        this.selectedshelf=shelf;
    }
    public ItemShelf getSelectedShelf(){
        return selectedshelf;
    }

    //user operations
    public void insertCoins(List<Coins> coins){
        state.insertCoin(coins);
    }
    public void selectProduct(int code){
        state.selectProduct(code);
    }
    public void dispense() {
        state.dispense();
    }


    public void cancel() {
        state.cancel();
    }


    public void refund() {
        state.refund();
    }


}

//idle state
public class IdleState implements State{
    private VendingMachine machine;
    public IdleState(VendingMachine machine){
        this.machine=machine;
    }
    @Override
    public void insertCoin(List<Coins> coin){
        machine.getStrategy().collectPayment(coin);
        System.out.println("Money inserted"+ machine.getStrategy().getAmount());
        machine.setState(new HasMoneyState(machine));
    }
    @Override
    public void selectProduct(int code){
        System.out.println("Please insert money First");
    }
    @Override
    public void dispense(){
        System.out.println("Please insert money First");

    }
    @Override
    public void cancel(){
        System.out.println("Nothing to cancel");

    }
    @Override
    public void refund(){
        System.out.println("Please insert money First");

    }
    @Override
    public String getStateName(){
        return "IDLE";
    }
}

//has money state
public class HashMoneyState implements State{
    private VendingMachine machine;
    public HasMoneyState(VendingMachine machine){
        this.machine=machine;
    }
    @Override
    public void insertCoin(List<Coins> coin){
        machine.getStrategy().collectPayment(coin);
        System.out.println("Total money"+ machine.getStrategy().getAmount());
        
    }
    @Override
    public void selectProduct(int code){
        ItemShelf shelf=machine.getInventory().getShelf(code);
        if(shelf==null){
            System.out.println("Invalid code");
            return;

        }
        if (shelf.getQuantity() <= 0) {

            System.out.println(
                    "Product is out of stock."
            );

            return;
        }


        // Check money
        double money =
                machine.getStrategy().getAmount();

        double price =
                shelf.getItem().getPrice();


        if (money < price) {

            System.out.println(
                    "Insufficient money."
            );

            return;
        }


        // Product selected
        machine.setSelectedShelf(shelf);

        System.out.println(
                "Selected: " +
                shelf.getItem().getName()
        );


        machine.setState(
                new SelectionState(machine)
        );

    }
    @Override
    public void dispense() {

        System.out.println(
                "Please select a product first."
        );
    }


    @Override
    public void cancel() {

        refund();

        machine.setState(
                new IdleState(machine)
        );
    }


    @Override
    public void refund() {

        double amount =
                machine.getPaymentStrategy().getAmount();

        System.out.println(
                "Refunded: ₹" + amount
        );

        machine.getPaymentStrategy().reset();
    }


    @Override
    public String getStateName() {
        return "HAS MONEY";
    }
}


// ======================================================
// SELECTION STATE
// ======================================================

class SelectionState implements State {

    private VendingMachine machine;


    public SelectionState(VendingMachine machine) {
        this.machine = machine;
    }


    @Override
    public void insertCoin(List<Coin> coins) {

        System.out.println(
                "Product already selected."
        );
    }


    @Override
    public void selectProduct(int code) {

        System.out.println(
                "Product already selected."
        );
    }


    @Override
    public void dispense() {

        ItemShelf shelf =
                machine.getSelectedShelf();

        Item item =
                shelf.getItem();


        // Remove item
        shelf.decQuantity();


        // Calculate change
        double paid =
                machine.getStrategy().getAmount();

        double change =
                paid - item.getPrice();


        System.out.println(
                "Dispensing: " +
                item.getName()
        );


        if (change > 0) {

            System.out.println(
                    "Change returned: ₹" +
                    change
            );
        }


        // Reset payment
        machine.getStrategy().reset();

        // Reset selection
        machine.setSelectedShelf(null);


        // Back to idle
        machine.setState(
                new IdleState(machine)
        );
    }


    @Override
    public void cancel() {

        refund();

        machine.setSelectedShelf(null);

        machine.setState(
                new IdleState(machine)
        );
    }


    @Override
    public void refund() {

        double amount =
                machine.getStrategy().getAmount();

        System.out.println(
                "Refunded: ₹" + amount
        );

        machine.getStrategy().reset();
    }


    @Override
    public String getStateName() {
        return "SELECTION";
    }
}


// ======================================================
// MAIN
// ======================================================

public class Main {

    public static void main(String[] args) {

        VendingMachine machine =
                new VendingMachine();


        // Create items
        Item coke = new Item(
                "1",
                "Coke",
                25,
                ItemType.COLD_DRINK
        );


        Item chips = new Item(
                "2",
                "Lays",
                20,
                ItemType.SNACK
        );


        Item chocolate = new Item(
                "3",
                "Dairy Milk",
                30,
                ItemType.CHOCOLATE
        );


        // Add shelves
        machine.getInventory().addShelf(
                new ItemShelf(1, coke, 5)
        );


        machine.getInventory().addShelf(
                new ItemShelf(2, chips, 3)
        );


        machine.getInventory().addShelf(
                new ItemShelf(3, chocolate, 2)
        );


        // User inserts ₹30
        machine.insertCoins(
                Arrays.asList(
                        Coin.TEN,
                        Coin.TEN,
                        Coin.TEN
                )
        );


        // Select Coke
        machine.selectProduct(1);


        // Dispense
        machine.dispense();
    }
}
