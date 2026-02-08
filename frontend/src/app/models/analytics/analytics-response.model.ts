import { AnalyticsItem } from './analytics.model';

export interface AnalyticsResponse {
  totalAmount: number;
  items: AnalyticsItem[];
  categories?: string[];
}
