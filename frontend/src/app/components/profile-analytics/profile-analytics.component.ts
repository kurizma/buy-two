import { Component, Input, OnChanges } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ChartData, ChartOptions, ChartType } from 'chart.js';
import { BaseChartDirective } from 'ng2-charts';
import { AnalyticsItem } from '../../models/profile/analytics-item';

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

  private readonly CATEGORY_MAP: Record<string, string> = {
    'CAT-001': 'code-nerd',
    'CAT-002': 'anime-pop',
    'CAT-003': 'code-queen',
    'CAT-004': 'gaming-esports',
    'CAT-005': 'geeky-memes',
    'CAT-006': 'limited-editions',
  };

  getCategorySlug(categoryId: string | string[]): string {
    const id = Array.isArray(categoryId) ? categoryId[0] : categoryId;
    return this.CATEGORY_MAP[id] || id || 'uncategorized';
  }

  barOptions: ChartOptions<'bar'> = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: {
      title: {
        display: true,
        text: '',
      },
    },
  };
  pieOptions: ChartOptions<'pie'> = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: {
      title: {
        display: true,
        text: 'Category Breakdown (%)',
      },
    },
  };

  ngOnChanges(): void {
    if (!this.items.length) return;

    const maxIdx = this.items.reduce(
      (maxIdx, item, idx) => (item.count > this.items[maxIdx].count ? idx : maxIdx),
      0,
    );
    this.bestLabel = this.role === 'user' ? 'Most bought item' : 'Best seller';

    // update bar title now that role is known
    this.barOptions = {
      ...this.barOptions,
      plugins: {
        ...this.barOptions.plugins,
        title: {
          display: true,
          text: this.role === 'user' ? 'Most Bought Items' : 'Best Selling Products',
        },
      },
    };
    this.barChartData = {
      labels: this.items.map((i) => i.name),
      datasets: [
        {
          label: 'Count',
          data: this.items.map((i) => i.count),
          backgroundColor: this.items.map((_, i) => (i === maxIdx ? 'gold' : '#0aeb7e')),
        },
      ],
    };

    const catSpend: { [key: string]: number } = {};
    this.items.forEach((item) => {
      // Fix: Handle array or string
      const rawCategory = Array.isArray(item.categories)
        ? item.categories[0] || 'Uncategorized' // First category or fallback
        : item.categories || 'Uncategorized';

      const category = this.getCategorySlug(rawCategory); // use slug if known
      catSpend[category] = (catSpend[category] || 0) + item.amount;
    });

    this.pieChartData = {
      labels: Object.keys(catSpend),
      datasets: [
        {
          data: Object.values(catSpend).filter((v) => v > 0), // Only positive spends
          backgroundColor: ['#FF6384', '#36A2EB', '#FFCE56', '#4BC0C0', '#9966FF'],
        },
      ],
    };
  }
}
