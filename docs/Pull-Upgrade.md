Bạn đang muốn **fork nhưng vẫn “bám” upstream sạch sẽ** — làm đúng cách thì hoàn toàn ổn.

Cách chuẩn (dev nào làm open-source cũng dùng):

---

## 1. Fork repo Aurora Store
Trên GitHub → bấm **Fork** về repo của bạn

---

## 2. Clone repo của bạn
```bash
git clone https://github.com/<your-user>/AuroraStore.git
cd AuroraStore
```

---

## 3. Thêm upstream (repo gốc)
```bash
git remote add upstream https://github.com/auroraoss/AuroraStore.git
```

Kiểm tra:
```bash
git remote -v
```

Bạn sẽ thấy:
- origin → repo của bạn
- upstream → repo Aurora gốc

---

## 4. Tạo branch riêng để sửa UI
Đừng sửa trực tiếp `main`

```bash
git checkout -b custom-ui
```

→ Bạn sửa giao diện thoải mái ở đây

---

## 5. Khi Aurora update → kéo về

```bash
git fetch upstream
git checkout main
git merge upstream/main
```

hoặc dùng rebase (sạch hơn):
```bash
git rebase upstream/main
```

---

## 6. Merge vào branch của bạn

```bash
git checkout custom-ui
git merge main
```

---

## 7. Resolve conflict (chỗ bạn sửa UI)
- Đây là chỗ quan trọng nhất
- Nếu bạn sửa UI nhiều → sẽ conflict thường xuyên

---

## Tips để đỡ “toang” khi merge

### ✅ 1. Tách UI ra rõ ràng
- Đừng sửa logic core
- Chỉ override:
  - layout XML / Compose UI
  - theme
  - colors
  - typography

---

### ✅ 2. Tránh sửa file “core”
Ví dụ:
- API client
- auth
- download manager

→ vì upstream hay đổi → conflict cực nhiều

---

### ✅ 3. Nếu dùng Jetpack Compose
Aurora Store mới dùng Compose khá nhiều:

Bạn có thể:
- giữ logic ViewModel
- viết lại UI layer riêng

→ merge sẽ nhẹ hơn

---

### ✅ 4. Giữ commit sạch
- 1 commit = 1 thay đổi rõ ràng
- sau này rebase đỡ đau đầu

---

## Option nâng cao (pro hơn)

### 🔥 Cách ít conflict nhất:
Thay vì sửa trực tiếp Aurora:

👉 tạo module riêng:
- import Aurora như dependency (hoặc submodule)
- build UI wrapper bên ngoài

Nhưng:
- khó hơn
- cần hiểu sâu codebase

---

## Tóm lại

Cách bạn nên làm:

- fork repo
- thêm upstream
- làm việc trên branch riêng
- định kỳ merge/rebase từ upstream
- chỉ đụng vào UI layer

---

Nếu bạn muốn, mình có thể:
- chỉ luôn **file nào trong Aurora Store nên sửa để đổi UI**
- hoặc vẽ cho bạn **kiến trúc UI của Aurora (Compose + ViewModel)** để bạn hack nhanh hơn

Bạn đang định sửa kiểu:
- chỉ theme (màu, font)
- hay redesign toàn bộ layout giống Play Store?