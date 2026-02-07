import { Component, OnInit, inject } from '@angular/core';
import {
  ReactiveFormsModule,
  FormBuilder,
  FormGroup,
  Validators,
  FormsModule,
} from '@angular/forms';
import { MatStepper, MatStepperModule } from '@angular/material/stepper';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { AuthService } from '../../services/auth.service';
import { CartService } from '../../services/cart.service';
import { CommonModule } from '@angular/common';
import { MatIcon } from '@angular/material/icon';
import { MatRadioModule } from '@angular/material/radio';
import { RouterLink, Router } from '@angular/router';
import { OrderService } from '../../services/order.service';
import { CreateOrderRequest } from '../../models/order/createOrderRequest.model';
import { PaymentMethod } from '../../models/order/order.model';
import { Address } from '../../models/order/address.model';

@Component({
  selector: 'app-checkout',
  templateUrl: './checkout.component.html',
  styleUrls: ['./checkout.component.css'],
  imports: [
    MatStepper,
    MatStepperModule,
    ReactiveFormsModule, // for FormGroup and FormBuilder
    MatFormFieldModule, // for mat-form-field
    MatInputModule, // for matInput
    MatButtonModule, // for mat-button
    MatIcon, // for mat-icon
    CommonModule, // for ngIf and ngFor
    RouterLink, // for routerLink
    MatRadioModule, // for mat-radio-button
    FormsModule, // for ngModel
  ],
})
export class CheckoutComponent implements OnInit {
  checkoutForm!: FormGroup;
  reviewForm!: FormGroup;
  selectedPayment: PaymentMethod = PaymentMethod.PAY_ON_DELIVERY;
  reviewConfirmed = false;
  cartItems: any[] = [];
  total = 0;
  formSubmitted = false;

  public readonly authService: AuthService = inject(AuthService);
  public readonly cartService: CartService = inject(CartService);
  private readonly orderService: OrderService = inject(OrderService);
  private readonly router = inject(Router);
  private readonly fb: FormBuilder = inject(FormBuilder);

  ngOnInit(): void {
    this.checkoutForm = this.fb.group({
      fullName: ['', Validators.required],
      street: ['', Validators.required],
      city: ['', Validators.required],
      state: [''],
      zipCode: ['', [Validators.required, Validators.pattern('^[0-9]{5}$')]],
      country: ['', Validators.required],
      phone: ['', [Validators.required, Validators.pattern('^[+]?[0-9]{10,15}$')]],
    });

    this.reviewForm = this.fb.group({
      confirmed: [false, Validators.requiredTrue],
    });

    // ✅ Subscribe to cart changes
    this.cartService.cartItems$.subscribe((items) => {
      this.cartItems = items;
      this.total = this.cartService.getTotal();
    });
  }

  confirmReview(stepper: MatStepper): void {
    this.reviewForm.patchValue({ confirmed: true });
    stepper.next();
  }

  placeOrder(): void {
    if (this.checkoutForm.valid && this.reviewForm.valid) {
      const request: CreateOrderRequest = {
        shippingAddress: this.makeAddress(),
      };

      console.log('🚀 Calling REAL API createOrder');
      this.orderService.createOrder(request).subscribe({
        next: (response) => {
          if (response.success && response.data?.orderNumber) {
            this.cartService.clearCartAfterOrder();
            this.router.navigate(['/order-detail', response.data.orderNumber]);
          }
        },
        error: (err) => console.error('Order failed:', err), // ✅ Snackbar later
      });
    }
  }

  private makeAddress(): Address {
    const form = this.checkoutForm.value;
    return {
      fullName: form.fullName,
      street: form.street,
      city: form.city,
      state: form.state || '',
      zipCode: form.zipCode,
      country: form.country,
      phone: form.phone,
    };
  }
}
