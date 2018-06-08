package com.booking.triplogger;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Looper;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatImageView;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailsActivity extends AppCompatActivity {

    public static final int PERMISSIONS_MULTIPLE_REQUEST = 123;
    int TAKE_PHOTO_CODE = 0;
    String dir;

    @BindView(R.id.picEditText)
    AppCompatEditText picEditText;

    @BindView(R.id.dateEditText)
    AppCompatEditText dateEditText;

    @BindView(R.id.tripTypeEditText)
    AppCompatEditText tripTypeEditText;

    @BindView(R.id.destinationEditText)
    AppCompatEditText destinationEditText;

    @BindView(R.id.titleEditText)
    AppCompatEditText titleEditText;

    @BindView(R.id.durationEditText)
    AppCompatEditText durationEditText;

    @BindView(R.id.commentsEditText)
    AppCompatEditText commentsEditText;

    @BindView(R.id.latitudeEditText)
    AppCompatEditText latitudeEditText;

    @BindView(R.id.longitudeEditText)
    AppCompatEditText longitudeEditText;

    @BindView(R.id.save)
    AppCompatButton save;

    @BindView(R.id.delete)
    AppCompatButton delete;

    @BindView(R.id.map)
    AppCompatButton map;

    @BindView(R.id.cancel)
    AppCompatButton cancel;

    //Current Location
    // location last updated time
    private String mLastUpdateTime;

    // location updates interval - 10sec
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;

    // fastest updates interval - 5 sec
    // location updates will be received if another app is requesting the locations
    // than your app can handle
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = 5000;

    private static final int REQUEST_CHECK_SETTINGS = 100;


    // bunch of location related apis
    private FusedLocationProviderClient mFusedLocationClient;
    private SettingsClient mSettingsClient;
    private LocationRequest mLocationRequest;
    private LocationSettingsRequest mLocationSettingsRequest;
    private LocationCallback mLocationCallback;
    private Location mCurrentLocation;

    // boolean flag to toggle the ui
    private Boolean mRequestingLocationUpdates;

    DataBaseHelper dataBaseHelper;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        ButterKnife.bind(this);

        dataBaseHelper = new DataBaseHelper(this);

        dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/Trip Logger/";
        File newdir = new File(dir);
        newdir.mkdirs();

        picEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    checkPermission();
                } else {
                    captureImage();
                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        dateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDatePicker();
            }
        });

        tripTypeEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openListDialog();
            }
        });

        intent = getIntent();
        if(intent != null){
            if(intent.hasExtra("TripAdapter")){
                titleEditText.setText(HomeActivity.tripDetails.getTitle());
                tripTypeEditText.setText(HomeActivity.tripDetails.getTripType());
                destinationEditText.setText(HomeActivity.tripDetails.getDestination());
                durationEditText.setText(HomeActivity.tripDetails.getDuration());
                dateEditText.setText(HomeActivity.tripDetails.getDate());
                commentsEditText.setText(HomeActivity.tripDetails.getComment());

                latitudeEditText.setText(HomeActivity.tripDetails.getLatitude());
                longitudeEditText.setText(HomeActivity.tripDetails.getLongitude());

                newfile = new File(HomeActivity.tripDetails.getPhoto());
                picEditText.setText("Image Entered");

                delete.setVisibility(View.VISIBLE);
                map.setVisibility(View.VISIBLE);
            }else {
                delete.setVisibility(View.GONE);
                map.setVisibility(View.GONE);
            }
        }else {
            delete.setVisibility(View.GONE);
            map.setVisibility(View.GONE);
        }

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = titleEditText.getText().toString().trim();
                String tripType = tripTypeEditText.getText().toString().trim();
                String destination = destinationEditText.getText().toString().trim();
                String duration = durationEditText.getText().toString().trim();
                String date = dateEditText.getText().toString().trim();
                String comments = commentsEditText.getText().toString().trim();

                if(title.length() <= 0 || tripType.length() <= 0 || destination.length() <= 0 || duration.length() <= 0 || date.length() <= 0 || comments.length() <= 0 || newfile == null){
                    Toast.makeText(DetailsActivity.this, "Please enter the all fields", Toast.LENGTH_LONG).show();
                }else {
                    if(intent != null) {
                        if (intent.hasExtra("BookingAdapter")) {
                            dataBaseHelper.updateTripDetails(Integer.parseInt(HomeActivity.tripDetails.getId()), title, date, tripType, destination, duration, comments, ""+newfile.getAbsolutePath(), ""+mCurrentLocation.getLatitude(), ""+mCurrentLocation.getLongitude());
                            Toast.makeText(DetailsActivity.this, "Booking updated successfully.", Toast.LENGTH_LONG).show();
                        }else {
                            dataBaseHelper.insertTripDetails(title, date, tripType, destination, duration, comments, ""+newfile.getAbsolutePath(), ""+mCurrentLocation.getLatitude(), ""+mCurrentLocation.getLongitude());
                            Toast.makeText(DetailsActivity.this, "Booking inserted successfully.", Toast.LENGTH_LONG).show();
                        }
                    }else {
                        dataBaseHelper.insertTripDetails(title, date, tripType, destination, duration, comments, ""+newfile.getAbsolutePath(), ""+mCurrentLocation.getLatitude(), ""+mCurrentLocation.getLongitude());
                        Toast.makeText(DetailsActivity.this, "Booking inserted successfully.", Toast.LENGTH_LONG).show();
                    }

                    onBackPressed();
                }

            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dataBaseHelper.deleteTripDetails(Integer.parseInt(HomeActivity.tripDetails.getId()));
                onBackPressed();
            }
        });

        map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetailsActivity.this, MapsActivity.class);
                intent.putExtra("Latitude",""+ HomeActivity.tripDetails.getLatitude());
                intent.putExtra("Longitude",""+ HomeActivity.tripDetails.getLongitude());
                startActivity(intent);
            }
        });

        init();
    }

    boolean checkShowAddress = true;

    private void openListDialog(){
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(this);
        builderSingle.setTitle("Select :-");

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.select_dialog_singlechoice);
        arrayAdapter.add("Work");
        arrayAdapter.add("Personal");
        arrayAdapter.add("Commute");

        builderSingle.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String strName = arrayAdapter.getItem(which);
                tripTypeEditText.setText(strName);
            }
        });
        builderSingle.show();
    }

    private void openDatePicker(){
        // Get Current Date
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);


        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {

                        String dayOfMonthString = ""+dayOfMonth;
                        if((""+dayOfMonth).length() == 1){
                            dayOfMonthString = "0"+dayOfMonthString;
                        }

                        String monthOfYearString = ""+monthOfYear + 1;
                        if((""+(monthOfYear + 1)).length() == 1){
                            monthOfYearString = "0"+(monthOfYear + 1);
                        }

                        dateEditText.setText(dayOfMonthString + "-" + monthOfYearString + "-" + year);

                    }
                }, mYear, mMonth, mDay);

        datePickerDialog.show();
    }

    Uri outputFileUri;
    File newfile;

    private void captureImage(){
        String file = dir+new Date()+".jpg";
        newfile = new File(file);
        try {
            newfile.createNewFile();
            outputFileUri = FileProvider.getUriForFile(this, "com.booking.triplogger.provider", newfile);
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            cameraIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);

            startActivityForResult(cameraIntent, TAKE_PHOTO_CODE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == TAKE_PHOTO_CODE && resultCode == RESULT_OK) {
            Log.e("CameraDemo", "Pic saved");
            picEditText.setText("Image Captured");
        }
    }

    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) + ContextCompat
                .checkSelfPermission(this,
                        Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale
                    (this, Manifest.permission.WRITE_EXTERNAL_STORAGE) ||
                    ActivityCompat.shouldShowRequestPermissionRationale
                            (this, Manifest.permission.CAMERA)) {

                requestPermissions(
                        new String[]{Manifest.permission
                                .WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA},
                        PERMISSIONS_MULTIPLE_REQUEST);
            } else {
                requestPermissions(
                        new String[]{Manifest.permission
                                .WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA},
                        PERMISSIONS_MULTIPLE_REQUEST);
            }
        } else {
            captureImage();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case PERMISSIONS_MULTIPLE_REQUEST:
                if (grantResults.length > 0) {
                    boolean cameraPermission = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    boolean readExternalFile = grantResults[0] == PackageManager.PERMISSION_GRANTED;

                    if(cameraPermission && readExternalFile) {
                        captureImage();
                    } else {
                        requestPermissions(
                                new String[]{Manifest.permission
                                        .WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA},
                                PERMISSIONS_MULTIPLE_REQUEST);
                    }
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent=new Intent(DetailsActivity.this,HomeActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onResume() {
        super.onResume();

        // Resuming location updates depending on button state and
        // allowed permissions
        if (mRequestingLocationUpdates && checkPermissions()) {
            startLocationUpdates();
        }

        updateLocationUI();
    }

    private boolean checkPermissions() {
        int permissionState = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    private void init() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mSettingsClient = LocationServices.getSettingsClient(this);

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                // location is received
                mCurrentLocation = locationResult.getLastLocation();
                mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());

                updateLocationUI();
            }
        };

        mRequestingLocationUpdates = false;

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        mLocationSettingsRequest = builder.build();

        startLocationButtonClick();
    }

    /**
     * Update the UI displaying the location data
     * and toggling the buttons
     */
    private void updateLocationUI() {
        if (mCurrentLocation != null) {

            Log.e(TAG, "Lat: " + mCurrentLocation.getLatitude() + ", " +
                    "Lng: " + mCurrentLocation.getLongitude());

            try {
                Geocoder geocoder;
                List<Address> addresses;
                geocoder = new Geocoder(this, Locale.getDefault());

                addresses = geocoder.getFromLocation(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude(), 1);

                String address = addresses.get(0).getAddressLine(0);

                latitudeEditText.setText(""+mCurrentLocation.getLatitude());
                longitudeEditText.setText(""+mCurrentLocation.getLongitude());

                destinationEditText.setText(address);

            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private static final String TAG = DetailsActivity.class.getSimpleName();

    /**
     * Starting location updates
     * Check whether location settings are satisfied and then
     * location updates will be requested
     */
    private void startLocationUpdates() {
        mSettingsClient
                .checkLocationSettings(mLocationSettingsRequest)
                .addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
                    @SuppressLint("MissingPermission")
                    @Override
                    public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                        Log.i(TAG, "All location settings are satisfied.");

                        //Toast.makeText(getApplicationContext(), "Started location updates!", Toast.LENGTH_SHORT).show();

                        //noinspection MissingPermission
                        mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                                mLocationCallback, Looper.myLooper());

                        updateLocationUI();
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        int statusCode = ((ApiException) e).getStatusCode();
                        switch (statusCode) {
                            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                Log.i(TAG, "Location settings are not satisfied. Attempting to upgrade " +
                                        "location settings ");
                                try {
                                    // Show the dialog by calling startResolutionForResult(), and check the
                                    // result in onActivityResult().
                                    ResolvableApiException rae = (ResolvableApiException) e;
                                    rae.startResolutionForResult(DetailsActivity.this, REQUEST_CHECK_SETTINGS);
                                } catch (IntentSender.SendIntentException sie) {
                                    Log.i(TAG, "PendingIntent unable to execute request.");
                                }
                                break;
                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                String errorMessage = "Location settings are inadequate, and cannot be " +
                                        "fixed here. Fix in Settings.";
                                Log.e(TAG, errorMessage);

                                Toast.makeText(DetailsActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                        }

                        updateLocationUI();
                    }
                });



    }

    public void startLocationButtonClick() {
        // Requesting ACCESS_FINE_LOCATION using Dexter library
        Dexter.withActivity(this)
                .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        mRequestingLocationUpdates = true;
                        startLocationUpdates();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        if (response.isPermanentlyDenied()) {
                            // open device settings when the permission is
                            // denied permanently
                            openSettings();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }

    public void stopLocationUpdates() {
        // Removing location updates
        mFusedLocationClient
                .removeLocationUpdates(mLocationCallback)
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(getApplicationContext(), "Location updates stopped!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void showLastKnownLocation() {
        if (mCurrentLocation != null) {
            Toast.makeText(getApplicationContext(), "Lat: " + mCurrentLocation.getLatitude()
                    + ", Lng: " + mCurrentLocation.getLongitude(), Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getApplicationContext(), "Last known location is not available!", Toast.LENGTH_SHORT).show();
        }
    }



    private void openSettings() {
        Intent intent = new Intent();
        intent.setAction(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package",
                BuildConfig.APPLICATION_ID, null);
        intent.setData(uri);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mRequestingLocationUpdates) {
            // pausing location updates
            stopLocationUpdates();
        }
    }

    ProgressDialog progress;

    public void showProgressBar(){
        progress = new ProgressDialog(this);
        progress.setTitle("Loading...!");
        progress.setMessage("Please Wait while loading...");
        progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
        progress.show();
    }

    public void dismissProgressBar(){
        if(progress != null){
            progress.dismiss();
        }
    }
}
