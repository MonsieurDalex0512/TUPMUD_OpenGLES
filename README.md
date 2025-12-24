# OPENGL ES 3.0 OPTIMIZATION BENCHMARK DEMO
## Hướng dẫn Thực hành Từng Bước Chi tiết

---

## 📖 GIỚI THIỆU

Project này là một ứng dụng Android demo các kỹ thuật tối ưu hóa OpenGL ES 3.0 với:
- ✅ Toggle ON/OFF từng kỹ thuật riêng biệt
- ✅ Real-time metrics monitoring
- ✅ Built-in benchmark tests
- ✅ Integration với Android Profiling Tools
- ✅ Comparison charts & graphs
- ✅ Advanced optimization techniques

---

## 🚀 BẮT ĐẦU NHANH

### 1. Mở Project trong Android Studio
```
File → Open → Chọn thư mục D:\TUPMUD\OpenGLES
```

### 2. Build Project
```
Build → Make Project (Ctrl + F9)
```

### 3. Chạy App
```
Run → Run 'app' (Shift + F10)
```

### 4. Quan sát
- HUD overlay hiển thị FPS, Frame Time, Draw Calls
- Scene 3D với các objects đang render

---

## 📚 HƯỚNG DẪN THỰC HÀNH

### File Hướng dẫn Chi tiết:
1. **`BUOC_THUC_HANH.md`** - Hướng dẫn từng bước thực hành chi tiết
2. **`HUONG_DAN_THUC_HANH_CHI_TIET.md`** - Lý thuyết và giải thích

### Các Bước Thực hành:

#### **BƯỚC 1: Đơn giản hóa Shaders** ⚡
- So sánh Simple vs Complex Shader
- Đo lường FPS và Frame Time
- File: `app/src/main/assets/shaders/`

#### **BƯỚC 2: Kỹ thuật Culling** 🎯
- Back-face Culling
- Frustum Culling
- File: `app/src/main/java/com/example/opengl_es/optimization/CullingManager.java`

#### **BƯỚC 3: Level of Detail (LOD)** 📐
- Giảm độ chi tiết cho objects ở xa
- File: `app/src/main/java/com/example/opengl_es/optimization/LODManager.java`

#### **BƯỚC 4: Quản lý Render Mode** 🔋
- RENDERMODE_WHEN_DIRTY vs RENDERMODE_CONTINUOUSLY
- File: `app/src/main/java/com/example/opengl_es/renderer/MyGLSurfaceView.java`

#### **BƯỚC 5: Công cụ Phân tích Hiệu năng** 📊
- Profile GPU Rendering (Visual)
- FrameMetrics API (Programmatic)
- Dumpsys Gfxinfo (Command Line)
- File: `app/src/main/java/com/example/opengl_es/monitoring/GPUProfiler.java`

#### **BƯỚC 6: Texture Optimization** 🖼️
- ETC1 Compression
- Mipmaps
- File: `app/src/main/java/com/example/opengl_es/optimization/TextureManager.java`

---

## 📁 CẤU TRÚC PROJECT

```
app/src/main/
├── java/com/example/opengl_es/
│   ├── MainActivity.java              # Main activity với HUD
│   ├── renderer/
│   │   ├── MyGLSurfaceView.java       # GLSurfaceView wrapper
│   │   ├── MyGLRenderer.java         # Core renderer
│   │   ├── RenderConfig.java         # Configuration flags
│   │   └── GLMesh.java               # VBO/IBO wrapper
│   ├── scene/
│   │   ├── SceneManager.java         # Scene management
│   │   ├── Camera.java               # Camera với matrices
│   │   ├── Object3D.java             # 3D object
│   │   └── Mesh.java                 # Mesh generators
│   ├── optimization/
│   │   ├── CullingManager.java       # BƯỚC 2: Culling
│   │   ├── LODManager.java          # BƯỚC 3: LOD
│   │   ├── ShaderManager.java        # BƯỚC 1: Shader management
│   │   └── TextureManager.java       # BƯỚC 6: Texture optimization
│   ├── monitoring/
│   │   ├── PerformanceMonitor.java   # Real-time FPS tracking
│   │   └── GPUProfiler.java         # BƯỚC 5: FrameMetrics API
│   └── utils/
│       ├── MathUtils.java            # Math utilities
│       └── TextureLoader.java        # Texture loading
├── assets/shaders/
│   ├── simple_vertex.glsl            # BƯỚC 1: Simple shader
│   ├── simple_fragment.glsl
│   ├── complex_vertex.glsl           # BƯỚC 1: Complex shader
│   └── complex_fragment.glsl
└── res/layout/
    ├── activity_main.xml             # Main layout
    └── overlay_metrics_hud.xml       # HUD overlay
```

---

## 🎯 MỤC TIÊU TỐI ƯU HÓA

### Threshold Quan trọng:
- **Frame Time < 16.67ms** → Đảm bảo 60 FPS
- **FPS >= 60** → Mượt mà
- **Jank Count < 5%** → Chấp nhận được

### Metrics cần đo:
1. **FPS** (Frames Per Second)
2. **Frame Time** (Average, 1% low, 99th percentile)
3. **Draw Calls** (per frame)
4. **Triangle Count** (per frame)
5. **Texture Memory** (MB)
6. **Jank Count** (frames > 16.67ms)

---

## 🔧 CÁCH SỬ DỤNG

### 1. Toggle Optimizations
Trong `RenderConfig.java`, thay đổi các flags:
```java
renderConfig.enableBackfaceCulling = true;  // BƯỚC 2
renderConfig.enableFrustumCulling = true;  // BƯỚC 2
renderConfig.enableLOD = true;             // BƯỚC 3
renderConfig.useMipmaps = true;            // BƯỚC 6
```

### 2. Switch Shader
Trong `MyGLRenderer.java`, line 240:
```java
String shaderName = "simple";  // hoặc "complex"
```

### 3. Switch Render Mode
Trong `MyGLSurfaceView.java`:
```java
setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);  // Tiết kiệm pin
// hoặc
setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY); // Mượt mà hơn
```

### 4. Enable GPU Profiling
- Settings → Developer Options → Profile GPU Rendering
- Hoặc dùng FrameMetrics API (đã tích hợp trong code)

---

## 📊 ĐO LƯỜNG & BENCHMARK

### Real-time Monitoring:
- HUD overlay hiển thị FPS, Frame Time, Draw Calls
- PerformanceMonitor track frame timing
- GPUProfiler capture FrameMetrics data

### Command Line Tools:
```bash
# Dumpsys Gfxinfo
adb shell dumpsys gfxinfo com.example.opengl_es

# Systrace (advanced)
python systrace.py gfx view -o trace.html
```

---

## ✅ CHECKLIST THỰC HÀNH

- [ ] Bước 1: So sánh Simple vs Complex Shader
- [ ] Bước 2.1: Enable Back-face Culling và đo lường
- [ ] Bước 2.2: Enable Frustum Culling và đo lường
- [ ] Bước 3: Enable LOD và đo lường triangles/FPS
- [ ] Bước 4: So sánh RENDERMODE_CONTINUOUSLY vs WHEN_DIRTY
- [ ] Bước 5.1: Enable Profile GPU Rendering và quan sát bars
- [ ] Bước 5.2: Kiểm tra FrameMetrics logs
- [ ] Bước 5.3: Chạy dumpsys gfxinfo và phân tích
- [ ] Bước 6.1: Enable Mipmaps và đo memory
- [ ] Hoàn thành bảng đo lường tổng hợp

---

## 📝 GHI CHÚ QUAN TRỌNG

1. **Luôn đo lường trước khi tối ưu:** Không đo = không biết có cải thiện không
2. **Threshold 16.67ms:** Đây là kim chỉ nam cho mọi optimization
3. **Trade-off:** Chất lượng hình ảnh vs Hiệu suất
4. **User Experience:** Mượt mà quan trọng hơn đẹp mắt

---

## 🐛 TROUBLESHOOTING

### Lỗi Build:
- Đảm bảo Gradle sync hoàn tất
- Check minSdk = 24 (cho FrameMetrics API)

### Shader không load:
- Kiểm tra file trong `assets/shaders/`
- Xem Logcat để biết lỗi compile

### App crash:
- Kiểm tra thiết bị có hỗ trợ OpenGL ES 3.0 không
- Xem Logcat để biết lỗi cụ thể

---

## 📚 TÀI LIỆU THAM KHẢO

- [OpenGL ES 3.0 Specification](https://www.khronos.org/opengles/)
- [Android GPU Profiling](https://developer.android.com/topic/performance/rendering/profile-gpu)
- [FrameMetrics API](https://developer.android.com/reference/android/view/FrameMetrics)

---

## 🎓 KẾT LUẬN

Sau khi hoàn thành tất cả các bước:
1. ✅ Hiểu cách tối ưu hóa OpenGL ES
2. ✅ Biết cách đo lường và phân tích performance
3. ✅ Áp dụng các kỹ thuật tối ưu hóa thực tế
4. ✅ Đạt được mục tiêu: Frame time < 16.67ms, FPS >= 60

**Chúc bạn thực hành thành công! 🚀**

---

## 📞 HỖ TRỢ

Nếu gặp vấn đề:
1. Kiểm tra Logcat để xem lỗi cụ thể
2. Đọc file `BUOC_THUC_HANH.md` để xem hướng dẫn chi tiết
3. Kiểm tra code comments trong các file Java

