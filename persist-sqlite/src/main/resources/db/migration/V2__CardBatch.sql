CREATE TABLE IF NOT EXISTS card_batch (
  id VARCHAR(32) NOT NULL,
	name VARCHAR(255) NOT NULL,
	description VARCHAR(255),
	"user" VARCHAR(80) NOT NULL,
	source VARCHAR(32) NOT NULL,
	target VARCHAR(32) NOT NULL,
	PRIMARY KEY(id)
);

CREATE INDEX IF NOT EXISTS idx_card_batch_by_user
ON card_batch (
	"user"
);

CREATE UNIQUE INDEX IF NOT EXISTS idx_card_batch_by_user_and_name
ON card_batch (
	"user",
	"name"
);
