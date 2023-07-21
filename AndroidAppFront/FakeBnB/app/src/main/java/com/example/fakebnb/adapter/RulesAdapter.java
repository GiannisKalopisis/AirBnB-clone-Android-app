package com.example.fakebnb.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fakebnb.R;

import java.util.ArrayList;

public class RulesAdapter extends RecyclerView.Adapter<RulesAdapter.RuleViewHolder> {

    private ArrayList<String> rules;

    public RulesAdapter(ArrayList<String> rules) {
        this.rules = rules;
    }

    @NonNull
    @Override
    public RuleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rule, parent, false);
        return new RuleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RuleViewHolder holder, int position) {
        String rule = rules.get(position);
        holder.textRuleItem.setText(rule);
    }

    @Override
    public int getItemCount() {
        return rules.size();
    }

    static class RuleViewHolder extends RecyclerView.ViewHolder {
        TextView textRuleItem;

        RuleViewHolder(@NonNull View itemView) {
            super(itemView);
            textRuleItem = itemView.findViewById(R.id.textRuleItem);
        }
    }
}

