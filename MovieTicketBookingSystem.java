/*
Functional requirements
1. User can browse the movies
2. user can select the movie
3.user select the threatre and shor
4. user select the avaiable seats 
5. lock the seat temporarily(locking mechanism)
6. make payment and confirm the booking
7. release the seat if payment fails or lock expires

 */

/*  classes and entities
1. user
2. movie
3. theatre
4. show
5. seat
6. booking 
7. payment

 */

import java.util.*;

//enum seat status
enum SeatStatus{
    AVAILABLE,LOCKED,BOOKED;
}

//class user
public class User{
    private int userId;
    private String name;
    private String email;

    public User(int userId,String name,String email){
        this.userId=userId;
        this.name=name;
        this.email=email;
    }
    public int getId(){
        return userId;
    }
    public String getName(){
        return name;
    }
    public String getEmail(){
        return email;
    }

}

// class Movie
public class Movie{
    private String name;
    public Movie(String name){
        this.name=name;
    }

    public String getMovieName(){
        return name;
    }
}

//Theatre

public class Theatre{
    private int theatreId;
    private String name;
    private String location;
    private List<Show> shows=new ArrayList<>();

    public Theatre(int id,String name,String location){
        this.theatreId=id;
        this.name=name;
        this.location=location;
    }
    public int getTheatreId(){
        return theatreId;
    }
    public String getTheatreName(){
        return name;
    }
    public String getLocation(){
        return location;
    }
    public List<Show> getShows(){
        return shows;
    }
    public void addShow(Show show){
        shows.add(show);
    }
}

//payment strategy pattern
//interface payment
interface PaymentStrategy{
    boolean pay(double amount);
}

//concrete strategies
public class CashPayment implements PaymentStrategy{
    @Override
    public boolean pay(double amount){
        System.out.println("Paid ₹"+amount + "via cash");
        return true;
    }
}
public class CardPayment implements PaymentStrategy{
    @Override
    public boolean pay(double amount){
        System.out.println("Paid ₹"+amount + "via card");
        return true;
    }
}
public class UPIPayment implements PaymentStrategy{
    @Override
    public boolean pay(double amount){
        System.out.println("Paid ₹"+amount + "via upi");
        return true;
    }
}

//context classes
public class Payment{
    private PaymentStrategy strategy;
    public Payment(PaymentStrategy strategy){
        this.strategy=strategy;
    }
    public boolean makePayment(double amount){
        return strategy.pay(amount);
    }
}

//class Seat
public class Seat{
    private String seatNo;
    private SeatStatus status;
    private int lockedByBookingId;
    private long lockExpiry;

    public Seat(String seatno){
        this.seatNo=seatno;
        this.status=SeatStatus.AVAILABLE;

    }
    public String getSeatNo(){
        return seatNo;
    }
    public SeatStatus getStatus(){
        return status;
    }
    public void setStatus(SeatStatus status){
        this.status=status;
    }
    public long getlockExpiry(){
        return lockExpiry;
    }
    public void setlockExpiry(long lockExpiry){
        this.lockExpiry=lockExpiry;
    }
    public int getlockedBookingId(){
        return lockedByBookingId;
    }
    public void setlockedBookingId(int bookingId){
        this.lockedByBookingId=bookingId;
    }

}
//class show
public class Show{
    private int showId;
    private Movie movie;
    private Theatre theatre;

    private Map<String,Seat> seats=new HashMap<>();
    public Show(int showid,Movie movie,Theatre theatre){
        this.showId=showid;
        this.movie=movie;
        this.theatre=theatre;
    }
    public void addSeat(Seat seat){
        seats.put(seat.getSeatNo(),seat);
    }
    public Map<String,Seat> showSeats(){
        return seats;
    }
    //we have to lock all the seats agar koo bhi seat available ni aayi to sidha return false
    public synchronized boolean lockSeats(List<String> seatNumbers){ // here list<String> seatNumbers me vo seats ka jo mujhe book krni h
        long currtime=System.currentTimeMillis();
        for(String seatno:seatNumbers){
            Seat seat=seats.get(seatno);
            if(seat==null) return false;
            
            //release expired lock
            if(seat.getStatus()==SeatStatus.LOCKED && seat.getlockExpiry()<=currtime){
                seat.setStatus(SeatStatus.AVAILABLE);

            }
            if(seat.getStatus()!=SeatStatus.AVAILABLE){
                return false;
            }
            

        }
        long expirytime=currtime + 5*60 *1000;  // 5mint ka expiry time in ms

        for(String seatno:seatNumbers){
            Seat seat=seats.get(seatno);
            seat.setStatus(SeatStatus.LOCKED);
            seat.setlockExpiry(expirytime);

        }
        return true;


    }

    private synchronized boolean confirmBooking(List<String> seatNumbers){
        long currtime=System.currentTimeMillis();
        for(String seatno:seatNumbers){
            Seat seat=seats.get(seatno);
            if(seat==null) return false;
            
            //release expired lock
            if(seat.getStatus()!=SeatStatus.LOCKED || seat.getlockExpiry()<=currtime){
                return false;
            }
            
        }
        for(String seatnoo:seatNumbers){
            seats.get(seatnoo).setStatus(SeatStatus.BOOKED);
        }
        return true;

    }
    public synchronized void releaseSeats(List<String> seatNumbers){
        for(String seatno:seatNumbers){
            Seat seat=seats.get(seatno);
            if(seat!=null && seat.getStatus()==SeatStatus.LOCKED){
                seat.setStatus(SeatStatus.AVAILABLE);
            }
        }
        
    }


}

//class Booking
public class Booking{
    private int bookingid;
    private User user;
    private Show show;
    private List<String> seatNumbers;
    private double amount;

    public Booking(int bookingid,User user,Show show,List<String> seatNumbers,double amount){
        this.bookingid=bookingid;
        this.user=user;
        this.show=show;
        this.seatNumbers=seatNumbers;
        this.amount=amount;
    }
    public boolean confirmBooking(PaymentStrategy strategy){
        Payment payment=new Payment(strategy);
        boolean success=payment.makePayment(amount);
        if(!success){
            System.out.println("Payment failed");
            show.releaseSeats(seatNumbers);
            return false;
        }
        boolean confirmed=show.confirmBooking(seatNumbers);
        if(!confirmed){
            System.out.println("Booking failed");
            return false;

        }
        System.out.println("Tickets booked");
        return true;
    }
}
public class Main {

    public static void main(String[] args) {

        // Create User
        User user = new User(
            1,
            "Dhruv",
            "dhruv@gmail.com"
        );

        // Create Movie
        Movie movie = new Movie("Avengers");

        // Create Show
        Show show = new Show(101, movie);

        // Add Seats
        show.addSeat(new Seat("A1"));
        show.addSeat(new Seat("A2"));
        show.addSeat(new Seat("A3"));

        // Select Seats
        List<String> selectedSeats =
            Arrays.asList("A1", "A2");

        // Lock Seats
        boolean locked = show.lockSeats(selectedSeats);

        if (!locked) {
            System.out.println("Seats not available");
            return;
        }

        System.out.println("Seats locked successfully");

        // Create Booking
        Booking booking = new Booking(
            1001,
            user,
            show,
            selectedSeats,
            500
        );

        // Confirm using UPI Strategy
        boolean confirmed = booking.confirm(
            new UpiPayment()
        );

        if (!confirmed) {
            System.out.println("Booking failed");
        }
    }
}
