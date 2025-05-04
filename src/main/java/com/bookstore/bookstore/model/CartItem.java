/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.bookstore.bookstore.model;

/**
 *
 * @author SANIDU WICKRAMASIGHE
 */
public class CartItem 
{
    
    private String bookId;
    private String bookTitle;
    private int bookQuantity;
    private double totalPriceForBookItem;
    
    public CartItem() {}
    
    public CartItem(String bookId, int itemQuantity, Books bookObj)
    {
        this.bookId = bookId;
        this.bookTitle = bookObj.getBookTitleByBookId(bookId);
        this.bookQuantity = itemQuantity;
        this.totalPriceForBookItem = itemQuantity * bookObj.getBookPrice();
    }
    

    public void setBookId(String bookId) 
    {
        this.bookId = bookId;
    }
    
    public String getBookId() 
    {
        return bookId;
    }

    public String getBookTitle() 
    { 
        return bookTitle; 
    }
    
    public void setBookQuantity(int bookQuantity) 
    {
        this.bookQuantity = bookQuantity;
    }

    public int getBookQuantity() 
    {
        return bookQuantity;
    }

    public void setTotalPriceForBookItem(double totalPriceForBookItem) 
    {
        this.totalPriceForBookItem = totalPriceForBookItem;
    }

    public double getTotalPriceForBookItem() 
    {
        return totalPriceForBookItem;
    }
    
    @Override
    public String toString() 
    {
        return "CartItem {" +
                " book id : '" + bookId +
                "' quantity : '" + bookQuantity + 
                "' }"; 
    }
    
    
}
