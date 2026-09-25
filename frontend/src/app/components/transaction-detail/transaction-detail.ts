import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { DomSanitizer, SafeHtml } from '@angular/platform-browser';
import { SepaService } from '../../services/sepa.service';
import { SepaDetail } from '../../models/sepa.model';

@Component({
  selector: 'app-transaction-detail',
  imports: [CommonModule, RouterLink],
  templateUrl: './transaction-detail.html',
  styleUrl: './transaction-detail.css'
})
export class TransactionDetailComponent implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly sepaService = inject(SepaService);
  private readonly sanitizer = inject(DomSanitizer);

  readonly id = signal<number | null>(null);
  readonly detail = signal<SepaDetail | null>(null);
  readonly rawXml = signal<string>('');
  readonly rawHtml = signal<SafeHtml | null>(null);
  readonly loading = signal<boolean>(true);
  readonly errorMessage = signal<string | null>(null);
  readonly activeTab = signal<'structured' | 'xml' | 'xslt'>('structured');
  readonly copied = signal<boolean>(false);

  ngOnInit(): void {
    const paramId = this.route.snapshot.paramMap.get('id');
    if (paramId) {
      const parsed = parseInt(paramId, 10);
      this.id.set(parsed);
      this.loadDetail(parsed);
    } else {
      this.errorMessage.set('Identifiant de transaction manquant.');
      this.loading.set(false);
    }
  }

  loadDetail(id: number): void {
    this.loading.set(true);
    this.errorMessage.set(null);

    this.sepaService.getDetailXml(id).subscribe({
      next: (res) => {
        if (res.error) {
          this.errorMessage.set(res.error);
        } else {
          this.rawXml.set(res.rawXml);
          this.detail.set(res.detail || null);
        }
        this.loading.set(false);
      },
      error: () => {
        this.errorMessage.set('Erreur de connexion au serveur.');
        this.loading.set(false);
      }
    });

    // Précharge également le rendu HTML officiel du backend
    this.sepaService.getDetailHtml(id).subscribe({
      next: (html) => {
        this.rawHtml.set(this.sanitizer.bypassSecurityTrustHtml(html));
      }
    });
  }

  copyXml(): void {
    navigator.clipboard.writeText(this.rawXml()).then(() => {
      this.copied.set(true);
      setTimeout(() => this.copied.set(false), 2000);
    });
  }
}
