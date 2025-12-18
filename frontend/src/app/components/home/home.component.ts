import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HeroComponent } from '../ui/hero/hero.component';
import { CategoryService } from '../../services/category.service';
import { Category } from '../../models/categories/category.model';
import { ProductService } from '../../services/product.service';
import { UserService } from '../../services/user.service';
import { InfiniteSliderComponent } from '../ui/infinite-slider/infinite-slider.component';
import { Router } from '@angular/router';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, HeroComponent, InfiniteSliderComponent],
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css'],
})
export class HomeComponent implements OnInit {
  private router = inject(Router);
  private categoryService = inject(CategoryService);
  private productService = inject(ProductService);
  private userService = inject(UserService);

  categories: Category[] = [];
  sliderSellers: { id: string; avatar: string; name: string }[] = [];
  sliderProducts: { id: string; image: string }[] = [];

  ngOnInit(): void {
    this.loadCategories();
    this.loadSellers();
    this.loadProducts();
  }

  private loadCategories(): void {
    this.categoryService.getCategories().subscribe({
      next: (cats) => (this.categories = cats),
      error: (err) => {
        console.error('Failed to load categories on home', err);
      },
    });
  }

  private loadSellers(): void {
    this.userService.getSellers().subscribe({
      next: (sellers) => {
        this.sliderSellers = sellers.map((seller) => ({
          id: seller.id,
          avatar: seller.avatar ?? 'assets/avatars/user-default.png',
          name: seller.name,
        }));
      },
      error: (err) => {
        console.error('Failed to load sellers on home', err);
      },
    });
  }

  private loadProducts(): void {
    this.productService.getProducts().subscribe({
      next: (products) => {
        this.sliderProducts = products.map((product) => ({
          id: product.id,
          image: Array.isArray(product.images) ? product.images[0] : product.images,
        }));
      },
      error: (err) => {
        console.error('Failed to load products on home', err);
      },
    });
  }

  onCategoryClick(categorySlug: string): void {
    this.router.navigate(['/categories', categorySlug]);
  }

  shopNow() {}
  registerSeller() {}
  browseCreators() {}
}
