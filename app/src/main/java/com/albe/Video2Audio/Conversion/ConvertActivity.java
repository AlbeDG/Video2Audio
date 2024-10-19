package com.albe.Video2Audio.Conversion;

import static com.albe.Video2Audio.FileFormats.EncodingFormat.COMPRESSED;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.albe.Video2Audio.ADS.AdsManager;
import com.albe.Video2Audio.BackButtonActivity;
import com.albe.Video2Audio.Conversion.Dialogs.QualityDialog;
import com.albe.Video2Audio.Conversion.Dialogs.TagsDialog;
import com.albe.Video2Audio.Conversion.Dialogs.TrimDialog;
import com.albe.Video2Audio.Conversion.Dialogs.VolumeDialog;
import com.albe.Video2Audio.FileFormats.AudioFileFormat;
import com.albe.Video2Audio.FileFormats.EncodingFormat;
import com.albe.Video2Audio.MainActivity;
import com.albe.Video2Audio.R;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.ui.PlayerView;

import java.util.Random;

public class ConvertActivity extends BackButtonActivity {

    public static final AudioFileFormat[] AVAILABLE_FORMATS = {AudioFileFormat.MP3, AudioFileFormat.AAC, AudioFileFormat.AMR, AudioFileFormat.AIFF, AudioFileFormat.FLAC, AudioFileFormat.M4A, AudioFileFormat.OPUS, AudioFileFormat.WAV, AudioFileFormat.WMA};

    public static Converter converter;

    TextView titleView, progressTextView;
    Spinner availableAudioFormats, availableEncodingFormats;

    Button convertButton;

    ProgressBar progressBar;

    ArrayAdapter<CharSequence> adapter;

    String videoTitle;

    PlayerView videoView;

    ExoPlayer exoPlayer;

    LinearLayout titleLayout, trimLayout, qualityLayout, volumeLayout, modifyTagsLayout;

    public final int DEFAULT_BITRATE = 128;

    public static final String INTERSTITIAL_ADS_ID = "ca-app-pub-6787571117557133/4432293582",
    BANNER_ADS_ID = "ca-app-pub-6787571117557133/5739168643";

    public static AdsManager adsManager = new AdsManager(INTERSTITIAL_ADS_ID, BANNER_ADS_ID);

    Random random = new Random();

    public boolean converting = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String path = getIntent().getStringExtra("videoPath");
        Uri uri = Uri.parse(getIntent().getStringExtra("videoURI"));
        try {
            converter = new Converter(this, AudioFileFormat.MP3, path);
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            startActivity(new Intent(this, MainActivity.class));
        }
        setContentView(R.layout.activity_convert);
        addBackButton(titleLayout = findViewById(R.id.titleLayout));
        QualityDialog qualityDialog = new QualityDialog(this);
        VolumeDialog volumeDialog = new VolumeDialog(this);
        TagsDialog tagsDialog = new TagsDialog(this);
        progressTextView = findViewById(R.id.progressText);
        progressTextView.setVisibility(View.GONE);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);
        availableAudioFormats = findViewById(R.id.availableAudioFormats);
        availableEncodingFormats = findViewById(R.id.availableEncodingFormats);
        adapter = ArrayAdapter.createFromResource(this, R.array.audio_formats, R.layout.custom_spinner_item);
        availableAudioFormats.setAdapter(adapter);
        adapter = new ArrayAdapter<>(getApplicationContext(), R.layout.custom_spinner_item, EncodingFormat.getFormatStringSequences(converter.audioFileFormat));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        availableEncodingFormats.setAdapter(adapter);
        titleView = findViewById(R.id.videoTitle);
        videoView = findViewById(R.id.videoView);
        videoTitle = converter.getFileName(uri);
        titleView.setText(videoTitle);
        convertButton = findViewById(R.id.convertButton);
        convertButton.setOnClickListener(view -> {
            convertButton.setEnabled(false);
            String formatString = availableAudioFormats.getSelectedItem().toString();
            converter.setAudioFileFormat(AudioFileFormat.fromString(formatString));
            if (!converter.trim) converter.beginConversion();
            else converter.beginConversion(Converter.formatDuration(converter.from), Converter.formatDuration(converter.to), converter.trim);
            progressBar.setVisibility(View.VISIBLE);
            progressTextView.setVisibility(View.VISIBLE);
        });
        availableAudioFormats.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                AudioFileFormat selectedFormat = AVAILABLE_FORMATS[position];
                converter.encodingFormat.init(selectedFormat);
                converter.setAudioFileFormat(selectedFormat);
                adapter = new ArrayAdapter<>(getApplicationContext(), R.layout.custom_spinner_item, EncodingFormat.getFormatStringSequences(selectedFormat));
                availableEncodingFormats.setAdapter(adapter);
                availableEncodingFormats.setEnabled(adapter.getCount() > 1);
                if (adapter.getCount() > 10) {
                    availableEncodingFormats.setSelection(10); //CBR 128KB/S
                    converter.encodingFormat.bitrate = DEFAULT_BITRATE;
                }
                qualityDialog.amr = selectedFormat.equals(AudioFileFormat.AMR);
                qualityDialog.setDefaultAudioFrequency(selectedFormat);
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
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        trimLayout = findViewById(R.id.trimLayout);
        trimLayout.setOnClickListener(view -> {
            if (converting) return;
            TrimDialog trimDialog = new TrimDialog(this);
            trimDialog.show();
        });
        qualityLayout = findViewById(R.id.qualityLayout);
        qualityLayout.setOnClickListener(view -> {
            if (converting) return;
            qualityDialog.init();
            if (!qualityDialog.amr) qualityDialog.show();
            else Toast.makeText(this, "AMR-NB format does not support quality changes!", Toast.LENGTH_LONG).show();
        });
        volumeLayout = findViewById(R.id.volumeLayout);
        volumeLayout.setOnClickListener(view -> {
            if (converting) return;
            volumeDialog.show();
        });
        modifyTagsLayout = findViewById(R.id.modifyTagsLayout);
        modifyTagsLayout.setOnClickListener(view -> {
            if (converting) return;
            tagsDialog.show();
        });
        loadVideo(uri);
    }

    public void loadVideo(Uri uri) {
        exoPlayer = new ExoPlayer.Builder(this).build();
        videoView.setPlayer(exoPlayer);
        MediaItem mediaItem = MediaItem.fromUri(uri);
        exoPlayer.setMediaItem(mediaItem);
        exoPlayer.prepare();
        exoPlayer.addListener(new Player.Listener() {
            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                if (playbackState == ExoPlayer.STATE_READY) {
                    converter.videoDuration = exoPlayer.getDuration();
                    if (!converter.ready) {
                        converter.ready = true;
                        converter.from = 0;
                        converter.to = converter.videoDuration;
                    }
                }
            }
        });

    }

    public void onConversionEnded() {
        converting = false;
        convertButton.setEnabled(true);
        progressBar.setVisibility(View.GONE);
        progressTextView.setVisibility(View.GONE);
        //ADS
        if (shouldShowAd()) adsManager.loadInterstitial(this);
        //END ADS
    }

    public void updateProgressBar(int progress) {
        progressBar.setProgress(progress);
        progressTextView.setText(progress + "%");
    }


    @Override
    public void onBackPressed() {
        exoPlayer.stop();
        if (!converting) super.onBackPressed();
    }

    boolean shouldShowAd() {
        int randomNumber = random.nextInt(100);
        return randomNumber < 50;
    }
}
