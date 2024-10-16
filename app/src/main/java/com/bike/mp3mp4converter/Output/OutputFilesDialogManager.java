package com.bike.mp3mp4converter.Output;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.bike.mp3mp4converter.MainActivity;
import com.bike.mp3mp4converter.Output.Dialogs.FileDialog;
import com.bike.mp3mp4converter.Output.Dialogs.InfoDialog;
import com.bike.mp3mp4converter.Output.Dialogs.RenameDialog;
import com.bike.mp3mp4converter.R;


public class OutputFilesDialogManager {

    OutputFilesManager outputFilesManager;

    boolean dialogOpen = false;
    public OutputFilesDialogManager(OutputFilesManager outputFilesManager) {
        this.outputFilesManager = outputFilesManager;
    }

    public void showDialog(OutputFile current) {
        dialogOpen = true;
        String applicationId = MainActivity.INSTANCE.getPackageName();
        FileDialog fileDialog = new FileDialog(MainActivity.INSTANCE);
        fileDialog.init(current);
        //Video Title view
        fileDialog.videoTitleView.setText(current.title);
        //Delete layout
        fileDialog.deleteLayout.setOnClickListener(deleteListener(fileDialog, current));
        //Share layout
        fileDialog.shareLayout.setOnClickListener(shareListener(applicationId, current));
        //Open file layout
        fileDialog.openLayout.setOnClickListener(openFileListener(applicationId, current));
        //Rename file layout
        fileDialog.renameLayout.setOnClickListener(renameListener(fileDialog.videoTitleView, current));
        //Info layout
        fileDialog.infoLayout.setOnClickListener(infoListener(current));

        fileDialog.show();
        fileDialog.setOnCancelListener(view -> {
            dialogOpen = false;
        });
    }

    public View.OnClickListener openFileListener(String applicationId, OutputFile current) {
        return view -> current.open(applicationId);
    }
    public View.OnClickListener shareListener(String applicationId, OutputFile current) {
        return view -> current.share(applicationId);
    }
    public View.OnClickListener deleteListener(Dialog dialog, OutputFile current) {
        return view -> {
            DialogInterface.OnClickListener listener = (dialogInterface, i) -> {
                if (i == DialogInterface.BUTTON_POSITIVE) {
                    if (current.toFile().delete() || !current.toFile().exists()) {
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
            InfoDialog infoDialog = new InfoDialog(view.getContext());
            infoDialog.init(current);
            infoDialog.show();
        };
    }
    public View.OnClickListener renameListener(TextView videoTitleView, OutputFile current) {
        return view -> {
            RenameDialog renameDialog = new RenameDialog(view.getContext());
            renameDialog.init(videoTitleView, current, outputFilesManager);
            renameDialog.show();
        };
    }
}
