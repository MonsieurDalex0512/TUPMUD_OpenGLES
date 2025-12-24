# 📚 HƯỚNG DẪN CHO NGƯỜI MỚI BẮT ĐẦU - OPENGL ES OPTIMIZATION APP

## 🎯 PHẦN 1: OPENGL ES LÀ GÌ? (Đọc để hiểu trước)

### OpenGL ES là gì?
- **OpenGL ES** = OpenGL for Embedded Systems
- Đây là **công cụ vẽ đồ họa 3D** trên điện thoại/tablet
- Giống như "cây cọ" để vẽ các hình 3D (cubes, spheres, v.v.)
- App của bạn dùng OpenGL ES 3.0 để vẽ các hình 3D lên màn hình

### Tại sao cần tối ưu hóa?
- Vẽ 3D **rất tốn tài nguyên** (CPU, GPU, memory)
- Nếu không tối ưu → **lag, giật, nóng máy**
- Tối ưu hóa = làm cho app chạy **mượt hơn, nhanh hơn**

---

## 🎯 PHẦN 2: APP CỦA BẠN LÀM GÌ?

### Mục đích của app:
1. **Vẽ scene 3D** (các cubes với pattern checkerboard)
2. **Đo lường performance** (FPS, frame time, v.v.)
3. **Cho phép bật/tắt các tối ưu hóa** để so sánh
4. **Chạy benchmark** để test performance

### App có hoạt động đúng không?
✅ **CÓ** nếu bạn thấy:
- Màn hình có màu xanh đậm
- Có các hình vuông (cubes) với pattern đen trắng
- Góc trên trái có số liệu: FPS, Frame, Draws
- Có nút tròn (FAB) ở góc dưới phải

---

## 🎯 PHẦN 3: HƯỚNG DẪN TỪNG CHỨC NĂNG

### 🔍 CHỨC NĂNG 1: HUD OVERLAY (Góc trên trái)

#### Bạn thấy gì?
```
FPS: 83.4
Frame: 12.00 ms
Draws: 64
```

#### Giải thích từng số:

**1. FPS (Frames Per Second)**
- **Là gì?** Số khung hình mỗi giây
- **Ý nghĩa:**
  - 60 FPS = mượt (tốt)
  - 30 FPS = chấp nhận được
  - < 30 FPS = lag (xấu)
- **App của bạn:** Nếu FPS > 60 → ✅ HOẠT ĐỘNG TỐT
- **Cách kiểm tra:**
  - FPS thay đổi liên tục (không đứng yên) → ✅ Đúng
  - FPS = 0 hoặc không hiển thị → ❌ Lỗi

**2. Frame (Frame Time)**
- **Là gì?** Thời gian để vẽ 1 khung hình (tính bằng ms)
- **Ý nghĩa:**
  - < 16.67 ms = tốt (> 60 FPS)
  - 16.67-33 ms = chấp nhận được (30-60 FPS)
  - > 33 ms = lag (< 30 FPS)
- **App của bạn:** Nếu Frame < 20 ms → ✅ HOẠT ĐỘNG TỐT
- **Cách kiểm tra:**
  - Frame time thay đổi (không cố định) → ✅ Đúng
  - Frame time = 0 hoặc rất cao (> 100) → ❌ Lỗi

**3. Draws (Draw Calls)**
- **Là gì?** Số lần gọi lệnh vẽ
- **Ý nghĩa:**
  - Càng ít càng tốt (giảm CPU load)
  - Thường từ 20-100 là bình thường
- **App của bạn:** Nếu Draws = 64 → ✅ HOẠT ĐỘNG TỐT
- **Cách kiểm tra:**
  - Draws thay đổi khi bạn toggle optimizations → ✅ Đúng
  - Draws = 0 → ❌ Lỗi

#### ✅ KIỂM TRA APP CÓ HOẠT ĐỘNG ĐÚNG:
```
1. Mở app
2. Quan sát HUD góc trên trái
3. Kiểm tra:
   ✅ FPS > 30 (tốt nhất > 60)
   ✅ Frame < 33 ms (tốt nhất < 16.67 ms)
   ✅ Draws > 0
   ✅ Các số thay đổi liên tục (không đứng yên)
→ Nếu đúng tất cả → APP HOẠT ĐỘNG TỐT! ✅
```

---

### 🔍 CHỨC NĂNG 2: SCENE 3D (Màn hình chính)

#### Bạn thấy gì?
- Background màu xanh đậm
- Các hình vuông (cubes) với pattern đen trắng (checkerboard)
- Các cubes xếp thành grid (lưới)
- Có thể có các hình khác (spheres, pyramids)

#### Giải thích:
- **Background xanh đậm:** Màu nền của OpenGL
- **Cubes với checkerboard:** Các hình 3D được vẽ
- **Grid layout:** Các cubes xếp thành hàng, cột

#### ✅ KIỂM TRA APP CÓ HOẠT ĐỘNG ĐÚNG:
```
1. Mở app
2. Quan sát màn hình chính
3. Kiểm tra:
   ✅ Thấy background màu xanh đậm
   ✅ Thấy các cubes với pattern đen trắng
   ✅ Các cubes có thể xoay hoặc di chuyển (tùy code)
   ✅ Không bị đen màn hình hoặc crash
→ Nếu đúng tất cả → SCENE 3D HOẠT ĐỘNG TỐT! ✅
```

#### ❌ NẾU KHÔNG THẤY CUBES:
- Chỉ thấy background xanh → Có thể objects bị cull hết
- Màn hình đen → Lỗi OpenGL
- App crash → Lỗi code

---

### 🔍 CHỨC NĂNG 3: FAB BUTTON (Nút tròn góc dưới phải)

#### Bạn thấy gì?
- Nút tròn màu xanh (hoặc màu theme)
- Có icon hình bánh răng (⚙️) hoặc menu (☰)

#### Chức năng:
- **Nhấn vào:** Mở bottom sheet (bảng điều khiển)
- **Nhấn lại:** Đóng bottom sheet

#### ✅ KIỂM TRA APP CÓ HOẠT ĐỘNG ĐÚNG:
```
1. Nhấn vào FAB button
2. Kiểm tra:
   ✅ Bottom sheet trượt lên từ dưới
   ✅ Thấy 3 tabs: "Controls", "Metrics", "Charts"
   ✅ Nhấn lại FAB → Bottom sheet đóng lại
→ Nếu đúng → FAB BUTTON HOẠT ĐỘNG TỐT! ✅
```

#### ❌ NẾU KHÔNG HOẠT ĐỘNG:
- Nhấn không có phản ứng → Lỗi click listener
- Bottom sheet không mở → Lỗi layout hoặc code

---

### 🔍 CHỨC NĂNG 4: TAB "CONTROLS" (Điều khiển)

#### Bạn thấy gì?
Sau khi mở bottom sheet, click tab "Controls", bạn sẽ thấy:

**A. Texture Optimizations:**
- ☐ ETC1 Texture Compression
- ☑ Mipmaps (có dấu tích)
- ☐ Texture Atlasing

**B. Culling Techniques:**
- ☑ Back-face Culling (có dấu tích)
- ☐ Frustum Culling
- ☐ Occlusion Culling

**C. Other Optimizations:**
- ☑ Level of Detail (LOD) (có dấu tích)
- ☐ Instanced Rendering
- ☐ Depth Pre-Pass
- ☐ Show Overdraw Heatmap

**D. Nút "Run Benchmark Suite"** (ở cuối)

#### Giải thích từng toggle:

**1. Back-face Culling**
- **Là gì?** Không vẽ mặt sau của objects (vì không thấy)
- **Bật:** ✅ Giảm ~50% triangles → FPS tăng
- **Tắt:** ❌ Vẽ tất cả → FPS giảm
- **Cách test:**
  ```
  1. Bật Back-face Culling
  2. Ghi lại FPS (ví dụ: 80 FPS)
  3. Tắt Back-face Culling
  4. Ghi lại FPS (ví dụ: 50 FPS)
  5. So sánh: FPS giảm → ✅ Hoạt động đúng
  ```

**2. Frustum Culling**
- **Là gì?** Chỉ vẽ objects trong tầm nhìn camera
- **Bật:** ✅ Bỏ qua objects ngoài tầm nhìn → Draw Calls giảm
- **Tắt:** ❌ Vẽ tất cả → Draw Calls tăng
- **Cách test:**
  ```
  1. Bật Frustum Culling
  2. Ghi lại Draw Calls (ví dụ: 40)
  3. Tắt Frustum Culling
  4. Ghi lại Draw Calls (ví dụ: 64)
  5. So sánh: Draw Calls tăng → ✅ Hoạt động đúng
  ```

**3. Level of Detail (LOD)**
- **Là gì?** Objects ở xa dùng mesh đơn giản hơn
- **Bật:** ✅ Giảm triangles → FPS tăng
- **Tắt:** ❌ Tất cả dùng mesh chi tiết → Triangles tăng
- **Cách test:**
  ```
  1. Bật LOD
  2. Vào tab "Metrics", ghi lại Triangles
  3. Tắt LOD
  4. Ghi lại Triangles
  5. So sánh: Triangles tăng → ✅ Hoạt động đúng
  ```

**4. Mipmaps**
- **Là gì?** Tạo các phiên bản nhỏ hơn của texture
- **Bật:** ✅ Tăng performance khi texture ở xa
- **Tắt:** ❌ Luôn dùng texture gốc → Performance giảm nhẹ
- **Cách test:**
  ```
  1. Bật Mipmaps
  2. Ghi lại FPS
  3. Tắt Mipmaps
  4. Ghi lại FPS
  5. So sánh: FPS giảm nhẹ → ✅ Hoạt động đúng
  ```

#### ✅ KIỂM TRA APP CÓ HOẠT ĐỘNG ĐÚNG:
```
1. Mở tab "Controls"
2. Toggle "Back-face Culling" ON/OFF
3. Quan sát HUD (góc trên trái):
   ✅ FPS thay đổi khi toggle
   ✅ Frame Time thay đổi khi toggle
4. Toggle "Frustum Culling" ON/OFF
5. Quan sát HUD:
   ✅ Draw Calls thay đổi khi toggle
→ Nếu các số thay đổi → APP HOẠT ĐỘNG ĐÚNG! ✅
```

#### ❌ NẾU KHÔNG HOẠT ĐỘNG:
- Toggle không có phản ứng → Lỗi listener
- FPS không thay đổi → Lỗi renderer không áp dụng config

---

### 🔍 CHỨC NĂNG 5: TAB "METRICS" (Số liệu chi tiết)

#### Bạn thấy gì?
Sau khi click tab "Metrics", bạn sẽ thấy:

**Frame Timing:**
- Avg Frame Time: 12.34 ms
- Frame Variance: 2.45
- Jank Count: 5

**Rendering Metrics:**
- Triangles: 2304
- Texture Binds: 1
- Shader Switches: 1
- Overdraw Ratio: 1.00

**Culling Stats:**
- Objects Rendered: 64
- Objects Culled: 0

#### Giải thích từng số:

**1. Avg Frame Time (Average Frame Time)**
- **Là gì?** Thời gian trung bình vẽ 1 frame
- **Ý nghĩa:** Giống "Frame" trong HUD nhưng là trung bình
- **Tốt:** < 16.67 ms
- **Cách kiểm tra:** Số thay đổi liên tục → ✅ Đúng

**2. Frame Variance**
- **Là gì?** Độ biến thiên của frame time
- **Ý nghĩa:**
  - Thấp = ổn định (tốt)
  - Cao = không ổn định, có jank
- **Tốt:** < 5.0
- **Cách kiểm tra:** Số > 0 → ✅ Đúng

**3. Jank Count**
- **Là gì?** Số frame bị miss (vượt quá 16.67 ms)
- **Ý nghĩa:** Càng thấp càng tốt
- **Tốt:** < 10
- **Cách kiểm tra:** Số tăng dần theo thời gian → ✅ Đúng

**4. Triangles**
- **Là gì?** Số tam giác đang render
- **Ý nghĩa:** Càng ít càng tốt (giảm GPU load)
- **Cách kiểm tra:**
  - Toggle LOD ON/OFF → Triangles thay đổi → ✅ Đúng
  - Triangles = 0 → ❌ Lỗi

**5. Objects Rendered**
- **Là gì?** Số objects đang được render
- **Cách kiểm tra:**
  - Toggle Frustum Culling → Objects Rendered thay đổi → ✅ Đúng

**6. Objects Culled**
- **Là gì?** Số objects bị bỏ qua (không render)
- **Cách kiểm tra:**
  - Bật Frustum Culling → Objects Culled > 0 → ✅ Đúng

#### ✅ KIỂM TRA APP CÓ HOẠT ĐỘNG ĐÚNG:
```
1. Mở tab "Metrics"
2. Kiểm tra:
   ✅ Tất cả các số > 0 (trừ Objects Culled có thể = 0)
   ✅ Các số thay đổi liên tục (không đứng yên)
   ✅ Toggle optimizations → Các số thay đổi
3. Quay lại tab "Controls"
4. Toggle "LOD" ON/OFF
5. Quay lại tab "Metrics"
6. Kiểm tra Triangles thay đổi
→ Nếu Triangles thay đổi → APP HOẠT ĐỘNG ĐÚNG! ✅
```

---

### 🔍 CHỨC NĂNG 6: TAB "CHARTS" (Biểu đồ)

#### Bạn thấy gì?
Sau khi click tab "Charts", bạn sẽ thấy 2 biểu đồ:

**1. FPS Over Time (Line Chart)**
- Đường xanh hiển thị FPS theo thời gian
- Trục X: Thời gian (số frame)
- Trục Y: FPS (0-120)

**2. Performance Comparison (Bar Chart)**
- Các cột so sánh metrics
- Cột 1: FPS
- Cột 2: Draw Calls
- Cột 3: Triangles/100

#### Giải thích:

**FPS Over Time Chart:**
- **Mục đích:** Xem FPS thay đổi như thế nào theo thời gian
- **Cách đọc:**
  - Đường cao = FPS cao (tốt)
  - Đường thấp = FPS thấp (xấu)
  - Đường ổn định = performance ổn định
- **Tương tác:**
  - Pinch to zoom: Phóng to/thu nhỏ
  - Drag: Kéo để xem các điểm
  - Double tap: Reset zoom

**Performance Comparison Chart:**
- **Mục đích:** So sánh các metrics với nhau
- **Cách đọc:**
  - Cột cao = giá trị cao
  - So sánh các cột để thấy metric nào cao/thấp

#### ✅ KIỂM TRA APP CÓ HOẠT ĐỘNG ĐÚNG:
```
1. Mở tab "Charts"
2. Kiểm tra FPS chart:
   ✅ Thấy đường xanh
   ✅ Đường thay đổi liên tục (không đứng yên)
   ✅ Có thể zoom/drag được
3. Kiểm tra Comparison chart:
   ✅ Thấy các cột
   ✅ Các cột có giá trị > 0
4. Đợi 5-10 giây
5. Kiểm tra FPS chart cập nhật
→ Nếu charts cập nhật → APP HOẠT ĐỘNG ĐÚNG! ✅
```

#### ❌ NẾU KHÔNG HOẠT ĐỘNG:
- Charts trống/trắng → Lỗi MPAndroidChart
- Charts không cập nhật → Lỗi update logic

---

### 🔍 CHỨC NĂNG 7: BENCHMARK SUITE (Chạy test)

#### Bạn thấy gì?
- Nút "Run Benchmark Suite" ở cuối tab "Controls"

#### Chức năng:
- Chạy 6 tests để đo performance
- Mỗi test mất ~3-5 giây
- Tổng thời gian: ~30-60 giây

#### Các tests:

**1. Triangle Throughput Test**
- **Làm gì?** Tạo 400 cubes để test khả năng render triangles
- **Đo gì?** FPS, triangles/sec

**2. Texture Fill Rate Test**
- **Làm gì?** Test khả năng render texture
- **Đo gì?** FPS, pixels/sec

**3. Shader Complexity Test**
- **Làm gì?** So sánh simple vs complex shader
- **Đo gì?** FPS, frame time

**4. Culling Effectiveness Test**
- **Làm gì?** So sánh với/không có culling
- **Đo gì?** FPS, objects culled

**5. Overdraw Test**
- **Làm gì?** Đo mức độ overdraw
- **Đo gì?** Overdraw ratio, FPS

**6. Memory Bandwidth Test**
- **Làm gì?** Test bandwidth khi switch textures
- **Đo gì?** Memory bandwidth, FPS

#### Cách chạy:
```
1. Mở tab "Controls"
2. Scroll xuống cuối
3. Nhấn nút "Run Benchmark Suite"
4. Đợi ~30-60 giây
5. App tự động mở màn hình kết quả
```

#### ✅ KIỂM TRA APP CÓ HOẠT ĐỘNG ĐÚNG:
```
1. Nhấn "Run Benchmark Suite"
2. Kiểm tra:
   ✅ Toast message hiện: "Running benchmark suite..."
   ✅ App không crash
   ✅ Đợi 30-60 giây
   ✅ Màn hình kết quả tự động mở
3. Xem kết quả:
   ✅ Thấy "Overall Score" (số từ 0-100)
   ✅ Thấy danh sách 6 tests
   ✅ Mỗi test có FPS, Frame Time, Score
→ Nếu thấy kết quả → BENCHMARK HOẠT ĐỘNG ĐÚNG! ✅
```

#### ❌ NẾU KHÔNG HOẠT ĐỘNG:
- App crash khi chạy → Lỗi benchmark code
- Không có kết quả → Lỗi benchmark runner
- Màn hình kết quả trống → Lỗi display results

---

## 🎯 PHẦN 4: CHECKLIST KIỂM TRA APP HOẠT ĐỘNG ĐÚNG

### ✅ CHECKLIST CƠ BẢN:

**1. App khởi động:**
- [ ] App không crash khi mở
- [ ] Thấy background màu xanh đậm
- [ ] Thấy các cubes với pattern đen trắng
- [ ] Thấy HUD góc trên trái (FPS, Frame, Draws)

**2. HUD Overlay:**
- [ ] FPS > 30 (tốt nhất > 60)
- [ ] Frame < 33 ms (tốt nhất < 16.67 ms)
- [ ] Draws > 0
- [ ] Các số thay đổi liên tục (không đứng yên)

**3. FAB Button:**
- [ ] Thấy nút tròn góc dưới phải
- [ ] Nhấn vào → Bottom sheet mở
- [ ] Nhấn lại → Bottom sheet đóng

**4. Tab "Controls":**
- [ ] Thấy các toggle switches
- [ ] Toggle "Back-face Culling" → FPS thay đổi
- [ ] Toggle "Frustum Culling" → Draw Calls thay đổi
- [ ] Thấy nút "Run Benchmark Suite"

**5. Tab "Metrics":**
- [ ] Thấy các metrics (Frame Time, Triangles, v.v.)
- [ ] Các số > 0 (trừ Objects Culled có thể = 0)
- [ ] Các số thay đổi liên tục
- [ ] Toggle LOD → Triangles thay đổi

**6. Tab "Charts":**
- [ ] Thấy FPS line chart
- [ ] Thấy Comparison bar chart
- [ ] Charts cập nhật theo thời gian
- [ ] Có thể zoom/drag charts

**7. Benchmark:**
- [ ] Nhấn "Run Benchmark Suite" → Toast hiện
- [ ] Đợi 30-60 giây → Màn hình kết quả mở
- [ ] Thấy Overall Score
- [ ] Thấy 6 test results

### ✅ NẾU TẤT CẢ ĐÚNG → APP HOẠT ĐỘNG TỐT! 🎉

---

## 🎯 PHẦN 5: HƯỚNG DẪN TEST ĐẦY ĐỦ TỪNG TÍNH NĂNG

> **LƯU Ý:** Mỗi test cần ghi lại các chỉ số TRƯỚC và SAU khi thay đổi để so sánh. Sử dụng bảng ghi chép hoặc screenshot để theo dõi.

---

### 📋 TEST 1: HUD OVERLAY (Góc trên trái)

#### Mục đích: Kiểm tra HUD hiển thị và cập nhật đúng

#### Bước test:

**Bước 1: Khởi động app**
```
1. Mở app
2. Đợi 3-5 giây để app render ổn định
3. Quan sát góc trên trái màn hình
```

**Bước 2: Ghi lại các chỉ số ban đầu**
```
Ghi vào bảng:
┌─────────────────┬──────────┐
│ Chỉ số          │ Giá trị  │
├─────────────────┼──────────┤
│ FPS             │ _____    │
│ Frame (ms)      │ _____    │
│ Draws           │ _____    │
└─────────────────┴──────────┘
```

**Bước 3: Kiểm tra cập nhật real-time**
```
1. Quan sát HUD trong 10 giây
2. Kiểm tra:
   ✅ FPS thay đổi liên tục (không đứng yên)
   ✅ Frame Time thay đổi liên tục
   ✅ Draws có thể thay đổi hoặc cố định
```

**Bước 4: Kiểm tra giá trị hợp lệ**
```
✅ FPS > 0 và < 120 (thường 30-90)
✅ Frame Time > 0 và < 100 ms (thường 10-30 ms)
✅ Draws > 0 (thường 20-100)
```

#### ✅ KẾT QUẢ ĐÚNG:
- Tất cả 3 chỉ số hiển thị
- Các số thay đổi liên tục (không đứng yên)
- Giá trị nằm trong khoảng hợp lệ

#### ❌ KẾT QUẢ SAI:
- FPS = 0 hoặc không hiển thị
- Frame Time = 0 hoặc rất cao (> 100 ms)
- Draws = 0
- Các số đứng yên, không thay đổi

---

### 📋 TEST 2: SCENE 3D (Màn hình chính)

#### Mục đích: Kiểm tra scene 3D render đúng

#### Bước test:

**Bước 1: Kiểm tra background**
```
1. Quan sát toàn bộ màn hình
2. Kiểm tra:
   ✅ Background màu xanh đậm (không phải đen)
   ✅ Không có lỗi hiển thị (artifacts, glitches)
```

**Bước 2: Kiểm tra cubes**
```
1. Quan sát các hình 3D
2. Kiểm tra:
   ✅ Thấy các cubes (hình vuông 3D)
   ✅ Các cubes có pattern đen trắng (checkerboard)
   ✅ Các cubes xếp thành grid (lưới)
   ✅ Số lượng cubes hợp lý (thường 64-100 cubes)
```

**Bước 3: Kiểm tra animation (nếu có)**
```
1. Quan sát trong 5 giây
2. Kiểm tra:
   ✅ Các cubes có thể xoay hoặc di chuyển
   ✅ Animation mượt mà (không giật)
```

#### ✅ KẾT QUẢ ĐÚNG:
- Background xanh đậm
- Thấy cubes với pattern checkerboard
- Animation mượt (nếu có)

#### ❌ KẾT QUẢ SAI:
- Màn hình đen hoàn toàn
- Chỉ thấy background, không thấy cubes
- Cubes không có pattern
- Animation giật hoặc không mượt

---

### 📋 TEST 3: FAB BUTTON VÀ BOTTOM SHEET

#### Mục đích: Kiểm tra UI controls hoạt động đúng

#### Bước test:

**Bước 1: Kiểm tra FAB button**
```
1. Quan sát góc dưới phải màn hình
2. Kiểm tra:
   ✅ Thấy nút tròn (FAB)
   ✅ Nút có icon (bánh răng hoặc menu)
   ✅ Nút có màu (xanh hoặc màu theme)
```

**Bước 2: Test mở bottom sheet**
```
1. Nhấn vào FAB button
2. Quan sát:
   ✅ Bottom sheet trượt lên từ dưới
   ✅ Animation mượt mà
   ✅ Thấy 3 tabs: "Controls", "Metrics", "Charts"
```

**Bước 3: Test đóng bottom sheet**
```
1. Nhấn lại FAB button (hoặc vuốt xuống)
2. Quan sát:
   ✅ Bottom sheet trượt xuống
   ✅ Animation mượt mà
   ✅ Scene 3D hiển thị lại bình thường
```

#### ✅ KẾT QUẢ ĐÚNG:
- FAB button hiển thị và có thể nhấn
- Bottom sheet mở/đóng mượt mà
- 3 tabs hiển thị đúng

#### ❌ KẾT QUẢ SAI:
- FAB button không hiển thị
- Nhấn FAB không có phản ứng
- Bottom sheet không mở
- Thiếu tabs

---

### 📋 TEST 4: BACK-FACE CULLING

#### Mục đích: Kiểm tra tối ưu hóa Back-face Culling hoạt động đúng

#### Bước test:

**Bước 1: Ghi lại chỉ số ban đầu (Back-face Culling BẬT)**
```
1. Mở app, đợi ổn định 5 giây
2. Mở bottom sheet → Tab "Controls"
3. Đảm bảo "Back-face Culling" đang BẬT (☑)
4. Ghi lại các chỉ số từ HUD (góc trên trái):
┌─────────────────┬──────────┐
│ Chỉ số          │ Giá trị  │
├─────────────────┼──────────┤
│ FPS             │ _____    │
│ Frame (ms)      │ _____    │
│ Draws           │ _____    │
└─────────────────┴──────────┘
5. Mở tab "Metrics", ghi lại:
┌──────────────────────┬──────────┐
│ Chỉ số              │ Giá trị  │
├──────────────────────┼──────────┤
│ Avg Frame Time (ms)  │ _____    │
│ Triangles            │ _____    │
│ Objects Rendered     │ _____    │
└──────────────────────┴──────────┘
```

**Bước 2: Tắt Back-face Culling**
```
1. Quay lại tab "Controls"
2. Tắt "Back-face Culling" (☐)
3. Đợi 3-5 giây để app áp dụng thay đổi
```

**Bước 3: Ghi lại chỉ số sau khi TẮT**
```
1. Quan sát HUD (góc trên trái), ghi lại:
┌─────────────────┬──────────┬──────────┐
│ Chỉ số          │ TRƯỚC    │ SAU      │
├─────────────────┼──────────┼──────────┤
│ FPS             │ _____    │ _____   │
│ Frame (ms)      │ _____    │ _____   │
│ Draws           │ _____    │ _____   │
└─────────────────┴──────────┴──────────┘
2. Mở tab "Metrics", ghi lại:
┌──────────────────────┬──────────┬──────────┐
│ Chỉ số              │ TRƯỚC    │ SAU      │
├──────────────────────┼──────────┼──────────┤
│ Avg Frame Time (ms)  │ _____    │ _____   │
│ Triangles            │ _____    │ _____   │
│ Objects Rendered     │ _____    │ _____   │
└──────────────────────┴──────────┴──────────┘
```

**Bước 4: Bật lại Back-face Culling**
```
1. Quay lại tab "Controls"
2. Bật "Back-face Culling" (☑)
3. Đợi 3-5 giây
4. Quan sát các chỉ số quay về gần giá trị ban đầu
```

#### So sánh và phân tích:

**Kết quả mong đợi khi TẮT Back-face Culling:**
- ✅ FPS **GIẢM** (ví dụ: 80 → 50 FPS)
- ✅ Frame Time **TĂNG** (ví dụ: 12 ms → 20 ms)
- ✅ Triangles **TĂNG** (ví dụ: 2000 → 3000)
- ✅ Avg Frame Time **TĂNG**

**Kết quả mong đợi khi BẬT lại:**
- ✅ FPS **TĂNG** về gần giá trị ban đầu
- ✅ Frame Time **GIẢM** về gần giá trị ban đầu
- ✅ Triangles **GIẢM** về gần giá trị ban đầu

#### ✅ KẾT QUẢ ĐÚNG:
- FPS giảm ít nhất 20-30% khi tắt
- Triangles tăng ít nhất 50% khi tắt (vì render cả mặt sau)
- Các chỉ số quay về gần ban đầu khi bật lại

#### ❌ KẾT QUẢ SAI:
- FPS không thay đổi khi toggle
- Triangles không thay đổi
- Các chỉ số không quay về khi bật lại

---

### 📋 TEST 5: FRUSTUM CULLING

#### Mục đích: Kiểm tra tối ưu hóa Frustum Culling hoạt động đúng

#### Bước test:

**Bước 1: Ghi lại chỉ số ban đầu (Frustum Culling TẮT)**
```
1. Mở app, đợi ổn định 5 giây
2. Mở bottom sheet → Tab "Controls"
3. Đảm bảo "Frustum Culling" đang TẮT (☐)
4. Mở tab "Metrics", ghi lại:
┌──────────────────────┬──────────┐
│ Chỉ số              │ Giá trị  │
├──────────────────────┼──────────┤
│ Objects Rendered     │ _____    │
│ Objects Culled       │ _____    │
│ Draw Calls (HUD)     │ _____    │
│ FPS                  │ _____    │
└──────────────────────┴──────────┘
```

**Bước 2: Bật Frustum Culling**
```
1. Quay lại tab "Controls"
2. Bật "Frustum Culling" (☑)
3. Đợi 3-5 giây
```

**Bước 3: Ghi lại chỉ số sau khi BẬT**
```
1. Mở tab "Metrics", ghi lại:
┌──────────────────────┬──────────┬──────────┐
│ Chỉ số              │ TRƯỚC    │ SAU      │
├──────────────────────┼──────────┼──────────┤
│ Objects Rendered     │ _____    │ _____   │
│ Objects Culled       │ _____    │ _____   │
│ Draw Calls (HUD)     │ _____    │ _____   │
│ FPS                  │ _____    │ _____   │
└──────────────────────┴──────────┴──────────┘
```

#### So sánh và phân tích:

**Kết quả mong đợi khi BẬT Frustum Culling:**
- ✅ Objects Rendered **GIẢM** (ví dụ: 64 → 40)
- ✅ Objects Culled **TĂNG** (ví dụ: 0 → 24)
- ✅ Draw Calls **GIẢM** (ví dụ: 64 → 40)
- ✅ FPS **TĂNG** nhẹ (ví dụ: 75 → 80 FPS)

**Công thức kiểm tra:**
```
Objects Rendered (TRƯỚC) = Objects Rendered (SAU) + Objects Culled (SAU)
Ví dụ: 64 = 40 + 24 ✅
```

#### ✅ KẾT QUẢ ĐÚNG:
- Objects Rendered giảm
- Objects Culled tăng (từ 0 lên > 0)
- Tổng Objects Rendered + Objects Culled = giá trị ban đầu
- FPS tăng nhẹ (do render ít objects hơn)

#### ❌ KẾT QUẢ SAI:
- Objects Rendered không thay đổi
- Objects Culled vẫn = 0
- Draw Calls không thay đổi

---

### 📋 TEST 6: OCCLUSION CULLING

#### Mục đích: Kiểm tra tối ưu hóa Occlusion Culling hoạt động đúng

#### Bước test:

**Bước 1: Ghi lại chỉ số ban đầu (Occlusion Culling TẮT)**
```
1. Mở app, đợi ổn định 5 giây
2. Mở bottom sheet → Tab "Controls"
3. Đảm bảo "Occlusion Culling" đang TẮT (☐)
4. Mở tab "Metrics", ghi lại:
┌──────────────────────┬──────────┐
│ Chỉ số              │ Giá trị  │
├──────────────────────┼──────────┤
│ Objects Rendered     │ _____    │
│ Objects Culled       │ _____    │
│ Overdraw Ratio       │ _____    │
│ FPS                  │ _____    │
└──────────────────────┴──────────┘
```

**Bước 2: Bật Occlusion Culling**
```
1. Quay lại tab "Controls"
2. Bật "Occlusion Culling" (☑)
3. Đợi 3-5 giây
```

**Bước 3: Ghi lại chỉ số sau khi BẬT**
```
1. Mở tab "Metrics", ghi lại:
┌──────────────────────┬──────────┬──────────┐
│ Chỉ số              │ TRƯỚC    │ SAU      │
├──────────────────────┼──────────┼──────────┤
│ Objects Rendered     │ _____    │ _____   │
│ Objects Culled       │ _____    │ _____   │
│ Overdraw Ratio       │ _____    │ _____   │
│ FPS                  │ _____    │ _____   │
└──────────────────────┴──────────┴──────────┘
```

#### So sánh và phân tích:

**Kết quả mong đợi khi BẬT Occlusion Culling:**
- ✅ Objects Rendered **GIẢM** (objects bị che không render)
- ✅ Objects Culled **TĂNG** (từ 0 lên > 0)
- ✅ Overdraw Ratio **GIẢM** (ví dụ: 1.5 → 1.2)
- ✅ FPS **TĂNG** nhẹ

#### ✅ KẾT QUẢ ĐÚNG:
- Objects Culled tăng (từ 0 lên > 0)
- Overdraw Ratio giảm
- FPS tăng nhẹ

#### ❌ KẾT QUẢ SAI:
- Objects Culled vẫn = 0
- Overdraw Ratio không thay đổi

---

### 📋 TEST 7: LEVEL OF DETAIL (LOD)

#### Mục đích: Kiểm tra tối ưu hóa LOD hoạt động đúng

#### Bước test:

**Bước 1: Ghi lại chỉ số ban đầu (LOD BẬT)**
```
1. Mở app, đợi ổn định 5 giây
2. Mở bottom sheet → Tab "Controls"
3. Đảm bảo "Level of Detail (LOD)" đang BẬT (☑)
4. Mở tab "Metrics", ghi lại:
┌──────────────────────┬──────────┐
│ Chỉ số              │ Giá trị  │
├──────────────────────┼──────────┤
│ Triangles            │ _____    │
│ FPS                  │ _____    │
│ Avg Frame Time (ms)  │ _____    │
└──────────────────────┴──────────┘
```

**Bước 2: Tắt LOD**
```
1. Quay lại tab "Controls"
2. Tắt "Level of Detail (LOD)" (☐)
3. Đợi 3-5 giây
```

**Bước 3: Ghi lại chỉ số sau khi TẮT**
```
1. Mở tab "Metrics", ghi lại:
┌──────────────────────┬──────────┬──────────┐
│ Chỉ số              │ TRƯỚC    │ SAU      │
├──────────────────────┼──────────┼──────────┤
│ Triangles            │ _____    │ _____   │
│ FPS                  │ _____    │ _____   │
│ Avg Frame Time (ms)  │ _____    │ _____   │
└──────────────────────┴──────────┴──────────┘
```

**Bước 4: Bật lại LOD**
```
1. Quay lại tab "Controls"
2. Bật "Level of Detail (LOD)" (☑)
3. Đợi 3-5 giây
4. Quan sát Triangles giảm về gần giá trị ban đầu
```

#### So sánh và phân tích:

**Kết quả mong đợi khi TẮT LOD:**
- ✅ Triangles **TĂNG** (ví dụ: 2000 → 3000)
- ✅ FPS **GIẢM** nhẹ (ví dụ: 80 → 75 FPS)
- ✅ Avg Frame Time **TĂNG** (ví dụ: 12 ms → 13 ms)

**Kết quả mong đợi khi BẬT lại:**
- ✅ Triangles **GIẢM** về gần giá trị ban đầu
- ✅ FPS **TĂNG** về gần giá trị ban đầu

#### ✅ KẾT QUẢ ĐÚNG:
- Triangles tăng ít nhất 30-50% khi tắt LOD
- FPS giảm nhẹ khi tắt LOD
- Triangles quay về gần ban đầu khi bật lại

#### ❌ KẾT QUẢ SAI:
- Triangles không thay đổi
- FPS không thay đổi

---

### 📋 TEST 8: MIPMAPS

#### Mục đích: Kiểm tra tối ưu hóa Mipmaps hoạt động đúng

#### Bước test:

**Bước 1: Ghi lại chỉ số ban đầu (Mipmaps BẬT)**
```
1. Mở app, đợi ổn định 5 giây
2. Mở bottom sheet → Tab "Controls"
3. Đảm bảo "Mipmaps" đang BẬT (☑)
4. Ghi lại:
┌──────────────────────┬──────────┐
│ Chỉ số              │ Giá trị  │
├──────────────────────┼──────────┤
│ FPS                  │ _____    │
│ Frame Time (ms)      │ _____    │
│ Texture Binds        │ _____    │
└──────────────────────┴──────────┘
```

**Bước 2: Tắt Mipmaps**
```
1. Quay lại tab "Controls"
2. Tắt "Mipmaps" (☐)
3. Đợi 3-5 giây
```

**Bước 3: Ghi lại chỉ số sau khi TẮT**
```
1. Ghi lại:
┌──────────────────────┬──────────┬──────────┐
│ Chỉ số              │ TRƯỚC    │ SAU      │
├──────────────────────┼──────────┼──────────┤
│ FPS                  │ _____    │ _____   │
│ Frame Time (ms)      │ _____    │ _____   │
│ Texture Binds        │ _____    │ _____   │
└──────────────────────┴──────────┴──────────┘
```

#### So sánh và phân tích:

**Kết quả mong đợi khi TẮT Mipmaps:**
- ✅ FPS **GIẢM** nhẹ (ví dụ: 80 → 78 FPS)
- ✅ Frame Time **TĂNG** nhẹ (ví dụ: 12 ms → 12.5 ms)
- ⚠️ Texture Binds có thể không thay đổi nhiều

**Lưu ý:** Mipmaps ảnh hưởng chủ yếu khi texture ở xa, nên sự khác biệt có thể nhỏ.

#### ✅ KẾT QUẢ ĐÚNG:
- FPS giảm nhẹ khi tắt (2-5%)
- Frame Time tăng nhẹ

#### ❌ KẾT QUẢ SAI:
- Không có thay đổi gì

---

### 📋 TEST 9: ETC1 TEXTURE COMPRESSION

#### Mục đích: Kiểm tra tối ưu hóa ETC1 Compression hoạt động đúng

#### Bước test:

**Bước 1: Ghi lại chỉ số ban đầu (ETC1 TẮT)**
```
1. Mở app, đợi ổn định 5 giây
2. Mở bottom sheet → Tab "Controls"
3. Đảm bảo "ETC1 Texture Compression" đang TẮT (☐)
4. Mở tab "Metrics", ghi lại:
┌──────────────────────┬──────────┐
│ Chỉ số              │ Giá trị  │
├──────────────────────┼──────────┤
│ FPS                  │ _____    │
│ Frame Time (ms)      │ _____    │
│ Texture Memory (nếu có)│ _____    │
└──────────────────────┴──────────┘
```

**Bước 2: Bật ETC1 Compression**
```
1. Quay lại tab "Controls"
2. Bật "ETC1 Texture Compression" (☑)
3. Đợi 3-5 giây (có thể lâu hơn vì cần nén texture)
```

**Bước 3: Ghi lại chỉ số sau khi BẬT**
```
1. Ghi lại:
┌──────────────────────┬──────────┬──────────┐
│ Chỉ số              │ TRƯỚC    │ SAU      │
├──────────────────────┼──────────┼──────────┤
│ FPS                  │ _____    │ _____   │
│ Frame Time (ms)      │ _____    │ _____   │
│ Texture Memory       │ _____    │ _____   │
└──────────────────────┴──────────┴──────────┘
```

#### So sánh và phân tích:

**⚠️ LƯU Ý QUAN TRỌNG:**
- **ETC1 Compression hiện tại CHƯA được implement đầy đủ** - chỉ tính toán memory estimate
- Texture không được reload khi toggle ETC1, nên **KHÔNG có sự khác biệt thực tế** khi bật/tắt
- Các chỉ số dưới đây là **kỳ vọng** khi ETC1 được implement đầy đủ

**Các chỉ số bị ảnh hưởng khi ETC1 được implement đầy đủ:**

1. **Texture Memory (Ước tính - không hiển thị trong UI hiện tại):**
   - **TRƯỚC (ETC1 TẮT):** ~4 bytes/pixel (RGBA8888)
   - **SAU (ETC1 BẬT):** ~0.5 bytes/pixel (ETC1)
   - **Giảm:** ~87.5% (từ 1.0 MB xuống ~0.17 MB cho texture 512x512)

2. **FPS:**
   - **TRƯỚC:** Có thể thấp hơn do memory bandwidth cao
   - **SAU:** **TĂNG nhẹ** (2-5 FPS) do giảm memory bandwidth
   - **Lý do:** Texture nhỏ hơn → ít data transfer → GPU xử lý nhanh hơn

3. **Frame Time (ms):**
   - **TRƯỚC:** Có thể cao hơn
   - **SAU:** **GIẢM nhẹ** (0.1-0.3ms) do giảm texture loading time

4. **Texture Binds:**
   - **KHÔNG THAY ĐỔI** (vẫn bind cùng số lượng texture)

5. **Các chỉ số khác:**
   - **Triangles:** Không thay đổi
   - **Draw Calls:** Không thay đổi
   - **Shader Switches:** Không thay đổi
   - **Objects Rendered:** Không thay đổi

**Kết quả mong đợi khi ETC1 được implement đầy đủ:**
- ✅ Texture Memory **GIẢM** ~87.5% (từ 4 bytes/pixel xuống 0.5 bytes/pixel)
- ✅ FPS **TĂNG** nhẹ (2-5 FPS) do giảm memory bandwidth
- ✅ Frame Time **GIẢM** nhẹ (0.1-0.3ms)
- ⚠️ Visual Quality có thể **GIẢM nhẹ** (compression artifacts)

#### ✅ KẾT QUẢ ĐÚNG (khi implement đầy đủ):
- Texture Memory giảm đáng kể (~87.5%)
- FPS tăng nhẹ (2-5 FPS)
- Frame Time giảm nhẹ

#### ❌ KẾT QUẢ SAI:
- Texture Memory không thay đổi
- FPS giảm (không mong đợi)

#### 📝 LƯU Ý HIỆN TẠI:
- **Vì ETC1 chưa được implement đầy đủ, khi bật/tắt ETC1 bạn sẽ KHÔNG thấy sự khác biệt trong metrics**
- Texture Memory không được hiển thị trong UI Metrics Panel
- Để thấy sự khác biệt, cần implement đầy đủ ETC1 compression và reload texture khi toggle

---

### 📋 TEST 10: TEXTURE ATLASING

#### Mục đích: Kiểm tra tối ưu hóa Texture Atlasing hoạt động đúng

#### Bước test:

**Bước 1: Ghi lại chỉ số ban đầu (Texture Atlasing TẮT)**
```
1. Mở app, đợi ổn định 5 giây
2. Mở bottom sheet → Tab "Controls"
3. Đảm bảo "Texture Atlasing" đang TẮT (☐)
4. Mở tab "Metrics", ghi lại:
┌──────────────────────┬──────────┐
│ Chỉ số              │ Giá trị  │
├──────────────────────┼──────────┤
│ Texture Binds        │ _____    │
│ Shader Switches      │ _____    │
│ FPS                  │ _____    │
└──────────────────────┴──────────┘
```

**Bước 2: Bật Texture Atlasing**
```
1. Quay lại tab "Controls"
2. Bật "Texture Atlasing" (☑)
3. Đợi 3-5 giây
```

**Bước 3: Ghi lại chỉ số sau khi BẬT**
```
1. Ghi lại:
┌──────────────────────┬──────────┬──────────┐
│ Chỉ số              │ TRƯỚC    │ SAU      │
├──────────────────────┼──────────┼──────────┤
│ Texture Binds        │ _____    │ _____   │
│ Shader Switches      │ _____    │ _____   │
│ FPS                  │ _____    │ _____   │
└──────────────────────┴──────────┴──────────┘
```

#### So sánh và phân tích:

**Kết quả mong đợi khi BẬT Texture Atlasing:**
- ✅ Texture Binds **GIẢM** (ví dụ: 10 → 1)
- ✅ Shader Switches **GIẢM** (do ít switch texture hơn)
- ✅ FPS **TĂNG** nhẹ

#### ✅ KẾT QUẢ ĐÚNG:
- Texture Binds giảm đáng kể
- FPS tăng nhẹ

#### ❌ KẾT QUẢ SAI:
- Texture Binds không thay đổi

---

### 📋 TEST 11: INSTANCED RENDERING

#### Mục đích: Kiểm tra tối ưu hóa Instanced Rendering hoạt động đúng

#### Bước test:

**Bước 1: Ghi lại chỉ số ban đầu (Instanced Rendering TẮT)**
```
1. Mở app, đợi ổn định 5 giây
2. Mở bottom sheet → Tab "Controls"
3. Đảm bảo "Instanced Rendering" đang TẮT (☐)
4. Ghi lại:
┌──────────────────────┬──────────┐
│ Chỉ số              │ Giá trị  │
├──────────────────────┼──────────┤
│ Draw Calls           │ _____    │
│ FPS                  │ _____    │
│ Frame Time (ms)      │ _____    │
└──────────────────────┴──────────┘
```

**Bước 2: Bật Instanced Rendering**
```
1. Quay lại tab "Controls"
2. Bật "Instanced Rendering" (☑)
3. Đợi 3-5 giây
```

**Bước 3: Ghi lại chỉ số sau khi BẬT**
```
1. Ghi lại:
┌──────────────────────┬──────────┬──────────┐
│ Chỉ số              │ TRƯỚC    │ SAU      │
├──────────────────────┼──────────┼──────────┤
│ Draw Calls           │ _____    │ _____   │
│ FPS                  │ _____    │ _____   │
│ Frame Time (ms)      │ _____    │ _____   │
└──────────────────────┴──────────┴──────────┘
```

#### So sánh và phân tích:

**Kết quả mong đợi khi BẬT Instanced Rendering:**
- ✅ Draw Calls **GIẢM** đáng kể (ví dụ: 64 → 10)
- ✅ FPS **TĂNG** (ví dụ: 75 → 85 FPS)
- ✅ Frame Time **GIẢM**

#### ✅ KẾT QUẢ ĐÚNG:
- Draw Calls giảm ít nhất 50%
- FPS tăng đáng kể

#### ❌ KẾT QUẢ SAI:
- Draw Calls không thay đổi
- FPS không thay đổi

---

### 📋 TEST 12: DEPTH PRE-PASS

#### Mục đích: Kiểm tra tối ưu hóa Depth Pre-Pass hoạt động đúng

#### Bước test:

**Bước 1: Ghi lại chỉ số ban đầu (Depth Pre-Pass TẮT)**
```
1. Mở app, đợi ổn định 5 giây
2. Mở bottom sheet → Tab "Controls"
3. Đảm bảo "Depth Pre-Pass" đang TẮT (☐)
4. Mở tab "Metrics", ghi lại:
┌──────────────────────┬──────────┐
│ Chỉ số              │ Giá trị  │
├──────────────────────┼──────────┤
│ Overdraw Ratio       │ _____    │
│ FPS                  │ _____    │
│ Frame Time (ms)      │ _____    │
└──────────────────────┴──────────┘
```

**Bước 2: Bật Depth Pre-Pass**
```
1. Quay lại tab "Controls"
2. Bật "Depth Pre-Pass" (☑)
3. Đợi 3-5 giây
```

**Bước 3: Ghi lại chỉ số sau khi BẬT**
```
1. Ghi lại:
┌──────────────────────┬──────────┬──────────┐
│ Chỉ số              │ TRƯỚC    │ SAU      │
├──────────────────────┼──────────┼──────────┤
│ Overdraw Ratio       │ _____    │ _____   │
│ FPS                  │ _____    │ _____   │
│ Frame Time (ms)      │ _____    │ _____   │
└──────────────────────┴──────────┴──────────┘
```

#### So sánh và phân tích:

**Kết quả mong đợi khi BẬT Depth Pre-Pass:**
- ✅ Overdraw Ratio **GIẢM** (ví dụ: 1.5 → 1.2)
- ✅ FPS **TĂNG** nhẹ
- ✅ Frame Time **GIẢM** nhẹ

**Lưu ý:** Depth Pre-Pass có thể tăng Draw Calls (do render 2 lần), nhưng giảm overdraw.

#### ✅ KẾT QUẢ ĐÚNG:
- Overdraw Ratio giảm
- FPS tăng nhẹ

#### ❌ KẾT QUẢ SAI:
- Overdraw Ratio không thay đổi

---

### 📋 TEST 13: SHOW OVERDRAW HEATMAP

#### Mục đích: Kiểm tra tính năng hiển thị Overdraw Heatmap

#### Bước test:

**Bước 1: Kiểm tra scene bình thường**
```
1. Mở app, đợi ổn định 5 giây
2. Quan sát scene 3D:
   ✅ Background xanh đậm
   ✅ Cubes có pattern đen trắng
```

**Bước 2: Bật Overdraw Heatmap**
```
1. Mở bottom sheet → Tab "Controls"
2. Bật "Show Overdraw Heatmap" (☑)
3. Đợi 2-3 giây
```

**Bước 3: Kiểm tra hiển thị heatmap**
```
1. Quan sát scene 3D:
   ✅ Màu sắc thay đổi (không còn pattern đen trắng)
   ✅ Các vùng có màu khác nhau:
      - Xanh lá = ít overdraw (tốt)
      - Vàng = overdraw trung bình
      - Đỏ = overdraw nhiều (xấu)
   ✅ Có thể thấy các vùng đỏ ở nơi objects chồng lên nhau
```

**Bước 4: Tắt Overdraw Heatmap**
```
1. Tắt "Show Overdraw Heatmap" (☐)
2. Quan sát scene quay về bình thường
```

#### ✅ KẾT QUẢ ĐÚNG:
- Scene thay đổi màu sắc khi bật
- Thấy các vùng màu khác nhau (xanh, vàng, đỏ)
- Scene quay về bình thường khi tắt

#### ❌ KẾT QUẢ SAI:
- Scene không thay đổi màu
- Vẫn thấy pattern đen trắng

---

### 📋 TEST 14: TAB "METRICS" - TẤT CẢ CHỈ SỐ

#### Mục đích: Kiểm tra tất cả metrics hiển thị và cập nhật đúng

#### Bước test:

**Bước 1: Mở tab "Metrics"**
```
1. Mở bottom sheet
2. Click tab "Metrics"
3. Quan sát tất cả các metrics
```

**Bước 2: Ghi lại tất cả chỉ số**
```
Ghi vào bảng:
┌──────────────────────────┬──────────┐
│ Chỉ số                  │ Giá trị  │
├──────────────────────────┼──────────┤
│ Avg Frame Time (ms)      │ _____    │
│ Frame Variance           │ _____    │
│ Jank Count               │ _____    │
│ Triangles                │ _____    │
│ Texture Binds            │ _____    │
│ Shader Switches          │ _____    │
│ Overdraw Ratio           │ _____    │
│ Objects Rendered         │ _____    │
│ Objects Culled           │ _____    │
└──────────────────────────┴──────────┘
```

**Bước 3: Kiểm tra cập nhật real-time**
```
1. Quan sát tab "Metrics" trong 10 giây
2. Kiểm tra:
   ✅ Avg Frame Time thay đổi liên tục
   ✅ Frame Variance thay đổi
   ✅ Jank Count tăng dần (nếu có jank)
   ✅ Triangles có thể thay đổi
   ✅ Objects Rendered có thể thay đổi
```

**Bước 4: Kiểm tra giá trị hợp lệ**
```
✅ Avg Frame Time > 0 và < 100 ms
✅ Frame Variance > 0
✅ Jank Count >= 0
✅ Triangles > 0
✅ Texture Binds >= 0
✅ Shader Switches >= 0
✅ Overdraw Ratio >= 1.0
✅ Objects Rendered > 0
✅ Objects Culled >= 0
```

#### ✅ KẾT QUẢ ĐÚNG:
- Tất cả 9 metrics hiển thị
- Các số thay đổi liên tục (trừ Jank Count chỉ tăng)
- Giá trị nằm trong khoảng hợp lệ

#### ❌ KẾT QUẢ SAI:
- Thiếu metrics
- Các số đứng yên, không cập nhật
- Giá trị không hợp lệ (ví dụ: Triangles = 0)

---

### 📋 TEST 15: TAB "CHARTS" - FPS OVER TIME CHART

#### Mục đích: Kiểm tra FPS chart hiển thị và cập nhật đúng

#### Bước test:

**Bước 1: Mở tab "Charts"**
```
1. Mở bottom sheet
2. Click tab "Charts"
3. Quan sát FPS Over Time chart (biểu đồ đường)
```

**Bước 2: Kiểm tra hiển thị ban đầu**
```
1. Quan sát chart:
   ✅ Thấy trục X (thời gian/số frame)
   ✅ Thấy trục Y (FPS, 0-120)
   ✅ Thấy đường xanh (FPS line)
   ✅ Đường có dữ liệu (không trống)
```

**Bước 3: Kiểm tra cập nhật real-time**
```
1. Quan sát chart trong 15 giây
2. Kiểm tra:
   ✅ Đường xanh di chuyển sang phải (thời gian trôi)
   ✅ Đường thay đổi lên xuống (FPS thay đổi)
   ✅ Chart tự động scroll sang phải
```

**Bước 4: Test tương tác**
```
1. Pinch to zoom: Phóng to/thu nhỏ chart
   ✅ Chart zoom được
2. Drag: Kéo chart sang trái/phải
   ✅ Chart di chuyển được
3. Double tap: Reset zoom
   ✅ Chart quay về zoom ban đầu
```

**Bước 5: Test thay đổi khi toggle optimization**
```
1. Ghi lại FPS trung bình từ chart (quan sát đường)
2. Mở tab "Controls"
3. Tắt "Back-face Culling"
4. Quay lại tab "Charts"
5. Quan sát:
   ✅ Đường FPS giảm xuống
   ✅ Chart cập nhật ngay lập tức
```

#### ✅ KẾT QUẢ ĐÚNG:
- Chart hiển thị đường xanh với dữ liệu
- Chart cập nhật liên tục
- Có thể zoom/drag/double tap
- Chart phản ánh thay đổi khi toggle optimization

#### ❌ KẾT QUẢ SAI:
- Chart trống/trắng
- Đường không di chuyển
- Không thể tương tác
- Chart không cập nhật

---

### 📋 TEST 16: TAB "CHARTS" - PERFORMANCE COMPARISON CHART

#### Mục đích: Kiểm tra Comparison chart hiển thị đúng

#### Bước test:

**Bước 1: Kiểm tra hiển thị**
```
1. Mở tab "Charts"
2. Scroll xuống (nếu cần) để thấy Performance Comparison chart
3. Quan sát:
   ✅ Thấy biểu đồ cột (bar chart)
   ✅ Thấy 3 cột: FPS, Draw Calls, Triangles/100
   ✅ Các cột có giá trị > 0
```

**Bước 2: Ghi lại giá trị các cột**
```
Ghi vào bảng:
┌──────────────────────┬──────────┐
│ Metric               │ Giá trị  │
├──────────────────────┼──────────┤
│ FPS                  │ _____    │
│ Draw Calls           │ _____    │
│ Triangles/100        │ _____    │
└──────────────────────┴──────────┘
```

**Bước 3: Kiểm tra cập nhật**
```
1. Quan sát chart trong 10 giây
2. Kiểm tra:
   ✅ Các cột thay đổi giá trị
   ✅ Chart cập nhật real-time
```

**Bước 4: So sánh với HUD**
```
1. So sánh FPS từ chart với FPS trong HUD:
   ✅ Giá trị gần giống nhau (có thể khác nhẹ do trung bình)
2. So sánh Draw Calls:
   ✅ Giá trị gần giống nhau
```

#### ✅ KẾT QUẢ ĐÚNG:
- Chart hiển thị 3 cột với giá trị > 0
- Chart cập nhật liên tục
- Giá trị khớp với HUD (gần đúng)

#### ❌ KẾT QUẢ SAI:
- Chart trống
- Các cột = 0
- Chart không cập nhật

---

### 📋 TEST 17: BENCHMARK SUITE - TỔNG QUAN

#### Mục đích: Kiểm tra benchmark suite chạy được và có kết quả

#### Bước test:

**Bước 1: Chuẩn bị**
```
1. Mở app, đợi ổn định 10 giây
2. Mở bottom sheet → Tab "Controls"
3. Scroll xuống cuối
4. Tìm nút "Run Benchmark Suite"
```

**Bước 2: Chạy benchmark**
```
1. Nhấn nút "Run Benchmark Suite"
2. Quan sát:
   ✅ Toast message hiện: "Running benchmark suite..."
   ✅ App không crash
   ✅ Scene vẫn render (có thể thay đổi)
```

**Bước 3: Đợi benchmark hoàn thành**
```
1. Đợi 30-60 giây (tùy thiết bị)
2. Quan sát:
   ✅ App không crash
   ✅ Có thể thấy scene thay đổi (do các test khác nhau)
```

**Bước 4: Kiểm tra màn hình kết quả**
```
1. Sau khi benchmark xong, app tự động mở màn hình kết quả
2. Kiểm tra:
   ✅ Thấy "Overall Score" (số từ 0-100)
   ✅ Thấy danh sách 6 tests:
      - Triangle Throughput Test
      - Texture Fill Rate Test
      - Shader Complexity Test
      - Culling Effectiveness Test
      - Overdraw Test
      - Memory Bandwidth Test
   ✅ Mỗi test có:
      - Tên test
      - FPS
      - Frame Time
      - Score
```

#### ✅ KẾT QUẢ ĐÚNG:
- Toast message hiện
- Benchmark chạy không crash
- Màn hình kết quả tự động mở
- Thấy Overall Score và 6 test results

#### ❌ KẾT QUẢ SAI:
- App crash khi chạy
- Không có màn hình kết quả
- Thiếu test results

---

### 📋 TEST 18: BENCHMARK - TRIANGLE THROUGHPUT TEST

#### Mục đích: Kiểm tra test đo khả năng render triangles

#### Bước test:

**Bước 1: Chạy benchmark và xem kết quả**
```
1. Chạy "Run Benchmark Suite" (xem TEST 17)
2. Đợi benchmark xong
3. Tìm "Triangle Throughput Test" trong danh sách
```

**Bước 2: Ghi lại kết quả**
```
Ghi vào bảng:
┌──────────────────────┬──────────┐
│ Metric               │ Giá trị  │
├──────────────────────┼──────────┤
│ FPS                  │ _____    │
│ Frame Time (ms)      │ _____    │
│ Score                │ _____    │
└──────────────────────┴──────────┘
```

**Bước 3: Phân tích kết quả**
```
1. Kiểm tra FPS:
   ✅ FPS > 30 (tốt nhất > 60)
   ✅ FPS hợp lệ (không phải 0 hoặc âm)
2. Kiểm tra Frame Time:
   ✅ Frame Time < 33 ms (tốt nhất < 16.67 ms)
3. Kiểm tra Score:
   ✅ Score từ 0-100
   ✅ Score càng cao càng tốt
```

**Bước 4: So sánh với các test khác**
```
1. So sánh FPS của test này với các test khác:
   - Triangle Throughput thường có FPS thấp hơn (do nhiều triangles)
   - Nếu FPS quá thấp (< 20) → GPU yếu hoặc quá nhiều triangles
```

#### ✅ KẾT QUẢ ĐÚNG:
- Có kết quả FPS, Frame Time, Score
- Giá trị hợp lệ
- FPS phản ánh khả năng render triangles

#### ❌ KẾT QUẢ SAI:
- Không có kết quả
- FPS = 0
- Score = 0

---

### 📋 TEST 19: BENCHMARK - TEXTURE FILL RATE TEST

#### Mục đích: Kiểm tra test đo khả năng render texture

#### Bước test:

**Bước 1: Xem kết quả**
```
1. Sau khi chạy benchmark, tìm "Texture Fill Rate Test"
2. Ghi lại kết quả:
┌──────────────────────┬──────────┐
│ Metric               │ Giá trị  │
├──────────────────────┼──────────┤
│ FPS                  │ _____    │
│ Frame Time (ms)      │ _____    │
│ Score                │ _____    │
└──────────────────────┴──────────┘
```

**Bước 2: Phân tích**
```
1. Texture Fill Rate test đo khả năng render texture
2. FPS thường cao hơn Triangle Throughput (do ít triangles hơn)
3. Kiểm tra:
   ✅ FPS > 30
   ✅ Frame Time < 33 ms
   ✅ Score hợp lệ
```

#### ✅ KẾT QUẢ ĐÚNG:
- Có kết quả đầy đủ
- FPS hợp lệ
- Score hợp lệ

---

### 📋 TEST 20: BENCHMARK - SHADER COMPLEXITY TEST

#### Mục đích: Kiểm tra test so sánh simple vs complex shader

#### Bước test:

**Bước 1: Xem kết quả**
```
1. Tìm "Shader Complexity Test" trong kết quả
2. Ghi lại:
┌──────────────────────┬──────────┐
│ Metric               │ Giá trị  │
├──────────────────────┼──────────┤
│ FPS                  │ _____    │
│ Frame Time (ms)      │ _____    │
│ Score                │ _____    │
└──────────────────────┴──────────┘
```

**Bước 2: Phân tích**
```
1. Test này so sánh simple shader vs complex shader
2. FPS thường thấp hơn do complex shader tốn GPU hơn
3. Kiểm tra:
   ✅ Có kết quả
   ✅ FPS hợp lệ
   ✅ Score hợp lệ
```

#### ✅ KẾT QUẢ ĐÚNG:
- Có kết quả
- FPS phản ánh độ phức tạp của shader

---

### 📋 TEST 21: BENCHMARK - CULLING EFFECTIVENESS TEST

#### Mục đích: Kiểm tra test đo hiệu quả của culling

#### Bước test:

**Bước 1: Xem kết quả**
```
1. Tìm "Culling Effectiveness Test"
2. Ghi lại:
┌──────────────────────┬──────────┐
│ Metric               │ Giá trị  │
├──────────────────────┼──────────┤
│ FPS                  │ _____    │
│ Frame Time (ms)      │ _____    │
│ Score                │ _____    │
└──────────────────────┴──────────┘
```

**Bước 2: Phân tích**
```
1. Test này so sánh với/không có culling
2. FPS khi có culling thường cao hơn
3. Kiểm tra:
   ✅ Có kết quả
   ✅ FPS hợp lệ
   ✅ Score hợp lệ
```

#### ✅ KẾT QUẢ ĐÚNG:
- Có kết quả
- FPS phản ánh hiệu quả của culling

---

### 📋 TEST 22: BENCHMARK - OVERDRAW TEST

#### Mục đích: Kiểm tra test đo mức độ overdraw

#### Bước test:

**Bước 1: Xem kết quả**
```
1. Tìm "Overdraw Test"
2. Ghi lại:
┌──────────────────────┬──────────┐
│ Metric               │ Giá trị  │
├──────────────────────┼──────────┤
│ FPS                  │ _____    │
│ Frame Time (ms)      │ _____    │
│ Score                │ _____    │
└──────────────────────┴──────────┘
```

**Bước 2: Phân tích**
```
1. Test này đo overdraw ratio
2. Overdraw cao → FPS thấp
3. Kiểm tra:
   ✅ Có kết quả
   ✅ FPS hợp lệ
```

#### ✅ KẾT QUẢ ĐÚNG:
- Có kết quả
- FPS phản ánh mức độ overdraw

---

### 📋 TEST 23: BENCHMARK - MEMORY BANDWIDTH TEST

#### Mục đích: Kiểm tra test đo memory bandwidth

#### Bước test:

**Bước 1: Xem kết quả**
```
1. Tìm "Memory Bandwidth Test"
2. Ghi lại:
┌──────────────────────┬──────────┐
│ Metric               │ Giá trị  │
├──────────────────────┼──────────┤
│ FPS                  │ _____    │
│ Frame Time (ms)      │ _____    │
│ Score                │ _____    │
└──────────────────────┴──────────┘
```

**Bước 2: Phân tích**
```
1. Test này đo bandwidth khi switch textures
2. FPS có thể thấp do tốn bandwidth
3. Kiểm tra:
   ✅ Có kết quả
   ✅ FPS hợp lệ
```

#### ✅ KẾT QUẢ ĐÚNG:
- Có kết quả
- FPS hợp lệ

---

### 📋 TEST 24: OVERALL SCORE VÀ SO SÁNH

#### Mục đích: Phân tích Overall Score và so sánh các test

#### Bước test:

**Bước 1: Ghi lại Overall Score**
```
1. Sau khi chạy benchmark, xem "Overall Score"
2. Ghi lại:
┌──────────────────────┬──────────┐
│ Overall Score        │ _____    │
└──────────────────────┴──────────┘
```

**Bước 2: Ghi lại tất cả test results**
```
Ghi vào bảng:
┌──────────────────────────────┬──────────┬──────────────┬──────────┐
│ Test Name                    │ FPS     │ Frame Time   │ Score    │
├──────────────────────────────┼──────────┼──────────────┼──────────┤
│ Triangle Throughput          │ _____   │ _____ ms     │ _____    │
│ Texture Fill Rate            │ _____   │ _____ ms     │ _____    │
│ Shader Complexity            │ _____   │ _____ ms     │ _____    │
│ Culling Effectiveness        │ _____   │ _____ ms     │ _____    │
│ Overdraw                     │ _____   │ _____ ms     │ _____    │
│ Memory Bandwidth             │ _____   │ _____ ms     │ _____    │
└──────────────────────────────┴──────────┴──────────────┴──────────┘
```

**Bước 3: Phân tích và so sánh**
```
1. So sánh FPS của các test:
   - Test nào có FPS cao nhất? → GPU mạnh ở lĩnh vực đó
   - Test nào có FPS thấp nhất? → GPU yếu ở lĩnh vực đó
2. Kiểm tra Overall Score:
   ✅ Score từ 0-100
   ✅ Score > 50 = GPU khá
   ✅ Score > 70 = GPU tốt
   ✅ Score > 85 = GPU rất tốt
3. So sánh với lần chạy trước:
   - Nếu thay đổi optimizations → Score thay đổi
   - Score cao hơn = tối ưu hóa tốt hơn
```

#### ✅ KẾT QUẢ ĐÚNG:
- Overall Score hợp lệ (0-100)
- Tất cả 6 tests có kết quả
- Có thể so sánh giữa các test

---

## 📊 BẢNG TỔNG HỢP TEST RESULTS

Sử dụng bảng này để ghi lại tất cả kết quả test:

```
┌─────────────────────────────────────────────────────────────────────┐
│                    BẢNG TỔNG HỢP TEST RESULTS                       │
├─────────────────────────────────────────────────────────────────────┤
│ TEST 1: HUD Overlay                                                 │
│   FPS: _____ | Frame: _____ ms | Draws: _____                       │
│                                                                     │
│ TEST 4: Back-face Culling                                          │
│   TRƯỚC: FPS _____ | Triangles _____                               │
│   SAU (TẮT): FPS _____ | Triangles _____                           │
│                                                                     │
│ TEST 5: Frustum Culling                                            │
│   TRƯỚC: Objects Rendered _____ | Objects Culled _____              │
│   SAU (BẬT): Objects Rendered _____ | Objects Culled _____          │
│                                                                     │
│ TEST 7: LOD                                                        │
│   TRƯỚC: Triangles _____ | FPS _____                               │
│   SAU (TẮT): Triangles _____ | FPS _____                            │
│                                                                     │
│ ... (ghi tiếp các test khác)                                       │
│                                                                     │
│ BENCHMARK RESULTS:                                                 │
│   Overall Score: _____                                              │
│   Triangle Throughput: FPS _____ | Score _____                     │
│   Texture Fill Rate: FPS _____ | Score _____                       │
│   ... (ghi tiếp)                                                   │
└─────────────────────────────────────────────────────────────────────┘
```

---

## ✅ CHECKLIST HOÀN THÀNH TẤT CẢ TESTS

Sử dụng checklist này để theo dõi tiến độ:

- [ ] TEST 1: HUD Overlay
- [ ] TEST 2: Scene 3D
- [ ] TEST 3: FAB Button và Bottom Sheet
- [ ] TEST 4: Back-face Culling
- [ ] TEST 5: Frustum Culling
- [ ] TEST 6: Occlusion Culling
- [ ] TEST 7: Level of Detail (LOD)
- [ ] TEST 8: Mipmaps
- [ ] TEST 9: ETC1 Texture Compression
- [ ] TEST 10: Texture Atlasing
- [ ] TEST 11: Instanced Rendering
- [ ] TEST 12: Depth Pre-Pass
- [ ] TEST 13: Show Overdraw Heatmap
- [ ] TEST 14: Tab "Metrics" - Tất cả chỉ số
- [ ] TEST 15: Tab "Charts" - FPS Over Time
- [ ] TEST 16: Tab "Charts" - Performance Comparison
- [ ] TEST 17: Benchmark Suite - Tổng quan
- [ ] TEST 18: Benchmark - Triangle Throughput
- [ ] TEST 19: Benchmark - Texture Fill Rate
- [ ] TEST 20: Benchmark - Shader Complexity
- [ ] TEST 21: Benchmark - Culling Effectiveness
- [ ] TEST 22: Benchmark - Overdraw
- [ ] TEST 23: Benchmark - Memory Bandwidth
- [ ] TEST 24: Overall Score và So sánh

---

**🎉 Nếu bạn đã hoàn thành tất cả 24 tests và tất cả đều ✅ → APP HOẠT ĐỘNG HOÀN HẢO!**

---

## 🎯 PHẦN 6: CÁC LỖI THƯỜNG GẶP VÀ CÁCH SỬA

### Lỗi 1: App không hiển thị cubes
**Triệu chứng:** Chỉ thấy background xanh, không thấy cubes
**Nguyên nhân:** Objects bị cull hết hoặc camera không đúng
**Cách sửa:**
1. Mở tab "Controls"
2. Tắt "Frustum Culling"
3. Tắt "Back-face Culling"
4. Quan sát lại

### Lỗi 2: FPS = 0 hoặc không hiển thị
**Triệu chứng:** HUD hiển thị FPS: 0 hoặc trống
**Nguyên nhân:** PerformanceMonitor chưa khởi tạo
**Cách sửa:**
1. Đóng app
2. Mở lại app
3. Đợi 2-3 giây
4. Quan sát lại

### Lỗi 3: Bottom sheet không mở
**Triệu chứng:** Nhấn FAB không có phản ứng
**Nguyên nhân:** Lỗi layout hoặc code
**Cách sửa:**
1. Rebuild project: Build → Rebuild Project
2. Chạy lại app

### Lỗi 4: Charts trống
**Triệu chứng:** Tab "Charts" không hiển thị gì
**Nguyên nhân:** MPAndroidChart chưa sync
**Cách sửa:**
1. File → Sync Project with Gradle Files
2. Build → Rebuild Project
3. Chạy lại app

### Lỗi 5: Benchmark không chạy
**Triệu chứng:** Nhấn "Run Benchmark Suite" không có gì xảy ra
**Nguyên nhân:** BenchmarkSuite chưa khởi tạo
**Cách sửa:**
1. Đóng app
2. Mở lại app
3. Đợi app render ổn định (5 giây)
4. Chạy benchmark lại

---

## 🎯 PHẦN 7: TÓM TẮT

### App của bạn làm gì?
1. ✅ Vẽ scene 3D (cubes với checkerboard pattern)
2. ✅ Hiển thị performance metrics (FPS, frame time, v.v.)
3. ✅ Cho phép toggle optimizations
4. ✅ Hiển thị metrics chi tiết
5. ✅ Hiển thị charts
6. ✅ Chạy benchmark tests

### Làm sao biết app hoạt động đúng?
- ✅ FPS > 30 (tốt nhất > 60)
- ✅ Frame < 33 ms (tốt nhất < 16.67 ms)
- ✅ Draws > 0
- ✅ Thấy cubes trên màn hình
- ✅ Toggle optimizations → Metrics thay đổi
- ✅ Charts cập nhật liên tục
- ✅ Benchmark chạy được và có kết quả

### Nếu tất cả đúng → APP HOẠT ĐỘNG TỐT! 🎉

---

**Chúc bạn thực hành thành công! Nếu có thắc mắc, hãy đọc lại phần tương ứng hoặc hỏi tôi!** 🚀

