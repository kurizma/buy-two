import { Component, OnInit, inject } from '@angular/core';
import { Router, RouterLink, ActivatedRoute } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule, FormControl, ReactiveFormsModule } from '@angular/forms';
import { debounceTime, distinctUntilChanged, catchError, switchMap } from 'rxjs/operators';
import { of } from 'rxjs';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatChipsModule } from '@angular/material/chips';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core';
import { MatSnackBarModule, MatSnackBar } from '@angular/material/snack-bar';
import { Order, OrderStatus } from '../../models/order/order.model';
import { AuthService } from '../../services/auth.service';
import { CartService } from '../../services/cart.service';
import { OrderService } from '../../services/order.service';

@Component({
  selector: 'app-order-list',
  standalone: true,
  imports: [
    CommonModule,
    RouterLink,
    FormsModule,
    ReactiveFormsModule,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatChipsModule,
    MatDatepickerModule,
    MatNativeDateModule,
    MatSnackBarModule,
  ],
  templateUrl: './order-list.component.html',
  styleUrls: ['./order-list.component.css'],
})
export class OrderListComponent implements OnInit {
  orders: Order[] = [];
  filteredOrders: Order[] = [];
  loading = false;

  // Filters
  searchCtrl = new FormControl('');
  statusFilter = new FormControl<OrderStatus | 'ALL'>('ALL');
  startDate: Date | null = null;
  endDate: Date | null = null;

  OrderStatus = OrderStatus;
  statusOptions = ['ALL', ...Object.values(OrderStatus)];

  private router = inject(Router);
  private route = inject(ActivatedRoute);
  private orderService = inject(OrderService);
  private authService = inject(AuthService);
  private cartService = inject(CartService);
  private snackBar = inject(MatSnackBar);

  // User context
  isSeller = false;
  currentUserId = '';

  ngOnInit(): void {
    this.currentUserId = this.authService.getUserId() || '';
    this.isSeller = this.authService.isSeller();

    console.log('User:', this.currentUserId, 'Seller?', this.isSeller);

    this.loadOrders();

    // Filter subscriptions
    this.searchCtrl.valueChanges
      .pipe(debounceTime(300), distinctUntilChanged())
      .subscribe(() => this.applyFilters());

    this.statusFilter.valueChanges.subscribe(() => this.applyFilters());
  }

  loadOrders(): void {
    this.loading = true;

    this.orderService
      .getMyOrders()
      .pipe(catchError(() => of([])))
      .subscribe({
        next: (orders) => {
          this.orders = orders;
          this.applyFilters();
          this.loading = false;
          console.log('✅ Orders loaded:', orders.length);
        },
        error: (err) => {
          console.error('Load orders failed:', err);
          this.snackBar.open('Failed to load orders', 'Retry', { duration: 3000 });
          this.loading = false;
        },
      });
  }

  applyFilters(): void {
    this.filteredOrders = this.orders.filter((order) => {
      // Search
      const search = this.searchCtrl.value?.toLowerCase() || '';
      const matchesSearch =
        order.orderNumber.toLowerCase().includes(search) ||
        order.items.some((item) => item.productName.toLowerCase().includes(search));

      // Status
      const statusFilter = this.statusFilter.value;
      const matchesStatus = statusFilter === 'ALL' || order.status === statusFilter;

      // Seller filter (buyer sees all, seller sees their items only)
      if (this.isSeller) {
        const hasSellerItems = order.items.some((item) => item.sellerId === this.currentUserId);
        if (!hasSellerItems) return false;
      }

      // Date range
      const matchesDate = this.dateInRange(order.createdAt);

      return matchesSearch && matchesStatus && matchesDate;
    });
  }

  private dateInRange(dateStr: string | undefined): boolean {
    if (!dateStr) return true;
    const orderDate = new Date(dateStr);
    if (this.startDate && orderDate < this.startDate) return false;
    if (this.endDate) {
      const end = new Date(this.endDate);
      end.setHours(23, 59, 59);
      if (orderDate > end) return false;
    }
    return true;
  }

  // Real API Actions
  cancelOrder(orderNumber: string, event: Event): void {
    event.stopPropagation();

    if (
      ![OrderStatus.PENDING].includes(
        this.orders.find((o) => o.orderNumber === orderNumber)?.status!,
      )
    ) {
      this.snackBar.open('Only PENDING orders can be cancelled', 'OK');
      return;
    }

    this.snackBar
      .open(`Cancel ${orderNumber}?`, 'Confirm', { duration: 4000 })
      .onAction()
      .subscribe(() => {
        // POST /api/orders/{orderNumber}/cancel
        this.orderService.cancelOrder(orderNumber).subscribe({
          next: () => {
            this.loadOrders();
            this.snackBar.open('Cancelled!', 'OK');
          },
          error: () => this.snackBar.open('Cancel failed', 'OK'),
        });
      });
  }

  redoOrder(orderNumber: string, event: Event): void {
    event.stopPropagation();

    if (
      !this.orders.some((o) => o.orderNumber === orderNumber && o.status === OrderStatus.CANCELLED)
    ) {
      this.snackBar.open('Only CANCELLED orders can be redone', 'OK');
      return;
    }

    this.snackBar
      .open(`Redo ${orderNumber}?`, 'Redo', { duration: 4000 })
      .onAction()
      .subscribe(() => {
        this.orderService.redoOrder(orderNumber).subscribe({
          next: () => {
            this.loadOrders();
            this.snackBar.open('Order redone - check your cart!', 'OK');
          },
          error: () => this.snackBar.open('Redo failed', 'OK'),
        });
      });
  }

  removeOrder(orderNumber: string, event: Event): void {
    event.stopPropagation();

    this.snackBar
      .open(`Remove ${orderNumber}?`, 'Confirm', { duration: 4000 })
      .onAction()
      .subscribe(() => {
        // DELETE /api/orders/{orderNumber} or localStorage.removeItem
        localStorage.removeItem(`order_${orderNumber}`);
        this.loadOrders();
        this.snackBar.open('Order removed!', 'OK');
      });
  }

  viewOrderDetail(orderNumber: string): void {
    this.router.navigate(['/order-detail', orderNumber]);
  }

  // UI Helpers
  getStatusClass(status: OrderStatus): string {
    return `status-${status.toLowerCase()}`;
  }

  getSellerRevenue(order: Order): number {
    if (!this.isSeller) return 0;
    const sellerItems = order.items.filter((item) => item.sellerId === this.currentUserId);
    return sellerItems.reduce((sum, item) => sum + item.price * item.quantity, 0);
  }

  getSellerItems(order: Order): any[] {
    if (!this.isSeller) return order.items;
    return order.items.filter((item) => item.sellerId === this.currentUserId);
  }

  canConfirm(order: Order): boolean {
    return (
      order.status === OrderStatus.PENDING &&
      order.items.some((item) => item.sellerId === this.currentUserId)
    );
  }

  confirmOrder(orderNumber: string, event: Event): void {
    event.stopPropagation();
    this.snackBar
      .open(`Confirm order ${orderNumber}?`, 'Confirm', { duration: 4000 })
      .onAction()
      .subscribe(() => {
        this.orderService.updateStatus(orderNumber, OrderStatus.CONFIRMED).subscribe({
          next: () => {
            this.loadOrders(); // Reload to show updated status
            this.snackBar.open('Order confirmed!', 'OK');
          },
          error: (err) => {
            console.error('Confirm failed', err);
            this.snackBar.open('Confirm failed', 'OK');
          },
        });
      });
  }

  // Permissions
  canCancel(order: Order): boolean {
    return order.status === OrderStatus.PENDING || order.status === OrderStatus.CONFIRMED;
  }

  canRedo(order: Order): boolean {
    return order.status === OrderStatus.CANCELLED && !this.isSeller;
  }

  canRemove(order: Order): boolean {
    return order.status === OrderStatus.CANCELLED;
  }

  trackByOrderNumber(index: number, order: Order): string {
    return order.orderNumber;
  }

  clearFilters(): void {
    this.searchCtrl.setValue('');
    this.statusFilter.setValue('ALL');
    this.startDate = null;
    this.endDate = null;
    this.applyFilters();
  }

  onDateChange(): void {
    this.applyFilters();
  }
}
