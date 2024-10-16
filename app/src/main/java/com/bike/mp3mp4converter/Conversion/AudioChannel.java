package com.bike.mp3mp4converter.Conversion;

public enum AudioChannel {
    STEREO,
    MONO;

    public int getAcOptionValue() {
        return this == STEREO ? 2 : 1;
    }
}
