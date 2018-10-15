package edu.uci.ics.fabflixmobile;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class ResultsAdaptor extends ArrayAdapter<Movie> {
    private ArrayList<Movie> movies;

    public ResultsAdaptor(ArrayList<Movie> movies, Context context) {
        super(context, R.layout.layout_listview_row, movies);
        this.movies = movies;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.layout_listview_row, parent, false);

        final Movie mov = movies.get(position);

        TextView titleView = (TextView)view.findViewById(R.id.title);
        TextView yearView = (TextView)view.findViewById(R.id.year);
        TextView directorView = (TextView)view.findViewById(R.id.director);
        TextView genresView = (TextView)view.findViewById(R.id.genres);
        TextView starsView = (TextView)view.findViewById(R.id.stars);

        titleView.setText("Title: " + mov.getTitle());
        yearView.setText("Year: " +mov.getYear());
        directorView.setText("Director: " +mov.getDirector());
        genresView.setText("Genres: " +mov.getGenres());
        starsView.setText("Stars: " +mov.getStars());

        titleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), SingleMovieActivity.class);
                intent.putExtra("id", mov.getId());
                view.getContext().startActivity(intent);
            }
        });

        return view;
    }
}

