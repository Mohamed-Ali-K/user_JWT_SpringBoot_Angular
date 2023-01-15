import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, RouterStateSnapshot, Router } from '@angular/router';
import {AuthenticationService} from "../service/authentication.service";
import {ToastrService} from "ngx-toastr";

@Injectable({
  providedIn: 'root'
})
export class AuthenticationGuard implements CanActivate {


  constructor(
    private authenticationService: AuthenticationService,
    private router:Router,
    private notification: ToastrService
  ) {
  }

  canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot): boolean{
    return this.isUserLoggedIn();
  }


  private isUserLoggedIn(): boolean {
    const isLoggedIn = this.authenticationService.isLogin();
    if (!isLoggedIn) {
      this.router.navigate(['/login']).then(() => {
        // Navigated successfully. Do something here.
        this.notification.error('you need to login to access this page'.toUpperCase())
        console.log("Navigated to login page.");
      }).catch(error => {
        // Navigation failed. Handle the error here.
        console.log("Navigation failed. Error: ", error);
      });
    }
    return isLoggedIn;
  }

}
