package com.example.yousafkhan.equake;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
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
        // if listview already has an adapter set, then use that adapter
        EarthQuakeAdapter adapter = (EarthQuakeAdapter) listView.getAdapter();

        if(adapter == null) {
            adapter = new EarthQuakeAdapter(this, list);
            listView.setAdapter(adapter);
        } else {
            adapter.addAll(list);
        }
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.reload_data:
                ArrayList<EarthQuake> list = earthQuakeViewmodel.getDataList().getValue();

                if(list != null) {
                    // clear listview adapter
                    EarthQuakeAdapter adapter = (EarthQuakeAdapter) listView.getAdapter();
                    adapter.clear();
                    adapter.notifyDataSetChanged();

                    progressBar.setVisibility(View.VISIBLE);

                    earthQuakeViewmodel.loadData();
                }

                break;

            case R.id.settings:
                Intent openSettings = new Intent(this, SettingsActivity.class);
                startActivity(openSettings);
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
