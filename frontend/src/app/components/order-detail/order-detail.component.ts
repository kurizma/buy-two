import { Component, OnInit, inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatChipsModule } from '@angular/material/chips';
import { Order, OrderStatus } from '../../models/order/order.model';
import { OrderService } from '../../services/order.service';

@Component({
  selector: 'app-order-detail',
  standalone: true,
  imports: [CommonModule, MatCardModule, MatButtonModule, MatIconModule, MatChipsModule],
  templateUrl: './order-detail.component.html',
  styleUrls: ['./order-detail.component.css'],
})
export class OrderDetailComponent implements OnInit {
  order: Order | null = null;
  loading = true;
  error = false;

  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private orderService = inject(OrderService);

  // For displaying status colors
  OrderStatus = OrderStatus;

  ngOnInit(): void {
    const orderId = this.route.snapshot.paramMap.get('id');

    if (!orderId) {
      this.error = true;
      this.loading = false;
      return;
    }

    // Load order from localStorage via service
    this.orderService.getOrder(orderId).subscribe({
      next: (order) => {
        this.order = order;
        this.loading = false;
        this.error = !order;
      },
      error: () => {
        this.error = true;
        this.loading = false;
      },
    });
  }

  getStatusClass(status: OrderStatus): string {
    switch (status) {
      case OrderStatus.PENDING:
        return 'status-pending';
      case OrderStatus.CONFIRMED:
        return 'status-confirmed';
      case OrderStatus.SHIPPED:
        return 'status-shipped';
      case OrderStatus.DELIVERED:
        return 'status-delivered';
      case OrderStatus.CANCELLED:
        return 'status-cancelled';
      default:
        return '';
    }
  }

  // Helper to show which statuses are completed
  isStatusCompleted(checkStatus: OrderStatus): boolean {
    if (!this.order) return false;

    const statusOrder = [
      OrderStatus.PENDING,
      OrderStatus.CONFIRMED,
      OrderStatus.SHIPPED,
      OrderStatus.DELIVERED,
    ];

    const currentIndex = statusOrder.indexOf(this.order.status);
    const checkIndex = statusOrder.indexOf(checkStatus);

    return checkIndex <= currentIndex;
  }

  goToOrders(): void {
    this.router.navigate(['/order-list']);
  }

  continueShopping(): void {
    this.router.navigate(['/']);
  }
}
