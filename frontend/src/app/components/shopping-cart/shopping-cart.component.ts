import { Component, OnInit, OnDestroy, inject } from '@angular/core';
import { CommonModule, CurrencyPipe } from '@angular/common';
import { CartItem } from '../../models/cart-item/cart-item.model';
import { CartService } from '../../services/cart.service';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-shopping-cart',
  standalone: true,
  imports: [CommonModule, CurrencyPipe],
  templateUrl: './shopping-cart.component.html',
  styleUrls: ['./shopping-cart.component.css'],
})
export class ShoppingCartComponent implements OnInit, OnDestroy {
  cartItems: CartItem[] = [];
  private subscription: Subscription | null = null;
  private readonly cartService = inject(CartService);

  ngOnInit() {
    this.subscription = this.cartService.cartItems$.subscribe((items) => {
      this.cartItems = items || [];
    });
  }

  get totalExclVat(): number {
    return this.cartItems.reduce((sum, item) => sum + item.price * item.quantity, 0);
  }

  get vatAmount(): number {
    return this.totalExclVat * 0.2;
  }

  get totalInclVat(): number {
    return this.totalExclVat * 1.2;
  }

  trackByProductId(index: number, item: CartItem): string {
    return item.productId;
  }

  clearCart(): void {
    this.cartService.clearCart();
  }

  ngOnDestroy() {
    this.subscription?.unsubscribe();
  }
}
