/**
 * The User class represents a user in the system.
 * It contains properties for storing information about a user,
 * such as their name, username, email, and role.
 */
export class User {
  /**
   * The id of the user
   */
  public id : number | null;
  /**
   * The user's unique identifier
   */
  public userId: string;
  /**
   * The user's first name
   */
  public firstName: string;
  /**
   * The user's last name
   */
  public lastName: string;
  /**
   * The user's email address
   */
  public username: string;
  /**
   * The user's email address
   */
  public email: string;
  /**
   * The user's password
   */
  public password: string;
  /**
   * The date of the user's last login
   */
  public lastLoginDate: Date | null;
  /**
   * A string representation of the user's last login date for display purposes
   */
  public lastLoginDateDisplay: Date | null;
  /**
   * The date the user joined the system
   */
  public joinDate: Date | null;
  /**
   * The URL of the user's profile image
   */
  public profileImageUrl: string;
  /**
   * The active status of the user
   */
  public active: boolean;
  /**
   * The not locked status of the user
   */
  public notLocked: boolean;
  /**
   * The role of the user
   */
  public role: string;
  /**
   * The authorities of the user
   */
  public authorities: [];

  /**
   * Constructs a new User instance
   */
  constructor() {
    this.id = null;
    this.userId = '';
    this.firstName = '';
    this.lastName = '';
    this.username = '';
    this.email = '';
    this.password = '';
    this.lastLoginDate = null;
    this.lastLoginDateDisplay = null;
    this.joinDate = null;
    this.profileImageUrl = '';
    this.active = false;
    this.notLocked = false;
    this.role = '';
    this.authorities = [];
  }

}
