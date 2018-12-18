CREATE TABLE IF NOT EXISTS card_field (
  id VARCHAR(32) NOT NULL,
  batch_id VARCHAR(32) NOT NULL,
	name VARCHAR(80) NOT NULL,
	display_name VARCHAR(255) NOT NULL,
	"type" VARCHAR(80) NOT NULL,
	PRIMARY KEY(id)
);

CREATE INDEX IF NOT EXISTS idx_card_field_by_batch_id
ON card_field (
	batch_id
);

CREATE UNIQUE INDEX IF NOT EXISTS idx_card_field_by_name
ON card_field (
	batch_id, name
);
