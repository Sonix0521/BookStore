/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.bookstore.bookstore.exceptions;

/**
 *
 * @author SANIDU WICKRAMASIGHE
 */
public class OrderNotFoundException extends RuntimeException
{   
    public OrderNotFoundException(String message) 
    {
        super(message);
    }
}