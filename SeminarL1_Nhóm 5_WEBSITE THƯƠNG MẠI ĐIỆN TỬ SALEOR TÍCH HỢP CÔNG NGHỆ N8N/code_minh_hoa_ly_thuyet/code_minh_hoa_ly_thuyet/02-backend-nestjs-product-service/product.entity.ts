// Minh họa Entity trong Product Service dùng TypeORM
// Có thể đặt trong microservices/product-service/src/products/product.entity.ts

import { Column, Entity, PrimaryGeneratedColumn } from 'typeorm';

@Entity('products')
export class ProductEntity {
  @PrimaryGeneratedColumn('uuid')
  id!: string;

  @Column({ length: 255 })
  name!: string;

  @Column({ type: 'text', nullable: true })
  description?: string;

  @Column({ type: 'int' })
  price!: number;

  @Column({ type: 'int', default: 0 })
  stock!: number;

  @Column({ nullable: true })
  imageUrl?: string;
}
