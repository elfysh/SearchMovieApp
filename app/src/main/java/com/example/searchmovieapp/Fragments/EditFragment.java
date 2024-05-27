package com.example.searchmovieapp.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.searchmovieapp.R;
import com.example.searchmovieapp.databinding.FragmentEditBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class EditFragment extends DialogFragment {

    private FragmentEditBinding binding;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentEditBinding.inflate(inflater, container, false);
        View view = binding.getRoot();



        binding.buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveChanges();
            }
        });

        binding.buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        return view;
    }

    private void saveChanges() {
        String newName = binding.editTextName.getText().toString();



        // Получаем уникальный идентификатор текущего пользователя
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        String userId = currentUser.getUid();

        // Получаем ссылку на узел пользователя в базе данных Firebase
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId);

        // Обновляем данные пользователя в базе данных Firebase
        userRef.child("name").setValue(newName)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Закрываем фрагмент после сохранения изменений
                        dismiss();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // В случае ошибки выводим сообщение в журнал
                        e.printStackTrace();
                    }
                });
    }


}