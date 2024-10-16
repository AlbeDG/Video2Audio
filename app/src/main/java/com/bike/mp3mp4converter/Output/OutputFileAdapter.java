package com.bike.mp3mp4converter.Output;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bike.mp3mp4converter.MainActivity;
import com.bike.mp3mp4converter.R;

import java.util.ArrayList;

import static com.bike.mp3mp4converter.MainActivity.*;


public class OutputFileAdapter extends RecyclerView.Adapter<OutputFileAdapter.OutputFileHolder> {

    public static OutputFilesManager outputFilesManager;

    OutputFilesDialogManager outputFilesDialogManager = new OutputFilesDialogManager(outputFilesManager);

    public TextView emptyFolderTextView;

    boolean selection = false;

    ArrayList<OutputFile> selectedFiles = new ArrayList<>();

    ArrayList<OutputFileHolder> holderList = new ArrayList<>();

    LinearLayout selectionButtonsLayout;

    public OutputFileAdapter(LinearLayout selectionButtonsLayout, ImageButton selectionDeleteButton, ImageButton selectionShareButton) {
        this.selectionButtonsLayout = selectionButtonsLayout;
        selectionDeleteButton.setOnClickListener(view -> {
            DialogInterface.OnClickListener listener = (dialogInterface, result) -> {
                if (result == DialogInterface.BUTTON_POSITIVE) {
                    for (int i = 0; i < selectedFiles.size(); i++) {
                        OutputFile outputFile = selectedFiles.get(i);
                        if (outputFile.toFile().delete() || !outputFile.toFile().exists()) outputFilesManager.deleteFile(selectedFiles.get(i));
                        else Toast.makeText(view.getContext(), "Could not remove the file " + outputFile.title, Toast.LENGTH_LONG).show();
                    }
                }
                dialogInterface.cancel();
                unToggleSelection();
            };
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.INSTANCE);
            AlertDialog alertDialog = builder.setMessage("Are you sure you want to delete these files?").setPositiveButton("Yes", listener).setNegativeButton("No", listener).create();
            alertDialog.show();
            Button positive = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE), negative = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
            int lightBlue = ContextCompat.getColor(MainActivity.INSTANCE, R.color.light_blue);
            positive.setTextColor(lightBlue);
            negative.setTextColor(lightBlue);


        });
        selectionShareButton.setOnClickListener(view -> {
            String applicationId = MainActivity.INSTANCE.getPackageName();
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_SEND_MULTIPLE);
            intent.putExtra(Intent.EXTRA_SUBJECT, "Share files using...");
            intent.setType("audio/*");
            ArrayList<Uri> uriList = new ArrayList<>();
            for (int i = 0; i < selectedFiles.size(); i++) {
                OutputFile file = selectedFiles.get(i);
                uriList.add(file.toUri(applicationId));
            }
            intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uriList);
            MainActivity.INSTANCE.startActivity(intent);

        });
    }

    public void createManager() {
        outputFilesManager = new OutputFilesManager(this);
    }

    @NonNull
    @Override
    public OutputFileHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_output_file, parent, false);
        checkEmptyFolder();
        return new OutputFileHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull OutputFileHolder holder, int position) {
        holderList.add(holder);
        checkEmptyFolder();
        OutputFile current = outputFilesManager.get(position);
        holder.title.setText(isSelected(current) ? "Selected" : current.title);
        holder.bitrate.setText("Bitrate: " + current.bitrate);
        holder.duration.setText(current.duration);
        holder.encoding.setText("Encoding: " + current.encoding);
        holder.size.setText(current.getFileSizeString());
        holder.layout.setOnClickListener(view -> {
            if (selection) {
                if (isSelected(current)) deselect(holder, current);
                else select(holder, current);
            }
            else {
                if (!outputFilesDialogManager.dialogOpen) {
                    outputFilesDialogManager.showDialog(current);
                }
            }
        });
        holder.layout.setOnLongClickListener(view -> {
            if (selection) {
                if (!isSelected(current)) select(holder, current);
            } else toggleSelection(holder, current);
            return true;
        });
    }

    @Override
    public void onViewRecycled(@NonNull OutputFileHolder holder) {
        holderList.remove(holder);
    }

    public boolean isSelected(OutputFile file) {
        return selectedFiles.contains(file);
    }

    public LinearLayout getSelectAllLayout() {
        return INSTANCE.getFragmentOutputFolder().selectAllLayout;
    }

    public CheckBox getSelectAllCheckbox() {
        return INSTANCE.getFragmentOutputFolder().selectAllCheckbox;
    }

    public void selectAll() {
        for (int i = 0; i < holderList.size(); i++) {
            OutputFileHolder holder = holderList.get(i);
            OutputFile outputFile = outputFilesManager.get(i);
            select(holder, outputFile);
        }
    }

    public void deselectAll() {
        for (int i = 0; i < holderList.size(); i++) {
            OutputFileHolder holder = holderList.get(i);
            OutputFile outputFile = outputFilesManager.get(i);
            deselect(holder, outputFile);
        }
        unToggleSelection();
    }

    public void select(OutputFileHolder holder, OutputFile file) {
        if (!selectedFiles.contains(file)) {
            showButtons(true);
            if (!selection) selection = true;
            getSelectAllLayout().setVisibility(View.VISIBLE);
            holder.layout.setSelected(true);
            holder.checkBox.setChecked(true);
            holder.checkBox.setVisibility(View.VISIBLE);
            selectedFiles.add(file);
        }
    }

    public void deselect(OutputFileHolder holder, OutputFile file) {
        if (selectedFiles.contains(file)) {
            getSelectAllCheckbox().setChecked(false);
            holder.title.setText(file.title);
            selectedFiles.remove(file);
            holder.checkBox.setChecked(false);
            if (selectedFiles.isEmpty()) unToggleSelection();
        }
    }

    private void toggleSelection() {
        if (!selection) selection = true;
        showCheckBoxes(true);
        showImages(false);
    }

    public void toggleSelection(OutputFileHolder holder, OutputFile firstFile) {
        toggleSelection();
        select(holder, firstFile);
    }

    public void unToggleSelection() {
        showButtons(false);
        showCheckBoxes(false);
        if (selection) selection = false;
        showImages(true);
        getSelectAllLayout().setVisibility(View.GONE);
    }

    public void showCheckBoxes(boolean show) {
        for (int i = 0; i < holderList.size(); i++) holderList.get(i).checkBox.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    public void showImages(boolean show) {
        for (int i = 0; i < holderList.size(); i++) holderList.get(i).outputFileImageView.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    public void showButtons(boolean show) {
        selectionButtonsLayout.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    @Override
    public int getItemCount() {
        return outputFilesManager.size();
    }

    public void checkEmptyFolder() {
        emptyFolderTextView.setVisibility(outputFilesManager.isEmpty() ? View.VISIBLE : View.GONE);
    }

    public static class OutputFileHolder extends RecyclerView.ViewHolder {

        public TextView title, duration, size, bitrate, encoding;

        public ImageView outputFileImageView;

        LinearLayout layout;

        CheckBox checkBox;

        public OutputFileHolder(View itemView) {
            super(itemView);
            layout = itemView.findViewById(R.id.outputFileLayout);
            title = itemView.findViewById(R.id.titleTextView);
            duration = itemView.findViewById(R.id.durationTextView);
            size = itemView.findViewById(R.id.sizeTextView);
            bitrate = itemView.findViewById(R.id.bitrateTextView);
            encoding = itemView.findViewById(R.id.encodingTextView);
            outputFileImageView = itemView.findViewById(R.id.outputFileImageView);
            checkBox = itemView.findViewById(R.id.checkBox);
        }
    }
}
