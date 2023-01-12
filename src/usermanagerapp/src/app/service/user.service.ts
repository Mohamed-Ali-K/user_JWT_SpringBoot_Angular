import {Injectable} from '@angular/core';
import {environment} from "../../environments/environment.development";
import {HttpClient, HttpErrorResponse, HttpEvent} from "@angular/common/http";
import {Observable} from "rxjs";
import {User} from "../model/user";
import {CustomHttpResponse} from "../model/custom-http-response";

/**
 * The UserService class is an Angular service that handles all user-related
 * operations such as retrieving, adding, updating, and deleting users.
 *
 * @author Mohamed Ali Kenis
 */
@Injectable({
  providedIn: 'root'
})
export class UserService {
  private host = environment.apiUrl;

  /**
   * Constructs a new UserService instance with the given HTTP client.
   *
   * @param http the HTTP client used to make API calls
   */
  constructor(private http: HttpClient) {
  }

  /**
   * Retrieves a list of all users.
   *
   * @return an observable of an array of users or an HTTP error response
   */
  public getUsers(): Observable<User[] | HttpErrorResponse> {
    return this.http.get<User[]>(
      `${this.host}user/list`
    );
  }

  /**
   * Adds a new user.
   *
   * @param formData the form data containing the user information
   * @return an observable of the added user or an HTTP error response
   */
  public addUser(formData: FormData): Observable<User | HttpErrorResponse> {
    return this.http.post<User>(
      `${this.host}user/add`,
      formData);
  }

  /**
   * Updates an existing user.
   *
   * @param formData the form data containing the updated user information
   * @return an observable of the updated user or an HTTP error response
   */
  public updateUser(formData: FormData): Observable<User | HttpErrorResponse> {
    return this.http.post<User>(
      `${this.host}user/update`,
      formData
    );
  }

  /**
   * Resets the password for a user with the given email address.
   *
   * @param email the email address of the user whose password should be reset
   * @return an observable of a custom HTTP response or an HTTP error response
   */
  public resetPassword(email: string): Observable<CustomHttpResponse | HttpErrorResponse> {
    return this.http.get<CustomHttpResponse>(
      `${this.host}user/reset-password/${email}`
    );
  }

  /**
   * Update user profile image
   *
   * @param formData the form data containing the updated user information
   * @return an observable of the updated user or an HTTP error response
   */
  public updateUserImage(formData: FormData): Observable<HttpEvent<User> | HttpErrorResponse> {
    return this.http.post<User>(
      `${this.host}user/updateProfileImage`,
      formData,
      {
        reportProgress: true,
        observe: 'events'
      });
  }

  /**
   * deletes user from the system based on the given identifier.
   * @param id : unique identifier of the user.
   * @return Observable<CustomHttpResponse | HttpErrorResponse> : returns observable of custom http response if successfull , or an error response if something went wrong.
   */
  public deleteUser(id: number): Observable<CustomHttpResponse | HttpErrorResponse> {
    return this.http.delete<CustomHttpResponse>(
      `${this.host}user/delete/${id}`);
  }

  /**
   * stores the users in the local storage.
   * @param users : list of users to be stored.
   */
  public addUsersToLocalCache(users: User[]): void {
    localStorage.setItem('users', JSON.stringify(users));
  }

  /**
   * retrieves users from local cache.
   * @returns an array of User objects or null if not found.
   */
  public getUsersFromLocalCache(): User[] | null {
    const users = localStorage.getItem('users');
    return users ? JSON.parse(users) : null;
  }
  /**
   * creates a FormData object for adding/updating user.
   * @param loggedInUserName the current user's username
   * @param user an User object.
   * @param profileImage a File object representing the user's profile image
   * @returns a FormData object
   */
  public createUserFormDate(loggedInUserName: string, user: User, profileImage: File): FormData {
    const formDate = new FormData();
    formDate.append('currentUsername', loggedInUserName);
    formDate.append('firstName', user.firstName);
    formDate.append('lastName', user.lastName);
    formDate.append('username', user.username);
    formDate.append('email', user.email);
    formDate.append('role', user.role);
    formDate.append('isActive', JSON.stringify(user.active));
    formDate.append('isNotLocked', JSON.stringify(user.notLocked));
    formDate.append('profileImage', profileImage);
    return formDate;
  }

}
