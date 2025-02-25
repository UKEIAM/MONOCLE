-- Create your application-specific database
SELECT 'CREATE DATABASE mtb'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'mtb');\gexec

\c mtb; -- Switch to the mtb database

-- Create health_insurance table
CREATE TABLE IF NOT EXISTS health_insurance (
                                                id SERIAL PRIMARY KEY,
                                                provider_name VARCHAR(255) NOT NULL,
                                                policy_number VARCHAR(100) UNIQUE NOT NULL,
                                                coverage_details TEXT NOT NULL,
                                                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create workflow table
CREATE TABLE IF NOT EXISTS workflow (
                                        id SERIAL PRIMARY KEY,
                                        process_name VARCHAR(255) NOT NULL,
                                        status VARCHAR(50) NOT NULL CHECK (status IN ('pending', 'in_progress', 'completed')),
                                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
