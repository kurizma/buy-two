import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError, map, catchError } from 'rxjs';
import { environment } from '../../environments/environment';
import { ApiResponse } from '../models/api-response/api-response.model';
import { AnalyticsResponse } from '../models/analytics/analytics-response.model';

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
        console.log('🔍 topCategories raw:', data.topCategories); // ✅ Debug

        return {
          totalAmount: Number(data.totalSpent || 0),
          items: data.mostBoughtProducts.map((p: any) => ({
            name: p.name,
            count: p.totalQty,
            amount: p.totalAmount ? Number(p.totalAmount) : 0,
            categories: [p.category || 'N/A'],
            productId: p.productId, // Optional
          })),
          categories: (data.topCategories || []).map((c: any) => c.category),
          categoryAmounts: data.topCategories.map((c: any) => Number(c.totalSpent || 0)),
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
        console.log('🔍 seller topCategories raw:', data.topCategories); // ✅ Debug

        return {
          totalAmount: Number(data.totalRevenue || 0),
          items:
            data.bestSellingProducts?.map((p: any) => ({
              // Adjust field name
              name: p.name,
              count: p.unitsSold,
              amount: p.revenue ? Number(p.revenue) : 0,
              categories: [p.category || 'N/A'],
              productId: p.productId,
            })) || [],
          categories: (data.topCategories || []).map((c: any) => c.category),
          categoryAmounts: data.topCategories.map((c: any) => Number(c.totalRevenue || 0)),
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
