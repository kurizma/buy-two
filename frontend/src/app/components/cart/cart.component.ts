import { Component, OnInit, OnDestroy, inject } from '@angular/core';
import { RouterLink, Router } from '@angular/router';
import { CommonModule, CurrencyPipe } from '@angular/common';
import { CartItem } from '../../models/cart/cart-item.model';
import { CartService } from '../../services/cart.service';
import { CategoryService } from '../../services/category.service';
import { Subscription } from 'rxjs';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';

@Component({
  selector: 'app-cart',
  standalone: true,
  imports: [CommonModule, CurrencyPipe, RouterLink, MatSnackBarModule],
  templateUrl: './cart.component.html',
  styleUrls: ['./cart.component.css'],
})
export class CartComponent implements OnInit, OnDestroy {
  cartItems: CartItem[] = [];
  private subscription: Subscription | null = null; // Remove 'readonly' here
  public readonly cartService = inject(CartService);
  private readonly router = inject(Router);
  private readonly categoryService = inject(CategoryService);
  private readonly snackBar = inject(MatSnackBar);

  ngOnInit() {
    console.log('🛒 CartComponent init - loading from API'); // Debug

    this.cartService.loadCart();

    this.subscription = this.cartService.cartItems$.subscribe((items) => {
      console.log('🛒 Cart items updated:', items); // Debug
      this.cartItems = items || [];
    });

    this.categoryService.loadCategories();
  }

  ngOnDestroy() {
    this.subscription?.unsubscribe();
  }

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
      if (item.quantity && item.quantity + 1 > item.quantity) {
        this.snackBar.open(`Max ${item.quantity} available!`, 'OK', {
          duration: 3000,
          horizontalPosition: 'center',
          verticalPosition: 'top',
          panelClass: ['custom-snackbar'],
        });
        return;
      }
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
    this.router.navigate(['/order-checkout']);
  }

  trackByProductId(index: number, item: CartItem): string {
    return item.productId;
  }
}
