import { TestBed } from '@angular/core/testing';
import { of, throwError } from 'rxjs';
import { provideRouter } from '@angular/router';
import { TransactionsComponent } from './transactions';
import { SepaService } from '../../services/sepa.service';
import { SepaTransactionItem } from '../../models/sepa.model';

describe('TransactionsComponent', () => {
  const mockItems: SepaTransactionItem[] = [
    {
      id: 1,
      pmtId: 'TX-100',
      date: '2026-03-01',
      amount: 250.0,
      currency: 'EUR',
      creditor: 'Creditor 1',
      debtor: 'Debtor 1',
      iban: 'FR76001'
    }
  ];

  let sepaServiceMock: {
    getTransactions: ReturnType<typeof vi.fn>;
    deleteTransaction: ReturnType<typeof vi.fn>;
  };

  beforeEach(async () => {
    sepaServiceMock = {
      getTransactions: vi.fn().mockReturnValue(of(mockItems)),
      deleteTransaction: vi.fn().mockReturnValue(of({ status: 'DELETED', id: 1 }))
    };

    await TestBed.configureTestingModule({
      imports: [TransactionsComponent],
      providers: [
        provideRouter([]),
        { provide: SepaService, useValue: sepaServiceMock }
      ]
    }).compileComponents();
  });

  it('should create and load transactions successfully', () => {
    const fixture = TestBed.createComponent(TransactionsComponent);
    fixture.detectChanges();
    const component = fixture.componentInstance;

    expect(component).toBeTruthy();
    expect(sepaServiceMock.getTransactions).toHaveBeenCalled();
    expect(component.transactions().length).toBe(1);
    expect(component.errorMessage()).toBeNull();
  });

  it('should show in-place error state when service fails', () => {
    sepaServiceMock.getTransactions.mockReturnValue(throwError(() => new Error('Server down')));
    const fixture = TestBed.createComponent(TransactionsComponent);
    fixture.detectChanges();
    const component = fixture.componentInstance;

    expect(component.transactions().length).toBe(0);
    expect(component.errorMessage()).toContain('Le serveur backend SEPA26 ne répond pas');

    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.querySelector('.error-state-card')).toBeTruthy();
    expect(compiled.querySelector('.error-state-title')?.textContent).toContain('Impossible de charger les transactions');
  });

  it('should allow retrying when in error state', () => {
    sepaServiceMock.getTransactions.mockReturnValue(throwError(() => new Error('Server down')));
    const fixture = TestBed.createComponent(TransactionsComponent);
    fixture.detectChanges();
    const component = fixture.componentInstance;

    expect(component.errorMessage()).not.toBeNull();

    // Now mock success on retry
    sepaServiceMock.getTransactions.mockReturnValue(of(mockItems));
    component.loadTransactions();
    fixture.detectChanges();

    expect(component.errorMessage()).toBeNull();
    expect(component.transactions().length).toBe(1);
  });
});
