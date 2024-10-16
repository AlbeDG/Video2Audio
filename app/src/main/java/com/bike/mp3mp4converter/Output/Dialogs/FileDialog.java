package com.bike.mp3mp4converter.Output.Dialogs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bike.mp3mp4converter.Output.OutputFile;
import com.bike.mp3mp4converter.R;

public class FileDialog extends Dialog {

    public TextView videoTitleView;

    public LinearLayout deleteLayout, shareLayout, openLayout, renameLayout, infoLayout;

    public FileDialog(Context context) {
        super(context);
    }

    public void init(OutputFile current) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.bottom_sheet_layout);
        //Video Title view
        videoTitleView = findViewById(R.id.videoTitleDialog);
        videoTitleView.setText(current.title);
        //Delete layout
        deleteLayout = findViewById(R.id.deleteLayout);
        //Share layout
        shareLayout = findViewById(R.id.shareLayout);
        //Open file layout
        openLayout = findViewById(R.id.openLayout);
        //Rename file layout
        renameLayout = findViewById(R.id.renameLayout);
        //Info layout
        infoLayout = findViewById(R.id.infoLayout);
    }

    @Override
    public void show() {
        super.show();
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        getWindow().setGravity(Gravity.BOTTOM);
    }
}
