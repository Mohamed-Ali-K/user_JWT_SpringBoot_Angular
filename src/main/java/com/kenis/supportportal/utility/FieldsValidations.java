package com.kenis.supportportal.utility;

import com.kenis.supportportal.exception.domain.BlankFieldException;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;


import static com.kenis.supportportal.constant.UserImpConstant.BLANK_FIELD_MESSAGE;
import static org.apache.commons.lang3.StringUtils.isBlank;

@Component
public class FieldsValidations {

    @AllArgsConstructor
    @Data
    public static class Field {
        private String name;
        private String value;
    }

    @Data
    @AllArgsConstructor
    public static class FieldDTO {
        private String username;
        private String email;
        private String firstName;
        private String lastName;
    }

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

    public void validateFieldsLogin(String username, String password) throws BlankFieldException {
        validateFields(
                List.of(
                        new Field("Username", username),
                        new Field("Password", password)
                )
        );
    }

    public void validationField(String fieldName, String field) throws BlankFieldException {
        validateFields(List.of(new Field(fieldName, field)));
    }

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