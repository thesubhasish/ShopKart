CREATE TABLE categories (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    slug VARCHAR(100) NOT NULL UNIQUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO categories (name, slug) VALUES
    ('Electronics', 'electronics'),
    ('Fashion', 'fashion'),
    ('Home & Kitchen', 'home-kitchen'),
    ('Books', 'books'),
    ('Sports & Fitness', 'sports-fitness');
