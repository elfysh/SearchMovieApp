package com.example.searchmovieapp.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;

import com.example.searchmovieapp.Adapters.SearchListAdapter;
import com.example.searchmovieapp.Domain.Film;
import com.example.searchmovieapp.R;
import com.example.searchmovieapp.databinding.FragmentSearchBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;


public class SearchFragment extends Fragment {
    private FragmentSearchBinding binding;
    private FirebaseDatabase database;
    private ArrayList<Film> films;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = FragmentSearchBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        database = FirebaseDatabase.getInstance();
        initView();
        binding.searchBar.clearFocus();
        binding.searchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                                                     @Override
                                                     public boolean onQueryTextSubmit(String query) {

                                                         return false;
                                                     }

                                                     @Override
                                                     public boolean onQueryTextChange(String newText) {
                                                         filterList(newText);
                                                         return true;
                                                     }
                                                 }

        );

        return binding.getRoot();
    }

    private void filterList(String text) {
        if (films == null) {
            return;
        }

        ArrayList<Film> filteredList = new ArrayList<>();
        for (Film item: films){
            if (item.getTitle().toLowerCase().contains(text.toLowerCase())){
                filteredList.add(item);
            }
        }
        if (filteredList.isEmpty()){

        }
        else{
            binding.viewMovies.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
            binding.viewMovies.setAdapter(new SearchListAdapter(filteredList));
        }
    }


    private void initView(){
        DatabaseReference myRef = database.getReference("Items");
        binding.progressBar.setVisibility(View.VISIBLE);
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
                        binding.viewMovies.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
                        binding.viewMovies.setAdapter(new SearchListAdapter(items));
                    }
                    binding.progressBar.setVisibility(View.GONE);
                }
                films = items;
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {}

        });
}
}