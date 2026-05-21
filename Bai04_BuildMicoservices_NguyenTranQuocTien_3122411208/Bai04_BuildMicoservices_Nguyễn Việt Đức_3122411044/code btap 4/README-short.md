# Quick Demo - Microservices Starter

## 1) Start hệ thống
```powershell
docker compose up -d --build
```

## 2) Check nhanh service
- Discovery: http://localhost:8761
- Product health: http://localhost:8081/actuator/health
- Inventory health: http://localhost:8082/actuator/health
- Order health: http://localhost:8083/actuator/health
- Zipkin: http://localhost:9411

Nếu lỗi kết nối:
```powershell
docker compose ps
docker compose logs order-service
docker compose logs inventory-service
docker compose logs product-service
docker compose logs discovery-server
```

## 3) Demo API

### Tạo product
```powershell
$body = @{name="iPhone 13"; description="iPhone 13"; price=1200} | ConvertTo-Json
Invoke-RestMethod -Method Post -Uri "http://localhost:8081/api/product" -ContentType "application/json" -Body $body
```

### Lấy product
```powershell
Invoke-RestMethod -Method Get -Uri "http://localhost:8081/api/product"
```

### Check inventory
```powershell
Invoke-RestMethod -Method Get -Uri "http://localhost:8082/api/inventory?skuCode=iphone_13&skuCode=iphone_13_red"
```

### Đặt order thành công
```powershell
$orderBody = @{orderLineItemsDtoList=@(@{skuCode="iphone_13"; price=1200; quantity=1})} | ConvertTo-Json -Depth 5
Invoke-RestMethod -Method Post -Uri "http://localhost:8083/api/order" -ContentType "application/json" -Body $orderBody
```

### Đặt order hết hàng (kỳ vọng HTTP 400)
```powershell
$orderBodyOut = @{orderLineItemsDtoList=@(@{skuCode="iphone_13_red"; price=1200; quantity=1})} | ConvertTo-Json -Depth 5
try {
  Invoke-RestMethod -Method Post -Uri "http://localhost:8083/api/order" -ContentType "application/json" -Body $orderBodyOut
} catch {
  $_.Exception.Response.StatusCode.value__
  $_.ErrorDetails.Message
}
```

## 4) Stop
```powershell
docker compose down
```
