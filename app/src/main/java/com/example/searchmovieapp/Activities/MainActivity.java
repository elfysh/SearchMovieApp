package com.example.searchmovieapp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;

import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.example.searchmovieapp.Adapters.FilmListAdapter;
import com.example.searchmovieapp.Adapters.SliderAdapters;
import com.example.searchmovieapp.Domain.Film;
import com.example.searchmovieapp.Domain.SliderItems;
import com.example.searchmovieapp.R;
import com.example.searchmovieapp.databinding.ActivityMainBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private FirebaseDatabase database;
    RecyclerView.Adapter adapterBestMovies, AdapterUpComming,adapterCategory;
    private RecyclerView recyclerViewBestMovies, recyclerViewUpcomming, recyclerViewCategory;
    private RequestQueue mRequestQueue;
    private StringRequest mStringRequest1, mStringRequest2, mStringRequest3;
    private ProgressBar loading1, loading2, loading3;
    private ViewPager2 viewPager2;
    private Handler slideHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        database = FirebaseDatabase.getInstance();
        Window w = getWindow();
        w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        binding.profBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                startActivity(intent);

            }
        });


        initTopMoving();
        initUpComming();
        initView();
        banners();
    }
    private void initTopMoving(){
       DatabaseReference myRef = database.getReference("Items");
       binding.progressBarTop.setVisibility(View.VISIBLE);
       ArrayList<Film> items = new ArrayList<>();
       myRef.addListenerForSingleValueEvent(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot snapshot) {
               if (snapshot.exists()) {
                   for (DataSnapshot issue : snapshot.getChildren()) {
                       items.add(issue.getValue(Film.class));
                   }

                   Collections.shuffle(items);

                   if(!items.isEmpty()){
                       binding.viewTopMovies.setLayoutManager(new LinearLayoutManager(MainActivity.this,
                               LinearLayoutManager.HORIZONTAL,false));
                       binding.viewTopMovies.setAdapter(new FilmListAdapter(items));
                   }
                       binding.progressBarTop.setVisibility(View.GONE);
                   }
           }


           @Override
           public void onCancelled(@NonNull DatabaseError error) {}

           });
    }
    private void initUpComming(){
        DatabaseReference myRef = database.getReference("Upcomming");
        binding.progressBarTop.setVisibility(View.VISIBLE);
        ArrayList<Film> items = new ArrayList<>();
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot issue : snapshot.getChildren()) {
                        items.add(issue.getValue(Film.class));
                    }
                    if(!items.isEmpty()){
                        binding.viewUpcomming.setLayoutManager(new LinearLayoutManager(MainActivity.this,LinearLayoutManager.HORIZONTAL,false));
                        binding.viewUpcomming.setAdapter(new FilmListAdapter(items));
                    }
                    binding.progressBarUpcomming.setVisibility(View.GONE);
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {}

        });
    }



    private void banners() {
        List<SliderItems> sliderItems = new ArrayList<>();
        sliderItems.add(new SliderItems(R.drawable.wide1));
        sliderItems.add(new SliderItems(R.drawable.wide));
        sliderItems.add(new SliderItems(R.drawable.wide3));

        viewPager2.setAdapter(new SliderAdapters(sliderItems,viewPager2));
        viewPager2.setClipToPadding(false);
        viewPager2.setOffscreenPageLimit(3);
        viewPager2.setClipChildren(false);
        viewPager2.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);


        CompositePageTransformer compositePageTransformer = new CompositePageTransformer();
        compositePageTransformer.addTransformer(new MarginPageTransformer(40));
        compositePageTransformer.addTransformer(new ViewPager2.PageTransformer() {
            @Override
            public void transformPage(@NonNull View page, float position) {
                float r=1-Math.abs(position);
                page.setScaleY(0.85f+r*0.15f);
            }
        });
        viewPager2.setPageTransformer(compositePageTransformer);
        viewPager2.setCurrentItem(1);
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                slideHandler.removeCallbacks(sliderRunnable);
            }
        });
    }
    private Runnable sliderRunnable = new Runnable(){

        @Override
        public void run() {
            viewPager2.setCurrentItem(viewPager2.getCurrentItem()+1);
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        slideHandler.removeCallbacks(sliderRunnable);
    }

    @Override
    protected void onResume() {
        super.onResume();
        slideHandler.postDelayed(sliderRunnable,2000);
    }


    private void initView() {
        viewPager2 = findViewById(R.id.viewPager);


    }
}