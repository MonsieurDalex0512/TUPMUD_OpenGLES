# HƯỚNG DẪN THỰC HÀNH TỐI ƯU HÓA OPENGL ES 3.0
## Từng bước chi tiết và chuyên sâu

---

## 📚 MỤC LỤC

1. [Bước 1: Đơn giản hóa Shaders](#bước-1-đơn-giản-hóa-shaders)
2. [Bước 2: Kỹ thuật Culling](#bước-2-kỹ-thuật-culling)
3. [Bước 3: Level of Detail (LOD)](#bước-3-level-of-detail-lod)
4. [Bước 4: Quản lý Render Mode](#bước-4-quản-lý-render-mode)
5. [Bước 5: Công cụ Phân tích Hiệu năng](#bước-5-công-cụ-phân-tích-hiệu-năng)
6. [Bước 6: Texture Optimization (ETC1, Mipmaps)](#bước-6-texture-optimization)

---

## BƯỚC 1: ĐƠN GIẢN HÓA SHADERS

### Lý thuyết:
Shaders càng phức tạp thì GPU càng tốn nhiều chu kỳ xử lý. Cần giảm thiểu các phép tính toán học không cần thiết.

### Thực hành:

#### 1.1. Tạo Shader đơn giản (Simple Shader)
- Vertex Shader: Chỉ tính toán vị trí, không có lighting phức tạp
- Fragment Shader: Chỉ output màu cơ bản hoặc texture đơn giản

#### 1.2. Tạo Shader phức tạp (Complex Shader) để so sánh
- Vertex Shader: Có normal transformation, multiple lights
- Fragment Shader: Phong lighting, specular highlights, multiple textures

#### 1.3. Đo lường hiệu suất
- So sánh FPS giữa simple và complex shader
- Đo frame time để xác định overhead

---

## BƯỚC 2: KỸ THUẬT CULLING

### 2.1. Frustum Culling

**Lý thuyết:** Loại bỏ các vật thể nằm ngoài tầm nhìn của camera trước khi gửi lệnh vẽ tới GPU.

**Thực hành:**
1. Extract 6 frustum planes từ view-projection matrix
2. Test bounding sphere của mỗi object với các planes
3. Chỉ render objects nằm trong frustum

**Code implementation:**
- Extract frustum planes từ matrix
- Test distance từ object center đến mỗi plane
- Nếu distance < -radius → object bị cull

### 2.2. Back Face Culling

**Lý thuyết:** Luôn bật tính năng này để không vẽ các mặt phía sau.

**Thực hành:**
```java
GLES30.glEnable(GLES30.GL_CULL_FACE);
GLES30.glCullFace(GLES30.GL_BACK);
```

**Đo lường:**
- So sánh số triangles render với/không có back-face culling
- Đo FPS improvement

---

## BƯỚC 3: LEVEL OF DETAIL (LOD)

**Lý thuyết:** Các vật thể ở xa nên được vẽ bằng ít đa giác hơn.

**Thực hành:**
1. Tính khoảng cách từ camera đến object
2. Chọn mesh phù hợp dựa trên distance:
   - Gần (< 5 units): High detail mesh (nhiều triangles)
   - Trung bình (5-15 units): Medium detail mesh
   - Xa (> 15 units): Low detail mesh hoặc cull

**Implementation:**
- Tạo nhiều version của mesh với độ chi tiết khác nhau
- Switch mesh dựa trên distance trong render loop

---

## BƯỚC 4: QUẢN LÝ RENDER MODE

**Lý thuyết:** RENDERMODE_WHEN_DIRTY giúp GPU nghỉ ngơi, tiết kiệm pin.

**Thực hành:**
1. Switch từ RENDERMODE_CONTINUOUSLY sang RENDERMODE_WHEN_DIRTY
2. Chỉ gọi requestRender() khi:
   - User interaction
   - Animation frame
   - Scene thay đổi

**Code:**
```java
glSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
// Khi cần render:
glSurfaceView.requestRender();
```

**Đo lường:**
- Monitor battery usage
- So sánh frame count với/không có optimization

---

## BƯỚC 5: CÔNG CỤ PHÂN TÍCH HIỆU NĂNG

### 5.1. Profile GPU Rendering

**Lý thuyết:** Biểu đồ thanh màu hiển thị:
- Draw (xanh dương): Tạo lệnh vẽ
- Prepare (tím): Chuẩn bị dữ liệu
- Process (đỏ): Thực thi danh sách lệnh
- Execute (vàng): Gửi lệnh tới GPU

**Thực hành:**
1. Enable "Profile GPU Rendering" trong Developer Options
2. Mục tiêu: Tất cả bars < 16.67ms (đường xanh lá)
3. Nếu vượt quá → có jank (giật)

### 5.2. Dumpsys Gfxinfo

**Lý thuyết:** Lấy dữ liệu chi tiết về janky frames.

**Thực hành:**
```bash
adb shell dumpsys gfxinfo com.example.opengl_es
```

**Phân tích:**
- Total frames rendered
- Janky frames count
- Percentiles (50th, 90th, 95th, 99th)

### 5.3. FrameMetrics API (Programmatic)

**Thực hành:**
- Sử dụng Window.OnFrameMetricsAvailableListener
- Capture frame timing data programmatically
- Log và analyze trong app

---

## BƯỚC 6: TEXTURE OPTIMIZATION

### 6.1. ETC1 Compression

**Lý thuyết:** Giảm memory usage từ 4 bytes/pixel xuống ~0.5 bytes/pixel.

**Thực hành:**
1. Convert textures sang ETC1 format
2. Upload compressed data
3. Đo memory reduction

### 6.2. Mipmaps

**Lý thuyết:** Tự động chọn texture resolution phù hợp với distance.

**Thực hành:**
```java
GLES30.glGenerateMipmap(GLES30.GL_TEXTURE_2D);
GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, 
    GLES30.GL_TEXTURE_MIN_FILTER, 
    GLES30.GL_LINEAR_MIPMAP_LINEAR);
```

**Đo lường:**
- Memory usage với/không có mipmaps
- Texture quality ở các distances khác nhau

---

## 📊 BENCHMARK & MEASUREMENT

### Metrics cần đo:
1. **FPS (Frames Per Second)**
   - Target: 60 FPS
   - Measure: Average, 1% low, 0.1% low

2. **Frame Time**
   - Target: < 16.67ms
   - Measure: Average, variance, percentiles

3. **Draw Calls**
   - Target: Minimize
   - Measure: Count per frame

4. **Triangle Count**
   - Target: Optimize based on LOD
   - Measure: Triangles rendered per frame

5. **Memory Usage**
   - Target: Minimize texture memory
   - Measure: Texture memory với/không có compression

---

## 🎯 CHECKLIST THỰC HÀNH

- [ ] Bước 1: Implement simple vs complex shaders
- [ ] Bước 2: Implement frustum culling
- [ ] Bước 2: Enable back-face culling
- [ ] Bước 3: Implement LOD system
- [ ] Bước 4: Switch to RENDERMODE_WHEN_DIRTY
- [ ] Bước 5: Integrate GPU Profiling tools
- [ ] Bước 5: Implement FrameMetrics API
- [ ] Bước 6: Implement ETC1 compression
- [ ] Bước 6: Generate mipmaps
- [ ] Benchmark: Measure all metrics
- [ ] Compare: Before vs After optimization

---

## 📝 GHI CHÚ QUAN TRỌNG

1. **Luôn đo lường trước khi tối ưu:** Không đo = không biết có cải thiện không
2. **Threshold 16.67ms:** Đây là kim chỉ nam cho mọi optimization
3. **Trade-off:** Chất lượng hình ảnh vs Hiệu suất
4. **User Experience:** Mượt mà quan trọng hơn đẹp mắt

---

## 🚀 BẮT ĐẦU THỰC HÀNH

Bắt đầu từ Bước 1 và làm từng bước một. Mỗi bước sẽ có code example và giải thích chi tiết.

