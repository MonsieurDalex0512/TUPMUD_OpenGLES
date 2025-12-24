package com.example.opengl_es.monitoring;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.util.ArrayList;
import java.util.List;

/**
 * Generates charts for performance visualization using MPAndroidChart.
 */
public class ChartGenerator {
    
    /**
     * Update FPS line chart with historical data.
     */
    public static void updateFpsChart(LineChart chart, List<Float> fpsHistory) {
        if (chart == null || fpsHistory == null || fpsHistory.isEmpty()) {
            return;
        }
        
        List<Entry> entries = new ArrayList<>();
        for (int i = 0; i < fpsHistory.size(); i++) {
            entries.add(new Entry(i, fpsHistory.get(i)));
        }
        
        LineDataSet dataSet = new LineDataSet(entries, "FPS");
        dataSet.setColor(0xFF00FF00);
        dataSet.setCircleColor(0xFF00FF00);
        dataSet.setLineWidth(2f);
        dataSet.setCircleRadius(3f);
        dataSet.setDrawCircleHole(false);
        dataSet.setValueTextSize(10f);
        dataSet.setDrawValues(false);
        
        LineData lineData = new LineData(dataSet);
        chart.setData(lineData);
        
        // Configure chart
        chart.getDescription().setEnabled(false);
        chart.setTouchEnabled(true);
        chart.setDragEnabled(true);
        chart.setScaleEnabled(true);
        chart.setPinchZoom(true);
        
        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.valueOf((int) value);
            }
        });
        
        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setAxisMinimum(0f);
        leftAxis.setAxisMaximum(120f);
        leftAxis.setDrawGridLines(true);
        
        chart.getAxisRight().setEnabled(false);
        chart.animateX(500);
        chart.invalidate();
    }
    
    /**
     * Create comparison bar chart for benchmark results.
     */
    public static void updateComparisonChart(BarChart chart, MetricsCollector.ComparisonReport report) {
        if (chart == null || report == null) {
            return;
        }
        
        List<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(0, report.avgFps));
        entries.add(new BarEntry(1, report.avgDrawCalls));
        entries.add(new BarEntry(2, report.avgTriangles / 100f)); // Scale down for visibility
        
        BarDataSet dataSet = new BarDataSet(entries, "Performance Metrics");
        dataSet.setColor(0xFF2196F3);
        dataSet.setValueTextSize(10f);
        
        BarData barData = new BarData(dataSet);
        chart.setData(barData);
        
        // Configure chart
        chart.getDescription().setEnabled(false);
        chart.setTouchEnabled(true);
        chart.setDragEnabled(true);
        chart.setScaleEnabled(true);
        
        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                String[] labels = {"FPS", "Draw Calls", "Triangles/100"};
                int index = (int) value;
                if (index >= 0 && index < labels.length) {
                    return labels[index];
                }
                return "";
            }
        });
        
        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setAxisMinimum(0f);
        leftAxis.setDrawGridLines(true);
        
        chart.getAxisRight().setEnabled(false);
        chart.animateY(500);
        chart.invalidate();
    }
}

