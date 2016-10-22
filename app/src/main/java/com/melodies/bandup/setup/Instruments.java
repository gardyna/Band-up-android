package com.melodies.bandup.setup;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.melodies.bandup.R;

import org.json.JSONArray;


/**
 * An activity class that controls the Instruments view.
 */
public class Instruments extends AppCompatActivity {
    private GridView    gridView;
    private ProgressBar progressBar;
    private SetupShared sShared;
    private TextView    txtNoInstruments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instruments);
        progressBar      = (ProgressBar) findViewById(R.id.instrumentProgressBar);
        progressBar.setVisibility(View.VISIBLE);

        TextView txt1 = (TextView) findViewById(R.id.textView);
        TextView txt2 = (TextView) findViewById(R.id.textView2);
        TextView txt3 = (TextView) findViewById(R.id.textView3);
        txt1.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/caviar_dreams.ttf"));
        txt3.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/caviar_dreams.ttf"));
        txt2.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/caviar_dreams_bold.ttf"));

        gridView         = (GridView) findViewById(R.id.instrumentGridView);
        txtNoInstruments = (TextView) findViewById(R.id.txtNoInstruments);
        sShared          = new SetupShared();

        // Gets the list of instruments.
        sShared.getInstruments(Instruments.this, gridView, progressBar, txtNoInstruments);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                sShared.toggleItemSelection(getApplicationContext(), parent, view, position);
            }
        });
    }

    public void onClickNext(View v) {
        if (v.getId() == R.id.btnNext) {
            DoubleListAdapter dla = (DoubleListAdapter) gridView.getAdapter();

            // The adapter for the GridView hasn't been set.
            // This means we didn't get data from the server.
            if (dla == null) {
                Toast.makeText(Instruments.this, "I cannot contact the server.\nPlease contact support.", Toast.LENGTH_LONG).show();
                return;
            }

            // Send the items that the user selected to the server.
            JSONArray selectedInstruments = sShared.prepareSelectedList(Instruments.this, dla);
            if (selectedInstruments.length() > 0) {
                sShared.postInstruments(Instruments.this, selectedInstruments);
                Intent toUserListIntent = new Intent(Instruments.this, Genres.class);
                Instruments.this.startActivity(toUserListIntent);
                overridePendingTransition(R.anim.no_change, R.anim.slide_out_left);
                finish();
            } else {
                Toast.makeText(Instruments.this, R.string.setup_no_instrument_selection, Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(!isTaskRoot()) {
            overridePendingTransition(R.anim.no_change, R.anim.slide_out_left);
        }
    }
}