package com.bike.mp3mp4converter.FileFormats;

import android.util.Log;

import java.util.HashMap;
import java.util.Map;

public class EncodingFormat {


    public Type type;

    public static float[] CBRBitrates;

    public static final float[] MP3CBRBitrates = new float[] {8, 16, 24, 32, 40, 48, 64, 80, 96, 112, 128, 160, 192, 224, 256, 320};

    public static final float[] WMACBRBitrates = new float[] {64, 80, 96, 112, 128, 160, 192, 224, 256, 320};

    public static final float[] AMRBitrates = new float[] {4.75f, 5.15f, 5.9f, 6.7f, 7.4f, 7.95f, 10.2f, 12.2f};
    public static final HashMap<Integer, Integer> MP3VBRQualityMap = new HashMap<Integer, Integer>() {
        {
            put(1, 245);
            put(2, 190);
            put(3, 175);
            put(4, 165);
            put(5, 130);
            put(6, 115);
            put(7, 100);
            put(8, 85);
            put(9, 65);
        }};

    public static final HashMap<Integer, Integer> AACVBRQualityMap = new HashMap<Integer, Integer>() {
        {
            put(1, 32);
            put(2, 40);
            put(3, 56);
            put(4, 72);
            put(5, 112);
        }
    };

    public static final HashMap<Integer, Integer> OGGVBRQualityMap = new HashMap<Integer, Integer>() {
        {
            put(0, 64);
            put(1, 80);
            put(2, 96);
            put(3, 112);
            put(4, 128);
            put(5, 160);
            put(6, 192);
            put(7, 224);
            put(8, 256);
            put(9, 320);
            put(10, 500);
        }
    };

    public static final HashMap<Integer, Integer> OPUSVBRQualityMap = new HashMap<>();
    public static HashMap<Integer, Integer> VBRQualityMap = new HashMap<>();

    public float bitrate;

    public static final String COMPRESSED = "Compressed";


    public EncodingFormat() {
        int i = 0;
        for (i = 0; i < MP3CBRBitrates.length; i++) {
            float mp3Bitrate = MP3CBRBitrates[i];
            OPUSVBRQualityMap.put(i, (int) mp3Bitrate);
        }
    }

    public void setBitrate(int index) {
        if (isCbr()) {
            if (CBRBitrates.length > index) {
                bitrate = CBRBitrates[index];
            } else Log.e("APP", "Could not find the CBR bitrate from the index " + index);
        } else {
            if (VBRQualityMap.size() > index) {
                bitrate = VBRQualityMap.get(index);
            } else Log.e("APP", "Could not find the VBR bitrate from the index " + index);
        }
    }

    public static String bitrateToString(float bitrate) {
        double decimalPart = bitrate - (long) bitrate;
        if (decimalPart == 0) return String.valueOf((int)bitrate);
        else return String.valueOf(bitrate);
    }

    public void init(AudioFileFormat format) {
        VBRQualityMap = null;
        if (format.VBR) {
            switch (format) {
                case MP3:
                    VBRQualityMap = MP3VBRQualityMap;
                    break;
                case M4A:
                case AAC:
                    VBRQualityMap = AACVBRQualityMap;
                    break;
                case OGG:
                    VBRQualityMap = OGGVBRQualityMap;
                    break;
                case OPUS:
                    VBRQualityMap = OPUSVBRQualityMap;
                    break;
            }
        }
        if (format.CBR) {
            if (format == AudioFileFormat.AMR) {
                CBRBitrates = AMRBitrates;
            } else if (format == AudioFileFormat.WMA) {
                CBRBitrates = WMACBRBitrates;
            }
            else {
                CBRBitrates = MP3CBRBitrates;
            }
        }
    }

    public void setDefaultCbrBitrate() {
       setBitrate(10);
    }
    public boolean isCbr() {
        return this.type == Type.CBR;
    }


    public static CharSequence[] getFormatStringSequences(AudioFileFormat format) {
        boolean noneSupported = !format.CBR && !format.VBR;
        int size = noneSupported ? 1 : VBRQualityMap == null ? CBRBitrates.length : CBRBitrates.length + VBRQualityMap.size();
        CharSequence[] charSequences = new CharSequence[size];
        int index = 0;
        if (noneSupported) {
            charSequences[0] = COMPRESSED;
        } else {
            String bitrate;
            if (format.CBR) {
                for (float cbrBitrate : CBRBitrates) {
                    bitrate = bitrateToString(cbrBitrate);
                    charSequences[index] = "CBR " + bitrate + "kb/s";
                    index++;
                }
            }
            if (format.VBR) {
                for (Map.Entry<Integer, Integer> entry : VBRQualityMap.entrySet()) {
                    bitrate = bitrateToString(entry.getValue());
                    charSequences[index] = "VBR " + bitrate + "kb/s";
                    index++;
                }
            }
        }
        return charSequences;
    }

    public Integer getVBRQuality(float bitrate) {
        for (Map.Entry<Integer, Integer> entry : VBRQualityMap.entrySet()) {
            if (entry.getValue() == bitrate) return entry.getValue();
        }
        return null;
    }

    public String encodingCommand(AudioFileFormat audioFileFormat) {
        String bitrateString = bitrateToString(this.bitrate);
        if (audioFileFormat == AudioFileFormat.WAV || audioFileFormat == AudioFileFormat.FLAC) return "";
        if (isCbr()) {
            return switch (audioFileFormat) {
                case MP3 -> String.format("-codec:a libmp3lame -b:a %sk", bitrateString);
                case AAC, M4A -> String.format("-codec:a aac -b:a %sk", bitrateString);
                case OGG -> String.format("-codec:a libvorbis -b:a %sk", bitrateString);
                case OPUS -> String.format("-codec:a libopus -b:a %sk", bitrateString);
                case WMA -> String.format("-codec:a wmav2 -b:a %sk", bitrateString);
                case AIFF -> "-codec:a pcm_s16be";
                case AMR ->
                        String.format("-b:a %sk -codec:a libopencore_amrnb", bitrateString);
                default -> null;
            };
        } else {
            Integer vbrQuality = getVBRQuality(this.bitrate);
            if (vbrQuality != null) {
                switch (audioFileFormat) {
                    case MP3:
                        return String.format("-codec:a libmp3lame -qscale:a %d", vbrQuality);
                    case AAC:
                    case M4A:
                        return String.format("-codec:a aac -q:a %d", vbrQuality);
                    case OGG:
                        return String.format("-codec:a libvorbis -q:a %d", vbrQuality);
                    case OPUS:
                        return String.format("-codec:a libopus -vbr on -b:a %sk", bitrateString);
                    default:
                        Log.e("APP", "Unsupported audio file format for VBR: " + audioFileFormat);
                        return null;
                }
            } else {
                Log.e("APP", "Could not find VBR Quality from bitrate " + bitrateString);
                return null;
            }
        }
    }

    public enum Type {
        VBR,
        CBR;
    }
}
