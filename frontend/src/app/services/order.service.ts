import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';
import { delay } from 'rxjs/operators';
import { Order } from '../models/order/order.model';

@Injectable({ providedIn: 'root' })
export class OrderService {
  getOrder(id: string): Observable<Order> {
    const key = `order_${id}`; // Must match!
    console.log('🔍 Looking for:', key); // Debug

    const data = localStorage.getItem(`order_${id}`);
    return of(data ? JSON.parse(data) : (null as any)).pipe(delay(100));
  }
}
