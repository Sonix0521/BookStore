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
@Path("/books")
public class BookResource 
{
    
    private static final ConcurrentHashMap<String, Books> bookList = new ConcurrentHashMap<>();
    public static final ConcurrentHashMap<String, Authors> extractedAuthorList = AuthorResource.getAuthorList();
    
    
    
    public static ConcurrentHashMap<String, Books> getBookList()
    {
        return bookList;
    }
        
        
        
    static 
    {
        DefaultDataStore.addDefaultBooksToList(bookList, extractedAuthorList);
    }
    
    
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllBooks()
    {
        if (bookList.isEmpty())
        {
            // return Response.status(Response.Status.NOT_FOUND).entity("NO BOOKS AVAILABLE.").build();
            throw new BookNotFoundException("NO BOOKS AVAILABLE.");
        }
        
        ArrayList<Books> allBooks = new ArrayList<>(bookList.values());
        return Response.ok(allBooks).build();
    }
    
    
    
    @GET
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getBookById(@PathParam("id") String bookId)
    {
        Books book = bookList.get(bookId);
        
        if(book != null)
        {
            return Response.ok(book).build();
        }
        else
        {
            throw new BookNotFoundException("BOOK NOT FOUND. \nINVALID BOOK ID : " + bookId);
            // return Response.status(Response.Status.NOT_FOUND).entity("BOOK NOT FOUND. \nINVALID BOOK ID : " + bookId).build();
        }
    }
    
    
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createBook(Books newBookDetails)
    {
        
        Response validationResponse = BookstoreValidations.BookValidator.validateBookDetails(newBookDetails, null, "POST"); // Passing `null` for bookId in POST

        if (validationResponse.getStatus() != Response.Status.OK.getStatusCode()) {
            return validationResponse; // If validation failed, return the Response with errors
        }
        
        String newAuthorId = newBookDetails.getBookAuthorId();
        if(!extractedAuthorList.containsKey(newAuthorId))
        {
            throw new AuthorNotFoundException("AUTHOR NOT FOUND. INVALID AUTHOR ID : " + newAuthorId);
            // return Response.status(Response.Status.NOT_FOUND).entity("AUTHOR NOT FOUND. INVALID AUTHOR ID : " + newAuthorId).build();
        }
        
        Response duplicateResponse = BookstoreValidations.BookValidator.checkForDuplicateBook(newBookDetails, "POST");
        if (duplicateResponse != null) 
        {
            return duplicateResponse;
        }
        
        Authors authorDetails = extractedAuthorList.get(newAuthorId);
        newBookDetails.setBookAuthorName(authorDetails.getAuthorName());
        
        String newBookId = UUID.randomUUID().toString().substring(0,8);
        newBookDetails.setBookId(newBookId);
        
        bookList.put(newBookId, newBookDetails);
        
        ApiResponse response = new ApiResponse("NEW BOOK SUCCESSFULLY REGISTERED", newBookDetails);
        return Response.status(Response.Status.CREATED).entity(response).build();
    }
    
    
    
    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateBookDetails(@PathParam("id") String bookId, Books toBeUpdatedBookDetails)
    {
        
        Books existingBookDetails = bookList.get(bookId);        
        if(existingBookDetails == null)
        {
            throw new BookNotFoundException("BOOK NOT FOUND. \nINVALID BOOK ID : " + bookId);
            // return Response.status(Response.Status.NOT_FOUND).entity("BOOK NOT FOUND. \nINVALID BOOK ID : " + bookId).build();
        }
        
        Response validationResponse = BookstoreValidations.BookValidator.validateBookDetails(toBeUpdatedBookDetails, existingBookDetails, "PUT"); // bookId is included
        if (validationResponse.getStatus() != Response.Status.OK.getStatusCode()) {
            return validationResponse;
        }
        
        Response duplicateResponse = BookstoreValidations.BookValidator.checkForDuplicateBook(toBeUpdatedBookDetails, "PUT");
        if (duplicateResponse != null) 
        {
            String existingErrorMessage = (String) duplicateResponse.getEntity();

            throw new InvalidInputException(existingErrorMessage + "\nPLEASE CHANGE VALUES TO UPDATE");
            // return Response.status(Response.Status.CONFLICT).entity(existingErrorMessage + "\nPLEASE CHANGE VALUES TO UPDATE").build();
        }
        
        StringBuilder responseMessage = BookstoreValidations.BookValidator.generateUpdateMessage(toBeUpdatedBookDetails, existingBookDetails);
        
        ApiResponse response = new ApiResponse(responseMessage.toString(), existingBookDetails);

        return Response.ok(response).build();
    }
    
    
    
    @DELETE
    @Path("/{id}")
    public Response deleteCustomer(@PathParam("id") String bookId)
    {
        if(bookList.remove(bookId) != null)
        {
            return Response.status(Response.Status.OK).entity("BOOK ID : " + bookId + " DELETED").build();
        }
        else
        {
            throw new BookNotFoundException("BOOK NOT FOUND. \nINVALID BOOK ID : " + bookId);
            // return Response.status(Response.Status.NOT_FOUND).entity("BOOK NOT FOUND. \nINVALID BOOK ID : " + bookId).build();
        }
    }
    
    
    
}
