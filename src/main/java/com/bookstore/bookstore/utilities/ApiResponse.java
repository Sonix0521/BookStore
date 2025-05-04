/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.bookstore.bookstore.utilities;

/**
 *
 * @author SANIDU WICKRAMASIGHE
 */
public class ApiResponse 
{
    
    private String message;
    private Object data;
    
    public ApiResponse(String message, Object data)
    {
        this.message = message;
        this.data = data;
    }

    public String getMessage() 
    {
        return message;
    }
    
    public Object getData() 
    {
        return data;
    }
    
    
}
