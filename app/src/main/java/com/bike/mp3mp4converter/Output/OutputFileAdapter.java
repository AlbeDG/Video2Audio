package com.bike.mp3mp4converter.Output;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bike.mp3mp4converter.R;


public class OutputFileAdapter extends RecyclerView.Adapter<OutputFileAdapter.OutputFileHolder> {

    public OutputFilesManager outputFilesManager = new OutputFilesManager(this);

    OutputFilesDialogManager outputFilesDialogManager = new OutputFilesDialogManager(outputFilesManager);
    public TextView emptyFolderTextView;

    boolean dialogOpen = false;

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
        checkEmptyFolder();
        OutputFile current = outputFilesManager.get(position);
        holder.title.setText(current.title);
        holder.bitrate.setText("Bitrate: " + current.bitrate);
        holder.duration.setText(current.duration);
        holder.encoding.setText("Encoding: " + current.encoding);
        holder.size.setText(current.getFileSizeString());
        holder.layout.setOnClickListener(view -> {
            if (!dialogOpen) outputFilesDialogManager.showDialog(current);
        });
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

        ConstraintLayout layout;
        public OutputFileHolder(View itemView) {
            super(itemView);
            layout = itemView.findViewById(R.id.outputFileLayout);
            title = itemView.findViewById(R.id.titleTextView);
            duration = itemView.findViewById(R.id.durationTextView);
            size = itemView.findViewById(R.id.sizeTextView);
            bitrate = itemView.findViewById(R.id.bitrateTextView);
            encoding = itemView.findViewById(R.id.encodingTextView);
        }
    }
}
