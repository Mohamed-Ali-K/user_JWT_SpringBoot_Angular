package com.kenis.supportportal.constant;

/**
 * This class contains constant values used for file handling in the application.
 * @author Mohamed Ali Kenis
 */
public class FileConstant {
    /**
     * The path for storing user images.
     */
    public static final String USER_IMAGE_PATH = "/user/image/";
    /**
     * The file extension for JPEG image files.
     */
    public static final String JPG_EXTENSION = "jpg";
    /**
     * The base directory for storing user files.
     */
    public static final String USER_FOLDER = System.getProperty("user.home") + "/supportportal/user/";
    /**
     * A message indicating that a directory was created.
     */
    public static final String DIRECTORY_CREATED = "Created directory for: ";
    /**
     * The base URL for the default profile image.
     */
    public static final String DEFAULT_USER_IMAGE_PATH = "/user/image/profile/";
    /**
     * A message indicating that a file was saved in the file system.
     */
    public static final String FILE_SAVED_IN_FILE_SYSTEM = "Saved file in file system by name: ";
    /**
     * The string representation of a period.
     */
    public static final String DOT = ".";
    /**
     * The string representation of a forward slash.
     */
    public static final String FORWARD_SLASH = "/";
    /**
     * An error message indicating that the uploaded file is not an image file.
     */
    public static final String NOT_AN_IMAGE_FILE = " is not an image file. Please upload an image file";
    /**
     * The base URL for the temporary profile image service.
     */
    public static final String TEMP_PROFILE_IMAGE_BASE_URL = "https://robohash.org/";
}
