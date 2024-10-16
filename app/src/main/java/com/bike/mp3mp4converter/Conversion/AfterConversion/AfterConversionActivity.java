package com.bike.mp3mp4converter.Conversion.AfterConversion;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bike.mp3mp4converter.BackButtonActivity;
import com.bike.mp3mp4converter.MainActivity;
import com.bike.mp3mp4converter.Output.Dialogs.RenameDialog;
import com.bike.mp3mp4converter.Output.OutputFile;
import com.bike.mp3mp4converter.R;
import com.google.android.gms.ads.AdView;

import static com.bike.fragment.FragmentOutputFolder.gson;
import static com.bike.mp3mp4converter.Conversion.ConvertActivity.adsManager;
import static com.bike.mp3mp4converter.Output.OutputFileAdapter.outputFilesManager;

public class AfterConversionActivity extends BackButtonActivity {

    TextView titleTextView;

    LinearLayout openLayout, shareLayout, editTitleLayout;

    Button outputFolderButton;

    LinearLayout buttonLayout;

    AdView currentAdView;

    public static final String STRING_EXTRA_NAME = "output_file_json";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_after_conversion);
        addBackButton(buttonLayout = findViewById(R.id.afterConversionButtonLayout));
        String json = getIntent().getStringExtra(STRING_EXTRA_NAME);
        OutputFile outputFile = gson.fromJson(json, OutputFile.class);
        //Title
        titleTextView = findViewById(R.id.afterConversionTitle);
        titleTextView.setText(outputFile.title);
        //Image views
        String applicationId = MainActivity.INSTANCE.getPackageName();
        openLayout = findViewById(R.id.afterConversionOpenFile);
        openLayout.setOnClickListener(view -> outputFile.open(applicationId));
        shareLayout = findViewById(R.id.afterConversionShare);
        shareLayout.setOnClickListener(view -> outputFile.share(applicationId));
        editTitleLayout = findViewById(R.id.afterConversionEditTitle);
        editTitleLayout.setOnClickListener(view -> {
            RenameDialog renameDialog = new RenameDialog(view.getContext());
            renameDialog.init(titleTextView, outputFile, outputFilesManager);
            renameDialog.show();
        });
        //Button
        outputFolderButton = findViewById(R.id.afterConversionGoToOutputFolder);
        outputFolderButton.setOnClickListener(view -> {
            Intent intent = new Intent(view.getContext(), MainActivity.class);
            startActivity(intent);
        });
        //Ad View
        currentAdView = findViewById(R.id.afterConversionBannerAd);
        adsManager.loadBanner(this, currentAdView);
    }
}
