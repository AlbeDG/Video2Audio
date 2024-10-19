package com.albe.Video2Audio.Conversion;

public enum AudioChannel {
    STEREO,
    MONO;

    public int getAcOptionValue() {
        return this == STEREO ? 2 : 1;
    }
}
