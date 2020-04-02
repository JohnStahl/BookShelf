package com.example.bookshelf;

public class Book {

    private int id;
    private String title, author, coverURL;

    public Book(int id, String title, String author, String coverURL){
        this.id = id;
        this.title = title;
        this.author = author;
        this.coverURL = coverURL;
    }

    public int getId(){ return id;}

    public String getTitle(){ return title;}

    public String getAuthor(){ return author;}

    public String getCoverURL(){ return coverURL;}
}
