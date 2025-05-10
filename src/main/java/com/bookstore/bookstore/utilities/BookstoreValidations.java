/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.bookstore.bookstore.utilities;

import com.bookstore.bookstore.model.Authors;
import com.bookstore.bookstore.model.Books;
import com.bookstore.bookstore.model.Cart;
import com.bookstore.bookstore.model.Customers;

import java.util.Map;
import javax.ws.rs.core.Response;
import java.time.Year;

/**
 *
 * @author SANIDU WICKRAMASIGHE
 */
public class BookstoreValidations 
{
    
    private static final Map<String, Customers> extractedCustomerList = DefaultDataStore.getCustomerList();
    private static final Map<String, Authors> extractedAuthorList = DefaultDataStore.getAuthorList();
    private static final Map<String, Books> extractedBookList = DefaultDataStore.getBookList();
    
 
    
    public static class CustomerValidator
    {
        
        public static StringBuilder validateCustomerDetails(Customers customerDetails, String method) 
        {
            
            StringBuilder errorMessages = new StringBuilder();
            boolean isPost = "POST".equalsIgnoreCase(method);

            if (customerDetails == null) 
            {
                errorMessages.append("CUSTOMER DETAILS MUST BE ENTERED");
                return errorMessages;
            }

            if (isPost || customerDetails.getCustomerName() != null) 
            {
                if (customerDetails.getCustomerName() == null || customerDetails.getCustomerName().trim().isEmpty()) 
                {
                    errorMessages.append("CUSTOMER NAME REQUIRED");
                }
            }

            if (isPost || customerDetails.getCustomerEmail() != null) 
            {
                if (customerDetails.getCustomerEmail() == null || customerDetails.getCustomerEmail().trim().isEmpty()) 
                {
                    errorMessages.append("\nCUSTOMER EMAIL REQUIRED");
                } 
                else if (!customerDetails.getCustomerEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) 
                {
                    errorMessages.append("\nINVALID EMAIL FORMAT");
                }
            }

            if (isPost || customerDetails.getCustomerPassword() != null) 
            {
                if (customerDetails.getCustomerPassword() == null || customerDetails.getCustomerPassword().trim().isEmpty()) 
                {
                    errorMessages.append("\nCUSTOMER PASSWORD REQUIRED");
                }
            }

            return errorMessages;
        }

        
        
        public static Response checkForDuplicateCustomer(Customers customerDetails)
        {
            if (customerDetails.getCustomerEmail() != null)
            {
                for(Customers existingCustomer : extractedCustomerList.values())
                {
                    if( existingCustomer.getCustomerName().equalsIgnoreCase(customerDetails.getCustomerName()) &&
                        existingCustomer.getCustomerEmail().equalsIgnoreCase(customerDetails.getCustomerEmail()) )
                    {
                        return Response.status(Response.Status.CONFLICT).entity("CUSTOMER ALREADY EXISTS. \nCUSTOMER ID : " + existingCustomer.getCustomerId()).build();
                    }
                }
            }
            return null;
        }

        
        
        public static StringBuilder responseMessageForUpdates(Customers updatedCustomerDetails, Customers existingCustomerDetails) 
        {
            StringBuilder responseMessage = new StringBuilder();
            boolean updated = false;

            if (updatedCustomerDetails.getCustomerName() != null && !updatedCustomerDetails.getCustomerName().equalsIgnoreCase(existingCustomerDetails.getCustomerName())) 
            {
                existingCustomerDetails.setCustomerName(updatedCustomerDetails.getCustomerName());
                responseMessage.append("CUSTOMER NAME UPDATED: ").append(existingCustomerDetails.getCustomerName()).append(" | ");
                updated = true;
            }
            if (updatedCustomerDetails.getCustomerEmail() != null && !updatedCustomerDetails.getCustomerEmail().equalsIgnoreCase(existingCustomerDetails.getCustomerEmail())) 
            {
                existingCustomerDetails.setCustomerEmail(updatedCustomerDetails.getCustomerEmail());
                responseMessage.append("CUSTOMER EMAIL UPDATED : ").append(existingCustomerDetails.getCustomerEmail()).append(" | ");
                updated = true;
            }
            if (updatedCustomerDetails.getCustomerPassword() != null && !updatedCustomerDetails.getCustomerPassword().equals(existingCustomerDetails.getCustomerPassword())) 
            {
                existingCustomerDetails.setCustomerPassword(updatedCustomerDetails.getCustomerPassword());
                responseMessage.append("CUSTOMER PASSWORD UPDATED | ");
                updated = true;
            }
            if (!updated) 
            {
                responseMessage.append("NO FIELDS WERE UPDATED");
            }
            return responseMessage;
        }
        
    }
    
    
    
    public static class BookValidator
    {
        
        private static int currentYear = Year.now().getValue();    
        

        
        public static StringBuilder generateUpdateMessage(Books updatedBooksDetails, Books existingBookDetails) 
        {
            StringBuilder responseMessage = new StringBuilder();
            boolean updated = false;

            if (updatedBooksDetails.getBookTitle() != null && updatedBooksDetails.getBookTitle().equalsIgnoreCase(existingBookDetails.getBookTitle())) 
            {
                responseMessage.append("BOOK TITLE UPDATED : ").append(existingBookDetails.getBookTitle()).append(" | ");
                updated = true;
            }

            if (updatedBooksDetails.getBookISBN() != null && updatedBooksDetails.getBookISBN().equalsIgnoreCase(existingBookDetails.getBookISBN())) 
            {
                responseMessage.append("BOOK ISBN UPDATED : ").append(existingBookDetails.getBookISBN()).append(" | ");
                updated = true;
            }

            if (updatedBooksDetails.getBookAuthorId() != null && updatedBooksDetails.getBookAuthorId().equalsIgnoreCase(existingBookDetails.getBookAuthorId())) 
            {
                responseMessage.append("BOOK AUTHOR ID UPDATED : ").append(existingBookDetails.getBookAuthorId()).append(" | ");
                updated = true;
            }

            if (updatedBooksDetails.getBookPublishedYear() != 0 && updatedBooksDetails.getBookPublishedYear() == existingBookDetails.getBookPublishedYear()) 
            {
                responseMessage.append("BOOK PUBLISHED YEAR UPDATED : ").append(existingBookDetails.getBookPublishedYear()).append(" | ");
                updated = true;
            }

            if (updatedBooksDetails.getBookPrice() != 0 && updatedBooksDetails.getBookPrice() == existingBookDetails.getBookPrice()) 
            {
                responseMessage.append("BOOK PRICE UPDATED : ").append(existingBookDetails.getBookPrice()).append(" | ");
                updated = true;
            }

            if (updatedBooksDetails.getBookStockQuantity() != 0 && updatedBooksDetails.getBookStockQuantity() == existingBookDetails.getBookStockQuantity()) 
            {
                responseMessage.append("BOOK QUANTITY UPDATED : ").append(existingBookDetails.getBookStockQuantity()).append(" | ");
                updated = true;
            }

            if (!updated) 
            {
                responseMessage.append("NO FIELDS WERE UPDATED");
            }

            return responseMessage;

        }

        
        
        public static Response validateBookDetails(Books bookDetails, Books existingBookDetails, String method) 
        {
            StringBuilder errorMessages = new StringBuilder();

            // Ensure a valid book object is provided
            if (bookDetails == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                               .entity("All BOOK DATA SHOULD BE ENTERED.")
                               .build();
            }

            // Handle POST validation
            if ("POST".equalsIgnoreCase(method)) 
            {
                if (bookDetails.getBookTitle() == null || bookDetails.getBookTitle().trim().isEmpty()) 
                {
                    errorMessages.append("BOOK TITLE REQUIRED. ");
                }
                if (bookDetails.getBookAuthorId() == null || bookDetails.getBookAuthorId().trim().isEmpty()) 
                {
                    errorMessages.append("\nBOOK AUTHOR ID REQUIRED. ");
                }
                if (bookDetails.getBookISBN() == null || bookDetails.getBookISBN().trim().isEmpty()) 
                {
                    errorMessages.append("\nBOOK ISBN REQUIRED. ");
                }
                if (bookDetails.getBookPublishedYear() == 0) 
                {
                    errorMessages.append("\nPUBLICATION YEAR REQUIRED. ");
                } 
                else 
                {
                    if (bookDetails.getBookPublishedYear() > currentYear) 
                    {
                        errorMessages.append("\nPUBLICATION YEAR CANNOT BE IN FUTURE (").append(bookDetails.getBookPublishedYear()).append(") \nCURRENT YEAR : ").append(currentYear).append(". ");
                    }
                    if (bookDetails.getBookPublishedYear() <= 0) 
                    {
                        errorMessages.append("\nPUBLICATION YEAR CANNOT BE NEGATIVE.");
                    }
                }
                if (bookDetails.getBookPrice() == 0) 
                {
                    errorMessages.append("\nPRICE REQUIRED.");
                } 
                else if (bookDetails.getBookPrice() <= 0) 
                {
                    errorMessages.append("\nPRICE CANNOT BE NEGATIVE.");
                }
                if (bookDetails.getBookStockQuantity() < 0 ) 
                {
                    errorMessages.append("\nSTOCK QUANTITY CANNOT BE NEGATIVE.");
                }

                // If there are errors, return the response
                if (errorMessages.length() > 0) 
                {
                    return Response.status(Response.Status.BAD_REQUEST).entity(errorMessages.toString()).build();
                }
            }

            // Handle PUT validation
            if ("PUT".equalsIgnoreCase(method)) 
            {

                if (bookDetails.getBookISBN() != null) 
                {
                    if (bookDetails.getBookISBN().trim().isEmpty()) 
                    {
                        return Response.status(Response.Status.BAD_REQUEST)
                                       .entity("ENTER BOOK TITLE")
                                       .build();
                    }
                    existingBookDetails.setBookISBN(bookDetails.getBookISBN());
                }

                if (bookDetails.getBookTitle() != null) 
                {
                    if (bookDetails.getBookTitle().trim().isEmpty()) 
                    {
                        return Response.status(Response.Status.BAD_REQUEST)
                                       .entity("ENTER BOOK ISBN")
                                       .build();
                    }
                    existingBookDetails.setBookTitle(bookDetails.getBookTitle());
                }

                if (bookDetails.getBookAuthorId() != null) 
                {
                    String toBeUpdatedAuthorId = bookDetails.getBookAuthorId();

                    if(!extractedAuthorList.containsKey(toBeUpdatedAuthorId))
                    {
                        return Response.status(Response.Status.NOT_FOUND).entity("AUTHOR NOT FOUND. INVALID AUTHOR ID : " + toBeUpdatedAuthorId).build();
                    }

                    if (bookDetails.getBookAuthorId().trim().isEmpty()) 
                    {
                        return Response.status(Response.Status.BAD_REQUEST)
                                       .entity("ENTER BOOK AUTHOR ID")
                                       .build();
                    }

                    existingBookDetails.setBookAuthorId(toBeUpdatedAuthorId);

                    String authorName = extractedAuthorList.get(toBeUpdatedAuthorId).getAuthorName();
                    existingBookDetails.setBookAuthorName(authorName);
                }

                if (bookDetails.getBookPublishedYear() != 0) 
                {
                    if ( bookDetails.getBookPublishedYear() < 0 || bookDetails.getBookPublishedYear() > currentYear) 
                    {
                        return Response.status(Response.Status.BAD_REQUEST).entity("ENTER A VALID YEAR").build();
                    }
                    existingBookDetails.setBookPublishedYear(bookDetails.getBookPublishedYear());
                }

                if (bookDetails.getBookPrice() != 0) 
                {
                    if ( bookDetails.getBookPrice() < 0 ) 
                    {
                        return Response.status(Response.Status.BAD_REQUEST).entity("ENTER A VALID PRICE").build();
                    }
                    existingBookDetails.setBookPrice(bookDetails.getBookPrice());
                }

                if (bookDetails.getBookStockQuantity() != 0) 
                {
                    if ( bookDetails.getBookStockQuantity() < 0 ) 
                    {
                        return Response.status(Response.Status.BAD_REQUEST).entity("ENTER A VALID QUANTITY").build();
                    }
                    existingBookDetails.setBookStockQuantity(bookDetails.getBookStockQuantity());
                }
            }

            return Response.ok().build();  // Return success response if everything is valid
        }

        
        
        public static Response checkForDuplicateBook(Books bookDetails, String method) 
        {
            boolean isPost = "POST".equalsIgnoreCase(method);
        
            for (Books existingBooks : extractedBookList.values()) 
            {
        
                boolean isSameBook = !isPost && existingBooks.getBookId().equals(bookDetails.getBookId());
                
                // Skip the same book during PUT
                if (isSameBook) continue;
        
                // 1. Check for ISBN conflict
                if (existingBooks.getBookISBN().equalsIgnoreCase(bookDetails.getBookISBN()))
                {
                    // If other fields differ but ISBN is same — ISBN conflict
                    boolean sameTitle = existingBooks.getBookTitle().equalsIgnoreCase(bookDetails.getBookTitle());
                    boolean sameYear = existingBooks.getBookPublishedYear() == bookDetails.getBookPublishedYear();
                    boolean sameAuthor = existingBooks.getBookAuthorId().equals(bookDetails.getBookAuthorId());
        
                    // Full duplicate
                    if (sameTitle && sameYear && sameAuthor)
                    {
                        return Response.status(Response.Status.CONFLICT).entity("BOOK ALREADY EXISTS UNDER SAME AUTHOR.\nBOOK ID : " + existingBooks.getBookId()).build();
                    }
        
                    // Duplicate ISBN under different author
                    if (!sameAuthor && sameTitle && sameYear) 
                    {
                        return Response.status(Response.Status.CONFLICT).entity("BOOK ALREADY EXISTS WITH A DIFFERENT AUTHOR.\nBOOK ID : " + existingBooks.getBookId() +"\nAUTHOR ID : " + existingBooks.getBookAuthorId()).build();
                    }
        
                    // ISBN used for a different book (title/year mismatch)
                    if (!sameTitle || !sameYear) 
                    {
                        return Response.status(Response.Status.CONFLICT).entity("BOOK ISBN ALREADY EXISTS.\nBOOK ID : " + existingBooks.getBookId()).build();
                    }
                }
            }
        
            return null;
        }        
        
    }
    
    
    
    public static class AuthorValidator
    {
        
        public static StringBuilder validateAuthorDetails(Authors AuthorDetails, String method) 
        {
            StringBuilder errorMessages = new StringBuilder();
            boolean isPost = "POST".equalsIgnoreCase(method);

            if (AuthorDetails == null) 
            {
                errorMessages.append("AUTHOR DETAILS MUST BE ENTERED");
                return errorMessages;
            }

            if (isPost || AuthorDetails.getAuthorName() != null) 
            {
                if (AuthorDetails.getAuthorName() == null || AuthorDetails.getAuthorName().trim().isEmpty()) 
                {
                    errorMessages.append("\nAUTHOR NAME REQUIRED");
                }
            }

            return errorMessages;
        }

    
    
        public static Response checkForDuplicateAuthor(Authors authorDetails) 
        {    
            for(Authors existingAuthors : extractedAuthorList.values())
            {
                boolean sameAuthorName = existingAuthors.getAuthorName().equalsIgnoreCase(authorDetails.getAuthorName());
                boolean sameAuthorBio = existingAuthors.getAuthorBiography().equalsIgnoreCase(authorDetails.getAuthorBiography());

                if (sameAuthorName) 
                {
                    return Response.status(Response.Status.CONFLICT)
                        .entity("AUTHOR NAME ALREADY EXISTS. AUTHOR ID: " + existingAuthors.getAuthorId()).build();
                }

                if (sameAuthorBio) 
                {
                    return Response.status(Response.Status.CONFLICT)
                        .entity("AUTHOR BIOGRAPHY ALREADY EXISTS. AUTHOR ID: " + existingAuthors.getAuthorId()).build();
                }
            }
            return null;
        }
    
    
    
        public static StringBuilder responseMessageForUpdates(Authors updatedAuthorDetails, Authors existingAuthorDetails) 
        {
            StringBuilder responseMessage = new StringBuilder();
            boolean updated = false;

            if (updatedAuthorDetails.getAuthorName() != null && !updatedAuthorDetails.getAuthorName().equalsIgnoreCase(existingAuthorDetails.getAuthorName())) 
            {
                existingAuthorDetails.setAuthorName(updatedAuthorDetails.getAuthorName());
                responseMessage.append("AUTHOR NAME UPDATED: ").append(existingAuthorDetails.getAuthorName()).append(" | ");
                updated = true;
            }

            if (updatedAuthorDetails.getAuthorBiography() != null && !updatedAuthorDetails.getAuthorBiography().equalsIgnoreCase(existingAuthorDetails.getAuthorBiography())) 
            {
                existingAuthorDetails.setAuthorBiography(updatedAuthorDetails.getAuthorBiography());
                responseMessage.append("AUTHOR BIOGRAPHY UPDATED : ").append(existingAuthorDetails.getAuthorBiography()).append(" | ");
                updated = true;
            }

            if (!updated) 
            {
                responseMessage.append("NO FIELDS WERE UPDATED");
            }

            return responseMessage;
        }
        
    }
    
    
    
    public static class ValidateCartAndCustomer
    {
        
        public static Response validateCustomerId(String customerId, Map<String, Customers> extractedCustomerList) 
        {

            if(customerId == null || customerId.trim().isEmpty())
            {
                return Response.status(Response.Status.BAD_REQUEST).entity("CUSTOMER ID REQUIRED.").build();
            }

            boolean existingCustomer = extractedCustomerList.containsKey(customerId);
            if(!existingCustomer)
            {
                return Response.status(Response.Status.NOT_FOUND).entity("INVALID CUSTOMER ID IN RESOURCE PATH. \nCUSTOMER NOT FOUND. CUSTOMER ID : " + customerId).build();
            }

            return null;
        }
    
    
        public static Cart validateCartExistence(String customerId, Map<String, Cart> cartListForCustomersCartItems)
        {
            Cart customerCartDetails = cartListForCustomersCartItems.get(customerId);

            if(customerCartDetails == null)
            {
                return null;
            }

            return customerCartDetails;
        }
        
    }
    
}
