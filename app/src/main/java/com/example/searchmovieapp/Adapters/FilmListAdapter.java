package com.example.searchmovieapp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.searchmovieapp.Activities.DetailActivity;
import com.example.searchmovieapp.Domain.Film;
import com.example.searchmovieapp.R;

import java.util.ArrayList;

public class FilmListAdapter extends RecyclerView.Adapter<FilmListAdapter.ViewHolder> {

    ArrayList<Film> items;
    Context context;
    public FilmListAdapter(ArrayList<Film> items){
        this.items=items;
    }
    @NonNull
    @Override
    public FilmListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context =parent.getContext();
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.film_viewholder,parent,false);

        return new ViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull FilmListAdapter.ViewHolder holder, int position) {
        holder.titleTxt.setText(items.get(position).getTitle());
        RequestOptions requestOptions=new RequestOptions();
        requestOptions=requestOptions.transform(new CenterCrop(), new RoundedCorners(30));


        Glide.with(context).load(items.get(position).getImgLink()).apply(requestOptions).into(holder.pic);


        holder.itemView.setOnClickListener((new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DetailActivity.class);
                intent.putExtra("object",items.get(position));
                context.startActivity(intent);
            }
        }));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView titleTxt;
        ImageView pic;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTxt=itemView.findViewById(R.id.titleTxt);
            pic = itemView.findViewById(R.id.pic);
        }
    }
}
