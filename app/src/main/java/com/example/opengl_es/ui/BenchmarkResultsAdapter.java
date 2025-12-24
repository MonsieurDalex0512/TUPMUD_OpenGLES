package com.example.opengl_es.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.opengl_es.R;
import com.example.opengl_es.benchmark.BenchmarkResult;

import java.util.List;

public class BenchmarkResultsAdapter extends RecyclerView.Adapter<BenchmarkResultsAdapter.ViewHolder> {
    
    private List<BenchmarkResult> results;
    
    public BenchmarkResultsAdapter(List<BenchmarkResult> results) {
        this.results = results;
    }
    
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
            .inflate(android.R.layout.simple_list_item_2, parent, false);
        return new ViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BenchmarkResult result = results.get(position);
        holder.text1.setText(result.testName);
        holder.text2.setText(result.toString());
    }
    
    @Override
    public int getItemCount() {
        return results != null ? results.size() : 0;
    }
    
    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView text1;
        TextView text2;
        
        ViewHolder(View view) {
            super(view);
            text1 = view.findViewById(android.R.id.text1);
            text2 = view.findViewById(android.R.id.text2);
        }
    }
}

