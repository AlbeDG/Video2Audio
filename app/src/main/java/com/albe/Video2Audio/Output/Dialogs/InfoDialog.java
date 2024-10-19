package com.albe.Video2Audio.Output.Dialogs;

import android.app.Dialog;
import android.content.Context;
import android.view.ViewGroup;
import android.view.Window;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.albe.Video2Audio.Info.InfoRecyclerAdapter;
import com.albe.Video2Audio.MainActivity;
import com.albe.Video2Audio.Output.OutputFile;
import com.albe.Video2Audio.R;

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
