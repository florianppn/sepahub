import { TestBed } from '@angular/core/testing';
import { of } from 'rxjs';
import { TransfertComponent } from './transfert';
import { SepaService } from '../../services/sepa.service';

describe('TransfertComponent', () => {
  let sepaServiceMock: {
    insertXml: ReturnType<typeof vi.fn>;
  };

  beforeEach(async () => {
    sepaServiceMock = {
      insertXml: vi.fn().mockReturnValue(of({ status: 'INSERTED', id: 42 }))
    };

    await TestBed.configureTestingModule({
      imports: [TransfertComponent],
      providers: [
        { provide: SepaService, useValue: sepaServiceMock }
      ]
    }).compileComponents();
  });

  it('should create the transfert component', () => {
    const fixture = TestBed.createComponent(TransfertComponent);
    const component = fixture.componentInstance;
    expect(component).toBeTruthy();
  });

  it('should display error alert when insertXml returns error status', () => {
    sepaServiceMock.insertXml.mockReturnValue(of({
      status: 'INVALID_XML',
      description: 'Document XML invalide par rapport au schéma XSD.'
    }));
    const fixture = TestBed.createComponent(TransfertComponent);
    const component = fixture.componentInstance;

    component.xmlContent.set('<invalid>XML</invalid>');
    component.submitXml();
    fixture.detectChanges();

    expect(component.errorMessage()).toContain('Document XML invalide');
    expect(component.errorStatus()).toBe('INVALID_XML');

    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.querySelector('.alert-danger')).toBeTruthy();
    expect(compiled.querySelector('.alert-title')?.textContent).toContain('INVALID_XML');
  });

  it('should dismiss error alert when close button is clicked', () => {
    const fixture = TestBed.createComponent(TransfertComponent);
    const component = fixture.componentInstance;

    component.errorMessage.set('Erreur temporaire');
    fixture.detectChanges();

    const compiled = fixture.nativeElement as HTMLElement;
    const closeBtn = compiled.querySelector('.alert-close') as HTMLButtonElement;
    expect(closeBtn).toBeTruthy();

    closeBtn.click();
    fixture.detectChanges();

    expect(component.errorMessage()).toBeNull();
    expect(compiled.querySelector('.alert-danger')).toBeNull();
  });
});
