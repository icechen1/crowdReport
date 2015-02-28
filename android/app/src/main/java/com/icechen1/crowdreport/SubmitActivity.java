package com.icechen1.crowdreport;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;


public class SubmitActivity extends ActionBarActivity {
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_IMAGE_CROP = 2;
    @InjectView(R.id.picture)
    ImageView picture;
    @OnClick(R.id.picture)
    public void takePicture(View view){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }
    @OnClick(R.id.location_change)
    void showDialog(View v) {
        DialogFragment newFragment = MapDialogFragment.newInstance(
                R.string.map_dialog);
        newFragment.show(getSupportFragmentManager(), "dialog");
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Intent intent = new Intent(this, CropActivity.class);
            intent.putExtra("picture",extras);
            startActivityForResult(intent, REQUEST_IMAGE_CROP);
            //Bitmap imageBitmap = (Bitmap) extras.get("data");
            //mImageView.setImageBitmap(imageBitmap);
        }else if (requestCode == REQUEST_IMAGE_CROP && resultCode == RESULT_OK) {
            picture.setImageBitmap((Bitmap) data.getExtras().get("result"));
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit);
        ButterKnife.inject(this);
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
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
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
                fragmentTransaction.add(R.id.map_container, mMapFrag,"mapfragment");
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
                    if(m!=null){
                        m.remove();
                    }
                    m = mMap.addMarker(new MarkerOptions().position(latLng).title("Selected location"));
                }
            });
            //mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
        }
    }
}
