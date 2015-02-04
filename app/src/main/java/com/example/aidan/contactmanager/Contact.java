package com.example.aidan.contactmanager;

import android.net.Uri;

import java.net.URI;

/**
 * Created by Aidan on 1/29/2015.
 */
public class Contact {

    private String _title, _price, _keywords, _description;
    private Uri _imageUri;
    private int _id;

    public Contact(int id, String title, String price, String keywords, String description, Uri imageUri) {
        _id = id;
        _title = title;
        _price = price;
        _keywords= keywords;
        _description = description;
        _imageUri = imageUri;
    }

    public int getId(){
        return _id;
    }

    public Uri getImageURI(){
        return _imageUri;
    }

    public String getTitle(){
        return _title;
    }

    public String getPrice(){
        return _price;
    }

    public String getKeywords(){
        return _keywords;
    }

    public String getDescription(){
        return _description;
    }
}
