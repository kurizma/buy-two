import { CartItem } from './cart-item.model';

export interface CartResponse {
  id: string;
  userId: string;
  items: CartItem[];
  subtotal: number;
  tax: number;
  total: number;
  updatedAt: string;
}
