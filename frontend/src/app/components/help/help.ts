import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { SepaService } from '../../services/sepa.service';
import { EndpointDoc } from '../../models/sepa.model';

@Component({
  selector: 'app-help',
  imports: [CommonModule, RouterLink],
  templateUrl: './help.html',
  styleUrl: './help.css'
})
export class HelpComponent implements OnInit {
  private readonly sepaService = inject(SepaService);

  readonly endpoints = signal<EndpointDoc[]>([]);
  readonly loading = signal<boolean>(false);
  readonly errorMessage = signal<string | null>(null);

  ngOnInit(): void {
    this.loadEndpoints();
  }

  loadEndpoints(): void {
    this.loading.set(true);
    this.errorMessage.set(null);
    this.sepaService.getHelpEndpoints().subscribe({
      next: (data) => {
        const sanitized = data.map(ep => ({
          ...ep,
          description: ep.description ? ep.description.replace(/<br\s*\/?>/gi, '\n') : ''
        }));
        this.endpoints.set(sanitized);
        this.loading.set(false);
      },
      error: () => {
        this.errorMessage.set('Le serveur backend SEPA26 ne répond pas ou a rencontré une anomalie. Impossible de charger la documentation des routes.');
        this.loading.set(false);
      }
    });
  }
}
