package com.example.griisa_account_service.dto;

import com.opencsv.bean.CsvBindByName;
import lombok.Data;

@Data
public class UserCsvRecordDto {

    @CsvBindByName(column = "first_name")
    private String firstName;

    @CsvBindByName(column = "last_name")
    private String lastName;

    @CsvBindByName(column = "email")
    private String email;

    @CsvBindByName(column = "phone_number")
    private String phoneNumber;

    @CsvBindByName(column = "date_of_birth")
    private String dateOfBirth; // Keeping as String for now

    @CsvBindByName(column = "address_line_1")
    private String addressLine1;

    @CsvBindByName(column = "address_line_2")
    private String addressLine2;

    @CsvBindByName(column = "city")
    private String city;

    @CsvBindByName(column = "state")
    private String state;

    @CsvBindByName(column = "zip_code")
    private String zipCode;

    @CsvBindByName(column = "id_document_type")
    private String idDocumentType;

    @CsvBindByName(column = "id_document_number")
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
