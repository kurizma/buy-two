import { Component, EventEmitter, Input, Output, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ProductResponse } from '../../models/products/product-response.model';
import { UserResponse } from '../../models/users/user-response.model';
import { ProductImageCarouselComponent } from '../ui/product-image-carousel/product-image-carousel.component';
import { RouterLink, Router } from '@angular/router';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { CartService } from '../../services/cart.service';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-product-grid-card',
  standalone: true,
  templateUrl: './products-grid-card.component.html',
  styleUrls: ['./products-grid-card.component.css'],
  imports: [CommonModule, ProductImageCarouselComponent, RouterLink, MatSnackBarModule],
})
export class ProductGridCardComponent {
  // One product per card – passed in from parent
  @Input() product!: ProductResponse;

  // Optional: seller passed in from parent (e.g. getSeller in listing component)
  @Input() seller: UserResponse | undefined;

  // Category display name (computed in parent)
  @Input() categoryName = '';

  // Flags so different pages can hide/show bits
  @Input() showSeller = true;
  @Input() showCategory = true;

  // Events back to parent
  @Output() view = new EventEmitter<string>();
  @Output() addToCartClick = new EventEmitter<any>();

  private router = inject(Router);
  private snackBar = inject(MatSnackBar);
  private cartService = inject(CartService);
  private authService = inject(AuthService);
  onView(): void {
    this.view.emit(this.product.id);
  }

  addToCart(product: any): void {
    const cartProduct = {
      ...product,
      sellerName: this.seller?.name || 'Unknown Seller',
      sellerAvatarUrl: this.seller?.avatar || undefined,
    };

    this.cartService.addProductToCart(cartProduct);

    this.snackBar.open(`${product.name} added to cart!`, '', {
      duration: 2000,
      horizontalPosition: 'end',
      verticalPosition: 'top',
      panelClass: ['custom-snackbar'],
    });
  }

  isSeller(): boolean {
    // Assuming AuthService is available for injection
    return this.authService.isSeller();
  }

  isInCart(productId: string): boolean {
    return this.cartService.isInCart(productId);
  }
}
