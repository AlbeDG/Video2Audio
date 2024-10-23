package com.albe.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import com.albe.Video2Audio.ConfigManager;
import com.albe.Video2Audio.Conversion.MetadataManager;
import com.albe.Video2Audio.Output.OutputFile;
import com.albe.Video2Audio.Output.OutputFileArrayAdapter;
import com.albe.Video2Audio.Output.OutputRecyclerDecoration;
import com.albe.Video2Audio.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class FragmentOutputFolder extends Fragment {

    View view;

    public static ConfigManager configManager;

    public static final String OUTPUT_FILE_KEY = "outputFiles";

    public static Gson gson = new Gson();

    public OutputFileArrayAdapter adapter;

    public LinearLayout selectionButtonsLayout, selectAllLayout;

    ImageButton selectionDeleteButton, selectionShareButton;

    OutputRecyclerDecoration outputRecyclerDecoration;

    public CheckBox selectAllCheckbox;

    ListView listView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
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
        adapter = new OutputFileArrayAdapter(getContext(), selectionButtonsLayout, selectionDeleteButton, selectionShareButton);
        adapter.createManager();
        listView = view.findViewById(R.id.outputFilesList);
        listView.setAdapter(adapter);
        adapter.listView = listView;
        int space = getResources().getDimensionPixelOffset(R.dimen.item_spacing);
        outputRecyclerDecoration = new OutputRecyclerDecoration(space);
        adapter.emptyFolderTextView = view.findViewById(R.id.emptyOutputFolderText);
        adapter.handleEmptyTextView();
        init();
        return view;
    }


    public void init() {
        ArrayList<OutputFile> outputFiles = load();
        if (outputFiles != null) {
            checkList(outputFiles);
            outputFiles.forEach(f -> Log.d("APP", "File name: " + f.title));
            if (OutputFileArrayAdapter.outputFilesManager.isEmpty()) OutputFileArrayAdapter.outputFilesManager.addFiles(outputFiles);
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
        String jsonList = gson.toJson(OutputFileArrayAdapter.outputFilesManager);
        Log.d("APP", "New json to save: " + jsonList + " output files size: " + OutputFileArrayAdapter.outputFilesManager.size());
        configManager.saveString(OUTPUT_FILE_KEY, jsonList);
    }

    public void saveNewFile(OutputFile outputFile, MetadataManager manager) {
        if (manager != null) outputFile.manager = manager;
        OutputFileArrayAdapter.outputFilesManager.addFile(outputFile);
        save();
    }
}