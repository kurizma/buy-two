export interface SellerMostSold {
  productId: string;
  name: string;
  totalQty: number;
  totalRevenue?: number;
}

export interface SellerTopCategory {
  category: string;
  totalRevenue: number;
}
