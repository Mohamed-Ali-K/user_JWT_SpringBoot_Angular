import {Component, OnDestroy, OnInit} from '@angular/core';
import {UntypedFormControl, UntypedFormGroup, Validators} from "@angular/forms";
import {Router} from "@angular/router";
import {AuthenticationService} from "../service/authentication.service";
import {ToastrService} from "ngx-toastr";
import {User} from "../model/user";
import {Subscription} from "rxjs";
import {HttpErrorResponse, HttpResponse} from "@angular/common/http";
import {HeaderType} from "../enum/header-type";
import {ErrorConstants} from "../constant/error-constants";

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit, OnDestroy {
  private subscriptions: Subscription[] = [];
  loginForm!: UntypedFormGroup;
  loading!: boolean;


  constructor(
    private router: Router,
    private authenticationService: AuthenticationService,
    private notificationService: ToastrService
  ) {
  }


  ngOnInit() {
    if (this.authenticationService.isLogin()) {
      this.router.navigateByUrl('/user/management').then(() => this.notificationService.info('You already logged in !', 'Information'));
    } else {
      this.router.navigateByUrl('/login').then(() => this.notificationService.info('Welcome !', 'Information'));
    }
    this.createForm();
  }

  private createForm() {
    const username = this.authenticationService.getUserFromLocalStorage()?.username

    this.loginForm = new UntypedFormGroup(
      {
      username: new UntypedFormControl(
        username,
        [Validators.required,
          Validators.pattern(
            '^[a-zA-Z0-9]+$'),
          Validators.minLength(
            5),
          Validators.maxLength(
            20)]),
      password: new UntypedFormControl(
        '',
        Validators.required),
      rememberMe: new UntypedFormControl(
        username !== null)
    });
  }

  onLogin(): void {
    this.loading = true;
    const user = new User();
    user.username = this.loginForm.get('username')?.value;
    user.password = this.loginForm.get('password')?.value;
    const rememberMe = this.loginForm.get('rememberMe')?.value;
    console.log(user);
    this.subscriptions.push(
      this.authenticationService.login(user).subscribe(
        {
        next: (response: HttpResponse<User>) => {
          const token: string | null = response.headers.get(HeaderType.JWT_TOKEN);
          this.authenticationService.saveToken(token);
          if (rememberMe) {
            this.authenticationService.addUserToLocalStorage(response.body);
          }
          this.router.navigateByUrl('/user/management').then(() => this.loading = false);
        },
        error: (httpErrorResponse: HttpErrorResponse) => {
          this.loading = false
          console.log(httpErrorResponse);
          const error = httpErrorResponse.error;
          this.sendErrorNotification(error.message,error.reason)
        }
      })
    );
  }

  sendErrorNotification(message: string, title: string): void {
    if (message) {
      this.notificationService.error(message,title);
    } else {
      this.notificationService.error(
        ErrorConstants.DEFAULT_ERROR.message,
        ErrorConstants.DEFAULT_ERROR.title
      );
    }
  }


  resetPassword() {
    this.router.navigate(['/auth/password-reset-request']);
  }


  ngOnDestroy(): void {
    this.subscriptions.forEach(sub => sub.unsubscribe());
  }
}
