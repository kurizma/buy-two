export interface OrderItemResponse {
  productName: string;
  sellerId: string;
  price: number;
  quantity: number;
  sellerName?: string;
  imageUrl?: string;
}
