import { inject, Injectable } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';
import {
  BehaviorSubject,
  catchError,
  firstValueFrom,
  map,
  of,
  Observable,
  EMPTY,
  throwError,
} from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment.docker';

import { UserService } from './user.service';
import { ProductService } from './product.service';
import { CategoryService } from './category.service';
import { CartItem } from '../models/cart/cart-item.model';
import { CartResponse } from '../models/cart/cart-response.model';
import { ApiResponse } from '../models/api-response/api-response.model';
import { AuthService } from './auth.service';

@Injectable({
  providedIn: 'root',
})
export class CartService {
  private readonly CART_STORAGE_KEY = 'shopping_cart';
  private readonly baseUrl = `${environment.apiBaseUrl}/api/cart`;

  private readonly http = inject(HttpClient);
  private readonly snackBar = inject(MatSnackBar);
  private readonly userService = inject(UserService);
  private readonly categoryService = inject(CategoryService);
  private readonly productService = inject(ProductService);
  private readonly authService = inject(AuthService);
  private sellerCache: { [sellerId: string]: { name: string; avatar: string } } = {};

  private cartItemsSubject = new BehaviorSubject<CartItem[]>(this.loadCartFromStorage());
  cartItems$ = this.cartItemsSubject.asObservable();

  constructor() {
    // Save cart to localStorage on changes (backup)
    this.cartItems$.subscribe((items) => {
      this.saveCartToStorage(items);
    });
  }

  // Public methods

  /** // ✅ GET /api/cart */
  loadCart(): void {
    console.log('🔄 Loading cart from:', this.baseUrl);

    if (!this.authService.isAuthenticated()) {
      console.log('👤 Not logged in - empty cart');
      this.cartItemsSubject.next([]);
      return;
    }

    this.http
      .get<ApiResponse<any>>(this.baseUrl)
      .pipe(catchError(this.handleApiError.bind(this)))
      .subscribe({
        next: (response) => {
          if (response.success && response.data?.items?.length > 0) {
            this.loadSellersForCart(response.data.items);
          } else {
            this.cartItemsSubject.next([]);
          }
        },
        error: (error) => {
          console.warn('🛒 Cart 400 - user has no cart yet:', error);
          this.cartItemsSubject.next([]); // Silent fallback
        },
      });
  }

  private loadSellersForCart(items: any[]) {
    const sellerIds = [...new Set(items.map((item) => item.sellerId).filter(Boolean))];

    // Load missing sellers only
    const missingSellers = sellerIds.filter((id) => !this.sellerCache[id]);

    if (missingSellers.length === 0) {
      this.mapCartWithSellers(items);
      return;
    }

    // Load sellers in parallel
    missingSellers.forEach((sellerId) => {
      this.userService.getUserById(sellerId).subscribe({
        next: (user) => {
          if (user) {
            this.sellerCache[user.id] = {
              name: user.name || 'Seller',
              avatar: user.avatar || '/assets/user-default.png',
            };
          }
          // Map when all loaded (or first time)
          if (Object.keys(this.sellerCache).length === sellerIds.length) {
            this.mapCartWithSellers(items);
          }
        },
        error: () => {
          console.warn(`Seller ${sellerId} not found`);
          this.mapCartWithSellers(items); // Continue anyway
        },
      });
    });
  }

  private async mapCartWithSellers(items: any[]) {
    // Process all items in parallel
    const frontendItemsPromises = items.map(async (item: any) => {
      // Existing seller mapping
      const sellerName = this.sellerCache[item.sellerId]?.name || 'Seller';
      const sellerAvatarUrl =
        this.sellerCache[item.sellerId]?.avatar || '/assets/avatars/user-default.png';

      // NEW: Get real categoryId from product
      const product = await firstValueFrom(this.productService.getProductById(item.productId));
      const categoryId = product.categoryId || '';

      return {
        id: this.generateCartItemId(),
        productId: item.productId,
        productName: item.productName,
        sellerId: item.sellerId,
        sellerName,
        sellerAvatarUrl,
        price: Number.parseFloat(item.price) || 0,
        quantity: Number.parseInt(item.quantity, 10) || 1,
        categoryId, // Now real ID!
        categorySlug: this.categoryService.getCategorySlug(categoryId),
        imageUrl: item.imageUrl || '/assets/product-default.png',
      };
    });

    const frontendItems = await Promise.all(frontendItemsPromises);
    this.cartItemsSubject.next(frontendItems);
  }

  /** // ✅ POST /api/cart/items */
  addProductToCart(product: any): void {
    this.addToCart({
      productId: product._id || product.id,
      productName: product.name,
      sellerId: product.sellerId || product.userId || product.ownerId || 'Unknown Seller',
      sellerName: product.sellerName || 'Unknown Seller',
      sellerAvatarUrl: product.sellerAvatarUrl || '/assets/avatars/user-default.png',
      price: Number.parseFloat(product.price) || 0,
      categoryId: product.categoryId,
      imageUrl: product.images?.[0] || '/assets/product-default.png',
      productDescription: product.description,
      availableStock: product.quantity,
    });
  }

  /** Add/update item */
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
    // FRONTEND STOCK CHECK
    if (params.availableStock !== undefined && params.availableStock < 1) {
      this.snackBar.open('❌ Out of stock!', 'Close', {
        duration: 3000,
        horizontalPosition: 'center',
        verticalPosition: 'top',
        panelClass: ['custom-snackbar'],
      });
      return;
    }
    const cartItem = {
      productId: params.productId,
      sellerId: params.sellerId,
      quantity: 1,
      price: params.price,
      productName: params.productName,
      categoryId: params.categoryId,
      categorySlug: this.categoryService.getCategorySlug(params.categoryId) || '',
      imageUrl: params.imageUrl || '/assets/product-default.png',
    };

    // Backend handles final stock validation (CartServiceImpl)
    this.http
      .post<ApiResponse<CartResponse>>(`${this.baseUrl}/items`, cartItem)
      .pipe(catchError(this.handleApiError.bind(this)))
      .subscribe((response) => {
        if (response.success && response.data?.items) {
          this.loadSellersForCart(response.data.items);
          this.snackBar.open('✅ Item added to cart', 'Close', {
            duration: 2000,
            horizontalPosition: 'center',
            verticalPosition: 'top',
            panelClass: ['custom-snackbar'],
          });
        }
      });
  }

  /** // ✅ PUT /api/cart/items/{productId}/quantity/{quantity} */
  updateQuantity(productId: string, quantity: number): void {
    if (quantity < 1) {
      this.removeItem(productId);
      return;
    }

    // Get product stock BEFORE API call
    this.productService.getProductById(productId).subscribe((product) => {
      const availableStock = product.quantity || 0;

      if (quantity > availableStock) {
        this.snackBar.open(`⚠️ Max ${availableStock} available`, 'OK', {
          duration: 3000,
          horizontalPosition: 'center',
          verticalPosition: 'top',
          panelClass: ['custom-snackbar'],
        });
        return;
      }

      // Safe to update
      this.http
        .put<ApiResponse<CartResponse>>(
          `${this.baseUrl}/items/${productId}/quantity/${quantity}`,
          null,
        )
        .pipe(catchError(this.handleApiError.bind(this)))
        .subscribe((response) => {
          if (response.success && response.data?.items) {
            this.loadSellersForCart(response.data.items);
          }
        });
    });
  }

  /** // ✅ DELETE /api/cart/items/{productId} */
  removeItem(productId: string): void {
    this.http
      .delete<ApiResponse<CartResponse>>(`${this.baseUrl}/items/${productId}`)
      .pipe(catchError(this.handleApiError.bind(this)))
      .subscribe((response) => {
        if (response.success) {
          this.loadCart();
        }
      });
  }

  /** Update full item */
  updateItemQuantity(updatedItem: CartItem): void {
    this.updateQuantity(updatedItem.productId, updatedItem.quantity);
  }

  /** Clear cart with confirmation */
  clearCart(): void {
    const snackBarRef = this.snackBar.open('Clear entire cart?', 'Confirm', {
      duration: 5000,
      horizontalPosition: 'center',
      verticalPosition: 'top',
      panelClass: ['custom-snackbar'],
    });

    snackBarRef.onAction().subscribe(() => {
      this.http
        .delete<ApiResponse<any>>(this.baseUrl)
        .pipe(catchError(this.handleApiError.bind(this)))
        .subscribe((response) => {
          if (response.success) {
            this.loadCart();
            this.snackBar.open('🛒 Cart cleared!', 'Close', {
              duration: 3000,
              horizontalPosition: 'center',
              verticalPosition: 'top',
              panelClass: ['custom-snackbar'],
            });
          }
        });
    });
  }

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
    return this.cartItemsSubject.value.length;
  }

  getSellerName(sellerId: string): Observable<string> {
    if (this.sellerCache[sellerId]?.name) {
      return of(this.sellerCache[sellerId].name);
    }
    return this.userService.getUserById(sellerId).pipe(
      map((user) => {
        const name = user?.name || 'Seller';
        this.sellerCache[sellerId] = { name, avatar: user?.avatar || '/assets/user-default.png' };
        return name;
      }),
      catchError(() => of('Seller')),
    );
  }

  getCachedSellerName(sellerId: string): string {
    return this.sellerCache[sellerId]?.name || 'Seller';
  }

  clearCartAfterOrder(): void {
    this.cartItemsSubject.next([]);
    console.log('🛒 Cart cleared after order!');
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

  // LocalStorage methods (backup)
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

  /** API Error handler */
  private handleApiError(error: any) {
    console.error('Cart API error:', error);
    // ⭐ SILENT for 400 (empty cart normal)
    if (error.status === 400) {
      return EMPTY; // No snackbar!
    }
    this.snackBar.open('Please sign in and try again.', 'Close', {
      duration: 4000,
      horizontalPosition: 'center',
      verticalPosition: 'top',
      panelClass: ['custom-snackbar'],
    });
    return throwError(() => error);
  }
}
