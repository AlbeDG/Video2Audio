package com.albe.Video2Audio.InfoActivities;

import android.os.Bundle;
import android.webkit.WebView;
import android.widget.LinearLayout;

import com.albe.Video2Audio.BackButtonActivity;
import com.albe.Video2Audio.R;

public class PrivacyPolicyActivity extends BackButtonActivity {

    String location = "file:///android_asset/privacy_policy.html";

    LinearLayout topLayout;

    WebView webView;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_policy);
        topLayout = findViewById(R.id.privacyTopLayout);
        addBackButton(topLayout);
        webView = findViewById(R.id.privacyPolicyWebView);
        webView.loadUrl(location);
    }
}
