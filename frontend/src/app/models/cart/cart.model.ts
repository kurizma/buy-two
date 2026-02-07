import { CartItem } from './cart-item.model';

export interface Cart {
  items: CartItem[];
  subtotal: number;
  tax?: number;
  shippingCost?: number;
  total: number;
}
