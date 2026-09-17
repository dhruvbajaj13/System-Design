//core class and entities
/* 1. builing
2. floors
3. elevators
4.elevator request
5.elevtorcontroller
6. panels--> insidepanel and outsidepanel
7. display
8. door
9. buttons
10. scheduling strategy
 */

import java.util.*;
//enum directions
enum Direction{
    UP,DOWN,IDLE;
}

//enum elevator state
enum ElevatorState{
    IDLE,MOVING,STOPPED,MAINTAINANCE;
}

//enum door state
enum DoorState{
    OPEN,CLOSED;
}

//class elevator request
public class ElevatorRequest{
    int sourcefloor;
    int destfloor;
    Direction direction;

    public ElevatorRequest(int srcfloor,int destfloor){
        this.sourcefloor=srcfloor;
        this.destfloor=destfloor;
        if(destfloor>srcfloor){
            direction=Direction.UP;
        }else if(srcfloor>destfloor){
            direction=Direction.DOWN;
        }else{
            direction=Direction.IDLE;
        }
        
    }
    public int getSourceFloor(){
        return sourcefloor;
    }
    public int getDestFloor(){
        return destfloor;
    }
    public Direction getDirection(){
        return direction;
    }

}

//class door
public class Door{
    private DoorState state;
    public Door(){
        state=DoorState.CLOSED;
    }
    public void openDoor(){
        state=DoorState.OPEN;
        System.out.println("Door opened");
    }

    public void closeDoor(){
        state=DoorState.CLOSED;
        System.out.println("Door closed");
    }

    public DoorState getState(){
        return state;
    }

}

//class display
public class Display{
    private int currfloor;
    private Direction direction;

    public void update(int floor,Direction dir){
        this.currfloor=floor;
        this.direction=dir;

        System.out.println("Display: Floor | "+currfloor + "| Direction" + direction);
    }

}

//interface button
interface Button{
    void press();
}
//concrete classes
//hallbutton-> outside the elevator
public class HallButton implements Button{
    private Direction directions;
    public HallButton(Direction dir){
        this.directions=dir;
    }
    @Override
    public void press(){
        System.out.println("Hall Button Pressed");
    }
    public Direction getDirection(){
        return directions;
    }

}
//floor button-> inside the elevator
public class FloorButton implements Button{
    private int floor;
    public FloorButton(int floor){
        this.floor=floor;
    }
    @Override
    public void press(){
        System.out.println("Floor Button Pressed");
    }
    public int getFloor(){
        return floor;
    }

}
//door button-> inside the elevator
public class DoorButton implements Button{
    private boolean openbutton;
    public DoorButton(boolean openbutton){
        this.openbutton=openbutton;
    }
    @Override
    public void press(){
        if(openbutton){
            System.out.println("Open door button pressed");
        }else{
            System.out.println("Closed door button pressed");
        }
    }
    

}

//panels class
//inside panel class
public class InsidePanel{
    List<FloorButton> floorbuttons;
    private DoorButton openbutton;
    private DoorButton closedbutton;

    public InsidePanel(int totalfloors){
        floorbuttons=new ArrayList<>();
        for(int i=0;i<totalfloors;i++){
            floorbuttons.add(new FloorButton(i));

        openbutton=new DoorButton(true);
        closedbutton=new DoorButton(false);

    }
    public void selectFloor(int floor){
        floorbuttons.get(floor).press();

    }
    public void pressOpen(){
        openbutton.press();
    }
    public void pressClosed(){
        closedbutton.press();
    }

}

//outside panel
public class OutsidePanel{
    private HallButton upbutton;
    private HallButton downbutton;

    public OutsidePanel(){
        upbutton=new HallButton(Direction.UP);
        downbutton=new HallButton(Direction.DOWN);
    }
    public void pressUp(){
        upbutton.press();
    }
    public void pressDown(){
        downbutton.press();
    }
}

//class Elevator
public class Elevator{
    private int id;
    private ElevatorState state;
    private Direction direction;
    private int currfloor;
    private InsidePanel insidepanel;
    private Door door;
    private Display display;

    public Elevator(int id,int totalfloors){
        this.id=id;
        state=ElevatorState.IDLE;
        direction=Direction.IDLE;
        currfloor=0;
        insidepanel=new InsidePanel(totalfloors);
        door=new Door();
        display=new Display();
    }
    public int getId(){
        return id;
    }
    public int getCurrFloor(){
        return currfloor;
    }
    public Direction getDirection(){
        return direction;
    }
    public ElevatorState getState(){
        return state;
    }

    public void moveToFloor(int destfloor){
        if(state==ElevatorState.MAINTAINANCE){
            System.out.println("Elevaor under maintainance");
            return;

        }
        if(destfloor==currfloor){
            System.out.println("Destination already reached");
            return;
        }
        if(destfloor>currfloor){
            direction=Direction.UP;
        }else{
            direction=Direction.DOWN;
        }
        state=ElevatorState.MOVING;
        currfloor=destfloor;
        state=ElevatorState.STOPPED;
        
        System.out.println("Destination floor reached");
        
        display.update(currfloor,direction);
        openDoor();
        closeDoor();
        direction=Direction.IDLE;
        state=ElevatorState.IDLE;

    }

    public void processRequest(ElevatorRequest req){
        moveToFloor(req.getSourceFloor()); //move to source floor
        insidepanel.selectFloor(req.getDestFloor());
        moveToFloor(req.getDestFloor());
    }
    public void openDoor(){
        door.openDoor();
    }
    public void closeDoor(){
        door.closeDoor();
    }
    public void setMaintenance(boolean value) {

        if (value) {
            state = ElevatorState.MAINTAINANCE;
        } else {
            state = ElevatorState.IDLE;
        }
    }
}

//scheduling strategy
interface SchedulingStrategy{
    Elevator selectElevator(List<Elevator> elevators,ElevatorRequest request);
}

//fcfs algo
public class FCFS implements SchedulingStrategy{
    @Override
    public Elevator selectElevator(List<Elevator> elevators,ElevatorRequest request){
        Elevator selected=null;
        int mindist=Integer.MAX_VALUE;
        for(Elevator elevator:elevators){
            if(elevator.getState()==ElevatorState.MAINTAINANCE) continue;

            int distance=Math.abs(elevator.getCurrFloor()-request.getSourceFloor());
            if(distance<mindist){
                mindist=distance;
                selected=elevator;
            }
        }
        return selected;
    }

}

//scan algo
public class SCAN implements SchedulingStrategy{
    @Override
    public Elevator selectElevator(List<Elevator> elevators,ElevatorRequest request){
        Elevator selected=null;
        int mindist=Integer.MAX_VALUE;
        for(Elevator elevator:elevators){
            if(elevator.getState()==ElevatorState.MAINTAINANCE) continue;
            boolean samedirection=(elevator.getDirection()==request.getDirection());
            boolean idle=(elevator.getDirection()==Direction.IDLE);

            if(samedirection || idle){
                int distance=Math.abs(elevator.getCurrFloor()-request.getSourceFloor());
                if(distance<mindist){
                   mindist=distance;
                   selected=elevator;
                }

            }

            
            
        }
        if(selected==null){
            for(Elevator elevator:elevators){
                if(elevator.getState()!=ElevatorState.MAINTAINANCE){
                    selected=elevator;
                    break;
                }
            }
        }
        return selected;
    }

}

//look algo
public class LOOK implements SchedulingStrategy{
    @Override
    public Elevator selectElevator(List<Elevator> elevators,ElevatorRequest request){
        Elevator selected=null;
        int mindist=Integer.MAX_VALUE;
        for(Elevator elevator:elevators){
            if(elevator.getState()==ElevatorState.MAINTAINANCE) continue;
            boolean samedirection=(elevator.getDirection()==request.getDirection());
            boolean idle=(elevator.getDirection()==Direction.IDLE);

            if(samedirection || idle){
                int distance=Math.abs(elevator.getCurrFloor()-request.getSourceFloor());
                if(distance<mindist){
                   mindist=distance;
                   selected=elevator;
                }

            }

            
            
        }
        if(selected==null){
            for(Elevator elevator:elevators){
                if(elevator.getState()!=ElevatorState.MAINTAINANCE){
                    selected=elevator;
                    break;
                }
            }
        }
        return selected;
    }

}

//elevatorcontroller class
public class ElevatorController{
    private List<Elevator> elevators;
    private SchedulingStrategy strategy;

    public ElevatorController(List<Elevator> elevators,SchedulingStrategy strategy){
        this.elevators=elevators;
        this.strategy=strategy;
    }
    public void requestElevator(ElevatorRequest request){
        Elevator elevator=strategy.selectElevator(elevators,request);
        if(elevator==null){
            System.out.println("No elevator available");
            return;
        }
        System.out.println("Elevator assigned");
        elevator.processRequest(request);
    }
    public void setStrategy(SchedulingStrategy strategy){
        this.strategy=strategy;
    }
}

//class floor
public class Floor{
    private int floorno;
    private OutsidePanel panel;
    public Floor(int floorno){
        this.floorno=floorno;
        panel=new OutsidePanel();
    }

    public int getFloorNo(){
        return floorno;
    }
    public OutsidePanel getPanel(){
        return panel;
    }
}

//building class
public class Building{
    private List<Floor> floors;
    private List<Elevator> elevators;
    private ElevatorController controller;
    public Building(int totalfloors,int totalelevators){
        floors=new ArrayList<>();
        elevators=new ArrayList<>();
        for(int i=0;i<totalfloors;i++){
            floors.add(new Floor(i));
        }
        for(int i=0;i<totalelevators;i++){
            elevators.add(new Elevator(i,totalfloors));
        }
        controller=new ElevatorController(elevators,new FCFS());
    }
    public List<Floor> getFloors(){
        return floors;
    }
    public List<Elevator> getElevators(){
        return elevators;
    }
    public ElevatorController getController(){
        return controller;
    }

}

