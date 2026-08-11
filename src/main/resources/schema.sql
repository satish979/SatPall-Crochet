CREATE TABLE IF NOT EXISTS persistent_logins (
    username VARCHAR(100) NOT NULL,
    series VARCHAR(100) PRIMARY KEY,
    token VARCHAR(100) NOT NULL,
    last_used TIMESTAMP NOT NULL
);
