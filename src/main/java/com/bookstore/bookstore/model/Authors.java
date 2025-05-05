package com.bookstore.bookstore.model;

import java.util.UUID;

/**
 *
 * @author SANIDU WICKRAMASIGHE
 */
public class Authors 
{
    
    private String authorId;
    private String authorName;
    private String authorBiography;
    
    public Authors() {}
    
    public Authors(String name, String biography)
    {
        this.authorId = UUID.randomUUID().toString().substring(0,8);
        this.authorName = name;
        this.authorBiography = biography;
    }

    public void setAuthorId() 
    {
        this.authorId = UUID.randomUUID().toString().substring(0,8);
    }

    public String getAuthorId() 
    {
        return authorId;
    }
    
    public void setAuthorName(String authorName) 
    {
        this.authorName = authorName;
    }

    public String getAuthorName() 
    {
        return authorName;
    }
    
    public void setAuthorBiography(String authorBiography) 
    {
        this.authorBiography = authorBiography;
    }

    public String getAuthorBiography() 
    {
        return authorBiography;
    }

    @Override
    public String toString() 
    {
        return "Author {" +
                " id : '" + authorId + 
                "' name : " + authorName +
                "' biography : " + authorBiography + 
                "' }";
    }
    
    
    
    
}
