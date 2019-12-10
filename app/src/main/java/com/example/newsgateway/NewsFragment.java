package com.example.newsgateway;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

public class NewsFragment extends Fragment {


    public static NewsFragment newInstance(Article article) {


        NewsFragment f = new NewsFragment();
        Bundle bndl = new Bundle(7);
        bndl.putString("AUTHOR", article.getAuthor());
        bndl.putString("PUBLISHED", article.getPublishedAt());
        bndl.putString("DESCRIPTION", article.getDescription());
        bndl.putString("TITLE", article.getTitle());
        bndl.putString("URLTOIMAGE", article.getUrlToImage());
        bndl.putString("URL", article.getUrl());
        bndl.putInt("POSITION", article.getPosition());
        f.setArguments(bndl);
        return f;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        String AUTHOR = null;
        String PUBLISHED = null;
        String DESCRIPTION = null;
        String TITLE = null;
        String URLTOIMAGE = null;
        int POSITION = -1;

        if(getArguments() != null){
            AUTHOR = getArguments().getString("AUTHOR");
            PUBLISHED = getArguments().getString("PUBLISHED");
            DESCRIPTION = getArguments().getString("DESCRIPTION");
            TITLE = getArguments().getString("TITLE");
            URLTOIMAGE = getArguments().getString("URLTOIMAGE");
            POSITION = getArguments().getInt("POSITION");
        }

        TextView author = rootView.findViewById(R.id.author);
        TextView date = rootView.findViewById(R.id.date);
        TextView description = rootView.findViewById(R.id.description);
        TextView headline = rootView.findViewById(R.id.headline);
        ImageView urltoimage = rootView.findViewById(R.id.image);
        TextView count = rootView.findViewById(R.id.count);
        author.setText(AUTHOR);
        date.setText(PUBLISHED);
        description.setText(DESCRIPTION);
        headline.setText(TITLE);
        count.setText(Integer.toString(POSITION) + " of 9");
        Picasso.with(getActivity()).load(URLTOIMAGE).error(R.drawable.fakenews).placeholder(R.drawable.fakenews).into(urltoimage);
        return rootView;
    }

}
