package com.bike.mp3mp4converter.Conversion;

import static android.os.Environment.DIRECTORY_MUSIC;
import static com.arthenica.mobileffmpeg.Config.RETURN_CODE_SUCCESS;
import static com.bike.mp3mp4converter.MainActivity.changeFileExtension;

import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.arthenica.mobileffmpeg.Config;
import com.arthenica.mobileffmpeg.FFmpeg;
import com.arthenica.mobileffmpeg.FFprobe;
import com.bike.fragment.FragmentOutputFolder;
import com.bike.mp3mp4converter.FileFormats.AudioFileFormat;
import com.bike.mp3mp4converter.FileFormats.EncodingFormat;
import com.bike.mp3mp4converter.FileFormats.VideoFileFormat;
import com.bike.mp3mp4converter.RealPathUtil;


import java.io.File;

public class Converter {

    ConvertActivity activity;

    public VideoFileFormat videoFileFormat;
    public AudioFileFormat audioFileFormat;

    public EncodingFormat encodingFormat = new EncodingFormat();


    public long videoDuration = 0;

    public Converter() {

    }
    public Converter(ConvertActivity activity, AudioFileFormat audioFileFormat) {
        this.activity = activity;
        this.audioFileFormat = audioFileFormat;
        encodingFormat.type = EncodingFormat.Type.CBR;
        encodingFormat.init(audioFileFormat);
        encodingFormat.setDefaultCbrBitrate();
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

    String fileExtension(String path) {
        return path.substring(path.lastIndexOf('.'));
    }

    boolean checkFileFormat(String filePath) {
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
    public void convert(Uri uri) {
        String selectedFilePath = RealPathUtil.getRealPath(activity, uri);
        if (checkFileFormat(selectedFilePath)) {
            setVideoDuration(selectedFilePath);
            init();
            String newFilePath = getMusicDirectoryPath() + File.separator + changeFileExtension(selectedFilePath, audioFileFormat.getFormatStr());
            File newFile = new File(newFilePath);
            Log.d("APP", "Output path: " + changeFileExtension(selectedFilePath, audioFileFormat.getFormatStr()));
            String command = String.format("-i \"%s\" %s \"%s\"", selectedFilePath, encodingFormat.encodingCommand(audioFileFormat), newFilePath);
            Log.d("APP", "Command: " + command);
            activity.updateProgressBar(0);
            FFmpeg.executeAsync(command, ((executionId, returnCode) -> {
                activity.onConversionEnded();
                if (returnCode == RETURN_CODE_SUCCESS) {
                    openFolder(newFile.getAbsolutePath());
                    FragmentOutputFolder.saveNewFile(newFile, formatDuration(videoDuration), encodingFormat.bitrate + "kb/s", encodingFormat.type.toString());
                } else Log.e("APP", "Failed conversion error code: " + returnCode);
                Config.resetStatistics();
            }));
        }
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

    public void setVideoDuration(String filePath) {
        String command = String.format("-i \"%s\" -show_entries format=duration -v quiet -of csv=\"p=0\"\n", filePath);
        int result = FFprobe.execute(command);
        if (result == RETURN_CODE_SUCCESS) {
            String output = Config.getLastCommandOutput();
            Log.d("OUTPUT", "Getting video duration " + output);
            double number = Double.parseDouble(output);
            videoDuration = Math.round(number) * 1000;
        } else {
            Log.d("FFProbe", "Failed ffprobe with return code: " + result);
        }
    }
    public static String formatDuration(long milliseconds) {
        long totalSeconds = milliseconds / 1000;
        long hours = totalSeconds / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = totalSeconds % 60;

        if (hours > 0) {
            return String.format("%02d:%02d:%02d", hours, minutes, seconds);
        } else {
            return String.format("%02d:%02d", minutes, seconds);
        }
    }

}
