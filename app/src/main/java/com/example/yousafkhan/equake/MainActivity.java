package com.example.yousafkhan.equake;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private ListView listView;

    private EarthQuakeVModel earthQuakeViewmodel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressBar = findViewById(R.id.progress_bar);
        listView = findViewById(R.id.listview);

        earthQuakeViewmodel = ViewModelProviders.of(this).get(EarthQuakeVModel.class);

        earthQuakeViewmodel.getDataList().observe(this, new Observer<ArrayList<EarthQuake>>() {
            @Override
            public void onChanged(@Nullable ArrayList<EarthQuake> earthQuakes) {
                progressBar.setVisibility(View.GONE);
                populateListView(earthQuakes);
            }
        });

        setListViewListener();
    }

    private void populateListView(ArrayList<EarthQuake> list) {
        EarthQuakeAdapter adapter = new EarthQuakeAdapter(this, list);
        listView.setAdapter(adapter);
    }

    private void setListViewListener() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                EarthQuake eq = earthQuakeViewmodel.getItemAtPosition(position);

                // open browser to load the url to view details of the clicked earthquake
                Intent seeDetails = new Intent(Intent.ACTION_VIEW);
                seeDetails.setData(Uri.parse(eq.getDetailURL()));
                startActivity(seeDetails);
            }
        });
    }
}
