import { Component, OnInit, OnDestroy, inject } from '@angular/core';
import { RouterLink, Router } from '@angular/router';
import { CommonModule, CurrencyPipe } from '@angular/common';
import { CartItem } from '../../models/cart/cart-item.model';
import { CartService } from '../../services/cart.service';
import { CategoryService } from '../../services/category.service';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-cart',
  standalone: true,
  imports: [CommonModule, CurrencyPipe, RouterLink],
  templateUrl: './cart.component.html',
  styleUrls: ['./cart.component.css'],
})
export class CartComponent implements OnInit, OnDestroy {
  cartItems: CartItem[] = [];
  private subscription: Subscription | null = null; // Remove 'readonly' here
  public readonly cartService = inject(CartService);
  private readonly router = inject(Router);
  private readonly categoryService = inject(CategoryService);

  ngOnInit() {
    this.cartService.loadCart();

    this.subscription = this.cartService.cartItems$.subscribe((items) => {
      this.cartItems = items || [];
    });

    this.categoryService.loadCategories();
  }

  ngOnDestroy() {
    this.subscription?.unsubscribe();
  }

  // delegate all cart calculations to CartService

  // Cart item actions
  getCategorySlug(categoryId: string): string {
    return this.categoryService.getCategorySlug(categoryId) || '';
  }

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
    this.cartService.clearCart();
  }

  proceedToCheckout(): void {
    if (this.cartService.getItemCount() === 0) {
      return; // Do not proceed if cart is empty
    }
    // Navigate to checkout page
    console.log('Proceeding to checkout...');
    this.router.navigate(['/checkout']);
  }

  trackByProductId(index: number, item: CartItem): string {
    return item.productId;
  }
}
