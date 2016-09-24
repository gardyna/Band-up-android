package com.melodies.bandup.setup;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;

import com.melodies.bandup.R;
import com.melodies.bandup.UserList;

/**
 * An activity class that controls the Genres view.
 */
public class Genres extends AppCompatActivity {
    private String url;
    private String route = "/genres";
    private GridView gridView;
    private ProgressBar progressBar;
    private SetupShared sShared;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_genres);

        url         = getResources().getString(R.string.api_address).concat(route);
        gridView    = (GridView) findViewById(R.id.genreGridView);
        progressBar = (ProgressBar) findViewById(R.id.genreProgressBar);
        sShared     = new SetupShared();

        sShared.getSetupItems(Genres.this, url, gridView, progressBar);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                sShared.toggleItemSelection(getApplicationContext(), parent, view, position);
            }
        });
    }

    public void onClickFinish(View v) {
        if (v.getId() == R.id.btnFinish) {
            DoubleListAdapter dla = (DoubleListAdapter) gridView.getAdapter();
            if (sShared.postSelectedItems(dla, Genres.this, url)) {
                Intent toUserListIntent = new Intent(Genres.this, UserList.class);
                Genres.this.startActivity(toUserListIntent);
                overridePendingTransition(R.anim.no_change, R.anim.slide_out_left);
                finish();
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