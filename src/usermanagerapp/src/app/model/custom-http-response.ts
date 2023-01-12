/**
 * The CustomHttpResponse interface represents a custom HTTP response object
 * containing a status code, a status message, a reason and a message
 */
export interface CustomHttpResponse {
  /**
   * The HTTP status code of the response
   */
  httpStatusCode: number;
  /**
   * The HTTP status message of the response
   */
  httpStatus: string;
  /**
   * The reason for the response
   */
  reason: string;
  /**
   * The message of the response
   */
  message: string;
}
