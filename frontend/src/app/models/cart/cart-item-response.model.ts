export interface CartItemResponse {
  productId: string;
  sellerId: string;
  productName: string;
  price: number;
  quantity: number;
  imageUrl: string;
  categoryId?: string;
}
