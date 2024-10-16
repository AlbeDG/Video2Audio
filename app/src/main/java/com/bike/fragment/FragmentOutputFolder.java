package com.bike.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.bike.mp3mp4converter.ConfigManager;
import com.bike.mp3mp4converter.Conversion.MetadataManager;
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

public class FragmentOutputFolder extends Fragment {

    View view;

    RecyclerView recyclerView;

    public static ConfigManager configManager;

    public static final String OUTPUT_FILE_KEY = "outputFiles";

    public static Gson gson = new Gson();

    public OutputFileAdapter adapter;

    public LinearLayout selectionButtonsLayout, selectAllLayout;

    ImageButton selectionDeleteButton, selectionShareButton;

    OutputRecyclerDecoration outputRecyclerDecoration;

    public CheckBox selectAllCheckbox;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.fragment_output_folder, container, false);
        selectionButtonsLayout = view.findViewById(R.id.selectionButtonsLayout);
        selectionButtonsLayout.setVisibility(View.GONE);
        selectionDeleteButton = view.findViewById(R.id.selectionDelete);
        selectionShareButton = view.findViewById(R.id.selectionShare);
        selectAllLayout = view.findViewById(R.id.selectAllLayout);
        selectAllCheckbox = view.findViewById(R.id.selectAllCheckbox);
        selectAllCheckbox.setChecked(false);

        recyclerView = view.findViewById(R.id.outputRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.INSTANCE));
        recyclerView.setAdapter(adapter = new OutputFileAdapter(selectionButtonsLayout, selectionDeleteButton, selectionShareButton));
        adapter.createManager();
        int space = getResources().getDimensionPixelOffset(R.dimen.item_spacing);
        outputRecyclerDecoration = new OutputRecyclerDecoration(space);
        recyclerView.addItemDecoration(outputRecyclerDecoration);
        adapter.emptyFolderTextView = view.findViewById(R.id.emptyOutputFolderText);
        init();
        selectAllLayout.setOnClickListener(view1 -> {
            boolean newCheck = !selectAllCheckbox.isChecked();
            selectAllCheckbox.setChecked(newCheck);
            if (newCheck) adapter.selectAll();
            else adapter.deselectAll();
        });
        return view;
    }

    public void init() {
        ArrayList<OutputFile> outputFiles = load();
        if (outputFiles != null) {
            checkList(outputFiles);
            outputFiles.forEach(f -> Log.d("APP", "File name: " + f.title));
            if (OutputFileAdapter.outputFilesManager.isEmpty()) OutputFileAdapter.outputFilesManager.addFiles(outputFiles);
            save();
        }
    }

    static ArrayList<OutputFile> load() {
        if (!configManager.hasString(OUTPUT_FILE_KEY)) {
            return null;
        }
        String jsonList = configManager.loadString(OUTPUT_FILE_KEY);
        Type itemListType = new TypeToken<ArrayList<OutputFile>>(){}.getType();
        return gson.fromJson(jsonList, itemListType);
    }

    public void checkList(ArrayList<OutputFile> files) {
        for (int i = 0; i < files.size(); i++) {
            OutputFile outputFile = files.get(i);
            File currentFile = outputFile.toFile();
            if (!currentFile.exists()) {
                Log.d("APP", "File with name " + currentFile.getName() + " does not exist!");
                files.remove(outputFile);
            } else {
                Log.d("APP", outputFile.toFile().getName());

            }
        }
    }

    public void save() {
        String jsonList = gson.toJson(OutputFileAdapter.outputFilesManager);
        Log.d("APP", "New json to save: " + jsonList + " output files size: " + OutputFileAdapter.outputFilesManager.size());
        configManager.saveString(OUTPUT_FILE_KEY, jsonList);
    }

    public void saveNewFile(OutputFile outputFile, MetadataManager manager) {
        if (manager != null) outputFile.manager = manager;
        OutputFileAdapter.outputFilesManager.addFile(outputFile);
        save();
    }
}