CREATE TABLE documents (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    original_filename VARCHAR(255) NOT NULL,
    file_size BIGINT NOT NULL,
    content_type VARCHAR(255) NOT NULL,
    storage_path VARCHAR(512) NOT NULL,
    upload_time DATETIME(6) NOT NULL
);
