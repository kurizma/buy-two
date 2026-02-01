import { Component, OnInit, inject } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
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

@Component({
  selector: 'app-order-list',
  standalone: true,
  imports: [
    CommonModule,
    RouterLink,
    FormsModule,
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

  searchTerm = '';
  selectedStatus: OrderStatus | 'ALL' = 'ALL';
  startDate: Date | null = null;
  endDate: Date | null = null;

  OrderStatus = OrderStatus;
  statusOptions = ['ALL', ...Object.values(OrderStatus)];

  private router = inject(Router);
  private authService = inject(AuthService);
  private cartService = inject(CartService); // Adjust if you have a separate cart service
  private snackBar = inject(MatSnackBar);
  // Determine if current user is a seller
  isSeller = false;
  currentUserId = '';

  ngOnInit(): void {
    // Get current user info (adjust based on your auth service)
    this.currentUserId = this.authService.getUserId() || 'unknown';
    this.isSeller = this.authService.isSeller();

    console.log('🔍 Current User ID:', this.currentUserId);
    console.log('🔍 Is Seller:', this.isSeller);

    this.loadOrders();

    // Debug: Log all orders
    console.log('📦 All orders loaded:', this.orders);
    console.log('📦 Filtered orders:', this.filteredOrders);
  }

  loadOrders(): void {
    const orders: Order[] = [];

    // Load all orders from localStorage
    for (let i = 0; i < localStorage.length; i++) {
      const key = localStorage.key(i);
      if (!key?.startsWith('order_')) {
        continue;
      }

      const order = this.getOrderFromStorage(key);
      if (order && this.shouldIncludeOrder(order)) {
        orders.push(order);
      }
    }

    // Sort by date (newest first)
    orders.sort(
      (a, b) => new Date(b.createdAt || '').getTime() - new Date(a.createdAt || '').getTime(),
    );
    this.orders = orders;

    this.filteredOrders = [...this.orders];
  }

  private getOrderFromStorage(key: string): any {
    try {
      const orderData = localStorage.getItem(key);
      return orderData ? JSON.parse(orderData) : null;
    } catch (error) {
      console.error('Error parsing order:', error);
      return null;
    }
  }

  private shouldIncludeOrder(order: any): boolean {
    // Filter based on user role
    if (this.isSeller) {
      // Seller: show orders that contain their products
      return order.items.some((item: any) => item.sellerId === this.currentUserId);
    }
    // User: show their own orders
    return order.userId === this.currentUserId;
  }

  applyFilters(): void {
    this.filteredOrders = this.orders.filter((order) => {
      // Search filter
      const matchesSearch =
        !this.searchTerm ||
        order.orderNumber.toLowerCase().includes(this.searchTerm.toLowerCase()) ||
        order.items.some((item) =>
          item.productName.toLowerCase().includes(this.searchTerm.toLowerCase()),
        );

      // Status filter
      const matchesStatus = this.selectedStatus === 'ALL' || order.status === this.selectedStatus;

      // Date range filter
      let matchesDateRange = true;
      if (this.startDate || this.endDate) {
        const orderDate = new Date(order.createdAt || '');
        if (this.startDate && orderDate < this.startDate) {
          matchesDateRange = false;
        }
        if (this.endDate) {
          const endOfDay = new Date(this.endDate);
          endOfDay.setHours(23, 59, 59, 999);
          if (orderDate > endOfDay) {
            matchesDateRange = false;
          }
        }
      }

      return matchesSearch && matchesStatus && matchesDateRange;
    });
  }

  onSearch(): void {
    this.applyFilters();
  }

  onStatusChange(): void {
    this.applyFilters();
  }

  onDateChange(): void {
    this.applyFilters();
  }

  clearFilters(): void {
    this.searchTerm = '';
    this.selectedStatus = 'ALL';
    this.startDate = null;
    this.endDate = null;
    this.filteredOrders = [...this.orders];
  }

  viewOrderDetail(orderId: string): void {
    this.router.navigate(['/order-detail', orderId]);
  }

  getStatusClass(status: OrderStatus): string {
    return `status-${status.toLowerCase()}`;
  }

  // Order Management Actions
  cancelOrder(order: Order, event: Event): void {
    event.stopPropagation(); // Prevent navigation to detail page

    if (order.status === OrderStatus.DELIVERED || order.status === OrderStatus.CANCELLED) {
      this.snackBar.open('This order cannot be cancelled.', 'Close', {
        duration: 3000,
        horizontalPosition: 'center',
        verticalPosition: 'top',
        panelClass: ['custom-snackbar'],
      });
      return;
    }

    if (confirm(`Cancel order ${order.orderNumber}?`)) {
      order.status = OrderStatus.CANCELLED;
      localStorage.setItem(`order_${order.id}`, JSON.stringify(order));
      this.loadOrders();
      this.applyFilters();
    }
  }

  removeOrder(order: Order, event: Event): void {
    event.stopPropagation();

    // Only allow removal of cancelled orders
    if (order.status !== OrderStatus.CANCELLED) {
      this.snackBar.open('Only cancelled orders can be removed.', 'Close', {
        duration: 3000,
        horizontalPosition: 'center',
        verticalPosition: 'top',
        panelClass: ['custom-snackbar'],
      });
      return;
    }

    if (confirm(`Permanently remove order ${order.orderNumber}? This cannot be undone.`)) {
      localStorage.removeItem(`order_${order.id}`);
      this.loadOrders();
      this.applyFilters();
    }
  }

  redoOrder(order: Order, event: Event): void {
    event.stopPropagation();

    if (order.status !== OrderStatus.CANCELLED) {
      this.snackBar.open('Only cancelled orders can be redone.', 'Close', {
        duration: 3000,
        horizontalPosition: 'center',
        verticalPosition: 'top',
        panelClass: ['custom-snackbar'],
      });
      return;
    }

    // ✅ Replace this line
    const snackBarRef = this.snackBar.open(
      `Add all items from order ${order.orderNumber} back to your cart?`,
      'Add to Cart',
      {
        duration: 5000,
        horizontalPosition: 'right',
        verticalPosition: 'top',
        panelClass: ['custom-snackbar'],
      },
    );

    // ✅ Wrap your existing code in the action
    snackBarRef.onAction().subscribe(() => {
      let addedCount = 0;

      // Add each item from the order back to cart
      order.items.forEach((item) => {
        // Create a product-like object from order item
        const product = {
          _id: item.productId,
          id: item.productId,
          name: item.productName,
          userId: item.sellerId,
          sellerName: item.sellerName,
          price: item.price,
          categoryId: item.categoryId || '',
          images: item.imageUrl ? [item.imageUrl] : [],
          description: '',
          quantity: 999, // Assume available stock (since we don't have real data)
        };

        // Add to cart with the original quantity
        for (let i = 0; i < item.quantity; i++) {
          this.cartService.addProductToCart(product);
        }

        addedCount++;
      });

      // Show success message and navigate to cart
      this.snackBar.open(`${addedCount} product(s) added to your cart!`, 'Close', {
        duration: 3000,
        horizontalPosition: 'center',
        verticalPosition: 'top',
        panelClass: ['custom-snackbar'],
      });
      this.router.navigate(['/cart']);
    });
  }
  // Helper to show available actions based on status and role
  canCancel(order: Order): boolean {
    return order.status !== OrderStatus.DELIVERED && order.status !== OrderStatus.CANCELLED;
  }

  canRemove(order: Order): boolean {
    return order.status === OrderStatus.CANCELLED;
  }

  canRedo(order: Order): boolean {
    return order.status === OrderStatus.CANCELLED && !this.isSeller;
  }
}
