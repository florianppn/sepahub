import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { SepaService } from '../../services/sepa.service';
import { SepaTransactionItem, SepaApiResponse } from '../../models/sepa.model';

@Component({
  selector: 'app-transactions',
  imports: [CommonModule, RouterLink],
  templateUrl: './transactions.html',
  styleUrl: './transactions.css'
})
export class TransactionsComponent implements OnInit {
  private readonly sepaService = inject(SepaService);

  readonly transactions = signal<SepaTransactionItem[]>([]);
  readonly loading = signal<boolean>(false);
  readonly errorMessage = signal<string | null>(null);
  readonly feedback = signal<SepaApiResponse | null>(null);

  // Modal de confirmation de suppression
  readonly selectedTxForDelete = signal<SepaTransactionItem | null>(null);

  private feedbackTimeout: any = null;

  ngOnInit(): void {
    this.loadTransactions();
  }

  loadTransactions(): void {
    this.loading.set(true);
    this.errorMessage.set(null);
    this.sepaService.getTransactions().subscribe({
      next: (data) => {
        this.transactions.set(data);
        this.loading.set(false);
      },
      error: () => {
        this.errorMessage.set('Le serveur backend SEPA26 ne répond pas ou a rencontré une anomalie. Vérifiez que l\'application Spring Boot est démarrée sur le port 8080.');
        this.loading.set(false);
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

    this.loading.set(true);
    this.sepaService.deleteTransaction(tx.id).subscribe({
      next: (res) => {
        this.selectedTxForDelete.set(null);
        this.showToast(res);
        this.loadTransactions();
      },
      error: () => {
        this.selectedTxForDelete.set(null);
        this.showToast({ status: 'ERROR', description: 'Erreur lors de la suppression de la transaction.' });
        this.loading.set(false);
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
}
