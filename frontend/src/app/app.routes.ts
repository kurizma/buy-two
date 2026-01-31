import { Routes } from '@angular/router';
import { HomeComponent } from './components/home/home.component';
import { SignInComponent } from './components/auth/sign-in/sign-in.component';
import { SignUpComponent } from './components/auth/sign-up/sign-up.component';
import { ProfileComponent } from './components/profile/profile.component';
import { ProductListingComponent } from './components/product-listing/product-listing.component';
import { ProductCardComponent } from './components/product-card/product-card.component';
import { CategoriesComponent } from './components/categories/categories.component';
import { SellerDashboardComponent } from './components/seller-dashboard/seller-dashboard.component';
import { SellerShopComponent } from './components/seller-shop/seller-shop.component';
import { AboutComponent } from './components/about/about.component';
import { SellerGuard } from './guards/seller.guard';
import { AuthGuard } from './guards/auth.guard';
import { CartComponent } from './components/cart/cart.component';
import { CheckoutComponent } from './components/checkout/checkout.component';
import { ClientOnlyGuard } from './guards/client-only.guard';
import { EmptyCartGuard } from './guards/empty-cart.guard';

export const routes: Routes = [
  { path: '', component: HomeComponent, pathMatch: 'full' },
  { path: 'signin', component: SignInComponent },
  { path: 'signup', component: SignUpComponent },
  { path: 'product-listing', component: ProductListingComponent },
  { path: 'product/:id', component: ProductCardComponent },
  { path: 'categories', component: CategoriesComponent },
  { path: 'categories/:slug', component: CategoriesComponent },
  { path: 'seller-shop/:id', component: SellerShopComponent },
  { path: 'about', component: AboutComponent },
  {
    path: 'profile',
    component: ProfileComponent,
    canActivate: [AuthGuard],
  },
  {
    path: 'seller-dashboard',
    component: SellerDashboardComponent,
    canActivate: [SellerGuard],
  },
  {
    path: 'cart',
    component: CartComponent,
    canActivate: [ClientOnlyGuard, EmptyCartGuard],
  },
  {
    path: 'checkout',
    component: CheckoutComponent,
    canActivate: [ClientOnlyGuard, EmptyCartGuard],
  },
  { path: '**', redirectTo: '' },
];
