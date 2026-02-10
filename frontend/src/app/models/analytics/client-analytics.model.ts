export interface ClientMostBought {
  productId: string;
  name: string;
  totalQty: number;
  totalAmount?: number; // Optional: if backend adds it
}

export interface ClientTopCategory {
  category: string;
  totalSpent: number;
}
