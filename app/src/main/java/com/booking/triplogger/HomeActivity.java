package com.booking.triplogger;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeActivity extends AppCompatActivity {

    private List<TripModelDetails> tripDetailsList = new ArrayList<>();
    private TripAdapter mAdapter;

    DataBaseHelper dataBaseHelper;

    public static TripModelDetails tripDetails;

    @BindView(R.id.tripList)
    RecyclerView tripList;

    @BindView(R.id.log)
    AppCompatButton log;

    @BindView(R.id.settings)
    AppCompatButton settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        ButterKnife.bind(this);

        dataBaseHelper = new DataBaseHelper(this);

        mAdapter = new TripAdapter(tripDetailsList, this, new TripAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(TripModelDetails item) {
                tripDetails = item;
                Intent intent = new Intent(HomeActivity.this, DetailsActivity.class);
                intent.putExtra("TripAdapter", "TripAdapter");
                startActivity(intent);
                finish();
            }
        });
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        tripList.setLayoutManager(mLayoutManager);
        tripList.setItemAnimator(new DefaultItemAnimator());
        tripList.setAdapter(mAdapter);

        prepareTempDetailsData();

        log.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, DetailsActivity.class);
                startActivity(intent);
                finish();
            }
        });

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, ProfileSettingsActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }


    private void prepareTempDetailsData() {

        tripDetailsList.addAll(dataBaseHelper.getAllTripsDetails());

        Log.e("size", "" + tripDetailsList.size());

        mAdapter.notifyDataSetChanged();
    }
}
