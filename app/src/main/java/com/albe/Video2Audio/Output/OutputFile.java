package com.albe.Video2Audio.Output;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import com.albe.Video2Audio.Conversion.MetadataManager;
import com.albe.Video2Audio.MainActivity;

import org.apache.commons.io.FilenameUtils;

import java.io.File;

public class OutputFile {

    public String title, duration, size, path, bitrate, encoding;

    public MetadataManager manager;

    public OutputFile(File file, String duration, String bitrate, String encoding) {
        this.path = file.getAbsolutePath();
        this.title = file.getName();
        this.duration = duration;
        this.bitrate = bitrate;
        this.encoding = encoding;
        this.size = String.valueOf(file.length());
    }

    public String getFileSizeString() {
        File file = toFile();
        if (!file.exists()) {
            return "File does not exist.";
        }

        long bytes = file.length();
        if (bytes < 1024) {
            return bytes + " bytes";
        } else if (bytes < 1048576) { // 1024 * 1024
            return String.format("%.2f KB", bytes / 1024.0);
        } else if (bytes < 1073741824) { // 1024 * 1024 * 1024
            return String.format("%.2f MB", bytes / 1048576.0);
        } else {
            return String.format("%.2f GB", bytes / 1073741824.0);
        }
    }

    public File toFile() {
        return new File(path);
    }

    public String getNameWithoutExtension() {
        return FilenameUtils.removeExtension(title);
    }

    public String getExtension() {
        return "." + FilenameUtils.getExtension(title);
    }

    public void share(String applicationId) {
        File toFile = toFile();
        if (toFile.exists()) {
            Uri uri = toUri(applicationId);
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            if (shareIntent.resolveActivity(MainActivity.INSTANCE.getPackageManager()) != null) {
                MainActivity.INSTANCE.startActivity(Intent.createChooser(shareIntent, "Share file using"));
            }
        } else Toast.makeText(MainActivity.INSTANCE, "File not found", Toast.LENGTH_LONG).show();
    }

    public void open(String applicationId) {
        File toFile = toFile();
        if (toFile.exists()) {
            Uri uri = toUri(applicationId);
            Intent promptInstall = new Intent(Intent.ACTION_VIEW).setData(uri);
            promptInstall.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            promptInstall.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            MainActivity.INSTANCE.startActivity(promptInstall);
        } else Log.e("APP", "Wanted file does not exist!");
    }

    public Uri toUri(String applicationId) {
        File toFile = toFile();
        return FileProvider.getUriForFile(MainActivity.INSTANCE, applicationId + ".fileprovider", toFile);
    }

}
