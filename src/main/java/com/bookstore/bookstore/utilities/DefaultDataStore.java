package com.bookstore.bookstore.utilities;

import com.bookstore.bookstore.model.Authors;
import com.bookstore.bookstore.model.Books;
import com.bookstore.bookstore.model.Cart;
import com.bookstore.bookstore.model.Customers;
import com.bookstore.bookstore.model.Orders;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author SANIDU WICKRAMASIGHE
 */
public class DefaultDataStore 
{
    private static final ConcurrentHashMap<String, Authors> authorList = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, Books> bookList = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, Customers> customerList = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, Cart> cartListForCustomersCartItems = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<String, Orders> customersOrdersList = new ConcurrentHashMap<>();



    public static ConcurrentHashMap<String, Authors> getAuthorList()
    {
        return authorList;
    }
    public static ConcurrentHashMap<String, Books> getBookList()
    {
        return bookList;
    }
    public static ConcurrentHashMap<String, Customers> getCustomerList()
    {
        return customerList;
    }
    public static ConcurrentHashMap<String, Cart> getCartItemsList()
    {
        return cartListForCustomersCartItems;
    }
    public static ConcurrentHashMap<String, Orders> getOrdersList()
    {
        return customersOrdersList;
    }

    
    
    public static void addDefaultAuthorsToList(Map<String, Authors> defaultAuthorList, Map<String, Books> defaultBookList)
    {

        Authors author1 = new Authors("F. Scott Fitzgerald", "American novelist best known for 'The Great Gatsby', a classic of 20th-century literature.");
        Authors author2 = new Authors("George Orwell", "English novelist famous for his dystopian works '1984' and 'Animal Farm'.");
        Authors author3 = new Authors("Harper Lee", "American author best known for 'To Kill a Mockingbird', a novel that explores racial injustice in the Deep South.");

        defaultAuthorList.put(author1.getAuthorId(), author1);
        defaultAuthorList.put(author2.getAuthorId(), author2);
        defaultAuthorList.put(author3.getAuthorId(), author3);
        
        Books book1 = new Books("The Great Gatsby", author1, "978-0743273565", 1925, 10.99, 40);
        Books book2 = new Books("1984", author2, "978-0451524935", 1949, 9.99, 50);
        Books book3 = new Books("To Kill a Mockingbird", author3, "978-0061120084", 1960, 14.99, 35);
        
        defaultBookList.put(book1.getBookId(), book1);
        defaultBookList.put(book2.getBookId(), book2);
        defaultBookList.put(book3.getBookId(), book3);
        
    }
    


    public static void addDefaultCustomersToList(Map<String, Customers> defaultCustomerList)
    {

       Customers customer1 = new Customers("Michael Johnson", "michael.johnson@example.com", "MJ987");
       Customers customer2 = new Customers("Emma Williams", "emma.williams@example.com", "EW456");
       Customers customer3 = new Customers("James Smith", "james.smith@example.com", "JS123");
       
       defaultCustomerList.put(customer1.getCustomerId(), customer1);
       defaultCustomerList.put(customer2.getCustomerId(), customer2);
       defaultCustomerList.put(customer3.getCustomerId(), customer3);

    }
    


    public static void addDefaultBooksToList(Map<String, Books> defaultBookList, Map<String, Authors> defaultAuthorList)
    {   

        Authors author1 = new Authors("Sir Arthur Conan Doyle", "Scottish writer best known for creating the legendary detective Sherlock Holmes.");
        Authors author2 = new Authors("J.K. Rowling", "British author famous for the Harry Potter series, which has captivated millions worldwide.");

        defaultAuthorList.put(author1.getAuthorId(), author1);
        defaultAuthorList.put(author2.getAuthorId(), author2);
        
        Books book1 = new Books("A Study in Scarlet", author1, "978-1514697077", 1887, 9.99, 30); // First Sherlock Holmes novel
        Books book2 = new Books("Harry Potter and the Sorcerer’s Stone", author2, "978-0590353427", 1997, 12.99, 50); // First Harry Potter book

        defaultBookList.put(book1.getBookId(), book1);
        defaultBookList.put(book2.getBookId(), book2);

    }
    
}
