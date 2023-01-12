import {Injectable} from '@angular/core';
import {HttpEvent, HttpHandler, HttpInterceptor, HttpRequest} from '@angular/common/http';
import {Observable} from 'rxjs';
import {AuthenticationService} from "../service/authentication.service";

/**
 * AuthInterceptor is an Angular HttpInterceptor that is responsible for adding the JWT token to the headers of all authenticated API requests.
 * It will automatically ignore requests to the unauthenticated endpoints: login, register, and reset-password.
 * @Injectable() decorator is used to make this class available to be injected as a dependency, by the Angular dependency injection system.
 * @class AuthInterceptorThis class represents the AuthInterceptor that implements the HttpInterceptor interface, it means this class can be used to intercept HTTP requests and responses.
 * @implements {HttpInterceptor} This class implements the HttpInterceptor interface, it means this class can be used to intercept HTTP requests and responses.
 *
 */
@Injectable()
export class AuthInterceptor implements HttpInterceptor {

  /**
   *
   * unauthenticatedEndpoints is an array of endpoints that do not require authentication.
   * @private
   * @type {string[]}
   * @memberof AuthInterceptor
   */
  private readonly unauthenticatedEndpoints = [
    `${this.authenticationService.host}/user/login`,
    `${this.authenticationService.host}/user/register`,
    `${this.authenticationService.host}/user/reset-password`
  ];

  /**
   *
   *   Creates an instance of AuthInterceptor.
   *   @param {AuthenticationService} authenticationService
   *   @memberof AuthInterceptor
   */
  constructor(private authenticationService: AuthenticationService) {
  }

  /**
   * Intercepts the Http request and adds an authorization header if the request's url is not an unauthenticated endpoint.
   * @param httpRequest the Http request to be intercepted
   * @param httpHandler
   * @returns the Http request with an added authorization header if the request's url is not an unauthenticated endpoint.
   */
  intercept(httpRequest: HttpRequest<any>, httpHandler: HttpHandler): Observable<HttpEvent<any>> {
    if (this.unauthenticatedEndpoints.some(endpoint => httpRequest.url.includes(endpoint))) {
      return httpHandler.handle(httpRequest);
    }
    this.authenticationService.loadToken();
    const token = this.authenticationService.getToken();
    const request = httpRequest.clone({setHeaders: {Authorization: `Bearer ${token}`}})
    return httpHandler.handle(request);
  }
}
