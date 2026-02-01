export interface CartItem {
  id: string;
  productId: string;
  productName: string;
  productDescription?: string;
  price: number;
  quantity: number;
  imageUrl: string;
  sellerId: string;
  sellerName: string;
  sellerAvatarUrl?: string;
  categoryId: string;
}
