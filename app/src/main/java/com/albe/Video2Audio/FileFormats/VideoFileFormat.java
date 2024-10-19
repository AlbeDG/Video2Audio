package com.albe.Video2Audio.FileFormats;

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

    public static String[] extArray() {
        VideoFileFormat[] formats = VideoFileFormat.values();
        String[] extArray = new String[formats.length];
        for (int i = 0; i < formats.length; i++) {
            extArray[i] = formats[i].getFormatStr();
        }
        return extArray;
    }
}
