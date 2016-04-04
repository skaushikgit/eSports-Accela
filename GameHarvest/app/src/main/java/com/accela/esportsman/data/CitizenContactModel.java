package com.accela.esportsman.data;

/**
 * Created by eyang on 10/5/15.
 */

import com.accela.framework.AMBaseModel;
import com.accela.framework.model.AddressModel;
import com.accela.sqlite.annotation.Column;
import com.accela.sqlite.annotation.Table;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Created by skaushik on 9/22/15.
 */
@SuppressWarnings("serial")
@Table(name = "CitizenContactModel")
public class CitizenContactModel extends AMBaseModel implements Serializable {

    @Column(name = "Addresses", onDelete = Column.ForeignKeyAction.CASCADE, onUpdate = Column.ForeignKeyAction.CASCADE)
    private AddressModel address;

    @Column(name = "BirthCity_text")
    private String birthCity_text;

    @Column(name = "BirthCity_value")
    private Date birthCity_value;

    @Column(name = "BirthDate")
    private Date birthDate;

    @Column(name = "BirthRegion_text")
    private String birthRegion_text;

    @Column(name = "BirthRegion_value")
    private String birthRegion_value;

    @Column(name = "BirthState_text")
    private String birthState_text;

    @Column(name = "BirthState_value")
    private String birthState_value;

    @Column(name = "BusinessName")
    private String businessName;

    @Column(name = "BusinessName2")
    private String businessName2;

    @Column(name = "Comment")
    private String comment;

    @Column(name = "ContactAddressModel", onDelete = Column.ForeignKeyAction.CASCADE, onUpdate = Column.ForeignKeyAction.CASCADE)
    private List<ContactAddressModel> contactAddresses;

    @Column(name = "DeceasedDate")
    private Date deceasedDate;

    @Column(name = "DriverLicenseNumber")
    private String driverLicenseNumber;

    @Column(name = "DriverLicenseState_text")
    private String driverLicenseState_text;

    @Column(name = "DriverLicenseState_value")
    private String driverLicenseState_value;

    @Column(name = "Email")
    private String email;

    @Column(name = "FaxCountryCode")
    private String faxCountryCode;

    @Column(name = "FaxNumber")
    private String faxNumber;

    @Column(name = "FederalEmployerId")
    private String federalEmployerId;

    @Column(name = "FirstName")
    private String firstName;

    @Column(name = "FullName")
    private String fullName;

    @Column(name = "Gender_text")
    private String gender_text;

    @Column(name = "Gender_value")
    private String gender_value;

    @Column(name = "Id")
    private String id;

    @Column(name = "IsPrimary")
    private String isPrimary;

    @Column(name = "LastName")
    private String lastName;

    @Column(name = "MiddleName")
    private String middleName;

    @Column(name = "Namesuffix")
    private String namesuffix;

    @Column(name = "PassportNumber")
    private String passportNumber;

    @Column(name = "Phone1CountryCode")
    private String phone1CountryCode;

    @Column(name = "Phone2CountryCode")
    private String phone2CountryCode;

    @Column(name = "Phone3CountryCode")
    private String phone3CountryCode;

    @Column(name = "PhoneNumber1")
    private String phoneNumber1;

    @Column(name = "PhoneNumber2")
    private String phoneNumber2;

    @Column(name = "PhoneNumber3")
    private String phoneNumber3;

    @Column(name = "PostOfficeBox")
    private String postOfficeBox;

    @Column(name = "PreferredChannel_text")
    private String preferredChannel_text;

    @Column(name = "PreferredChannel_value")
    private String preferredChannel_value;

    @Column(name = "Race_text")
    private String race_text;

    @Column(name = "Race_value")
    private String race_value;

    @Column(name = "Relation_text")
    private String relation_text;

    @Column(name = "Relation_value")
    private String relation_value;

    @Column(name = "Salutation_text")
    private String salutation_text;

    @Column(name = "Salutation_value")
    private String salutation_value;

    @Column(name = "SocialSecurityNumber")
    private String socialSecurityNumber;

    @Column(name = "StateIdNumber")
    private String stateIdNumber;

//    @Column(name = "Status")
//    private Status status;

    @Column(name = "Title")
    private String title;

    @Column(name = "TradeName")
    private String tradeName;

    @Column(name = "Type_text")
    private String type_text;

    @Column(name = "Type_value")
    private String type_value;

    @Column(name = "TypeFlag")
    private String typeFlag;

    @Column(name = "Status")
    private int status;

    public AddressModel getAddress() {
        return address;
    }

    public void setAddress(AddressModel address) {
        this.address = address;
    }

    public String getBirthCity_text() {
        return birthCity_text;
    }

    public void setBirthCity_text(String birthCity_text) {
        this.birthCity_text = birthCity_text;
    }

    public Date getBirthCity_value() {
        return birthCity_value;
    }

    public void setBirthCity_value(Date birthCity_value) {
        this.birthCity_value = birthCity_value;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public String getBirthRegion_text() {
        return birthRegion_text;
    }

    public void setBirthRegion_text(String birthRegion_text) {
        this.birthRegion_text = birthRegion_text;
    }

    public String getBirthRegion_value() {
        return birthRegion_value;
    }

    public void setBirthRegion_value(String birthRegion_value) {
        this.birthRegion_value = birthRegion_value;
    }

    public String getBirthState_text() {
        return birthState_text;
    }

    public void setBirthState_text(String birthState_text) {
        this.birthState_text = birthState_text;
    }

    public String getBirthState_value() {
        return birthState_value;
    }

    public void setBirthState_value(String birthState_value) {
        this.birthState_value = birthState_value;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public String getBusinessName2() {
        return businessName2;
    }

    public void setBusinessName2(String businessName2) {
        this.businessName2 = businessName2;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public List<ContactAddressModel> getContactAddresses() {
        return contactAddresses;
    }

    public void setContactAddresses(List<ContactAddressModel> contactAddresses) {
        this.contactAddresses = contactAddresses;
    }

    public Date getDeceasedDate() {
        return deceasedDate;
    }

    public void setDeceasedDate(Date deceasedDate) {
        this.deceasedDate = deceasedDate;
    }

    public String getDriverLicenseNumber() {
        return driverLicenseNumber;
    }

    public void setDriverLicenseNumber(String driverLicenseNumber) {
        this.driverLicenseNumber = driverLicenseNumber;
    }

    public String getDriverLicenseState_text() {
        return driverLicenseState_text;
    }

    public void setDriverLicenseState_text(String driverLicenseState_text) {
        this.driverLicenseState_text = driverLicenseState_text;
    }

    public String getDriverLicenseState_value() {
        return driverLicenseState_value;
    }

    public void setDriverLicenseState_value(String driverLicenseState_value) {
        this.driverLicenseState_value = driverLicenseState_value;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFaxCountryCode() {
        return faxCountryCode;
    }

    public void setFaxCountryCode(String faxCountryCode) {
        this.faxCountryCode = faxCountryCode;
    }

    public String getFaxNumber() {
        return faxNumber;
    }

    public void setFaxNumber(String faxNumber) {
        this.faxNumber = faxNumber;
    }

    public String getFederalEmployerId() {
        return federalEmployerId;
    }

    public void setFederalEmployerId(String federalEmployerId) {
        this.federalEmployerId = federalEmployerId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getGender_text() {
        return gender_text;
    }

    public void setGender_text(String gender_text) {
        this.gender_text = gender_text;
    }

    public String getGender_value() {
        return gender_value;
    }

    public void setGender_value(String gender_value) {
        this.gender_value = gender_value;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIsPrimary() {
        return isPrimary;
    }

    public void setIsPrimary(String isPrimary) {
        this.isPrimary = isPrimary;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getNamesuffix() {
        return namesuffix;
    }

    public void setNamesuffix(String namesuffix) {
        this.namesuffix = namesuffix;
    }

    public String getPassportNumber() {
        return passportNumber;
    }

    public void setPassportNumber(String passportNumber) {
        this.passportNumber = passportNumber;
    }

    public String getPhone1CountryCode() {
        return phone1CountryCode;
    }

    public void setPhone1CountryCode(String phone1CountryCode) {
        this.phone1CountryCode = phone1CountryCode;
    }

    public String getPhone2CountryCode() {
        return phone2CountryCode;
    }

    public void setPhone2CountryCode(String phone2CountryCode) {
        this.phone2CountryCode = phone2CountryCode;
    }

    public String getPhone3CountryCode() {
        return phone3CountryCode;
    }

    public void setPhone3CountryCode(String phone3CountryCode) {
        this.phone3CountryCode = phone3CountryCode;
    }

    public String getPhoneNumber1() {
        return phoneNumber1;
    }

    public void setPhoneNumber1(String phoneNumber1) {
        this.phoneNumber1 = phoneNumber1;
    }

    public String getPhoneNumber2() {
        return phoneNumber2;
    }

    public void setPhoneNumber2(String phoneNumber2) {
        this.phoneNumber2 = phoneNumber2;
    }

    public String getPhoneNumber3() {
        return phoneNumber3;
    }

    public void setPhoneNumber3(String phoneNumber3) {
        this.phoneNumber3 = phoneNumber3;
    }

    public String getPostOfficeBox() {
        return postOfficeBox;
    }

    public void setPostOfficeBox(String postOfficeBox) {
        this.postOfficeBox = postOfficeBox;
    }

    public String getPreferredChannel_text() {
        return preferredChannel_text;
    }

    public void setPreferredChannel_text(String preferredChannel_text) {
        this.preferredChannel_text = preferredChannel_text;
    }

    public String getPreferredChannel_value() {
        return preferredChannel_value;
    }

    public void setPreferredChannel_value(String preferredChannel_value) {
        this.preferredChannel_value = preferredChannel_value;
    }

    public String getRace_text() {
        return race_text;
    }

    public void setRace_text(String race_text) {
        this.race_text = race_text;
    }

    public String getRace_value() {
        return race_value;
    }

    public void setRace_value(String race_value) {
        this.race_value = race_value;
    }

    public String getRelation_text() {
        return relation_text;
    }

    public void setRelation_text(String relation_text) {
        this.relation_text = relation_text;
    }

    public String getRelation_value() {
        return relation_value;
    }

    public void setRelation_value(String relation_value) {
        this.relation_value = relation_value;
    }

    public String getSalutation_text() {
        return salutation_text;
    }

    public void setSalutation_text(String salutation_text) {
        this.salutation_text = salutation_text;
    }

    public String getSalutation_value() {
        return salutation_value;
    }

    public void setSalutation_value(String salutation_value) {
        this.salutation_value = salutation_value;
    }

    public String getSocialSecurityNumber() {
        return socialSecurityNumber;
    }

    public void setSocialSecurityNumber(String socialSecurityNumber) {
        this.socialSecurityNumber = socialSecurityNumber;
    }

    public String getStateIdNumber() {
        return stateIdNumber;
    }

    public void setStateIdNumber(String stateIdNumber) {
        this.stateIdNumber = stateIdNumber;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTradeName() {
        return tradeName;
    }

    public void setTradeName(String tradeName) {
        this.tradeName = tradeName;
    }

    public String getType_text() {
        return type_text;
    }

    public void setType_text(String type_text) {
        this.type_text = type_text;
    }

    public String getType_value() {
        return type_value;
    }

    public void setType_value(String type_value) {
        this.type_value = type_value;
    }

    public String getTypeFlag() {
        return typeFlag;
    }

    public void setTypeFlag(String typeFlag) {
        this.typeFlag = typeFlag;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }


}