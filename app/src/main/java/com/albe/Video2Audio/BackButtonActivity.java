package com.albe.Video2Audio;

import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

public class BackButtonActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void addBackButton(LinearLayout buttonLayout) {
        ImageView backButton = new ImageView(this);
        backButton.setImageDrawable(getDrawable(R.drawable.baseline_arrow_back_24));
        // Set selectableItemBackground programmatically
        TypedValue outValue = new TypedValue();
        getTheme().resolveAttribute(android.R.attr.selectableItemBackground, outValue, true);
        backButton.setBackgroundResource(outValue.resourceId);
        backButton.setClickable(true);
        backButton.setOnClickListener(view -> onBackPressed());
        int backButtonDimen = (int) getResources().getDimension(R.dimen.back_button), backButtonMarginTop = (int) getResources().getDimension(R.dimen.back_button_margin_top),
                backButtonMarginLeft = (int) getResources().getDimension(R.dimen.back_button_margin_left);
        LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(backButtonDimen, backButtonDimen);
        buttonParams.gravity = Gravity.CENTER_VERTICAL|Gravity.START;
        backButton.setLayoutParams(buttonParams);
        buttonLayout.addView(backButton, 0);
    }
}
