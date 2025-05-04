package com.bookstore.bookstore.model;

import com.bookstore.bookstore.utilities.DefaultDataStore;
import java.util.Map;
import java.util.UUID;

/**
 *
 * @author SANIDU WICKRAMASIGHE
 */
public class Books 
{
    private String bookId;
    private String bookISBN; 
    private String bookTitle;
    private String bookAuthorId;
    private String bookAuthorName;
    private int bookPublishedYear;
    private double bookPrice;
    private int bookStockQuantity;
    
    public Books() {}
    
    public Books(String title, Authors authorDetails, String bookISBN, int publishedYear, double price, int stockQuantity) 
    {
        this.bookId = UUID.randomUUID().toString().substring(0,8);
        this.bookISBN = bookISBN;
        this.bookTitle = title;
        this.bookAuthorId = authorDetails.getAuthorId();
        this.bookAuthorName = authorDetails.getAuthorName();
        this.bookPublishedYear = publishedYear;
        this.bookPrice = price;
        this.bookStockQuantity = stockQuantity;
    }

    public void setBookId(String bookId) 
    {
        this.bookId = bookId;
    }
    
    public String getBookId() 
    {
        return bookId;
    }
    
    public void setBookISBN(String bookISBN) 
    {
        this.bookISBN = bookISBN;
    }
    
    public String getBookISBN() 
    {
        return bookISBN;
    }

    public void setBookTitle(String bookTitle) 
    {
        this.bookTitle = bookTitle;
    }

    public String getBookTitle() 
    {
        return bookTitle;
    }
    
    public String getBookTitleByBookId(String bookId)
    {
        Map<String, Books> extractedBookList = DefaultDataStore.getBookList();
        for (Books bookItem : extractedBookList.values())
        {
            if(bookItem.getBookId().equals(bookId))
            {
                return bookItem.getBookTitle();
            }
        }
        return "BOOK NOT FOUND";
    }

    public void setBookAuthorId(String bookAuthorId) 
    {
        this.bookAuthorId = bookAuthorId;
    }

    public String getBookAuthorId() 
    {
        return bookAuthorId;
    }

    public void setBookAuthorName(String bookAuthorName)
    {
        this.bookAuthorName = bookAuthorName;
    }

    public String getBookAuthorName() 
    {
        return bookAuthorName;
    }

    public void setBookPublishedYear(int bookPublishedYear) 
    {
        this.bookPublishedYear = bookPublishedYear;
    }

    public int getBookPublishedYear() 
    {
        return bookPublishedYear;
    }
    
    public void setBookPrice(double bookPrice) 
    {
        this.bookPrice = bookPrice;
    }

    public double getBookPrice() 
    {
        return bookPrice;
    }

    public void setBookStockQuantity(int bookStockQuantity) 
    {
        this.bookStockQuantity = bookStockQuantity;
    }

    public int getBookStockQuantity() 
    {
        return bookStockQuantity;
    }

    @Override
    public String toString() 
    {
        return "Book {" + 
                " ISBN : '" + bookISBN +
                "' title : " + bookTitle +
                "' author : '" + bookAuthorId + 
                "' published year : '" + bookPublishedYear + 
                "' price : '" + bookPrice + 
                "' stock : " + bookStockQuantity + 
                "' }";
    }
    
}
