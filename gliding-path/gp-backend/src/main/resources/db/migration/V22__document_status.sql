ALTER TABLE documents RENAME COLUMN pdf_link TO file_key;
ALTER TABLE documents ALTER COLUMN file_key TYPE VARCHAR(255);