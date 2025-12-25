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

### 📖 GIẢI THÍCH CÁC CHỈ SỐ PROFILER (Dễ hiểu cho thuyết trình)

Khi thuyết trình về Profiler, bạn cần giải thích các chỉ số này một cách dễ hiểu. Dưới đây là cách giải thích:

---

### 💻 CPU PROFILER

#### 🔢 **CPU Usage (%)** - Mức độ sử dụng CPU

**Cách giải thích khi thuyết trình:**
- **"CPU Usage là gì?"** 
  - Đây là **phần trăm CPU đang được sử dụng** để chạy app
  - Giống như xem **máy tính đang làm việc bao nhiêu phần trăm**
  
- **"Tại sao quan trọng?"**
  - CPU Usage cao → CPU phải làm việc nhiều → **tốn pin, nóng máy**
  - CPU Usage thấp → CPU nhàn rỗi → **tiết kiệm pin, mát máy**
  
- **"Giá trị tốt/xấu?"**
  - ✅ **Tốt**: < 50% (CPU còn sức để xử lý)
  - ⚠️ **Trung bình**: 50-80% (chấp nhận được)
  - ❌ **Xấu**: > 80% (CPU quá tải → lag, giật)
  
- **"Khi nào thấy trong Profiler?"**
  - Trong CPU timeline, bạn sẽ thấy **đường màu xanh** biểu diễn CPU Usage
  - Đường cao = CPU usage cao
  - Đường thấp = CPU usage thấp

**Ví dụ khi thuyết trình:**
> "Như các bạn thấy ở đây, khi TẮT Back-face Culling, CPU Usage là 45%. Khi BẬT Back-face Culling, CPU Usage giảm xuống còn 30%. Điều này có nghĩa là CPU phải làm việc ít hơn 15%, giúp tiết kiệm pin và giảm nhiệt độ."

---

#### 📊 **Thread Activity** - Hoạt động của các luồng xử lý

**Cách giải thích khi thuyết trình:**
- **"Thread Activity là gì?"**
  - Thread = **luồng xử lý** (giống như công nhân trong nhà máy)
  - Thread Activity = **mức độ bận rộn của các luồng xử lý**
  
- **"Tại sao quan trọng?"**
  - Thread ổn định → app chạy mượt
  - Thread có nhiều spikes (nhọn) → app bị giật, lag
  
- **"Giá trị tốt/xấu?"**
  - ✅ **Tốt**: Đường thread ổn định, không có spikes lớn
  - ❌ **Xấu**: Đường thread có nhiều spikes, không ổn định
  
- **"Khi nào thấy trong Profiler?"**
  - Trong CPU timeline, bạn sẽ thấy **các đường màu khác nhau** cho mỗi thread
  - Thread chính (main thread) thường là đường dày nhất

**Ví dụ khi thuyết trình:**
> "Nhìn vào Thread Activity, các bạn thấy khi TẮT Back-face Culling, thread có nhiều spikes (nhọn) - điều này gây lag. Khi BẬT Back-face Culling, thread ổn định hơn, không có spikes - app chạy mượt hơn."

---

#### ⏱️ **Method Execution Time** - Thời gian thực thi hàm

**Cách giải thích khi thuyết trình:**
- **"Method Execution Time là gì?"**
  - Đây là **thời gian một hàm (method) chạy xong**
  - Giống như đo **thời gian một công việc hoàn thành**
  
- **"Tại sao quan trọng?"**
  - Method chạy lâu → app chậm
  - Method chạy nhanh → app nhanh
  
- **"Các method quan trọng:"**
  - `onDrawFrame()`: **Thời gian vẽ 1 frame** (quan trọng nhất!)
    - Tốt: < 16.67 ms (để đạt 60 FPS)
    - Xấu: > 33 ms (chỉ đạt < 30 FPS)
  - `cullObjects()`: Thời gian loại bỏ objects không cần thiết
  - `getMeshForLOD()`: Thời gian tính toán LOD
  
- **"Khi nào thấy trong Profiler?"**
  - Trong "Call Chart" view, bạn sẽ thấy **các thanh ngang** biểu diễn thời gian
  - Thanh dài = method chạy lâu
  - Thanh ngắn = method chạy nhanh

**Ví dụ khi thuyết trình:**
> "Trong Profiler, tôi tìm method `onDrawFrame()` - đây là hàm vẽ mỗi frame. Khi TẮT Back-face Culling, hàm này chạy mất 20ms. Khi BẬT Back-face Culling, hàm này chỉ chạy 12ms. Giảm 8ms - đây là cải thiện đáng kể!"

---

### 💾 MEMORY PROFILER

#### 📈 **Memory Usage (MB)** - Mức độ sử dụng bộ nhớ

**Cách giải thích khi thuyết trình:**
- **"Memory Usage là gì?"**
  - Đây là **số MB bộ nhớ (RAM) app đang sử dụng**
  - Giống như xem **app đang chiếm bao nhiêu bộ nhớ**
  
- **"Tại sao quan trọng?"**
  - Memory cao → app có thể bị kill bởi hệ thống
  - Memory ổn định → app chạy ổn định
  - Memory tăng liên tục → **memory leak** (rò rỉ bộ nhớ) - rất xấu!
  
- **"Giá trị tốt/xấu?"**
  - ✅ **Tốt**: Memory ổn định, không tăng liên tục
  - ❌ **Xấu**: Memory tăng liên tục (memory leak)
  
- **"Khi nào thấy trong Profiler?"**
  - Trong Memory timeline, bạn sẽ thấy **đường màu xanh** biểu diễn Memory Usage
  - Đường tăng dần = memory leak
  - Đường ổn định = tốt

**Ví dụ khi thuyết trình:**
> "Nhìn vào Memory Usage, khi TẮT ETC1 Compression, app sử dụng 150MB. Khi BẬT ETC1 Compression, app chỉ sử dụng 20MB. Giảm 130MB - tiết kiệm rất nhiều bộ nhớ!"

---

#### 🔄 **Allocations** - Số lượng object được tạo

**Cách giải thích khi thuyết trình:**
- **"Allocations là gì?"**
  - Đây là **số lượng object (đối tượng) được tạo mới**
  - Mỗi lần tạo object mới = 1 allocation
  
- **"Tại sao quan trọng?"**
  - Nhiều allocations → **Garbage Collection (GC)** chạy nhiều → lag
  - Ít allocations → GC chạy ít → mượt
  
- **"Giá trị tốt/xấu?"**
  - ✅ **Tốt**: Ít allocations trong render loop (vòng lặp vẽ)
  - ❌ **Xấu**: Nhiều allocations trong render loop → gây GC → lag
  
- **"Khi nào thấy trong Profiler?"**
  - Trong Memory Profiler, bạn sẽ thấy **các sự kiện GC** (garbage collection)
  - Nhiều GC events = nhiều allocations

**Ví dụ khi thuyết trình:**
> "Trong render loop, nếu chúng ta tạo nhiều objects mới mỗi frame, sẽ có nhiều allocations. Điều này gây ra Garbage Collection - làm app bị giật. Vì vậy, chúng ta nên tái sử dụng objects thay vì tạo mới."

---

### 📊 CÁCH ĐỌC BIỂU ĐỒ TRONG PROFILER

#### **Timeline View (Xem theo thời gian):**
- **Trục X (ngang)**: Thời gian (từ trái sang phải)
- **Trục Y (dọc)**: Giá trị (CPU %, Memory MB, v.v.)
- **Đường màu**: Biểu diễn giá trị theo thời gian
  - Đường cao = giá trị cao
  - Đường thấp = giá trị thấp
  - Đường ổn định = tốt
  - Đường có spikes = không tốt

#### **Call Chart View (Biểu đồ gọi hàm):**
- **Thanh ngang**: Mỗi thanh = 1 method
- **Độ dài thanh**: Thời gian method chạy
  - Thanh dài = method chạy lâu
  - Thanh ngắn = method chạy nhanh
- **Màu sắc**: Mỗi màu = 1 thread khác nhau
- **Chiều sâu**: Method gọi method khác = lồng nhau

#### **Flame Chart View (Biểu đồ ngọn lửa):**
- **Hình dạng**: Giống ngọn lửa (flame)
- **Chiều rộng**: Thời gian method chạy
- **Chiều cao**: Độ sâu của call stack (method gọi method)
- **Màu sắc**: Mỗi màu = 1 method khác nhau

---

### 🎯 CÁC CHỈ SỐ CỤ THỂ CHO OPENGL ES APP

#### **CPU Usage (%):**
- **Ý nghĩa**: Phần trăm CPU được sử dụng
- **Tốt**: < 50% (cho mobile app)
- **Xấu**: > 80% (có thể gây lag)

#### **Thread Activity:**
- **Ý nghĩa**: Hoạt động của các thread
- **Tốt**: Thread ổn định, không có spikes
- **Xấu**: Thread có nhiều spikes, không ổn định

#### **Method Execution Time:**
- **Ý nghĩa**: Thời gian thực thi của từng method
- **Quan trọng**: 
  - `MyGLRenderer.onDrawFrame()`: Thời gian render 1 frame
  - `CullingManager.cullObjects()`: Thời gian culling
  - `LODManager.getMeshForLOD()`: Thời gian tính LOD

#### **Memory Usage (MB):**
- **Ý nghĩa**: Bộ nhớ đang sử dụng
- **Tốt**: Ổn định, không tăng liên tục
- **Xấu**: Tăng liên tục (memory leak)

#### **Allocations:**
- **Ý nghĩa**: Số object được tạo
- **Tốt**: Ít allocations trong render loop
- **Xấu**: Nhiều allocations (gây GC)

---

## 🎤 HƯỚNG DẪN TRÌNH BÀY KHI THUYẾT TRÌNH

### 📋 Checklist trước khi thuyết trình:

1. ✅ **Chuẩn bị Profiler**
   - Mở Android Studio
   - Chạy app
   - Mở Profiler (Alt + 6)
   - Chọn process: com.example.opengl_es

2. ✅ **Chuẩn bị app**
   - Đợi app render ổn định (5 giây)
   - Mở bottom sheet → Tab "Controls"
   - Sẵn sàng toggle các optimizations

3. ✅ **Chuẩn bị bảng ghi chép**
   - In bảng ghi chép (hoặc mở Excel)
   - Sẵn sàng ghi lại các chỉ số

### 🎯 Cấu trúc trình bày (3 bước):

#### **BƯỚC 1: Giới thiệu chỉ số (30 giây)**
> "Bây giờ tôi sẽ sử dụng Android Studio Profiler để đo lường performance. Profiler cho chúng ta 2 chỉ số quan trọng:
> - **CPU Usage**: Mức độ sử dụng CPU (phần trăm)
> - **Method Execution Time**: Thời gian các hàm chạy (milliseconds)
> 
> Tôi sẽ so sánh TRƯỚC và SAU khi bật tối ưu hóa để thấy sự khác biệt."

#### **BƯỚC 2: Demo thực tế (2-3 phút)**
1. **Show Profiler đang record**
   > "Đây là Profiler đang record. Tôi đã TẮT Back-face Culling, và đang record 10 giây."

2. **Ghi lại chỉ số TRƯỚC**
   > "Sau 10 giây, tôi thấy:
   > - CPU Usage: 45%
   > - onDrawFrame() time: 20ms
   > 
   > Tôi sẽ ghi lại vào bảng."

3. **Toggle optimization**
   > "Bây giờ tôi sẽ BẬT Back-face Culling trong app."

4. **Ghi lại chỉ số SAU**
   > "Sau khi bật, tôi record lại 10 giây. Kết quả:
   > - CPU Usage: 30% (giảm 15%)
   > - onDrawFrame() time: 12ms (giảm 8ms)
   > 
   > Đây là cải thiện đáng kể!"

#### **BƯỚC 3: Phân tích và kết luận (1 phút)**
> "Như các bạn thấy:
> - CPU Usage giảm 15% → CPU phải làm việc ít hơn → tiết kiệm pin
> - onDrawFrame() time giảm 8ms → vẽ nhanh hơn → FPS tăng
> 
> Điều này chứng minh Back-face Culling là một tối ưu hóa rất hiệu quả!"

### 💡 Mẹo khi thuyết trình:

1. **Chỉ số quan trọng nhất:**
   - ✅ **onDrawFrame() time** - Quan trọng nhất! (thời gian vẽ 1 frame)
   - ✅ **CPU Usage** - Dễ hiểu, dễ so sánh
   - ⚠️ Thread Activity - Khó giải thích, chỉ nói khi cần

2. **Cách giải thích số liệu:**
   - ✅ Luôn so sánh TRƯỚC và SAU
   - ✅ Tính phần trăm cải thiện: "Giảm 15% = từ 45% xuống 30%"
   - ✅ Liên hệ với trải nghiệm người dùng: "Giảm 8ms → FPS tăng → mượt hơn"

3. **Tránh:**
   - ❌ Giải thích quá kỹ thuật (thread, stack, v.v.)
   - ❌ Chỉ đọc số liệu mà không giải thích ý nghĩa
   - ❌ So sánh quá nhiều chỉ số cùng lúc (chỉ focus 2-3 chỉ số chính)

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

**📖 Giải thích các chỉ số:**
- **CPU Usage (avg)**: Mức độ sử dụng CPU trung bình (%). Giá trị này cho biết CPU đang làm việc bao nhiêu phần trăm. Ví dụ: 45% = CPU đang làm việc 45% công suất.
- **CPU Usage (max)**: Mức độ sử dụng CPU tối đa (%). Giá trị cao nhất trong khoảng thời gian record. Ví dụ: 60% = có lúc CPU lên đến 60%.
- **Thread Activity**: Hoạt động của các luồng xử lý. Quan sát xem có spikes (nhọn) hay không. Spikes = không tốt (gây lag).

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

**📖 Giải thích các chỉ số:**
- **onDrawFrame()**: Đây là hàm vẽ 1 frame (1 hình ảnh). Đây là hàm QUAN TRỌNG NHẤT vì nó quyết định tốc độ render.
- **onDrawFrame() (avg)**: Thời gian trung bình để vẽ 1 frame (milliseconds). 
  - Tốt: < 16.67 ms (đạt 60 FPS)
  - Xấu: > 33 ms (chỉ đạt < 30 FPS)
- **onDrawFrame() (max)**: Thời gian tối đa (frame chậm nhất). Giá trị này cho biết có frame nào bị lag không.
- **onDrawFrame() (min)**: Thời gian tối thiểu (frame nhanh nhất). Giá trị này cho biết frame nhanh nhất là bao nhiêu.

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

#### 🎤 Cách giải thích khi thuyết trình:

**Khi show CPU Usage:**
> "Như các bạn thấy, khi TẮT Back-face Culling, CPU Usage là 45%. Khi BẬT Back-face Culling, CPU Usage giảm xuống còn 30%. Điều này có nghĩa là CPU phải làm việc ít hơn 15% - giúp tiết kiệm pin và giảm nhiệt độ."

**Khi show onDrawFrame() time:**
> "Quan trọng hơn, thời gian vẽ 1 frame (onDrawFrame) giảm từ 20ms xuống 12ms - giảm 8ms. Điều này có nghĩa là:
> - Trước: 1 giây vẽ được 50 frame (1000ms ÷ 20ms = 50 FPS)
> - Sau: 1 giây vẽ được 83 frame (1000ms ÷ 12ms = 83 FPS)
> 
> FPS tăng từ 50 lên 83 - cải thiện 66%! App chạy mượt hơn rất nhiều."

**Khi show Thread Activity:**
> "Nhìn vào Thread Activity, các bạn thấy khi TẮT Back-face Culling, thread có nhiều spikes (nhọn) - điều này gây lag. Khi BẬT Back-face Culling, thread ổn định hơn, không có spikes - app chạy mượt hơn."

**Kết luận:**
> "Back-face Culling là một tối ưu hóa rất hiệu quả với chi phí thấp. Chỉ cần enable một flag, chúng ta đã giảm được 50% triangles cần render, giảm CPU usage 15%, và tăng FPS 66%. Đây là tối ưu hóa nên LUÔN BẬT."

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

**📖 Giải thích các chỉ số:**
- **CPU Usage (avg)**: Mức độ sử dụng CPU trung bình (%). Khi TẮT Frustum Culling, CPU phải xử lý tất cả objects (kể cả ngoài tầm nhìn) → CPU Usage cao hơn.
- **onDrawFrame() (avg)**: Thời gian trung bình vẽ 1 frame (ms). Khi TẮT Frustum Culling, phải render nhiều objects hơn → thời gian vẽ lâu hơn.

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

**📖 Giải thích các chỉ số:**
- **performFrustumCulling()**: Hàm tính toán và loại bỏ objects ngoài tầm nhìn camera. Đây là "chi phí" của culling - phải tốn thời gian để tính toán.
  - Giá trị mong đợi: ~0.5-2ms (rất nhỏ)
  - Nếu > 5ms → culling quá chậm, cần tối ưu
- **onDrawFrame() (avg)**: Thời gian vẽ 1 frame. Khi BẬT Frustum Culling, render ít objects hơn → thời gian vẽ giảm.
  - Giảm mong đợi: 10-15% (ví dụ: từ 15ms xuống 13ms)

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

**📖 Giải thích các chỉ số:**
- **CPU Usage (avg)**: Mức độ sử dụng CPU trung bình (%). Khi TẮT Occlusion Culling, CPU không phải tính toán culling → CPU Usage có thể thấp hơn, nhưng phải render nhiều objects hơn.
- **onDrawFrame() (avg)**: Thời gian vẽ 1 frame (ms). Khi TẮT Occlusion Culling, phải render cả objects bị che → thời gian vẽ lâu hơn.

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

**📖 Giải thích các chỉ số:**
- **performOcclusionCulling()**: Hàm tính toán và loại bỏ objects bị che khuất. Đây là "chi phí" của occlusion culling.
  - Giá trị mong đợi: ~1-3ms (nhiều hơn frustum culling vì phức tạp hơn)
  - Lưu ý: Cost này có thể lớn hơn frustum culling, nhưng vẫn đáng giá nếu scene có nhiều objects chồng lên nhau
- **onDrawFrame() (avg)**: Thời gian vẽ 1 frame. Khi BẬT Occlusion Culling, render ít objects hơn (bỏ qua objects bị che) → thời gian vẽ giảm.
  - Giảm mong đợi: 5-10% (ví dụ: từ 14ms xuống 13ms)

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

**📖 Giải thích các chỉ số:**
- **CPU Usage (avg)**: Mức độ sử dụng CPU trung bình (%). Khi BẬT LOD, objects ở xa dùng mesh đơn giản hơn → ít triangles hơn → CPU xử lý nhanh hơn → CPU Usage giảm.
- **onDrawFrame() (avg)**: Thời gian vẽ 1 frame (ms). Khi BẬT LOD, render ít triangles hơn (objects xa dùng mesh đơn giản) → thời gian vẽ giảm.

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

**📖 Giải thích các chỉ số:**
- **calculateLOD()**: Hàm tính toán LOD level cho mỗi object (dựa trên khoảng cách từ camera). Đây là "chi phí" của LOD.
  - Giá trị mong đợi: ~0.1-0.5ms (rất nhỏ - chỉ tính khoảng cách)
  - Cost này RẤT NHỎ so với lợi ích (giảm triangles)
- **onDrawFrame() (avg)**: Thời gian vẽ 1 frame. Khi BẬT LOD, render ít triangles hơn → thời gian vẽ giảm đáng kể.
  - Giảm mong đợi: 10-15% (ví dụ: từ 16ms xuống 14ms)

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

**📖 Giải thích các chỉ số:**
- **Memory Usage (avg)**: Bộ nhớ trung bình app đang sử dụng (MB). Khi TẮT ETC1, texture không nén → tốn nhiều memory hơn.
  - Ví dụ: Texture 512×512 không nén = 1.0 MB
- **Memory Usage (max)**: Bộ nhớ tối đa app sử dụng (MB). Giá trị này cho biết peak memory usage.

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

**📖 Giải thích các chỉ số:**
- **Memory Usage (avg)**: Bộ nhớ trung bình. Khi BẬT ETC1, texture được nén → memory giảm đáng kể.
  - Giảm mong đợi: 80-90% (ví dụ: từ 1.0 MB xuống 0.13 MB cho texture 512×512)
- **Texture Allocations**: Số lần tạo texture mới. Giá trị này cho biết có bao nhiêu texture được load.
  - Lưu ý: ETC1 không thay đổi số allocations, chỉ thay đổi kích thước mỗi texture

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

**📖 Giải thích các chỉ số:**
- **Memory Usage (avg)**: Bộ nhớ trung bình. Khi TẮT Mipmaps, chỉ lưu texture gốc → memory thấp hơn.
  - Ví dụ: Texture 512×512 không mipmaps = 1.0 MB
- **CPU Usage (avg)**: Mức độ sử dụng CPU. Khi TẮT Mipmaps, GPU luôn dùng texture gốc (lớn) → tốn bandwidth → CPU có thể cao hơn.

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

**📖 Giải thích các chỉ số:**
- **CPU Usage (avg)**: Mức độ sử dụng CPU. Khi TẮT Texture Atlasing, phải bind texture nhiều lần (mỗi object 1 lần) → tốn CPU hơn.
- **onDrawFrame() (avg)**: Thời gian vẽ 1 frame. Khi TẮT Texture Atlasing, nhiều texture binds → thời gian vẽ lâu hơn.

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

**📖 Giải thích các chỉ số:**
- **glBindTexture() calls**: Số lần gọi hàm bind texture (gắn texture vào GPU). 
  - Khi TẮT Texture Atlasing: Mỗi object bind 1 lần → N objects = N lần bind
  - Khi BẬT Texture Atlasing: Chỉ bind 1 lần cho tất cả → 1 lần bind
  - Giảm mong đợi: Từ N xuống 1 (ví dụ: từ 64 xuống 1)
- **onDrawFrame() (avg)**: Thời gian vẽ 1 frame. Ít texture binds → thời gian vẽ giảm.
  - Giảm mong đợi: 5-10% (ví dụ: từ 13ms xuống 12ms)

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

**📖 Giải thích các chỉ số:**
- **CPU Usage (avg)**: Mức độ sử dụng CPU. Khi TẮT Instanced Rendering, mỗi object = 1 draw call → nhiều draw calls → CPU phải giao tiếp với GPU nhiều lần → CPU Usage cao.
- **onDrawFrame() (avg)**: Thời gian vẽ 1 frame. Nhiều draw calls → thời gian vẽ lâu hơn.

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

**📖 Giải thích các chỉ số:**
- **Draw calls**: Số lần gọi lệnh vẽ (mỗi lần CPU bảo GPU "vẽ cái này" = 1 draw call).
  - Khi TẮT Instanced Rendering: Mỗi object = 1 draw call → N objects = N draw calls
  - Khi BẬT Instanced Rendering: Tất cả objects = 1 draw call (instanced)
  - Giảm mong đợi: Từ N xuống 1 (ví dụ: từ 64 xuống 1)
- **Instanced draw calls**: Số lần gọi instanced rendering. Khi BẬT = 1 (vẽ tất cả cùng lúc).
- **onDrawFrame() (avg)**: Thời gian vẽ 1 frame. Ít draw calls → ít CPU-GPU communication → thời gian vẽ giảm.
  - Giảm mong đợi: 20-30% (ví dụ: từ 13ms xuống 10ms)

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

**📖 Giải thích các chỉ số:**
- **CPU Usage (avg)**: Mức độ sử dụng CPU. Khi TẮT Depth Pre-pass, chỉ render 1 lần (depth + color cùng lúc) → CPU Usage thấp hơn, nhưng GPU phải render nhiều pixels bị che (overdraw).
- **onDrawFrame() (avg)**: Thời gian vẽ 1 frame. Khi TẮT Depth Pre-pass, GPU render nhiều pixels không cần thiết → thời gian vẽ có thể lâu hơn.

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

**📖 Giải thích các chỉ số:**
- **Draw calls (1 pass)**: Số draw calls khi TẮT Depth Pre-pass (chỉ render 1 lần - depth + color cùng lúc).
- **Draw calls (2 passes)**: Số draw calls khi BẬT Depth Pre-pass (render 2 lần - depth pass + color pass).
  - Tăng: Gấp đôi (ví dụ: từ 64 lên 128)
  - Lưu ý: Tăng draw calls nhưng giảm pixels rendered (do giảm overdraw)
- **onDrawFrame() (avg)**: Thời gian vẽ 1 frame. Mặc dù tăng draw calls, nhưng giảm overdraw → thời gian vẽ có thể giảm.
  - Giảm mong đợi: 5-10% (ví dụ: từ 14ms xuống 13ms)

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

**📖 Giải thích các chỉ số:**
- **CPU Usage (avg)**: Mức độ sử dụng CPU. Khi TẮT Overdraw Heatmap, render bình thường (1 lần) → CPU Usage thấp.
- **onDrawFrame() (avg)**: Thời gian vẽ 1 frame. Khi TẮT Overdraw Heatmap, render bình thường → thời gian vẽ nhanh.

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

**📖 Giải thích các chỉ số:**
- **CPU Usage (avg)**: Khi BẬT Overdraw Heatmap, CPU Usage TĂNG vì phải render objects nhiều lần (4 lần) để visualize overdraw.
  - Tăng mong đợi: 20-30% (ví dụ: từ 30% lên 40%)
  - Lưu ý: Đây là tính năng debug/visualization, không phải tối ưu hóa → tăng CPU là bình thường
- **onDrawFrame() (avg)**: Thời gian vẽ 1 frame TĂNG vì render 4 lần.
  - Tăng mong đợi: 30-50% (ví dụ: từ 12ms lên 18ms)
  - Lưu ý: Overdraw Heatmap chỉ dùng để visualize, không dùng trong production

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


