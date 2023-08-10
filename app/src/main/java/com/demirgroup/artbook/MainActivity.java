package com.demirgroup.artbook;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.demirgroup.artbook.databinding.ActivityMainBinding;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    Artadapter artadapter;
    private ActivityMainBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityMainBinding.inflate(getLayoutInflater());
        View view=binding.getRoot();
        setContentView(view);
        setSupportActionBar(binding.myToolbar);
        artArrayList=new ArrayList<>();
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        artadapter=new Artadapter(artArrayList);
        binding.recyclerView.setAdapter(artadapter);
        getdata();
    }
    ArrayList<Art> artArrayList;
    public void getdata(){
        try {
            SQLiteDatabase sqLiteDatabase=this.openOrCreateDatabase("Artbook",MODE_PRIVATE,null);
            Cursor cursor=sqLiteDatabase.rawQuery("SELECT * FROM artbook",null);
            while (cursor.moveToNext()){
                Art art=new Art(cursor.getString(1),cursor.getInt(0));
                artArrayList.add(art);
            }
            cursor.close();
            artadapter.notifyDataSetChanged();
        }catch (Exception e){
            e.printStackTrace();
        }

    }
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId()==R.id.art_id1){
            Intent intent=new Intent(this, frmdetails.class);
            intent.putExtra("info","new");
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}