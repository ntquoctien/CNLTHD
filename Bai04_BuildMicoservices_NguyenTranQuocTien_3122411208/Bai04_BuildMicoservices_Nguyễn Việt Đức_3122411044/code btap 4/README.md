# Microservices Starter (Product, Inventory, Order, Discovery)

## 1) Mục tiêu hệ thống

Hệ thống gồm 4 service:
- `discovery-server` (Eureka)
- `product-service` (MongoDB)
- `inventory-service` (PostgreSQL)
- `order-service` (PostgreSQL + gọi Inventory + Kafka + Resilience4j)

Infra đi kèm:
- MongoDB, PostgreSQL (2 DB), Zookeeper, Kafka, Zipkin

## 2) Yêu cầu môi trường

Cần cài đặt:
- Docker Desktop
- Docker Compose plugin (`docker compose`)

Kiểm tra nhanh:
```powershell
docker --version
docker compose version
```

## 3) Chạy toàn hệ thống

Tại thư mục gốc project (`D:\bài tập 4`):
```powershell
docker compose up -d --build
```

Theo dõi log live:
```powershell
docker compose logs -f
```

Dừng hệ thống:
```powershell
docker compose down
```

Dừng và xóa volume (nếu cần reset dữ liệu):
```powershell
docker compose down -v
```

## 4) Kiểm tra service đã lên

Mở các URL:
- Discovery dashboard: http://localhost:8761
- Product health: http://localhost:8081/actuator/health
- Inventory health: http://localhost:8082/actuator/health
- Order health: http://localhost:8083/actuator/health
- Zipkin: http://localhost:9411

Nếu gặp `ERR_CONNECTION_REFUSED`, chạy:
```powershell
docker compose ps
docker compose logs discovery-server
docker compose logs product-service
docker compose logs inventory-service
docker compose logs order-service
```

## 5) API demo nhanh (PowerShell)

### 5.1 Tạo product
```powershell
$body = @{
  name = "iPhone 13"
  description = "iPhone 13"
  price = 1200
} | ConvertTo-Json

Invoke-RestMethod -Method Post `
  -Uri "http://localhost:8081/api/product" `
  -ContentType "application/json" `
  -Body $body
```

### 5.2 Lấy danh sách product
```powershell
Invoke-RestMethod -Method Get -Uri "http://localhost:8081/api/product"
```

### 5.3 Check inventory
```powershell
Invoke-RestMethod -Method Get -Uri "http://localhost:8082/api/inventory?skuCode=iphone_13&skuCode=iphone_13_red"
```

Kỳ vọng:
- `iphone_13` -> `isInStock: true`
- `iphone_13_red` -> `isInStock: false`

### 5.4 Đặt order (case thành công)
```powershell
$orderBody = @{
  orderLineItemsDtoList = @(
    @{
      skuCode = "iphone_13"
      price = 1200
      quantity = 1
    }
  )
} | ConvertTo-Json -Depth 5

Invoke-RestMethod -Method Post `
  -Uri "http://localhost:8083/api/order" `
  -ContentType "application/json" `
  -Body $orderBody
```

Kỳ vọng response: `Order Placed`

### 5.5 Đặt order (case hết hàng)
```powershell
$orderBodyOutOfStock = @{
  orderLineItemsDtoList = @(
    @{
      skuCode = "iphone_13_red"
      price = 1200
      quantity = 1
    }
  )
} | ConvertTo-Json -Depth 5

try {
  Invoke-RestMethod -Method Post `
    -Uri "http://localhost:8083/api/order" `
    -ContentType "application/json" `
    -Body $orderBodyOutOfStock
} catch {
  $_.Exception.Response.StatusCode.value__
  $_.ErrorDetails.Message
}
```

Kỳ vọng:
- HTTP `400`
- message: `Product is not in stock, please try again later`

## 6) Checklist nộp bài

- [ ] Discovery dashboard thấy các service đăng ký (product, inventory, order)
- [ ] Product POST/GET chạy đúng
- [ ] Inventory API trả đúng in-stock/out-of-stock
- [ ] Order thành công khi đủ hàng
- [ ] Order trả lỗi 400 khi hết hàng
- [ ] Có metrics endpoint `/actuator/prometheus`
- [ ] Zipkin nhận trace khi gọi API

## 7) Troubleshooting nhanh

1. `ERR_CONNECTION_REFUSED`
- Service chưa start xong hoặc crash
- Kiểm tra bằng `docker compose ps` và `docker compose logs <service-name>`

2. Service crash do DB/Kafka chưa sẵn sàng
- Chạy lại:
```powershell
docker compose down
docker compose up -d --build
```

3. Muốn reset dữ liệu test
- Chạy:
```powershell
docker compose down -v
docker compose up -d --build
```
