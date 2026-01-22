import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { CartItem } from '../models/cart-item/cart-item.model';

@Injectable({
  providedIn: 'root',
})
export class CartService {
  private readonly cartItemsSubject = new BehaviorSubject<CartItem[]>([]);
  cartItems$ = this.cartItemsSubject.asObservable();

  addToCart(
    productId: string,
    productName: string,
    sellerId: string,
    price: number,
    categoryId: string,
    imageUrl?: string,
  ): void {
    const currentCart = this.cartItemsSubject.value;
    const existingItem = currentCart.find((item) => item.productId === productId);

    if (existingItem) {
      // update quantity
      existingItem.quantity++;
      this.cartItemsSubject.next([...currentCart]);
    } else {
      // add new item
      const newItem: CartItem = {
        productId,
        productName,
        sellerId,
        price,
        quantity: 1,
        categoryId,
        imageUrl,
      };
      this.cartItemsSubject.next([...currentCart, newItem]);
    }
  }

  removeItemFromCart(productId: string): void {
    const currentCart = this.cartItemsSubject.value;
    const updatedCart = currentCart.filter((item) => item.productId !== productId);
    this.cartItemsSubject.next(updatedCart);
  }

  updateItemQuantity(updatedItem: CartItem): void {
    const currentCart = this.cartItemsSubject.value;
    const itemIndex = currentCart.findIndex((item) => item.productId === updatedItem.productId);

    if (itemIndex !== -1) {
      currentCart[itemIndex].quantity = updatedItem.quantity;
      this.cartItemsSubject.next([...currentCart]);
    }
  }

  getTotal(): number {
    return this.cartItemsSubject.value.reduce((sum, item) => sum + item.price * item.quantity, 0);
  }

  clearCart(): void {
    this.cartItemsSubject.next([]);
  }
}
