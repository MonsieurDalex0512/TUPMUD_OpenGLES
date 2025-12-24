package com.example.opengl_es.utils;

import com.example.opengl_es.benchmark.BenchmarkResult;
import com.example.opengl_es.benchmark.BenchmarkResults;
import com.example.opengl_es.monitoring.MetricsCollector;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Exports benchmark results and metrics to CSV files.
 */
public class CSVExporter {
    
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.US);
    
    /**
     * Export benchmark results to CSV file.
     */
    public static File exportBenchmarkResults(BenchmarkResults results, File outputDir) throws IOException {
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }
        
        String filename = "benchmark_" + DATE_FORMAT.format(new Date()) + ".csv";
        File csvFile = new File(outputDir, filename);
        
        try (FileWriter writer = new FileWriter(csvFile)) {
            // Header
            writer.append("Test Name,Average FPS,Min FPS,Max FPS,Avg Frame Time (ms),");
            writer.append("Min Frame Time (ms),Max Frame Time (ms),Draw Calls,Triangles,");
            writer.append("Texture Binds,Shader Switches,Overdraw Ratio,Score,Unit\n");
            
            // Data rows
            for (BenchmarkResult result : results.getResults()) {
                writer.append(escape(result.testName)).append(",");
                writer.append(String.valueOf(result.averageFPS)).append(",");
                writer.append(String.valueOf(result.minFPS)).append(",");
                writer.append(String.valueOf(result.maxFPS)).append(",");
                writer.append(String.valueOf(result.averageFrameTimeMs)).append(",");
                writer.append(String.valueOf(result.minFrameTimeMs)).append(",");
                writer.append(String.valueOf(result.maxFrameTimeMs)).append(",");
                writer.append(String.valueOf(result.totalDrawCalls)).append(",");
                writer.append(String.valueOf(result.totalTriangles)).append(",");
                writer.append(String.valueOf(result.textureBinds)).append(",");
                writer.append(String.valueOf(result.shaderSwitches)).append(",");
                writer.append(String.valueOf(result.overdrawRatio)).append(",");
                writer.append(String.valueOf(result.score)).append(",");
                writer.append(escape(result.unit)).append("\n");
            }
            
            // Summary
            writer.append("\n");
            writer.append("Overall Score,").append(String.valueOf(results.getOverallScore())).append("\n");
            writer.append("Device,").append(escape(results.getDeviceInfo())).append("\n");
            writer.append("Timestamp,").append(DATE_FORMAT.format(new Date(results.getTimestamp()))).append("\n");
        }
        
        return csvFile;
    }
    
    /**
     * Export metrics snapshots to CSV file.
     */
    public static File exportMetrics(MetricsCollector collector, File outputDir) throws IOException {
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }
        
        String filename = "metrics_" + DATE_FORMAT.format(new Date()) + ".csv";
        File csvFile = new File(outputDir, filename);
        
        List<MetricsCollector.MetricsSnapshot> snapshots = collector.getSnapshots();
        
        try (FileWriter writer = new FileWriter(csvFile)) {
            // Header
            writer.append("Timestamp,FPS,Frame Time (ms),Draw Calls,Triangles,");
            writer.append("Texture Binds,Shader Switches,Overdraw Ratio,Objects Rendered,Objects Culled\n");
            
            // Data rows
            for (MetricsCollector.MetricsSnapshot snapshot : snapshots) {
                writer.append(String.valueOf(snapshot.timestamp)).append(",");
                writer.append(String.valueOf(snapshot.fps)).append(",");
                writer.append(String.valueOf(snapshot.frameTimeMs)).append(",");
                writer.append(String.valueOf(snapshot.drawCalls)).append(",");
                writer.append(String.valueOf(snapshot.triangles)).append(",");
                writer.append(String.valueOf(snapshot.textureBinds)).append(",");
                writer.append(String.valueOf(snapshot.shaderSwitches)).append(",");
                writer.append(String.valueOf(snapshot.overdrawRatio)).append(",");
                writer.append(String.valueOf(snapshot.objectsRendered)).append(",");
                writer.append(String.valueOf(snapshot.objectsCulled)).append("\n");
            }
        }
        
        return csvFile;
    }
    
    private static String escape(String value) {
        if (value == null) return "";
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}




