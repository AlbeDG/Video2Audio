package com.bike.fragment;

import static android.app.Activity.RESULT_OK;
import static com.bike.mp3mp4converter.MainActivity.FILE_REQUEST_RESULT;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.bike.mp3mp4converter.Conversion.ConvertActivity;
import com.bike.mp3mp4converter.R;


public class FragmentHome extends Fragment implements View.OnClickListener {

    View view;


    Button selectFileButton, outputFolderButton;

    public FragmentHome() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.fragment_home, container, false);
        selectFileButton = view.findViewById(R.id.selectFileButton);
        selectFileButton.setOnClickListener(view -> {
            openFilePicker();
        });

        return view;
    }


    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("video/*");
        startActivityForResult(Intent.createChooser(intent, "Select a video"), FILE_REQUEST_RESULT);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FILE_REQUEST_RESULT && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            Intent intent = new Intent(getContext(), ConvertActivity.class);
            intent.putExtra("videoURI", uri.toString());
            startActivity(intent);
        }
    }


    @Override
    public void onClick(View view) {

    }
}