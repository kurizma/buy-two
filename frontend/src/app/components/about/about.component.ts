import { Component, inject } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-about',
  imports: [],
  templateUrl: './about.component.html',
  styleUrl: './about.component.css',
  standalone: true,
})
export class AboutComponent {
  private router = inject(Router);
  private authService = inject(AuthService);

  shopNow() {
    this.router.navigate(['/product-listing']);
  }
  onSellWithUsClick(): void {
    const user = this.authService.currentUserValue; // or however you read it
    const isSeller = user?.role === 'SELLER';

    if (isSeller) {
      this.router.navigate(['/seller-dashboard']);
    } else {
      this.registerSeller(); // uses your existing method
    }
  }

  registerSeller(): void {
    this.authService.logout();
    this.router.navigate(['/signup']); // seller sign-up page
  }

  browseCollections() {
    this.router.navigate(['/categories']);
  }
}
