package com.bike.mp3mp4converter.Info;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bike.mp3mp4converter.Output.OutputFile;
import com.bike.mp3mp4converter.R;

import java.util.LinkedHashMap;
import java.util.Map;

public class InfoRecyclerAdapter extends RecyclerView.Adapter<InfoRecyclerAdapter.InfoHolder> {

    LinkedHashMap<String, String> nameValuesMap = new LinkedHashMap<>();

    @NonNull
    @Override
    public InfoHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.info_element_layout, parent, false);
        return new InfoHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull InfoHolder holder, int position) {
        Map.Entry<String, String> entry = (Map.Entry<String, String>) nameValuesMap.entrySet().stream().toArray()[position];
        holder.infoName.setText(entry.getKey());
        holder.infoValue.setText(entry.getValue());
    }

    @Override
    public int getItemCount() {
        return nameValuesMap.size();
    }

    public void addValues(OutputFile outputFile) {
        nameValuesMap.put("Name", outputFile.title);
        nameValuesMap.put("Path", outputFile.path);
        nameValuesMap.put("Duration", outputFile.duration);
        nameValuesMap.put("Bitrate", outputFile.bitrate);
        nameValuesMap.put("Extension", outputFile.getExtension());
        nameValuesMap.put("Size", outputFile.getFileSizeString());
        nameValuesMap.put("Encoding", outputFile.encoding);
        if (outputFile.manager != null) {
            nameValuesMap.put("Metadatas", outputFile.manager.toString());
        }
    }
    public class InfoHolder extends RecyclerView.ViewHolder {

        TextView infoName, infoValue;
        public InfoHolder(@NonNull View itemView) {
            super(itemView);
            infoName = itemView.findViewById(R.id.infoName);
            infoValue = itemView.findViewById(R.id.infoValue);
        }
    }
}
