import {Injectable} from '@angular/core';
import {environment} from "../../environments/environment.development";
import {HttpClient, HttpErrorResponse, HttpResponse} from "@angular/common/http";
import {Observable} from "rxjs";
import {User} from "../model/user";
import {JwtHelperService} from "@auth0/angular-jwt";

/**
 * AuthenticationService handles user authentication by making
 * HTTP requests to a backend for login, logout and registration.
 * it also handle token management and storage of user data locally
 * @author Mohamed Ali Kenis
 * @version 1.0
 */
@Injectable({
  providedIn: 'root'
})
export class AuthenticationService {
  /**
   * The base URL of the authentication service's API.
   */
  host = environment.apiUrl;
  /**
   * Token received from the server
   */
  private token: string | null = null;
  /**
   * logged in user name
   */
  private loggedInUsername: string | null = null;
  /**
   * JWTHelper service instance
   */
  private jwtHelper : JwtHelperService;

  constructor(private http: HttpClient, jwtHelper : JwtHelperService) {
    this.jwtHelper = jwtHelper;
  }

  /**
   * Send a login request to the backend with the provided user's credentials
   *
   * @param user - an object containing the user's credentials
   * @returns -  An observable of type `HttpResponse<any> | HttpErrorResponse`
   *  which means it is subscribing to the response of a http post request.
   */
  public login(user: User): Observable<HttpResponse<any> | HttpErrorResponse> {
    return this.http
      .post<HttpResponse<any> | HttpErrorResponse>(
        `${this.host}/user/login`, user, {observe: 'response'}
      );
  }
  /**
   * Send a registration request to the backend with the provided user's data
   *
   * @param user - an object containing the user's data
   * @returns - An observable of type `User | HttpErrorResponse`
   * which means it is subscribing to the response of a http post request.
   */
  public register(user: User): Observable<User | HttpErrorResponse> {
    return this.http
      .post<User | HttpErrorResponse>(
        `${this.host}/user/register`, user
      );
  }
  /**
   * Logout the current user by clearing the token and username from the service and local storage
   */
  public logout(): void {
    this.token = null;
    this.loggedInUsername = null;
    localStorage.removeItem('user');
    localStorage.removeItem('token');
    localStorage.removeItem('users');
  }

  /**
   * Save the received token in the service and local storage
   * @param token - JWT token received from the server
   */
  public saveToken(token: string): void {
    this.token = token;
    localStorage.setItem('token', token);
  }

  /**
   * save the user data in the local storage
   * @param user - the user data to save
   */
  public addUserToLocalStorage(user: User): void {
    localStorage.setItem('user', JSON.stringify(user));
  }
  /**
   * Retrieves the user data from the local storage
   * @returns user - the user data saved in the local storage
   */
  public getUserToLocalStorage(): User | null {
    const user = localStorage.getItem('user');
    return user ? JSON.parse(user) : null ;
  }
  /**
   * Retrieves the token from the local storage and sets it in the service
   */
  public loadToken(): void {
    this.token = localStorage.getItem('token')
  }
  /**
   * Retrieves the token from the service
   * @returns token - JWT token stored in the service
   */
  public getToken(): string | null {
    return this.token ;
  }
  /**
   * Validate the token is not expired and not tampered with
   *
   * It first checks if the token is not null or an empty string,
   * Then it uses the JwtHelperService to decode the token and extract the `sub` property which contains
   * the subject of the token (typically the user's unique identifier),
   * it checks if the sub is not null or empty string,
   * and checks if the token is expired or not by calling the isTokenExpired()
   * method from JwtHelperService
   * if all the checks pass it means the token is valid, so the method assigns the value of the `sub`
   * property to the `loggedInUsername` property and returns true.
   * If any of the checks fail, the method returns false.
   *
   * @returns true if the token is valid, false otherwise
   */

  private isTokenValid(): boolean {
    if (this.token != null && this.token != '') {
      const { sub } = this.jwtHelper.decodeToken(this.token);
      if (sub != null && sub !== '' && !this.jwtHelper.isTokenExpired(this.token)) {
        this.loggedInUsername = sub;
        return true;
      }
    }
    return false;
  }

  /**
   * Check if the user is logged in by checking the token and its validity
   *
   * It calls `loadToken()` to load the token from local storage into the service,
   * then it calls `isTokenValid()` to check if the token is valid or not
   * if the token is valid this mean the user is logged in it returns true
   * otherwise it calls logout method to clear the token and the user data
   * from the local storage and return false.
   *
   * @returns true if the user is logged in, false otherwise
   */
  public isLogin(): boolean {
    this.loadToken();
    if (this.isTokenValid()) {
      return true;
    } else {
      this.logout();
      return false;
    }
  }
}
