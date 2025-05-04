/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.bookstore.bookstore.resources;

import com.bookstore.bookstore.exceptions.BookNotFoundException;
import com.bookstore.bookstore.exceptions.CartNotFoundException;
import com.bookstore.bookstore.exceptions.OutOfStockException;
import com.bookstore.bookstore.model.Books;
import com.bookstore.bookstore.model.Cart;
import com.bookstore.bookstore.model.CartItem;
import com.bookstore.bookstore.model.Customers;
import com.bookstore.bookstore.utilities.BookstoreValidations;
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
@Path("/customers/{customerId}/cart")
public class CartResource 
{
    
    private static final ConcurrentHashMap<String, Cart> cartListForCustomersCartItems = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, Customers> extractedCustomerList = CustomerResource.getCustomerList();
    private static final ConcurrentHashMap<String, Books> extractedBookList = BookResource.getBookList();
    
    
        
    public static ConcurrentHashMap<String, Cart> getCartItemsList()
    {
        return cartListForCustomersCartItems;
    }
    
    
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCart(@PathParam("customerId") String customerId)
    {
        
        Response validationResponse = BookstoreValidations.ValidateCartAndCustomer.validateCustomerId(customerId, extractedCustomerList);
        if (validationResponse != null) 
        {
            return validationResponse;
        }
        
        Cart customerCartDetails = BookstoreValidations.ValidateCartAndCustomer.validateCartExistence(customerId, cartListForCustomersCartItems);
        if (customerCartDetails == null)
        {
            throw new CartNotFoundException("CART DOESN'T EXIST FOR CUSTOMER ID : " + customerId);
            // return Response.status(Response.Status.NOT_FOUND).entity("CART DOESN'T EXIST FOR CUSTOMER ID : " + customerId).build();
        }
        
        customerCartDetails.calculateTotalCartPrice();
        
        return Response.ok(customerCartDetails).build();
    }
    
    
    
    @POST
    @Path("/items")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addCartItem(@PathParam("customerId") String customerId, CartItem cartItemRequest)
    {
        
        String requestedBookId = cartItemRequest.getBookId();
        int requestedBookQuantity = cartItemRequest.getBookQuantity();
        
        Response validationResponse = BookstoreValidations.ValidateCartAndCustomer.validateCustomerId(customerId, extractedCustomerList);
        if (validationResponse != null) 
        {
            return validationResponse;
        }
        
        if (requestedBookQuantity <= 0)
        {
            return Response.status(Response.Status.BAD_REQUEST).entity("VALID QUANTITY REQUIRED. \nINVALID QUANTITY : " + requestedBookQuantity).build();
        }
        
        if(requestedBookId == null || requestedBookId.trim().isEmpty())
        {
            return Response.status(Response.Status.BAD_REQUEST).entity("BOOK ID REQUIRED.").build();
        }
        
        // Retrieve the book using requestedBookId from the books list
        Books bookDetails = extractedBookList.get(requestedBookId);
        if (bookDetails == null) 
        {
            throw new BookNotFoundException("BOOK NOT FOUND. INVALID BOOK ID : " + requestedBookId);
            
            // return Response.status(Response.Status.NOT_FOUND).entity("BOOK NOT FOUND. INVALID BOOK ID : " + requestedBookId).build();
        }
        
        // Check if the requested quantity is available in stock
        int availableBookQuantity = bookDetails.getBookStockQuantity();
        if (requestedBookQuantity > availableBookQuantity ) 
        {
            throw new OutOfStockException("REQUESTED QUANTITY UNAVAILABLE. \nAVAILABLE STOCK : " + availableBookQuantity);

            // return Response.status(Response.Status.BAD_REQUEST).entity("REQUESTED QUANTITY UNAVAILABLE. \nAVAILABLE STOCK : " + availableBookQuantity).build();
        }
        
        // Retrieve the existing cart for the customer if available.
        // Otherwise, create a new cart and associate it with the customerId.
        Cart customerCartDetails = cartListForCustomersCartItems.computeIfAbsent(customerId, id -> 
        {
            Cart newCart = new Cart();
        
            newCart.setCustomerId(customerId);
            return newCart;
        });
        
        // Check if the book already exists in the cart
        CartItem existingBookItemInCart = null;
        for (CartItem existingBook : customerCartDetails.getCartItemsList()) 
        {
            if (existingBook.getBookId().equals(requestedBookId)) 
            {
                existingBookItemInCart = existingBook; 
                break;
            }
        }
        
        // If the book already exists in the cart, update the quantity.
        if (existingBookItemInCart != null)
        {
            int updatedBookQuantity = existingBookItemInCart.getBookQuantity() + requestedBookQuantity;
            
            // Validate updated quantity doesn't exceed stock
            if(updatedBookQuantity > availableBookQuantity)
            {
                return Response.status(Response.Status.BAD_REQUEST).entity("BOOK ALREADY EXISTS IN CART FROM PREVIOUS SELECTION.\nTOTAL QUANTITY UNAVAILABLE. \nAVAILABLE STOCK : " + availableBookQuantity).build();
            }
            
            existingBookItemInCart.setBookQuantity(updatedBookQuantity);
            
            existingBookItemInCart.setTotalPriceForBookItem(updatedBookQuantity * bookDetails.getBookPrice());
            
            bookDetails.setBookStockQuantity(availableBookQuantity - requestedBookQuantity);
            
            // Recalculate the total price of the cart after adding the new item.
            customerCartDetails.calculateTotalCartPrice();

            return Response.status(Response.Status.OK).entity("BOOK ALREADY EXISTS IN CART FROM PREVIOUS SELECTION.\nITEM UPDATED IN CART.\n\nBOOK TITLE: " + bookDetails.getBookTitle() + "\nNO.OF BOOKS : " + updatedBookQuantity + "\nTOTAL PRICE: $" + existingBookItemInCart.getTotalPriceForBookItem()).build();
        }
        else
        {
            // Create a new CartItem for the first time and add it to the cart
            CartItem newBookItemInCart = new CartItem(requestedBookId, requestedBookQuantity, bookDetails);
            // Add the requested Book to the cart.
            customerCartDetails.getCartItemsList().add(newBookItemInCart);
            
            bookDetails.setBookStockQuantity(availableBookQuantity - requestedBookQuantity);
            
            // Recalculate the total price of the cart after adding the new item.
            customerCartDetails.calculateTotalCartPrice();
            
            return Response.status(Response.Status.CREATED).entity("NEW CART CREATED. \nITEM ADDED TO CART. \n\nBOOK TITLE  : " + bookDetails.getBookTitle() + "\nNO.OF BOOKS : " + requestedBookQuantity + "\nTOTAL PRICE : $" + newBookItemInCart.getTotalPriceForBookItem()).build();
        }
        
    }
 
    
    
    @PUT
    @Path("/items/{bookId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateCartItem(@PathParam("customerId") String customerId, @PathParam("bookId") String bookId, CartItem bookQuantity)
    {
        
        Response validationResponse = BookstoreValidations.ValidateCartAndCustomer.validateCustomerId(customerId, extractedCustomerList);
        if (validationResponse != null) 
        {
            return validationResponse;
        }
        
        if(bookId == null)
        {
            return Response.status(Response.Status.BAD_REQUEST).entity("BOOK ID REQUIRED.").build();
        }
        
        // Validate the cartItemRequest
        if (bookQuantity == null || bookQuantity.getBookQuantity() <= 0) 
        {
            return Response.status(Response.Status.BAD_REQUEST).entity("VALID QUANTITY REQUIRED.").build();
        }
        
        Cart customerCartDetails = BookstoreValidations.ValidateCartAndCustomer.validateCartExistence(customerId, cartListForCustomersCartItems);
        if (customerCartDetails == null)
        {
            throw new CartNotFoundException("CART DOESN'T EXIST FOR CUSTOMER ID : " + customerId);
            // return Response.status(Response.Status.NOT_FOUND).entity("CART DOESN'T EXIST FOR CUSTOMER ID : " + customerId).build();
        }
        
        // Check if the item exists in the cart
        CartItem existingBookItemInCart = null;
        for (CartItem existingBook : customerCartDetails.getCartItemsList()) 
        {
            if (existingBook.getBookId().equals(bookId))
            {
                existingBookItemInCart = existingBook;
                break;
            }
        }

        if (existingBookItemInCart == null)
        {
            throw new BookNotFoundException("BOOK NOT FOUND IN CART. BOOK ID : " + bookId);
            // return Response.status(Response.Status.NOT_FOUND).entity("BOOK NOT FOUND IN CART. BOOK ID : " + bookId).build();
        }

        Books bookDetails = extractedBookList.get(bookId);
        
        int availableBookQuantity = bookDetails.getBookStockQuantity();
        int currentCartQuantity = existingBookItemInCart.getBookQuantity();

        // If the new quantity is more than the current cart quantity, check stock for the increase
        if (bookQuantity.getBookQuantity() > currentCartQuantity) 
        {
            int stockRequired = bookQuantity.getBookQuantity() - currentCartQuantity;

            // Check if the requested quantity is available in stock
            if (stockRequired > availableBookQuantity) 
            {
                throw new OutOfStockException("REQUESTED QUANTITY UNAVAILABLE. \nAVAILABLE STOCK : " + availableBookQuantity);

                // return Response.status(Response.Status.BAD_REQUEST).entity("INSUFFICIENT STOCK.").build();
            }

            // Update stock when quantity is increased
            bookDetails.setBookStockQuantity(availableBookQuantity - stockRequired);
        }
        else if (bookQuantity.getBookQuantity() < currentCartQuantity) 
        {
            int quantityReturnedToStock = currentCartQuantity - bookQuantity.getBookQuantity();

            // Update stock when quantity is decreased
            bookDetails.setBookStockQuantity(availableBookQuantity + quantityReturnedToStock);
        }
        
        // Update the cart item quantity
        existingBookItemInCart.setBookQuantity(bookQuantity.getBookQuantity());
        // Update the total price for this item
        existingBookItemInCart.setTotalPriceForBookItem(existingBookItemInCart.getBookQuantity() * bookDetails.getBookPrice());
        
        // Recalculate the total price for the cart after the update
        double newCartTotalPrice = 0.0;
        for (CartItem item : customerCartDetails.getCartItemsList()) 
        {
            newCartTotalPrice += item.getTotalPriceForBookItem(); // Sum up the prices of all items
        }

        // Update the total price of the cart
        customerCartDetails.setTotalCartPrice(newCartTotalPrice);

        double totalPrice = existingBookItemInCart.getBookQuantity() * bookDetails.getBookPrice();

        return Response.status(Response.Status.OK).entity("ITEM UPDATED IN CART.\nBOOK TITLE: " + bookDetails.getBookTitle() + "\nNO.OF BOOKS: " + existingBookItemInCart.getBookQuantity() + "\nTOTAL PRICE: $" + totalPrice).build();

        
    }
    
    
    
    @DELETE
    @Path("/items/{bookId}")
    public Response deleteCartItem(@PathParam("customerId") String customerId, @PathParam("bookId") String bookId)
    {

        Response validationResponse = BookstoreValidations.ValidateCartAndCustomer.validateCustomerId(customerId, extractedCustomerList);
        if (validationResponse != null) 
        {
            return validationResponse;
        }

        Cart customerCartDetails = BookstoreValidations.ValidateCartAndCustomer.validateCartExistence(customerId, cartListForCustomersCartItems);
        if (customerCartDetails == null)
        {
            throw new CartNotFoundException("CART DOESN'T EXIST FOR CUSTOMER ID : " + customerId);

            // return Response.status(Response.Status.NOT_FOUND).entity("CART DOESN'T EXIST FOR CUSTOMER ID : " + customerId).build();
        }
        
        // Find the CartItem with matching bookId
        CartItem itemToRemove = null;
        for (CartItem item : customerCartDetails.getCartItemsList()) 
        {
            if (item.getBookId().equals(bookId)) 
            {
                itemToRemove = item;
                break;
            }
        }

        if (itemToRemove == null) 
        {
            throw new BookNotFoundException("BOOK NOT FOUND IN CART. BOOK ID : " + bookId);

            // return Response.status(Response.Status.NOT_FOUND).entity("BOOK NOT FOUND IN CART. BOOK ID: " + bookId).build();
        }

        // Update the book stock
        Books bookDetails = extractedBookList.get(bookId);
        if (bookDetails != null) 
        {
            int updatedStock = bookDetails.getBookStockQuantity() + itemToRemove.getBookQuantity();
            bookDetails.setBookStockQuantity(updatedStock);
        }
        
        customerCartDetails.getCartItemsList().remove(itemToRemove);
        
        // Recalculate the total price after removal
        double cartTotalPrice = 0.0;
        for (CartItem item : customerCartDetails.getCartItemsList()) 
        {
            cartTotalPrice += item.getTotalPriceForBookItem(); // Adding total price of all remaining items
        }

        // Update total price of the cart
        customerCartDetails.setTotalCartPrice(cartTotalPrice);
        
        
        boolean customerCartEmpty = customerCartDetails.getCartItemsList().isEmpty();
        if(customerCartEmpty)
        {
            cartListForCustomersCartItems.remove(customerId);
        }
        
        return Response.ok("CART ITEM DELETED. BOOK ID : " + customerId).build();   

    }
    
    
    
}
