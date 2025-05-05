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
            throw new AuthorNotFoundException("NO AUTHORS AVAILABLE.");
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
        }

        ArrayList<Books> authorsBooks = extractedBookList.values().stream().filter(book -> book.getBookAuthorId().equals(authorId)).collect(Collectors.toCollection(ArrayList::new)); // Explicitly collecting as ArrayList

        if (authorsBooks.isEmpty()) 
        {
            throw new BookNotFoundException("NO BOOKS FOUND FOR AUTHOR ID: " + authorId);
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
        
        newAuthorDetails.setAuthorId();
        
        extractedAuthorList.put(newAuthorDetails.getAuthorId(), newAuthorDetails);
        
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
            throw new AuthorNotFoundException("AUTHOR NOT FOUND. \nINVALID AUTHOR ID : " + authorId);
        }
    }



    @DELETE
    @Path("/DELETE-ALL-EXISTING-AUTHORS")
    public Response deleteAllAuthors()
    {
        if (!extractedAuthorList.isEmpty())
        {
            extractedAuthorList.clear();
            return Response.status(Response.Status.OK).entity("ALL AUTHORS SUCCESSFULLY DELETED.").build();
        }
        else
        {
            throw new AuthorNotFoundException("NO AUTHORS AVAILABLE.");
        }
    }
    
}
