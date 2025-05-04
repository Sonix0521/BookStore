package com.bookstore.bookstore.model;

import java.util.ArrayList;

/**
 *
 * @author SANIDU WICKRAMASIGHE
 */
public class Cart 
{
    
    private String customerId;
//    private String customerName;
    private double totalCartPrice;
    private ArrayList<CartItem> cartItemsList;
    
    public Cart() 
    {
        this.cartItemsList = new ArrayList<>();
        this.totalCartPrice = 0.0;
    }
    
    public Cart(Customers customer)
    {
        this.customerId = customer.getCustomerId();
//        this.customerName = customer.getCustomerName();
        this.totalCartPrice = 0.0;
        this.cartItemsList = new ArrayList<>();
    }

    public void setCustomerId(String customerId) 
    {
        this.customerId = customerId;
    }

    public String getCustomerId() 
    {
        return customerId;
    }

    public void setCartItemsList(ArrayList<CartItem> cartItems) 
    {
        this.cartItemsList = cartItems;
        calculateTotalCartPrice();
    }
    
    public ArrayList<CartItem> getCartItemsList() 
    {
        return cartItemsList;
    }
    
    public void calculateTotalCartPrice()
    {
        double total = 0.0;
        for (CartItem item : cartItemsList)
        {
            total += item.getTotalPriceForBookItem();
        }
        this.totalCartPrice = total;
    }

    public void setTotalCartPrice(double totalCartPrice) 
    {
        this.totalCartPrice = totalCartPrice;
    }

    public double getTotalCartPrice() 
    {
        return totalCartPrice;
    }

    @Override
    public String toString() 
    {
        return "Author {" + 
                " customer id : '" + customerId +
                "' biography : '" + cartItemsList + 
                "' }";
    }
    
}
