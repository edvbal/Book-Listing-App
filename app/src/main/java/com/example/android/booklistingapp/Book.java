package com.example.android.booklistingapp;

/**
 * Created by Edvinas on 01/06/2017.
 */

public class Book {
    private String author;
    private String title;

    public Book(String author, String title) {
        this.author = author;
        this.title = title;
    }
    public String getAuthor() {return author;}
    public void setAuthor(String author) {this.author = author;}

    public String getTitle() {return title;}
    public void setTitle(String title) {this.title = title;}
}
