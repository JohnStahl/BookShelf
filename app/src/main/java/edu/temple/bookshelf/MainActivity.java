package edu.temple.bookshelf;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements BookListFragment.BookSelectedInterface {

    FragmentManager fm;
    boolean twoPane;
    BookDetailsFragment bookDetailsFragment;
    String search = "";
    final String BOOK_URL = "https://kamorris.com/lab/abp/booksearch.php?search=";
    ArrayList<Book> BOOK_LIST = new ArrayList<>();
    RequestQueue requestQueue;
    EditText searchEditText;
    int selectedBook;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        twoPane = findViewById(R.id.container2) != null;
        fm = getSupportFragmentManager();
        requestQueue = Volley.newRequestQueue(this);

        /*Setting up search button*/
        searchEditText = findViewById(R.id.searchEditText);
        findViewById(R.id.searchButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search = searchEditText.getText().toString();
                fm.beginTransaction()
                        .replace(R.id.container1, BookListFragment.newInstance(searchBooks(search, BOOK_LIST)))
                        .commit();
            }
        });

        /*Pull list of books from BOOK_URL*/
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(BOOK_URL,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            int book_id;
                            String title = "";
                            String author = "";
                            String cover_url = "";

                            for(int i = 0; i < 7; i++){
                                JSONObject jsonObject = response.getJSONObject(i);

                                book_id = Integer.parseInt(jsonObject.getString("book_id"));
                                title = jsonObject.getString("title");
                                author = jsonObject.getString("author");
                                cover_url = jsonObject.getString("cover_url");

                                BOOK_LIST.add(new Book(book_id,title, author, cover_url));
                                //Toast.makeText(getApplicationContext(), String.valueOf(BOOK_LIST.size()),Toast.LENGTH_SHORT).show();
                            }
                            fm.beginTransaction()
                                    .replace(R.id.container1, BookListFragment.newInstance(searchBooks(search, BOOK_LIST)))
                                    .commit();
                            if(savedInstanceState != null) bookSelected(selectedBook);
                            
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "Something went wrong",Toast.LENGTH_SHORT).show();
                    }
                });

        requestQueue.add(jsonArrayRequest);
/*
        fm.beginTransaction()
                .replace(R.id.container1, BookListFragment.newInstance(getTestBooks()))
                .commit();
*/
        /*
        If we have two containers available, load a single instance
        of BookDetailsFragment to display all selected books
         */
        if (twoPane) {
            bookDetailsFragment = new BookDetailsFragment();
            fm.beginTransaction()
                    .replace(R.id.container2, bookDetailsFragment)
                    .commit();
        }
    }

    /*
    Generate an arbitrary list of "books" for testing
     */

    private ArrayList<Book> getTestBooks() {
        final ArrayList<Book> books = new ArrayList<>();
        Book book;

        for (int i = 0; i < 10; i++) {
            book = new Book(i, "Title" + i, "Author" + i, "https://kamorris.com//lab//abp//covers//oliver_twist.jpg");
            books.add(book);
        }

        return books;
    }


    /*
    Accepts a string and an ArrayList of books then returns an ArrayList of books that have the
    given string as a substring in their title.
    */
    public ArrayList<Book> searchBooks(String search, ArrayList<Book> books){
        ArrayList<Book> searchResult = new ArrayList<>();

        for(Book b : books){
            if (b.getTitle().contains(search)) searchResult.add(b);
        }

        return searchResult;
    }

    @Override
    public void bookSelected(int index) {
        selectedBook = index;

        if (twoPane)
            /*
            Display selected book using previously attached fragment
             */
            bookDetailsFragment.displayBook(searchBooks(search, BOOK_LIST).get(index));
        else {
            /*
            Display book using new fragment
             */
            fm.beginTransaction()
                    .replace(R.id.container1, BookDetailsFragment.newInstance(searchBooks(search, BOOK_LIST).get(index)))
                    // Transaction is reversible
                    .addToBackStack(null)
                    .commit();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("search", search);
        outState.putInt("selectedBook", selectedBook);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        search = savedInstanceState.getString("search");
        selectedBook = savedInstanceState.getInt("selectedBook");
    }
}
