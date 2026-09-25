export interface SepaTransactionItem {
  id?: number;
  pmtId: string;
  date: string;
  amount: number;
  currency: string;
  creditor: string;
  debtor: string;
  iban: string;
}

export interface SepaDetail {
  id?: number;
  msgId: string;
  creDtTm: string;
  nbOfTxs: string;
  ctrlSum: string;
  initgPty: string;
  pmtInfId: string;
  cdtrNm: string;
  cdtrIban: string;
  dbtrNm: string;
  dbtrIban: string;
  instdAmt: string;
  instdAmtCcy: string;
  pmtId: string;
  rmtInf: string;
  rawXml: string;
}

export interface SepaApiResponse {
  id?: number;
  status: 'INSERTED' | 'DELETED' | 'ERROR' | 'OK' | 'NONE' | string;
  description?: string;
  rawResponse?: string;
}

export interface EndpointDoc {
  url: string;
  method: string;
  format: string;
  operation: string;
  description: string;
}

