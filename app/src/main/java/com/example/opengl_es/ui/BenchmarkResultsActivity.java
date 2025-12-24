package com.example.opengl_es.ui;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.opengl_es.R;
import com.example.opengl_es.benchmark.BenchmarkResult;
import com.example.opengl_es.benchmark.BenchmarkResults;

import java.util.List;

public class BenchmarkResultsActivity extends AppCompatActivity {
    
    private TextView txtOverallScore;
    private RecyclerView recyclerResults;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_benchmark_results);
        
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        
        txtOverallScore = findViewById(R.id.txt_overall_score);
        recyclerResults = findViewById(R.id.recycler_results);
        
        // Get results from intent (would need to be passed from MainActivity)
        // For now, display placeholder
        displayResults(null);
    }
    
    public void displayResults(BenchmarkResults results) {
        if (results == null) {
            // Try to get from intent
            results = (BenchmarkResults) getIntent().getSerializableExtra("benchmark_results");
        }
        
        if (results == null) {
            txtOverallScore.setText("--");
            return;
        }
        
        float overallScore = results.getOverallScore();
        txtOverallScore.setText(String.format("%.1f", overallScore));
        
        // Setup RecyclerView
        List<BenchmarkResult> resultList = results.getResults();
        BenchmarkResultsAdapter adapter = new BenchmarkResultsAdapter(resultList);
        recyclerResults.setLayoutManager(new LinearLayoutManager(this));
        recyclerResults.setAdapter(adapter);
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        displayResults(null); // Load from intent
    }
    
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}

