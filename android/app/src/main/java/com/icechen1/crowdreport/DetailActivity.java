package com.icechen1.crowdreport;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.Date;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class DetailActivity extends ActionBarActivity {
    @InjectView(R.id.category)
    TextView category;
    @InjectView(R.id.time)
    TextView time;
    @InjectView(R.id.location)
    TextView location;
    @InjectView(R.id.description)
    TextView description;
    @InjectView(R.id.picture)
    ImageView picture;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.inject(this);
        // Create global configuration and initialize ImageLoader with this config
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
        .build();
        ImageLoader.getInstance().init(config);
        if(getIntent().getExtras() != null){
            Bundle extras = getIntent().getExtras();
            Date date = (Date) extras.getParcelable("time");
            if(date != null)
                time.setText(date.toString());
            final Double lat = extras.getDouble("lat");
            final Double lon = extras.getDouble("lon");
            location.setText(lat+ " " + lon);
            location.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Map intent
                    Uri gmmIntentUri = Uri.parse("geo:"+lat+","+lon);
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                    startActivity(mapIntent);
                }
            });

            if(extras.getString("description") != null)
                description.setText(extras.getString("description"));
            if(extras.getString("picture") != null)
                ImageLoader.getInstance().displayImage(extras.getString("picture"), picture);
            if(extras.getString("category") != null)
                category.setText(extras.getString("category"));
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detail, menu);
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
        }else if(id == android.R.id.home)    // Respond to the action bar's Up/Home button

        {
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
