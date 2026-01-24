import { CartItem } from './cart-item.model';

export interface Cart {
  items: CartItem[];
  subtotal: number;
  tax?: number;
  shipping?: number;
  total: number;
}
