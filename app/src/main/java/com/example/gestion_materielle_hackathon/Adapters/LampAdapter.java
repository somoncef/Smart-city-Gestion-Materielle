package com.example.gestion_materielle_hackathon.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gestion_materielle_hackathon.R;
import com.example.gestion_materielle_hackathon.model.Lamp;

import java.util.List;

public class LampAdapter extends RecyclerView.Adapter<LampAdapter.LampViewHolder> {

    private List<Lamp> lamps;

    public LampAdapter(List<Lamp> lamps) {
        this.lamps = lamps;
    }

    public void setLamps(List<Lamp> lamps) {
        this.lamps = lamps;
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public LampViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lamp_item, parent, false);
        return new LampViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LampViewHolder holder, int position) {
        Lamp lamp = lamps.get(position);
        holder.lampId.setText(lamp.getId());
        // Set other views as needed
    }

    @Override
    public int getItemCount() {
        return lamps.size();
    }

    public static class LampViewHolder extends RecyclerView.ViewHolder {
        TextView lampId;
        View vPriority;

        public LampViewHolder(@NonNull View itemView) {
            super(itemView);
            lampId = itemView.findViewById(R.id.tvLampId);
            vPriority = itemView.findViewById(R.id.vPriority);
            // Initialize other views
        }
    }
}