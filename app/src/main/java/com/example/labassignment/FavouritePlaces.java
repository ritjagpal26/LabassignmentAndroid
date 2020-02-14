package com.example.labassignment;


import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;

import java.util.ArrayList;
import java.util.List;

public class FavouritePlaces extends AppCompatActivity  {

    SQLiteDatabase mDatabase;
    List<Favplace> favplaceslist;
    SwipeMenuListView listView;
    Favplace favplace;
    private static final String TAG = "FavouritePlaces";

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
        Intent intent = new Intent(FavouritePlaces.this
                , MainActivity.class);
        startActivity(intent);


    }
});

        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {
                // create "open" item
                SwipeMenuItem openItem = new SwipeMenuItem(
                        getApplicationContext());
                // set item background
                openItem.setBackground(new ColorDrawable(Color.rgb(255, 255, 255)));
                // set item width
                openItem.setWidth(170);
                // set item title
                openItem.setTitle("Update");
                // set item title fontsize
                openItem.setTitleSize(18);
                // set item title font color
                openItem.setTitleColor(Color.BLUE);
                // add to menu
                menu.addMenuItem(openItem);

                // create "delete" item
                SwipeMenuItem deleteItem = new SwipeMenuItem(
                        getApplicationContext());
                // set item background
                deleteItem.setBackground(new ColorDrawable(Color.rgb(255, 255, 255)));
                // set item width
                deleteItem.setWidth(170);
                // set a icon
                deleteItem.setIcon(R.drawable.ic_action_name);
                // add to menu
                menu.addMenuItem(deleteItem);
            }
        };


        listView.setMenuCreator(creator);
        listView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                switch (index) {
                    case 0:

                        AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
                        builder.setTitle("Are you sure?");
                        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String sql = "DELETE FROM favplaces WHERE id = ?";
                                mDatabase.execSQL(sql, new Integer[]{favplace.getId()});
                                loadfvplaces();

                            }
                        });
                        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();

                        Log.d(TAG,"On List View Item Clicked " + index);

                        break;
                    case 1:
                        Log.d(TAG,"On List View Item Clicked " + index);

                        break;
                }
                // false : close the menu; true : not close the menu
                return false;
            }
        });


        loadfvplaces();
    }


// set creator



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
            favdapter.notifyDataSetChanged();

        }
    }

}
