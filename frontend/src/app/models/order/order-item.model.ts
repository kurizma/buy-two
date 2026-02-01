export interface OrderItem {
  productId: string;
  productName: string;
  sellerId: string;
  sellerName: string;
  price: number;
  quantity: number;
  imageUrl?: string;
  categoryId?: string;
}
