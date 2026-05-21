// Minh họa DTO kiểm soát dữ liệu đầu vào khi tạo sản phẩm

export class CreateProductDto {
  name!: string;
  description?: string;
  price!: number;
  stock!: number;
  imageUrl?: string;
}
