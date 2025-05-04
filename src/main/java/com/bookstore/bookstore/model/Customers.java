/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.bookstore.bookstore.model;

import java.util.UUID;

/**
 *
 * @author SANIDU WICKRAMASIGHE
 */
public class Customers 
{
    
    private String customerId;
    private String customerName;
    private String customerEmail;
    private String customerPassword;
    
    public Customers(String name, String email, String password)
    {
        this.customerId = UUID.randomUUID().toString().substring(0,8);;
        this.customerName = name;
        this.customerEmail = email;
        this.customerPassword = password;
    }
    
    public Customers() {}
    
    public void setCustomerId(String customerId)
    {
        this.customerId = customerId;
    }
    
    public String getCustomerId() 
    {
        return customerId;
    }

    public void setCustomerName(String customerName) 
    {
        this.customerName = customerName;
    }

    public String getCustomerName() 
    {
        return customerName;
    }

    public void setCustomerEmail(String customerEamil) 
    {
        this.customerEmail = customerEamil;
    }
    
    public String getCustomerEmail() 
    {
        return customerEmail;
    }

    public void setCustomerPassword(String customerPassword) 
    {
        this.customerPassword = customerPassword;
    }

    public String getCustomerPassword() 
    {
        return customerPassword;
    }

    @Override
    public String toString() 
    {
        return "Customer {" + 
                " id : '" + customerId + 
                "' name : " + customerName + 
                "' email : " + customerEmail + 
                "' password : {PROTECTED}" ;
    }
    
}
