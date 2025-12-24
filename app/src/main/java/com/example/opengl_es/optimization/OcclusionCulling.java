package com.example.opengl_es.optimization;

import com.example.opengl_es.scene.Camera;
import com.example.opengl_es.scene.Object3D;

import java.util.ArrayList;
import java.util.List;

/**
 * GPU-based occlusion culling using distance-based heuristic.
 * Objects far from camera are considered occluded and not rendered.
 * This reduces overdraw by not rendering objects that are likely hidden behind closer objects.
 */
public class OcclusionCulling {
    
    public List<Object3D> performOcclusionCulling(List<Object3D> candidates, Camera camera) {
        try {
            // Safety check: ensure candidates list is valid
            if (candidates == null) {
                android.util.Log.w("OcclusionCulling", "Candidates list is null, returning empty list");
                return new ArrayList<>();
            }
            
            if (candidates.isEmpty()) {
                return new ArrayList<>();
            }
            
            // Safety check: ensure camera is valid
            if (camera == null) {
                android.util.Log.w("OcclusionCulling", "Camera is null, returning all candidates");
                return new ArrayList<>(candidates);
            }
            
            // Get camera position safely - use default values if fails
            float camX = 0f, camY = 2f, camZ = 15f; // Default camera position
            try {
                camX = camera.getPositionX();
                camY = camera.getPositionY();
                camZ = camera.getPositionZ();
            } catch (Exception e) {
                android.util.Log.w("OcclusionCulling", "Error getting camera position, using defaults", e);
                // Use default camera position
            }
            
            // Simplified: use distance-based heuristic
            // Objects closer to camera are more likely visible (less likely to be occluded)
            List<Object3D> visible = new ArrayList<>();
            float maxDistanceSq = 200.0f; // Within ~14 units (sqrt(200) ≈ 14.14)
            
            // Process each candidate object
            for (Object3D obj : candidates) {
                // Skip null objects
                if (obj == null) {
                    continue;
                }
                
                try {
                    // Calculate distance squared from camera to object
                    float distSq = obj.getDistanceSquared(camX, camY, camZ);
                    
                    // Check for NaN or invalid values
                    if (Float.isNaN(distSq) || Float.isInfinite(distSq) || distSq < 0) {
                        // Invalid distance - add object to be safe
                        visible.add(obj);
                        continue;
                    }
                    
                    // Simple heuristic: objects closer to camera are more likely visible
                    // Objects far away are more likely to be occluded by closer objects
                    if (distSq < maxDistanceSq) {
                        visible.add(obj);
                    }
                    // Objects beyond maxDistance are considered occluded and not added
                    
                } catch (NullPointerException e) {
                    // If object has null fields, skip it
                    android.util.Log.w("OcclusionCulling", "NullPointerException for object, skipping");
                    continue;
                } catch (Exception e) {
                    // If any error calculating distance, add object to be safe
                    android.util.Log.w("OcclusionCulling", "Error calculating distance, adding object anyway", e);
                    visible.add(obj);
                }
            }
            
            // Safety: if we ended up with no visible objects but had candidates, return all
            // This prevents losing all objects if culling is too aggressive
            if (visible.isEmpty() && !candidates.isEmpty()) {
                android.util.Log.w("OcclusionCulling", "No visible objects after culling, returning all candidates");
                return new ArrayList<>(candidates);
            }
            
            return visible;
        } catch (Throwable e) { // Catch cả Error và Exception
            android.util.Log.e("OcclusionCulling", "Critical error in performOcclusionCulling", e);
            e.printStackTrace();
            // Return all candidates on error to prevent crash
            return candidates != null ? new ArrayList<>(candidates) : new ArrayList<>();
        }
    }
}

