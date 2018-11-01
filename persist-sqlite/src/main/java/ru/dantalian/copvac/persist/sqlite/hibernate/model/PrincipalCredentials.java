package ru.dantalian.copvac.persist.sqlite.hibernate.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table( name = "creds" )
public class PrincipalCredentials {

	@Id
	@Column(name = "id", length = 36)
	private String id;

	@Column(name = "salt")
	private String salt;

	@Column(name = "passwd")
	private String passwd;

	public String getId() {
		return id;
	}

	public void setId(final String aId) {
		id = aId;
	}

	public String getSalt() {
		return salt;
	}

	public void setSalt(final String aSalt) {
		salt = aSalt;
	}

	public String getPasswd() {
		return passwd;
	}

	public void setPasswd(final String aPasswd) {
		passwd = aPasswd;
	}

}
