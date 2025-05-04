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
import com.bookstore.bookstore.utilities.DefaultDataStore;

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
    
    private static final ConcurrentHashMap<String, Books> extractedBookList = DefaultDataStore.getBookList();
    private static final ConcurrentHashMap<String, Customers> extractedCustomerList = DefaultDataStore.getCustomerList();
    private static final ConcurrentHashMap<String, Cart> extractedCartItemList = DefaultDataStore.getCartItemsList();
    private static final ConcurrentHashMap<String, Orders> extractedOrderList = DefaultDataStore.getOrdersList();
    
    
    
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
        for (Orders order : extractedOrderList.values()) 
        {
            if (order.getCustomerId().equals(customerId))
            {
                customerRelatedOrders.add(order);
            }
        }
    
        if (customerRelatedOrders.isEmpty())
        {
            throw new OrderNotFoundException("NO ORDERS FOUND FOR CUSTOMER ID : " + customerId);
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
        
        for (Orders order : extractedOrderList.values()) 
        {
            if (order.getOrderId().equals(orderId) && order.getCustomerId().equals(customerId)) 
            {
                return Response.ok(order).build();
            }
        }
        
        throw new OrderNotFoundException("ORDER NOT FOUND FOR CUSTOMER ID : " + customerId + " ORDER ID : " + orderId);
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

        Cart cartDetails = extractedCartItemList.get(customerId);
        if (cartDetails == null || cartDetails.getCartItemsList().isEmpty()) 
        {
            throw new CartNotFoundException("CART NOT FOUND FOR CUSTOMER ID : " + customerId);
        }

        for (CartItem item : cartDetails.getCartItemsList()) 
        {
            Books bookDetails = extractedBookList.get(item.getBookId());
            if (bookDetails == null || item.getBookQuantity() > bookDetails.getBookStockQuantity()) 
            {
                throw new OutOfStockException("REQUESTED QUANTITY UNAVAILABLE. \nAVAILABLE STOCK : " + bookDetails.getBookStockQuantity());
            }
        }

        Orders order = new Orders(cartDetails);

        for (CartItem item : cartDetails.getCartItemsList()) 
        {
            Books book = extractedBookList.get(item.getBookId());
            book.setBookStockQuantity(book.getBookStockQuantity() - item.getBookQuantity());
        }

        extractedOrderList.put(order.getOrderId(), order);
        extractedCartItemList.remove(customerId);

        return Response.status(Response.Status.CREATED).entity(order).build();
    }
    
}
