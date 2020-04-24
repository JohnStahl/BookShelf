package edu.temple.bookshelf;

public class Book {

    private int id, duration;
    private String title, author, coverURL;

    public Book(int id, int duration, String title, String author, String coverURL){
        this.id = id;
        this.duration = duration;
        this.title = title;
        this.author = author;
        this.coverURL = coverURL;
    }

    public int getId(){ return id;}

    public int getDuration() {return duration;}

    public String getTitle(){ return title;}

    public String getAuthor(){ return author;}

    public String getCoverURL(){ return coverURL;}
}
