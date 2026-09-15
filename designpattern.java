//Structural Design Pattern
//1. Factory Pattern
interface Vehicle{
    void start();
}

class Car implements Vehicle{
    @Override 
    public void start(){
        System.out.println("Started");
    }

}
class Bike implements Vehicle{
    @Override 
    public void start(){
        System.out.println("Started");
    }

}
public class VehicleFactory{
    static Vehicle getVehicle(String type){
        if(type.equals("Car")) return new Car();
        else if(type.equals("Bike")) return new Bike();
        else throw UnsupportedOperationException("Not exist");
    }
    
}


//2. Singleton Pattern
class Singleton{
    private static Singleton instance;
    private Singleton(){

    }
    public static Singleton getInstance(){
        if(instance==null){
            instance=new Singleton();
        }
        return instance;
    }
}

//multithreading singleton case
class MultiThreadedSingleton{
    private static volatile MultiThreadedSingleton instance;
    private MultiThreadedSingleton(){

    }
    public static MultiThreadedSingleton getInstace(){
        synchronized(MultiThreadedSingleton.class){
            if(instance==null){
                instance=new MultiThreadedSingleton();
            }
            return instance;
        }
    }
}

//3. Strategy Pattern

//strategy interface
interface PaymentStrategy{
    void pay();

}

//concrete strategies
public class CardPayment implements PaymentStrategy{
    @Override
    public void pay(){
        System.out.println("Card Payment");
        
    }
}

public class CashPayment implements PaymentStrategy{
    @Override
    public void pay(){
        System.out.println("Cash Payment");
        
    }
}

//context classes
public class PaymentProcesser{
    private PaymentStrategy strategy;
    public PaymentProcessor(PaymentStrategy strategy){
        this.strategy=strategy;
    }
    public void pay(){
        strategy.pay();
    }
    public void setStrategy(PaymentStrategy strategy){
        this.strategy=strategy;
    }
}

//client code
public class Payment{
    public static void main(String[] args){
        PaymentProcessor process=new PaymentProcessor(new CardPayment());
        process.pay();
        process.setStrategy(new CashPayment());
        
    }
}

// 4. Observer Pattern

//observer interface
interface Subscriber{
    void update(String video);
}
//concrete observer
public class AppUser implements Subscriber{
    @Override
    public void update(String video){
        System.out.println("App notification" +video);
    }

}
public class EmailUser implements Subscriber{
    @Override
    public void update(String video){
        System.out.println("Email notification" +video);
    }

}

//subject interface
interface Channel{
    void addSubscribers(Subscriber s);
    void removeSubscribers(Subscriber s);
    void notifySubscribers();
}

//concrete subject
public class YTchannel implements Channel{
    private List<Subscriber> subscribers=new ArrayList<>();
    String video;

    public void addSubscriber(Subscriber s){
        subscribers.add(s);
    }
    public void removeSubscriber(Subscriber s){
        subscribers.remove(s);
    }
    public void notifySubscribers(){
        for(Subscriber s:subscribers){
            s.update(video);
        }
    }
    private void update(String video){
        this.video=video;
        notifySubscribers();
    }
}


//5. State pattern
//interface state
interface State{
    void handle(Context ctx);
}
//Concrete state
class ConcreteStateA implements State{
    @Override
    public void handle(Context ctx){
        ctx.setState(new ConcreteStateB());
    }
}

//context class
class Context{
    private State state;
    public void setState(State state){
        this.state=state;
    }
    public void request(){
        state.handle(this);
    }
}
