package com.albe.Video2Audio.InfoActivities;

import android.os.Bundle;
import android.webkit.WebView;
import android.widget.LinearLayout;

import com.albe.Video2Audio.BackButtonActivity;
import com.albe.Video2Audio.R;

public class CreditsActivity extends BackButtonActivity {

    String location = "file:///android_asset/credits.html";

    LinearLayout topLayout;

    WebView creditsWebView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_credits);
        topLayout = findViewById(R.id.creditsTopLayout);
        addBackButton(topLayout);
        creditsWebView = findViewById(R.id.creditsWebView);
        creditsWebView.loadUrl(location);
    }
}
