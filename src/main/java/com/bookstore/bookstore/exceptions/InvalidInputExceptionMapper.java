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
public class InvalidInputExceptionMapper implements ExceptionMapper<InvalidInputException>
{
    @Override
    public Response toResponse(InvalidInputException ex) 
    {        
        ErrorResponse error = new ErrorResponse("INVALID INPUT", ex.getMessage());

        return Response.status(Response.Status.BAD_REQUEST).entity(error).type(MediaType.APPLICATION_JSON).build();    
    }
    
}
