import { Injectable, inject } from '@angular/core';
import { CanActivate, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

@Injectable({
  providedIn: 'root',
})
export class ClientOnlyGuard implements CanActivate {
  authService = inject(AuthService);
  router = inject(Router);

  constructor() {}

  canActivate(): boolean {
    // Check auth + role
    if (this.authService.isClient()) {
      return true;
    }

    // Not a client, redirect to home or some other page
    this.router.navigate(['/']);
    return false;
  }
}
