package com.accela.esportsman.data;

/**
 * Created by eyang on 10/5/15.
 */
import com.accela.framework.AMBaseModel;
import com.accela.sqlite.annotation.Column;
import com.accela.sqlite.annotation.Table;

import java.io.Serializable;
import java.util.Date;

@SuppressWarnings("serial")
@Table(name = "ContactAddressModel")
public class ContactAddressModel extends AMBaseModel implements Serializable {
    @Column(name = "AddressLine1")
    private String addressLine1;


    @Column(name = "AddressLine2")
    private String addressLine2;

    @Column(name = "AddressLine3")
    private String addressLine3;

    @Column(name = "City")
    private String city;

    @Column(name = "Country_text")
    private String country_text;

    @Column(name = "Country_value")
    private String country_value;

    @Column(name = "EffectiveDate")
    private Date effectiveDate;

    @Column(name = "EntityID")
    private long entityID;

    @Column(name = "ExpirationDate")
    private Date ExpirationDate;

    @Column(name = "Fax")
    private String fax;

    @Column(name = "faxCountryCode")
    private String faxCountryCode;

    @Column(name = "FullAddress")
    private String fullAddress;

    @Column(name = "HouseNumberAlphaEnd")
    private String houseNumberAlphaEnd;

    @Column(name = "HouseNumberAlphaStart")
    private String houseNumberAlphaStart;

    @Column(name = "HouseNumberEnd")
    private long houseNumberEnd;

    @Column(name = "HouseNumberStart")
    private long houseNumberStart;

    @Column(name = "Id")
    private long id;

    @Column(name = "LevelNumberEnd")
    private String levelNumberEnd;

    @Column(name = "LevelNumberStart")
    private String levelNumberStart;

    @Column(name = "LevelPrefix")
    private String levelPrefix;

    @Column(name = "Phone")
    private String phone;

    @Column(name = "PhoneCountryCode")
    private String phoneCountryCode;

    @Column(name = "PostalCode")
    private String postalCode;

    @Column(name = "Primary")
    private String primary;

    @Column(name = "Recipient")
    private String recipient;

    @Column(name = "State_text")
    private String state_text;

    @Column(name = "State_value")
    private String state_value;

    @Column(name = "Status")
    private String status;

    @Column(name = "StreetDirection_text")
    private String streetDirection;

    @Column(name = "StreetDirection_value")
    private String streetDirection_value;

    @Column(name = "StreetName")
    private String streetName;

    @Column(name = "StreetPrefix")
    private String streetPrefix;

    @Column(name = "StreetSuffix_text")
    private String streetSuffix_text;

    @Column(name = "StreetSuffix_value")
    private String streetSuffix_value;

    @Column(name = "StreetSuffixDirection_text")
    private String streetSuffixDirection_text;

    @Column(name = "StreetSuffixDirection_value")
    private String streetSuffixDirection_value;

    @Column(name = "Type_text")
    private String type_text;

    @Column(name = "Type_value")
    private String type_value;

    @Column(name = "UnitEnd")
    private String unitEnd;

    @Column(name = "UnitStart")
    private String unitStart;

    @Column(name = "UnitType_text")
    private String unitType_text;

    @Column(name = "UnitType_value")
    private String unitType_value;

    @Column(name = "ValidateFlag")
    private String validateFlag;


    public String getAddressLine1() {
        return addressLine1;
    }

    public void setAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
    }

    public String getAddressLine2() {
        return addressLine2;
    }

    public void setAddressLine2(String addressLine2) {
        this.addressLine2 = addressLine2;
    }

    public String getAddressLine3() {
        return addressLine3;
    }

    public void setAddressLine3(String addressLine3) {
        this.addressLine3 = addressLine3;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry_text() {
        return country_text;
    }

    public void setCountry_text(String country_text) {
        this.country_text = country_text;
    }

    public String getCountry_value() {
        return country_value;
    }

    public void setCountry_value(String country_value) {
        this.country_value = country_value;
    }

    public Date getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(Date effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    public long getEntityID() {
        return entityID;
    }

    public void setEntityID(long entityID) {
        this.entityID = entityID;
    }

    public Date getExpirationDate() {
        return ExpirationDate;
    }

    public void setExpirationDate(Date expirationDate) {
        ExpirationDate = expirationDate;
    }

    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

    public String getFaxCountryCode() {
        return faxCountryCode;
    }

    public void setFaxCountryCode(String faxCountryCode) {
        this.faxCountryCode = faxCountryCode;
    }

    public String getFullAddress() {
        return fullAddress;
    }

    public void setFullAddress(String fullAddress) {
        this.fullAddress = fullAddress;
    }

    public String getHouseNumberAlphaEnd() {
        return houseNumberAlphaEnd;
    }

    public void setHouseNumberAlphaEnd(String houseNumberAlphaEnd) {
        this.houseNumberAlphaEnd = houseNumberAlphaEnd;
    }

    public String getHouseNumberAlphaStart() {
        return houseNumberAlphaStart;
    }

    public void setHouseNumberAlphaStart(String houseNumberAlphaStart) {
        this.houseNumberAlphaStart = houseNumberAlphaStart;
    }

    public long getHouseNumberEnd() {
        return houseNumberEnd;
    }

    public void setHouseNumberEnd(long houseNumberEnd) {
        this.houseNumberEnd = houseNumberEnd;
    }

    public long getHouseNumberStart() {
        return houseNumberStart;
    }

    public void setHouseNumberStart(long houseNumberStart) {
        this.houseNumberStart = houseNumberStart;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getLevelNumberEnd() {
        return levelNumberEnd;
    }

    public void setLevelNumberEnd(String levelNumberEnd) {
        this.levelNumberEnd = levelNumberEnd;
    }

    public String getLevelNumberStart() {
        return levelNumberStart;
    }

    public void setLevelNumberStart(String levelNumberStart) {
        this.levelNumberStart = levelNumberStart;
    }

    public String getLevelPrefix() {
        return levelPrefix;
    }

    public void setLevelPrefix(String levelPrefix) {
        this.levelPrefix = levelPrefix;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPhoneCountryCode() {
        return phoneCountryCode;
    }

    public void setPhoneCountryCode(String phoneCountryCode) {
        this.phoneCountryCode = phoneCountryCode;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getPrimary() {
        return primary;
    }

    public void setPrimary(String primary) {
        this.primary = primary;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public String getState_text() {
        return state_text;
    }

    public void setState_text(String state_text) {
        this.state_text = state_text;
    }

    public String getState_value() {
        return state_value;
    }

    public void setState_value(String state_value) {
        this.state_value = state_value;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStreetDirection() {
        return streetDirection;
    }

    public void setStreetDirection(String streetDirection) {
        this.streetDirection = streetDirection;
    }

    public String getStreetDirection_value() {
        return streetDirection_value;
    }

    public void setStreetDirection_value(String streetDirection_value) {
        this.streetDirection_value = streetDirection_value;
    }

    public String getStreetName() {
        return streetName;
    }

    public void setStreetName(String streetName) {
        this.streetName = streetName;
    }

    public String getStreetPrefix() {
        return streetPrefix;
    }

    public void setStreetPrefix(String streetPrefix) {
        this.streetPrefix = streetPrefix;
    }

    public String getStreetSuffix_text() {
        return streetSuffix_text;
    }

    public void setStreetSuffix_text(String streetSuffix_text) {
        this.streetSuffix_text = streetSuffix_text;
    }

    public String getStreetSuffix_value() {
        return streetSuffix_value;
    }

    public void setStreetSuffix_value(String streetSuffix_value) {
        this.streetSuffix_value = streetSuffix_value;
    }

    public String getStreetSuffixDirection_text() {
        return streetSuffixDirection_text;
    }

    public void setStreetSuffixDirection_text(String streetSuffixDirection_text) {
        this.streetSuffixDirection_text = streetSuffixDirection_text;
    }

    public String getStreetSuffixDirection_value() {
        return streetSuffixDirection_value;
    }

    public void setStreetSuffixDirection_value(String streetSuffixDirection_value) {
        this.streetSuffixDirection_value = streetSuffixDirection_value;
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

    public String getUnitEnd() {
        return unitEnd;
    }

    public void setUnitEnd(String unitEnd) {
        this.unitEnd = unitEnd;
    }

    public String getUnitStart() {
        return unitStart;
    }

    public void setUnitStart(String unitStart) {
        this.unitStart = unitStart;
    }

    public String getUnitType_text() {
        return unitType_text;
    }

    public void setUnitType_text(String unitType_text) {
        this.unitType_text = unitType_text;
    }

    public String getUnitType_value() {
        return unitType_value;
    }

    public void setUnitType_value(String unitType_value) {
        this.unitType_value = unitType_value;
    }

    public String getValidateFlag() {
        return validateFlag;
    }

    public void setValidateFlag(String validateFlag) {
        this.validateFlag = validateFlag;
    }

}