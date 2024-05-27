package com.example.searchmovieapp.Fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.example.searchmovieapp.Activities.MainActivity;
import com.example.searchmovieapp.Activities.ProfileActivity;
import com.example.searchmovieapp.Adapters.FilmListAdapter;
import com.example.searchmovieapp.Adapters.SliderAdapters;
import com.example.searchmovieapp.Domain.Film;
import com.example.searchmovieapp.Domain.SliderItems;
import com.example.searchmovieapp.R;
import com.example.searchmovieapp.databinding.ActivityMainBinding;
import com.example.searchmovieapp.databinding.FragmentMainBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainFragment extends Fragment {

    private FragmentMainBinding binding;
    private FirebaseDatabase database;

    private ViewPager2 viewPager2;
    private Handler slideHandler = new Handler();


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = FragmentMainBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        database = FirebaseDatabase.getInstance();
        binding.addNew.setVisibility(View.INVISIBLE);
        checkAdminStatus();
        binding.addNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddFilmDialog();
            }
        });

        initTopMoving();
        initUpComming();
        initView();
        banners();

        return binding.getRoot();
    }
    private void showAddFilmDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_film, null);
        builder.setView(dialogView);

        // Находим все поля ввода в макете диалога
        EditText titleEditText = dialogView.findViewById(R.id.titleEditText);
        EditText yearEditText = dialogView.findViewById(R.id.yearEditText);
        EditText directorEditText = dialogView.findViewById(R.id.directorEditText);
        EditText genreEditText = dialogView.findViewById(R.id.genreEditText);
        EditText countryEditText = dialogView.findViewById(R.id.countryEditText);
        EditText plotEditText = dialogView.findViewById(R.id.plotEditText);
        EditText imdbEditText = dialogView.findViewById(R.id.imdbEditText);
        EditText imgLinkEditText = dialogView.findViewById(R.id.imgLinkEditText);

        // Создаем диалог
        AlertDialog dialog = builder.create();

        // Удаляем заголовок
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        dialog.show();

        // Находим кнопки и устанавливаем обработчики
        Button buttonSave = dialogView.findViewById(R.id.buttonSave);
        Button buttonCancel = dialogView.findViewById(R.id.buttonCancel);

        buttonSave.setOnClickListener(v -> {
            // Получаем значения из полей ввода
            String title = titleEditText.getText().toString();
            int year = Integer.parseInt(yearEditText.getText().toString());
            String director = directorEditText.getText().toString();
            String genre = genreEditText.getText().toString();
            String country = countryEditText.getText().toString();
            String plot = plotEditText.getText().toString();
            String imdb = imdbEditText.getText().toString();
            String imgLink = imgLinkEditText.getText().toString();

            // Создаем объект фильма
            Film film = new Film();
            film.setTitle(title);
            film.setYear(year);
            film.setDirector(director);
            film.setGenre(genre);
            film.setCountry(country);
            film.setPlot(plot);
            film.setIMDb(imdb);
            film.setImgLink(imgLink);

            // Добавляем фильм в базу данных
            DatabaseReference itemsRef = FirebaseDatabase.getInstance().getReference("Items");
            itemsRef.push().setValue(film);

            // Оповещаем пользователя о добавлении фильма
            Toast.makeText(requireContext(), "Фильм добавлен", Toast.LENGTH_SHORT).show();

            // Закрываем диалог
            dialog.dismiss();
        });

        buttonCancel.setOnClickListener(v -> dialog.cancel());
    }

    private void checkAdminStatus() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId);

            // Получаем значение атрибута "admin" пользователя из Firebase
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        boolean isAdmin = snapshot.child("admin").getValue(Boolean.class);
                        // Проверяем, является ли пользователь админом
                        if (isAdmin) {
                            // Если пользователь админ, делаем кнопку "addNew" видимой
                            binding.addNew.setVisibility(View.VISIBLE);
                        } else {
                            // Если пользователь не админ, скрываем кнопку "addNew"
                            binding.addNew.setVisibility(View.GONE);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("CheckAdminStatus", "Ошибка при получении данных пользователя из Firebase: " + error.getMessage());
                }
            });
        }
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
                        binding.viewTopMovies.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false));
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
                        binding.viewUpcomming.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL,false));
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
    public void onPause() {
        super.onPause();
        slideHandler.removeCallbacks(sliderRunnable);
    }



    @Override
    public void onResume() {
        super.onResume();
        slideHandler.postDelayed(sliderRunnable,2000);
    }

    private void initView() {
        viewPager2 = binding.viewPager;
    }


}
