package com.example.cryptoinformer.ui.crypto_prices;

import android.os.Bundle;
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

public class CryptoPricesFragment extends Fragment {

    private CryptoPricesViewModel dashboardViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        dashboardViewModel =
                ViewModelProviders.of(this).get(CryptoPricesViewModel.class);
        View root = inflater.inflate(R.layout.fragment_prices, container, false);

        LinearLayout pricesLinearLayout = (LinearLayout) root.findViewById(R.id.price_linear_layout);

        TextView dynamicPriceViewElement = new TextView(getContext());
        dynamicPriceViewElement.setText("Programmtically Generated Text");
        pricesLinearLayout.addView(dynamicPriceViewElement);

        final TextView textView = root.findViewById(R.id.prices);
        dashboardViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }
}