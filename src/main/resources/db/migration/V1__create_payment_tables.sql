CREATE TABLE users (
    id BIGINT NOT NULL AUTO_INCREMENT,
    full_name VARCHAR(150) NOT NULL,
    document VARCHAR(14) NOT NULL,
    email VARCHAR(150) NOT NULL,
    password VARCHAR(255) NOT NULL,
    type VARCHAR(20) NOT NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_users_document UNIQUE (document),
    CONSTRAINT uk_users_email UNIQUE (email)
);

CREATE TABLE wallets (
    id BIGINT NOT NULL AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    balance DECIMAL(19, 2) NOT NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_wallets_user_id UNIQUE (user_id),
    CONSTRAINT fk_wallets_user_id FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE transfers (
    id VARCHAR(36) NOT NULL,
    payer_id BIGINT NOT NULL,
    payee_id BIGINT NOT NULL,
    value DECIMAL(19, 2) NOT NULL,
    status VARCHAR(20) NOT NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_transfers_payer_id FOREIGN KEY (payer_id) REFERENCES users(id),
    CONSTRAINT fk_transfers_payee_id FOREIGN KEY (payee_id) REFERENCES users(id)
);