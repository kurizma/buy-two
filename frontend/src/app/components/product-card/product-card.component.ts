import { Component, OnInit, inject, Output, EventEmitter } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common';
import { ProductImageCarouselComponent } from '../ui/product-image-carousel/product-image-carousel.component';

import { ProductService } from '../../services/product.service';
import { ProductResponse } from '../../models/products/product-response.model';
import { UserService } from '../../services/user.service';
import { UserResponse } from '../../models/users/user-response.model';
import { CategoryService } from '../../services/category.service';
import { Category } from '../../models/categories/category.model';
import { CartItem } from '../../models/cart-item/cart-item.model';

@Component({
  selector: 'app-product-card',
  templateUrl: './product-card.component.html',
  styleUrls: ['./product-card.component.css'],
  imports: [CommonModule, ProductImageCarouselComponent, RouterLink],
})
export class ProductCardComponent implements OnInit {
  private readonly route: ActivatedRoute = inject(ActivatedRoute);
  private readonly productService: ProductService = inject(ProductService);
  private readonly userService: UserService = inject(UserService);
  private readonly categoryService: CategoryService = inject(CategoryService);

  product!: ProductResponse; // non-null after load
  seller: UserResponse | undefined;
  category: Category | undefined;
  errorMessage: string | null = null;

  @Output() addToCartClick = new EventEmitter<Partial<CartItem>>();

  isFading = false;

  ngOnInit() {
    const productId = this.route.snapshot.paramMap.get('id');
    if (!productId) return;

    this.productService.getProductById(productId).subscribe({
      next: (prod) => {
        this.product = prod;

        // load seller
        this.userService.getUserById(prod.userId).subscribe({
          next: (user) => (this.seller = user),
          error: () => {}, // optional: handle seller errors
        });

        // load category
        this.categoryService.getCategoryById(prod.categoryId).subscribe({
          next: (cat) => (this.category = cat),
          error: () => {}, // optional: handle category errors
        });
      },
      error: (err) => {
        if (err.status === 404) {
          this.errorMessage = 'Product not found';
        } else {
          this.errorMessage = 'Failed to load product. Please try again.';
        }
      },
    });
  }

  getCategoryName(): string {
    return this.category ? this.category.name : '';
  }

  onAddToCart(): void {
    this.addToCartClick.emit({
      productId: this.product.id,
      productName: this.product.name,
      sellerId: this.product.userId,
      price: this.product.price,
      categoryId: this.product.categoryId,
      imageUrl: this.product.images[0],
      quantity: 1, // Default quantity when adding to cart
    });
  }

  goBack() {
    this.isFading = true;
    setTimeout(() => {
      this.isFading = false;
      globalThis.history.back();
    }, 350);
  }

  nextProduct(currentProductId: string) {
    this.isFading = true;
    setTimeout(() => {
      this.isFading = false;
      this.productService.getProducts().subscribe((products) => {
        const currentIndex = products.findIndex((p) => p.id === currentProductId);
        const nextIndex = (currentIndex + 1) % products.length;
        const nextProductId = products[nextIndex].id;
        // Navigate to the next product
        globalThis.location.href = `/product/${nextProductId}`;
      });
    }, 350);
  }
}
