import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ProductResponse } from '../../models/products/product-response.model';
import { UserResponse } from '../../models/users/user-response.model';
import { ProductImageCarouselComponent } from '../ui/product-image-carousel/product-image-carousel.component';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-product-grid-card',
  standalone: true,
  templateUrl: './products-grid-card.component.html',
  styleUrls: ['./products-grid-card.component.css'],
  imports: [CommonModule, ProductImageCarouselComponent, RouterLink],
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

  onView(): void {
    this.view.emit(this.product.id);
  }

  onAddToCart(): void {
    this.addToCartClick.emit({
      productId: this.product.id,
      productName: this.product.name,
      sellerId: this.product.userId,
      price: this.product.price,
      categoryId: this.product.categoryId,
      imageUrl: this.product.images[0],
    });
  }
}
