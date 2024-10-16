package com.bike.mp3mp4converter.Conversion.Dialogs;

import static com.bike.mp3mp4converter.Conversion.ConvertActivity.converter;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.bike.mp3mp4converter.Conversion.AudioChannel;
import com.bike.mp3mp4converter.Conversion.AudioFrequencies;
import com.bike.mp3mp4converter.FileFormats.AudioFileFormat;
import com.bike.mp3mp4converter.R;

public class QualityDialog extends Dialog {

    Spinner channelSpinner, frequencySpinner;

    CharSequence[] channelSequences = new CharSequence[] {"Stereo", "Mono"};

    public AudioFrequencies.AudioFrequency[] frequencies;

    public boolean amr;

    public QualityDialog(Context context) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.audio_quality_layout);
        frequencySpinner = findViewById(R.id.frequencySpinner);
        init();

    }

    public void init() {
        AudioFileFormat audioFormat = converter.audioFileFormat;
        int freqIndex = getDefaultFrequencyIndex(audioFormat);
        frequencies = AudioFrequencies.fromFormat(audioFormat);
        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<>(getContext(), R.layout.custom_spinner_item, getAudioFrequenciesCharsequence());
        frequencySpinner.setAdapter(adapter);
        frequencySpinner.setSelection(freqIndex);
        converter.audioFrequency = frequencies[freqIndex];
        frequencySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                converter.audioFrequency = frequencies[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        //Channel spinner
        channelSpinner = findViewById(R.id.channelSpinner);
        adapter = new ArrayAdapter<>(getContext(), R.layout.custom_spinner_item, channelSequences);
        channelSpinner.setAdapter(adapter);
        channelSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                converter.audioChannel = position == 0 ? AudioChannel.STEREO : AudioChannel.MONO;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    public void setDefaultAudioFrequency(AudioFileFormat audioFormat) {
        frequencies = AudioFrequencies.fromFormat(audioFormat);
        converter.audioFrequency = frequencies[getDefaultFrequencyIndex(audioFormat)];
    }

    public int getDefaultFrequencyIndex(AudioFileFormat audioFormat) {
        return AudioFrequencies.defaultAudioFrequencyIndex(audioFormat);
    }

    @Override
    public void show() {
        super.show();
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        getWindow().setGravity(Gravity.BOTTOM);
    }

    public CharSequence[] getAudioFrequenciesCharsequence() {
        CharSequence[] sequences = new CharSequence[frequencies.length];
        for (int i = 0; i < frequencies.length; i++) {
            sequences[i] = frequencies[i].toString();
        }
        return sequences;
    }
}
