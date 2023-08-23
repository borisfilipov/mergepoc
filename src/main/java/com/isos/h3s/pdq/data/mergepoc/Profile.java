package com.isos.h3s.pdq.data.mergepoc;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "profilec")
public class Profile {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seqGen")
    @SequenceGenerator(name = "seqGen", sequenceName = "profileseq", initialValue = 1)
  private int id;
  private String firstName;
  private String lastName;
  private String employeeId;
  private String Phone;
  private String Email;
  private int ttProfileId;
  private int companyId;

  protected Profile() {}

  public Profile(String firstName, String lastName, String employeeId, String Phone, String Email, int ttProfileId, int companyId) {
    this.firstName = firstName;
    this.lastName = lastName;
    this.employeeId = employeeId;
    this.Phone = Phone;
    this.Email = Email;
    this.ttProfileId = ttProfileId;
    this.companyId = companyId;
  }

  @Override
  public String toString() {
    return String.format(
        "Profile[id='%s', firstName='%s', lastName='%s', employeeId='%s', Phone='%s', Email='%s', ttProfileId='%s', companyId='%s']",
        id, firstName, lastName, employeeId, Phone, Email, ttProfileId, companyId);
  }

  public void setFirstName(String firstName) {
	this.firstName = firstName;
}

public void setLastName(String lastName) {
	this.lastName = lastName;
}

public String getEmployeeId() {
	return employeeId;
}

public void setEmployeeId(String employeeId) {
	this.employeeId = employeeId;
}

public String getPhone() {
	return Phone;
}

public void setPhone(String phone) {
	Phone = phone;
}

public String getEmail() {
	return Email;
}

public void setEmail(String email) {
	Email = email;
}

public int getTtProfileId() {
	return ttProfileId;
}

public void setTtProfileId(int ttProfileId) {
	this.ttProfileId = ttProfileId;
}

public int getCompanyId() {
	return companyId;
}

public void setCompanyId(int companyId) {
	this.companyId = companyId;
}

public int getId() {
    return id;
  }

  public String getFirstName() {
    return firstName;
  }

  public String getLastName() {
    return lastName;
  }
}
