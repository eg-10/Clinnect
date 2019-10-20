package com.example.clinnect1;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.clinnect.R;

import java.util.List;

public class PlaceAdapter extends RecyclerView.Adapter<PlaceAdapter.PlaceViewHolder> {
    private Context mCtx;
    private List<Place>placeList;

    public PlaceAdapter(Context mCtx, List<Place> placeList) {
        this.mCtx = mCtx;
        this.placeList = placeList;
    }

    @NonNull
    @Override
    public PlaceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater =  LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.list_layout, null);

        return new PlaceViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull PlaceViewHolder holder, int position) {
        final Place place = placeList.get(position);
        holder.textViewName.setText(place.getName());
        holder.textViewAddress.setText(place.getAddress());
        holder.textViewRating.setText(String.valueOf(place.getRating()));
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mCtx, PlaceDetails.class);
                intent.putExtra("placeID", place.getId());
                mCtx.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return placeList.size();
    }

    class PlaceViewHolder extends RecyclerView.ViewHolder{

        TextView textViewName, textViewAddress,textViewRating;
        CardView cardView;
        public PlaceViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.textViewName);
            textViewAddress = itemView.findViewById(R.id.textViewAddress);
            textViewRating = itemView.findViewById(R.id.textViewRating);
            cardView = itemView.findViewById((R.id.cardView));
        }
    }

}
