export interface AddCartItemRequest {
  productId: string;
  productName: string;
  sellerId: string;
  price: number;
  categoryId: string;
  imageUrl?: string;
  productDescription?: string;
  availableStock?: number;
  sellerName: string;
  sellerAvatarUrl?: string;
}
