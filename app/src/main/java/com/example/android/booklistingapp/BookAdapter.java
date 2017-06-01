package com.example.android.booklistingapp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Edvinas on 01/06/2017.
 */

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.ViewHolder> {

    List<Book> books;
    Context context;

    public static class ViewHolder extends RecyclerView.ViewHolder{

        private TextView title;
        private TextView author;

        public ViewHolder(View view){
            super(view);
            title = (TextView) view.findViewById(R.id.title);
            author = (TextView) view.findViewById(R.id.author);
        }
    }

    public BookAdapter(List<Book> books, Context context) {
        this.books = books;
        this.context = context;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Book book = books.get(position);
        holder.title.setText(book.getTitle());
        holder.author.setText(book.getAuthor());
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View recyclerItem = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_item,parent,false);
        ViewHolder viewHolder = new ViewHolder(recyclerItem);
        return viewHolder;
    }

    @Override
    public int getItemCount() {
        return books.size();
    }
}
