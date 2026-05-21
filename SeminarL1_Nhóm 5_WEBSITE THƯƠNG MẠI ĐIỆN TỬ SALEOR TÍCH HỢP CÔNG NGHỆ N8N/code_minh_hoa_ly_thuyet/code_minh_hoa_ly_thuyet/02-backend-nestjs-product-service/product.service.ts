// Minh họa Service trong NestJS: xử lý nghiệp vụ sản phẩm

import { Injectable, NotFoundException } from '@nestjs/common';
import { InjectRepository } from '@nestjs/typeorm';
import { Repository } from 'typeorm';
import { ProductEntity } from './product.entity';
import { CreateProductDto } from './create-product.dto';

@Injectable()
export class ProductService {
  constructor(
    @InjectRepository(ProductEntity)
    private readonly productRepository: Repository<ProductEntity>,
  ) {}

  async create(data: CreateProductDto): Promise<ProductEntity> {
    const product = this.productRepository.create(data);
    return this.productRepository.save(product);
  }

  async findAll(): Promise<ProductEntity[]> {
    return this.productRepository.find({ order: { name: 'ASC' } });
  }

  async findOne(id: string): Promise<ProductEntity> {
    const product = await this.productRepository.findOne({ where: { id } });
    if (!product) {
      throw new NotFoundException('Không tìm thấy sản phẩm');
    }
    return product;
  }
}
