export interface AnalyticsItem {
  productId?: string;
  name: string;
  count: number; //(totalQty or unitsSold)
  amount?: number; //(totalAmount or revenue)
  categories: string[];
}
