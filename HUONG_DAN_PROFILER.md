# 📊 HƯỚNG DẪN SỬ DỤNG ANDROID STUDIO PROFILER CHO TỪNG TÍNH NĂNG

## 🎯 TỔNG QUAN

File này cung cấp hướng dẫn chi tiết cách sử dụng **Android Studio Profiler** để đo lường và so sánh performance cho từng tính năng tối ưu hóa trong OpenGL ES Optimization App.

---

## 📋 MỤC LỤC

1. [Cách mở Profiler](#1-cách-mở-profiler)
2. [Các chỉ số quan trọng](#2-các-chỉ-số-quan-trọng)
3. [Hướng dẫn cho từng tính năng](#3-hướng-dẫn-cho-từng-tính-năng)
   - [3.1. Back-face Culling](#31-back-face-culling)
   - [3.2. Frustum Culling](#32-frustum-culling)
   - [3.3. Occlusion Culling](#33-occlusion-culling)
   - [3.4. Level of Detail (LOD)](#34-level-of-detail-lod)
   - [3.5. Shader Optimization](#35-shader-optimization)
   - [3.6. ETC1 Texture Compression](#36-etc1-texture-compression)
   - [3.7. Mipmaps](#37-mipmaps)
   - [3.8. Texture Atlasing](#38-texture-atlasing)
   - [3.9. Instanced Rendering](#39-instanced-rendering)
   - [3.10. Depth Pre-pass](#310-depth-pre-pass)
   - [3.11. Overdraw Heatmap](#311-overdraw-heatmap)

---

## 1. CÁCH MỞ PROFILER

### Bước 1: Mở Android Studio
```
1. Mở Android Studio
2. Mở project: D:\TUPMUD\OpenGLES
```

### Bước 2: Chạy app
```
1. Kết nối thiết bị Android hoặc khởi động emulator
2. Run → Run 'app' (Shift + F10)
3. Đợi app khởi động hoàn toàn
```

### Bước 3: Mở Profiler
```
1. View → Tool Windows → Profiler
   (hoặc nhấn Alt + 6)
2. Chọn process: com.example.opengl_es
3. Profiler sẽ hiển thị 4 tabs:
   - CPU
   - Memory
   - Network
   - Energy
```

### Bước 4: Cấu hình Profiler
```
1. Click vào tab "CPU"
2. Chọn "Call Chart" view (biểu đồ gọi hàm)
3. Chọn "Flame Chart" view (biểu đồ ngọn lửa)
4. Chọn "Top Down" view (cây gọi hàm từ trên xuống)
```

---

## 2. CÁC CHỈ SỐ QUAN TRỌNG

### CPU Profiler:

#### CPU Usage (%):
- **Ý nghĩa**: Phần trăm CPU được sử dụng
- **Tốt**: < 50% (cho mobile app)
- **Xấu**: > 80% (có thể gây lag)

#### Thread Activity:
- **Ý nghĩa**: Hoạt động của các thread
- **Tốt**: Thread ổn định, không có spikes
- **Xấu**: Thread có nhiều spikes, không ổn định

#### Method Execution Time:
- **Ý nghĩa**: Thời gian thực thi của từng method
- **Quan trọng**: 
  - `MyGLRenderer.onDrawFrame()`: Thời gian render 1 frame
  - `CullingManager.cullObjects()`: Thời gian culling
  - `LODManager.getMeshForLOD()`: Thời gian tính LOD

### Memory Profiler:

#### Memory Usage (MB):
- **Ý nghĩa**: Bộ nhớ đang sử dụng
- **Tốt**: Ổn định, không tăng liên tục
- **Xấu**: Tăng liên tục (memory leak)

#### Allocations:
- **Ý nghĩa**: Số object được tạo
- **Tốt**: Ít allocations trong render loop
- **Xấu**: Nhiều allocations (gây GC)

---

## 3. HƯỚNG DẪN CHO TỪNG TÍNH NĂNG

### 3.1. BACK-FACE CULLING

#### Mục đích:
Đo lường sự khác biệt CPU usage và frame time khi bật/tắt Back-face Culling.

#### Bước 1: Chuẩn bị
```
1. Mở app, đợi ổn định 5 giây
2. Mở Android Studio Profiler
3. Chọn process: com.example.opengl_es
4. Click tab "CPU"
```

#### Bước 2: Record khi TẮT Back-face Culling
```
1. Trong app: Mở bottom sheet → Tab "Controls"
2. Đảm bảo "Back-face Culling" TẮT (☐)
3. Trong Profiler: Click nút "Record" (●) để bắt đầu record
4. Đợi 10 giây (để có đủ dữ liệu)
5. Click "Stop" để dừng record
6. Ghi lại:
   ┌──────────────────────┬──────────┐
   │ Chỉ số              │ Giá trị  │
   ├──────────────────────┼──────────┤
   │ CPU Usage (avg)      │ _____%   │
   │ CPU Usage (max)      │ _____%   │
   │ Thread Activity      │ _____    │
   └──────────────────────┴──────────┘
```

#### Bước 3: Record khi BẬT Back-face Culling
```
1. Trong app: Bật "Back-face Culling" (☑)
2. Đợi 3 giây để app áp dụng thay đổi
3. Trong Profiler: Click "Record" lại
4. Đợi 10 giây
5. Click "Stop"
6. Ghi lại:
   ┌──────────────────────┬──────────┐
   │ Chỉ số              │ Giá trị │
   ├──────────────────────┼──────────┤
   │ CPU Usage (avg)      │ _____%   │
   │ CPU Usage (max)      │ _____%   │
   │ Thread Activity      │ _____    │
   └──────────────────────┴──────────┘
```

#### Bước 4: Phân tích Method Calls
```
1. Trong Profiler, chọn "Call Chart" view
2. Tìm method: MyGLRenderer.onDrawFrame()
3. Click vào method để xem chi tiết
4. Ghi lại:
   ┌──────────────────────┬──────────┬──────────┐
   │ Method               │ TRƯỚC    │ SAU      │
   ├──────────────────────┼──────────┼──────────┤
   │ onDrawFrame() (avg)  │ _____ ms │ _____ ms │
   │ onDrawFrame() (max)  │ _____ ms │ _____ ms │
   │ onDrawFrame() (min)  │ _____ ms │ _____ ms │
   └──────────────────────┴──────────┴──────────┘
```

#### Bước 5: So sánh và Phân tích
```
1. So sánh CPU Usage:
   ✅ CPU Usage GIẢM khi bật Back-face Culling
   ✅ Giảm khoảng: 10-20%
   
2. So sánh onDrawFrame() time:
   ✅ Thời gian thực thi GIẢM khi bật
   ✅ Giảm khoảng: 20-30%
   
3. So sánh Thread Activity:
   ✅ Thread ổn định hơn khi bật
   ✅ Ít spikes, ít jank
```

#### Chỉ số mong đợi:
- **CPU Usage**: Giảm 10-20% khi bật
- **onDrawFrame() time**: Giảm 20-30% khi bật
- **Thread Activity**: Ổn định hơn (ít spikes)

---

### 3.2. FRUSTUM CULLING

#### Mục đích:
Đo lường sự khác biệt CPU usage và thời gian culling khi bật/tắt Frustum Culling.

#### Bước 1: Chuẩn bị
```
1. Mở app, đợi ổn định 5 giây
2. Mở Android Studio Profiler
3. Chọn process: com.example.opengl_es
4. Click tab "CPU"
```

#### Bước 2: Record khi TẮT Frustum Culling
```
1. Trong app: Đảm bảo "Frustum Culling" TẮT (☐)
2. Trong Profiler: Click "Record" (●)
3. Đợi 10 giây
4. Click "Stop"
5. Ghi lại:
   ┌──────────────────────┬──────────┐
   │ Chỉ số              │ Giá trị  │
   ├──────────────────────┼──────────┤
   │ CPU Usage (avg)      │ _____%   │
   │ onDrawFrame() (avg)  │ _____ ms │
   └──────────────────────┴──────────┘
```

#### Bước 3: Record khi BẬT Frustum Culling
```
1. Trong app: Bật "Frustum Culling" (☑)
2. Đợi 3 giây
3. Trong Profiler: Record lại 10 giây
4. Ghi lại:
   ┌──────────────────────┬──────────┐
   │ Chỉ số              │ Giá trị  │
   ├──────────────────────┼──────────┤
   │ CPU Usage (avg)      │ _____%   │
   │ onDrawFrame() (avg)  │ _____ ms │
   └──────────────────────┴──────────┘
```

#### Bước 4: Phân tích Method Calls
```
1. Trong Profiler, chọn "Call Chart" view
2. Tìm các method:
   - CullingManager.performFrustumCulling()
   - MyGLRenderer.onDrawFrame()
3. Ghi lại:
   ┌──────────────────────────────┬──────────┬──────────┐
   │ Method                       │ TRƯỚC    │ SAU      │
   ├──────────────────────────────┼──────────┼──────────┤
   │ performFrustumCulling()      │ _____ ms │ _____ ms │
   │ onDrawFrame() (avg)          │ _____ ms │ _____ ms │
   └──────────────────────────────┴──────────┴──────────┘
```

#### Bước 5: So sánh và Phân tích
```
1. So sánh CPU Usage:
   ✅ CPU Usage GIẢM khi bật Frustum Culling
   ✅ Giảm khoảng: 5-10%
   
2. So sánh onDrawFrame() time:
   ✅ Thời gian thực thi GIẢM khi bật
   ✅ Giảm khoảng: 10-15%
   
3. Cost của culling:
   ✅ performFrustumCulling() time: ~0.5-2ms
   ✅ Cost này NHỎ HƠN lợi ích (render ít objects hơn)
```

#### Chỉ số mong đợi:
- **CPU Usage**: Giảm 5-10% khi bật
- **onDrawFrame() time**: Giảm 10-15% khi bật
- **performFrustumCulling() time**: ~0.5-2ms (cost của culling)

---

### 3.3. OCCLUSION CULLING

#### Mục đích:
Đo lường sự khác biệt CPU usage và cost của occlusion culling.

#### Bước 1: Chuẩn bị
```
1. Mở app, đợi ổn định 5 giây
2. Mở Android Studio Profiler
3. Chọn process: com.example.opengl_es
4. Click tab "CPU"
```

#### Bước 2: Record khi TẮT Occlusion Culling
```
1. Trong app: Đảm bảo "Occlusion Culling" TẮT (☐)
2. Trong Profiler: Click "Record" (●)
3. Đợi 10 giây
4. Click "Stop"
5. Ghi lại:
   ┌──────────────────────┬──────────┐
   │ Chỉ số              │ Giá trị  │
   ├──────────────────────┼──────────┤
   │ CPU Usage (avg)      │ _____%   │
   │ onDrawFrame() (avg)  │ _____ ms │
   └──────────────────────┴──────────┘
```

#### Bước 3: Record khi BẬT Occlusion Culling
```
1. Trong app: Bật "Occlusion Culling" (☑)
2. Đợi 3 giây
3. Trong Profiler: Record lại 10 giây
4. Ghi lại:
   ┌──────────────────────┬──────────┐
   │ Chỉ số              │ Giá trị  │
   ├──────────────────────┼──────────┤
   │ CPU Usage (avg)      │ _____%   │
   │ onDrawFrame() (avg)  │ _____ ms │
   └──────────────────────┴──────────┘
```

#### Bước 4: Phân tích Method Calls
```
1. Trong Profiler, chọn "Call Chart" view
2. Tìm method: OcclusionCulling.performOcclusionCulling()
3. Ghi lại:
   ┌──────────────────────────────┬──────────┬──────────┐
   │ Method                       │ TRƯỚC    │ SAU      │
   ├──────────────────────────────┼──────────┼──────────┤
   │ performOcclusionCulling()    │ _____ ms │ _____ ms │
   │ onDrawFrame() (avg)          │ _____ ms │ _____ ms │
   └──────────────────────────────┴──────────┴──────────┘
```

#### Bước 5: So sánh và Phân tích
```
1. So sánh CPU Usage:
   ⚠️ CPU Usage có thể TĂNG nhẹ (do tính toán culling)
   ✅ Nhưng overall performance TỐT HƠN (do render ít objects)
   
2. So sánh onDrawFrame() time:
   ✅ Thời gian thực thi GIẢM khi bật
   ✅ Giảm khoảng: 5-10%
   
3. Cost của culling:
   ✅ performOcclusionCulling() time: ~1-3ms
   ✅ Cost này có thể lớn hơn frustum culling
```

#### Chỉ số mong đợi:
- **CPU Usage**: Có thể tăng 2-5% (do tính toán) nhưng overall tốt hơn
- **onDrawFrame() time**: Giảm 5-10% khi bật
- **performOcclusionCulling() time**: ~1-3ms (cost của culling)

---

### 3.4. LEVEL OF DETAIL (LOD)

#### Mục đích:
Đo lường sự khác biệt CPU usage và thời gian tính LOD.

#### Bước 1: Chuẩn bị
```
1. Mở app, đợi ổn định 5 giây
2. Mở Android Studio Profiler
3. Chọn process: com.example.opengl_es
4. Click tab "CPU"
```

#### Bước 2: Record khi BẬT LOD
```
1. Trong app: Đảm bảo "Level of Detail (LOD)" BẬT (☑)
2. Trong Profiler: Click "Record" (●)
3. Đợi 10 giây
4. Click "Stop"
5. Ghi lại:
   ┌──────────────────────┬──────────┐
   │ Chỉ số              │ Giá trị  │
   ├──────────────────────┼──────────┤
   │ CPU Usage (avg)      │ _____%   │
   │ onDrawFrame() (avg)  │ _____ ms │
   └──────────────────────┴──────────┘
```

#### Bước 3: Record khi TẮT LOD
```
1. Trong app: Tắt "Level of Detail (LOD)" (☐)
2. Đợi 3 giây
3. Trong Profiler: Record lại 10 giây
4. Ghi lại:
   ┌──────────────────────┬──────────┐
   │ Chỉ số              │ Giá trị  │
   ├──────────────────────┼──────────┤
   │ CPU Usage (avg)      │ _____%   │
   │ onDrawFrame() (avg)  │ _____ ms │
   └──────────────────────┴──────────┘
```

#### Bước 4: Phân tích Method Calls
```
1. Trong Profiler, chọn "Call Chart" view
2. Tìm method: LODManager.calculateLOD()
3. Ghi lại:
   ┌──────────────────────┬──────────┬──────────┐
   │ Method               │ TRƯỚC    │ SAU      │
   ├──────────────────────┼──────────┼──────────┤
   │ calculateLOD() (avg)  │ _____ ms │ _____ ms │
   │ onDrawFrame() (avg)  │ _____ ms │ _____ ms │
   └──────────────────────┴──────────┴──────────┘
```

#### Bước 5: So sánh và Phân tích
```
1. So sánh CPU Usage:
   ✅ CPU Usage GIẢM khi bật LOD (do render ít triangles)
   ✅ Giảm khoảng: 5-10%
   
2. So sánh onDrawFrame() time:
   ✅ Thời gian thực thi GIẢM khi bật
   ✅ Giảm khoảng: 10-15%
   
3. Cost của LOD:
   ✅ calculateLOD() time: ~0.1-0.5ms (rất nhỏ)
   ✅ Lợi ích lớn hơn cost rất nhiều
```

#### Chỉ số mong đợi:
- **CPU Usage**: Giảm 5-10% khi bật LOD
- **onDrawFrame() time**: Giảm 10-15% khi bật
- **calculateLOD() time**: ~0.1-0.5ms (cost rất nhỏ)

---

### 3.5. SHADER OPTIMIZATION

#### Mục đích:
So sánh performance giữa Simple Shader và Complex Shader.

#### Bước 1: Chuẩn bị
```
1. Mở app, đợi ổn định 5 giây
2. Mở Android Studio Profiler
3. Chọn process: com.example.opengl_es
4. Click tab "CPU"
```

#### Bước 2: Record với Simple Shader
```
1. Trong app: Đảm bảo dùng Simple Shader
   (Không bật "Instanced Rendering" → dùng simple shader)
2. Trong Profiler: Click "Record" (●)
3. Đợi 10 giây
4. Click "Stop"
5. Ghi lại:
   ┌──────────────────────┬──────────┐
   │ Chỉ số              │ Giá trị  │
   ├──────────────────────┼──────────┤
   │ CPU Usage (avg)      │ _____%   │
   │ onDrawFrame() (avg)  │ _____ ms │
   └──────────────────────┴──────────┘
```

#### Bước 3: Record với Complex Shader
```
1. Trong app: Bật "Instanced Rendering" (☑)
   (Khi bật Instanced Rendering → dùng complex shader)
2. Đợi 3 giây
3. Trong Profiler: Record lại 10 giây
4. Ghi lại:
   ┌──────────────────────┬──────────┐
   │ Chỉ số              │ Giá trị  │
   ├──────────────────────┼──────────┤
   │ CPU Usage (avg)      │ _____%   │
   │ onDrawFrame() (avg)  │ _____ ms │
   └──────────────────────┴──────────┘
```

#### Bước 4: Phân tích GPU Performance
```
1. Trong Profiler, chọn "GPU" tab (nếu có)
2. Hoặc xem CPU usage của shader execution
3. Ghi lại:
   ┌──────────────────────┬──────────┬──────────┐
   │ Metric               │ Simple   │ Complex  │
   ├──────────────────────┼──────────┼──────────┤
   │ CPU Usage (avg)      │ _____%   │ _____%   │
   │ onDrawFrame() (avg)  │ _____ ms │ _____ ms │
   │ GPU Usage (nếu có)   │ _____%   │ _____%   │
   └──────────────────────┴──────────┴──────────┘
```

#### Bước 5: So sánh và Phân tích
```
1. So sánh CPU Usage:
   ✅ CPU Usage TĂNG khi dùng Complex Shader
   ✅ Tăng khoảng: 20-30%
   
2. So sánh onDrawFrame() time:
   ✅ Thời gian thực thi TĂNG khi dùng Complex Shader
   ✅ Tăng khoảng: 30-50%
   
3. Lý do:
   - Complex Shader có nhiều phép tính hơn
   - GPU phải xử lý nhiều instructions hơn
```

#### Chỉ số mong đợi:
- **CPU Usage**: Tăng 20-30% khi dùng Complex Shader
- **onDrawFrame() time**: Tăng 30-50% khi dùng Complex Shader
- **GPU Usage**: Tăng đáng kể (nếu có GPU profiler)

---

### 3.6. ETC1 TEXTURE COMPRESSION

#### Mục đích:
Đo lường sự khác biệt Memory usage khi bật/tắt ETC1 Compression.

#### Bước 1: Chuẩn bị
```
1. Mở app, đợi ổn định 5 giây
2. Mở Android Studio Profiler
3. Chọn process: com.example.opengl_es
4. Click tab "Memory"
```

#### Bước 2: Record khi TẮT ETC1
```
1. Trong app: Đảm bảo "ETC1 Texture Compression" TẮT (☐)
2. Trong Profiler: Click "Record" (●)
3. Đợi 10 giây
4. Click "Stop"
5. Ghi lại:
   ┌──────────────────────┬──────────┐
   │ Chỉ số              │ Giá trị  │
   ├──────────────────────┼──────────┤
   │ Memory Usage (avg)   │ _____ MB │
   │ Memory Usage (max)   │ _____ MB │
   └──────────────────────┴──────────┘
```

#### Bước 3: Record khi BẬT ETC1
```
1. Trong app: Bật "ETC1 Texture Compression" (☑)
2. Đợi 3 giây (có thể lâu hơn nếu reload texture)
3. Trong Profiler: Record lại 10 giây
4. Ghi lại:
   ┌──────────────────────┬──────────┐
   │ Chỉ số              │ Giá trị  │
   ├──────────────────────┼──────────┤
   │ Memory Usage (avg)   │ _____ MB │
   │ Memory Usage (max)   │ _____ MB │
   └──────────────────────┴──────────┘
```

#### Bước 4: Phân tích Memory
```
1. Trong Profiler, chọn "Memory" tab
2. Xem "Allocations" để thấy texture allocations
3. Ghi lại:
   ┌──────────────────────┬──────────┬──────────┐
   │ Metric               │ TRƯỚC    │ SAU      │
   ├──────────────────────┼──────────┼──────────┤
   │ Memory Usage (avg)   │ _____ MB │ _____ MB │
   │ Texture Allocations  │ _____    │ _____    │
   └──────────────────────┴──────────┴──────────┘
```

#### Bước 5: So sánh và Phân tích
```
1. So sánh Memory Usage:
   ✅ Memory Usage GIẢM khi bật ETC1
   ✅ Giảm khoảng: 80-90% (texture memory)
   
2. So sánh CPU Usage (nếu có):
   ✅ CPU Usage có thể GIẢM nhẹ (do ít memory bandwidth)
```

#### Chỉ số mong đợi:
- **Memory Usage**: Giảm 80-90% (texture memory) khi bật ETC1
- **CPU Usage**: Giảm nhẹ 2-5% (do giảm memory bandwidth)

---

### 3.7. MIPMAPS

#### Mục đích:
Đo lường sự khác biệt Memory usage và CPU usage khi bật/tắt Mipmaps.

#### Bước 1: Chuẩn bị
```
1. Mở app, đợi ổn định 5 giây
2. Mở Android Studio Profiler
3. Chọn process: com.example.opengl_es
4. Click tab "Memory"
```

#### Bước 2: Record khi TẮT Mipmaps
```
1. Trong app: Đảm bảo "Mipmaps" TẮT (☐)
2. Trong Profiler: Click "Record" (●)
3. Đợi 10 giây
4. Click "Stop"
5. Ghi lại:
   ┌──────────────────────┬──────────┐
   │ Chỉ số              │ Giá trị  │
   ├──────────────────────┼──────────┤
   │ Memory Usage (avg)   │ _____ MB │
   │ CPU Usage (avg)      │ _____%   │
   └──────────────────────┴──────────┘
```

#### Bước 3: Record khi BẬT Mipmaps
```
1. Trong app: Bật "Mipmaps" (☑)
2. Đợi 3 giây
3. Trong Profiler: Record lại 10 giây
4. Ghi lại:
   ┌──────────────────────┬──────────┐
   │ Chỉ số              │ Giá trị  │
   ├──────────────────────┼──────────┤
   │ Memory Usage (avg)   │ _____ MB │
   │ CPU Usage (avg)      │ _____%   │
   └──────────────────────┴──────────┘
```

#### Bước 4: So sánh và Phân tích
```
1. So sánh Memory Usage:
   ⚠️ Memory Usage TĂNG khi bật Mipmaps
   ⚠️ Tăng khoảng: 30-35% (do tạo nhiều mipmap levels)
   
2. So sánh CPU Usage:
   ✅ CPU Usage có thể GIẢM nhẹ (do cache efficiency)
   ✅ Giảm khoảng: 2-5%
```

#### Chỉ số mong đợi:
- **Memory Usage**: Tăng 30-35% khi bật Mipmaps
- **CPU Usage**: Giảm nhẹ 2-5% (do cache efficiency)

---

### 3.8. TEXTURE ATLASING

#### Mục đích:
Đo lường sự khác biệt CPU usage và texture bind calls khi bật/tắt Texture Atlasing.

#### Bước 1: Chuẩn bị
```
1. Mở app, đợi ổn định 5 giây
2. Mở Android Studio Profiler
3. Chọn process: com.example.opengl_es
4. Click tab "CPU"
```

#### Bước 2: Record khi TẮT Texture Atlasing
```
1. Trong app: Đảm bảo "Texture Atlasing" TẮT (☐)
2. Trong Profiler: Click "Record" (●)
3. Đợi 10 giây
4. Click "Stop"
5. Ghi lại:
   ┌──────────────────────┬──────────┐
   │ Chỉ số              │ Giá trị  │
   ├──────────────────────┼──────────┤
   │ CPU Usage (avg)      │ _____%   │
   │ onDrawFrame() (avg)  │ _____ ms │
   └──────────────────────┴──────────┘
```

#### Bước 3: Record khi BẬT Texture Atlasing
```
1. Trong app: Bật "Texture Atlasing" (☑)
2. Đợi 3 giây
3. Trong Profiler: Record lại 10 giây
4. Ghi lại:
   ┌──────────────────────┬──────────┐
   │ Chỉ số              │ Giá trị  │
   ├──────────────────────┼──────────┤
   │ CPU Usage (avg)      │ _____%   │
   │ onDrawFrame() (avg)  │ _____ ms │
   └──────────────────────┴──────────┘
```

#### Bước 4: Phân tích Method Calls
```
1. Trong Profiler, chọn "Call Chart" view
2. Tìm method: GLES30.glBindTexture()
3. Đếm số lần gọi:
   ┌──────────────────────┬──────────┬──────────┐
   │ Method               │ TRƯỚC    │ SAU      │
   ├──────────────────────┼──────────┼──────────┤
   │ glBindTexture() calls│ _____    │ _____    │
   │ onDrawFrame() (avg)  │ _____ ms │ _____ ms │
   └──────────────────────┴──────────┴──────────┘
```

#### Bước 5: So sánh và Phân tích
```
1. So sánh CPU Usage:
   ✅ CPU Usage GIẢM khi bật Texture Atlasing
   ✅ Giảm khoảng: 5-10%
   
2. So sánh glBindTexture() calls:
   ✅ Số lần gọi GIẢM đáng kể khi bật
   ✅ Giảm từ N lần xuống 1 lần
   
3. So sánh onDrawFrame() time:
   ✅ Thời gian thực thi GIẢM khi bật
   ✅ Giảm khoảng: 5-10%
```

#### Chỉ số mong đợi:
- **CPU Usage**: Giảm 5-10% khi bật Texture Atlasing
- **glBindTexture() calls**: Giảm từ N xuống 1 khi bật
- **onDrawFrame() time**: Giảm 5-10% khi bật

---

### 3.9. INSTANCED RENDERING

#### Mục đích:
Đo lường sự khác biệt CPU usage và draw calls khi bật/tắt Instanced Rendering.

#### Bước 1: Chuẩn bị
```
1. Mở app, đợi ổn định 5 giây
2. Mở Android Studio Profiler
3. Chọn process: com.example.opengl_es
4. Click tab "CPU"
```

#### Bước 2: Record khi TẮT Instanced Rendering
```
1. Trong app: Đảm bảo "Instanced Rendering" TẮT (☐)
2. Trong Profiler: Click "Record" (●)
3. Đợi 10 giây
4. Click "Stop"
5. Ghi lại:
   ┌──────────────────────┬──────────┐
   │ Chỉ số              │ Giá trị  │
   ├──────────────────────┼──────────┤
   │ CPU Usage (avg)      │ _____%   │
   │ onDrawFrame() (avg)  │ _____ ms │
   └──────────────────────┴──────────┘
```

#### Bước 3: Record khi BẬT Instanced Rendering
```
1. Trong app: Bật "Instanced Rendering" (☑)
2. Đợi 3 giây
3. Trong Profiler: Record lại 10 giây
4. Ghi lại:
   ┌──────────────────────┬──────────┐
   │ Chỉ số              │ Giá trị  │
   ├──────────────────────┼──────────┤
   │ CPU Usage (avg)      │ _____%   │
   │ onDrawFrame() (avg)  │ _____ ms │
   └──────────────────────┴──────────┘
```

#### Bước 4: Phân tích Method Calls
```
1. Trong Profiler, chọn "Call Chart" view
2. Tìm method: GLES30.glDrawArraysInstanced()
   hoặc GLES30.glDrawElementsInstanced()
3. Đếm số lần gọi:
   ┌──────────────────────┬──────────┬──────────┐
   │ Method               │ TRƯỚC    │ SAU      │
   ├──────────────────────┼──────────┼──────────┤
   │ Draw calls (N objects)│ _____    │ _____    │
   │ Instanced draw calls  │ _____    │ 1        │
   │ onDrawFrame() (avg)  │ _____ ms │ _____ ms │
   └──────────────────────┴──────────┴──────────┘
```

#### Bước 5: So sánh và Phân tích
```
1. So sánh CPU Usage:
   ✅ CPU Usage GIẢM đáng kể khi bật Instanced Rendering
   ✅ Giảm khoảng: 20-30%
   
2. So sánh Draw Calls:
   ✅ Draw calls GIẢM từ N xuống 1 khi bật
   ✅ Giảm CPU overhead đáng kể
   
3. So sánh onDrawFrame() time:
   ✅ Thời gian thực thi GIẢM khi bật
   ✅ Giảm khoảng: 20-30%
```

#### Chỉ số mong đợi:
- **CPU Usage**: Giảm 20-30% khi bật Instanced Rendering
- **Draw Calls**: Giảm từ N xuống 1 khi bật
- **onDrawFrame() time**: Giảm 20-30% khi bật

---

### 3.10. DEPTH PRE-PASS

#### Mục đích:
Đo lường sự khác biệt CPU usage và overdraw khi bật/tắt Depth Pre-pass.

#### Bước 1: Chuẩn bị
```
1. Mở app, đợi ổn định 5 giây
2. Mở Android Studio Profiler
3. Chọn process: com.example.opengl_es
4. Click tab "CPU"
```

#### Bước 2: Record khi TẮT Depth Pre-pass
```
1. Trong app: Đảm bảo "Depth Pre-Pass" TẮT (☐)
2. Trong Profiler: Click "Record" (●)
3. Đợi 10 giây
4. Click "Stop"
5. Ghi lại:
   ┌──────────────────────┬──────────┐
   │ Chỉ số              │ Giá trị  │
   ├──────────────────────┼──────────┤
   │ CPU Usage (avg)      │ _____%   │
   │ onDrawFrame() (avg)  │ _____ ms │
   └──────────────────────┴──────────┘
```

#### Bước 3: Record khi BẬT Depth Pre-pass
```
1. Trong app: Bật "Depth Pre-Pass" (☑)
2. Đợi 3 giây
3. Trong Profiler: Record lại 10 giây
4. Ghi lại:
   ┌──────────────────────┬──────────┐
   │ Chỉ số              │ Giá trị  │
   ├──────────────────────┼──────────┤
   │ CPU Usage (avg)      │ _____%   │
   │ onDrawFrame() (avg)  │ _____ ms │
   └──────────────────────┴──────────┘
```

#### Bước 4: Phân tích Method Calls
```
1. Trong Profiler, chọn "Call Chart" view
2. Tìm các method:
   - GLES30.glDrawArrays() (depth pass)
   - GLES30.glDrawArrays() (color pass)
3. Đếm số lần gọi:
   ┌──────────────────────┬──────────┬──────────┐
   │ Method               │ TRƯỚC    │ SAU      │
   ├──────────────────────┼──────────┼──────────┤
   │ Draw calls (1 pass)  │ _____    │ _____    │
   │ Draw calls (2 passes)│ _____    │ _____    │
   │ onDrawFrame() (avg)  │ _____ ms │ _____ ms │
   └──────────────────────┴──────────┴──────────┘
```

#### Bước 5: So sánh và Phân tích
```
1. So sánh CPU Usage:
   ⚠️ CPU Usage có thể TĂNG nhẹ (do render 2 passes)
   ✅ Nhưng GPU fill rate GIẢM (do giảm overdraw)
   
2. So sánh Draw Calls:
   ⚠️ Draw Calls TĂNG (do render 2 lần)
   ✅ Nhưng pixels rendered GIẢM (do depth test)
   
3. So sánh onDrawFrame() time:
   ✅ Thời gian thực thi có thể GIẢM (do giảm overdraw)
   ✅ Giảm khoảng: 5-10%
```

#### Chỉ số mong đợi:
- **CPU Usage**: Có thể tăng 5-10% (do 2 passes) nhưng overall tốt hơn
- **Draw Calls**: Tăng gấp đôi (do 2 passes)
- **onDrawFrame() time**: Giảm 5-10% (do giảm overdraw)

---

### 3.11. OVERDRAW HEATMAP

#### Mục đích:
Đo lường sự khác biệt CPU usage và GPU fill rate khi bật/tắt Overdraw Heatmap.

#### Bước 1: Chuẩn bị
```
1. Mở app, đợi ổn định 5 giây
2. Mở Android Studio Profiler
3. Chọn process: com.example.opengl_es
4. Click tab "CPU"
```

#### Bước 2: Record khi TẮT Overdraw Heatmap
```
1. Trong app: Đảm bảo "Show Overdraw Heatmap" TẮT (☐)
2. Trong Profiler: Click "Record" (●)
3. Đợi 10 giây
4. Click "Stop"
5. Ghi lại:
   ┌──────────────────────┬──────────┐
   │ Chỉ số              │ Giá trị  │
   ├──────────────────────┼──────────┤
   │ CPU Usage (avg)      │ _____%   │
   │ onDrawFrame() (avg)  │ _____ ms │
   └──────────────────────┴──────────┘
```

#### Bước 3: Record khi BẬT Overdraw Heatmap
```
1. Trong app: Bật "Show Overdraw Heatmap" (☑)
2. Đợi 3 giây
3. Trong Profiler: Record lại 10 giây
4. Ghi lại:
   ┌──────────────────────┬──────────┐
   │ Chỉ số              │ Giá trị  │
   ├──────────────────────┼──────────┤
   │ CPU Usage (avg)      │ _____%   │
   │ onDrawFrame() (avg)  │ _____ ms │
   └──────────────────────┴──────────┘
```

#### Bước 4: So sánh và Phân tích
```
1. So sánh CPU Usage:
   ⚠️ CPU Usage TĂNG khi bật Overdraw Heatmap
   ⚠️ Tăng khoảng: 20-30% (do render nhiều lần)
   
2. So sánh onDrawFrame() time:
   ⚠️ Thời gian thực thi TĂNG khi bật
   ⚠️ Tăng khoảng: 30-50% (do render 4 lần)
   
3. Lý do:
   - Heatmap render objects 4 lần để tăng overdraw
   - Disable depth test → render tất cả pixels
   - Additive blending → tốn GPU hơn
```

#### Chỉ số mong đợi:
- **CPU Usage**: Tăng 20-30% khi bật Overdraw Heatmap
- **onDrawFrame() time**: Tăng 30-50% khi bật (do render 4 lần)

---

## 📊 TỔNG KẾT

### Bảng so sánh Performance trong Profiler:

| Tính năng | CPU Usage | onDrawFrame() Time | Memory Usage | Draw Calls |
|-----------|-----------|-------------------|--------------|------------|
| **Back-face Culling** | ↓ 10-20% | ↓ 20-30% | - | - |
| **Frustum Culling** | ↓ 5-10% | ↓ 10-15% | - | ↓ 50% |
| **Occlusion Culling** | ↑ 2-5%* | ↓ 5-10% | - | ↓ 30% |
| **LOD** | ↓ 5-10% | ↓ 10-15% | - | - |
| **Simple Shader** | ↓ 20-30% | ↓ 30-50% | - | - |
| **ETC1 Compression** | ↓ 2-5% | - | ↓ 80-90% | - |
| **Mipmaps** | ↓ 2-5% | - | ↑ 30-35% | - |
| **Texture Atlasing** | ↓ 5-10% | ↓ 5-10% | - | - |
| **Instanced Rendering** | ↓ 20-30% | ↓ 20-30% | - | ↓ 90% |
| **Depth Pre-pass** | ↑ 5-10%* | ↓ 5-10% | - | ↑ 100% |
| **Overdraw Heatmap** | ↑ 20-30% | ↑ 30-50% | - | - |

*: Tăng nhưng overall performance tốt hơn

---

## ✅ LƯU Ý KHI SỬ DỤNG PROFILER

1. **Record đủ thời gian**: Đợi ít nhất 10 giây để có đủ dữ liệu
2. **So sánh cùng điều kiện**: Đảm bảo scene và camera giống nhau
3. **Xem nhiều metrics**: Không chỉ CPU, còn Memory, GPU (nếu có)
4. **Phân tích method calls**: Xem method nào tốn thời gian nhất
5. **Kiểm tra allocations**: Tránh tạo objects trong render loop

---

**📝 File này cung cấp hướng dẫn chi tiết cách sử dụng Android Studio Profiler để đo lường và so sánh performance cho từng tính năng tối ưu hóa.**

