package com.bike.mp3mp4converter.Output;

import org.apache.commons.io.FilenameUtils;

import java.io.File;

public class OutputFile {
    public String title, duration, size, path, bitrate, encoding;

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

}
