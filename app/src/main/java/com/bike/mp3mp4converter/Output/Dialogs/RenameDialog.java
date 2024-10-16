package com.bike.mp3mp4converter.Output.Dialogs;

import android.app.Dialog;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.bike.mp3mp4converter.MainActivity;
import com.bike.mp3mp4converter.Output.OutputFile;
import com.bike.mp3mp4converter.Output.OutputFilesManager;
import com.bike.mp3mp4converter.R;

import java.io.File;

public class RenameDialog extends Dialog {

    public RenameDialog(Context context) {
        super(context);
    }

    public void init(TextView videoTitleView, OutputFile current, OutputFilesManager outputFilesManager) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.rename_file_layout);
        //Input text
        EditText editText = findViewById(R.id.renameInputText);
        editText.setText(current.getNameWithoutExtension());
        //Done button
        Button doneButton = findViewById(R.id.renameDoneButton);
        doneButton.setEnabled(false);
        doneButton.setOnClickListener(view1 -> {
            outputFilesManager.deleteFile(current);
            String newTitle = editText.getText().toString() + current.getExtension(), newFilePath = current.path.replace(current.title, newTitle);
            File oldFile = current.toFile(), newFile = new File(newFilePath);
            current.path = newFilePath;
            current.title = newTitle;
            Log.d("APP", "New title: " + current.title + " New path: " + current.path);
            if (oldFile.renameTo(newFile)) {
                oldFile.delete();
                OutputFile outputFile = new OutputFile(newFile, current.duration, current.bitrate, current.encoding);
                MainActivity.INSTANCE.getFragmentOutputFolder().saveNewFile(outputFile, current.manager);
                cancel();
                videoTitleView.setText(newTitle);
            } else Log.e("RenameError", "File rename failed. Source: " + oldFile.getAbsolutePath() +
                    ", Destination: " + newFile.getAbsolutePath());
        });

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                doneButton.setEnabled(!charSequence.toString().equals(current.title) && !outputFilesManager.containsFile(charSequence.toString() + current.getExtension()));
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        //Cancel button
        Button cancelButton = findViewById(R.id.renameCancelButton);
        cancelButton.setOnClickListener(v -> cancel());
    }

    @Override
    public void show() {
        super.show();
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }
}
