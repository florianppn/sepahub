import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { SepaService } from '../../services/sepa.service';
import { SepaApiResponse } from '../../models/sepa.model';

@Component({
  selector: 'app-transfert',
  imports: [CommonModule, FormsModule],
  templateUrl: './transfert.html',
  styleUrl: './transfert.css'
})
export class TransfertComponent {
  private readonly sepaService = inject(SepaService);

  readonly xmlContent = signal<string>('');
  readonly validateXsd = signal<boolean>(true);
  readonly loading = signal<boolean>(false);

  // Pop-up de succès
  readonly showSuccessPopup = signal<boolean>(false);
  readonly successId = signal<number | null>(null);

  // Message d'erreur éventuel
  readonly errorMessage = signal<string | null>(null);
  readonly errorStatus = signal<string>('ERROR');

  private popupTimeout: any = null;

  loadSampleXml(): void {
    const randomId = Math.floor(Math.random() * 900000) + 100000;
    const now = new Date().toISOString().split('.')[0];
    const sample = `<?xml version="1.0" encoding="UTF-8"?>
<Document xmlns="http://univ.fr/sepa26">
  <CstmrDrctDbtInitn>
    <GrpHdr>
      <MsgId>MSG-${randomId}</MsgId>
      <CreDtTm>${now}</CreDtTm>
      <NbOfTxs>1</NbOfTxs>
      <CtrlSum>750.00</CtrlSum>
      <InitgPty>
        <Nm>Universite de Rouen Normandie</Nm>
      </InitgPty>
    </GrpHdr>
    <PmtInf>
      <PmtInfId>PMT-LOT-${randomId}</PmtInfId>
      <NbOfTxs>1</NbOfTxs>
      <CtrlSum>750.00</CtrlSum>
      <PmtTpInf>
        <SvcLvl><Cd>SEPA</Cd></SvcLvl>
        <LclInstrm><Cd>CORE</Cd></LclInstrm>
        <SeqTp>RCUR</SeqTp>
      </PmtTpInf>
      <ReqdColltnDt>2026-04-15</ReqdColltnDt>
      <Cdtr>
        <Nm>Universite de Rouen Normandie</Nm>
      </Cdtr>
      <CdtrAcct>
        <Id>
          <IBAN>FR7612345678901234567890123</IBAN>
        </Id>
      </CdtrAcct>
      <CdtrAgt>
        <FinInstnId>
          <BIC>ROUENSWNXXX</BIC>
        </FinInstnId>
      </CdtrAgt>
      <CdtrSchmeId>
        <Id>
          <PrvtId>
            <Othr>
              <Id>FR00ZZZ123456</Id>
              <SchmeNm>
                <Prtry>SEPA</Prtry>
              </SchmeNm>
            </Othr>
          </PrvtId>
        </Id>
      </CdtrSchmeId>
      <DrctDbtTxInf>
        <PmtId>REF-TX-${randomId}</PmtId>
        <InstdAmt Ccy="EUR">750.00</InstdAmt>
        <DrctDbtTx>
          <MndtRltdInf>
            <MndtId>MNDT-${randomId}</MndtId>
            <DtOfSgntr>2026-01-10</DtOfSgntr>
          </MndtRltdInf>
        </DrctDbtTx>
        <DbtrAgt>
          <FinInstnId>
            <BIC>ROUENSWNXXX</BIC>
          </FinInstnId>
        </DbtrAgt>
        <Dbtr>
          <Nm>Client Entreprise Normande</Nm>
        </Dbtr>
        <DbtrAcct>
          <Id>
            <IBAN>FR7698765432109876543210987</IBAN>
          </Id>
        </DbtrAcct>
        <RmtInf>Cotisation annuelle 2026 - Ref ${randomId}</RmtInf>
      </DrctDbtTxInf>
    </PmtInf>
  </CstmrDrctDbtInitn>
</Document>`;

    this.xmlContent.set(sample);
    this.errorMessage.set(null);
  }

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      const file = input.files[0];
      const reader = new FileReader();
      reader.onload = (e) => {
        const text = e.target?.result as string;
        this.xmlContent.set(text);
        this.errorMessage.set(null);
      };
      reader.readAsText(file);
    }
  }

  submitXml(): void {
    const raw = this.xmlContent().trim();
    if (!raw) {
      this.errorMessage.set('Veuillez saisir ou importer un document XML avant de procéder au transfert.');
      return;
    }

    this.loading.set(true);
    this.errorMessage.set(null);

    this.sepaService.insertXml(raw, this.validateXsd()).subscribe({
      next: (res) => {
        this.loading.set(false);
        if (res.status === 'INSERTED') {
          this.triggerSuccessPopup(res.id || null);
          this.errorMessage.set(null);
        } else {
          this.errorStatus.set(res.status);
          this.errorMessage.set(res.description || 'Le document soumis ne respecte pas le schéma XSD ou contient une erreur.');
        }
      },
      error: () => {
        this.loading.set(false);
        this.errorStatus.set('ERROR');
        this.errorMessage.set('Erreur lors de la communication avec le backend REST.');
      }
    });
  }

  triggerSuccessPopup(id: number | null): void {
    this.successId.set(id);
    this.showSuccessPopup.set(true);

    if (this.popupTimeout) {
      clearTimeout(this.popupTimeout);
    }
    this.popupTimeout = setTimeout(() => {
      this.showSuccessPopup.set(false);
    }, 4500);
  }

  dismissPopup(): void {
    this.showSuccessPopup.set(false);
    if (this.popupTimeout) {
      clearTimeout(this.popupTimeout);
    }
  }

  clear(): void {
    this.xmlContent.set('');
    this.errorMessage.set(null);
  }
}
