import {Injectable} from "@angular/core";

import { environment } from '../../environments/environment';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { CreatePortingRequest, PortingRequest, MobileNumberStatus } from '../models/porting.model';

@Injectable({providedIn: 'root'})
export class PortingService{

  private readonly baseUrl = environment.apiUrl;

  constructor(private http: HttpClient) {}

  createRequest(request: CreatePortingRequest, organization: string): Observable<PortingRequest> {
    const headers = new HttpHeaders({ organization });
    return this.http.post<PortingRequest>(`${this.baseUrl}/porting-requests`, request, { headers });
  }

  getRequests(organization: string): Observable<PortingRequest[]> {
    const headers = new HttpHeaders({ organization });
    return this.http.get<PortingRequest[]>(`${this.baseUrl}/porting-requests`, { headers });
  }

  getNumberStatus(phoneNumber: string, organization: string): Observable<MobileNumberStatus> {
    const headers = new HttpHeaders({organization});
    return this.http.get<MobileNumberStatus>(`${this.baseUrl}/mobile-numbers/${phoneNumber}/status`, {headers});
  }

}
