import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, throwError, of } from 'rxjs';
import { map, catchError } from 'rxjs/operators';
import { Order, OrderStatus } from '../models/order/order.model';
import { CreateOrderRequest } from '../models/order/createOrderRequest.model';
import { environment } from '../../environments/environment.docker';
import { AuthService } from './auth.service';
import { ApiResponse } from '../models/api-response/api-response.model';

@Injectable({
  providedIn: 'root',
})
export class OrderService {
  private readonly baseUrl = `${environment.apiBaseUrl}/api/orders`;
  private readonly http = inject(HttpClient);
  private readonly authService = inject(AuthService);

  // ✅ POST /api/orders/checkout
  createOrder(req: CreateOrderRequest): Observable<{ success: boolean; data: Order }> {
    return this.http
      .post<{ success: boolean; message: string; data: Order }>(`${this.baseUrl}/checkout`, req)
      .pipe(
        catchError((err) => throwError(() => err)), // ✅ Basic error handling
      );
  }

  // ✅ GET /api/orders/{orderNumber}
  getOrder(orderNumber: string): Observable<Order | null> {
    console.log('🌐 Fetching order from API:', orderNumber);
    return this.http.get<{ success: boolean; data: Order }>(`${this.baseUrl}/${orderNumber}`).pipe(
      map((res) => res.data),
      catchError((err) => {
        console.error('❌ Error fetching order:', err);
        return of(null); // Return null on error to handle gracefully in UI
      }),
    );
  }

  // ✅ GET /api/orders/seller or /api/orders/buyer
  getMyOrders(): Observable<Order[]> {
    const endpoint = this.authService.isSeller()
      ? `${this.baseUrl}/seller`
      : `${this.baseUrl}/buyer`;

    return this.http.get<ApiResponse<any>>(endpoint).pipe(
      map((res) => {
        if (!res.success || !res.data) {
          return [];
        }
        if (this.authService.isSeller() && res.data?.content) {
          return res.data.content; // Page.content = Order[]
        }
        return res.data || []; // Buyer = direct array
      }),
      catchError((err) => {
        console.error('❌ Error fetching orders:', err);
        return of([]); // Return empty array on error
      }),
    );
  }

  // ✅ GET /api/orders/{orderNumber}/cancel
  cancelOrder(orderNumber: string): Observable<any> {
    return this.http.post(`${this.baseUrl}/${orderNumber}/cancel`, {});
  }

  // ✅ GET /api/orders/{orderNumber}/redo
  redoOrder(orderNumber: string): Observable<any> {
    return this.http.post(`${this.baseUrl}/${orderNumber}/redo`, {});
  }

  // ✅ POST /api/orders/{orderNumber}/confirm
  // confirmOrder(orderNumber: string): Observable<any> {
  //   const currentUser = this.authService.currentUserValue;
  //   const userId = currentUser?.id || '';
  //   const headers = {
  //     'X-USER-ID': userId,
  //     'X-USER-ROLE': this.authService.isSeller() ? 'SELLER' : 'CLIENT',
  //   };
  //   return this.http.post(`${this.baseUrl}/${orderNumber}/confirm`, {}, { headers });
  // }

  // ✅ PUT /api/orders/{orderNumber}/status
  updateStatus(orderNumber: string, status: OrderStatus): Observable<Order> {
    const currentUser = this.authService.currentUserValue;
    const userId = currentUser?.id || '';
    const headers = {
      'X-USER-ID': userId,
      'X-USER-ROLE': this.authService.isSeller() ? 'SELLER' : 'CLIENT',
    };
    return this.http.put<Order>(
      `${this.baseUrl}/${orderNumber}/status?status=${status}`,
      {},
      { headers },
    );
  }
}
