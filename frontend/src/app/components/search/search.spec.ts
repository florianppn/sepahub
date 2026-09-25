import { TestBed } from '@angular/core/testing';
import { of } from 'rxjs';
import { provideRouter } from '@angular/router';
import { SearchComponent } from './search';
import { SepaService } from '../../services/sepa.service';
import { SepaTransactionItem } from '../../models/sepa.model';

describe('SearchComponent', () => {
  const mockItems: SepaTransactionItem[] = [
    {
      id: 1,
      pmtId: 'TX-1',
      date: '2026-03-01',
      amount: 150.0,
      currency: 'EUR',
      creditor: 'Creditor A',
      debtor: 'Debtor B',
      iban: 'FR76123'
    }
  ];

  let sepaServiceMock: {
    search: ReturnType<typeof vi.fn>;
    deleteTransaction: ReturnType<typeof vi.fn>;
  };

  beforeEach(async () => {
    sepaServiceMock = {
      search: vi.fn().mockReturnValue(of({ status: 'OK', items: mockItems })),
      deleteTransaction: vi.fn().mockReturnValue(of({ status: 'DELETED', id: 1 }))
    };

    await TestBed.configureTestingModule({
      imports: [SearchComponent],
      providers: [
        provideRouter([]),
        { provide: SepaService, useValue: sepaServiceMock }
      ]
    }).compileComponents();
  });

  it('should create and perform search', () => {
    const fixture = TestBed.createComponent(SearchComponent);
    const component = fixture.componentInstance;
    expect(component).toBeTruthy();

    component.searchDate.set('2026-03-01');
    component.performSearch();

    expect(sepaServiceMock.search).toHaveBeenCalledWith('2026-03-01', undefined);
    expect(component.results().length).toBe(1);
    expect(component.searchStatus()).toBe('OK');
  });

  it('should handle deletion of an item from search results', () => {
    const fixture = TestBed.createComponent(SearchComponent);
    const component = fixture.componentInstance;

    component.results.set([...mockItems]);
    component.searchStatus.set('OK');

    // Trigger ask delete
    component.askDelete(mockItems[0]);
    expect(component.selectedTxForDelete()).toEqual(mockItems[0]);

    // Confirm delete
    component.confirmDelete();
    expect(sepaServiceMock.deleteTransaction).toHaveBeenCalledWith(1);
    expect(component.results().length).toBe(0);
    expect(component.searchStatus()).toBe('NONE');
    expect(component.feedback()?.status).toBe('DELETED');
  });
});
