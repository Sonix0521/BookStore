/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.bookstore.bookstore.resources;

import com.bookstore.bookstore.exceptions.CartNotFoundException;
import com.bookstore.bookstore.exceptions.OrderNotFoundException;
import com.bookstore.bookstore.exceptions.OutOfStockException;
import com.bookstore.bookstore.model.Books;
import com.bookstore.bookstore.model.Cart;
import com.bookstore.bookstore.model.CartItem;
import com.bookstore.bookstore.model.Customers;
import com.bookstore.bookstore.model.Orders;
import com.bookstore.bookstore.utilities.BookstoreValidations;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 *
 * @author SANIDU WICKRAMASIGHE
 */
@Path("/customers/{customerId}/orders")
public class OrderResource 
{
    
    private static ConcurrentHashMap<String, Orders> customersOrdersList = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, Customers> extractedCustomerList = CustomerResource.getCustomerList();
    private static final ConcurrentHashMap<String, Books> extractedBookList = BookResource.getBookList();
    private static final ConcurrentHashMap<String, Cart> extractedCartItemsList = CartResource.getCartItemsList();
    
    
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllOrder(@PathParam("customerId") String customerId)
    {
        
        Response validationResponse =  BookstoreValidations.ValidateCartAndCustomer.validateCustomerId(customerId, extractedCustomerList);
        if (validationResponse != null) 
        {
            return validationResponse;
        }
        
        // Add orders related to the customer to this temp array to display.
        ArrayList<Orders> customerRelatedOrders = new ArrayList<>();
        for (Orders order : customersOrdersList.values()) 
        {
            if (order.getCustomerId().equals(customerId))
            {
                customerRelatedOrders.add(order);
            }
        }
    
        if (customerRelatedOrders.isEmpty())
        {
            throw new OrderNotFoundException("NO ORDERS FOUND FOR CUSTOMER ID : " + customerId);
            // return Response.status(Response.Status.NOT_FOUND).entity("NO ORDER FOUND FOR CUSTOMER ID : " + customerId).build();
        }

        return Response.ok(customerRelatedOrders).build();
        
    }
    
    
    @GET
    @Path("/{orderId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getOrderById(@PathParam("customerId") String customerId, @PathParam("orderId") String orderId)
    {
        Response validationResponse = BookstoreValidations.ValidateCartAndCustomer.validateCustomerId(customerId, extractedCustomerList);
        if (validationResponse != null) 
        {
            return validationResponse;
        }
        
        for (Orders order : customersOrdersList.values()) 
        {
            if (order.getOrderId().equals(orderId) && order.getCustomerId().equals(customerId)) 
            {
                return Response.ok(order).build();
            }
        }
        
        throw new OrderNotFoundException("ORDER NOT FOUND FOR CUSTOMER ID : " + customerId + " ORDER ID : " + orderId);
        // return Response.status(Response.Status.NOT_FOUND).entity("INVALID ORDER ID FOR CUSTOMER. CUSTOMER ID : " + customerId + " ORDER ID : " + orderId).build();
    }
    
    
    
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createOrder(@PathParam("customerId") String customerId) 
    {
        Response validationResponse = BookstoreValidations.ValidateCartAndCustomer.validateCustomerId(customerId, extractedCustomerList);
        if (validationResponse != null) 
        {
            return validationResponse;
        }

        Cart cartDetails = extractedCartItemsList.get(customerId);
        if (cartDetails == null || cartDetails.getCartItemsList().isEmpty()) 
        {
            throw new CartNotFoundException("CART NOT FOUND FOR CUSTOMER ID : " + customerId);
            // return Response.status(Response.Status.BAD_REQUEST).entity("CART DOESN'T EXIST FOR CUSTOMER ID : " + customerId).build();
        }

        for (CartItem item : cartDetails.getCartItemsList()) 
        {
            Books bookDetails = extractedBookList.get(item.getBookId());
            if (bookDetails == null || item.getBookQuantity() > bookDetails.getBookStockQuantity()) 
            {
                throw new OutOfStockException("REQUESTED QUANTITY UNAVAILABLE. \nAVAILABLE STOCK : " + bookDetails.getBookStockQuantity());
                // return Response.status(Response.Status.BAD_REQUEST).entity("INSUFFICIENT STOCK. BOOK ID : " + item.getBookId()).build();
            }
        }

        Orders order = new Orders(cartDetails);

        for (CartItem item : cartDetails.getCartItemsList()) 
        {
            Books book = extractedBookList.get(item.getBookId());
            book.setBookStockQuantity(book.getBookStockQuantity() - item.getBookQuantity());
        }

        customersOrdersList.put(order.getOrderId(), order);
        extractedCartItemsList.remove(customerId);

        return Response.status(Response.Status.CREATED).entity(order).build();
    }
    
    
}
