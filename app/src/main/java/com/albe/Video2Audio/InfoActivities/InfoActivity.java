package com.albe.Video2Audio.InfoActivities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.albe.Video2Audio.BackButtonActivity;
import com.albe.Video2Audio.R;


public class InfoActivity extends BackButtonActivity {

    TextView privacyPolicyTextView, creditsTextView;

    LinearLayout topLayout;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.applicaton_info_layout);
        topLayout = findViewById(R.id.infoTopLayout);
        addBackButton(topLayout);
        //Privacy text view
        privacyPolicyTextView = findViewById(R.id.privacyPolicyTextView);
        privacyPolicyTextView.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), PrivacyPolicyActivity.class);
            startActivity(intent);
        });
        //Credits text view
        creditsTextView = findViewById(R.id.creditsTextView);
        creditsTextView.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), CreditsActivity.class);
            startActivity(intent);
        });

    }
}
