/*
# Requirements
Add books to library.
Register members.
Search books by title or author.
Issue a book to a member.
Return a book.
Track book availability.
har book type ki bookcopy hogi so book se status hata kr bookcopy me dala h takki physical copy ka status rh available ya not
*/

// class and entities
/*
1. book
2. member
3. library

 */

import java.util.*;

//enum BookAvaiability
enum BookStatus{
    AVAILABLE,NOT_AVAILABLE;
}

//Book class
public class Book{
    private final int bookid;
    private final String booktitle;
    private final String author;
    //private BookStatus status;

    public Book(int bookid,String booktitle,String author){
        this.bookid=bookid;
        this.booktitle=booktitle;
        this.author=author;
        
    }
    public int getBookId(){
        return bookid;
    }
    public String getBookTitle(){
        return booktitle;
    }
    public String getAuthor(){
        return author;
    }
    

}
public class BookCopy{
    private int copyId;
    private Book book;
    private BookStatus status;

    public BookCopy(int copyId,Book book){
        this.copyId=copyId;
        this.book=book;
        this.status=BookStatus.AVAILABLE;
    }
    public int getCopyId(){
        return copyId;
    }
    public Book getBook(){
        return book;
    }
    public BookStatus getStatus(){
        return status;
    }
    public void setStatus(BookStatus status){
        this.status=status;
    }
}

public class BookFactory{
    public static Book createBook(int id,String title,String author){
        return new Book(id,title,author);
    }
}

// Member class
public class Member{
    private final int memberid;
    private final String membername;
    private List<BookCopy> borrowedbooks;
    private static final int MAX_BOOKS=5;

    public Member(int memberid,String name){
        this.memberid=memberid;
        this.membername=name;
        borrowedbooks=new ArrayList<>();
    }
    public int getMemberId(){
        return memberid;
    }
    public String getMemberName(){
        return membername;
    }
    public List<BookCopy> getBorrowedBooks(){
        return borrowedbooks;
    }
    public boolean canBorrow(){
        return borrowedbooks.size()<MAX_BOOKS;
    }
    public void borrowBook(BookCopy copy){
        
            borrowedbooks.add(copy);
        
    }
    public void returnBook(BookCopy copy){
        borrowedbooks.remove(copy);
    }

}

//library class
public class Library{
    private List<Book> books;
    private List<BookCopy> copies;
    private List<Member> members;

    private int nextCopyId=1;

    public Library(){
        books=new ArrayList<>();
        members=new ArrayList<>();
        copies=new ArrayList<>();
    }
    public void addBook(Book book){
        books.add(book);
        System.out.println("Book added");
    }
    public void addCopies(Book book,int count){
        for(int i=0;i<count;i++){
            BookCopy copy=new BookCopy(nextCopyId++,book);
            copies.add(copy);
        }
    }
    public void addMember(Member member){
        members.add(member);
        System.out.println("Member added");
    }

    public BookCopy findAvailableCopy(int bookid){
        for(BookCopy bookcopy:copies){
            if(bookcopy.getBook().getBookId()==bookid && bookcopy.getStatus()==BookStatus.AVAILABLE){
                //System.out.println(book.getBookId() + " " + book.getBookTitle() + " " + book.getAuthor() + " " + book.getStatus())
                //;
                return bookcopy;
            }

        }
        return null;

    }
    public synchronized void issueBook(int bookid,int memberid){
        Book selectedBook=null;
        Member selectedMember=null;

        for(Book book:books){
            if(book.getBookId()==bookid){
                selectedBook=book;
                break;
            }
        }
        for(Member member:members){
            if(member.getMemberId()==memberid){
                selectedMember=member;
                break;
            }
        }
        
        if(selectedBook==null || selectedMember==null){
            System.out.println("Book or member not found");
            return;
        }
        if (!selectedMember.canBorrow()) {

        System.out.println(
                "Borrowing limit reached"
        );

        return;
        }

        BookCopy selectedCopy = null;

    for (BookCopy copy : copies) {

        if (copy.getBook().getBookId() == bookid
                && copy.getStatus()
                == BookStatus.AVAILABLE) {

            selectedCopy = copy;
            break;
        }
    }

    if (selectedCopy == null) {

        System.out.println(
                "Selected book is already borrowed"
        );

        return;
    }

        selectedCopy.setStatus(BookStatus.NOT_AVAILABLE);
        selectedMember.borrowBook(selectedCopy);

        System.out.println("Book borrowed successfully");
    }
    public synchronized void returnBook(
        int copyid,
        int memberid) {

    Member selectedMember = null;

    // Find Member
    for (Member member : members) {

        if (member.getMemberId() == memberid) {

            selectedMember = member;
            break;
        }
    }

    if (selectedMember == null) {

        System.out.println("Member not found");
        return;
    }

    // Find Borrowed Copy
    BookCopy selectedCopy = null;

    for (BookCopy copy :
            selectedMember.getBorrowedBooks()) {

        if (copy.getCopyId() == copyid) {

            selectedCopy = copy;
            break;
        }
    }

    if (selectedCopy == null) {

        System.out.println(
                "Copy not found"
        );

        return;
    }

    // Return Book Copy
    selectedMember.returnBook(selectedCopy);

    selectedCopy.setStatus(BookStatus.AVAILABLE);

    System.out.println(
            "Book returned successfully"
    );


}
public class Main {

    public static void main(String[] args) {

        Library library = new Library();

        Book book1 = new Book(
                1,
                "BOOK1",
                "James Paul"
        );

        Book book2 = new Book(
                2,
                "BOOK2",
                "William Shakespeare"
        );

        Member member1 = new Member(
                101,
                "Dhruv"
        );

        Member member2 = new Member(
                102,
                "Yash"
        );

        library.addBook(book1);
        library.addBook(book2);

        // Add Multiple Copies
        library.addCopies(book1, 2);
        library.addCopies(book2, 2);

        library.addMember(member1);
        library.addMember(member2);

        library.searchBook("BOOK2");

        // Issue Book
        library.issueBook(1, 101);

        // Return Copy
        library.returnBook(1, 101);
    }
}
