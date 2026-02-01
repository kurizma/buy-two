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
import { OrderDetailComponent } from './components/order-detail/order-detail.component';

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
    canActivate: [AuthGuard], //Any logged-in user
  },
  {
    path: 'seller-dashboard',
    component: SellerDashboardComponent,
    canActivate: [SellerGuard], //Only sellers
  },
  {
    path: 'cart',
    component: CartComponent,
    canActivate: [ClientOnlyGuard, EmptyCartGuard], //Client + has items in cart
  },
  {
    path: 'checkout',
    component: CheckoutComponent,
    canActivate: [ClientOnlyGuard, EmptyCartGuard], //Client + has items in cart
  },
  {
    path: 'order-detail/:id',
    component: OrderDetailComponent,
    canActivate: [ClientOnlyGuard], // Assuming only clients can view order details
  },
  { path: '**', redirectTo: '' },
];
