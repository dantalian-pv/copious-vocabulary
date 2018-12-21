CREATE TABLE IF NOT EXISTS principal (
	name VARCHAR(80) NOT NULL,
	description VARCHAR(255),
	PRIMARY KEY(name)
);

CREATE TABLE IF NOT EXISTS principal_password (
	name VARCHAR(80) NOT NULL,
	password VARCHAR(255),
	PRIMARY KEY(name)
);
