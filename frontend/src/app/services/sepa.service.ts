import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Observable, map, catchError, of, throwError } from 'rxjs';
import { SepaTransactionItem, SepaDetail, SepaApiResponse, EndpointDoc } from '../models/sepa.model';

@Injectable({
  providedIn: 'root'
})
export class SepaService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = '/sepa26';

  /**
   * Récupère la liste des dernières transactions (format XML parsé en objets TypeScript)
   */
  getTransactions(): Observable<SepaTransactionItem[]> {
    return this.http.get(`${this.baseUrl}/resume/xml`, {
      responseType: 'text',
      headers: new HttpHeaders({ 'Accept': 'application/xml' })
    }).pipe(
      map(xmlText => this.parseDocumentListXml(xmlText)),
      catchError(err => {
        console.error('Erreur chargement transactions:', err);
        return throwError(() => err);
      })
    );
  }

  /**
   * Récupère le flux XML brut de /resume/xml
   */
  getRawResumeXml(): Observable<string> {
    return this.http.get(`${this.baseUrl}/resume/xml`, {
      responseType: 'text',
      headers: new HttpHeaders({ 'Accept': 'application/xml' })
    }).pipe(
      catchError(err => of('Erreur lors de la récupération du flux XML.'))
    );
  }

  /**
   * Récupère le détail d'une transaction au format XML
   */
  getDetailXml(id: number): Observable<{ rawXml: string; detail?: SepaDetail; error?: string }> {
    return this.http.get(`${this.baseUrl}/xml/${id}`, {
      responseType: 'text',
      headers: new HttpHeaders({ 'Accept': 'application/xml' })
    }).pipe(
      map(xmlText => {
        const parser = new DOMParser();
        const doc = parser.parseFromString(xmlText, 'text/xml');
        const statusNode = doc.querySelector('SepaResponse > status');
        if (statusNode && statusNode.textContent?.trim() === 'ERROR') {
          return { rawXml: xmlText, error: 'Transaction non trouvée ou erreur serveur.' };
        }
        const detail = this.parseDocumentXml(doc, id, xmlText);
        return { rawXml: this.formatXml(xmlText), detail };
      }),
      catchError(err => of({ rawXml: '', error: 'Erreur réseau lors de la récupération du détail.' }))
    );
  }

  /**
   * Formate et indente une chaîne XML avec retours à la ligne
   */
  formatXml(xml: string): string {
    if (!xml) return '';
    try {
      let formatted = '';
      let indent = 0;
      const tab = '  ';
      const clean = xml.replace(/>\s*</g, '><').trim();
      const withBreaks = clean.replace(/(>)(<)(\/*)/g, '$1\r\n$2$3');
      const lines = withBreaks.split('\r\n');

      for (const rawLine of lines) {
        const line = rawLine.trim();
        if (!line) continue;

        if (line.match(/^<\/\w/)) {
          indent = Math.max(0, indent - 1);
        }

        formatted += tab.repeat(indent) + line + '\n';

        if (line.match(/^<[\w:]+[^>]*[^\/]>$/) && !line.startsWith('<?') && !line.startsWith('<!')) {
          indent++;
        }
      }
      return formatted.trim();
    } catch {
      return xml;
    }
  }

  /**
   * Récupère le rendu HTML officiel généré par le serveur via XSLT
   */
  getDetailHtml(id: number): Observable<string> {
    return this.http.get(`${this.baseUrl}/html/${id}`, {
      responseType: 'text',
      headers: new HttpHeaders({ 'Accept': 'text/html' })
    }).pipe(
      catchError(err => of('<div class="alert alert-danger">Erreur lors de la récupération du document HTML.</div>'))
    );
  }

  /**
   * Insère un document XML SEPA
   */
  insertXml(xmlData: string, validate: boolean = true): Observable<SepaApiResponse> {
    const params = new HttpParams().set('validate', String(validate));
    return this.http.post(`${this.baseUrl}/insert`, xmlData, {
      params,
      responseType: 'text',
      headers: new HttpHeaders({
        'Content-Type': 'application/xml',
        'Accept': 'application/xml'
      })
    }).pipe(
      map(xmlText => this.parseSepaResponse(xmlText)),
      catchError(err => of({
        status: 'ERROR',
        description: 'Erreur réseau lors de la transmission du document.'
      }))
    );
  }

  /**
   * Supprime une transaction par son ID
   */
  deleteTransaction(id: number): Observable<SepaApiResponse> {
    return this.http.delete(`${this.baseUrl}/delete/${id}`, {
      responseType: 'text',
      headers: new HttpHeaders({ 'Accept': 'application/xml' })
    }).pipe(
      map(xmlText => this.parseSepaResponse(xmlText)),
      catchError(err => of({
        status: 'ERROR',
        description: 'Erreur réseau lors de la suppression.'
      }))
    );
  }

  /**
   * Recherche de transactions par date et/ou montant
   */
  search(date?: string, sum?: number): Observable<{ status: string; items: SepaTransactionItem[] }> {
    let params = new HttpParams();
    if (date) params = params.set('date', date);
    if (sum !== undefined && sum !== null) params = params.set('sum', sum.toString());

    return this.http.get(`${this.baseUrl}/search`, {
      params,
      responseType: 'text',
      headers: new HttpHeaders({ 'Accept': 'application/xml' })
    }).pipe(
      map(xmlText => {
        const parser = new DOMParser();
        const doc = parser.parseFromString(xmlText, 'text/xml');
        const statusNode = doc.querySelector('status');
        const status = statusNode?.textContent?.trim() || 'OK';

        if (status === 'NONE') {
          return { status: 'NONE', items: [] };
        }
        if (status === 'ERROR') {
          return { status: 'ERROR', items: [] };
        }

        const items = this.parseDocumentListXml(xmlText);
        return { status: 'OK', items };
      }),
      catchError(err => of({ status: 'ERROR', items: [] }))
    );
  }

  /**
   * Récupère la liste des endpoints documentés par le backend (/help)
   */
  getHelpEndpoints(): Observable<EndpointDoc[]> {
    return this.http.get<EndpointDoc[]>(`${this.baseUrl}/help`).pipe(
      catchError(err => {
        console.error('Erreur chargement documentation endpoints:', err);
        return throwError(() => err);
      })
    );
  }

  // --- Méthodes privées de parsing XML ---

  private parseDocumentListXml(xmlText: string): SepaTransactionItem[] {
    const parser = new DOMParser();
    const xmlDoc = parser.parseFromString(xmlText, 'text/xml');
    const documents = xmlDoc.querySelectorAll('Document');
    const results: SepaTransactionItem[] = [];

    documents.forEach((doc, index) => {
      const pmtId = this.getNodeText(doc, 'PmtId') || `TX-${index + 1}`;
      const date = this.getNodeText(doc, 'CreDtTm') || this.getNodeText(doc, 'ReqdColltnDt') || 'N/A';
      const amountStr = this.getNodeText(doc, 'InstdAmt') || this.getNodeText(doc, 'CtrlSum') || '0';
      const instdAmtNode = this.findFirstNode(doc, 'InstdAmt');
      const currency = instdAmtNode?.getAttribute('Ccy') || 'EUR';
      const creditor = this.getNodeText(doc, 'Cdtr > Nm') || this.getNodeText(doc, 'InitgPty > Nm') || 'Non spécifié';
      const debtor = this.getNodeText(doc, 'Dbtr > Nm') || 'Non spécifié';
      const iban = this.getNodeText(doc, 'DbtrAcct > Id > IBAN') || this.getNodeText(doc, 'CdtrAcct > Id > IBAN') || '';
      const idStr = this.getNodeText(doc, 'id');
      const id = idStr ? parseInt(idStr, 10) : (index + 1);

      results.push({
        id,
        pmtId,
        date,
        amount: parseFloat(amountStr) || 0,
        currency,
        creditor,
        debtor,
        iban
      });
    });

    return results;
  }

  private parseDocumentXml(doc: Document, id: number, rawXml: string): SepaDetail {
    return {
      id,
      rawXml,
      msgId: this.getNodeText(doc, 'MsgId') || 'N/A',
      creDtTm: this.getNodeText(doc, 'CreDtTm') || 'N/A',
      nbOfTxs: this.getNodeText(doc, 'GrpHdr > NbOfTxs') || '1',
      ctrlSum: this.getNodeText(doc, 'GrpHdr > CtrlSum') || '0',
      initgPty: this.getNodeText(doc, 'InitgPty > Nm') || 'N/A',
      pmtInfId: this.getNodeText(doc, 'PmtInfId') || 'N/A',
      cdtrNm: this.getNodeText(doc, 'Cdtr > Nm') || 'N/A',
      cdtrIban: this.getNodeText(doc, 'CdtrAcct > Id > IBAN') || 'N/A',
      dbtrNm: this.getNodeText(doc, 'Dbtr > Nm') || 'N/A',
      dbtrIban: this.getNodeText(doc, 'DbtrAcct > Id > IBAN') || 'N/A',
      instdAmt: this.getNodeText(doc, 'InstdAmt') || '0',
      instdAmtCcy: this.findFirstNode(doc, 'InstdAmt')?.getAttribute('Ccy') || 'EUR',
      pmtId: this.getNodeText(doc, 'PmtId') || 'N/A',
      rmtInf: this.getNodeText(doc, 'RmtInf') || 'N/A'
    };
  }

  private parseSepaResponse(xmlText: string): SepaApiResponse {
    const parser = new DOMParser();
    const doc = parser.parseFromString(xmlText, 'text/xml');
    const statusNode = doc.querySelector('status');
    const idNode = doc.querySelector('id');
    const descNode = doc.querySelector('description') || doc.querySelector('message');

    const status = statusNode?.textContent?.trim() || 'UNKNOWN';
    const id = idNode?.textContent ? parseInt(idNode.textContent.trim(), 10) : undefined;
    const description = descNode?.textContent?.trim();

    return {
      id,
      status,
      description,
      rawResponse: xmlText
    };
  }

  private getNodeText(parent: ParentNode, selector: string): string | null {
    const node = parent.querySelector(selector);
    return node?.textContent?.trim() || null;
  }

  private findFirstNode(parent: ParentNode, tagName: string): Element | null {
    return parent.querySelector(tagName);
  }
}
