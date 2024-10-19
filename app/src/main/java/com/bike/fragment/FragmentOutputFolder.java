package com.bike.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bike.mp3mp4converter.ConfigManager;
import com.bike.mp3mp4converter.MainActivity;
import com.bike.mp3mp4converter.Output.OutputFile;
import com.bike.mp3mp4converter.Output.OutputFileAdapter;
import com.bike.mp3mp4converter.Output.OutputRecyclerDecoration;
import com.bike.mp3mp4converter.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Objects;

public class FragmentOutputFolder extends Fragment {

    View view;

    RecyclerView recyclerView;

    public static ConfigManager configManager;

    public static final String OUTPUT_FILE_KEY = "outputFiles";

    static Gson gson = new Gson();

    public static OutputFileAdapter adapter = new OutputFileAdapter();

    OutputRecyclerDecoration outputRecyclerDecoration;

    public FragmentOutputFolder() {
        init();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.fragment_output_folder, container, false);
        recyclerView = view.findViewById(R.id.outputRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.INSTANCE));
        recyclerView.setAdapter(adapter);
        int space = getResources().getDimensionPixelOffset(R.dimen.item_spacing);
        outputRecyclerDecoration = new OutputRecyclerDecoration(space);
        recyclerView.addItemDecoration(outputRecyclerDecoration);
        adapter.emptyFolderTextView = view.findViewById(R.id.emptyOutputFolderText);
        return view;
    }

    public void init() {
        ArrayList<OutputFile> outputFiles = load();
        if (outputFiles != null) {
            checkList(outputFiles);
            if (adapter.outputFilesManager.isEmpty()) adapter.outputFilesManager.addFiles(load());
        }
    }

    static ArrayList<OutputFile> load() {
        if (!configManager.hasString(OUTPUT_FILE_KEY)) return null;
        String jsonList = configManager.loadString(OUTPUT_FILE_KEY);
        Type itemListType = new TypeToken<ArrayList<OutputFile>>(){}.getType();
        return gson.fromJson(jsonList, itemListType);
    }

    public void checkList(ArrayList<OutputFile> files) {
        boolean changes = false;
        for (int i = 0; i < files.size(); i++) {
            OutputFile outputFile = files.get(i);
            File currentFile = outputFile.toFile();
            if (!currentFile.exists()) {
                changes = true;
                files.remove(outputFile);
            }
        }
        if (changes) save();
    }

    public static void save() {
        if (adapter.outputFilesManager == null) return;
        String jsonList = gson.toJson(adapter.outputFilesManager);
        configManager.saveString(OUTPUT_FILE_KEY, jsonList);
    }

    public static void saveNewFile(File file, String duration, String bitrate, String encoding) {
        OutputFile outputFile = new OutputFile(file, duration, bitrate, encoding);
        adapter.outputFilesManager.addFile(outputFile);
        save();
    }
}