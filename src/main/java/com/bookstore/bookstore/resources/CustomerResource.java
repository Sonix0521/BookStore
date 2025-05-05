package com.bookstore.bookstore.resources;

import com.bookstore.bookstore.exceptions.CustomerNotFoundException;
import com.bookstore.bookstore.utilities.ApiResponse;
import com.bookstore.bookstore.model.Customers;
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
@Path("/customers")
public class CustomerResource 
{

    private static final ConcurrentHashMap<String, Customers> extractedCustomerList = DefaultDataStore.getCustomerList();
    
    
    
    static 
    {
        DefaultDataStore.addDefaultCustomersToList(extractedCustomerList);
    }


    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllCustomerDetails()
    {
        if (extractedCustomerList.isEmpty() || extractedCustomerList == null)
        {
            throw new CustomerNotFoundException("NO CUSTOMERS AVAILABLE");
        }
        
        ArrayList<Customers> allCustomers = new ArrayList<>(extractedCustomerList.values());
        return Response.ok(allCustomers).build();
    }
    
    
    
    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCustomerById(@PathParam("id") String customerId)
    {
        Customers customer = extractedCustomerList.get(customerId);
        
        if(customer != null)
        {
            return Response.ok(customer).build();
        }
        else
        {
            throw new CustomerNotFoundException("CUSTOMER NOT FOUND. \nINVALID CUSTOMER ID : " + customerId);
        }
    }
    
    
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createCustomer(Customers newCustomerDetails)
    {   
        
        StringBuilder errorMessage = BookstoreValidations.CustomerValidator.validateCustomerDetails(newCustomerDetails, "POST");
        if(errorMessage.length() > 0)
        {
            return Response.status(Response.Status.BAD_REQUEST).entity(errorMessage.toString()).build();
        }

        
        Response duplicateResponse = BookstoreValidations.CustomerValidator.checkForDuplicateCustomer(newCustomerDetails);
        if (duplicateResponse != null) 
        {
            return duplicateResponse;
        }
       
        
        String newCustomerId = UUID.randomUUID().toString().substring(0,8);
        
        newCustomerDetails.setCustomerId(newCustomerId);
        
        extractedCustomerList.put(newCustomerId, newCustomerDetails);
        
        ApiResponse response = new ApiResponse("NEW CUSTOMER SUCCESSFULLY REGISTERED", newCustomerDetails);
        return Response.status(Response.Status.CREATED).entity(response).build();
    }
    
    
    
    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateAuthorDetails(@PathParam("id") String customerId, Customers updatedCustomerDetails)
    {
        
        Customers existingAuthorDetails = extractedCustomerList.get(customerId);
        if(existingAuthorDetails == null)
        {
            throw new CustomerNotFoundException("CUSTOMER NOT FOUND. INVALID CUSTOMER ID: " + customerId);
        }
        
        StringBuilder errorMessage = BookstoreValidations.CustomerValidator.validateCustomerDetails(updatedCustomerDetails, "PUT");
        if(errorMessage.length() > 0)
        {
            return Response.status(Response.Status.BAD_REQUEST).entity(errorMessage.toString()).build();
        }
        
        Response duplicateResponse = BookstoreValidations.CustomerValidator.checkForDuplicateCustomer(updatedCustomerDetails);
        if (duplicateResponse != null) 
        {
            String existingErrorMessage = (String) duplicateResponse.getEntity();
            return Response.status(Response.Status.CONFLICT).entity(existingErrorMessage + "\nPLEASE CHANGE VALUES TO UPDATE").build();
        }
        
        StringBuilder responseMessage = BookstoreValidations.CustomerValidator.responseMessageForUpdates(updatedCustomerDetails, existingAuthorDetails);
        
        ApiResponse response = new ApiResponse(responseMessage.toString(), existingAuthorDetails);

        return Response.ok(response).build();
    }
    
    
    
    @DELETE
    @Path("/{id}")
    public Response deleteCustomer(@PathParam("id") String customerId)
    {
        if(extractedCustomerList.remove(customerId) != null)
        {
            return Response.status(Response.Status.OK).entity("CUSTOMER ID : " + customerId + " DELETED").build();
        }
        else
        {
            throw new CustomerNotFoundException("CUSTOMER NOT FOUND. \nINVALID CUSTOMER ID : " + customerId);
        }
    }
        
}
