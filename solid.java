import java.util.*;

//1. Single responsibilty principle (SRP)

//bad design
public class Bread{
    public void bakebread(){
        System.out.println("Bread baked");
    }
    public void manageInventory(){
        System.out.println("Inventory managemant");
    }
    public void orderSupplies(){
        System.out.println("Supplies ordered");
    }
}
// this is a wrong design bcz the same class is doing multiple jobs
//correct srp design:-

public class BakeBread{
    public void bakebread(){
        System.out.println("Bread baked");
    }
    
}
public class Inventory{
    public void manageInventory(){
        System.out.println("Inventory managemant");
    }

}

public class Supplies{
    public void orderSupplies(){
        System.out.println("Supplies ordered");
    }

}

public class Bakery{
    public static void main(String[] args){
        BakeBread bread=new BakeBread();
        Inventory inv=new Inventory();
        Supplies supp=new Supplies();

        bread.bakebread();
        inv.manageInventory();
        supp.orderSupplies();
    }

}


//2. Open Closed Principle(OCP)

//bad design
public class Shape{
    String type;
    public double calculateArea(String type){
        if(type.equals("Circle")){
            return 3.14*5*5;
        }else if(type.equals("Rectangle")){
            return 3*4;
        }
        return 0;
    }
}

//above one is a bad deign bcz if i want to add area of triangle suppose then i need to make modifications in the class shape that violates the open closed principle
// correct ocp design will be:-

abstract class Shape{
    abstract double calculateArea();
}

public class Circle extends Shape{
    private double radius;
    public Circle(double radius){
        this.radius=radius;
    }

    @Override
    public double calculateArea(){
        return 3.14*radius*radius;
    }
}

public class Rectangle extends Shape{
    private int h;
    private int w;
    public Reactangle(int h,int w){
        this.h=h;
        this.w=w;
    }
    @Override
    public double calculateArea(){
        return h*w;
    }
}

public class Shapes{
    public static void main(String[] args){
        Circle circle=new Circle(10);
        Rectangle rect=new Rectangle(10,20);

        circle.calculateArea();
        rect.calculateArea();

    }
}

//3. Liskov Substituion Principle

//bad design
public class Vehicle{
    void startEngine(){
        System.out.println("Starting Engine");
    }
}

public class Car extends Vehicle{
    @Override
    void startEngine(){
        System.out.println("Starting Engine");

    }
}
public class Cycle extends Vehicle{
    @Override
    void startEngine(){
        throw UnsupportedOperationException("Cycle doesnt have a engine");

    }
    
}

// good design

abstract class Vehicle{
    public void move(){
        System.out.println("Start moving");
    }
}

abstract class EngineVehicle extends Vehicle{
    abstract void startEngine();
    
}
abstract class NoEngineVehicle extends Vehicle{
    
}

public class Car extends EngineVehicle{
    @Override
    void startEngine(){
        System.out.println("Starting Engine");

    }

}
public class Bicycle extends NoEngineVehicle{

}

//4. Interface Segregation Principle (ISP)
//bad design

interface Machine{
    void scan();
    void fax();
    void print();
}

public class AllinonePrinter implements Machine{
    @Override
    public void scan(){
        System.out.println("Scanning");
    }
    @Override
    public void print(){
        System.out.println("Printing");
    }
}

public class BasicPrinter implements Machine{
    
    @Override
    public void print(){
        System.out.println("Printing");
    }
    @Override
    public void scan(){
        throw UnsupportedOperationException("Can't scan on a basic printer");
    }
}

//this design is violating isp bcz class depends on methods in interface they actually dont need
// correct design

interface Printer{
    void print();
}
interface Scanner{
    void scan();
}
interface FaxMachine{
    void fax();
}

public class AllinOnePrinter implements Printer,Scanner{
    @Override
    public void scan(){
        System.out.println("Scanning");
    }
    @Override
    public void print(){
        System.out.println("Printing");
    }

}
public class BasicPrinter implements Printer{
    
    @Override
    public void print(){
        System.out.println("Printing");
    }
}

//5. Dependancy Inversion Principle (DCP)
//bad design

class EmailNotifier{
    public void sendMail(String msg){

    }
}

class OrderService{
    private EmailNotifier notifier;
    public OrderService(){
        this.notifier=new EmailNotifier();
    }
    
}

//correct design
interface NotificationSystem{
    void sendNotification(String msg);
}

class OrderServices{
    private NotificationSystem noti;
    public orderServices(NotificationSystem noti){
        this.noti=noti;
    }
}