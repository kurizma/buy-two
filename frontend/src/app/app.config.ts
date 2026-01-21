import { ApplicationConfig, provideZoneChangeDetection } from '@angular/core';
import { provideRouter } from '@angular/router';
import { routes } from './app.routes';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { authInterceptor } from '../interceptors/auth.interceptor';
import { provideCharts, withDefaultRegisterables } from 'ng2-charts';
import ChartDataLabels from 'chartjs-plugin-datalabels';

export const appConfig: ApplicationConfig = {
  providers: [
    provideZoneChangeDetection({ eventCoalescing: true }),
    provideRouter(routes),
    provideHttpClient(withInterceptors([authInterceptor])),
    provideCharts(withDefaultRegisterables()),
    { provide: 'CUSTOM_ELEMENTS_SCHEMA', useValue: ChartDataLabels, multi: true },
  ],
};
