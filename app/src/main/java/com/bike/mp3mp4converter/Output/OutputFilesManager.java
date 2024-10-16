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
        OutputFile toDelete = null;
        for (OutputFile f : this) {
            if (f.title.equals(file.title)) {
                toDelete = f;
            }
        }
        if (toDelete != null) {
            int index = indexOf(toDelete);
            remove(index);
            adapter.notifyItemRemoved(index);
            adapter.checkEmptyFolder();
        }
    }

    public void addFiles(ArrayList<OutputFile> files) {
        int start = size(), addedCount = 0;
        for (OutputFile file : files) {
            if (file.toFile().exists()) {
                add(file);
                addedCount++;
            }
        }
        adapter.notifyItemRangeInserted(start, start + addedCount);
    }

    public boolean containsFile(String name) {
        for (int i = 0; i < size(); i++) {
            OutputFile file = get(i);
            if (file.title.equals(name)) return true;
        }
        return false;
    }

}
