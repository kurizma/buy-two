import { Component, Input, OnChanges, inject } from '@angular/core';
import { Router } from '@angular/router';
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
  @Input() role!: 'client' | 'seller';
  @Input() totalAmount = 0;
  @Input() items: AnalyticsItem[] = [];
  @Input() categories?: string[];

  barChartData: ChartData<'bar'> = { labels: [], datasets: [] };
  pieChartData: ChartData<'pie'> = { labels: [], datasets: [{ data: [] }] };
  barType: ChartType = 'bar';
  pieType: ChartType = 'pie';
  bestLabel = '';
  private readonly router = inject(Router);

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

  addFirstProduct(): void {
    if (this.role === 'seller') {
      this.router.navigate(['/seller-dashboard']);
    }
  }

  barOptions: ChartOptions<'bar'> = {
    responsive: true,
    maintainAspectRatio: false,
    scales: {
      y: {
        beginAtZero: true,
        max: undefined,
        ticks: { stepSize: 1 },
      },
    },
    plugins: {
      title: {
        display: true,
        text: '',
      },
      datalabels: {
        display: 'auto',
        anchor: 'end',
        align: 'top',
        font: { size: 12, weight: 'bold' },
        formatter: () => this.bestLabel,
        color: 'black',
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

    // ⭐ Bar chart highest → rightmost
    const sortedItems = [...this.items].sort((a, b) => a.count - b.count);

    const maxIdx = sortedItems.length - 1;

    console.log(
      'Sorted USER:',
      sortedItems.map((i) => `${i.name}(${i.count})`),
    ); // ⭐

    this.bestLabel = this.role === 'client' ? 'Most bought item' : 'Best seller';

    // update bar title CLIENT vs. SELLER
    this.barOptions = {
      ...this.barOptions,
      plugins: {
        ...this.barOptions.plugins,
        title: {
          display: true,
          text: this.role === 'client' ? 'Most Bought Items' : 'Best Selling Products',
        },
        datalabels: {
          display: (context: any) => context.dataIndex === maxIdx,
          anchor: 'end',
          align: 'top',
          font: { size: 12, weight: 'bold' },
          formatter: () => this.bestLabel,
          color: 'black',
        },
      },
    };
    this.barChartData = {
      labels: sortedItems.map((i) => i.name),
      datasets: [
        {
          label: 'Units',
          data: sortedItems.map((i) => i.count),
          backgroundColor: sortedItems.map((_, i) => (i === maxIdx ? 'gold' : '#0aeb7e')),
        },
      ],
    };

    // ⭐ Pie chart data aggregation
    const catSpend: { [key: string]: number } = {};
    sortedItems.forEach((item) => {
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
