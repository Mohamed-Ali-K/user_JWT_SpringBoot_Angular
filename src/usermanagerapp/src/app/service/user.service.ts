import {Injectable} from '@angular/core';
import {environment} from "../../environments/environment.development";
import {HttpClient, HttpErrorResponse, HttpEvent} from "@angular/common/http";
import {Observable} from "rxjs";
import {User} from "../model/user";

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private host = environment.apiUrl;

  constructor(private http: HttpClient) {
  }

  public getUsers(): Observable<User[] | HttpErrorResponse> {
    return this.http.get<User[]>(
      `${this.host}user/list`
    );
  }

  public addUser(formData: FormData): Observable<User | HttpErrorResponse> {
    return this.http.post<User>(
      `${this.host}user/add`,
      formData);
  }

  public updateUser(formData: FormData): Observable<User | HttpErrorResponse> {
    return this.http.post<User>(
      `${this.host}user/update`,
      formData
    );
  }

  public resetPassword(email: string): Observable<any| HttpErrorResponse> {
    return this.http.get<User>(
      `${this.host}user/reset-password/${email}`
    );
  }

  public updateUserImage(formData: FormData):Observable<HttpEvent<User> | HttpErrorResponse> {
    return this.http.post<User>(
      `${this.host}user/updateProfileImage`,
      formData,
      {
        reportProgress:true,
        observe:'events'
      });
  }

  public deleteUser(identifier: string):Observable<any | HttpErrorResponse> {
    return this.http.delete<any>(
      `${this.host}user/delete/${identifier}`);
  }

  public addUsersToLocalCache(users:User[]):void{
    localStorage.setItem('users',JSON.stringify(users) );
  }

  public getUsersFromLocalCache():User[] | null{
    const users = localStorage.getItem('users');
    return users ? JSON.parse(users) : null;
  }

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
