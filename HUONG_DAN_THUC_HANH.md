# 📱 HƯỚNG DẪN THỰC HÀNH OPENGL ES 3.0 OPTIMIZATION BENCHMARK

## 🎯 MỤC TIÊU
Học cách tối ưu hóa đồ họa OpenGL ES 3.0 trên Android thông qua:
- Toggle các kỹ thuật tối ưu hóa
- Quan sát performance metrics real-time
- Chạy benchmark tests
- Phân tích kết quả bằng charts và CSV

---

## 📋 BƯỚC 1: CHUẨN BỊ MÔI TRƯỜNG

### 1.1. Kiểm tra Android Studio
- ✅ Mở Android Studio
- ✅ Đảm bảo đã cài đặt:
  - Android SDK (API 24+)
  - Build Tools
  - Android Emulator hoặc thiết bị thật

### 1.2. Sync Project
```
1. Mở project trong Android Studio
2. Click File → Sync Project with Gradle Files
3. Đợi sync hoàn tất (có thể mất 2-3 phút lần đầu)
4. Kiểm tra không có lỗi đỏ trong Build tab
```

### 1.3. Kiểm tra Dependencies
- Mở `app/build.gradle.kts`
- Đảm bảo có dòng: `implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")`
- Nếu thiếu, sync lại project

---

## 📋 BƯỚC 2: BUILD VÀ CHẠY APP

### 2.1. Build Project
```
1. Click Build → Rebuild Project
2. Đợi build hoàn tất (có thể mất 1-2 phút)
3. Kiểm tra Build tab không có lỗi
```

### 2.2. Chạy App
```
1. Kết nối thiết bị Android hoặc khởi động Emulator
2. Click Run → Run 'app' (hoặc nhấn Shift+F10)
3. Đợi app cài đặt và khởi động
```

### 2.3. Kiểm tra Màn hình
Sau khi app chạy, bạn sẽ thấy:
- ✅ **Background màu xanh đậm** (OpenGL clear color)
- ✅ **Các cubes với checkerboard pattern** (scene 3D)
- ✅ **HUD overlay góc trên trái** hiển thị:
  - FPS: ~60-100 (tùy thiết bị)
  - Frame: ~10-16 ms
  - Draws: ~64 (số draw calls)
- ✅ **FAB button** (nút tròn) ở góc dưới bên phải

---

## 📋 BƯỚC 3: SỬ DỤNG CONTROL PANEL

### 3.1. Mở Bottom Sheet
```
1. Nhấn vào FAB button (nút tròn góc dưới phải)
2. Bottom sheet sẽ trượt lên từ dưới
3. Bạn sẽ thấy 3 tabs: "Controls", "Metrics", "Charts"
```

### 3.2. Tab "Controls" - Toggle Optimizations

#### A. Texture Optimizations
- **ETC1 Texture Compression**: Nén texture để giảm memory
  - Bật: Giảm memory, có thể giảm chất lượng nhẹ
  - Tắt: Texture chất lượng cao, tốn memory hơn
  
- **Mipmaps**: Tạo các phiên bản nhỏ hơn của texture
  - Bật: Tăng performance khi texture ở xa
  - Tắt: Tất cả mức độ đều dùng texture gốc
  
- **Texture Atlasing**: Gộp nhiều texture vào 1 texture lớn
  - Bật: Giảm texture switches, tăng performance
  - Tắt: Mỗi object dùng texture riêng

#### B. Culling Techniques
- **Back-face Culling**: Không render mặt sau của objects
  - Bật: Giảm ~50% triangles, tăng FPS
  - Tắt: Render tất cả faces (tốn performance)
  
- **Frustum Culling**: Chỉ render objects trong tầm nhìn
  - Bật: Bỏ qua objects ngoài camera view
  - Tắt: Render tất cả objects (kể cả không thấy)
  
- **Occlusion Culling**: Không render objects bị che khuất
  - Bật: Bỏ qua objects bị che bởi objects khác
  - Tắt: Render tất cả (kể cả bị che)

#### C. Other Optimizations
- **Level of Detail (LOD)**: Giảm độ chi tiết khi object ở xa
  - Bật: Objects xa dùng mesh đơn giản hơn
  - Tắt: Tất cả objects dùng mesh chi tiết
  
- **Instanced Rendering**: Render nhiều objects cùng lúc
  - Bật: Tăng throughput khi có nhiều objects giống nhau
  - Tắt: Render từng object một
  
- **Depth Pre-Pass**: Render depth trước, sau đó mới render color
  - Bật: Giảm overdraw, tăng performance
  - Tắt: Render trực tiếp color
  
- **Show Overdraw Heatmap**: Hiển thị màu sắc theo mức độ overdraw
  - Bật: Debug tool để tìm vùng overdraw cao
  - Tắt: Render bình thường

### 3.3. Thực hành Toggle
```
1. Mở tab "Controls"
2. Tắt "Back-face Culling" → Quan sát FPS giảm
3. Bật lại → FPS tăng lên
4. Tắt "Frustum Culling" → Quan sát Draw Calls tăng
5. Bật lại → Draw Calls giảm
6. Thử các toggle khác và quan sát sự thay đổi
```

---

## 📋 BƯỚC 4: XEM METRICS (Tab "Metrics")

### 4.1. Chuyển sang Tab "Metrics"
```
1. Trong bottom sheet, click tab "Metrics"
2. Bạn sẽ thấy các metrics chi tiết
```

### 4.2. Các Metrics Hiển thị

#### Frame Timing
- **Avg Frame Time**: Thời gian trung bình render 1 frame (ms)
  - < 16.67ms = > 60 FPS (tốt)
  - 16.67-33ms = 30-60 FPS (chấp nhận được)
  - > 33ms = < 30 FPS (cần tối ưu)
  
- **Frame Variance**: Độ biến thiên của frame time
  - Thấp = ổn định
  - Cao = có jank (lag)
  
- **Jank Count**: Số frame bị miss (vượt quá 16.67ms)
  - Càng thấp càng tốt

#### Rendering Metrics
- **Triangles**: Số triangles đang render mỗi frame
- **Texture Binds**: Số lần bind texture
- **Shader Switches**: Số lần switch shader program
- **Overdraw Ratio**: Tỷ lệ overdraw (1.0 = không overdraw, >1.0 = có overdraw)

#### Culling Stats
- **Objects Rendered**: Số objects đang được render
- **Objects Culled**: Số objects bị cull (bỏ qua)

### 4.3. Quan sát Metrics
```
1. Mở tab "Metrics"
2. Quan sát các giá trị real-time
3. Toggle các optimizations trong tab "Controls"
4. Quay lại tab "Metrics" để xem sự thay đổi
```

---

## 📋 BƯỚC 5: XEM CHARTS (Tab "Charts")

### 5.1. Chuyển sang Tab "Charts"
```
1. Trong bottom sheet, click tab "Charts"
2. Bạn sẽ thấy 2 charts:
   - FPS Over Time (line chart)
   - Performance Comparison (bar chart)
```

### 5.2. FPS Over Time Chart
- **Mục đích**: Theo dõi FPS theo thời gian
- **Cách đọc**:
  - Trục X: Thời gian (số frame)
  - Trục Y: FPS (0-120)
  - Đường xanh: FPS hiện tại
- **Tương tác**:
  - Pinch to zoom: Phóng to/thu nhỏ
  - Drag: Kéo để xem các điểm
  - Double tap: Reset zoom

### 5.3. Performance Comparison Chart
- **Mục đích**: So sánh các metrics
- **Các cột**:
  - FPS: FPS trung bình
  - Draw Calls: Số draw calls trung bình
  - Triangles/100: Triangles trung bình (chia 100 để dễ nhìn)

### 5.4. Thực hành với Charts
```
1. Mở tab "Charts"
2. Quan sát FPS chart cập nhật real-time
3. Toggle "Back-face Culling" OFF
4. Quan sát FPS chart giảm xuống
5. Toggle ON lại → FPS tăng lên
6. So sánh sự khác biệt trong comparison chart
```

---

## 📋 BƯỚC 6: CHẠY BENCHMARK SUITE

### 6.1. Chuẩn bị
```
1. Đảm bảo app đang chạy ổn định
2. Mở tab "Controls" trong bottom sheet
3. Scroll xuống cuối, tìm nút "Run Benchmark Suite"
```

### 6.2. Chạy Benchmark
```
1. Nhấn nút "Run Benchmark Suite"
2. Toast message hiện: "Running benchmark suite..."
3. Đợi ~30-60 giây (tùy thiết bị)
4. App sẽ tự động mở màn hình kết quả
```

### 6.3. Các Benchmark Tests

#### 1. Triangle Throughput Test
- **Mục đích**: Đo khả năng render triangles
- **Cách hoạt động**: Tạo 400 cubes (20x20 grid)
- **Metrics**: FPS, triangles/sec

#### 2. Texture Fill Rate Test
- **Mục đích**: Đo khả năng render texture
- **Metrics**: FPS, pixels/sec

#### 3. Shader Complexity Test
- **Mục đích**: So sánh simple vs complex shader
- **Metrics**: FPS, frame time

#### 4. Culling Effectiveness Test
- **Mục đích**: Đo hiệu quả của culling
- **Cách hoạt động**: So sánh với/không có culling
- **Metrics**: FPS, objects culled

#### 5. Overdraw Test
- **Mục đích**: Đo mức độ overdraw
- **Metrics**: Overdraw ratio, FPS

#### 6. Memory Bandwidth Test
- **Mục đích**: Đo bandwidth khi switch textures/meshes
- **Metrics**: Memory bandwidth, FPS

### 6.4. Xem Kết quả Benchmark
```
1. Sau khi benchmark xong, màn hình "Benchmark Results" hiện ra
2. Xem "Overall Score" (0-100, càng cao càng tốt)
3. Scroll xuống xem chi tiết từng test:
   - Test Name
   - Average FPS
   - Frame Time
   - Score
4. Nhấn nút Back để quay lại màn hình chính
```

---

## 📋 BƯỚC 7: SO SÁNH PERFORMANCE

### 7.1. Test Case 1: Back-face Culling
```
1. Mở tab "Controls"
2. Tắt "Back-face Culling"
3. Ghi lại FPS và Frame Time (từ HUD hoặc tab Metrics)
4. Bật lại "Back-face Culling"
5. Ghi lại FPS và Frame Time
6. So sánh: FPS tăng bao nhiêu? Frame Time giảm bao nhiêu?
```

### 7.2. Test Case 2: Frustum Culling
```
1. Tắt "Frustum Culling"
2. Ghi lại Draw Calls và Objects Rendered
3. Bật lại
4. Ghi lại Draw Calls và Objects Rendered
5. So sánh: Bao nhiêu objects bị cull?
```

### 7.3. Test Case 3: LOD
```
1. Tắt "Level of Detail (LOD)"
2. Ghi lại Triangles và FPS
3. Bật lại
4. Ghi lại Triangles và FPS
5. So sánh: Triangles giảm bao nhiêu? FPS tăng bao nhiêu?
```

### 7.4. Test Case 4: Shader Complexity
```
1. Tắt "Instanced Rendering" (dùng simple shader)
2. Ghi lại FPS
3. Bật "Instanced Rendering" (dùng complex shader)
4. Ghi lại FPS
5. So sánh: Shader phức tạp ảnh hưởng performance như thế nào?
```

---

## 📋 BƯỚC 8: SỬ DỤNG ANDROID PROFILER

### 8.1. Mở Android Profiler
```
1. Trong Android Studio, click View → Tool Windows → Profiler
2. Hoặc click tab "Profiler" ở dưới màn hình
```

### 8.2. CPU Profiler
```
1. Chọn app process trong Profiler
2. Click vào CPU timeline
3. Xem:
   - CPU usage (%)
   - Threads activity
   - Method traces
```

### 8.3. Memory Profiler
```
1. Click vào Memory timeline
2. Xem:
   - Memory usage (MB)
   - Allocations
   - Garbage collections
3. Quan sát memory khi toggle optimizations
```

### 8.4. GPU Profiler (Frame Rendering)
```
1. Click vào GPU timeline
2. Xem:
   - Frame rendering time
   - Frame drops
   - GPU usage
3. Quan sát frame time khi toggle optimizations
```

---

## 📋 BƯỚC 9: EXPORT KẾT QUẢ (Nâng cao)

### 9.1. Export Benchmark Results
```java
// Trong code, có thể gọi:
File outputDir = new File(getExternalFilesDir(null), "benchmarks");
CSVExporter.exportBenchmarkResults(results, outputDir);
```

### 9.2. Export Metrics
```java
// Trong code, có thể gọi:
File outputDir = new File(getExternalFilesDir(null), "metrics");
CSVExporter.exportMetrics(metricsCollector, outputDir);
```

### 9.3. Xem File CSV
```
1. Mở Device File Explorer trong Android Studio
2. Navigate đến: /sdcard/Android/data/com.example.opengl_es/files/
3. Tìm file CSV trong thư mục "benchmarks" hoặc "metrics"
4. Download về máy để phân tích bằng Excel/Google Sheets
```

---

## 📋 BƯỚC 10: THỰC HÀNH TỔNG HỢP

### 10.1. Scenario 1: Tối ưu cho Performance Tối đa
```
1. Bật TẤT CẢ optimizations:
   ✅ Back-face Culling
   ✅ Frustum Culling
   ✅ Occlusion Culling
   ✅ LOD
   ✅ Mipmaps
   ✅ Depth Pre-Pass
   
2. Quan sát FPS và Frame Time
3. Ghi lại kết quả
```

### 10.2. Scenario 2: Tối ưu cho Quality Tối đa
```
1. Tắt TẤT CẢ optimizations:
   ❌ Back-face Culling
   ❌ Frustum Culling
   ❌ LOD
   ❌ Mipmaps
   
2. Quan sát FPS và Frame Time
3. So sánh với Scenario 1
```

### 10.3. Scenario 3: Cân bằng Performance/Quality
```
1. Bật các optimizations không ảnh hưởng quality:
   ✅ Back-face Culling (không ảnh hưởng vì mặt sau không thấy)
   ✅ Frustum Culling (không ảnh hưởng vì object ngoài tầm nhìn)
   ✅ Mipmaps (chất lượng tốt, performance tốt)
   
2. Tắt các optimizations ảnh hưởng quality:
   ❌ LOD (giảm độ chi tiết)
   ❌ ETC1 Compression (giảm chất lượng texture)
   
3. Quan sát và so sánh
```

---

## 🎓 KẾT LUẬN

### Những gì bạn đã học:
1. ✅ Cách toggle các kỹ thuật tối ưu hóa OpenGL ES
2. ✅ Quan sát performance metrics real-time
3. ✅ Sử dụng charts để phân tích performance
4. ✅ Chạy benchmark tests và phân tích kết quả
5. ✅ So sánh performance với/không có optimizations
6. ✅ Sử dụng Android Profiler để debug

### Tips:
- **FPS > 60**: Performance tốt
- **Frame Time < 16.67ms**: Không có jank
- **Draw Calls càng ít càng tốt**: Giảm CPU overhead
- **Triangles càng ít càng tốt**: Giảm GPU load
- **Overdraw Ratio < 1.5**: Tốt, > 2.0 cần tối ưu

### Thực hành thêm:
1. Thử các combination khác nhau của optimizations
2. Quan sát sự thay đổi trong charts
3. Chạy benchmark nhiều lần để có kết quả trung bình
4. So sánh kết quả trên các thiết bị khác nhau

---

## 🆘 TROUBLESHOOTING

### App không chạy:
- Kiểm tra minSdk (phải >= 24)
- Kiểm tra thiết bị/emulator có hỗ trợ OpenGL ES 3.0
- Xem Logcat để tìm lỗi

### Bottom sheet không mở:
- Kiểm tra FAB button có hiển thị không
- Kiểm tra layout có đúng không
- Rebuild project

### Charts không hiển thị:
- Kiểm tra MPAndroidChart dependency đã sync chưa
- Xem Logcat có lỗi không
- Rebuild project

### Benchmark không chạy:
- Đợi app render ổn định trước khi chạy
- Kiểm tra renderer đã khởi tạo chưa
- Xem Logcat để debug

---

**Chúc bạn thực hành thành công! 🚀**





