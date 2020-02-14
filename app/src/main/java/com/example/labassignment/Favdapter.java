package com.example.labassignment;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class Favdapter extends ArrayAdapter {

    Context mContext;
    int layoutRes;
    List<Favplace> favplaces;
    SQLiteDatabase sqLiteDatabase;
    public Favdapter(@NonNull Context mContext, int layoutRes,  List<Favplace> favplaces, SQLiteDatabase sqLiteDatabase) {
        super(mContext, layoutRes, favplaces);
        this.mContext = mContext;
        this.layoutRes = layoutRes;
        this.favplaces = favplaces;
        this.sqLiteDatabase = sqLiteDatabase;
    }
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View v = inflater.inflate(layoutRes,null);
        TextView address = v.findViewById(R.id.tvAddress);


        final  Favplace favplace = favplaces.get(position);
        address.setText(favplace.getAddress());


//
        return v;
    }
    private void loadfvplaces() {
        String sql = "SELECT * FROM favplaces";

        Cursor cursor = sqLiteDatabase.rawQuery(sql,null);

        if(cursor.moveToFirst()){
            do{
                favplaces.add(new Favplace(
                        cursor.getInt(0),

                        cursor.getString(1),
                        cursor.getDouble(2),
                        cursor.getDouble(3)

                ));

            }while(cursor.moveToNext());
            cursor.close();
            notifyDataSetChanged();


        }
    }


}
