import { Component, OnInit, OnDestroy, inject } from '@angular/core';
import { RouterLink } from '@angular/router';
import { CommonModule, CurrencyPipe } from '@angular/common';
import { CartItem, mockCartItems } from '../../models/cart/cart-item.model';
import { CartService } from '../../services/cart.service';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-shopping-cart',
  standalone: true,
  imports: [CommonModule, CurrencyPipe, RouterLink],
  templateUrl: './shopping-cart.component.html',
  styleUrls: ['./shopping-cart.component.css'],
})
export class ShoppingCartComponent implements OnInit, OnDestroy {
  cartItems: CartItem[] = [];
  private subscription: Subscription | null = null; // Remove 'readonly' here
  private readonly cartService = inject(CartService);

  ngOnInit() {
    // For styling purposes, load mock data
    // Later replace this with actual service call
    this.cartItems = mockCartItems;
    console.log('Loaded mock cart items:', this.cartItems);

    // Uncomment when service is ready
    /*
    this.subscription = this.cartService.cartItems$.subscribe((items) => {
      this.cartItems = items || [];
    });
    */
  }

  // Getters for cart calculations (all as getters for consistency)
  get totalInclVat(): number {
    // Sum of all items (prices already include VAT)
    return this.cartItems.reduce((sum, item) => sum + item.price * item.quantity, 0);
  }

  get subtotal(): number {
    // Calculate subtotal without VAT (reverse calculation)
    // Price includes VAT, so: subtotal = totalInclVat / 1.24
    return this.totalInclVat / 1.24;
  }

  get vatAmount(): number {
    // VAT is the difference between total and subtotal
    return this.totalInclVat - this.subtotal;
  }

  get shippingCost(): number {
    // Free shipping over €50 (based on totalInclVat)
    return this.totalInclVat >= 50 ? 0 : 4.9;
  }

  get total(): number {
    // Total is totalInclVat + shipping (VAT already included in items)
    return this.totalInclVat + this.shippingCost;
  }

  get itemCount(): number {
    return this.cartItems.reduce((sum, item) => sum + item.quantity, 0);
  }

  get isEmpty(): boolean {
    return this.cartItems.length === 0;
  }

  // Cart item actions
  updateQuantity(productId: string, newQuantity: number): void {
    this.cartService.updateQuantity(productId, newQuantity);
  }

  increaseQuantity(productId: string): void {
    const item = this.cartItems.find((i) => i.productId === productId);
    if (item) {
      this.updateQuantity(productId, item.quantity + 1);
    }
  }

  decreaseQuantity(productId: string): void {
    const item = this.cartItems.find((i) => i.productId === productId);
    if (item) {
      this.updateQuantity(productId, item.quantity - 1);
    }
  }

  removeItem(productId: string): void {
    this.cartService.removeItem(productId);
  }

  clearCart(): void {
    if (confirm('Are you sure you want to clear your cart?')) {
      this.cartService.clearCart();
    }
  }

  proceedToCheckout(): void {
    // Navigate to checkout page
    console.log('Proceeding to checkout...');
    // Later: this.router.navigate(['/checkout']);
  }

  // Performance optimization for *ngFor
  trackByProductId(index: number, item: CartItem): string {
    return item.productId;
  }

  ngOnDestroy() {
    this.subscription?.unsubscribe();
  }
}
