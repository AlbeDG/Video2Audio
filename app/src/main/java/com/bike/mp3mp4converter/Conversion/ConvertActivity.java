package com.bike.mp3mp4converter.Conversion;

import static com.bike.mp3mp4converter.FileFormats.EncodingFormat.COMPRESSED;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bike.mp3mp4converter.FileFormats.AudioFileFormat;
import com.bike.mp3mp4converter.FileFormats.EncodingFormat;
import com.bike.mp3mp4converter.R;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.ui.PlayerView;


public class ConvertActivity extends AppCompatActivity {

    public static final AudioFileFormat[] AVAILABLE_FORMATS = {AudioFileFormat.MP3, AudioFileFormat.AAC, AudioFileFormat.AMR, AudioFileFormat.AIFF, AudioFileFormat.FLAC, AudioFileFormat.M4A, AudioFileFormat.OGG, AudioFileFormat.OPUS, AudioFileFormat.WAV, AudioFileFormat.WMA};

    public static Converter converter;

    TextView titleView, progressTextView;
    Spinner availableAudioFormats, availableEncodingFormats;

    Button convertButton;

    ProgressBar progressBar;

    ArrayAdapter<CharSequence> adapter;

    String videoTitle;

    PlayerView videoView;

    ExoPlayer exoPlayer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Uri uri = Uri.parse(getIntent().getStringExtra("videoURI"));
        converter = new Converter(this, AudioFileFormat.MP3);
        setContentView(R.layout.activity_convert);
        progressTextView = findViewById(R.id.progressText);
        progressTextView.setVisibility(View.GONE);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);
        availableAudioFormats = findViewById(R.id.availableAudioFormats);
        availableEncodingFormats = findViewById(R.id.availableEncodingFormats);
        adapter = ArrayAdapter.createFromResource(this, R.array.audio_formats, R.layout.spinner_item);
        availableAudioFormats.setAdapter(adapter);
        adapter = new ArrayAdapter<>(getApplicationContext(), R.layout.spinner_item, EncodingFormat.getFormatStringSequences(converter.audioFileFormat));
        availableEncodingFormats.setAdapter(adapter);
        availableEncodingFormats.setSelection(10);
        titleView = findViewById(R.id.videoTitle);
        videoView = findViewById(R.id.videoView);
        videoTitle = converter.getFileName(uri);
        titleView.setText(videoTitle);
        convertButton = findViewById(R.id.convertButton);
        convertButton.setOnClickListener(view -> {
            convertButton.setEnabled(false);
            String formatString = availableAudioFormats.getSelectedItem().toString();
            converter.setAudioFileFormat(AudioFileFormat.fromString(formatString));
            converter.convert(uri);
            progressBar.setVisibility(View.VISIBLE);
            progressTextView.setVisibility(View.VISIBLE);
        });
        availableAudioFormats.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                AudioFileFormat selectedFormat = AVAILABLE_FORMATS[position];
                converter.encodingFormat.init(selectedFormat);
                converter.setAudioFileFormat(selectedFormat);
                adapter = new ArrayAdapter<>(getApplicationContext(), R.layout.spinner_item, EncodingFormat.getFormatStringSequences(selectedFormat));
                availableEncodingFormats.setAdapter(adapter);
                availableEncodingFormats.setEnabled(adapter.getCount() > 1);
                Log.d("APP", "Set adapter for " + selectedFormat.toString());
                Log.d("APP", "Spinner elements " + availableEncodingFormats.getAdapter().getCount() + " " + EncodingFormat.getFormatStringSequences(selectedFormat).length);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        availableEncodingFormats.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedString = availableEncodingFormats.getItemAtPosition(position).toString();
                if (selectedString.equals(COMPRESSED)) return;
                String[] parts = selectedString.split(" ");
                if (parts.length > 0) {
                    if (parts[0].equals("CBR")) converter.encodingFormat.type = EncodingFormat.Type.CBR;
                    else converter.encodingFormat.type = EncodingFormat.Type.VBR;
                    converter.encodingFormat.bitrate = Float.parseFloat(parts[1].replace("kb/s", ""));
                } else {
                    Log.e("APP", "The spinner string is invalid");
                }
                Log.d("APP", "Selected format: " + converter.encodingFormat + " bitrate " + converter.encodingFormat.bitrate);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        loadVideo(uri);
    }

    public void loadVideo(Uri uri) {
        exoPlayer = new ExoPlayer.Builder(this).build();
        videoView.setPlayer(exoPlayer);
        MediaItem mediaItem = MediaItem.fromUri(uri);
        exoPlayer.setMediaItem(mediaItem);
        exoPlayer.prepare();
    }

    public void onConversionEnded() {
        convertButton.setEnabled(true);
        progressBar.setVisibility(View.GONE);
        progressTextView.setVisibility(View.GONE);
    }

    public void updateProgressBar(int progress) {
        progressBar.setProgress(progress);
        progressTextView.setText(progress + "%");
    }

    @Override
    public void onBackPressed() {
        exoPlayer.stop();
        super.onBackPressed();
    }
}
