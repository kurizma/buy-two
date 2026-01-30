import { Component, OnInit, inject } from '@angular/core';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatStepper, MatStepperModule } from '@angular/material/stepper';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { CartService } from '../../services/cart.service';
import { CommonModule } from '@angular/common';
import { MatIcon } from '@angular/material/icon';

@Component({
  selector: 'app-order-checkout',
  templateUrl: './order-checkout.component.html',
  styleUrls: ['./order-checkout.component.css'],
  imports: [
    MatStepper,
    MatStepperModule,
    ReactiveFormsModule, // for FormGroup and FormBuilder
    MatFormFieldModule, // for mat-form-field
    MatInputModule, // for matInput
    MatButtonModule, // for mat-button
    MatIcon, // for mat-icon
    CommonModule, // for ngIf and ngFor
  ],
})
export class OrderCheckoutComponent implements OnInit {
  checkoutForm: FormGroup;
  cartItems: any[] = [];
  total = 0;
  formSubmitted = false;

  public cartService: CartService = inject(CartService);
  private fb: FormBuilder = inject(FormBuilder);

  constructor() {
    this.checkoutForm = this.fb.group({
      firstname: ['', Validators.required],
      lastname: ['', Validators.required],
      address: ['', Validators.required],
      city: ['', Validators.required],
      zip: ['', Validators.required],
    });
  }

  ngOnInit() {
    this.cartItems = this.cartService.cartItems; // Load from service/localStorage
    this.total = this.cartService.getTotal();
  }

  goToNext(stepper: MatStepper) {
    this.formSubmitted = true;
    // Mark form as touched  to show errors
    this.checkoutForm.markAllAsTouched();

    if (this.checkoutForm.valid) {
      stepper.next();
    }
  }

  placeOrder() {
    // Call orders API with form value + cart
    console.log('Order placed with Pay on Delivery');
    // Navigate to orders or clear cart
  }
}
