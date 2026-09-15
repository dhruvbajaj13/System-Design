import java.util.*;
import java.time.Duration;
import java.time.LocalDateTime;


// 4 enums are there
// 1. vehicle type 2. sport type  3. payment status  4. ticket status

enum VehicleType{
    CAR,BIKE,TRUCK;
}

enum SpotType{
    CARSPOT,BIKESPOT,TRUCKSPOT;
}

enum PaymentStatus{
    PENDING,SUCCESS,FAILED;
}

enum TicketStatus{
    ACTIVE,EXITED,PAID
}

//core etities

//vehicle class

public class Vehicle{
    private final String licenseNumber;
    private final VehicleType vehicletype;

    public Vehicle(String licenseno,VehicleType vtype){
        this.licenseNumber=licenseno;
        this.vehicletype=vtype;
    }

    public String getLicenseNo(){
        return licenseNumber;
    }

    public VehicleType getType(){
        return vehicletype;
    }

}


//vehicle factory

public class VehicleFactory{
    public static Vehicle createVehicle(String lno,VehicleType vtype){
        return new Vehicle(lno,vtype);

    }
}

//parking spot

public class ParkingSpot{
    private final int id;
    private final SpotType spottype;

    private Vehicle vehicle; //null means spot is free

    public ParkingSpot(int id,SpotType spottype){
        this.id=id;
        this.spottype=spottype;
    }
    public int getId(){
        return id;
    }

    public SpotType getSpotType(){
        return spottype;
    }

    public synchronized boolean isAvailable(){
        return vehicle==null;
    }

    public synchronized void park(Vehicle vehicle){
        if(!isAvailable()){
            throw new IllegalStateException("Spot Occupied");
        }
        this.vehicle=vehicle;
    }
    public synchronized void vacate(){
        if(vehicle==null){
            throw new IllegalStateException("Spot already empty");
        }
        this.vehicle=null;
    }
    public synchronized Vehicle getVehicle(){
        return vehicle;
    }
}

//parking floor
public class ParkingFloor{
    private final int floorno;
    private final List<ParkingSpot> spots;
    

    public ParkingFloor(int floorno){
        this.floorno=floorno;
        this.spots=new ArrayList<>();

    }
    public int getFloor(){
        return floorno;
    }

    public void addSpot(ParkingSpot spot){
        spots.add(spot);
    }
    public List<ParkingSpot> getParkingSpots(){
        return spots;
    }

    //find the available parking spot
    public ParkingSpot findAvailableSpot(SpotType spottype){
        for(ParkingSpot parkingspot:spots){
            if(parkingspot.getSpotType()==spottype && parkingspot.isAvailable()){
                return parkingspot;
            }
        }
        return null;
    }
}

//ticket
public class Ticket{
    private final String ticketid;
    private final Vehicle vehicle;
    private final ParkingSpot parkingspot;
    private final LocalDateTime entrytime;

    private final LocalDateTime exittime;
    private TicketStatus status;

    public Ticket(String ticketid,Vehicle vehicle,ParkingSpot parkingspot){
        this.ticketid=ticketid;
        this.vehicle=vehicle;
        this.parkingspot=parkingspot;
        this.entrytime=LocalDateTime.now();
        this.status=TicketStatus.ACTIVE;
    }

    public String getId(){
        return ticketid;
    }

    public Vehicle getVehicle(){
        return vehicle;
    }
    public ParkingSpot getParkingspot(){
        return parkingspot;
    }

    public LocalDateTime getEntryTime(){
        return entrytime;
    }
    public TicketStatus getStatus(){
        return status;
    }
    public LocalDateTime getExitTime(){
        return exittime;
    }

    public void setExitTime(LocalDateTime exittime){
        this.exittime=exittime;
    }

    public void setStatus(TicketStatus status){
        this.status=status;
    }
}

//fee strategy

//interface
interface FeeStrategy{
    double calculateFee(Ticket ticket);
}
//concrete classes
public class HourlyFeeStrategy implements FeeStrategy{
    @Override
    public double calculateFee(Ticket ticket){
        LocalDateTime entrytime=ticket.getEntryTime();
        LocalDateTime exittime=ticket.getExitTime();

        long minutes=Duration.between(entrytime,exittime).toMinutes();

        //min 1 hour
        long hours=Math.max(1,(minutes+59)/60);

        VehicleType type=ticket.getVehicle().getType();
        if(type==VehicleType.BIKE) return hours*20;
        else if(type==VehicleType.CAR) return hours*50;
        else if(type==VehicleType.TRUCK) return hours*100;
        else throw new IllegalArgumentException("Unknown Vehicle Type");



    }

}
public class PremiumFeeStrategy implements FeeStrategy{
    @Override
    public double calculateFee(Ticket ticket){
        LocalDateTime entrytime=ticket.getEntryTime();
        LocalDateTime exittime=ticket.getExitTime();

        long minutes=Duration.between(entrytime,exittime).toMinutes();

        //min 1 hour
        long hours=Math.max(1,(minutes+59)/60);

        VehicleType type=ticket.getVehicle().getType();
        if(type==VehicleType.BIKE) return hours*40;
        else if(type==VehicleType.CAR) return hours*80;
        else if(type==VehicleType.TRUCK) return hours*140;
        else throw new IllegalArgumentException("Unknown Vehicle Type");



    }

}

//context class
class FeeCalculator {

    private FeeStrategy feeStrategy;

    public FeeCalculator(FeeStrategy feeStrategy) {
        this.feeStrategy = feeStrategy;
    }

    public void setFeeStrategy(FeeStrategy feeStrategy) {
        this.feeStrategy = feeStrategy;
    }

    public double calculateFee(Ticket ticket) {

        if (ticket.getExitTime() == null) {
            throw new IllegalStateException(
                    "Exit time is not set"
            );
        }

        return feeStrategy.calculateFee(ticket);
    }
}

//payment strategy

//interface
interface PaymentStrategy{
    boolean pay(double amount);
}

//concrete classes
public class CashPayment implements PaymentStrategy{
    @Override 
    public boolean pay(double amount){
        System.out.println("₹" + amount + "paid using cash");
        return true;
    }
}
public class CardPayment implements PaymentStrategy{
    @Override 
    public boolean pay(double amount){
        System.out.println("₹" + amount + "paid using card");
        return true;
    }
}
public class UPIPayment implements PaymentStrategy{
    @Override 
    public boolean pay(double amount){
        System.out.println("₹" + amount + "paid using google pay upi");
        return true;
    }
}

//context class
public class Payment{
    private final String paymentId;
    private final PaymentStrategy strategy;
    private final double amount;

    private final PaymentStatus status;

    public Payment(String paymenId,PaymentStrategy strategy,double amount){
        this.paymentId=paymentId;
        this.strategy=strategy;
        this.amount=amount;

        this.status=PaymentStatus.PENDING;
    }
    public double getAmount(){
        return amount;
    }
    public PaymentStatus getStatus(){
        return status;
    }
    public boolean process(){
        boolean success=strategy.pay(amount);
        if(success) status=PaymentStatus.SUCCESS;
        else status =PaymentStatus.FAILED;

        return success;


    }

}

//parking lot

public class ParkingLot{
    private static ParkingLot instance;
    private final List<ParkingFloor> floors;

    private int ticketCounter;
    private int paymentCounter;

    private ParkingLot(){
        floors=new ArrayList<>();
        ticketCounter=1;
        paymentCounter=1;
    }

    public static synchronized ParkingLot getInstance(){
        if(instance==null){
            instance=new ParkingLot();
        }
        return instance;
    }

    //add floors'
    public void addFloor(ParkingFloor floor){
        floors.add(floor);
        floors.sort(
                Comparator.comparingInt(
                        ParkingFloor::getFloorNumber
                )
        );
    }

    //findnearest spot
    private ParkingSpot findSpot(VehicleType vehicleType){
        SpotType requiredSpotType;

        switch (vehicleType) {

            case BIKE:
                requiredSpotType = SpotType.BIKE;
                break;

            case CAR:
                requiredSpotType = SpotType.CAR;
                break;

            case TRUCK:
                requiredSpotType = SpotType.TRUCK;
                break;

            default:
                throw new IllegalArgumentException(
                        "Unknown vehicle type"
                );
        }


        // Floors are sorted.
        // Spots are checked in insertion order.
        // Therefore first available spot = nearest.
        for (ParkingFloor floor : floors) {

            ParkingSpot spot =
                    floor.findAvailableSpot(requiredSpotType);

            if (spot != null) {
                return spot;
            }
        }

        return null;
    }
    public synchronized Ticket parkVehicle(
            Vehicle vehicle) {

        ParkingSpot spot =
                findSpot(vehicle.getVehicleType());

        if (spot == null) {

            throw new IllegalStateException(
                    "No parking spot available for "
                            + vehicle.getVehicleType()
            );
        }

        // Critical section protected by synchronized
        spot.park(vehicle);

        String ticketId =
                "T-" + ticketCounter++;

        Ticket ticket =
                new Ticket(
                        ticketId,
                        vehicle,
                        spot
                );

        System.out.println(
                "Vehicle "
                        + vehicle.getLicenseNumber()
                        + " parked at spot "
                        + spot.getId()
                        + ". Ticket: "
                        + ticketId
        );

        return ticket;
    }


    // --------------------------------------------------------
    // EXIT VEHICLE
    // --------------------------------------------------------

    public synchronized boolean exitVehicle(
            Ticket ticket,
            FeeStrategy feeStrategy,
            PaymentStrategy paymentStrategy) {

        if (ticket.getStatus() != TicketStatus.ACTIVE) {

            System.out.println(
                    "Ticket is not active"
            );

            return false;
        }


        // Set exit time
        ticket.setExitTime(LocalDateTime.now());


        // Calculate fee
        FeeCalculator feeCalculator =
                new FeeCalculator(feeStrategy);

        double fee =
                feeCalculator.calculateFee(ticket);

        System.out.println(
                "Parking fee = ₹" + fee
        );


        // Payment
        Payment payment =
                new Payment(
                        "P-" + paymentCounter++,
                        fee,
                        paymentStrategy
                );

        boolean paymentSuccessful =
                payment.process();


        if (!paymentSuccessful) {

            System.out.println(
                    "Payment failed. Vehicle cannot exit."
            );

            return false;
        }


        // Payment successful
        ticket.setStatus(TicketStatus.PAID);


        // Free parking spot
        ticket.getParkingSpot().vacate();


        // Finally mark ticket exited
        ticket.setStatus(TicketStatus.EXITED);


        System.out.println(
                "Spot "
                        + ticket.getParkingSpot().getId()
                        + " is now free."
        );

        System.out.println(
                "Vehicle "
                        + ticket.getVehicle().getLicenseNumber()
                        + " exited successfully."
        );

        return true;
    }



    
}
