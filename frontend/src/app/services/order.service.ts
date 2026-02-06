import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { map, catchError } from 'rxjs/operators';
import { Order } from '../models/order/order.model';
import { CreateOrderRequest } from '../models/order/createOrderRequest.model';
import { environment } from '../../environments/environment.docker';

@Injectable({
  providedIn: 'root',
})
export class OrderService {
  private readonly baseUrl = `${environment.apiBaseUrl}/api/orders`;
  private readonly http = inject(HttpClient);

  // ✅ POST /api/orders/checkout
  createOrder(req: CreateOrderRequest): Observable<{ success: boolean; data: Order }> {
    return this.http
      .post<{ success: boolean; message: string; data: Order }>(`${this.baseUrl}/checkout`, req)
      .pipe(
        catchError((err) => throwError(() => err)), // ✅ Basic error handling
      );
  }

  // ✅ GET /api/orders/buyer (interceptor adds headers)
  getMyOrders(): Observable<Order[]> {
    return this.http
      .get<{ success: boolean; data: Order[] }>(
        `${this.baseUrl}/buyer`,
        // No headers needed - interceptor handles!
      )
      .pipe(map((res) => res.data));
  }

  // ✅ GET /api/orders/{orderNumber}
  getOrderDetail(orderNumber: string): Observable<Order> {
    return this.http
      .get<{ success: boolean; data: Order }>(`${this.baseUrl}/${orderNumber}`)
      .pipe(map((res) => res.data));
  }
}

// getOrder(id: string): Observable<Order> {
//   const key = `order_${id}`; // Must match!
//   console.log('🔍 Looking for:', key); // Debug

//   const data = localStorage.getItem(`order_${id}`);
//   return of(data ? JSON.parse(data) : (null as any)).pipe(delay(100));
// }
