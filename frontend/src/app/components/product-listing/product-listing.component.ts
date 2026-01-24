import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';

import { ProductGridCardComponent } from '../products-grid-card/products-grid-card.component';
import { ProductService } from '../../services/product.service';
import { ProductResponse } from '../../models/products/product-response.model';
import { UserService } from '../../services/user.service';
import { UserResponse } from '../../models/users/user-response.model';
import { CategoryService } from '../../services/category.service';
import { Category } from '../../models/categories/category.model';
import { CartService } from '../../services/cart.service';

@Component({
  selector: 'app-product-listing',
  templateUrl: './product-listing.component.html',
  styleUrls: ['./product-listing.component.css'],
  standalone: true,
  imports: [CommonModule, FormsModule, ProductGridCardComponent],
})
export class ProductListingComponent implements OnInit {
  private readonly router = inject(Router);
  private readonly productService = inject(ProductService);
  private readonly userService = inject(UserService);
  private readonly categoryService = inject(CategoryService);
  private readonly cartService = inject(CartService);

  products: ProductResponse[] = [];
  filteredProducts: ProductResponse[] = [];

  sellers = new Map<string, UserResponse>();

  searchQuery = '';
  categoryFilter = 'all';
  sortBy = 'name';

  categories: Category[] = [];
  categoryOptions: { id: string; name: string }[] = [];

  isLoading = false;
  errorMessage: string | null = null;

  ngOnInit() {
    this.loadProducts();
    this.loadCategories();
  }

  private loadProducts() {
    this.isLoading = true;
    this.errorMessage = null;

    this.productService.getProducts().subscribe({
      next: (prods) => {
        this.products = prods;
        this.initializeCategoryOptions();
        this.updateFilteredProducts();
        this.loadSellersForProducts();
        this.isLoading = false;
      },
      error: () => {
        this.errorMessage = 'Could not load products.';
        this.isLoading = false;
      },
    });
  }

  private loadCategories() {
    this.categoryService.getCategories().subscribe({
      next: (cats) => {
        this.categories = cats;
        this.initializeCategoryOptions();
      },
      error: () => {
        this.categories = [];
        this.categoryOptions = [];
      },
    });
  }

  private initializeCategoryOptions() {
    this.categoryOptions = this.categories.map((c) => ({
      id: c.id,
      name: c.slug,
    }));
  }

  getCategoryName(categoryId: string): string {
    const cat = this.categories.find((c) => c.id === categoryId);
    return cat ? cat.slug : categoryId; // or cat.name
  }

  updateFilteredProducts() {
    this.filteredProducts = this.products
      .filter((product) => {
        const q = this.searchQuery.toLowerCase();
        const matchesSearch =
          product.name.toLowerCase().includes(q) || product.description.toLowerCase().includes(q);
        const matchesCategory =
          this.categoryFilter === 'all' || product.categoryId === this.categoryFilter;
        return matchesSearch && matchesCategory;
      })
      .sort((a, b) => {
        switch (this.sortBy) {
          case 'name':
            return a.name.localeCompare(b.name);
          case 'price-low':
            return a.price - b.price;
          case 'price-high':
            return b.price - a.price;
          default:
            return 0;
        }
      });
  }

  private loadSellersForProducts() {
    const ids = Array.from(
      new Set(this.products.map((p) => p.userId).filter((id): id is string => !!id)), // Filter out undefined and null
    );
    ids.forEach((id) => {
      if (!this.sellers.has(id)) {
        this.userService.getUserById(id).subscribe({
          next: (user) => {
            if (user?.role === 'SELLER') {
              this.sellers.set(id, user);
            }
          },
        });
      }
    });
  }

  getSeller(userId: string): UserResponse | undefined {
    return this.sellers.get(userId);
  }

  viewProductDetail(productId: string) {
    this.router.navigate(['/product', productId]);
  }

  addToCart(product: any): void {
    this.cartService.addProductToCart(product);
    // Show success message/toast
    alert(`${product.name} added to cart!`);
  }

  isInCart(productId: string): boolean {
    return this.cartService.isInCart(productId);
  }

  onSearchChange() {
    this.updateFilteredProducts();
  }

  onCategoryChange() {
    this.updateFilteredProducts();
  }

  onSortChange() {
    this.updateFilteredProducts();
  }
}
