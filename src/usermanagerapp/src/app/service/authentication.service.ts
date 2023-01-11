import {Injectable} from '@angular/core';
import {environment} from "../../environments/environment.development";
import {HttpClient, HttpErrorResponse, HttpResponse} from "@angular/common/http";
import {Observable} from "rxjs";
import {User} from "../model/user";



@Injectable({
  providedIn: 'root'
})
export class AuthenticationService {
  private host = environment.apiUrl;
  private token: string;
  private loggedInUsername: string;

  constructor(private http: HttpClient) {
  }

  public login(user: User): Observable<HttpResponse<any> | HttpErrorResponse> {
    return this.http
      .post<HttpResponse<any> | HttpErrorResponse>(
        `${this.host}/user/login`, user, {observe: 'response'}
      );
  }

  public register(user: User): Observable<User | HttpErrorResponse> {
    return this.http
      .post<User | HttpErrorResponse>(
        `${this.host}/user/register`, user
      );
  }

  public logout(): void{
   this.token = null;
   this.loggedInUsername = null;
   localStorage.removeItem('user');
   localStorage.removeItem('token');
    localStorage.removeItem('users');
  }

  public saveToken(token:string): void{
    this.token = token;
    localStorage.setItem('token', token);
  }

}