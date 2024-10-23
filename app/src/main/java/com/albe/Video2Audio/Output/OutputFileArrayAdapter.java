package com.albe.Video2Audio.Output;

import static com.albe.Video2Audio.MainActivity.INSTANCE;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.albe.Video2Audio.MainActivity;
import com.albe.Video2Audio.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class OutputFileArrayAdapter extends BaseAdapter {

    public ListView listView;

    Context ctx;

    public static OutputFilesManager outputFilesManager;

    public TextView emptyFolderTextView;

    HashMap<OutputFile, Boolean> outputSelectionMap = new HashMap<>();

    boolean selecting;

    OutputFilesDialogManager outputFilesDialogManager = new OutputFilesDialogManager();

    LinearLayout selectionButtonsLayout;

    public OutputFileArrayAdapter(Context ctx, LinearLayout selectionButtonsLayout, ImageButton selectionDeleteButton, ImageButton selectionShareButton) {
        this.ctx = ctx;
        this.selectionButtonsLayout = selectionButtonsLayout;
        selectionDeleteButton.setOnClickListener(view -> {
            DialogInterface.OnClickListener listener = (dialogInterface, result) -> {
                ArrayList<OutputFile> selectedFiles = selectedFiles();
                if (result == DialogInterface.BUTTON_POSITIVE) {
                    for (int i = 0; i < selectedFiles.size(); i++) {
                        OutputFile outputFile = selectedFiles.get(i);
                        if (outputFile.toFile().delete() || !outputFile.toFile().exists()) outputFilesManager.deleteFile(selectedFiles.get(i));
                        else Toast.makeText(view.getContext(), "Could not remove the file " + outputFile.title, Toast.LENGTH_LONG).show();
                    }
                }
                dialogInterface.cancel();
                stopSelecting();
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
            ArrayList<OutputFile> selectedFiles = selectedFiles();
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
        if (getSelectAllLayout() != null) {
            getSelectAllLayout().setOnClickListener(view -> {
                selectAllFiles(!allSelected());
                getSelectAllCheckbox().setChecked(allSelected());
            });
        }
    }
    public void createManager() {
        outputFilesManager = new OutputFilesManager(this);
    }

    public boolean selectAllExists() {
        return getSelectAllLayout() != null && getSelectAllCheckbox() != null;
    }

    @Override
    public int getCount() {
        return outputFilesManager.size();
    }

    @Override
    public Object getItem(int i) {
        return outputFilesManager.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(ctx);
        convertView = inflater.inflate(R.layout.layout_output_file, parent, false);
        ViewHolder viewHolder = new ViewHolder(convertView);
        OutputFile currentFile = outputFilesManager.get(position);
        boolean selected = isSelected(currentFile);
        setSelection(currentFile, viewHolder, selected);
        handleGUISelectionChanges(viewHolder, selected);
        viewHolder.title.setText(currentFile.title);
        viewHolder.bitrate.setText("Bitrate: " + currentFile.bitrate);
        viewHolder.duration.setText(currentFile.duration);
        viewHolder.size.setText(currentFile.getFileSizeString());
        viewHolder.layout.setOnClickListener(view -> {
            if (selecting) {
                boolean select = !isSelected(currentFile);
                setSelection(currentFile, viewHolder, select);
            } else {
                if (!outputFilesDialogManager.dialogOpen) outputFilesDialogManager.showDialog(currentFile);
            }
        });
        viewHolder.layout.setOnLongClickListener(view -> {
            if (!selecting) {
                startSelecting();
                setSelection(currentFile, viewHolder, true);
            }
            return true;
        });
        convertView.setTag(viewHolder);
        handleEmptyTextView();
        return convertView;
    }

    boolean allSelected() {
        return selectedFiles().size() == outputFilesManager.size();
    }
    public void selectAllFiles(boolean select) {
        for (OutputFile file : outputFilesManager) {
            outputSelectionMap.put(file, select);
        }
        updateVisibleHolders();
        if (!select) stopSelecting();
    }


    public void handleEmptyTextView() {
        emptyFolderTextView.setVisibility(outputFilesManager.isEmpty() ? View.VISIBLE : View.GONE);
    }
    void updateVisibleHolders() {
        if (!outputFilesManager.isEmpty()) {
            for (int i = 0; i < listView.getChildCount(); i++) {
                View childView = listView.getChildAt(i);
                boolean checked = isSelected(outputFilesManager.get(i));
                OutputFileArrayAdapter.ViewHolder holder = (OutputFileArrayAdapter.ViewHolder) childView.getTag();
                handleGUISelectionChanges(holder, checked);
            }
        } else handleEmptyTextView();

    }

    ArrayList<OutputFile> selectedFiles() {
        ArrayList<OutputFile> files = new ArrayList<>();
        for (Map.Entry<OutputFile, Boolean> entry : outputSelectionMap.entrySet()) {
            OutputFile file = entry.getKey();
            Boolean selected = entry.getValue();
            if (selected) files.add(file);
        }
        return files;
    }

    public void handleGUISelectionChanges(ViewHolder holder, boolean checked) {
        holder.checkBox.setVisibility(selecting ? View.VISIBLE : View.GONE);
        holder.checkBox.setChecked(checked);
        holder.outputFileImageView.setVisibility(selecting ? View.GONE : View.VISIBLE);
    }

    public boolean isSelected(OutputFile file) {
        return Boolean.TRUE.equals(outputSelectionMap.get(file));
    }

    public void setSelection(OutputFile file, ViewHolder holder, boolean selection) {
        if (!selectAllExists()) return;
        outputSelectionMap.put(file, selection);
        holder.checkBox.setChecked(selection);
        //Deselection case
        if (!selection) {
            if (selectedFiles().isEmpty()) stopSelecting();
            handleEmptyTextView();
        }
        getSelectAllCheckbox().setChecked(allSelected());
    }

    public void startSelecting() {
        if (!selectAllExists()) return;
        getSelectAllLayout().setVisibility(View.VISIBLE);
        getSelectAllCheckbox().setChecked(false);
        selecting = true;
        outputSelectionMap.clear();
        for (OutputFile file : outputFilesManager) {
            outputSelectionMap.put(file, false);
        }
        showBottomButtonsShown(true);
        updateVisibleHolders();
    }

    public void stopSelecting() {
        if (!selectAllExists()) return;
        getSelectAllLayout().setVisibility(View.GONE);
        getSelectAllCheckbox().setChecked(false);
        selecting = false;
        updateVisibleHolders();
        showBottomButtonsShown(false);
    }

    public void showBottomButtonsShown(boolean visible) {
        selectionButtonsLayout.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    public LinearLayout getSelectAllLayout() {
        return INSTANCE.getFragmentOutputFolder().selectAllLayout;
    }

    public CheckBox getSelectAllCheckbox() {
        return INSTANCE.getFragmentOutputFolder().selectAllCheckbox;
    }

    public static class ViewHolder {
        public TextView title, duration, size, bitrate;

        public ImageView outputFileImageView;

        LinearLayout layout;

        CheckBox checkBox;

        public ViewHolder(View itemView) {
            layout = itemView.findViewById(R.id.outputFileLayout);
            title = itemView.findViewById(R.id.titleTextView);
            duration = itemView.findViewById(R.id.durationTextView);
            size = itemView.findViewById(R.id.sizeTextView);
            bitrate = itemView.findViewById(R.id.bitrateTextView);
            outputFileImageView = itemView.findViewById(R.id.outputFileImageView);
            checkBox = itemView.findViewById(R.id.checkBox);
        }
    }
}
