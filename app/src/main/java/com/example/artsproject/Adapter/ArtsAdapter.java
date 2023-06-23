package com.example.artsproject.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.artsproject.Model.Arts;
import com.example.artsproject.databinding.RcyclerRowBinding;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ArtsAdapter extends RecyclerView.Adapter<ArtsAdapter.PostHolder> {
    private ArrayList<Arts> postArtList;

    public ArtsAdapter(ArrayList<Arts> postArtList) {
        this.postArtList = postArtList;
    }

    @NonNull
    @Override
    public PostHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RcyclerRowBinding rcyclerRowBinding = RcyclerRowBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new PostHolder(rcyclerRowBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull PostHolder holder, int position) {
        holder.recyclerRowBinding.recyclerViewName.setText(postArtList.get(position).getName());
        holder.recyclerRowBinding.recyclerViewArtistName.setText(postArtList.get(position).getPainterName());
        holder.recyclerRowBinding.recyclerViewYear.setText(postArtList.get(position).getYear());
        Picasso.get().load(postArtList.get(position).getDownloadUrl()).into(holder.recyclerRowBinding.recyclerViewimage);
    }

    @Override
    public int getItemCount() {
        return postArtList.size();
    }

    class PostHolder extends RecyclerView.ViewHolder {

        RcyclerRowBinding recyclerRowBinding;
        public PostHolder(RcyclerRowBinding recyclerRowBinding) {
            super(recyclerRowBinding.getRoot());
            this.recyclerRowBinding = recyclerRowBinding;
        }
    }

}
