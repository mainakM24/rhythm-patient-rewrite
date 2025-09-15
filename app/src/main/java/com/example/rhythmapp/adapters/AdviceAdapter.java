package com.example.rhythmapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rhythmapp.R;
import com.example.rhythmapp.models.Advice;

import java.util.List;

public class AdviceAdapter extends RecyclerView.Adapter< AdviceAdapter.AdviceViewHolder > {

    private final List<Advice> adviceList;

    public AdviceAdapter(List<Advice> adviceList) {
        this.adviceList = adviceList;
    }

    @NonNull
    @Override
    public AdviceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_advice, parent, false);
        return new AdviceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdviceViewHolder holder, int position) {

        Advice advice = adviceList.get(position);
        String adviceDescription = advice.getUser_name() + " . " + advice.getComment_date();

        holder.tvAdviceMessage.setText(advice.getComment_text());
        holder.tvAdviceDescription.setText(adviceDescription);
        //holder.imgAdviceIcon.setImageDrawable(advice.getUser_icon());
    }

    @Override
    public int getItemCount() {
        return adviceList.size();
    }

    public static class AdviceViewHolder extends RecyclerView.ViewHolder {

        TextView tvAdviceMessage, tvAdviceDescription;
        ImageView imgAdviceIcon;
        public AdviceViewHolder(@NonNull View itemView) {
            super(itemView);

            tvAdviceDescription = itemView.findViewById(R.id.tv_advice_description);
            tvAdviceMessage = itemView.findViewById(R.id.tv_advice_message);
            imgAdviceIcon = itemView.findViewById(R.id.img_advice_icon);
        }
    }
}
