CREATE TABLE users (
  id BIGINT NOT NULL,
  external_id VARCHAR(128) NOT NULL,
  email VARCHAR(255) NOT NULL,
  created_at TIMESTAMP WITH TIME ZONE NOT NULL,
  updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
  -- 
  CONSTRAINT pk_users PRIMARY KEY (id),
  -- 
  CONSTRAINT uq_users_external_id UNIQUE (external_id),
  CONSTRAINT uq_users_email UNIQUE (email)
);

CREATE INDEX idx_users_external_id ON users (external_id);
