package com.bike.mp3mp4converter.Conversion;

import androidx.annotation.NonNull;

import com.bike.mp3mp4converter.FileFormats.AudioFileFormat;

import java.util.Arrays;

public class AudioFrequencies {
    public static final AudioFrequency[] MP3_FREQUENCIES = {
            AudioFrequency.FREQUENCY_8000,
            AudioFrequency.FREQUENCY_11025,
            AudioFrequency.FREQUENCY_12000,
            AudioFrequency.FREQUENCY_16000,
            AudioFrequency.FREQUENCY_22050,
            AudioFrequency.FREQUENCY_24000,
            AudioFrequency.FREQUENCY_32000,
            AudioFrequency.FREQUENCY_44100,
            AudioFrequency.FREQUENCY_48000
    };

    public static final AudioFrequency[] AAC_FREQUENCIES = {
            AudioFrequency.FREQUENCY_8000,
            AudioFrequency.FREQUENCY_11025,
            AudioFrequency.FREQUENCY_12000,
            AudioFrequency.FREQUENCY_16000,
            AudioFrequency.FREQUENCY_22050,
            AudioFrequency.FREQUENCY_24000,
            AudioFrequency.FREQUENCY_32000,
            AudioFrequency.FREQUENCY_44100,
            AudioFrequency.FREQUENCY_48000,
            AudioFrequency.FREQUENCY_64000,
            AudioFrequency.FREQUENCY_88200,
            AudioFrequency.FREQUENCY_96000
    };

    public static final AudioFrequency[] AMR_FREQUENCIES = {
            AudioFrequency.FREQUENCY_8000,  // AMR-NB
    };

    public static final AudioFrequency[] AIFF_FREQUENCIES = {
            AudioFrequency.FREQUENCY_8000,
            AudioFrequency.FREQUENCY_11025,
            AudioFrequency.FREQUENCY_22050,
            AudioFrequency.FREQUENCY_32000,
            AudioFrequency.FREQUENCY_44100,
            AudioFrequency.FREQUENCY_48000,
            AudioFrequency.FREQUENCY_96000,
            AudioFrequency.FREQUENCY_192000
    };

    public static final AudioFrequency[] FLAC_FREQUENCIES = {
            AudioFrequency.FREQUENCY_44100,
            AudioFrequency.FREQUENCY_48000,
            AudioFrequency.FREQUENCY_96000,
            AudioFrequency.FREQUENCY_192000
    };

    public static final AudioFrequency[] M4A_FREQUENCIES = {
            AudioFrequency.FREQUENCY_8000,
            AudioFrequency.FREQUENCY_11025,
            AudioFrequency.FREQUENCY_12000,
            AudioFrequency.FREQUENCY_16000,
            AudioFrequency.FREQUENCY_22050,
            AudioFrequency.FREQUENCY_24000,
            AudioFrequency.FREQUENCY_32000,
            AudioFrequency.FREQUENCY_44100,
            AudioFrequency.FREQUENCY_48000,
            AudioFrequency.FREQUENCY_64000,
            AudioFrequency.FREQUENCY_88200,
            AudioFrequency.FREQUENCY_96000
    };

    public static final AudioFrequency[] OGG_FREQUENCIES = {
            AudioFrequency.FREQUENCY_8000,
            AudioFrequency.FREQUENCY_16000,
            AudioFrequency.FREQUENCY_22050,
            AudioFrequency.FREQUENCY_32000,
            AudioFrequency.FREQUENCY_44100,
            AudioFrequency.FREQUENCY_48000
    };

    public static final AudioFrequency[] OPUS_FREQUENCIES = {
            AudioFrequency.FREQUENCY_8000,
            AudioFrequency.FREQUENCY_12000,
            AudioFrequency.FREQUENCY_16000,
            AudioFrequency.FREQUENCY_24000,
            AudioFrequency.FREQUENCY_48000
    };

    public static final AudioFrequency[] WAV_FREQUENCIES = {
            AudioFrequency.FREQUENCY_8000,
            AudioFrequency.FREQUENCY_11025,
            AudioFrequency.FREQUENCY_16000,
            AudioFrequency.FREQUENCY_22050,
            AudioFrequency.FREQUENCY_32000,
            AudioFrequency.FREQUENCY_44100,
            AudioFrequency.FREQUENCY_48000,
            AudioFrequency.FREQUENCY_96000,
            AudioFrequency.FREQUENCY_192000
    };

    public static final AudioFrequency[] WMA_FREQUENCIES = {
            AudioFrequency.FREQUENCY_8000,
            AudioFrequency.FREQUENCY_11025,
            AudioFrequency.FREQUENCY_12000,
            AudioFrequency.FREQUENCY_16000,
            AudioFrequency.FREQUENCY_22050,
            AudioFrequency.FREQUENCY_24000,
            AudioFrequency.FREQUENCY_32000,
            AudioFrequency.FREQUENCY_44100,
            AudioFrequency.FREQUENCY_48000,
    };

    public static int defaultAudioFrequencyIndex(AudioFileFormat format) {
        return switch (format) {
            case MP3 -> getIndexOfFrequency(MP3_FREQUENCIES, 44100);
            case AAC -> getIndexOfFrequency(AAC_FREQUENCIES, 44100);
            case WAV -> getIndexOfFrequency(WAV_FREQUENCIES, 44100);
            case FLAC -> getIndexOfFrequency(FLAC_FREQUENCIES, 44100);
            case OGG -> getIndexOfFrequency(OGG_FREQUENCIES, 44100);
            case OPUS -> getIndexOfFrequency(OPUS_FREQUENCIES, 48000);
            case M4A -> getIndexOfFrequency(M4A_FREQUENCIES, 44100);
            case WMA -> getIndexOfFrequency(WMA_FREQUENCIES, 44100);
            case AIFF -> getIndexOfFrequency(AIFF_FREQUENCIES, 44100);
            case AMR -> getIndexOfFrequency(AMR_FREQUENCIES, 8000);
        };
    }

    static int getIndexOfFrequency(AudioFrequency[] frequencies, int frequency) {
        for (int i = 0; i < frequencies.length; i++) {
            AudioFrequency freq = frequencies[i];
            if (freq.frequency == frequency) return i;
        }
        return 0;
    }

    public static AudioFrequency[] fromFormat(AudioFileFormat format) {
        return switch (format) {
            case MP3 -> AudioFrequencies.MP3_FREQUENCIES;
            case WAV -> AudioFrequencies.WAV_FREQUENCIES;
            case AAC -> AudioFrequencies.AAC_FREQUENCIES;
            case FLAC -> AudioFrequencies.FLAC_FREQUENCIES;
            case OGG -> AudioFrequencies.OGG_FREQUENCIES;
            case OPUS -> AudioFrequencies.OPUS_FREQUENCIES;
            case M4A -> AudioFrequencies.M4A_FREQUENCIES;
            case WMA -> AudioFrequencies.WMA_FREQUENCIES;
            case AIFF -> AudioFrequencies.AIFF_FREQUENCIES;
            case AMR -> AudioFrequencies.AMR_FREQUENCIES;
        };
    }
    public enum AudioFrequency {
        FREQUENCY_8000(8000),
        FREQUENCY_11025(11025),
        FREQUENCY_12000(12000),
        FREQUENCY_16000(16000),
        FREQUENCY_22050(22050),
        FREQUENCY_24000(24000),
        FREQUENCY_32000(32000),
        FREQUENCY_44100(44100),
        FREQUENCY_48000(48000),
        FREQUENCY_64000(64000),
        FREQUENCY_88200(88200),
        FREQUENCY_96000(96000),
        FREQUENCY_192000(192000);

        public final int frequency;

        AudioFrequency(int frequency) {
            this.frequency = frequency;
        }

        @NonNull
        @Override
        public String toString() {
            return frequency + " Hz";
        }
    }
}

