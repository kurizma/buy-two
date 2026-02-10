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
  @Input() categoryAmounts?: number[];

  barChartData: ChartData<'bar'> = { labels: [], datasets: [] };
  pieChartData: ChartData<'pie'> = { labels: [], datasets: [{ data: [] }] };
  barType: ChartType = 'bar';
  pieType: ChartType = 'pie';
  barChartLabel = '';
  pieChartLabel = '';
  private readonly router = inject(Router);

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
        font: { size: 14, weight: 'bold' },
        formatter: () => this.barChartLabel,
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
        text: this.pieChartLabel,
      },
      legend: {
        display: true,
        position: 'bottom',
      },
      tooltip: {
        callbacks: {
          label: (context: any) => {
            const label = context.label || '';
            const value = context.parsed || 0;
            return `${label}: €${value.toFixed(2)}`;
          },
        },
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

    // ⭐ Bar chart best label
    this.barChartLabel = this.role === 'client' ? 'Most bought item' : 'Best seller';

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
          font: { size: 14, weight: 'bold' },
          formatter: () => this.barChartLabel,
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

    // ⭐ Pie chart label
    this.pieChartLabel =
      this.role === 'client' ? 'Top Categories by Spend' : 'Top Categories by Revenue';

    // Update pie chart title
    this.pieOptions = {
      ...this.pieOptions,
      plugins: {
        ...this.pieOptions.plugins,
        title: {
          display: true,
          text: this.pieChartLabel,
        },
      },
    };

    // ⭐ Pie chart using backend categoryAmounts
    if (this.categories && this.categories.length > 0) {
      const amounts =
        this.categoryAmounts?.length === this.categories.length &&
        this.categoryAmounts.every((a) => !Number.isNaN(a) && a > 0)
          ? this.categoryAmounts
          : this.categories.map(() => 1);

      this.pieChartData = {
        labels: this.categories,
        datasets: [
          {
            data: amounts,
            backgroundColor: ['#FF6384', '#36A2EB', '#FFCE56', '#4BC0C0', '#9966FF'],
          },
        ],
      };
    } else {
      this.pieChartData = {
        labels: ['No data'],
        datasets: [{ data: [1], backgroundColor: ['#CCCCCC'] }],
      };
    }
  }
}
