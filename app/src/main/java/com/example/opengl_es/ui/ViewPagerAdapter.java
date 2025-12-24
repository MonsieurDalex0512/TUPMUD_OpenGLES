package com.example.opengl_es.ui;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class ViewPagerAdapter extends FragmentStateAdapter {
    
    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }
    
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new ControlPanelFragment();
            case 1:
                return new MetricsPanelFragment();
            case 2:
                return new ComparisonChartsFragment();
            default:
                return new ControlPanelFragment();
        }
    }
    
    @Override
    public int getItemCount() {
        return 3; // Control Panel, Metrics, Charts
    }
}

