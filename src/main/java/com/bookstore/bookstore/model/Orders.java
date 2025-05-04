/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.bookstore.bookstore.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 *
 * @author SANIDU WICKRAMASIGHE
 */
public class Orders 
{
    private String orderId;
    private String customerId;
    private double totalOrderPrice;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd | HH:mm:ss")
    private Date orderDate;
    private List<CartItem> orderItems;
    
    public Orders(Cart cart) 
    {
        this.orderId = UUID.randomUUID().toString().substring(0, 8);
        this.customerId = cart.getCustomerId();
        this.orderItems = new ArrayList<>(cart.getCartItemsList());
        this.totalOrderPrice = cart.getTotalCartPrice();
        this.orderDate = new Date(); // Set current date automatically
    }

    
    public String getOrderId() 
    {
        return orderId;
    }

    public void setOrderId(String orderId) 
    {
        this.orderId = orderId;
    }

    public String getCustomerId() 
    {
        return customerId;
    }

    public void setCustomerId(String customerId) 
    {
        this.customerId = customerId;
    }

    public List<CartItem> getOrderItems() 
    {
        return orderItems;
    }

    public void setOrderItems(List<CartItem> orderItems) 
    {
        this.orderItems = orderItems;
    }

    public double getTotalOrderPrice() 
    {
        return totalOrderPrice;
    }

    public void setTotalOrderPrice(double totalOrderPrice) 
    {
        this.totalOrderPrice = totalOrderPrice;
    }

    public Date getOrderDate() 
    {
        return orderDate;
    }

    public void setOrderDate(Date orderDate) 
    {
        this.orderDate = orderDate;
    }

    @Override
    public String toString() 
    {
        return "Order { " +
                " orderId = '" + orderId +
                "' customerId = '" + customerId +
                "' totalOrderPrice = '" + totalOrderPrice +
                "' orderDate = '" + orderDate +
                "' orderItems = '" + orderItems +
                '}';
    }
}
