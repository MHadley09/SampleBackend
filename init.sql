CREATE USER docker;
CREATE USER postgres with password 'root';
CREATE DATABASE sample-db;
GRANT ALL PRIVILEGES ON DATABASE sample-db TO docker;
GRANT ALL PRIVILEGES ON DATABASE sample-db TO postgres;
