import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { SepaService } from '../../services/sepa.service';

@Component({
  selector: 'app-navbar',
  imports: [CommonModule, RouterLink, RouterLinkActive],
  templateUrl: './navbar.html',
  styleUrl: './navbar.css'
})
export class NavbarComponent {
  private readonly sepaService = inject(SepaService);

  readonly showXmlModal = signal<boolean>(false);
  readonly xmlData = signal<string>('');
  readonly loading = signal<boolean>(false);
  readonly copied = signal<boolean>(false);

  openXmlModal(event: Event): void {
    event.preventDefault();
    this.showXmlModal.set(true);
    this.loading.set(true);
    this.copied.set(false);

    this.sepaService.getRawResumeXml().subscribe({
      next: (xml) => {
        this.xmlData.set(this.formatXml(xml));
        this.loading.set(false);
      },
      error: () => {
        this.xmlData.set('Erreur lors du chargement du flux XML.');
        this.loading.set(false);
      }
    });
  }

  closeXmlModal(): void {
    this.showXmlModal.set(false);
  }

  copyXml(): void {
    navigator.clipboard.writeText(this.xmlData()).then(() => {
      this.copied.set(true);
      setTimeout(() => this.copied.set(false), 2000);
    });
  }

  private formatXml(xml: string): string {
    try {
      let formatted = '';
      let indent = '';
      const tab = '  ';
      xml.split(/>\s*</).forEach(node => {
        if (node.match(/^\/\w/)) indent = indent.substring(tab.length);
        formatted += indent + '<' + node + '>\r\n';
        if (node.match(/^<?\w[^>]*[^\/]$/)) indent += tab;
      });
      return formatted.trim();
    } catch {
      return xml;
    }
  }
}
