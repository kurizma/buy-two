import { Component, Input, OnChanges, inject } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common';
import { ChartData, ChartOptions, ChartType } from 'chart.js';
import { BaseChartDirective } from 'ng2-charts';
import { AnalyticsItem } from '../../models/analytics/analytics.model';

@Component({
  selector: 'app-profile-analytics',
  standalone: true,
  imports: [CommonModule, BaseChartDirective, RouterLink],
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

    const maxCount = sortedItems.at(-1)!.count;
    const maxIndices: number[] = [];
    sortedItems.forEach((item, idx) => {
      if (item.count === maxCount) maxIndices.push(idx);
    });

    console.log(
      'Sorted USER:',
      sortedItems.map((i) => `${i.name}(${i.count})`),
    );

    // ⭐ Bar chart best label
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
          display: (context: any) => maxIndices.includes(context.dataIndex),
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
          label: 'Qty',
          data: sortedItems.map((i) => i.count),
          backgroundColor: sortedItems.map((_, i) => (maxIndices.includes(i) ? 'gold' : '#0aeb7e')),
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
      catSpend[category] = (catSpend[category] || 0) + item.count;
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
