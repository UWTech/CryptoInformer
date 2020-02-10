package com.example.cryptoinformer.ui.crypto_news;

import android.os.Bundle;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.cryptoinformer.R;
import com.example.cryptoinformer.ui.crypto_news.cryptoRss.RSSRetriver;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

public class NewsFragment extends Fragment {

    private NewsViewModel homeViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        RSSRetriver rssRetriver = new RSSRetriver();

        homeViewModel =
                ViewModelProviders.of(this).get(NewsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_news, container, false);

        LinearLayout newsLinearLayout = (LinearLayout) root.findViewById(R.id.news_linear_layout);

        //TODO:: remove this after making class async
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);

        try {
            // populate RSS feed info in class instance
            rssRetriver.getRSSFeed();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // dynamically add RSS feed items to view
        for (String title: rssRetriver.getTitles()) {
            TextView dynamicNewsViewElement = new TextView(getContext());
            dynamicNewsViewElement.setText(title);
            newsLinearLayout.addView(dynamicNewsViewElement);
        }

        final TextView textView = root.findViewById(R.id.text_home);
        homeViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }
}