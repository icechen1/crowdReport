package com.icechen1.crowdreport;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.edmodo.cropper.CropImageView;


public class CropActivity extends ActionBarActivity {

    private CropImageView cropImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop);
        // Initialize components of the app
        cropImageView = (CropImageView) findViewById(R.id.CropImageView);
        Bundle extras = getIntent().getExtras();
        if(extras != null){
            Bitmap bm = ((Bitmap)extras.getBundle("picture").get("data"));
            if(bm != null){
                cropImageView.setFixedAspectRatio(false);
                //cropImageView.setAspectRatio(10,10);
                bm.setDensity(50);
                cropImageView.setImageBitmap(bm);
                //cropImageView.setImageResource(R.drawable.background);
            }

        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_crop, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_rot) {
            cropImageView.rotateImage(90);
            return true;
        }
        if (id == R.id.action_done) {
            Intent returnIntent = new Intent();
            returnIntent.putExtra("result",cropImageView.getCroppedImage());
            setResult(RESULT_OK,returnIntent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
