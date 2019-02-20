package com.example.vyaparivyvasayicongressapp;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;
import ir.apend.slider.model.Slide;
import ir.apend.slider.ui.Slider;


public class MainPage extends AppCompatActivity {
    Button button;
    Slider slider;
    List<Slide> slideList = new ArrayList<>();
    DatabaseReference reference;
    FirebaseDatabase database;
    Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.activity_main_page);
        button = findViewById(R.id.button);
        slider = findViewById(R.id.banner_slider);
        getBannerSlides();
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {

                // Start NewActivity.class
                Intent myIntent = new Intent(MainPage.this,
                        Registration.class);
                startActivity(myIntent);
            }
        });
    }
    void getBannerSlides(){
        database = FirebaseDatabase.getInstance();
        reference = database.getReference("Slider-Images/carousal");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    ArrayList<String> array = (ArrayList<String>) dataSnapshot.getValue();
                    Log.e("datasnap", String.valueOf(array));
                    int length = array.size();
                    int i = 0;
                    while( i < length )
                    slideList.add(new Slide(0, array.get(i++), getResources().getDimensionPixelSize(R.dimen.slider_image_corner)));
                    Log.e("size", String.valueOf(slideList.size()));
                    slider.addSlides(slideList);

                }
                else{

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
