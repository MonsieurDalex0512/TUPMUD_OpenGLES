# HƯỚNG DẪN THỰC HÀNH TỪNG BƯỚC CHI TIẾT
## OpenGL ES 3.0 Optimization trên Android Studio

---

## 🎯 MỤC TIÊU
Sau khi hoàn thành các bước này, bạn sẽ:
- Hiểu cách tối ưu hóa Shaders
- Implement các kỹ thuật Culling
- Áp dụng Level of Detail (LOD)
- Quản lý Render Mode để tiết kiệm pin
- Sử dụng Profiling Tools để đo lường
- Tối ưu hóa Texture với ETC1 và Mipmaps

---

## 📋 CHUẨN BỊ

### 1. Mở Project trong Android Studio
- File → Open → Chọn thư mục `D:\TUPMUD\OpenGLES`
- Đợi Gradle sync hoàn tất

### 2. Enable Developer Options trên thiết bị/emulator
- Settings → About Phone → Tap "Build Number" 7 lần
- Settings → Developer Options → Enable "Profile GPU Rendering"

### 3. Kết nối thiết bị hoặc chạy Emulator
- Thiết bị phải hỗ trợ OpenGL ES 3.0
- API Level >= 24 (Android 7.0) để dùng FrameMetrics

---

## BƯỚC 1: ĐƠN GIẢN HÓA SHADERS ⚡

### Mục tiêu:
So sánh hiệu suất giữa Simple Shader và Complex Shader

### Thực hành:

#### 1.1. Kiểm tra Shader Files
- Mở `app/src/main/assets/shaders/simple_vertex.glsl`
- Mở `app/src/main/assets/shaders/simple_fragment.glsl`
- So sánh với `complex_vertex.glsl` và `complex_fragment.glsl`

**Quan sát:**
- Simple shader: Chỉ tính toán vị trí, không có lighting
- Complex shader: Có Phong lighting, 4 lights, specular highlights

#### 1.2. Chạy App và Quan sát
1. Build và Run app (Shift + F10)
2. Xem HUD overlay (góc trên trái):
   - FPS: Bao nhiêu?
   - Frame Time: Bao nhiêu ms?
   - Draw Calls: Bao nhiêu?

#### 1.3. Switch Shader để So sánh
Trong `MyGLRenderer.java`, tìm dòng:
```java
String shaderName = renderConfig.enableInstancing ? "complex" : "simple";
```

**Thử nghiệm:**
- Đổi thành `"simple"` → Run → Ghi lại FPS
- Đổi thành `"complex"` → Run → Ghi lại FPS
- So sánh: Complex shader có làm FPS giảm không?

**Kết quả mong đợi:**
- Simple shader: FPS cao hơn (ít phép tính hơn)
- Complex shader: FPS thấp hơn nhưng đẹp hơn

---

## BƯỚC 2: KỸ THUẬT CULLING 🎯

### 2.1. Back-Face Culling

#### Thực hành:
1. Mở `CullingManager.java`
2. Tìm method `setBackFaceCulling()`
3. Quan sát code:
```java
GLES30.glEnable(GLES30.GL_CULL_FACE);
GLES30.glCullFace(GLES30.GL_BACK);
```

#### Kiểm tra:
1. Run app
2. Quan sát số Draw Calls và Triangles
3. Back-face culling đã được enable trong `RenderConfig` (mặc định = true)

**Đo lường:**
- Với Back-face culling OFF: Ghi lại số triangles
- Với Back-face culling ON: Ghi lại số triangles
- Tính % giảm: `(triangles_off - triangles_on) / triangles_off * 100%`

### 2.2. Frustum Culling

#### Thực hành:
1. Mở `CullingManager.java`
2. Tìm method `performFrustumCulling()`
3. Quan sát cách extract frustum planes từ view-projection matrix

#### Kiểm tra:
1. Trong `RenderConfig`, set `enableFrustumCulling = true`
2. Run app
3. Quan sát `objectsCulled` trong PerformanceMonitor

**Đo lường:**
- Tổng số objects trong scene: ?
- Objects được render (visible): ?
- Objects bị cull: ?
- % objects culled: ?

**Thử nghiệm:**
- Di chuyển camera (nếu có) → Quan sát số objects render thay đổi
- Objects ngoài frustum sẽ không được render

---

## BƯỚC 3: LEVEL OF DETAIL (LOD) 📐

### Thực hành:

#### 3.1. Kiểm tra LOD Manager
1. Mở `LODManager.java`
2. Quan sát các distance thresholds:
   - `lodDistance0 = 5.0f` (High detail)
   - `lodDistance1 = 15.0f` (Medium detail)
   - `lodDistance2 = 30.0f` (Low detail)

#### 3.2. Enable LOD
1. Trong `RenderConfig`, set `enableLOD = true`
2. Trong `MyGLRenderer`, tích hợp LOD vào render loop

#### 3.3. Đo lường
**Trước khi enable LOD:**
- Tổng số triangles: ?
- FPS: ?

**Sau khi enable LOD:**
- Tổng số triangles: ? (sẽ giảm vì objects xa dùng mesh đơn giản hơn)
- FPS: ? (sẽ tăng)

**Tính toán:**
- % triangles giảm: ?
- % FPS tăng: ?

---

## BƯỚC 4: QUẢN LÝ RENDER MODE 🔋

### Thực hành:

#### 4.1. Kiểm tra Render Mode
1. Mở `MyGLSurfaceView.java`
2. Tìm dòng:
```java
setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
```

#### 4.2. So sánh 2 chế độ

**RENDERMODE_CONTINUOUSLY:**
- Render liên tục 60fps
- Tốn pin nhiều
- Mượt mà cho animation

**RENDERMODE_WHEN_DIRTY:**
- Chỉ render khi có thay đổi
- Tiết kiệm pin
- Phù hợp cho static scenes

#### 4.3. Thử nghiệm
1. Đổi sang `RENDERMODE_CONTINUOUSLY`:
```java
setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
```

2. Run app → Quan sát FPS (sẽ luôn ~60fps)

3. Đổi lại `RENDERMODE_WHEN_DIRTY`:
```java
setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
```

4. Run app → Quan sát FPS (sẽ thấp hơn, nhưng tiết kiệm pin)

**Đo lường Pin:**
- Dùng Android Battery Historian hoặc Settings → Battery
- So sánh battery drain giữa 2 chế độ

---

## BƯỚC 5: CÔNG CỤ PHÂN TÍCH HIỆU NĂNG 📊

### 5.1. Profile GPU Rendering (Visual)

#### Thực hành:
1. Enable "Profile GPU Rendering" trong Developer Options
2. Run app
3. Quan sát các thanh màu trên màn hình:
   - **Xanh dương (Draw)**: Tạo lệnh vẽ
   - **Tím (Prepare)**: Chuẩn bị dữ liệu
   - **Đỏ (Process)**: Thực thi danh sách lệnh
   - **Vàng (Execute)**: Gửi lệnh tới GPU

#### Mục tiêu:
- **Tất cả bars < 16.67ms** (đường xanh lá ngang)
- Nếu vượt quá → có **JANK** (giật)

#### Phân tích:
- Bar nào cao nhất? → Đó là bottleneck
- Draw cao → Quá nhiều draw calls
- Execute cao → GPU overload
- Process cao → CPU overload

### 5.2. FrameMetrics API (Programmatic)

#### Thực hành:
1. Mở `GPUProfiler.java`
2. Quan sát cách capture frame timing data
3. Đã được tích hợp vào `MainActivity`

#### Kiểm tra Logs:
1. Run app
2. Mở Logcat (View → Tool Windows → Logcat)
3. Filter: `GPUProfiler`
4. Quan sát logs:
   - Frame time từng frame
   - JANK warnings khi > 16.67ms

#### Đo lường:
- Average frame time: ?
- Jank count: ?
- % frames > 16.67ms: ?

### 5.3. Dumpsys Gfxinfo (Command Line)

#### Thực hành:
1. Kết nối thiết bị qua USB
2. Mở Terminal trong Android Studio
3. Chạy lệnh:
```bash
adb shell dumpsys gfxinfo com.example.opengl_es
```

#### Phân tích Output:
- Total frames rendered: ?
- Janky frames: ?
- 50th percentile: ?
- 90th percentile: ?
- 95th percentile: ?
- 99th percentile: ?

**Mục tiêu:**
- 99th percentile < 16.67ms → Mượt mà
- Janky frames < 5% → Chấp nhận được

---

## BƯỚC 6: TEXTURE OPTIMIZATION 🖼️

### 6.1. Mipmaps

#### Thực hành:
1. Mở `TextureManager.java`
2. Tìm method `loadTexture()` với parameter `generateMipmaps`
3. Quan sát code generate mipmaps:
```java
GLES30.glGenerateMipmap(GLES30.GL_TEXTURE_2D);
```

#### Kiểm tra:
1. Load texture với `generateMipmaps = true`
2. Run app
3. Quan sát texture quality ở các distances khác nhau

**Đo lường:**
- Memory usage với mipmaps: ?
- Memory usage không mipmaps: ?
- % memory tăng: ~33% (nhưng quality tốt hơn)

### 6.2. ETC1 Compression

#### Lý thuyết:
- RGBA8888: 4 bytes/pixel
- ETC1: ~0.5 bytes/pixel
- Giảm memory ~87.5%

#### Thực hành:
1. Mở `TextureManager.java`
2. Tìm TODO comment về ETC1
3. Implement ETC1 compression (advanced - có thể skip nếu chưa có library)

**Đo lường:**
- Texture memory không compression: ?
- Texture memory với ETC1: ?
- % memory giảm: ~87.5%

---

## 📊 BẢNG ĐO LƯỜNG TỔNG HỢP

Tạo bảng để ghi lại kết quả:

| Metric | Baseline | After Optimization | Improvement |
|--------|----------|-------------------|-------------|
| FPS | ? | ? | ?% |
| Frame Time (avg) | ? ms | ? ms | ?% |
| Frame Time (99th) | ? ms | ? ms | ?% |
| Draw Calls | ? | ? | ?% |
| Triangles | ? | ? | ?% |
| Texture Memory | ? MB | ? MB | ?% |
| Jank Count | ? | ? | ?% |

---

## ✅ CHECKLIST HOÀN THÀNH

- [ ] Bước 1: So sánh Simple vs Complex Shader
- [ ] Bước 2.1: Enable Back-face Culling và đo lường
- [ ] Bước 2.2: Enable Frustum Culling và đo lường
- [ ] Bước 3: Enable LOD và đo lường triangles/FPS
- [ ] Bước 4: So sánh RENDERMODE_CONTINUOUSLY vs WHEN_DIRTY
- [ ] Bước 5.1: Enable Profile GPU Rendering và quan sát bars
- [ ] Bước 5.2: Kiểm tra FrameMetrics logs
- [ ] Bước 5.3: Chạy dumpsys gfxinfo và phân tích
- [ ] Bước 6.1: Enable Mipmaps và đo memory
- [ ] Bước 6.2: (Optional) Implement ETC1 compression
- [ ] Hoàn thành bảng đo lường tổng hợp

---

## 🎓 KẾT LUẬN

Sau khi hoàn thành tất cả các bước:
1. Bạn đã hiểu cách tối ưu hóa OpenGL ES
2. Biết cách đo lường và phân tích performance
3. Áp dụng các kỹ thuật tối ưu hóa thực tế
4. Đạt được mục tiêu: Frame time < 16.67ms, FPS >= 60

**Lưu ý quan trọng:**
- Luôn đo lường trước và sau khi optimize
- Threshold 16.67ms là kim chỉ nam
- Trade-off giữa quality và performance
- User experience (mượt mà) quan trọng hơn visual quality

---

## 📚 TÀI LIỆU THAM KHẢO

- [OpenGL ES 3.0 Specification](https://www.khronos.org/opengles/)
- [Android GPU Profiling](https://developer.android.com/topic/performance/rendering/profile-gpu)
- [FrameMetrics API](https://developer.android.com/reference/android/view/FrameMetrics)

---

**Chúc bạn thực hành thành công! 🚀**

