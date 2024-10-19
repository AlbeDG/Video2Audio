package com.albe.Video2Audio.FileFormats;

public enum AudioFileFormat {
    MP3(".mp3", true, true),
    WAV(".wav", false, false),
    AAC(".aac", true, true),
    FLAC(".flac", false, false),
    OPUS(".opus", true, true),
    M4A(".m4a", true, true),
    WMA(".wma", true, false),
    AIFF(".aiff", false, false),
    AMR(".amr", true, false);

    public final boolean VBR, CBR;
    private final String formatStr;

    AudioFileFormat(String formatStr, boolean CBR, boolean VBR) {
        this.formatStr = formatStr;
        this.CBR = CBR;
        this.VBR = VBR;
    }

    public String getFormatStr() {
        return formatStr;
    }

    public static AudioFileFormat fromString(String formatStr) {
        for (AudioFileFormat format : values()) {
            if (format.getFormatStr().equalsIgnoreCase("." + formatStr)) {
                return format;
            }
        }
        throw new IllegalArgumentException("Unknown format: " + formatStr);
    }

    public boolean isMp3() {
        return this == MP3;
    }

    public boolean isWav() {
        return this == WAV;
    }

    public boolean isAac() {
        return this == AAC;
    }

    public boolean isFlac() {
        return this == FLAC;
    }

    public boolean isOpus() {
        return this == OPUS;
    }

    public boolean isM4a() {
        return this == M4A;
    }

    public boolean isWma() {
        return this == WMA;
    }

    public boolean isAiff() {
        return this == AIFF;
    }

    public boolean isAmr() {
        return this == AMR;
    }
}