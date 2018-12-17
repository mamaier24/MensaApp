package de.hsulm.mensaapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.ListView;

import de.hsulm.mensaapp.SQL_SEARCH_BY_FRAGMENTS.SenderReceiver;

public class SearchActivity extends AppCompatActivity {

    //TODO: Delete variable urlAddress and access it over class Constants
    private String urlAddress="http:/s673993392.online.de/v1/searcher.php";
    private SearchView sv;
    private ListView lv;
    private ImageView noDataImg,noNetworkImg;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        lv= (ListView) findViewById(R.id.lv);
        sv= (SearchView) findViewById(R.id.sv);
        noDataImg= (ImageView) findViewById(R.id.nodataImg);
        noNetworkImg= (ImageView) findViewById(R.id.noserver);
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                SenderReceiver sr=new SenderReceiver(SearchActivity.this,urlAddress,query,lv,noDataImg,noNetworkImg);
                sr.execute();
                return false;
            }


            @Override
            public boolean onQueryTextChange(String query) {
                SenderReceiver sr=new SenderReceiver(SearchActivity.this,urlAddress,query,lv,noDataImg,noNetworkImg);
                sr.execute();

                return false;
            }

        });

    }

}