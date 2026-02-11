import { Component, OnDestroy, OnInit, inject } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../../services/auth.service';
import { CartService } from '../../../services/cart.service';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-navigation',
  standalone: true,
  templateUrl: './navigation.component.html',
  styleUrls: ['./navigation.component.css'],
  imports: [RouterModule, CommonModule],
})
export class NavigationComponent implements OnInit, OnDestroy {
  isAuthenticated = false;
  currentUserName: string | null = null;
  currentUserAvatar: string | null = null;
  cartItemCount = 0; // Integrate with CartService to get actual count

  public authService: AuthService = inject(AuthService);
  private router: Router = inject(Router);
  private cartService: CartService = inject(CartService);
  private cartSubs?: Subscription | null = null;

  ngOnInit() {
    this.authService.currentUser$.subscribe((user) => {
      this.isAuthenticated = !!user;
      this.currentUserName = user?.name || null;
      this.currentUserAvatar = user?.avatar || null;
    });

    // Live cart count

    this.cartSubs = this.cartService.cartItems$.subscribe((items) => {
      this.cartItemCount = items.length;
    });
  }

  ngOnDestroy() {
    this.cartSubs?.unsubscribe();
  }

  onLogout() {
    this.authService.logout();
    this.router.navigate(['/']);
  }
}
