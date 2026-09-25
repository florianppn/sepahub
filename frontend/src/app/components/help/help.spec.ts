import { TestBed } from '@angular/core/testing';
import { of, throwError } from 'rxjs';
import { provideRouter } from '@angular/router';
import { HelpComponent } from './help';
import { SepaService } from '../../services/sepa.service';
import { EndpointDoc } from '../../models/sepa.model';

describe('HelpComponent', () => {
  const mockEndpoints: EndpointDoc[] = [
    {
      url: '/sepa26/resume/xml',
      method: 'GET',
      format: 'XML',
      operation: 'Liste des transactions',
      description: 'Retourne la liste des transactions'
    },
    {
      url: '/sepa26/insert',
      method: 'POST',
      format: 'XML',
      operation: 'Ajout de transaction',
      description: 'Insère une transaction SEPA'
    }
  ];

  let sepaServiceMock: { getHelpEndpoints: ReturnType<typeof vi.fn> };

  beforeEach(async () => {
    sepaServiceMock = {
      getHelpEndpoints: vi.fn().mockReturnValue(of(mockEndpoints))
    };

    await TestBed.configureTestingModule({
      imports: [HelpComponent],
      providers: [
        provideRouter([]),
        { provide: SepaService, useValue: sepaServiceMock }
      ]
    }).compileComponents();
  });

  it('should create and load endpoints from SepaService', () => {
    const fixture = TestBed.createComponent(HelpComponent);
    fixture.detectChanges();
    const component = fixture.componentInstance;

    expect(component).toBeTruthy();
    expect(sepaServiceMock.getHelpEndpoints).toHaveBeenCalled();
    expect(component.endpoints().length).toBe(2);
    expect(component.endpoints()[0].url).toBe('/sepa26/resume/xml');
    expect(component.loading()).toBe(false);
    expect(component.errorMessage()).toBeNull();
  });

  it('should handle service errors gracefully', () => {
    sepaServiceMock.getHelpEndpoints.mockReturnValue(throwError(() => new Error('Network error')));
    const fixture = TestBed.createComponent(HelpComponent);
    fixture.detectChanges();
    const component = fixture.componentInstance;

    expect(component.endpoints().length).toBe(0);
    expect(component.loading()).toBe(false);
    expect(component.errorMessage()).toContain('Impossible de charger');
  });
});
