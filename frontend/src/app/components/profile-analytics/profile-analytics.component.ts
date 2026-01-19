import { Component, Input, OnChanges } from '@angular/core';
import { CommonModule } from '@angular/common';
import { BaseChartDirective } from 'ng2-charts';
import { ChartData, ChartOptions, ChartType } from 'chart.js';

interface AnalyticsItem {
  name: string;
  categories: string; // CAT-001 etc
  count: number;
  amount: number;
}

@Component({
  selector: 'app-profile-analytics',
  standalone: true,
  imports: [CommonModule, BaseChartDirective],
  templateUrl: './profile-analytics.component.html',
  styleUrls: ['./profile-analytics.component.css'],
})
export class ProfileAnalyticsComponent implements OnChanges {
  @Input() role!: 'user' | 'seller';
  @Input() totalAmount = 0;
  @Input() items: AnalyticsItem[] = [];
  @Input() categories?: string[];

  barChartData: ChartData<'bar'> = { labels: [], datasets: [] };
  pieChartData: ChartData<'pie'> = { labels: [], datasets: [{ data: [] }] };
  barType: ChartType = 'bar';
  pieType: ChartType = 'pie';
  bestLabel = '';

  barOptions: ChartOptions<'bar'> = {
    responsive: true,
    plugins: {
      title: {
        display: true,
        text: this.role === 'user' ? 'Most Bought Items' : 'Best Selling Products',
      },
    },
  };
  pieOptions: ChartOptions<'pie'> = {
    responsive: true,
    plugins: { title: { display: true, text: 'Category Breakdown (%)' } },
  };

  ngOnChanges(): void {
    if (this.items.length) {
      const maxIdx = this.items.reduce(
        (maxIdx, item, idx) => (item.count > this.items[maxIdx].count ? idx : maxIdx),
        0,
      );
      this.bestLabel = this.role === 'user' ? 'Most bought item' : 'Best seller';

      this.barChartData = {
        labels: this.items.map((i) => i.name),
        datasets: [
          {
            label: 'Count',
            data: this.items.map((i) => i.count),
            backgroundColor: this.items.map((_, i) => (i === maxIdx ? 'gold' : '#2196F3')),
          },
        ],
      };

      // Auto pie from categories in items
      const catSpend: { [key: string]: number } = {};
      this.items.forEach((item) => {
        catSpend[item.categories] = (catSpend[item.categories] || 0) + item.amount;
      });
      const totalSpend = this.items.reduce((sum, i) => sum + i.amount, 0);
      this.pieChartData = {
        labels: Object.keys(catSpend),
        datasets: [
          {
            data: Object.values(catSpend).map((spend) => (spend / totalSpend) * 100),
          },
        ],
      };
    }
  }
}
