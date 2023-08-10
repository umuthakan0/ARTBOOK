package com.demirgroup.artbook;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.demirgroup.artbook.databinding.RecycleviewrowBinding;

import java.util.ArrayList;

public class Artadapter extends RecyclerView.Adapter<Artadapter.artholder> {
    @NonNull
    ArrayList<Art> artArrayList;

    public Artadapter(@NonNull ArrayList<Art> artArrayList) {
        this.artArrayList = artArrayList;
    }

    @Override
    public artholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecycleviewrowBinding recycleviewrowBinding=RecycleviewrowBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new artholder(recycleviewrowBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull artholder holder, int position) {
        holder.binding.rcycletextview.setText(artArrayList.get(position).name);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(holder.itemView.getContext(), frmdetails.class);
                intent.putExtra("info","old");
                intent.putExtra("artId",artArrayList.get(position).id);
                holder.itemView.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return artArrayList.size();
    }

    class artholder extends RecyclerView.ViewHolder{
        private RecycleviewrowBinding binding;
        public artholder(RecycleviewrowBinding binding) {
            super(binding.getRoot());
            this.binding=binding;
        }
    }
}
