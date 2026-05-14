CREATE DATABASE IF NOT EXISTS p2p_lending_db;
USE p2p_lending_db;

-- Xóa bảng theo thứ tự ngược lại để tránh lỗi Foreign Key
SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS notifications;
DROP TABLE IF EXISTS documents;
DROP TABLE IF EXISTS transactions;
DROP TABLE IF EXISTS repayments;
DROP TABLE IF EXISTS investments;
DROP TABLE IF EXISTS loans;
DROP TABLE IF EXISTS loan_applications;
DROP TABLE IF EXISTS bank_accounts;
DROP TABLE IF EXISTS investors;
DROP TABLE IF EXISTS borrowers;
DROP TABLE IF EXISTS users;
SET FOREIGN_KEY_CHECKS = 1;

-- 1. Table: users
CREATE TABLE users (
    user_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role ENUM('admin', 'investor', 'borrower') NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- 2. Table: borrowers
CREATE TABLE borrowers (
    borrower_id BIGINT PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    id_card_number VARCHAR(20) NOT NULL UNIQUE,
    verification_status ENUM('pending', 'verified', 'rejected') DEFAULT 'pending',
    monthly_income DECIMAL(15,2) NOT NULL,
    credit_score INT NULL,
    risk_level ENUM('Low', 'Medium', 'High', 'Very High') DEFAULT 'Medium',
    CONSTRAINT fk_borrower_user FOREIGN KEY (borrower_id) REFERENCES users(user_id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- 3. Table: investors
CREATE TABLE investors (
    investor_id BIGINT PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    wallet_balance DECIMAL(15,2) DEFAULT 0.00,
    risk_appetite ENUM('Conservative', 'Moderate', 'Aggressive') NOT NULL,
    CONSTRAINT fk_investor_user FOREIGN KEY (investor_id) REFERENCES users(user_id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- 4. Table: bank_accounts
CREATE TABLE bank_accounts (
    account_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    bank_name VARCHAR(100) NOT NULL,
    account_number VARCHAR(20) NOT NULL UNIQUE,
    account_holder VARCHAR(100) NOT NULL,
    CONSTRAINT fk_bank_user FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- 5. Table: loan_applications
CREATE TABLE loan_applications (
    application_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    borrower_id BIGINT NOT NULL,
    amount_requested DECIMAL(15,2) NOT NULL,
    term_months INT NOT NULL,
    status ENUM('pending', 'approved', 'rejected', 'funded') DEFAULT 'pending',
    cic_issued_date DATE NOT NULL,
    cic_pdf_url VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_app_borrower FOREIGN KEY (borrower_id) REFERENCES borrowers(borrower_id)
) ENGINE=InnoDB;

-- 6. Table: loans
CREATE TABLE loans (
    loan_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    application_id BIGINT NOT NULL,
    total_amount DECIMAL(15,2) NOT NULL,
    current_funded DECIMAL(15,2) DEFAULT 0.00,
    interest_rate DECIMAL(5,2) NOT NULL,
    status ENUM('funding', 'active', 'completed', 'defaulted') DEFAULT 'funding',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_loan_app FOREIGN KEY (application_id) REFERENCES loan_applications(application_id)
) ENGINE=InnoDB;

-- 7. Table: investments
CREATE TABLE investments (
    investment_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    loan_id BIGINT NOT NULL,
    investor_id BIGINT NOT NULL,
    amount_invested DECIMAL(15,2) NOT NULL,
    status ENUM('pending', 'completed') DEFAULT 'completed',
    invested_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_inv_loan FOREIGN KEY (loan_id) REFERENCES loans(loan_id),
    CONSTRAINT fk_inv_investor FOREIGN KEY (investor_id) REFERENCES investors(investor_id)
) ENGINE=InnoDB;

-- 8. Table: repayments
CREATE TABLE repayments (
    repayment_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    loan_id BIGINT NOT NULL,
    installment_no INT NOT NULL,
    due_date DATE NOT NULL,
    principal_due DECIMAL(15,2) NOT NULL,
    interest_due DECIMAL(15,2) NOT NULL,
    penalty_fee DECIMAL(15,2) DEFAULT 0.00,
    amount_paid DECIMAL(15,2) DEFAULT 0.00,
    status ENUM('unpaid', 'paid', 'overdue', 'partial') DEFAULT 'unpaid',
    CONSTRAINT fk_repayment_loan FOREIGN KEY (loan_id) REFERENCES loans(loan_id)
) ENGINE=InnoDB;

-- 9. Table: transactions
CREATE TABLE transactions (
    transaction_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    amount DECIMAL(15,2) NOT NULL,
    transaction_type ENUM('deposit', 'withdraw', 'disbursement', 'repayment') NOT NULL,
    status ENUM('pending', 'completed', 'failed') DEFAULT 'pending',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_trans_user FOREIGN KEY (user_id) REFERENCES users(user_id)
) ENGINE=InnoDB;

-- 10. Table: documents
CREATE TABLE documents (
    document_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    document_type ENUM('id_card_front', 'id_card_back', 'salary_slip', 'contract', 'other') NOT NULL,
    file_url VARCHAR(255) NOT NULL,
    uploaded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_doc_user FOREIGN KEY (user_id) REFERENCES users(user_id)
) ENGINE=InnoDB;

-- 11. Table: notifications
CREATE TABLE notifications (
    notification_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    title VARCHAR(255) NOT NULL,
    message TEXT NOT NULL,
    is_read BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_notif_user FOREIGN KEY (user_id) REFERENCES users(user_id)
) ENGINE=InnoDB;

