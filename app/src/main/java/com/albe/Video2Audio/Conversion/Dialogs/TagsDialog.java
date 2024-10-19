package com.albe.Video2Audio.Conversion.Dialogs;

import static com.albe.Video2Audio.Conversion.ConvertActivity.converter;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import com.albe.Video2Audio.R;

public class TagsDialog extends Dialog {

    EditText titleEditText, artistEditText, albumEditText, yearEditText, genreEditText;

    Button doneButton;
    public TagsDialog(Context context) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.modify_tags_layout);
        //Edit texts
        titleEditText = findViewById(R.id.titleEditText);
        artistEditText = findViewById(R.id.artistEditText);
        albumEditText = findViewById(R.id.albumEditText);
        yearEditText = findViewById(R.id.yearEditText);
        genreEditText = findViewById(R.id.genreEditText);
        //Handle done button pressed
        doneButton = findViewById(R.id.tagsDoneButton);
        doneButton.setOnClickListener(view -> {
            String title = titleEditText.getText().toString(), artist = artistEditText.getText().toString(),
                    album = albumEditText.getText().toString(), year = yearEditText.getText().toString(),
                    genre = yearEditText.getText().toString();
            if (!title.isEmpty()) converter.metadataManager.title = title;
            if (!artist.isEmpty()) converter.metadataManager.artist = artist;
            if (!album.isEmpty()) converter.metadataManager.album = album;
            if (!year.isEmpty()) converter.metadataManager.year = year;
            if (!genre.isEmpty()) converter.metadataManager.genre = genre;
            cancel();
        });
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
