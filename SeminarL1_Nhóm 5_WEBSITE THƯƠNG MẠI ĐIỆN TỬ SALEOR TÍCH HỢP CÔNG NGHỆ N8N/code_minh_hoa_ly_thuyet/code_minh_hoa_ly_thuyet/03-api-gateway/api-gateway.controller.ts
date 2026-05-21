// Minh họa API Gateway: nhận request từ frontend và chuyển tiếp đến microservice phù hợp
// Có thể đặt trong microservices/api-gateway/src/api-gateway.controller.ts

import { Controller, Get, Headers, Param, Post, Body } from '@nestjs/common';
import { HttpService } from '@nestjs/axios';
import { firstValueFrom } from 'rxjs';

@Controller('api/v1')
export class ApiGatewayController {
  private readonly productServiceUrl = process.env.PRODUCT_SERVICE_URL ?? 'http://product-service:3001';
  private readonly cartServiceUrl = process.env.CART_SERVICE_URL ?? 'http://cart-service:3007';
  private readonly orderServiceUrl = process.env.ORDER_SERVICE_URL ?? 'http://order-service:3004';

  constructor(private readonly httpService: HttpService) {}

  @Get('products')
  async getProducts() {
    const response = await firstValueFrom(
      this.httpService.get(`${this.productServiceUrl}/api/v1/products`),
    );
    return response.data;
  }

  @Get('products/:id')
  async getProductDetail(@Param('id') id: string) {
    const response = await firstValueFrom(
      this.httpService.get(`${this.productServiceUrl}/api/v1/products/${id}`),
    );
    return response.data;
  }

  @Post('cart/items')
  async addToCart(@Headers('authorization') token: string, @Body() body: unknown) {
    const response = await firstValueFrom(
      this.httpService.post(`${this.cartServiceUrl}/api/v1/cart/items`, body, {
        headers: { authorization: token },
      }),
    );
    return response.data;
  }

  @Post('checkout')
  async checkout(@Headers('authorization') token: string, @Body() body: unknown) {
    const response = await firstValueFrom(
      this.httpService.post(`${this.orderServiceUrl}/api/v1/orders`, body, {
        headers: { authorization: token },
      }),
    );
    return response.data;
  }
}
