import { Injectable, inject } from '@angular/core';
import { CanActivate, Router } from '@angular/router';
import { CartService } from '../services/cart.service';

@Injectable({
  providedIn: 'root',
})
export class EmptyCartGuard implements CanActivate {
  private cartService: CartService = inject(CartService);
  private readonly router = inject(Router);

  async canActivate(): Promise<boolean> {
    if (this.cartService.getItemCount() === 0) {
      this.router.navigate(['/product-listing']);
      return false;
    }
    // If cart is not empty, allow access to the route
    return true;
  }
}
