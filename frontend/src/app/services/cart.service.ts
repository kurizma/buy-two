import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { CartItem } from '../models/cart/cart-item.model';

@Injectable({
  providedIn: 'root',
})
export class CartService {
  private readonly CART_STORAGE_KEY = 'shopping_cart';
  private readonly cartItemsSubject = new BehaviorSubject<CartItem[]>(this.loadCartFromStorage());
  cartItems$ = this.cartItemsSubject.asObservable();

  constructor() {
    // Save cart to localStorage whenever it changes
    this.cartItems$.subscribe((items) => {
      this.saveCartToStorage(items);
    });
  }

  // Add full product to cart (more convenient method)
  addProductToCart(product: any): void {
    this.addToCart({
      productId: product._id || product.id,
      productName: product.name,
      sellerId: product.userId,
      sellerName: product.seller?.name || product.sellerName || 'Unknown Seller', // Placeholder, ideally fetch from user service
      sellerAvatarUrl: product.seller?.avatarUrl || product.sellerAvatarUrl,
      price: product.price,
      categoryId: product.categoryId,
      imageUrl: product.images?.[0] || '', // Use first image
      productDescription: product.description,
      availableStock: product.quantity, // Available stock
    });
  }

  addToCart(params: {
    productId: string;
    productName: string;
    sellerId: string;
    price: number;
    categoryId: string;
    imageUrl?: string;
    productDescription?: string;
    availableStock?: number;
    sellerName: string;
    sellerAvatarUrl?: string;
  }): void {
    const {
      productId,
      productName,
      sellerId,
      price,
      categoryId,
      imageUrl,
      productDescription,
      availableStock,
      sellerName,
      sellerAvatarUrl,
    } = params;

    const currentCart = this.cartItemsSubject.value;
    const existingItem = currentCart.find((item) => item.productId === productId);

    if (existingItem) {
      // Check stock limit
      if (availableStock && existingItem.quantity >= availableStock) {
        console.warn('Cannot add more items. Stock limit reached.');
        return;
      }
      // Update quantity
      existingItem.quantity++;
      this.cartItemsSubject.next([...currentCart]);
    } else {
      // Add new item
      const newItem: CartItem = {
        id: this.generateCartItemId(),
        productId,
        productName,
        productDescription,
        sellerId,
        sellerName,
        price,
        quantity: 1,
        categoryId,
        imageUrl: imageUrl || '/assets/placeholder.jpg',
        sellerAvatarUrl,
      };
      this.cartItemsSubject.next([...currentCart, newItem]);
    }
  }

  removeItem(productId: string): void {
    const currentCart = this.cartItemsSubject.value;
    const updatedCart = currentCart.filter((item) => item.productId !== productId);
    this.cartItemsSubject.next(updatedCart);
  }

  updateQuantity(productId: string, quantity: number): void {
    if (quantity < 1) {
      this.removeItem(productId);
      return;
    }

    const currentCart = this.cartItemsSubject.value;
    const item = currentCart.find((item) => item.productId === productId);

    if (item) {
      item.quantity = quantity;
      this.cartItemsSubject.next([...currentCart]);
    }
  }

  // Keep the old method for backward compatibility
  updateItemQuantity(updatedItem: CartItem): void {
    this.updateQuantity(updatedItem.productId, updatedItem.quantity);
  }

  // Get cart totals
  getTotalInclVat(): number {
    return this.cartItemsSubject.value.reduce((sum, item) => sum + item.price * item.quantity, 0);
  }

  getSubtotal(): number {
    return this.getTotalInclVat() / 1.24;
  }

  getVatAmount(): number {
    return this.getTotalInclVat() - this.getSubtotal();
  }

  getShippingCost(): number {
    return this.getTotalInclVat() >= 50 ? 0 : 4.9;
  }

  getTotal(): number {
    return this.getTotalInclVat() + this.getShippingCost();
  }

  getItemCount(): number {
    return this.cartItemsSubject.value.reduce((sum, item) => sum + item.quantity, 0);
  }

  clearCart(): void {
    if (confirm('Are you sure you want to clear your cart?')) {
      this.cartItemsSubject.next([]);
    }
  }

  // Check if product is in cart
  isInCart(productId: string): boolean {
    return this.cartItemsSubject.value.some((item) => item.productId === productId);
  }

  // Get quantity of specific product in cart
  getProductQuantity(productId: string): number {
    const item = this.cartItemsSubject.value.find((item) => item.productId === productId);
    return item ? item.quantity : 0;
  }

  get isEmpty(): boolean {
    return this.cartItemsSubject.value.length === 0;
  }

  get cartItems(): CartItem[] {
    return this.cartItemsSubject.value;
  }

  // LocalStorage methods
  private loadCartFromStorage(): CartItem[] {
    try {
      const cartData = localStorage.getItem(this.CART_STORAGE_KEY);
      return cartData ? JSON.parse(cartData) : [];
    } catch (error) {
      console.error('Error loading cart from storage:', error);
      return [];
    }
  }

  private saveCartToStorage(items: CartItem[]): void {
    try {
      localStorage.setItem(this.CART_STORAGE_KEY, JSON.stringify(items));
    } catch (error) {
      console.error('Error saving cart to storage:', error);
    }
  }

  // Generate unique cart item ID
  private generateCartItemId(): string {
    return `cart-item-${Date.now()}-${Math.random().toString(36).substring(2, 11)}`;
  }
}
