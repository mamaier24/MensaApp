package de.hsulm.mensaapp;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import java.util.ArrayList;
import de.hsulm.mensaapp.CLASS_OBJ_AND_ADPT.FoodAdapter;
import de.hsulm.mensaapp.CLASS_OBJ_AND_ADPT.FoodClass;
import de.hsulm.mensaapp.JAVA_ID_AND_DATE_TIME.DateID;
import de.hsulm.mensaapp.SHARED_PREF_MANAGER.SharedPrefManager;
import de.hsulm.mensaapp.SQL_OPERATIONS.SQL_SEARCH_BY_DATEID.DatabaseOperationsDateID;
import de.hsulm.mensaapp.SQL_OPERATIONS.SQL_SEARCH_BY_DATEID.IDatabaseOperationsDateID;
import de.hsulm.mensaapp.CONNECTION_STATUS.Connection;

/**
 * Created by Marcel Maier on 30/11/18.
 * Class which implements the MAIN window
 */
public class UserAreaActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private RecyclerView recyclerView;
    private FoodAdapter recyclerViewAdapter = null;
    private RecyclerView.LayoutManager layoutManager;
    private SwipeRefreshLayout swipeRefresh;
    private TabLayout tabLayout;
    private DatabaseOperationsDateID operations = new DatabaseOperationsDateID(this);
    private DateID time = new DateID();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_area);

        tabLayout = (TabLayout)findViewById(R.id.tabLayout);
        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipeRefresh);
        swipeRefresh.setOnRefreshListener(this);

        if(Connection.getInstance().isOnline(this)) {
            ImageView noserver = (ImageView)findViewById(R.id.ivNoServer);
            noserver.setVisibility(View.INVISIBLE);

            initializeTabLayout();
            initializeRecycler();

            if (!SharedPrefManager.getInstance(this).isLoggedIn()) {
                finish();
                startActivity(new Intent(this, LoginActivity.class));
            }
        }else{
            ImageView ivNoserver = (ImageView)findViewById(R.id.ivNoServer);
            ivNoserver.setVisibility(View.VISIBLE);
        }
    }


    /*
     * Initializes the TabLayout with vegan and vegetarian food.
     */
    public void initializeTabLayout(){
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (recyclerViewAdapter != null) {
                    recyclerViewAdapter.clear();
                }
                initializeRecycler();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) { }

            @Override
            public void onTabReselected(TabLayout.Tab tab) { }

        });
    }


    /*
     * Initializes the Recycler and gets all current meals
     * Called on SwipeRefresh, onResume and onRestart.
     */
    public void initializeRecycler() {
        operations.getFoodFromDBbyDateID(time.getFoodID(), new IDatabaseOperationsDateID() {
            @Override
            public void onSuccess(final ArrayList<FoodClass> food_list) {
                recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
                layoutManager = new LinearLayoutManager(UserAreaActivity.this);

                //Doesnt check for vegan or vegetarian
                if (tabLayout.getSelectedTabPosition() == 0){
                    recyclerViewAdapter = new FoodAdapter(food_list);
                }

                //Checks for vegetarian food if tab is selected
                if(tabLayout.getSelectedTabPosition() == 1){

                    for(int i=0; i<food_list.size();i++){
                        if(food_list.get(i).isVegetarian() != 1){
                            food_list.remove(i);
                            i = i - 1;
                        }
                    }

                    recyclerViewAdapter = new FoodAdapter(food_list);
                }

                //Checks for vegan food if tab is selected
                if (tabLayout.getSelectedTabPosition() == 2){
                    for(int i=0; i<food_list.size();i++){
                        if(food_list.get(i).isVegan() == 0){
                            food_list.remove(i);
                            i = i - 1;
                        }
                    }
                    
                    recyclerViewAdapter = new FoodAdapter(food_list);
                }

                recyclerView.setLayoutManager(layoutManager);
                recyclerView.setAdapter(recyclerViewAdapter);

                recyclerViewAdapter.setOnItemClickListener(new FoodAdapter.OnItemClickListener() {
                    @Override
                    public void OnItemClick(int position) {
                        if(Connection.getInstance().isOnline(UserAreaActivity.this)) {
                            Intent intent = new Intent(UserAreaActivity.this, FoodProfileActivity.class);
                            intent.putExtra("food", food_list.get(position));
                            intent.putExtra("intent", "UserAreaActivity");
                            intent.putExtra("position", position);
                            startActivity(intent);
                        }else{
                            if(recyclerViewAdapter != null) {
                                recyclerViewAdapter.clear();
                            }

                            ImageView noserver = (ImageView)findViewById(R.id.ivNoServer);
                            noserver.setVisibility(View.VISIBLE);
                        }
                    }
                });
            }
        });
    }


    /*
     * Creates the menu spinner
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }


    /*
     * Handling of the menu spinner
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.Speiseplan:
                //Intent for "Speiseplan"
                Intent speiseplanIntent = new Intent(UserAreaActivity.this, MenuActivity.class);
                UserAreaActivity.this.startActivity(speiseplanIntent);
                break;

            case R.id.Inhaltsstoffe:
                //Intent for "Speiseplan"
                Intent InhaltsstoffeIntent = new Intent(UserAreaActivity.this, IngredientsActivity.class);
                UserAreaActivity.this.startActivity(InhaltsstoffeIntent);
                break;
                
            case R.id.Lob_Tadel:
                //Intent for "Lob und Tadel"
                Log.i("Want to Send Mail", "");
                String[] TO = {"mensa@studierendenwerk-ulm.de"};//Email
                String[] CC = new String[]{SharedPrefManager.getInstance(this).getUserEmail()};
                String UN = SharedPrefManager.getInstance(this).getUsername();
                Intent emailIntent = new Intent(Intent.ACTION_SEND);
                emailIntent.setData(Uri.parse("mailto:"));
                emailIntent.setType("text/plain");
                emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
                emailIntent.putExtra(Intent.EXTRA_CC, CC);
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Bitte geben sie Ihre Speise(n) an:\n");
                emailIntent.putExtra(Intent.EXTRA_TEXT, "Hier ist Platz für Lob und konstruktive Kritik:\n\n\n\nViele Grüße " + UN);

                try {
                    startActivity(Intent.createChooser(emailIntent, "Wählen Sie eine Email-App aus..."));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(this, "Kein Mailprogramm gefunden!", Toast.LENGTH_SHORT).show();
                }

                break;

            case R.id.Suche:
                //Intent zum Suchen
                Intent sucheIntent = new Intent(UserAreaActivity.this, SearchActivity.class);
                UserAreaActivity.this.startActivity(sucheIntent);
                break;

            case R.id.Abmelden:
                SharedPrefManager.getInstance(this).logout();
                finish();
                startActivity(new Intent(this, LoginActivity.class));
                break;

        }
        return true;
    }


    /*
     * Handling the SwipeRefresh
     */
    @Override
    public void onRefresh() {
        if(Connection.getInstance().isOnline(this)) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    ImageView noserver = (ImageView)findViewById(R.id.ivNoServer);
                    noserver.setVisibility(View.INVISIBLE);

                    if (recyclerViewAdapter != null) {
                        recyclerViewAdapter.clear();
                    }

                    initializeRecycler();

                    if (swipeRefresh != null) {
                        swipeRefresh.setRefreshing(false);
                    }
                }
            }, 4000);
        }else{
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {

                    if(recyclerViewAdapter != null) {
                        recyclerViewAdapter.clear();
                    }

                    ImageView noserver = (ImageView)findViewById(R.id.ivNoServer);
                    noserver.setVisibility(View.VISIBLE);

                    if (swipeRefresh != null) {
                        swipeRefresh.setRefreshing(false);
                    }
                }

            }, 4000);
        }
    }


    /*
     * Handling if the app is restarted
     */
    @Override
    public void onRestart() {
        if(Connection.getInstance().isOnline(this)) {
            super.onRestart();
            ImageView noserver = (ImageView)findViewById(R.id.ivNoServer);
            noserver.setVisibility(View.INVISIBLE);
            recyclerViewAdapter.clear();
            initializeRecycler();
        }else{
            super.onRestart();
            if (recyclerViewAdapter != null) {
                recyclerViewAdapter.clear();
            }
            ImageView noserver = (ImageView)findViewById(R.id.ivNoServer);
            noserver.setVisibility(View.VISIBLE);
        }
    }


    @Override
    public void onPause() {
        super.onPause();
    }

}

