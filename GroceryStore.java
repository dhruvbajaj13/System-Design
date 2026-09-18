/*Requirements
Admin products add/update/remove kar sake.
Customer products browse kar sake.
Customer cart mein products add/remove kar sake.
Stock availability check ho.
Checkout par total calculate ho.
Payment process ho.
Order place hone par stock update ho.*/

//Classes and entities
/*1.Product
2.Inventory
3.Cart
4.CartItem
5.Customer
6.Order
7.Payment
8. GroceryStore*/

import java.util.*;
//class Product
public class Product{
    private int id;
    private String name;
    private double price;
    private int stock;

    public Product(int id,String name,double price,int stock){
        this.id=id;
        this.name=name;
        this.price=price;
        this.stock=stock;
    }
    public int getId(){
        return id;
    }
    public String getName(){
        return name;
    }
    public double getPrice(){
        return price;
    }
    public int getStock(){
        return stock;
    }
    public void reduceStock(int quantity) {
        stock -= quantity;
    }

}
//class CartItem
public class CartItem{
    private Product product;
    private int quantity;

    public CartItem(Product prod,int qty){
        this.product=product;
        this.quantity=qty;
    }
    public Product getProduct(){
        return product;
    }
    public int getQuantity(){
        return quantity;
    }
    public double getTotalPrice(){
        return product.getPrice()*quantity;
    }
    
}

//payment startegy pattern
//interface Payment Strategy
interface PaymentStrategy{
    void pay(double amount);
}

//concerete strategies
public class CashPayment implements PaymentStrategy{
    @Override
    public void pay(double amount){
        System.out.println("Paid ₹" + amount + "via cash");
        return;
    }
}
public class CardPayment implements PaymentStrategy{
    @Override
    public void pay(double amount){
        System.out.println("Paid ₹" + amount + "via card");
        return;
    }
}
public class UPIPayment implements PaymentStrategy{
    @Override
    public void pay(double amount){
        System.out.println("Paid ₹" + amount + "via UPI");
        return;
    }
}

//context class
public class Payment{
    private PaymentStrategy strategy;
    public Payment(PaymentStrategy strategy){
        this.strategy=strategy;
    }
    public void makePayment(double amount){
        strategy.pay(amount);
    }
}

// class Cart
public class Cart{
    private List<CartItem> items=new ArrayList<>();
    public void addItem(Product product,int quantity){
        if(product.getStock()<quantity){
            System.out.println("Product out of Stock");
            return;
        }
        CartItem item=new CartItem(product,quantity);
        items.add(item);
        System.out.println("Product"+product.getName()+"added to Cart");
    }
    public double getTotal(){
        double total=0;
        for(CartItem item:items){
            total+=item.getTotalPrice();

        }
        return total;
    }
    public List<CartItem> getCart(){
        return items;
    }
    public void clearCart(){
        items.clear();
    }

}

//class Customer
public class Customer{
    private int id;
    private String name;
    private Cart cart;

    public Customer(int id,String name){
        this.id=id;
        this.name=name;
        cart=new Cart();
    }
    public Cart getCart(){
        return cart;
    }
    public String getName(){
        return name;
    }

}

//class order
public class Order{
    private int orderId;
    private Customer customer;
    private double totalamount;

    public Order(int orderId,Customer customer,double totalamount){
        this.orderId=orderId;
        this.customer=customer;
        this.totalamount=totalamount;
    }
    public void showOrder(){
        System.out.println("Order ID: " + orderId);
        System.out.println("Customer: " + customer.getName());
        System.out.println("Total: ₹" + totalamount);
    }
}

// class GroceryStore
public class GroceryStore{
    private List<Product> products=new ArrayList<>();
    private int nextOrderid=1;

    public void addProduct(Product product){
        products.add(product);
        System.out.println(product.getName() + "added successfully");

    }
    public void showProducts(){
        for(Product prod:products){
            System.out.println(prod.getId() + " | " + prod.getName() + " | " + prod.getStock() + " | " + prod.getPrice() );
        }
    }

    public void checkout(Customer customer,PaymentStrategy strategy){
        Cart cart=customer.getCart();
        double total=cart.getTotal();

        if (total == 0) {
            System.out.println("Cart is empty");
            return;
        }
        Payment payment = new Payment(strategy);
        payment.makePayment(total);

        for(CartItem item: cart.getCart()){
            item.getProduct().reduceStock(item.getQuantity());

        }
        Order order=new Order(nextOrderid++,customer,total);
        order.showOrder();

        cart.clearCart();
        System.out.println("Order placed successfully!");




    }
}

public class Main {

    public static void main(String[] args) {

        GroceryStore store = new GroceryStore();

        Product milk = new Product(
            1, "Milk", 60, 10
        );

        Product bread = new Product(
            2, "Bread", 40, 5
        );

        store.addProduct(milk);
        store.addProduct(bread);

        
        store.showProducts();

        Customer customer = new Customer(101, "Dhruv");

        // Add products to cart
        customer.getCart().addProduct(milk, 2);
        customer.getCart().addProduct(bread, 1);

        // Checkout using UPI
        PaymentStrategy payment = new UPIPayment();

        store.checkout(customer, payment);

        // Products after purchase
        System.out.println("\nUpdated Stock:");
        store.showProducts();
    }
}
