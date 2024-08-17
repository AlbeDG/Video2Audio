package com.bike.mp3mp4converter.Output;

import java.util.ArrayList;

public class OutputFilesManager extends ArrayList<OutputFile>{

    OutputFileAdapter adapter;
    public OutputFilesManager(OutputFileAdapter adapter) {
        this.adapter = adapter;
    }

    public void addFile(OutputFile file) {
        add(file);
        adapter.notifyItemInserted(size() - 1);
    }

    public void deleteFile(OutputFile file) {
        int index = indexOf(file);
        remove(index);
        adapter.notifyItemRemoved(index);
        adapter.checkEmptyFolder();
    }

    public void addFiles(ArrayList<OutputFile> files) {
        int start = size();
        addAll(files);
        adapter.notifyItemRangeInserted(start, files.size());
    }

    public boolean containsFile(String name) {
        for (int i = 0; i < size(); i++) {
            OutputFile file = get(i);
            if (file.title.equals(name)) return true;
        }
        return false;
    }

}
