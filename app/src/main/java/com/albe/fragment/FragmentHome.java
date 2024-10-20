package com.albe.fragment;

import static com.albe.Video2Audio.Conversion.Converter.fileExtension;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.albe.Video2Audio.Conversion.ConvertActivity;
import com.albe.Video2Audio.FileFormats.VideoFileFormat;
import com.albe.Video2Audio.InfoActivities.InfoActivity;
import com.albe.Video2Audio.MainActivity;
import com.albe.Video2Audio.R;
import com.albe.Video2Audio.RealPathUtil;

import ir.smartdevelopers.smartfilebrowser.customClasses.SFBFileFilter;
import ir.smartdevelopers.smartfilebrowser.customClasses.SmartFilePicker;


public class FragmentHome extends Fragment implements View.OnClickListener {

    View view;

    LinearLayout selectFileLayout;

    ImageView infoView;

    public FragmentHome() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        this.view = inflater.inflate(R.layout.fragment_home, container, false);
        selectFileLayout = view.findViewById(R.id.selectFileLayout);
        selectFileLayout.setOnClickListener(view -> {
            openFilePicker();
        });

        infoView = view.findViewById(R.id.infoImageView);
        infoView.setOnClickListener(view -> {
            Intent intent = new Intent(getContext(), InfoActivity.class);
            startActivity(intent);
        });

        return view;
    }


    private void openFilePicker() {
        if (MainActivity.INSTANCE.checkFilePermissions()) {
            SFBFileFilter fileFilter = new SFBFileFilter.Builder().isFile(true).isFolder(false).setExtensionList(VideoFileFormat.extArray()).build();
            Intent intent = new SmartFilePicker.IntentBuilder()
                    .showCamera(false)
                    .canSelectMultipleInGallery(false)
                    .showPDFTab(false)
                    .showAudioTab(false)
                    .showFilesTab(true)
                    .showGalleryTab(true)
                    .showPickFromSystemGalleyMenu(true)
                    .setFileFilter(fileFilter)
                    .canSelectMultipleInFiles(false)
                    .build(getContext());
            startActivityForResult(intent, 10);
        } else {
            Toast.makeText(getContext(), "Video and audio permissions are required to start converting videos!", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package", MainActivity.INSTANCE.getPackageName(), null);
            intent.setData(uri);
            startActivity(intent);
        }

    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==10){
            if (data!=null) {
                Uri[] uris= SmartFilePicker.getResultUris(data);
                if (uris!=null){
                    Uri uri = uris[0];
                    String filePath = RealPathUtil.getRealPath(getContext(), uri);
                    boolean isFileValid = false;
                    String fileExtension = fileExtension(filePath);
                    for (VideoFileFormat format : VideoFileFormat.values()) {
                        if (format.getFormatStr().equals(fileExtension)) {
                            isFileValid = true;
                            break;
                        }
                    }
                    if (isFileValid) {
                        Intent intent = new Intent(getContext(), ConvertActivity.class);
                        intent.putExtra("videoURI", uri.toString());
                        intent.putExtra("videoPath", filePath);
                        startActivity(intent);
                    } else Toast.makeText(getContext(), "Please select a valid file", Toast.LENGTH_LONG).show();
                }
            }
        }
    }


    @Override
    public void onClick(View view) {

    }
}