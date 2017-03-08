package com.zaafoohpl.zaafoo.fragment;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zaafoohpl.zaafoo.myapplication.R;

import java.util.ArrayList;

/**
 * Created by SUB on 2/17/2017.
 */

public class PastBookingAdapter extends RecyclerView.Adapter<PastBookingAdapter.ViewHolder> {


    ArrayList<Past> pastBookings;
    Context context;

    public PastBookingAdapter(ArrayList<Past> pastBookings, Context context) {
        this.pastBookings = pastBookings;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.past_booking_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        holder.rest_name.setText(pastBookings.get(position).getRestname());
        holder.total_amount.setText("Rs."+pastBookings.get(position).getTotal());
        holder.advance_amount.setText("Rs."+pastBookings.get(position).getAdvance());

    }

    @Override
    public int getItemCount() {
        return pastBookings.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView rest_name;
        TextView total_amount;
        TextView advance_amount;

        public ViewHolder(View itemView) {
            super(itemView);
            rest_name=(TextView)itemView.findViewById(R.id.textView15);
            total_amount=(TextView)itemView.findViewById(R.id.textView18);
            advance_amount=(TextView)itemView.findViewById(R.id.textView19);

        }
    }
}
