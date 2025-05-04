/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.bookstore.bookstore.exceptions;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 *
 * @author SANIDU WICKRAMASIGHE
 */
@Provider
public class CartNotFoundExceptionMapper implements ExceptionMapper<CartNotFoundException>
{ 
    @Override
    public Response toResponse(CartNotFoundException ex) 
    {
        ErrorResponse error = new ErrorResponse("CART NOT FOUND", ex.getMessage());
        return Response.status(Response.Status.NOT_FOUND).entity(error).type(MediaType.APPLICATION_JSON).build();    
    }
    
}