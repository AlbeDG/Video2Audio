package com.albe.Video2Audio.Conversion;

import static android.os.Environment.DIRECTORY_MUSIC;
import static com.albe.Video2Audio.Conversion.AfterConversion.AfterConversionActivity.STRING_EXTRA_NAME;
import static com.albe.Video2Audio.MainActivity.changeFileExtension;
import static com.albe.fragment.FragmentOutputFolder.gson;
import static com.arthenica.mobileffmpeg.Config.RETURN_CODE_SUCCESS;

import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.albe.Video2Audio.Conversion.AfterConversion.AfterConversionActivity;
import com.albe.Video2Audio.FileFormats.AudioFileFormat;
import com.albe.Video2Audio.FileFormats.EncodingFormat;
import com.albe.Video2Audio.FileFormats.VideoFileFormat;
import com.albe.Video2Audio.MainActivity;
import com.albe.Video2Audio.Output.OutputFile;
import com.albe.Video2Audio.RealPathUtil;
import com.arthenica.mobileffmpeg.Config;
import com.arthenica.mobileffmpeg.FFmpeg;

import java.io.File;

public class Converter {

    ConvertActivity activity;

    public VideoFileFormat videoFileFormat;
    public AudioFileFormat audioFileFormat;

    public EncodingFormat encodingFormat = new EncodingFormat();

    public long videoDuration = 0, from = 0, to = 0;

    public String filePath;


    public AudioFrequencies.AudioFrequency audioFrequency;

    public boolean trim, ready = false;

    public static final AudioFrequencies.AudioFrequency DEFAULT_FREQUENCY = AudioFrequencies.AudioFrequency.FREQUENCY_44100;

    public float volumeMultiplier;

    public MetadataManager metadataManager = new MetadataManager();

    public AudioChannel audioChannel = AudioChannel.STEREO;
    public Converter() {}

    public Converter(ConvertActivity activity, AudioFileFormat audioFileFormat, String filePath) throws Exception {
        this.activity = activity;
        this.audioFileFormat = audioFileFormat;
        this.filePath = filePath;
        this.audioFrequency = DEFAULT_FREQUENCY;
        this.volumeMultiplier = 1.0f;
        if (checkFileFormat()) {
            encodingFormat.type = EncodingFormat.Type.CBR;
            encodingFormat.init(audioFileFormat);
            encodingFormat.setDefaultCbrBitrate();
        } else throw new Exception("File format is invalid!");
    }

    public void setAudioFileFormat(AudioFileFormat audioFileFormat) {
        this.audioFileFormat = audioFileFormat;
    }

    public void init() {
        Config.enableStatisticsCallback(statistics -> {
            int progress = (int) Math.min((statistics.getTime() * 100) / videoDuration, 100);
            activity.updateProgressBar(progress);
            Log.d("APP", "Progress: " + progress);
        });

    }

    public static String fileExtension(String filePath) {
        return filePath.substring(filePath.lastIndexOf('.'));
    }

    boolean checkFileFormat() {
        try {
            videoFileFormat = VideoFileFormat.fromString(fileExtension(filePath));
        } catch (IllegalArgumentException exception) {
            Toast.makeText(activity, "The selected file is not valid", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }
    String getFileName(Uri uri) {
        Log.d("APP", "Real path is: " + RealPathUtil.getRealPath(activity, uri));
        return new File(RealPathUtil.getRealPath(activity, uri)).getName();
    }

    public void beginConversion(String start, String end, boolean trim) {
        if (checkFileFormat()) {
            if (end == null) end = formatDuration(videoDuration);
            if (ready) convert(start, end, trim);
        }
    }

    public void beginConversion() {
        beginConversion("00:00", null, false);
    }

    void convert(String from, String to, boolean trim) {
        init();
        String newFilePath = getMusicDirectoryPath() + File.separator + changeFileExtension(filePath, audioFileFormat.getFormatStr());
        File newFile = new File(newFilePath);
        metadataManager.init();
        if (audioFileFormat.equals(AudioFileFormat.AMR)) audioChannel = AudioChannel.MONO; //AMR ONLY SUPPORTS MONO
        String command = String.format("-i \"%s\" -ac %s %s -af \"volume=%s\" -ar %s %s %s %s \"%s\"",
                filePath,
                audioChannel.getAcOptionValue(),
                metadataManager.command,
                volumeMultiplier,
                audioFrequency.frequency,
                "-ss " + from, "-to " + to,
                encodingFormat.encodingCommand(audioFileFormat),
                newFilePath);
        Log.d("APP", "Command: " + command);
        activity.updateProgressBar(0);
        activity.converting = true;
        FFmpeg.executeAsync(command, ((executionId, returnCode) -> {
            activity.onConversionEnded();
            if (returnCode == RETURN_CODE_SUCCESS) {
                openFolder(newFile.getAbsolutePath());
                OutputFile outputFile = new OutputFile(newFile,
                        formatDuration(getRealVideoDuration(trim)), encodingFormat.bitrate + "kb/s",
                        encodingFormat.type.toString());
                MainActivity.INSTANCE.getFragmentOutputFolder().saveNewFile(outputFile, metadataManager.hasMetadata() ? metadataManager : null);
                afterConversionInit(outputFile);
            } else Log.e("APP", "Failed conversion error code: " + returnCode);
            Config.resetStatistics();
        }));

    }

    public void afterConversionInit(OutputFile outputFile) {
        Intent intent = new Intent(activity, AfterConversionActivity.class);
        intent.putExtra(STRING_EXTRA_NAME, gson.toJson(outputFile));
        activity.startActivity(intent);
    }

    long getRealVideoDuration(boolean trim) {
        return trim ? to - from : videoDuration;
    }

    public void openFolder(String location)
    {
        Uri selectedUri = Uri.parse(location);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(selectedUri, "resource/folder");

        if (intent.resolveActivityInfo(activity.getPackageManager(), 0) != null)
        {
            activity.startActivity(intent);
        }
        else
        {
            Log.d("APP", "No app to open the folder");
        }
    }

    public String getMusicDirectoryPath() {
        return Environment.getExternalStoragePublicDirectory(DIRECTORY_MUSIC).getAbsolutePath();
    }


    public static String formatDuration(long milliseconds) {
        long totalSeconds = (milliseconds / 1000);
        long hours = totalSeconds / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = totalSeconds % 60;

        if (hours > 0) {
            return String.format("%02d:%02d:%02d", hours, minutes, seconds);
        } else {
            return String.format("%02d:%02d", minutes, seconds);
        }
    }

    public static long duration(String time) {
        String[] parts = time.split(":");

        if (parts.length != 2) {
            throw new IllegalArgumentException("Invalid time format. Expected format is MM:SS");
        }

        try {
            int minutes = Integer.parseInt(parts[0]);
            int seconds = Integer.parseInt(parts[1]);

            if (seconds < 0 || seconds > 59) {
                throw new IllegalArgumentException("Invalid value for seconds. It should be between 0 and 59.");
            }

            long totalMillis = (minutes * 60 * 1000L) + (seconds * 1000L);
            return totalMillis;

        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid numbers in time format.");
        }
    }

}
