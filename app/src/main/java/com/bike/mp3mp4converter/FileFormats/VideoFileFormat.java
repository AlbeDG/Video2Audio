package com.bike.mp3mp4converter.FileFormats;

public enum VideoFileFormat {
    MP4(".mp4"),
    MKV(".mkv"),
    AVI(".avi"),
    MOV(".mov"),
    FLV(".flv"),
    WEBM(".webm"),
    MPEG(".mpeg"),
    MPG(".mpg"),
    OGV(".ogv"),
    WMV(".wmv"),
    ASF(".asf"),
    _3GP(".3gp");

    private final String formatStr;

    VideoFileFormat(String formatStr) {
        this.formatStr = formatStr;
    }

    public String getFormatStr() {
        return formatStr;
    }

    public static VideoFileFormat fromString(String formatStr) {
        for (VideoFileFormat format : VideoFileFormat.values()) {
            if (format.getFormatStr().equalsIgnoreCase(formatStr)) {
                return format;
            }
        }
        throw new IllegalArgumentException("Unknown format: " + formatStr);
    }
}
