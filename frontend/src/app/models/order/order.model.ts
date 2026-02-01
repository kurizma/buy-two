import { OrderItem } from './order-item.model';

export enum OrderStatus {
  PENDING = 'PENDING',
  CONFIRMED = 'CONFIRMED',
  SHIPPED = 'SHIPPED',
  DELIVERED = 'DELIVERED',
  CANCELLED = 'CANCELLED',
}

export enum PaymentMethod {
  PAY_ON_DELIVERY = 'PAY_ON_DELIVERY',
}

export interface Address {
  firstname?: string;
  lastname?: string;
  street?: string;
  city?: string;
  zip?: string;
  country?: string;
  phone?: string;
}

export interface Order {
  id: string;
  userId: string;
  orderNumber: string;
  items: OrderItem[];
  status: OrderStatus;
  paymentMethod: PaymentMethod;
  shippingAddress?: Address;
  subtotal?: number;
  tax?: number;
  total?: number;
  createdAt?: string;
  updatedAt?: string;
}
