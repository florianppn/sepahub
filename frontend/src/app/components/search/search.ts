import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { SepaService } from '../../services/sepa.service';
import { SepaTransactionItem, SepaApiResponse } from '../../models/sepa.model';

@Component({
  selector: 'app-search',
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './search.html',
  styleUrl: './search.css'
})
export class SearchComponent {
  private readonly sepaService = inject(SepaService);

  readonly searchDate = signal<string>('');
  readonly searchSum = signal<number | null>(null);
  readonly results = signal<SepaTransactionItem[]>([]);
  readonly searchStatus = signal<'IDLE' | 'LOADING' | 'OK' | 'NONE' | 'ERROR'>('IDLE');

  // Suppression et confirmation
  readonly selectedTxForDelete = signal<SepaTransactionItem | null>(null);
  readonly deleting = signal<boolean>(false);
  readonly feedback = signal<SepaApiResponse | null>(null);
  private feedbackTimeout: any = null;

  performSearch(): void {
    const dateVal = this.searchDate() || undefined;
    const sumVal = this.searchSum() !== null ? this.searchSum()! : undefined;

    this.searchStatus.set('LOADING');
    this.sepaService.search(dateVal, sumVal).subscribe({
      next: (res) => {
        if (res.status === 'NONE') {
          this.searchStatus.set('NONE');
          this.results.set([]);
        } else if (res.status === 'ERROR') {
          this.searchStatus.set('ERROR');
          this.results.set([]);
        } else {
          this.searchStatus.set('OK');
          this.results.set(res.items);
        }
      },
      error: () => {
        this.searchStatus.set('ERROR');
        this.results.set([]);
      }
    });
  }

  askDelete(tx: SepaTransactionItem): void {
    this.selectedTxForDelete.set(tx);
  }

  cancelDelete(): void {
    this.selectedTxForDelete.set(null);
  }

  confirmDelete(): void {
    const tx = this.selectedTxForDelete();
    if (!tx || !tx.id) return;

    this.deleting.set(true);
    this.sepaService.deleteTransaction(tx.id).subscribe({
      next: (res) => {
        this.selectedTxForDelete.set(null);
        this.deleting.set(false);
        this.showToast(res);
        this.results.update(list => list.filter(item => item.id !== tx.id));
        if (this.results().length === 0) {
          this.searchStatus.set('NONE');
        }
      },
      error: () => {
        this.selectedTxForDelete.set(null);
        this.deleting.set(false);
        this.showToast({ status: 'ERROR', description: 'Erreur lors de la suppression de la transaction.' });
      }
    });
  }

  private showToast(res: SepaApiResponse): void {
    this.feedback.set(res);
    if (this.feedbackTimeout) {
      clearTimeout(this.feedbackTimeout);
    }
    this.feedbackTimeout = setTimeout(() => {
      this.feedback.set(null);
    }, 4500);
  }

  dismissFeedback(): void {
    this.feedback.set(null);
    if (this.feedbackTimeout) {
      clearTimeout(this.feedbackTimeout);
    }
  }

  reset(): void {
    this.searchDate.set('');
    this.searchSum.set(null);
    this.results.set([]);
    this.searchStatus.set('IDLE');
    this.dismissFeedback();
  }
}
