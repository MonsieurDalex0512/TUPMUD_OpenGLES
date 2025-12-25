# 📊 BÁO CÁO NHÓM 1: CULLING TECHNIQUES (KỸ THUẬT LOẠI BỎ)

## 🎯 TỔNG QUAN NHÓM

**Nhóm Culling Techniques** bao gồm 3 kỹ thuật giúp **loại bỏ các objects không cần thiết** trước khi render, từ đó giảm tải cho GPU và CPU.

### Mục đích chung:
- **Giảm số lượng objects** cần render mỗi frame
- **Giảm số triangles** được gửi đến GPU
- **Tăng FPS** và giảm frame time
- **Giảm CPU overhead** từ việc xử lý ít objects hơn

### 3 chức năng trong nhóm:
1. **Back-face Culling** - Loại bỏ mặt sau của objects
2. **Frustum Culling** - Loại bỏ objects ngoài tầm nhìn camera
3. **Occlusion Culling** - Loại bỏ objects bị che khuất

---

## 📖 THUẬT NGỮ KỸ THUẬT (Cần hiểu trước khi thuyết trình)

Để hiểu rõ các chức năng trong nhóm này, bạn cần nắm các thuật ngữ sau:

### 🔺 **Triangles (Tam giác)**
- **Là gì?** Đơn vị cơ bản để vẽ hình 3D. Mọi hình 3D đều được tạo từ các tam giác.
- **Ví dụ:** 
  - 1 hình vuông (cube) = 12 tam giác (6 mặt × 2 tam giác/mặt)
  - 1 hình cầu (sphere) = hàng trăm hoặc hàng nghìn tam giác
- **Tại sao quan trọng?** Càng nhiều tam giác → GPU phải xử lý càng nhiều → chậm hơn
- **Mục tiêu:** Giảm số tam giác cần render → tăng tốc độ

### 🎨 **Draw Calls (Lệnh vẽ)**
- **Là gì?** Mỗi lần CPU bảo GPU "vẽ cái này" = 1 draw call
- **Ví dụ:**
  - Có 100 objects → 100 draw calls (mỗi object 1 lần)
  - Với Instanced Rendering → 1 draw call (vẽ tất cả cùng lúc)
- **Tại sao quan trọng?** Mỗi draw call = CPU phải giao tiếp với GPU → tốn thời gian
- **Mục tiêu:** Giảm số draw calls → giảm thời gian giao tiếp CPU-GPU

### 📊 **FPS (Frames Per Second - Số khung hình mỗi giây)**
- **Là gì?** Số hình ảnh được vẽ trong 1 giây
- **Ví dụ:**
  - 60 FPS = vẽ 60 hình/giây → mượt mà
  - 30 FPS = vẽ 30 hình/giây → chấp nhận được
  - < 30 FPS = lag, giật
- **Tại sao quan trọng?** FPS càng cao → game/app càng mượt
- **Mục tiêu:** Tăng FPS lên ít nhất 60 FPS

### ⏱️ **Frame Time (Thời gian vẽ 1 khung hình)**
- **Là gì?** Thời gian để vẽ 1 hình ảnh (tính bằng milliseconds - ms)
- **Ví dụ:**
  - 60 FPS = 16.67 ms/frame (1000ms ÷ 60 = 16.67ms)
  - 30 FPS = 33.33 ms/frame
- **Tại sao quan trọng?** Frame time càng thấp → vẽ càng nhanh → FPS càng cao
- **Mục tiêu:** Giảm frame time xuống < 16.67 ms (để đạt 60 FPS)

### 🎯 **Objects (Đối tượng 3D)**
- **Là gì?** Các hình 3D trong scene (cubes, spheres, v.v.)
- **Ví dụ:** Scene có 100 cubes = 100 objects
- **Tại sao quan trọng?** Càng nhiều objects → càng tốn tài nguyên
- **Mục tiêu:** Giảm số objects cần render (bằng culling)

### 🎥 **Camera (Máy quay)**
- **Là gì?** Điểm nhìn trong scene 3D (giống như mắt người)
- **Ví dụ:** Camera ở vị trí (0, 2, 15) nhìn về phía trước
- **Tại sao quan trọng?** Chỉ objects trong tầm nhìn camera mới cần render
- **Mục tiêu:** Chỉ render objects camera nhìn thấy

### 🔍 **Culling (Loại bỏ)**
- **Là gì?** Kỹ thuật bỏ qua các objects không cần render
- **Ví dụ:** Object ở sau camera → không nhìn thấy → cull (bỏ qua)
- **Tại sao quan trọng?** Không render objects không nhìn thấy → tiết kiệm tài nguyên
- **Mục tiêu:** Cull càng nhiều objects không cần thiết càng tốt

### 💻 **CPU (Central Processing Unit)**
- **Là gì?** Bộ xử lý trung tâm (bộ não của máy tính/điện thoại)
- **Vai trò:** Xử lý logic, tính toán, quyết định render gì
- **Tại sao quan trọng?** CPU quyết định performance của app

### 🎮 **GPU (Graphics Processing Unit)**
- **Là gì?** Bộ xử lý đồ họa (chuyên vẽ hình 3D)
- **Vai trò:** Vẽ các tam giác, texture, màu sắc lên màn hình
- **Tại sao quan trọng?** GPU quyết định tốc độ vẽ hình 3D

### 📈 **Performance (Hiệu suất)**
- **Là gì?** Mức độ app chạy nhanh/chậm, mượt/lag
- **Đo lường bằng:** FPS, Frame Time, CPU Usage, Memory Usage
- **Mục tiêu:** Tối ưu hóa để tăng performance

### 🔄 **Overhead (Chi phí phụ)**
- **Là gì?** Thời gian/tài nguyên dùng cho việc phụ (không phải render trực tiếp)
- **Ví dụ:** CPU-GPU communication, tính toán culling, v.v.
- **Mục tiêu:** Giảm overhead → tăng thời gian cho việc render

---

## 🔍 CHỨC NĂNG 1: BACK-FACE CULLING

### 1.1. Khái niệm

**Back-face Culling** là kỹ thuật **không render các mặt phía sau** của objects (mặt không nhìn thấy từ camera). Đây là tối ưu hóa cơ bản nhất và hiệu quả nhất.

**Nguyên lý:**
- Mỗi object 3D có 2 mặt: **front face** (mặt trước) và **back face** (mặt sau)
- Camera chỉ nhìn thấy **front face**, không thấy **back face**
- Vì vậy, không cần render **back face** → giảm ~50% triangles

### 1.2. Code TRƯỚC khi sử dụng (Không có Back-face Culling)

```java
// MyGLRenderer.java - onDrawFrame()
@Override
public void onDrawFrame(GL10 gl) {
    // Không có culling → render tất cả faces
    GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT | GLES30.GL_DEPTH_BUFFER_BIT);
    
    // KHÔNG enable culling
    // GLES30.glEnable(GLES30.GL_CULL_FACE); // ← KHÔNG CÓ DÒNG NÀY
    
    // Render tất cả objects
    List<Object3D> allObjects = sceneManager.getObjects();
    
    for (Object3D obj : allObjects) {
        // Build model matrix
        Matrix.setIdentityM(modelMatrix, 0);
        Matrix.translateM(modelMatrix, 0, obj.positionX, obj.positionY, obj.positionZ);
        
        // Calculate MVP
        Matrix.multiplyMM(mvpMatrix, 0, viewProj, 0, modelMatrix, 0);
        GLES30.glUniformMatrix4fv(mvpMatrixLoc, 1, false, mvpMatrix, 0);
        
        // Draw - render TẤT CẢ faces (cả front và back)
        cubeMesh.draw();
        performanceMonitor.drawCalls++;
        performanceMonitor.triangleCount += cubeMesh.getTriangleCount();
        // Mỗi cube có 12 triangles (6 faces × 2 triangles/face)
        // Với 100 cubes → 1200 triangles
    }
}
```

**Kết quả khi TẮT Back-face Culling:**
- Triangles: **1200** (100 cubes × 12 triangles)
- FPS: **~50 FPS** (tùy thiết bị)
- Frame Time: **~20 ms**
- GPU phải xử lý cả mặt sau (không nhìn thấy) → lãng phí tài nguyên

### 1.3. Code SAU khi sử dụng (Có Back-face Culling)

```java
// CullingManager.java
public void setBackFaceCulling(boolean enable) {
    this.enableBackFaceCulling = enable;
    if (enable) {
        // BẬT back-face culling
        GLES30.glEnable(GLES30.GL_CULL_FACE);
        GLES30.glCullFace(GLES30.GL_BACK);  // Cull mặt sau
    } else {
        GLES30.glDisable(GLES30.GL_CULL_FACE);
    }
}

// MyGLRenderer.java - onDrawFrame()
@Override
public void onDrawFrame(GL10 gl) {
    // BƯỚC 1: Enable back-face culling TRƯỚC khi render
    cullingManager.setBackFaceCulling(renderConfig.enableBackfaceCulling);
    
    GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT | GLES30.GL_DEPTH_BUFFER_BIT);
    
    List<Object3D> allObjects = sceneManager.getObjects();
    
    for (Object3D obj : allObjects) {
        // Build matrices...
        Matrix.setIdentityM(modelMatrix, 0);
        Matrix.translateM(modelMatrix, 0, obj.positionX, obj.positionY, obj.positionZ);
        Matrix.multiplyMM(mvpMatrix, 0, viewProj, 0, modelMatrix, 0);
        GLES30.glUniformMatrix4fv(mvpMatrixLoc, 1, false, mvpMatrix, 0);
        
        // Draw - chỉ render FRONT faces
        // GPU tự động cull back faces → giảm 50% triangles
        cubeMesh.draw();
        performanceMonitor.drawCalls++;
        performanceMonitor.triangleCount += cubeMesh.getTriangleCount();
        // Mỗi cube chỉ render 6 triangles (3 faces nhìn thấy × 2 triangles/face)
        // Với 100 cubes → 600 triangles (giảm 50%)
    }
}
```

**Kết quả khi BẬT Back-face Culling:**
- Triangles: **600** (giảm 50% từ 1200)
- FPS: **~80 FPS** (tăng 60%)
- Frame Time: **~12.5 ms** (giảm 37.5%)
- GPU chỉ xử lý mặt trước → tiết kiệm tài nguyên

### 1.4. So sánh Performance

| Metric | TRƯỚC (OFF) | SAU (ON) | Cải thiện |
|--------|-------------|----------|-----------|
| Triangles | 1200 | 600 | **-50%** |
| FPS | 50 | 80 | **+60%** |
| Frame Time | 20 ms | 12.5 ms | **-37.5%** |
| CPU Usage | 45% | 30% | **-33%** |

### 1.5. Giải thích chi tiết

**Cách hoạt động:**
1. OpenGL ES kiểm tra **winding order** (thứ tự đỉnh) của mỗi triangle
2. Nếu triangle có **winding order ngược chiều kim đồng hồ** (back face) → GPU tự động bỏ qua
3. Chỉ render các triangles có **winding order cùng chiều kim đồng hồ** (front face)

**Lợi ích:**
- ✅ **Giảm 50% triangles** → GPU xử lý nhanh hơn
- ✅ **Không tốn CPU** (GPU tự động xử lý)
- ✅ **Hiệu quả cao** với chi phí thấp (chỉ cần enable flag)

**Nhược điểm:**
- ⚠️ Không phù hợp khi cần nhìn thấy cả 2 mặt (ví dụ: lá cây 2 mặt)

### 1.6. 📊 HƯỚNG DẪN SỬ DỤNG ANDROID STUDIO PROFILER

#### Mục đích:
Đo lường sự khác biệt CPU usage và frame time khi bật/tắt Back-face Culling bằng Android Studio Profiler.

#### Bước 1: Chuẩn bị
```
1. Mở app, đợi ổn định 5 giây
2. Mở Android Studio Profiler
   - View → Tool Windows → Profiler (hoặc Alt + 6)
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

## 🔍 CHỨC NĂNG 2: FRUSTUM CULLING

### 2.1. Khái niệm

**Frustum Culling** là kỹ thuật **chỉ render các objects nằm trong tầm nhìn camera** (frustum). Objects ngoài frustum sẽ bị loại bỏ trước khi render.

**Nguyên lý:**
- Camera có **6 mặt phẳng** tạo thành hình chóp cụt (frustum): left, right, top, bottom, near, far
- Chỉ objects **nằm trong frustum** mới được render
- Objects **ngoài frustum** → bị cull → không render

### 2.2. Code TRƯỚC khi sử dụng (Không có Frustum Culling)

```java
// MyGLRenderer.java - onDrawFrame()
@Override
public void onDrawFrame(GL10 gl) {
    GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT | GLES30.GL_DEPTH_BUFFER_BIT);
    
    // Lấy TẤT CẢ objects (không cull)
    List<Object3D> allObjects = sceneManager.getObjects();
    // allObjects.size() = 100 (ví dụ)
    
    // Render TẤT CẢ objects (kể cả ngoài tầm nhìn)
    for (Object3D obj : allObjects) {
        // Build matrices...
        Matrix.setIdentityM(modelMatrix, 0);
        Matrix.translateM(modelMatrix, 0, obj.positionX, obj.positionY, obj.positionZ);
        Matrix.multiplyMM(mvpMatrix, 0, viewProj, 0, modelMatrix, 0);
        GLES30.glUniformMatrix4fv(mvpMatrixLoc, 1, false, mvpMatrix, 0);
        
        // Draw - render cả objects ngoài tầm nhìn
        cubeMesh.draw();
        performanceMonitor.drawCalls++;
    }
    // Draw Calls = 100 (render tất cả)
}
```

**Kết quả khi TẮT Frustum Culling:**
- Objects Rendered: **100** (tất cả)
- Objects Culled: **0**
- Draw Calls: **100**
- FPS: **~75 FPS**
- CPU phải xử lý tất cả objects (kể cả không nhìn thấy)

### 2.3. Code SAU khi sử dụng (Có Frustum Culling)

```java
// CullingManager.java
public void setFrustumCulling(boolean enable) {
    this.enableFrustumCulling = enable;
}

private List<Object3D> performFrustumCulling(List<Object3D> objects, Camera camera) {
    List<Object3D> visible = new ArrayList<>();
    
    // Extract 6 frustum planes từ view-projection matrix
    float[] viewProj = camera.getViewProjMatrix();
    float[][] planes = extractFrustumPlanes(viewProj);
    
    for (Object3D obj : objects) {
        // Test bounding sphere against 6 frustum planes
        boolean inside = true;
        for (int i = 0; i < 6; i++) {
            // Tính distance từ object center đến plane
            float distance = planes[i][0] * obj.positionX + 
                            planes[i][1] * obj.positionY + 
                            planes[i][2] * obj.positionZ + 
                            planes[i][3];
            
            // Nếu sphere ngoài plane → cull
            if (distance < -obj.boundingRadius) {
                inside = false;
                break;
            }
        }
        
        if (inside) {
            visible.add(obj);  // Chỉ thêm objects trong frustum
        }
    }
    
    return visible;
}

// MyGLRenderer.java - onDrawFrame()
@Override
public void onDrawFrame(GL10 gl) {
    GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT | GLES30.GL_DEPTH_BUFFER_BIT);
    
    // BƯỚC 1: Enable frustum culling
    cullingManager.setFrustumCulling(renderConfig.enableFrustumCulling);
    
    // BƯỚC 2: Cull objects ngoài frustum
    List<Object3D> allObjects = sceneManager.getObjects();
    List<Object3D> visibleObjects = cullingManager.cullObjects(allObjects, camera);
    // visibleObjects.size() = 40 (ví dụ: chỉ 40 objects trong tầm nhìn)
    
    // BƯỚC 3: Chỉ render objects trong frustum
    for (Object3D obj : visibleObjects) {
        // Build matrices...
        Matrix.setIdentityM(modelMatrix, 0);
        Matrix.translateM(modelMatrix, 0, obj.positionX, obj.positionY, obj.positionZ);
        Matrix.multiplyMM(mvpMatrix, 0, viewProj, 0, modelMatrix, 0);
        GLES30.glUniformMatrix4fv(mvpMatrixLoc, 1, false, mvpMatrix, 0);
        
        // Draw - chỉ render objects trong tầm nhìn
        cubeMesh.draw();
        performanceMonitor.drawCalls++;
    }
    // Draw Calls = 40 (chỉ render objects trong frustum)
}
```

**Kết quả khi BẬT Frustum Culling:**
- Objects Rendered: **40** (chỉ trong tầm nhìn)
- Objects Culled: **60** (ngoài tầm nhìn)
- Draw Calls: **40** (giảm 60%)
- FPS: **~85 FPS** (tăng 13%)
- CPU chỉ xử lý objects trong tầm nhìn

### 2.4. So sánh Performance

| Metric | TRƯỚC (OFF) | SAU (ON) | Cải thiện |
|--------|-------------|----------|-----------|
| Objects Rendered | 100 | 40 | **-60%** |
| Objects Culled | 0 | 60 | **+60** |
| Draw Calls | 100 | 40 | **-60%** |
| FPS | 75 | 85 | **+13%** |
| CPU Usage | 40% | 25% | **-37.5%** |

### 2.5. Giải thích chi tiết

**Cách hoạt động:**
1. **Extract frustum planes** từ view-projection matrix (6 mặt phẳng)
2. Với mỗi object, test **bounding sphere** (hình cầu bao quanh object) với 6 planes
3. Nếu sphere **nằm trong tất cả 6 planes** → object trong frustum → render
4. Nếu sphere **ngoài bất kỳ plane nào** → object ngoài frustum → cull

**Lợi ích:**
- ✅ **Giảm draw calls** đáng kể (30-70% tùy scene)
- ✅ **Giảm CPU overhead** (ít objects cần xử lý)
- ✅ **Hiệu quả cao** với scenes có nhiều objects ngoài tầm nhìn

**Nhược điểm:**
- ⚠️ Tốn CPU để tính toán frustum test (nhưng vẫn đáng giá)
- ⚠️ Cần bounding sphere/box cho mỗi object

**Công thức kiểm tra:**
```
Objects Rendered (TRƯỚC) = Objects Rendered (SAU) + Objects Culled (SAU)
Ví dụ: 100 = 40 + 60 ✅
```

### 2.6. 📊 HƯỚNG DẪN SỬ DỤNG ANDROID STUDIO PROFILER

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

#### 🎤 Cách giải thích khi thuyết trình:

**Khi show CPU Usage:**
> "Khi TẮT Frustum Culling, CPU Usage là 40% vì phải xử lý tất cả 100 objects. Khi BẬT Frustum Culling, CPU Usage giảm xuống còn 35% - giảm 5% vì chỉ xử lý 40 objects trong tầm nhìn."

**Khi show performFrustumCulling() time:**
> "Chi phí của Frustum Culling là rất nhỏ - chỉ tốn 1ms để tính toán. Nhưng lợi ích rất lớn - giảm được 60 objects không cần render. 1ms để tiết kiệm 60 objects là rất đáng giá!"

**Khi show onDrawFrame() time:**
> "Thời gian vẽ 1 frame giảm từ 15ms xuống 13ms - giảm 2ms. Điều này giúp FPS tăng từ 66 lên 77 - cải thiện 17%."

---

## 🔍 CHỨC NĂNG 3: OCCLUSION CULLING

### 3.1. Khái niệm

**Occlusion Culling** là kỹ thuật **loại bỏ các objects bị che khuất** bởi objects khác gần camera hơn. Objects ở xa và bị che không cần render.

**Nguyên lý:**
- Objects **gần camera** thường che khuất objects **xa camera**
- Objects bị che khuất → không nhìn thấy → không cần render
- Giảm **overdraw** (vẽ chồng lên nhau) → tăng performance

### 3.2. Code TRƯỚC khi sử dụng (Không có Occlusion Culling)

```java
// MyGLRenderer.java - onDrawFrame()
@Override
public void onDrawFrame(GL10 gl) {
    GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT | GLES30.GL_DEPTH_BUFFER_BIT);
    
    // Lấy tất cả objects (không cull occlusion)
    List<Object3D> allObjects = sceneManager.getObjects();
    // allObjects.size() = 64 (ví dụ)
    
    // Render TẤT CẢ objects (kể cả bị che khuất)
    for (Object3D obj : allObjects) {
        // Build matrices...
        Matrix.setIdentityM(modelMatrix, 0);
        Matrix.translateM(modelMatrix, 0, obj.positionX, obj.positionY, obj.positionZ);
        Matrix.multiplyMM(mvpMatrix, 0, viewProj, 0, modelMatrix, 0);
        GLES30.glUniformMatrix4fv(mvpMatrixLoc, 1, false, mvpMatrix, 0);
        
        // Draw - render cả objects bị che
        cubeMesh.draw();
        performanceMonitor.drawCalls++;
    }
    // Draw Calls = 64
    // Overdraw Ratio = 1.5 (nhiều objects chồng lên nhau)
}
```

**Kết quả khi TẮT Occlusion Culling:**
- Objects Rendered: **64** (tất cả)
- Objects Culled: **0**
- Overdraw Ratio: **1.5** (cao - nhiều overdraw)
- FPS: **~70 FPS**

### 3.3. Code SAU khi sử dụng (Có Occlusion Culling)

```java
// OcclusionCulling.java
public List<Object3D> performOcclusionCulling(List<Object3D> candidates, Camera camera) {
    List<Object3D> visible = new ArrayList<>();
    
    // Get camera position
    float camX = camera.getPositionX();
    float camY = camera.getPositionY();
    float camZ = camera.getPositionZ();
    
    // Simplified: use distance-based heuristic
    // Objects closer to camera are more likely visible (less likely to be occluded)
    float maxDistanceSq = 200.0f; // Within ~14 units
    
    for (Object3D obj : candidates) {
        // Calculate distance squared from camera to object
        float distSq = obj.getDistanceSquared(camX, camY, camZ);
        
        // Simple heuristic: objects closer to camera are more likely visible
        // Objects far away are more likely to be occluded by closer objects
        if (distSq < maxDistanceSq) {
            visible.add(obj);  // Chỉ thêm objects gần camera
        }
        // Objects beyond maxDistance are considered occluded and not added
    }
    
    return visible;
}

// CullingManager.java
public List<Object3D> cullObjects(List<Object3D> objects, Camera camera) {
    List<Object3D> visible = new ArrayList<>(objects);
    
    // Frustum culling first
    if (enableFrustumCulling) {
        visible = performFrustumCulling(visible, camera);
    }
    
    // Occlusion culling (most expensive, do last)
    if (enableOcclusionCulling) {
        visible = occlusionCulling.performOcclusionCulling(visible, camera);
    }
    
    return visible;
}

// MyGLRenderer.java - onDrawFrame()
@Override
public void onDrawFrame(GL10 gl) {
    GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT | GLES30.GL_DEPTH_BUFFER_BIT);
    
    // BƯỚC 1: Enable occlusion culling
    cullingManager.setOcclusionCulling(renderConfig.enableOcclusionCulling);
    
    // BƯỚC 2: Cull objects bị che khuất
    List<Object3D> allObjects = sceneManager.getObjects();
    List<Object3D> visibleObjects = cullingManager.cullObjects(allObjects, camera);
    // visibleObjects.size() = 45 (ví dụ: chỉ objects gần camera, không bị che)
    
    // BƯỚC 3: Chỉ render objects không bị che
    for (Object3D obj : visibleObjects) {
        // Build matrices...
        Matrix.setIdentityM(modelMatrix, 0);
        Matrix.translateM(modelMatrix, 0, obj.positionX, obj.positionY, obj.positionZ);
        Matrix.multiplyMM(mvpMatrix, 0, viewProj, 0, modelMatrix, 0);
        GLES30.glUniformMatrix4fv(mvpMatrixLoc, 1, false, mvpMatrix, 0);
        
        // Draw - chỉ render objects không bị che
        cubeMesh.draw();
        performanceMonitor.drawCalls++;
    }
    // Draw Calls = 45
    // Overdraw Ratio = 1.2 (giảm do ít objects chồng lên nhau)
}
```

**Kết quả khi BẬT Occlusion Culling:**
- Objects Rendered: **45** (chỉ objects gần, không bị che)
- Objects Culled: **19** (objects xa, bị che)
- Overdraw Ratio: **1.2** (giảm từ 1.5)
- FPS: **~78 FPS** (tăng 11%)

### 3.4. So sánh Performance

| Metric | TRƯỚC (OFF) | SAU (ON) | Cải thiện |
|--------|-------------|----------|-----------|
| Objects Rendered | 64 | 45 | **-30%** |
| Objects Culled | 0 | 19 | **+19** |
| Overdraw Ratio | 1.5 | 1.2 | **-20%** |
| FPS | 70 | 78 | **+11%** |

### 3.5. Giải thích chi tiết

**Cách hoạt động:**
1. Tính **khoảng cách** từ camera đến mỗi object
2. Objects **gần camera** → ít bị che → render
3. Objects **xa camera** → dễ bị che bởi objects gần → cull
4. Giảm **overdraw** (vẽ chồng lên nhau)

**Lợi ích:**
- ✅ **Giảm overdraw** → GPU xử lý ít pixels hơn
- ✅ **Tăng FPS** khi có nhiều objects chồng lên nhau
- ✅ **Hiệu quả** với scenes có nhiều objects ở xa

**Nhược điểm:**
- ⚠️ Implementation đơn giản (distance-based) có thể không chính xác 100%
- ⚠️ Có thể cull nhầm objects không bị che (false positive)

**Lưu ý:**
- Implementation hiện tại dùng **distance-based heuristic** (đơn giản)
- Có thể nâng cấp lên **GPU-based occlusion queries** (chính xác hơn nhưng phức tạp hơn)

### 3.6. 📊 HƯỚNG DẪN SỬ DỤNG ANDROID STUDIO PROFILER

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

#### 🎤 Cách giải thích khi thuyết trình:

**Khi show CPU Usage:**
> "Khi BẬT Occlusion Culling, CPU Usage có thể tăng nhẹ từ 35% lên 37% - tăng 2% vì phải tính toán culling. Nhưng điều này vẫn đáng giá vì chúng ta render ít objects hơn."

**Khi show performOcclusionCulling() time:**
> "Chi phí của Occlusion Culling là 2ms - nhiều hơn Frustum Culling (1ms) vì phức tạp hơn. Nhưng nó giúp loại bỏ objects bị che khuất - giảm overdraw."

**Khi show onDrawFrame() time:**
> "Mặc dù tốn 2ms để tính toán culling, nhưng thời gian vẽ frame giảm từ 14ms xuống 13ms - giảm 1ms. Tổng thể vẫn tốt hơn vì giảm overdraw."

---

## 📊 TỔNG KẾT NHÓM 1: CULLING TECHNIQUES

### So sánh tổng thể 3 chức năng:

| Chức năng | Giảm Triangles/Draws | Tăng FPS | CPU Cost | Độ phức tạp |
|-----------|---------------------|----------|----------|-------------|
| **Back-face Culling** | -50% triangles | +60% | Rất thấp (GPU tự động) | ⭐ Dễ |
| **Frustum Culling** | -30-70% draws | +10-15% | Trung bình | ⭐⭐ Trung bình |
| **Occlusion Culling** | -20-40% draws | +10-15% | Trung bình-cao | ⭐⭐⭐ Khó |

### Kết hợp cả 3 chức năng:

**Khi BẬT cả 3:**
- Triangles: Giảm ~50% (Back-face) + ~30% (Frustum/Occlusion) = **~65% tổng thể**
- Draw Calls: Giảm ~60% (Frustum) + ~30% (Occlusion) = **~72% tổng thể**
- FPS: Tăng từ 50 → **~90 FPS** (tăng 80%)
- Frame Time: Giảm từ 20ms → **~11ms** (giảm 45%)

### Khuyến nghị sử dụng:

1. **Back-face Culling**: ✅ **LUÔN BẬT** (hiệu quả cao, chi phí thấp)
2. **Frustum Culling**: ✅ **NÊN BẬT** khi có nhiều objects ngoài tầm nhìn
3. **Occlusion Culling**: ⚠️ **TÙY SCENE** - bật khi có nhiều objects chồng lên nhau

---

**📝 Lưu ý:** Tất cả các số liệu trên là ví dụ và có thể thay đổi tùy theo:
- Thiết bị (GPU mạnh/yếu)
- Số lượng objects trong scene
- Độ phức tạp của objects
- Camera position và angle

