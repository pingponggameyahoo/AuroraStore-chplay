Có, nhưng “API crawl book” thì không có một chuẩn chung kiểu như Google Play đâu — tùy nguồn sách mà bạn phải dùng cách khác nhau. Có mấy hướng phổ biến:

**1. Google Books API (dễ dùng nhất)**
- API chính thức của Google
- Cho phép tìm sách, metadata (title, author, description, ISBN…)
- Có preview link (một số sách xem được một phần)

Endpoint ví dụ:
```
https://www.googleapis.com/books/v1/volumes?q=harry+potter
```

**2. Open Library API (miễn phí, open source)**
- Dữ liệu lớn, free hoàn toàn
- Có thông tin sách, tác giả, cover

Ví dụ:
```
https://openlibrary.org/search.json?q=harry+potter
```

**3. Goodreads (không còn public API chính thức)**
- Trước có API, giờ bị đóng
- Muốn crawl thì phải scrape → dễ dính rate limit / block

**4. Amazon Books**
- Không có API public cho book data
- Có Amazon Product Advertising API nhưng phải đăng ký affiliate
- Scrape thì khá khó vì chống bot mạnh

**5. Các nguồn khác**
- ISBNdb API (có free tier)
- Gutendex API (Project Gutenberg – sách public domain)
```
https://gutendex.com/books
```

---

**Nếu mục tiêu của bạn là:**

- Chỉ lấy metadata sách → dùng Google Books hoặc Open Library là đủ
- Lấy full nội dung sách → gần như chỉ có:
  - Project Gutenberg (public domain)
  - hoặc nguồn riêng (có license)

---

**Lưu ý quan trọng:**
- Crawl nội dung sách bản quyền là vấn đề pháp lý
- API chính thống thường chỉ cho metadata + preview, không cho full text

---

Nếu bạn nói rõ hơn bạn muốn crawl kiểu gì (metadata, giá, review hay full nội dung), mình có thể đề xuất stack cụ thể hơn cho bạn.