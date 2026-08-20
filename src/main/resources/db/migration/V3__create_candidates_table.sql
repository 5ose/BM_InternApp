CREATE TABLE candidates (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    phone VARCHAR(255),
    position VARCHAR(255),
    status VARCHAR(50) NOT NULL,
    tags VARCHAR(255),
    document_id BIGINT,
    created_at DATETIME(6) NOT NULL
);
