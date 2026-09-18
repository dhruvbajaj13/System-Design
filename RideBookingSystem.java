/*Functional Requirements
Rider ride request kar sakta hai.
System available drivers maintain karega.
Rider ko ek available driver assign hoga.
Driver ride accept karega.
Ride start aur complete ho sakti hai.
Ride ka fare calculate/store hoga.
Ride complete hone par payment hoga.
Ride complete hone ke baad driver available ho jayega.

*/

/*
//classes and entities
1. Location
2. Rider
3. Driver
4. Payment
5. Payment Strategy
6. RideManager
7. Ride Status
8. ride
9. driver matching strategy
 */

import java.util.*;

//ride status enum
enum RideStatus{
    REQUESTED,ONGOING,COMPLETED;
}

//class Location
public class Location{
    private double x;
    private double y;

    public Location(double x,double y){
        this.x=x;
        this.y=y;
    }
    public double distanceFrom(Location other){
        double dx=x-other.x;
        double dy=y-other.y;

        return Math.sqrt(dx*dx+dy*dy);
    }
}

//class rider
public class Rider{
    private int id;
    private String name;
    private Location location;

    public Rider(int id,String name,Location location){
        this.id=id;
        this.name=name;
        this.location=location;
    }
    public int getId(){
        return id;
    }
    public String getName(){
        return name;
    }
    public Location getLocation(){
        return location;
    }
}

//class driver

public class Driver{
    private int id;
    private String name;
    private Location location;
    private boolean available;

    public Driver(int id,String name,Location location){
        this.id=id;
        this.name=name;
        this.location=location;
        this.available=true;
    }
    public int getId(){
        return id;
    }
    public String getName(){
        return name;
    }
    public Location getLocation(){
        return location;
    }
    public boolean isAvailable(){
        return available;
    }
    public void setAvailable(boolean available){
        this.available=available;
    }
}

//payment strategy
//interface payment strategy
interface PaymentStrategy{
    void pay(double amount);
}
//concrete strategies
public class CashPayment implements PaymentStrategy{
    @Override 
    public void pay(double amount){
        System.out.println("₹"+ amount +"paid via Cash");
    }

}
public class CardPayment implements PaymentStrategy{
    @Override 
    public void pay(double amount){
        System.out.println("₹"+ amount +"paid via Card");
    }

}
public class UPIPayment implements PaymentStrategy{
    @Override 
    public void pay(double amount){
        System.out.println("₹"+ amount +"paid via UPI");
    }

}

//context class payment

public class Payment{
    private PaymentStrategy strategy;
    public Payment(PaymentStrategy strategy){
        this.strategy=strategy;
    }
    public void makePayment(double amount){
        strategy.pay(amount);
    }
}

//class ride
public class Ride{
    private int id;
    private Rider rider;
    private Driver driver;

    private double fare;
    private Payment payment;
    private RideStatus status;

    public Ride(int id,Rider rider,Driver driver,double fare,Payment payment){
        this.id=id;
        this.rider=rider;
        this.driver=driver;
        this.fare=fare;
        this.payment=payment;
        this.status=RideStatus.REQUESTED;
    }
    public void startRide(){
        if(status!=RideStatus.REQUESTED){
            System.out.println("Cannot start a ride");
            return;
        }
        status=RideStatus.ONGOING;
        System.out.println("Rider started with" + driver.getName());

    }
    public void completeRide(){
        if(status!=RideStatus.ONGOING){
            System.out.println("Cannot start a ride");
            return;
        }
        payment.makePayment(fare);
        status=RideStatus.COMPLETED;
        driver.setAvailable(true);
        System.out.println("Rider completed successfully" );

    }
}

// driver matching starategy
//interface driver matching strategy 
interface DriverMatchingStrategy{
    Driver findDriver(Rider rider,List<Driver> drivers);

    
}
//concrete strategies
public class NearestDriverStrategy implements DriverMatchingStrategy{
    @Override
    public Driver findDriver(Rider rider,List<Driver> drivers){
        Location startlocation=rider.getLocation();
        Driver selected=null;
        double mindist=Integer.MAX_VALUE;

        for(Driver driver:drivers){
            Location driverlocation=driver.getLocation();
            double dist=startlocation.distanceFrom(driverlocation);
            if(dist<mindist){
                selected=driver;
                mindist=dist;
            }
        }
        return selected;
    }
}

//class RideManager
public class RideManager{
    private List<Driver> drivers;
    private DriverMatchingStrategy strategy;
    int nextDriverid=1;

    public RideManager(DriverMatchingStrategy strategy){
        this.strategy=strategy;
        drivers=new ArrayList<>();
    }

    public void addDriver(Driver driver){
        drivers.add(driver);
    }
    public Ride bookRide(Rider rider,double fare,PaymentStrategy paymentstrategy){
        Driver driver=strategy.findDriver(rider,drivers);
        if(driver==null){
            System.out.println("No driver found");
            return null;
        }
        driver.setAvailable(false);
        Payment payment=new Payment(paymentstrategy);
        Ride ride=new Ride(nextDriverid++,rider,driver,fare,payment);
        System.out.println("Ride Booked Successfully with" + driver.getName());
        return ride;
    }
    
}

public class Main{
    public static void main(String[] args){
        Rider rider=new Rider(1,"Dhruv",new Location(1,2))
        Driver driver1=new Driver(101,"Yash",new Location(3,4));
        Driver driver2=new Driver(101,"Rudra",new Location(10,13));

        DriverMatchingStrategy matchingstrategy=new NearestDriverStrategy();
        RideManager manager=new RideManager(matchingstrategy);

        manager.addDriver(driver1);
        manager.addDriver(driver2);

        PaymentStrategy paymentStrategy=new CashPayment();
        Ride ride=manager.bookRide(rider,250.0,paymentStrategy);

        if(ride!=null){
            ride.startRide();
            ride.completeRide();

        }
    }
}
