package com.example.labassignment;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class FavouritePlaces extends AppCompatActivity {
    SQLiteDatabase mDatabase;
    List<Favplace> favplaceslist;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourite_places);
        listView = findViewById(R.id.fvPlaces);
        favplaceslist = new ArrayList<>();
        mDatabase = openOrCreateDatabase(MainActivity.DATABASE_NAME, MODE_PRIVATE , null);
listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }
});
        loadfvplaces();
    }
    private void loadfvplaces() {
        String sql = "SELECT * FROM favplaces";

        Cursor cursor = mDatabase.rawQuery(sql,null);

        if(cursor.moveToFirst()){
            do{
                favplaceslist.add(new Favplace(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getDouble(2),
                        cursor.getDouble(3)

                        ));

            }while(cursor.moveToNext());
            cursor.close();

            // show item in a listView
            Favdapter favdapter = new Favdapter(this,R.layout.activity_fav_list_layout,favplaceslist, mDatabase);
            listView.setAdapter(favdapter);

        }
    }

}
