import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError, map, catchError } from 'rxjs';
import { environment } from '../../environments/environment';
import { ApiResponse } from '../models/api-response/api-response.model';
import { AnalyticsResponse } from '../models/analytics/analytics-response.model';
import { ClientMostBought, ClientTopCategory } from '../models/analytics/client-analytics.model';
import { SellerMostSold, SellerTopCategory } from '../models/analytics/seller-analytics.model';

@Injectable({
  providedIn: 'root',
})
export class AnalyticsService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = `${environment.apiBaseUrl}/api/analytics`;

  // ✅ GET /api/analytics/client/{userId}
  getClientAnalytics(userId: string): Observable<AnalyticsResponse> {
    console.log('📡 AnalyticsService.getClientAnalytics:', userId);
    return this.http.get<ApiResponse<any>>(`${this.apiUrl}/client/${userId}`).pipe(
      map((apiResponse: ApiResponse<any>) => {
        console.log('📥 Raw response:', apiResponse);
        if (!apiResponse.success) {
          throw new Error(apiResponse.message || 'Client analytics failed');
        }
        const data = apiResponse.data;
        return {
          totalAmount: Number(data.totalSpent), // BigDecimal → number
          items: data.mostBoughtProducts.map((p: ClientMostBought) => ({
            name: p.name,
            count: p.totalQty,
            amount: p.totalAmount ? Number(p.totalAmount) : 0, // For table
            categories: [], // Or populate from product lookup if needed
            productId: p.productId, // Optional
          })),
          categories: data.topCategories?.map((c: ClientTopCategory) => c.category) || [],
        };
      }),
      catchError(this.handleError),
    );
  }

  // ✅ GET /api/analytics/seller/{userId}
  getSellerAnalytics(userId: string): Observable<AnalyticsResponse> {
    console.log('📡 AnalyticsService.getSellerAnalytics:', userId);
    return this.http.get<ApiResponse<any>>(`${this.apiUrl}/seller/${userId}`).pipe(
      map((apiResponse: ApiResponse<any>) => {
        console.log('📥 Raw response:', apiResponse);
        if (!apiResponse.success) {
          throw new Error(apiResponse.message || 'Seller analytics failed');
        }
        const data = apiResponse.data;
        return {
          totalAmount: Number(data.totalRevenue || data.totalSpent), // Seller field
          items:
            data.bestSellingProducts?.map((p: SellerMostSold) => ({
              // Adjust field name
              name: p.name,
              count: p.totalQty,
              amount: p.totalRevenue ? Number(p.totalRevenue) : 0,
              categories: [],
              productId: p.productId,
            })) || [],
          categories: data.topCategories?.map((c: SellerTopCategory) => c.category) || [],
        };
      }),
      catchError(this.handleError),
    );
  }

  private handleError(error: HttpErrorResponse): Observable<never> {
    console.error('Analytics API error:', error);
    let msg = 'Failed to load analytics';
    if (error.status === 404) msg = 'No analytics data found';
    else if (error.status === 401) msg = 'Please log in again';
    return throwError(() => new Error(msg));
  }
}
