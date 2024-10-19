package com.bike.mp3mp4converter.Output;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bike.fragment.FragmentOutputFolder;
import com.bike.mp3mp4converter.Info.InfoRecyclerAdapter;
import com.bike.mp3mp4converter.MainActivity;
import com.bike.mp3mp4converter.R;

import java.io.File;

public class OutputFilesDialogManager {

    OutputFilesManager outputFilesManager;

    boolean dialogOpen = false;
    public OutputFilesDialogManager(OutputFilesManager outputFilesManager) {
        this.outputFilesManager = outputFilesManager;
    }

    public void showDialog(OutputFile current) {
        dialogOpen = true;
        String applicationId = MainActivity.INSTANCE.getPackageName();
        Dialog dialog = new Dialog(MainActivity.INSTANCE);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottom_sheet_layout);
        //Video Title view
        TextView videoTitleView = dialog.findViewById(R.id.videoTitleDialog);
        videoTitleView.setText(current.title);
        //Delete layout
        LinearLayout deleteLayout = dialog.findViewById(R.id.deleteLayout);
        deleteLayout.setOnClickListener(deleteListener(dialog, current));
        //Share layout
        LinearLayout shareLayout = dialog.findViewById(R.id.shareLayout);
        shareLayout.setOnClickListener(shareListener(applicationId, current));
        //Open file layout
        LinearLayout openLayout = dialog.findViewById(R.id.openLayout);
        openLayout.setOnClickListener(openFileListener(applicationId, current));
        //Rename file layout
        LinearLayout renameLayout = dialog.findViewById(R.id.renameLayout);
        renameLayout.setOnClickListener(renameListener(videoTitleView, current));
        //Info layout
        LinearLayout infoLayout = dialog.findViewById(R.id.infoLayout);
        infoLayout.setOnClickListener(infoListener(current));
        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        dialog.setOnCancelListener(view -> {
            dialogOpen = false;
        });
    }

    public View.OnClickListener openFileListener(String applicationId, OutputFile current) {
        return view -> {
            File toFile = current.toFile();
            if (toFile.exists()) {
                Uri uri = FileProvider.getUriForFile(MainActivity.INSTANCE, applicationId + ".fileprovider", toFile);
                Intent promptInstall = new Intent(Intent.ACTION_VIEW).setData(uri);
                promptInstall.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                promptInstall.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                MainActivity.INSTANCE.startActivity(promptInstall);
            } else Log.e("APP", "Wanted file does not exist!");
        };
    }
    public View.OnClickListener shareListener(String applicationId, OutputFile current) {
        return view -> {
            File toFile = current.toFile();
            if (toFile.exists()) {
                Uri uri = FileProvider.getUriForFile(MainActivity.INSTANCE, applicationId + ".fileprovider", toFile);
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
                shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                if (shareIntent.resolveActivity(MainActivity.INSTANCE.getPackageManager()) != null) {
                    MainActivity.INSTANCE.startActivity(Intent.createChooser(shareIntent, "Share file using"));
                }
            } else Toast.makeText(MainActivity.INSTANCE, "File not found", Toast.LENGTH_LONG).show();
        };
    }
    public View.OnClickListener deleteListener(Dialog dialog, OutputFile current) {
        return view -> {
            DialogInterface.OnClickListener listener = (dialogInterface, i) -> {
                if (i == DialogInterface.BUTTON_POSITIVE) {
                    if (current.toFile().delete()) {
                        outputFilesManager.deleteFile(current);
                        dialog.cancel();
                    } else Toast.makeText(MainActivity.INSTANCE, "Could not remove this file!", Toast.LENGTH_LONG).show();
                }
            };
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.INSTANCE);
            AlertDialog alertDialog = builder.setMessage("Are you sure to delete this file?").setPositiveButton("Yes", listener).setNegativeButton("No", listener).create();
            alertDialog.show();
            Button positive = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE), negative = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
            int lightBlue = ContextCompat.getColor(MainActivity.INSTANCE, R.color.light_blue);
            positive.setTextColor(lightBlue);
            negative.setTextColor(lightBlue);
        };
    }

    public View.OnClickListener infoListener(OutputFile current) {
        return view -> {
            Dialog dialog = new Dialog(MainActivity.INSTANCE);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.info_file_layout);
            //Init recycler
            RecyclerView recyclerView = dialog.findViewById(R.id.infoRecycler);
            InfoRecyclerAdapter infoRecyclerAdapter = new InfoRecyclerAdapter();
            infoRecyclerAdapter.addValues(current);
            recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.INSTANCE));
            recyclerView.setAdapter(infoRecyclerAdapter);
            dialog.show();
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        };
    }
    public View.OnClickListener renameListener(TextView videoTitleView, OutputFile current) {
        return view -> {
            Dialog dialog = new Dialog(MainActivity.INSTANCE);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.rename_file_layout);
            //Input text
            EditText editText = dialog.findViewById(R.id.renameInputText);
            editText.setText(current.getNameWithoutExtension());
            //Done button
            Button doneButton = dialog.findViewById(R.id.renameDoneButton);
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
                    FragmentOutputFolder.saveNewFile(newFile, current.duration, current.bitrate, current.encoding);
                    dialog.cancel();
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
            Button cancelButton = dialog.findViewById(R.id.renameCancelButton);
            cancelButton.setOnClickListener(v -> dialog.cancel());
            dialog.show();
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        };
    }
}
