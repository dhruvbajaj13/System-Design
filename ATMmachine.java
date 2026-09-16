import java.util.*;

//core classes and entities
/*  1. ATMmachine  
2. ATMstate using state pattern
3. Card
4. Account
5. ATMinventory
6. CASH TYPE enum
7. TRANSACTION TYPE enum
*/

//cash type enum
enum CashType{
    BILL_100(100),
    BILL_50(50),
    BILL_20(20),
    BILL_10(10);

    private final int value;

    CashType(int val){
        this.value=val;
    }

    public int getValue(){
        return value;
    }
    
}

//enum transaction type

enum TransactionType{
    WITHDRAW, CHECK_BALANCE
}

// class Card

public class Card{
    private final String cardno;
    private final int pinno;
    private final String accountno;

    public Card(String cardno,int pinno,String accountno){
        this.cardno=cardno;
        this.pinno=pinno;
        this.accountno=accountno;

    }
    public String getCardNo(){
        return cardno;
    }
    public String getAccountNo(){
        return accountno;
    }
    private boolean validatepin(int enteredpin){
        return this.pinno==enteredpin;
    }
}


//class Account

public class Account{
    private final String accountno;
    private double balance;

    public Account(String accountno,double balance){
        this.accountno=accountno;
        this.balance=balance;
    }

    public String getAccountNo(){
        return accountno;
    }
    public double getBalance(){
        return balance;
    }
    private boolean withdraw(double amount){
        if(balance<amount) return false;
        if(amount<=0) return false;

        balance-=amount;
        return true;
    }
    
    private void deposit(double amount){
        if(amount>0) balance+=amount;
    }
}

// class ATM inventory

public class ATMInventory{
    private final Map<CashType,Integer> inventory;
    public ATMInventory(){
        inventory=new HashMap<>();

        for(CashType type:CashType.values()){
            inventory.put(type,0);
        }
    }

    public void addCash(CashType type,int count){
        inventory.put(type,inventory.get(type)+count);

    }
    public int getTotalCash(){
        int total=0;
        for(CashType type:inventory.keySet()){
            total+=type.getValue() + inventory.get(type);

        }
        return total;
    }
    public boolean hasSufficientCash(int amount){
        return getTotalCash()>=amount;

    }

    public Map<CashType,Integer> dispenseCash(int amount){
        if(!hasSufficientCash(amount)) return new HashMap<>();
        Map<CashType,Integer> map =new HashMap<>();
        int remaining=amount;   

        CashType[] denominations={
            CashType.BILL_100,
            CashType.BILL_50,
            CashType.BILL_20,
            CashType.BILL_10
        };

        for(CashType type:denominations){
            int available=inventory.get(type);
            int reqd=Math.min(available,remaining/type.getValue());

            if(reqd>0){
                map.put(type,reqd);
                remaining-=reqd*type.getValue();
            }
        }
        if(remaining!=0) return new HashMap<>();
        for(CashType type:map.keySet()){
            int count=map.get(type);

            inventory.put(type,map.get(type)-count);

        }
        return map;
    }

}

//atm state pattern 
//interface atmstate isme saare fn isliye h kyuki diff states me diff operstions allowed hote h ya ni hote to we have to according check for each state in its concrete class

interface ATMState{
    void insertCard(Card card);
    void ejectCard();
    void authpin(int pinno);
    void selectOperation(TransactionType type);
    void performTransaction(double amount);
    void cancelTransaction();
    String getStateName();
}

public class ATMMachine{
    private ATMState currstate;
    private Card currcard;
    private Account curraccount;
    private final ATMInventory inventory;

    //factory pattern
    private final ATMStateFactory stateFactory;

    public ATMMachine(){
        inventory=new ATMInventory();
        stateFactory=ATMStateFactory.getInstance();
        currstate=stateFactory.createIdleState(this);
    }
    //state
    public void setState(ATMState state){
        this.currstate=state;
        System.out.println("ATM STATE -->" + state.getStateName());
    }
    public ATMState getCurrState(){
        return currstate;
    }

    //card
    public void setCurrCard(Card card){
        this.currcard=card;
    }
    public Card getCurrCard(){
        return currcard;
    }

    public void removeCard(){
        currcard=null;
        curraccount=null;

        System.out.println("Card Ejected");
    }

    //account
    public void setCurrentAccount(Account account) {
        this.curraccount = account;
    }

    public Account getCurrentAccount() {
        return curraccount;
    }
    public ATMInventory getInventory() {
            return inventory;
        }


        // ---------------- USER OPERATIONS ----------------

        public void insertCard(Card card) {
            currstate.insertCard(card);
        }

        public void authenticatePin(int pin) {
            currstate.authpin(pin);
        }

        public void selectOperation(TransactionType type) {
            currstate.selectOperation(type);
        }

        public void performTransaction(double amount) {
            currstate.performTransaction(amount);
        }

        public void cancel() {
            currstate.cancelTransaction();
        }

        public void ejectCard() {
            currstate.ejectCard();
        }

}

//concrete classes
public class IdleState implements ATMState{
    private final ATMMachine atm;

    public IdleState(ATMMachine atm){
        this.atm=atm;
    }
    @Override 
    public void insertCard(Card card){
        if(card==null){
            System.out.println("Invalid card");
            return;
        }
        atm.setCurrCard(card);
        System.out.println("Card Inserted");

        atm.setState(ATMFactory.getInstance().createHasCardState(atm));
        
    }
    @Override
    public void authpin(int pin){
        System.out.println("Please insert card first");

    }
    @Override
    public void selectOperation(TransactionType type){
        System.out.println("Please insert card first");

    }
    @Override
    public void performTransaction(double amount){
        System.out.println("Please insert card first");
    }
    @Override 
    public void cancelTransaction(){
        System.out.println("Nothing to cancel");
        
    }
    @Override
    public void ejectCard(){
        System.out.println("Nothing to eject");
    }

    @Override
    public String getStateName(){
        return "IDLE";
    }

}
static class HasCardState implements ATMState {

        private final ATMMachine atm;

        public HasCardState(ATMMachine atm) {
            this.atm = atm;
        }


        @Override
        public void insertCard(Card card) {
            System.out.println(
                    "Card already inserted."
            );
        }


        @Override
        public void authenticatePin(int pin) {

            Card card = atm.getCurrentCard();

            if (card == null) {
                System.out.println("No card.");
                return;
            }

            if (!card.validatePin(pin)) {

                System.out.println(
                        "Incorrect PIN."
                );

                return;
            }

            System.out.println(
                    "PIN authenticated successfully."
            );

            // In real system this would come from DB/bank
            Account account =
                    new Account(
                            card.getAccountNo(),
                            10000
                    );

            atm.setCurrentAccount(account);

            atm.setState(
                    ATMStateFactory
                            .getInstance()
                            .createSelectOperationState(atm)
            );
        }


        @Override
        public void selectOperation(TransactionType type) {
            System.out.println(
                    "Please authenticate PIN first."
            );
        }


        @Override
        public void performTransaction(double amount) {
            System.out.println(
                    "Please authenticate PIN first."
            );
        }


        @Override
        public void cancel() {
            atm.removeCard();

            atm.setState(
                    ATMStateFactory
                            .getInstance()
                            .createIdleState(atm)
            );
        }


        @Override
        public void ejectCard() {
            atm.removeCard();

            atm.setState(
                    ATMStateFactory
                            .getInstance()
                            .createIdleState(atm)
            );
        }


        @Override
        public String getStateName() {
            return "HAS_CARD";
        }
    }


    // =========================================================
    // SELECT OPERATION STATE
    // =========================================================

    static class SelectOperationState implements ATMState {

        private final ATMMachine atm;

        public SelectOperationState(ATMMachine atm) {
            this.atm = atm;
        }


        @Override
        public void insertCard(Card card) {
            System.out.println(
                    "Card already inserted."
            );
        }


        @Override
        public void authenticatePin(int pin) {
            System.out.println(
                    "PIN already authenticated."
            );
        }


        @Override
        public void selectOperation(TransactionType type) {

            if (type == null) {
                System.out.println(
                        "Invalid transaction."
                );
                return;
            }

            System.out.println(
                    "Selected operation: " + type
            );

            atm.setState(
                    ATMStateFactory
                            .getInstance()
                            .createTransactionState(
                                    atm,
                                    type
                            )
            );
        }


        @Override
        public void performTransaction(double amount) {
            System.out.println(
                    "Select operation first."
            );
        }


        @Override
        public void cancel() {
            atm.removeCard();

            atm.setState(
                    ATMStateFactory
                            .getInstance()
                            .createIdleState(atm)
            );
        }


        @Override
        public void ejectCard() {
            atm.removeCard();

            atm.setState(
                    ATMStateFactory
                            .getInstance()
                            .createIdleState(atm)
            );
        }


        @Override
        public String getStateName() {
            return "SELECT_OPERATION";
        }
    }


    // =========================================================
    // TRANSACTION STATE
    // =========================================================

    static class TransactionState implements ATMState {

        private final ATMMachine atm;
        private final TransactionType transactionType;


        public TransactionState(
                ATMMachine atm,
                TransactionType transactionType) {

            this.atm = atm;
            this.transactionType = transactionType;
        }


        @Override
        public void insertCard(Card card) {
            System.out.println(
                    "Transaction already in progress."
            );
        }


        @Override
        public void authenticatePin(int pin) {
            System.out.println(
                    "PIN already authenticated."
            );
        }


        @Override
        public void selectOperation(TransactionType type) {
            System.out.println(
                    "Transaction already selected."
            );
        }


        @Override
        public void performTransaction(double amount) {

            Account account =
                    atm.getCurrentAccount();

            if (account == null) {
                System.out.println(
                        "Account not found."
                );
                return;
            }


            // ---------------- CHECK BALANCE ----------------

            if (transactionType ==
                    TransactionType.CHECK_BALANCE) {

                System.out.println(
                        "Current Balance: ₹"
                                + account.getBalance()
                );

                return;
            }


            // ---------------- WITHDRAW ----------------

            if (transactionType ==
                    TransactionType.WITHDRAW) {

                if (amount <= 0) {
                    System.out.println(
                            "Invalid withdrawal amount."
                    );
                    return;
                }


                // Check account balance
                if (amount > account.getBalance()) {

                    System.out.println(
                            "Insufficient account balance."
                    );

                    return;
                }


                // Check ATM cash
                if (!atm.getInventory()
                        .hasSufficientCash((int) amount)) {

                    System.out.println(
                            "ATM does not have sufficient cash."
                    );

                    return;
                }


                // Check denomination
                Map<CashType, Integer> cash =
                        atm.getInventory()
                                .dispenseCash((int) amount);


                if (cash.isEmpty()) {

                    System.out.println(
                            "ATM cannot dispense exact amount."
                    );

                    return;
                }


                // Deduct money from account
                account.withdraw(amount);


                System.out.println(
                        "Withdrawal successful."
                );

                System.out.println(
                        "Cash dispensed: "
                                + cash
                );

                System.out.println(
                        "Remaining balance: ₹"
                                + account.getBalance()
                );
            }
        }


        @Override
        public void cancel() {

            System.out.println(
                    "Transaction cancelled."
            );

            atm.setState(
                    ATMStateFactory
                            .getInstance()
                            .createSelectOperationState(atm)
            );
        }


        @Override
        public void ejectCard() {

            atm.removeCard();

            atm.setState(
                    ATMStateFactory
                            .getInstance()
                            .createIdleState(atm)
            );
        }


        @Override
        public String getStateName() {
            return "TRANSACTION";
        }
    }


    class ATMStateFactory {

    public ATMState createIdleState(ATMMachine atm) {
        return new IdleState(atm);
    }

    public ATMState createHasCardState(ATMMachine atm) {
        return new HasCardState(atm);
    }

    public ATMState createSelectOperationState(ATMMachine atm) {
        return new SelectOperationState(atm);
    }

    public ATMState createTransactionState(
            ATMMachine atm,
            TransactionType type) {

        return new TransactionState(atm, type);
    }
}

    // =========================================================
    // MAIN
    // =========================================================

    public static void main(String[] args) {

        ATMMachine atm = new ATMMachine();


        // Add cash to ATM

        atm.getInventory()
                .addCash(CashType.BILL_100, 50);

        atm.getInventory()
                .addCash(CashType.BILL_50, 50);

        atm.getInventory()
                .addCash(CashType.BILL_20, 50);

        atm.getInventory()
                .addCash(CashType.BILL_10, 50);


        System.out.println(
                "Total ATM Cash: ₹"
                        + atm.getInventory()
                        .getTotalCash()
        );


        // ---------------- TRANSACTION ----------------

        Card card =
                new Card(
                        "123456789",
                        1234,
                        "ACC001"
                );


        // 1. Insert card

        atm.insertCard(card);


        // 2. Authenticate

        atm.authenticatePin(1234);


        // 3. Select operation

        atm.selectOperation(
                TransactionType.WITHDRAW
        );


        // 4. Withdraw

        atm.performTransaction(370);


        // 5. Eject card

        atm.ejectCard();


        // ---------------- SECOND TRANSACTION ----------------

        System.out.println("\n--- Balance Check ---");


        atm.insertCard(card);

        atm.authenticatePin(1234);

        atm.selectOperation(
                TransactionType.CHECK_BALANCE
        );

        atm.performTransaction(0);

        atm.ejectCard();
    }
}
