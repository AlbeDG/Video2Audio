package com.bike.mp3mp4converter.Output.Dialogs;

import android.app.Dialog;
import android.content.Context;
import android.view.ViewGroup;
import android.view.Window;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bike.mp3mp4converter.Info.InfoRecyclerAdapter;
import com.bike.mp3mp4converter.MainActivity;
import com.bike.mp3mp4converter.Output.OutputFile;
import com.bike.mp3mp4converter.R;

public class InfoDialog extends Dialog {

    RecyclerView recyclerView;
    public InfoDialog(Context context) {
        super(context);
    }

    public void init(OutputFile current) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.info_file_layout);
        //Init recycler
        recyclerView = findViewById(R.id.infoRecycler);
        InfoRecyclerAdapter infoRecyclerAdapter = new InfoRecyclerAdapter();
        infoRecyclerAdapter.addValues(current);
        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.INSTANCE));
        recyclerView.setAdapter(infoRecyclerAdapter);
    }
    @Override
    public void show() {
        super.show();
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

}
