/*
Requirements
1. System should support multiple players.
2. Board should have a fixed size (e.g., 100 cells).
3. System should support multiple snakes.
4. System should support multiple ladders.
5. Each player should roll a dice on their turn.
6. Player should move according to the dice value.
7. If a player lands on a snake, they move to its tail.
8. If a player lands on a ladder, they move to its top.
9. Player should not move beyond the last cell.
10. The player who reaches the last cell wins.
11. Turns should rotate between players.
 */

//classes and enities
/*
1. Player
2. Board
3. Snake
4. Ladder
5. Game
6. Dice
7. DiceStrategy
 */

import java.util.*;

//player class
public class Player{
    private int id;
    private String name;
    private int position;
    
    public Player(int id,String name){
        this.id=id;
        this.name=name;
        this.position=0;

    }
    public int getId(){
        return id;
    }
    public String getName(){
        return name;
    }
    public int getPosition(){
        return position;
    }
    public void setPosition(int pos){
        this.position=pos;
    }
}

//snake class
public class Snake{
    private int start;
    private int end;

    public Snake(int start,int end){
        this.start=start;
        this.end=end;
    }
    public int getStart(){
        return start;
    }
    public int getEnd(){
        return end;
    }
}

//ladder class
public class Ladder{
    private int start;
    private int end;

    public Ladder(int start,int end){
        this.start=start;
        this.end=end;
    }
    public int getStart(){
        return start;
    }
    public int getEnd(){
        return end;
    }
}

//dice strategy pattern

//dicestrategy interface
interface DiceStrategy{
    int roll();
}

//concrete startegies
public class NormalDice implements DiceStrategy{
    private Random random=new Random();
    @Override
    public int roll(){
        return random.nextInt(6)+1;  //nextInt(6) 0 se 5 tk random no dega 
    }

}
public class LoadedDice implements DiceStrategy{
    @Override
    public int roll(){
        return 6;
    }
}

//dice class
public class Dice{
    private DiceStrategy strategy;
    public Dice(DiceStrategy strategy){
        this.strategy=strategy;
    }
    public int roll(){
        return strategy.roll();
    }
}

// class Board
public class Board{
    private int size;
    private List<Snake> snakes;
    private List<Ladder> ladders;

    public Board(int size){
        this.size=size;
        snakes=new ArrayList<>();
        ladders=new ArrayList<>();
    }
    public void addSnake(Snake snake){
        snakes.add(snake);
    }
    public void addLadder(Ladder ladder){
        ladders.add(ladder);
    }
    public int getSize(){
        return size;
    }
    public int getFinalPosition(int pos){
        //check snake
        for(Snake snake:snakes){
            if(snake.getStart()==pos){
                System.out.println("Moving from" + pos + "to" + snake.getEnd());
                return snake.getEnd();
        
            }
        }
        //check ladder
        for(Ladder ladder:ladders){
            if(ladder.getStart()==pos){
                System.out.println("Moving from" + pos + "to" + ladder.getEnd());
                return ladder.getEnd();

            }
        }
        return pos;
    }
}

//class game
public class Game{
    private Board board;
    private Dice dice;
    private List<Player> players;
    int currplayeridx;

    public Game(Board board,List<Player> players,DiceStrategy strategy){
        this.board=board;
        this.players=players;
        this.dice=new Dice(strategy);
        this.currplayeridx=0;
    }
    public void play(){
        while(true){
            Player player=players.get(currplayeridx);
            int dicevalue=dice.roll();
            int oldposition=player.getPosition();
            int newposition=oldposition+dicevalue;
            System.out.println(player.getName() +"rolled" + dicevalue);

            if(newposition>board.getSize()){
                System.out.println("Cannot move. Position remains" + oldposition);
            }else{
                int finalposition=board.getFinalPosition(newposition);
                player.setPosition(finalposition);
                System.out.println("Player" + player.getName() + "moved to" + finalposition);

                if(player.getPosition()==board.getSize()){
                    System.out.println(player.getName() + "won the game");
                    break;

                }
                currplayeridx=(currplayeridx+1)%players.size();
            }
        }
    }
}

public class Main{
    public static void main(String[] args){
        Board board=new Board(100);
        
        board.addSnake(new Snake(99, 10));
        board.addSnake(new Snake(95, 40));
        board.addSnake(new Snake(75, 30));

        // Add ladders
        board.addLadder(new Ladder(4, 25));
        board.addLadder(new Ladder(20, 45));
        board.addLadder(new Ladder(50, 70));

        // Create players
        Player p1 = new Player(1, "Dhruv");
        Player p2 = new Player(2, "Yash");

        List<Player> players = new ArrayList<>();

        players.add(p1);
        players.add(p2);

        // Normal Dice Strategy
        DiceStrategy strategy = new NormalDice();

        // Create game
        Game game = new Game(
                board,
                players,
                strategy
        );

        // Start game
        game.play();
    }
}
