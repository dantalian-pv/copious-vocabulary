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

CREATE TABLE IF NOT EXISTS language (
	name VARCHAR(8) NOT NULL,
	country VARCHAR(8) NOT NULL,
	variant VARCHAR(8) NOT NULL,
	text VARCHAR(255),
	PRIMARY KEY(name, country, variant)
);
