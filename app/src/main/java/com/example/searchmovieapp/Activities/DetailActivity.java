package com.example.searchmovieapp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.GranularRoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.searchmovieapp.Domain.Film;
import com.example.searchmovieapp.R;
import com.example.searchmovieapp.databinding.ActivityDetailBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class DetailActivity extends AppCompatActivity {
    private ActivityDetailBinding binding;
    private FirebaseAuth mAuth;
    private DatabaseReference userFavRef;
    private DatabaseReference userSavedRef;
    private boolean isFav = false; // флаг, чтобы отслеживать, добавлен ли фильм в избранное
    private boolean isSaved = false; // флаг, чтобы отслеживать, добавлен ли фильм в сохраненное

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            userFavRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId).child("favorites");
            userSavedRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId).child("saved");
        }

        setVariable();
    }

    private void setVariable() {
        Film item = (Film) getIntent().getSerializableExtra("object");
        RequestOptions requestOptions = new RequestOptions();
        requestOptions = requestOptions.transform(new CenterCrop(), new GranularRoundedCorners(0, 0, 50, 50));

        Glide.with(this).load(item.getImgLink()).apply(requestOptions).into(binding.filmPic);

        binding.titleMovieTxt.setText(item.getTitle());
        binding.IMDB.setText("IMDB ID: " + item.getIMDb());
        binding.movieSummary.setText(item.getPlot());
        binding.movieTimeTxt.setText(item.getDirector());

        binding.infoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://www.imdb.com/title/tt" + item.getIMDb();
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
            }
        });

        binding.backimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        binding.like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleFavorite(item);
            }
        });

        binding.willWatch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleSaved(item);
            }
        });

        // Проверка, находится ли фильм в избранном и сохраненном
        checkIfFavorite(item);
        checkIfSaved(item);
    }

    private void toggleFavorite(Film item) {
        if (isFav) {
            // Удалить из избранного
            userFavRef.child(item.getIMDb()).removeValue()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            isFav = false;
                            updateLikeButtonUI();
                            Toast.makeText(DetailActivity.this, "Удалено из избранного", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(DetailActivity.this, "Ошибка при удалении", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            // Добавить в избранное
            userFavRef.child(item.getIMDb()).setValue(item)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            isFav = true;
                            updateLikeButtonUI();
                            Toast.makeText(DetailActivity.this, "Добавлено в избранное", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(DetailActivity.this, "Ошибка при добавлении", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void toggleSaved(Film item) {
        if (isSaved) {
            // Удалить из сохраненного
            userSavedRef.child(item.getIMDb()).removeValue()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            isSaved = false;
                            updateSavedButtonUI();
                            Toast.makeText(DetailActivity.this, "Удалено из сохраненного", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(DetailActivity.this, "Ошибка при удалении", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            // Добавить в сохраненное
            userSavedRef.child(item.getIMDb()).setValue(item)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            isSaved = true;
                            updateSavedButtonUI();
                            Toast.makeText(DetailActivity.this, "Добавлено в сохраненное", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(DetailActivity.this, "Ошибка при добавлении", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void checkIfFavorite(Film item) {
        userFavRef.child(item.getIMDb()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    isFav = true;
                } else {
                    isFav = false;
                }
                updateLikeButtonUI();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // В случае ошибки
            }
        });
    }

    private void checkIfSaved(Film item) {
        userSavedRef.child(item.getIMDb()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    isSaved = true;
                } else {
                    isSaved = false;
                }
                updateSavedButtonUI();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // В случае ошибки
            }
        });
    }

    private void updateLikeButtonUI() {
        if (isFav) {
            binding.like.setImageResource(R.drawable.fav);
        } else {
            binding.like.setImageResource(R.drawable.btn_2);
        }
    }

    private void updateSavedButtonUI() {
        if (isSaved) {
            binding.willWatch.setImageResource(R.drawable.baseline_bookmark_24);
        } else {
            binding.willWatch.setImageResource(R.drawable.bookmark);
        }
    }
}
