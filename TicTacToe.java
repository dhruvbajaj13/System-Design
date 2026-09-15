import java.util.Scanner;
import java.util.ArrayList;
//Classes and entities
/* 1. enum SYMBOL
2. Position class
3. Player class
4. Board class
5. PlayerStrategy
6. TicRTacToeController
*/

//Position class

class Position{
    private int row;
    private int col;
    public Position(int row,int col){
        this.row=row;
        this.col=col;
    }
    public int getRow(){
        return row;
    }
    public int getCol(){
        return col;
    }
}

//enum Symbol

public  enum SYMBOL{
    X,O,EMPTY;
}

//Board class

public class Board{
    private int size;
    private SYMBOL[][] board;

    public Board(int size){
        this.size=size;
        board=new SYMBOL[size][size];
        initialise(board);
    }

    public void initialise(SYMBOL[][] board){
        for(int i=0;i<size;i++){
            for(int j=0;j<size;j++){
                board[i][j]=SYMBOL.EMPTY;
            }
        }
    }

    //validate move
    public boolean isValidMove(Position position){
        int row=position.getRow();
        int col=position.getCol();

        return row>=0 && row<size && col>=0 && col<size && board[row][col]==SYMBOL.EMPTY;
    }
    
    //make move
    public void makeMove(Position pos,SYMBOL s){
        board[pos.getRow()][pos.getCol()]=s;

    }

    //checking has winner
    public boolean hasWinner(SYMBOL s){
        //row wise
        for(int row=0;row<size;row++){
            boolean won=true;
            for(int col=0;col<size;col++){
                if(board[row][col]!=s){
                    won=false;
                    break;
                }

            }
            if(won) return true;
        }
        //col wise
        for(int col=0;col<size;col++){
            boolean won=true;
            for(int row=0;row<size;row++){
                if(board[row][col]!=s){
                    won=false;
                    break;
                }

            }
            if(won) return true;
        }

        //diagonal wise
        boolean won=true;
        for(int i=0;i<size;i++){
            if(board[i][i]!=s){
                won=false;
                break;
            }
        }
        if(won) return true;
        //anti diagonal
        won=true;
        for(int i=0;i<size;i++){
            if(board[i][size-i-1]!=s){
                won=false;
                break;
            }
        }
        if(won) return true;
        return false;
    }
    //draw logic
    public boolean isFull(){
        for(int i=0;i<size;i++){
            for(int j=0;j<size;j++){
                if(board[i][j]==SYMBOL.EMPTY){
                    return false;
                }
            }
        }
        return true;
    }

    public void displayBoard(){
        for(int i=0;i<size;i++){
            for(int j=0;j<size;j++){
                System.out.print(board[i][j] + " ");
            }
            System.out.println();
        }
    }
}

//Player class
public class Player{
    private final String name;
    private final SYMBOL symbol;
    private PlayerStrategy strategy;

    public Player(String name,SYMBOL symbol,PlayerStrategy strategy){
        this.name=name;
        this.symbol=symbol;
        this.strategy=strategy;
    }
    public Position makeMove(Board board){
        return strategy.makemove(board);
    }
    public String getName(){
        return name;
    }
    public SYMBOL getSymbol(){
        return symbol;
    }
}

//Player Strategy
interface PlayerStrategy{
    Position makemove(Board board);
}

public class HumanPlayerStrategy implements PlayerStrategy{
    private final Scanner sc;
    public HumanPlayerStrategy(){
        sc=new Scanner(System.in);
    }
    @Override
    public Position makemove(Board board){
        int row=sc.nextInt();
        int col=sc.nextInt();
        Position pos=new Position(row,col);

        if(board.isValidMove(pos)){
            return pos;

        }
        System.out.println("Invalid");

    }

}
//similary we can make more strategies like Ai players etc .....


public class TicTacToe{
    private final Board board;
    private ArrayList<Player> players;
    private int currplayeridx;

    public TicTacToe(Board board,ArrayList<Player> players){
        this.board=board;
        this.players=players;
        currplayeridx=0;
    }
    public void play(){
        while(true){
            board.displayBoard();
            Player currplayer=players.get(currplayeridx);
            System.out.println(currplayer.getName() + "'s turn");
            Position move=currplayer.makeMove(board);
            board.makeMove(move,currplayer.getSymbol());

            if(board.hasWinner(currplayer.getSymbol())){
                board.displayBoard();
                System.out.println(currplayer.getName() + "won");
                break;

            }
            if(board.isFull()){
                board.displayBoard();
                System.out.println("draw");
                break;

            }
            switchPlayer();

        }
    

    }
    public Player getcurrPlayer(){
        return players.get(currplayeridx);
    }

    private void switchPlayer(){
        currplayeridx=(currplayeridx+1)%players.size();
    }
}


// factory usage
class PlayerFactory {

    public static Player createPlayer(
        String name,
        SYMBOL symbol,
        String type
    ) {
        PlayerStrategy strategy;

        if(type.equals("HUMAN")) {
            strategy = new HumanPlayerStrategy();
        } else {
            strategy = new AIPlayerStrategy();
        }

        return new Player(name, symbol, strategy);
    }
}

Player p1 = PlayerFactory.createPlayer(
    "Dhruv", SYMBOL.X, "HUMAN"
);

Player p2 = PlayerFactory.createPlayer(
    "AI", SYMBOL.O, "AI"
);
