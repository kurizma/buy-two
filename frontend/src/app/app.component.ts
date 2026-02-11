import { Component } from '@angular/core';
import { ReactiveFormsModule, FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';

// Import Angular Material modules used in the app
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { CommonModule } from '@angular/common';
// ...import other material modules as needed

// ...import other components as needed
import { NavigationComponent } from './components/ui/navigation/navigation.component';
import { FooterComponent } from './components/ui/footer/footer.component';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css'],
  standalone: true,
  imports: [
    RouterModule,
    ReactiveFormsModule,
    CommonModule,
    FormsModule,
    MatCardModule,
    MatButtonModule,
    NavigationComponent,
    FooterComponent,
    // ...all material modules used
    // ...other components
  ],
})
export class AppComponent {
  constructor() {
    console.log('App root loaded');
  }
}
