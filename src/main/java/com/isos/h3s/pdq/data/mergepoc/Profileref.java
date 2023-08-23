package com.isos.h3s.pdq.data.mergepoc;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;

@Entity
public class Profileref {
	@Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seqGen")
    @SequenceGenerator(name = "seqGen", sequenceName = "profilerefseq", initialValue = 1)
	private int id;
	private int ttProfileId;
	private int ttProfileIdRef;
	private int companyId;
	private int profileId;
	
	public int getCompanyId() {
		return companyId;
	}
	public void setCompanyId(int companyId) {
		this.companyId = companyId;
	}
	public int getProfileId() {
		return profileId;
	}
	public void setProfileId(int profileId) {
		this.profileId = profileId;
	}
	public int getId() {
		return id;
	}
	public int getTtProfileId() {
		return ttProfileId;
	}
	public void setTtProfileId(int ttProfileId) {
		this.ttProfileId = ttProfileId;
	}
	public int getTtProfileIdRef() {
		return ttProfileIdRef;
	}
	public void setTtProfileIdRef(int ttProfileIdRef) {
		this.ttProfileIdRef = ttProfileIdRef;
	}
}
