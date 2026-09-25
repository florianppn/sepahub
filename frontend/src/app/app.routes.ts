import { Routes } from '@angular/router';
import { HomeComponent } from './components/home/home';
import { TransactionsComponent } from './components/transactions/transactions';
import { TransactionDetailComponent } from './components/transaction-detail/transaction-detail';
import { TransfertComponent } from './components/transfert/transfert';
import { SearchComponent } from './components/search/search';
import { HelpComponent } from './components/help/help';
import { NotFoundComponent } from './components/not-found/not-found';

export const routes: Routes = [
  { path: '', component: HomeComponent },
  { path: 'transactions', component: TransactionsComponent },
  { path: 'transactions/:id', component: TransactionDetailComponent },
  { path: 'transfert', component: TransfertComponent },
  { path: 'search', component: SearchComponent },
  { path: 'help', component: HelpComponent },
  { path: '**', component: NotFoundComponent }
];
