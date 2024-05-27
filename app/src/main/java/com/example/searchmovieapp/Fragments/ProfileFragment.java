package com.example.searchmovieapp.Fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.searchmovieapp.Activities.LoginActivity;
import com.example.searchmovieapp.Adapters.FilmListAdapter;
import com.example.searchmovieapp.Domain.Film;
import com.example.searchmovieapp.R;
import com.example.searchmovieapp.databinding.ActivityProfileBinding;
import com.example.searchmovieapp.databinding.FragmentMainBinding;
import com.example.searchmovieapp.databinding.FragmentProfileBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

public class ProfileFragment extends Fragment {
    private FragmentProfileBinding binding;
    private SharedPreferences sharedPreferences;
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        sharedPreferences = requireContext().getSharedPreferences("userData", Context.MODE_PRIVATE);
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        updateUser();
        setSaved();
        setLiked();


        Animation fadeInAnimation = AnimationUtils.loadAnimation(requireContext(), R.anim.fade_in);
        binding.HelloUser.startAnimation(fadeInAnimation);
        binding.emailUser.startAnimation(fadeInAnimation);
        binding.exitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });
        binding.editProf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditFragment editFragment = new EditFragment();
                // Показываем EditFragment как диалоговое окно
                editFragment.show(getParentFragmentManager(), "EditFragment");
                updateUser();
                refreshFragment();
            }
        });
        


        return binding.getRoot();
    }

    private void updateUser() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId);

            // Получаем данные пользователя из Firebase
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String helloUser = "Привет, " + snapshot.child("name").getValue(String.class);
                        String emailUser = snapshot.child("email").getValue(String.class);
                        Animation fadeInAnimation = AnimationUtils.loadAnimation(requireContext(), R.anim.fade_in);
                        // Обновляем текстовые поля
                        binding.HelloUser.setText(helloUser);
                        binding.HelloUser.startAnimation(fadeInAnimation);
                        binding.emailUser.setText(emailUser);
                        binding.emailUser.startAnimation(fadeInAnimation);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("UpdateUser", "Ошибка при получении данных пользователя из Firebase: " + error.getMessage());
                }
            });
        }
    }


    private void setSaved() {
        DatabaseReference userSavedRef = FirebaseDatabase.getInstance().getReference()
                .child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("saved");

        binding.progressBarSaved.setVisibility(View.VISIBLE);
        userSavedRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    ArrayList<Film> savedItems = new ArrayList<>();
                    for (DataSnapshot filmSnapshot : snapshot.getChildren()) {
                        Film film = filmSnapshot.getValue(Film.class);
                        savedItems.add(film);
                    }

                    if (!savedItems.isEmpty()) {
                        binding.viewSavedMovies.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
                        binding.viewSavedMovies.setAdapter(new FilmListAdapter(savedItems));
                    }
                }
                binding.progressBarSaved.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Обработка ошибок при запросе данных
                binding.progressBarSaved.setVisibility(View.GONE);
            }
        });
    }


    private void refreshFragment() {
        FragmentTransaction ft = getParentFragmentManager().beginTransaction();
        ft.detach(this).attach(this).commit();
    }


    private void setLiked() {
        DatabaseReference userSavedRef = FirebaseDatabase.getInstance().getReference()
                .child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("favorites");

        binding.progressBarLiked.setVisibility(View.VISIBLE);
        userSavedRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    ArrayList<Film> likedItems = new ArrayList<>();
                    for (DataSnapshot filmSnapshot : snapshot.getChildren()) {
                        Film film = filmSnapshot.getValue(Film.class);
                        likedItems.add(film);
                    }

                    if (!likedItems.isEmpty()) {
                        binding.viewLikedMovies.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
                        binding.viewLikedMovies.setAdapter(new FilmListAdapter(likedItems));
                    }
                }
                binding.progressBarLiked.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Обработка ошибок при запросе данных
                binding.progressBarLiked.setVisibility(View.GONE);
            }
        });
    }


    private void signOut() {
        if (mAuth != null) {
            mAuth.signOut();
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // This will clear all activities from the back stack
            startActivity(intent);
            getActivity().finish();
        } else {
            Log.e("ProfileFragment", "FirebaseAuth instance is null");
        }
    }


}
