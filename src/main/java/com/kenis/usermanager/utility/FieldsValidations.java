package com.kenis.usermanager.utility;

import com.kenis.usermanager.exception.domain.BlankFieldException;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;


import static com.kenis.usermanager.constant.UserImpConstant.BLANK_FIELD_MESSAGE;
import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * Utility class for validating various fields within the application.
 * This class provides methods to check for blank or empty fields in contexts
 * such as user registration, login, and other general field validations.
 */
@Component
public class FieldsValidations {

    /**
     * Represents a field to be validated, containing its name (for error messages)
     * and its value.
     */
    @AllArgsConstructor
    @Data
    public static class Field {
        private String name;
        private String value;
    }

    /**
     * Data Transfer Object (DTO) used for passing user registration-specific fields
     * to validation methods.
     */
    @Data
    @AllArgsConstructor
    public static class FieldDTO {
        private String username;
        private String email;
        private String firstName;
        private String lastName;
    }

    /**
     * Validates fields required for user registration (username, email, first name, last name) from a {@code FieldDTO}.
     *
     * @param fieldDTO The {@code FieldDTO} object containing registration fields.
     * @throws BlankFieldException if any of the required registration fields are blank.
     */
    public void validateFieldsRegistration(FieldDTO fieldDTO) throws BlankFieldException {
        validateFields(
                List.of(
                        new Field("Username", fieldDTO.getUsername()),
                        new Field("Email", fieldDTO.getEmail()),
                        new Field("First Name", fieldDTO.getFirstName()),
                        new Field("Last Name", fieldDTO.getLastName())
                )
        );
    }

    /**
     * Validates fields required for user login (username, password).
     *
     * @param username The username provided for login.
     * @param password The password provided for login.
     * @throws BlankFieldException if username or password is blank.
     */
    public void validateFieldsLogin(String username, String password) throws BlankFieldException {
        validateFields(
                List.of(
                        new Field("Username", username),
                        new Field("Password", password)
                )
        );
    }

    /**
     * Validates a single field to ensure it is not blank.
     *
     * @param fieldName The name of the field being validated (used in the exception message).
     * @param field The value of the field to validate.
     * @throws BlankFieldException if the field is blank.
     */
    public void validationField(String fieldName, String field) throws BlankFieldException {
        validateFields(List.of(new Field(fieldName, field)));
    }

    /**
     * Validates a list of {@code Field} objects, ensuring none of them are blank.
     *
     * @param fields A list of {@code Field} objects to validate.
     * @throws BlankFieldException if any field in the list is blank.
     */
    public void validateFields(List<Field> fields) throws BlankFieldException {
        List<String> invalidFieldsNames = fields.stream()
                .filter(f -> isBlank(f.getValue()))
                .map(Field::getName)
                .collect(Collectors.toList());
        if (!invalidFieldsNames.isEmpty()) {
            String joinedNames = String.join(", ", invalidFieldsNames);
            String message = invalidFieldsNames.size() == 1 ? joinedNames + " is" : joinedNames + " are";
            throw new BlankFieldException(message + BLANK_FIELD_MESSAGE);
        }
    }
}