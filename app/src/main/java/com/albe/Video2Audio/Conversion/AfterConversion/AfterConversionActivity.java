package com.albe.Video2Audio.Conversion.AfterConversion;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.albe.Video2Audio.BackButtonActivity;
import com.albe.Video2Audio.MainActivity;
import com.albe.Video2Audio.Output.Dialogs.RenameDialog;
import com.albe.Video2Audio.Output.OutputFile;
import com.albe.Video2Audio.R;
import com.google.android.gms.ads.AdView;

import static com.albe.fragment.FragmentOutputFolder.gson;
import static com.albe.Video2Audio.Conversion.ConvertActivity.adsManager;
import static com.albe.Video2Audio.Output.OutputFileArrayAdapter.outputFilesManager;

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
