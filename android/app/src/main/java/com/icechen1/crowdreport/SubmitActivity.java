package com.icechen1.crowdreport;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cloudinary.Cloudinary;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.icechen1.crowdreport.data.Issue;
import com.soundcloud.android.crop.Crop;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;


public class SubmitActivity extends ActionBarActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_IMAGE_CROP = 2;
    @InjectView(R.id.picture)
    ImageView picture;
    double lat;
    double lon;

    @InjectView(R.id.description)
    EditText description;
    @InjectView(R.id.location)
    TextView location;
    @InjectView(R.id.category)
    TextView category;
    private static String mSelectedItem;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private Map uploadResult;
    private Cloudinary cloudinary;
    private String mCurrentPhotoPath;
    private Uri output_file;

    @OnClick(R.id.picture)
    public void takePicture(View view) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            try {
                output_file = Uri.fromFile(createImageFile());
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        output_file);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @OnClick(R.id.gallery)
    public void gallery(View view) {
        Crop.pickImage(this);
    }
    @OnClick(R.id.location_change)
    void showDialog(View v) {
        DialogFragment newFragment = MapDialogFragment.newInstance(
                R.string.map_dialog);
        newFragment.show(getSupportFragmentManager(), "dialog");
    }

    @OnClick(R.id.category_change)
    void showChooserDialog(View v) {
        CategoryDialogFragment newFragment = new CategoryDialogFragment();
        newFragment.show(getSupportFragmentManager(), "dialog");
    }
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("SubmitActivity", "onActivityResult "+ requestCode);
        if ((requestCode == REQUEST_IMAGE_CAPTURE) && resultCode == RESULT_OK) {
            Uri outputUri = Uri.fromFile(new File(getCacheDir(), "cropped"));
            new Crop(output_file).output(outputUri).asSquare().start(this);
        } else if(requestCode == Crop.REQUEST_PICK && resultCode == RESULT_OK){
            Uri fullPhotoUri = data.getData();
            Uri outputUri = Uri.fromFile(new File(getCacheDir(), "cropped"));
            new Crop(fullPhotoUri).output(outputUri).asSquare().start(this);
        } else if (requestCode == Crop.REQUEST_CROP && resultCode == RESULT_OK) {
            output_file = Crop.getOutput(data);
            picture.setImageBitmap(BitmapFactory.decodeFile(output_file.getPath()));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit);
        ButterKnife.inject(this);
        buildGoogleApiClient();
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_submit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_submit) {
            submit();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void submit() {
        // Create the issue
        final Context c = this;
        final Issue issue = new Issue();
        issue.setDescription(description.getText().toString());
        issue.setCategory(mSelectedItem);
        if(mLastLocation != null){
            issue.setLat(mLastLocation.getLatitude());
            issue.setLon(mLastLocation.getLongitude());
        }

        // Insert the new item
        new AsyncTask<Void, Integer, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    publishProgress(0);
                    if(output_file != null){
                        String url = uploadPic();
                        issue.setPicture(cloudinary.url().generate(url));
                        publishProgress(50);
                    }
                    CrowdReportApplication.getInstance().mIssueTable.insert(issue).get();
                    publishProgress(100);
                    if (!issue.isComplete()) {
                        runOnUiThread(new Runnable() {
                            public void run() {
                                //mAdapter.add(item);
                                Toast.makeText(c, "Issue added", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        });
                    }
                } catch (final Exception exception) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(c, exception.toString(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                return null;
            }
            @Override
            protected void onProgressUpdate(Integer... progress) {
                Toast.makeText(getApplicationContext(),"Upload: "+progress[0], Toast.LENGTH_SHORT).show();
            }
        }.execute();
    }

    public String uploadPic(){
        Map config = new HashMap();
        config.put("cloud_name", "crowdreport");
        config.put("api_key", "634711247521955");
        config.put("api_secret", "mZkxHldOMCbI9lZ0KhV4v8VZU6Y");
        cloudinary = new Cloudinary(config);
        try {
            uploadResult = cloudinary.uploader().upload(output_file.getPath(), null);
            return (String) uploadResult.get("public_id");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void onConnected(Bundle bundle) {
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null) {
            Geocoder myLocation = new Geocoder(getApplicationContext(), Locale.getDefault());
            List<Address> myList = null;
            try {
                myList = myLocation.getFromLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude(), 1);
                location.setText("Near " + myList.get(0).getAddressLine(0));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    public class CategoryDialogFragment extends DialogFragment {
        int mNum;

        /**
         * Create a new instance of MyDialogFragment, providing "num"
         * as an argument.
         */
        CategoryDialogFragment newInstance(int num) {
            CategoryDialogFragment f = new CategoryDialogFragment();

            // Supply num input as an argument.
            Bundle args = new Bundle();
            args.putInt("num", num);
            f.setArguments(args);

            return f;
        }
        CategoryDialogFragment(){

        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final String[] array = {"Pot Holes", "Graffiti", "Traffic Signals/Sign", "Property Maintenance",
            "Dumping Violations", "Advertising Sign Violations","Water Line Breaks","Sewer Overflow or Break","Abandoned/Illegally Parked Vehicles",
            "Barking Dog","Other Problems"};
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            // Set the dialog title
            builder.setTitle(R.string.pick_category)
                    // Specify the list array, the items to be selected by default (null for none),
                    // and the listener through which to receive callbacks when items are selected
                    .setSingleChoiceItems(array, 0,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    mSelectedItem = array[which];
                                    category.setText(mSelectedItem);;
                                }
                            })
                            // Set the action buttons
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            // User clicked OK, so save the mSelectedItem results somewhere
                            // or return them to the component that opened the dialog

                        }
                    })
                    .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                        }
                    });

            return builder.create();
        }

    }

    public static class MapDialogFragment extends DialogFragment {
        int mNum;
        private GoogleMap mMap;

        /**
         * Create a new instance of MyDialogFragment, providing "num"
         * as an argument.
         */
        static MapDialogFragment newInstance(int num) {
            MapDialogFragment f = new MapDialogFragment();

            // Supply num input as an argument.
            Bundle args = new Bundle();
            args.putInt("num", num);
            f.setArguments(args);

            return f;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            int style = DialogFragment.STYLE_NO_TITLE, theme = 0;
            setStyle(style, theme);
            getDialog().setTitle(R.string.map_dialog);
            View v = inflater.inflate(R.layout.dialog_map, null);
            setUpMapIfNeeded();
            return v;
        }


        @Override
        public void onResume() {
            super.onResume();
            setUpMapIfNeeded();
        }

        private void setUpMapIfNeeded() {
            // Do a null check to confirm that we have not already instantiated the map.
            if (mMap == null) {
                // Try to obtain the map from the SupportMapFragment.
                GoogleMapOptions options = new GoogleMapOptions();
                options.compassEnabled(true)
                        .rotateGesturesEnabled(true)
                        .scrollGesturesEnabled(true)
                        .tiltGesturesEnabled(true);
                SupportMapFragment mMapFrag = SupportMapFragment.newInstance(options);
                FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
                fragmentTransaction.add(R.id.map_container, mMapFrag, "mapfragment");
                fragmentTransaction.commit();
                mMapFrag.getMapAsync(new OnMapReadyCallback() {
                    public void onMapReady(GoogleMap googleMap) {
                        mMap = googleMap;
                        setUpMap();
                    }
                });
            }
        }

        Marker m;

        /**
         * This is where we can add markers or lines, add listeners or move the camera. In this case, we
         * just add a marker near Africa.
         * <p/>
         * This should only be called once and when we are sure that {@link #mMap} is not null.
         */
        private void setUpMap() {
            mMap.setMyLocationEnabled(true);
            mMap.setIndoorEnabled(true);
            mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
                @Override
                public void onMyLocationChange(Location location) {
                    mMap.moveCamera(CameraUpdateFactory.newCameraPosition(CameraPosition.fromLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 6)));
                    //mMap.setOnMyLocationChangeListener(null);
                }
            });
            mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {
                    if (m != null) {
                        m.remove();
                    }
                    m = mMap.addMarker(new MarkerOptions().position(latLng).title("Selected location"));
                }
            });
            //mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
        }

    }
}
