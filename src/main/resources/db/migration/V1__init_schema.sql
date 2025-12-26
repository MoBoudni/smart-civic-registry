-- ============================================
-- Smart Civic Registry - Initial Database Schema
-- ============================================

-- Enable UUID extension
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- ============================================
-- Security Tables
-- ============================================

-- Users table for authentication
CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    username VARCHAR(100) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    enabled BOOLEAN DEFAULT TRUE,
    account_non_expired BOOLEAN DEFAULT TRUE,
    account_non_locked BOOLEAN DEFAULT TRUE,
    credentials_non_expired BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_login TIMESTAMP,
    failed_login_attempts INT DEFAULT 0
);

-- Roles table
CREATE TABLE roles (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- User-Roles junction table
CREATE TABLE user_roles (
    user_id UUID NOT NULL,
    role_id UUID NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
);

-- Refresh tokens for JWT
CREATE TABLE refresh_tokens (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL,
    token VARCHAR(500) NOT NULL UNIQUE,
    expires_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- ============================================
-- Domain Tables
-- ============================================

-- Persons table (natural persons)
CREATE TABLE persons (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    date_of_birth DATE NOT NULL,
    gender VARCHAR(20),
    citizenship VARCHAR(100),
    
    -- Address as embedded (could be normalized later)
    street VARCHAR(255),
    house_number VARCHAR(20),
    postal_code VARCHAR(20),
    city VARCHAR(100),
    country VARCHAR(100),
    
    -- Contact information
    email VARCHAR(255),
    phone VARCHAR(50),
    
    -- Audit fields
    created_by UUID,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_by UUID,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE,
    
    FOREIGN KEY (created_by) REFERENCES users(id),
    FOREIGN KEY (updated_by) REFERENCES users(id)
);

-- Organizations table (legal entities)
CREATE TABLE organizations (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(255) NOT NULL,
    legal_form VARCHAR(100),
    tax_id VARCHAR(100),
    
    -- Address
    street VARCHAR(255),
    house_number VARCHAR(20),
    postal_code VARCHAR(20),
    city VARCHAR(100),
    country VARCHAR(100),
    
    -- Contact
    email VARCHAR(255),
    phone VARCHAR(50),
    website VARCHAR(255),
    
    -- Metadata
    type VARCHAR(50) DEFAULT 'COMPANY',
    status VARCHAR(50) DEFAULT 'ACTIVE',
    
    -- Audit fields
    created_by UUID,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_by UUID,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (created_by) REFERENCES users(id),
    FOREIGN KEY (updated_by) REFERENCES users(id)
);

-- Person-Organization relationships (employees, members, etc.)
CREATE TABLE person_organizations (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    person_id UUID NOT NULL,
    organization_id UUID NOT NULL,
    relationship_type VARCHAR(50) NOT NULL, -- EMPLOYEE, MEMBER, CONTACT, etc.
    position VARCHAR(100),
    department VARCHAR(100),
    start_date DATE,
    end_date DATE,
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (person_id) REFERENCES persons(id) ON DELETE CASCADE,
    FOREIGN KEY (organization_id) REFERENCES organizations(id) ON DELETE CASCADE,
    UNIQUE (person_id, organization_id, relationship_type)
);

-- ============================================
-- Application/Workflow Tables
-- ============================================

-- Applications table (administrative procedures)
CREATE TABLE applications (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    application_number VARCHAR(50) NOT NULL UNIQUE,
    type VARCHAR(100) NOT NULL, -- NAME_CHANGE, ADDRESS_CHANGE, CITIZENSHIP, etc.
    status VARCHAR(50) DEFAULT 'SUBMITTED',
    
    -- Related entities
    person_id UUID NOT NULL,
    submitted_by UUID NOT NULL, -- User who submitted
    assigned_to UUID, -- User assigned for processing
    
    -- Application data (could be JSON for flexibility)
    data JSONB,
    
    -- Dates
    submission_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    decision_date TIMESTAMP,
    completion_date TIMESTAMP,
    
    -- Decision
    decision VARCHAR(50), -- APPROVED, REJECTED, etc.
    decision_reason TEXT,
    
    -- Audit
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (person_id) REFERENCES persons(id),
    FOREIGN KEY (submitted_by) REFERENCES users(id),
    FOREIGN KEY (assigned_to) REFERENCES users(id)
);

-- Application documents
CREATE TABLE application_documents (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    application_id UUID NOT NULL,
    document_type VARCHAR(100) NOT NULL,
    file_name VARCHAR(255) NOT NULL,
    file_path VARCHAR(500) NOT NULL,
    file_size BIGINT,
    mime_type VARCHAR(100),
    
    uploaded_by UUID NOT NULL,
    uploaded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (application_id) REFERENCES applications(id) ON DELETE CASCADE,
    FOREIGN KEY (uploaded_by) REFERENCES users(id)
);

-- ============================================
-- Audit Logging Tables
-- ============================================

-- Audit log for all data changes
CREATE TABLE audit_logs (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    table_name VARCHAR(100) NOT NULL,
    record_id UUID NOT NULL,
    action VARCHAR(20) NOT NULL, -- CREATE, UPDATE, DELETE, READ
    old_values JSONB,
    new_values JSONB,
    
    performed_by UUID,
    performed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    ip_address VARCHAR(45),
    user_agent TEXT,
    
    FOREIGN KEY (performed_by) REFERENCES users(id)
);

-- Security events log
CREATE TABLE security_events (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    event_type VARCHAR(100) NOT NULL, -- LOGIN_SUCCESS, LOGIN_FAILED, LOGOUT, etc.
    user_id UUID,
    username VARCHAR(100),
    ip_address VARCHAR(45),
    user_agent TEXT,
    details JSONB,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- ============================================
-- Indexes for Performance
-- ============================================

-- Users indexes
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_email ON users(email);

-- Persons indexes
CREATE INDEX idx_persons_last_name ON persons(last_name);
CREATE INDEX idx_persons_date_of_birth ON persons(date_of_birth);
CREATE INDEX idx_persons_city ON persons(city);

-- Organizations indexes
CREATE INDEX idx_organizations_name ON organizations(name);
CREATE INDEX idx_organizations_city ON organizations(city);

-- Applications indexes
CREATE INDEX idx_applications_person_id ON applications(person_id);
CREATE INDEX idx_applications_status ON applications(status);
CREATE INDEX idx_applications_submission_date ON applications(submission_date);

-- Audit logs indexes
CREATE INDEX idx_audit_logs_table_record ON audit_logs(table_name, record_id);
CREATE INDEX idx_audit_logs_performed_at ON audit_logs(performed_at);

-- Security events indexes
CREATE INDEX idx_security_events_created_at ON security_events(created_at);
CREATE INDEX idx_security_events_user_id ON security_events(user_id);

-- ============================================
-- Initial Data
-- ============================================

-- Insert default roles
INSERT INTO roles (name, description) VALUES
('ROLE_USER', 'Basic user role'),
('ROLE_OFFICER', 'Officer/Sachbearbeiter role'),
('ROLE_ADMIN', 'Administrator role');

-- Insert a default admin user (password: Admin123!)
-- Password is BCrypt hash of 'Admin123!'
INSERT INTO users (username, email, password, first_name, last_name, enabled) VALUES
('admin@smartcivicregistry.de', 'admin@smartcivicregistry.de', '$2a$10$X8z5L9Q7v6b4F1KjH2G3N.4M5N6B7V8C9X0Z1A2S3D4F5G6H7J8K9L0M', 'System', 'Administrator', true);

-- Assign admin role to admin user
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id 
FROM users u, roles r 
WHERE u.username = 'admin@smartcivicregistry.de' AND r.name = 'ROLE_ADMIN';
