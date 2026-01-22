export interface CartItem {
  productId: string;
  productName: string;
  sellerId: string;
  price: number;
  quantity: number;
  categoryId: string;
  imageUrl?: string;
}
