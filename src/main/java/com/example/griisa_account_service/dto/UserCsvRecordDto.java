package com.example.griisa_account_service.dto;

import com.opencsv.bean.CsvBindByName;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class UserCsvRecordDto {

    @CsvBindByName(column = "first_name")
    @NotBlank(message = "First name cannot be blank")
    private String firstName;

    @CsvBindByName(column = "last_name")
    private String lastName;

    @CsvBindByName(column = "email")
    @Email(message = "Invalid email format")
    @NotBlank(message = "Email cannot be blank")
    private String email;

    @CsvBindByName(column = "phone_number")
    @CsvBindByName(column = "phone_number")
    @Pattern(regexp = "\\d{10}", message = "Phone number must be 10 digits")
    private String phoneNumber;

    @CsvBindByName(column = "date_of_birth")
    @NotBlank(message = "Date of birth is required")
    private String dateOfBirth; // Keeping as String for now

    @CsvBindByName(column = "address_line_1")
    @NotBlank(message = "Address Line 1 is required")
    private String addressLine1;

    @CsvBindByName(column = "address_line_2")
    private String addressLine2;

    @CsvBindByName(column = "city")
    @NotBlank(message = "City is required")
    private String city;

    @CsvBindByName(column = "state")
    @NotBlank(message = "State is required")
    private String state;

    @CsvBindByName(column = "zip_code")
    private String zipCode;

    @CsvBindByName(column = "id_document_type")
    @NotBlank(message = "ID Document Type is required")
    private String idDocumentType;

    @CsvBindByName(column = "id_document_number")
    @NotBlank(message = "ID Document Number is required")
    private String idDocumentNumber;

    @CsvBindByName(column = "aadhaar_number")
    private String aadhaarNumber;

    @CsvBindByName(column = "pan_number")
    private String panNumber;

    // Getters and Setters

    // Optional: For better logging when converting to string
    @Override
    public String toString() {
        return String.format(
                "UserCsvRecordDto{firstName='%s', lastName='%s', email='%s', phone='%s', dob='%s'}",
                firstName, lastName, email, phoneNumber, dateOfBirth
        );
    }

    // Getters and setters omitted for brevity
    // You can use Lombok @Data if preferred
}
