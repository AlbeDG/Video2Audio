package com.bike.mp3mp4converter.Conversion.Dialogs;

import static com.bike.mp3mp4converter.Conversion.ConvertActivity.converter;

import android.app.Dialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Toast;

import com.bike.mp3mp4converter.Conversion.Converter;
import com.bike.mp3mp4converter.R;
import com.google.android.material.slider.LabelFormatter;
import com.google.android.material.slider.RangeSlider;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Objects;

public class TrimDialog extends Dialog {

    RangeSlider rangeSlider;

    final String timeRegex = "^([0-1][0-9]|2[0-3]):([0-5][0-9])$";

    public final String allowedChars = "0123456789:";

    InputFilter allowedCharsFilter;

    TextInputEditText fromText, toText;
    public TrimDialog(Context context) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.layout_trim);
        //Allowed chars
        allowedCharsFilter = (charSequence, start, end, spanned, dstart, dend) -> {
            for (int i = start; i < end; i++) {
                if (!allowedChars.contains(String.valueOf(charSequence.charAt(i)))) return "";
            }
            return null;
        };
        //Range Slider
        rangeSlider = findViewById(R.id.rangeSlider);
        int sliderColor = context.getColor(R.color.light_blue);
        rangeSlider.setValueFrom(0);
        rangeSlider.setValueTo(converter.videoDuration);
        updateRangeSlider();
        rangeSlider.setLabelBehavior(LabelFormatter.LABEL_GONE);
        rangeSlider.setTrackInactiveTintList(ColorStateList.valueOf(Color.GRAY));
        rangeSlider.setTrackActiveTintList(ColorStateList.valueOf(sliderColor));
        rangeSlider.setThumbTintList(ColorStateList.valueOf(Color.WHITE));
        rangeSlider.setHaloTintList(ColorStateList.valueOf(sliderColor));
        //Handle text views
        InputFilter[] filters = new InputFilter[] {allowedCharsFilter};
        fromText= findViewById(R.id.trimFromText);
        fromText.setFilters(filters);
        toText = findViewById(R.id.trimToText);
        toText.setFilters(filters);
        fromText.setText(Converter.formatDuration(converter.from));
        toText.setText(Converter.formatDuration(converter.to));
        rangeSlider.addOnChangeListener((slider, value, fromUser) -> {
            long fromValue = rangeSlider.getValues().get(0).longValue(), toValue = rangeSlider.getValues().get(1).longValue();
            fromText.setText(Converter.formatDuration(fromValue));
            toText.setText(Converter.formatDuration(toValue));
            if (fromValue < toValue) {
                converter.from = fromValue;
                converter.to = toValue;
            } else Log.e("APP", "Invalid trim values! (From >= To)");
            if (!converter.trim) converter.trim = true;
        });
        fromText.addTextChangedListener(new TextWatcher() {
            String previousText = "";
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                previousText = charSequence.toString();
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                String newTime = charSequence.toString();
                if (!newTime.contains(":")) {
                    fromText.setText(previousText);
                    fromText.setSelection(start + count);
                }
                updateTrim();
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        toText.addTextChangedListener(new TextWatcher() {
            String previousText = "";
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                previousText = charSequence.toString();
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                String newTime = charSequence.toString();
                if (!newTime.contains(":")) {
                    toText.setText(previousText);
                    toText.setSelection(start + count);
                }
                updateTrim();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    @Override
    public void cancel() {
        updateTrim();
        super.cancel();
    }

    @Override
    public void show() {
        super.show();
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        getWindow().setGravity(Gravity.BOTTOM);
    }

    boolean validFormat(String time) {
        return time.matches(timeRegex);
    }

    void updateTrim() {
        String fromInput = Objects.requireNonNull(fromText.getText()).toString(), toInput = Objects.requireNonNull(toText.getText().toString());
        if (validFormat(fromInput)) {
            long from = Converter.duration(fromInput);
            boolean validValue = from < converter.to;
            if (validValue) converter.from = Converter.duration(fromInput);
            else Toast.makeText(getContext(), "From value but be smaller than to value!", Toast.LENGTH_LONG).show();
        }
        else Log.d("APP", "Invalid trim from format " + fromInput);
        if (validFormat(toInput)) {
            long to = Converter.duration(toInput);
            boolean validValue = to > converter.from;
            boolean tooLong = to > converter.videoDuration;
            if (validValue && !tooLong) converter.to = Converter.duration(toInput);
            else if (!validValue) Toast.makeText(getContext(), "To value but be greater than from value", Toast.LENGTH_LONG).show();
            else if (tooLong) Toast.makeText(getContext(), "To value must be smaller than the video duration", Toast.LENGTH_LONG).show();
        }
        else Log.d("APP", "Invalid trim to format " + toInput);
        updateRangeSlider();
    }

    void updateRangeSlider() {
        rangeSlider.setValues((float) converter.from, (float) converter.to);
    }
}
