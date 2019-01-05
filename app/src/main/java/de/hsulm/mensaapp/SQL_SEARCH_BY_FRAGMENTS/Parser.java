package de.hsulm.mensaapp.SQL_SEARCH_BY_FRAGMENTS;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import de.hsulm.mensaapp.CLASS_OBJ_AND_ADPT.FoodClass;
import de.hsulm.mensaapp.FoodProfileActivity;

public class Parser extends AsyncTask<Void,Void,Integer> {

    private Context c;
    private String data;
    private ListView lv;
    private FoodClass food;

    private ArrayList<String> names=new ArrayList<>();
    private ArrayList<FoodClass> food_list = new ArrayList<>();

    public Parser(Context c, String data, ListView lv) {
        this.c = c;
        this.data = data;
        this.lv = lv;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Integer doInBackground(Void... params) {
        return this.parse();
    }

    @Override
    protected void onPostExecute(Integer integer) {
        super.onPostExecute(integer);

        if(integer==1)
        {

            ArrayAdapter adapter=new ArrayAdapter(c,android.R.layout.simple_list_item_1,names);
            lv.setAdapter(adapter);

            lv.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
                    String item = (String) lv.getItemAtPosition(position);
                    Intent intent = new Intent(c, FoodProfileActivity.class);
                    intent.putExtra("food", food_list.get(position));
                    c.startActivity(intent);
                }
            });

        }else {
            Toast.makeText(c,"Keine Suchergebnisse!",Toast.LENGTH_SHORT).show();
        }
    }

    private int parse()
    {
        try
        {
            JSONArray ja=new JSONArray(data);
            JSONObject jo=null;
            food_list.clear();

            names.clear();

            for(int i=0;i<ja.length();i++)
            {
                jo=ja.getJSONObject(i);
                String name=jo.getString("name");
                int id = jo.getInt("id");
                names.add(name);

                food = new FoodClass(jo.getInt("id"),
                        jo.getString("name"), jo.getString("category"),
                        jo.getInt("vegan"),
                        jo.getInt("vegetarian"), jo.getString("price"),
                        jo.getString("uuid"), jo.getInt("rating"), jo.getString("img_id_cover"));

                food_list.add(food);

            }

            return 1;

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return 0;
    }
}