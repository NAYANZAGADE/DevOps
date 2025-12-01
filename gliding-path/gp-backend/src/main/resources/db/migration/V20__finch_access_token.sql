-- Make access_token and refresh_token nullable to support flows without refresh tokens
ALTER TABLE tokens ALTER COLUMN access_token DROP NOT NULL;
ALTER TABLE tokens ALTER COLUMN refresh_token DROP NOT NULL;

