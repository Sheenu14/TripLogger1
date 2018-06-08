package com.booking.triplogger;

import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public class TripAdapter extends RecyclerView.Adapter<TripAdapter.MyViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(TripModelDetails item);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public AppCompatTextView titleText, dateText, destinationText;

        public MyViewHolder(View view) {
            super(view);
            titleText = (AppCompatTextView) view.findViewById(R.id.titleTextView);
            dateText = (AppCompatTextView) view.findViewById(R.id.dateTextView);
            destinationText = (AppCompatTextView) view.findViewById(R.id.destinationTextView);
        }

        public void bind(final TripModelDetails item, final OnItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    listener.onItemClick(item);
                }
            });
        }
    }

    private List<TripModelDetails> tripModelDetailsList;
    private Context context;
    private final OnItemClickListener listener;

    public TripAdapter(List<TripModelDetails> materialDetailsList, Context context, OnItemClickListener listener) {
        this.tripModelDetailsList = materialDetailsList;
        this.context = context;
        this.listener = listener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.listitem_layout, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        holder.bind(tripModelDetailsList.get(position), listener);

        TripModelDetails materialDetails = tripModelDetailsList.get(position);
        holder.titleText.setText(materialDetails.getTitle());
        holder.dateText.setText(materialDetails.getDate());
        holder.destinationText.setText(materialDetails.getDestination());

    }

    @Override
    public int getItemCount() {
        return tripModelDetailsList.size();
    }
}
