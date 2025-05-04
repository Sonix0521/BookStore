/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.bookstore.bookstore.resources;

import com.bookstore.bookstore.exceptions.AuthorNotFoundException;
import com.bookstore.bookstore.exceptions.BookNotFoundException;
import com.bookstore.bookstore.exceptions.InvalidInputException;
import com.bookstore.bookstore.model.Authors;
import com.bookstore.bookstore.model.Books;
import com.bookstore.bookstore.utilities.ApiResponse;
import com.bookstore.bookstore.utilities.BookstoreValidations;
import com.bookstore.bookstore.utilities.DefaultDataStore;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;



/**
 *
 * @author SANIDU WICKRAMASIGHE
 */
@Path("/authors")
public class AuthorResource 
{
    
    private static final ConcurrentHashMap<String, Authors> extractedAuthorList = DefaultDataStore.getAuthorList();
    private static final ConcurrentHashMap<String, Books> extractedBookList = DefaultDataStore.getBookList();
    
    

    static 
    {
        DefaultDataStore.addDefaultAuthorsToList(extractedAuthorList, extractedBookList);
    }
    
 
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllAuthors()
    {
        if (extractedAuthorList.isEmpty())
        {
            throw new AuthorNotFoundException("NO AUTHORS FOUND");
            // return Response.status(Response.Status.NOT_FOUND).entity("NO AUTHORS FOUND").build();
        }
        
        ArrayList<Authors> allAuthors = new ArrayList<>(extractedAuthorList.values());
        return Response.ok(allAuthors).build();
    }
    
    
    
    @GET
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAuthorById(@PathParam("id") String authorId)
    {
        Authors author = extractedAuthorList.get(authorId);
        
        if(author != null)
        {
            return Response.ok(author).build();
        }
        else
        {
            throw new AuthorNotFoundException("AUTHOR NOT FOUND. \nINVALID AUTHOR ID : " + authorId);
            // return Response.status(Response.Status.NOT_FOUND).entity("AUTHOR NOT FOUND. \nINVALID AUTHOR ID : " + authorId).build();
        }
    }
    
    
    
    @GET
    @Path("/{id}/authorsbooks")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAuthorBooksById(@PathParam("id") String authorId) 
    {
        Authors author = extractedAuthorList.get(authorId);

        if (author == null) 
        {
            throw new AuthorNotFoundException("AUTHOR NOT FOUND. INVALID AUTHOR ID: " + authorId);
            // return Response.status(Response.Status.NOT_FOUND).entity("AUTHOR NOT FOUND. INVALID AUTHOR ID: " + authorId).build();
        }

        ArrayList<Books> authorsBooks = extractedBookList.values().stream().filter(book -> book.getBookAuthorId().equals(authorId)).collect(Collectors.toCollection(ArrayList::new)); // Explicitly collecting as ArrayList

        if (authorsBooks.isEmpty()) 
        {
            throw new BookNotFoundException("NO BOOKS FOUND FOR AUTHOR ID: " + authorId);
            // return Response.status(Response.Status.NOT_FOUND).entity("NO BOOKS FOUND FOR AUTHOR ID: " + authorId).build();
        }

        return Response.ok(authorsBooks).build();
    }
    
    
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createCustomer(Authors newAuthorDetails)
    {   
        
        StringBuilder errorMessage = BookstoreValidations.AuthorValidator.validateAuthorDetails(newAuthorDetails, "POST");
        if(errorMessage.length() > 0)
        {
            return Response.status(Response.Status.BAD_REQUEST).entity(errorMessage.toString()).build();
        }
        
        Response duplicateResponse = BookstoreValidations.AuthorValidator.checkForDuplicateAuthor(newAuthorDetails);
        if (duplicateResponse != null) 
        {
            return duplicateResponse;
        }
        
        String newAuthorId = UUID.randomUUID().toString().substring(0,8);
        
        newAuthorDetails.setAuthorId(newAuthorId);
        
        extractedAuthorList.put(newAuthorId, newAuthorDetails);
        
        ApiResponse response = new ApiResponse("NEW AUTHOR SUCCESSFULLY REGISTERED", newAuthorDetails);
        return Response.status(Response.Status.CREATED).entity(response).build();
    }
    
    

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateAuthorDetails(@PathParam("id") String authorId, Authors updatedAuthorDetails)
    {
        
        Authors existingAuthorDetails = extractedAuthorList.get(authorId);
        if(existingAuthorDetails == null)
        {
            throw new AuthorNotFoundException("AUTHOR NOT FOUND. INVALID AUTHOR ID: " + authorId);
            // return Response.status(Response.Status.NOT_FOUND).entity("AUTHOR NOT FOUND. \nINVALID AUTHOR ID : " + authorId).build();
        }
        
        
        StringBuilder errorMessage = BookstoreValidations.AuthorValidator.validateAuthorDetails(updatedAuthorDetails, "PUT");
        if(errorMessage.length() > 0)
        {
            return Response.status(Response.Status.BAD_REQUEST).entity(errorMessage.toString()).build();
        }
        
        Response duplicateResponse = BookstoreValidations.AuthorValidator.checkForDuplicateAuthor(updatedAuthorDetails);
        if (duplicateResponse != null) 
        {
            String existingErrorMessage = (String) duplicateResponse.getEntity();

            throw new InvalidInputException(existingErrorMessage + "\nPLEASE CHANGE VALUES TO UPDATE");
            // return Response.status(Response.Status.CONFLICT).entity(existingErrorMessage + "\nPLEASE CHANGE VALUES TO UPDATE").build();
        }
        
        StringBuilder responseMessage = BookstoreValidations.AuthorValidator.responseMessageForUpdates(updatedAuthorDetails, existingAuthorDetails);
        
        ApiResponse response = new ApiResponse(responseMessage.toString(), existingAuthorDetails);

        return Response.ok(response).build();
    }    
    
    
    
    @DELETE
    @Path("/{id}")
    public Response deleteAuthor(@PathParam("id") String authorId)
    {
        if(extractedAuthorList.remove(authorId) != null)
        {
            return Response.status(Response.Status.OK).entity("AUTHOR ID : " + authorId + " DELETED").build();
        }
        else
        {
            throw new AuthorNotFoundException("AUTHOR ID : " + authorId + " NOT FOUND");
            // return Response.status(Response.Status.NOT_FOUND).entity("AUTHOR ID : " + authorId + " NOT FOUND").build();
        }
    }
    
}
