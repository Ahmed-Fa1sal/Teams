-- V2__Insert_Roles_And_Permissions.sql
-- Insert default roles
INSERT INTO roles (id, name, description) VALUES
(1, 'ROLE_SYSTEM_ADMIN', 'System Administrator with full system access'),
(2, 'ROLE_ORG_ADMIN', 'Organization Administrator'),
(3, 'ROLE_TEAM_OWNER', 'Team Owner'),
(4, 'ROLE_STANDARD_USER', 'Standard User'),
(5, 'ROLE_GUEST', 'Guest User with limited access');

-- Insert default permissions
INSERT INTO permissions (name, description) VALUES
('user:create', 'Create user'),
('user:read', 'Read user'),
('user:update', 'Update user'),
('user:delete', 'Delete user'),
('team:create', 'Create team'),
('team:read', 'Read team'),
('team:update', 'Update team'),
('team:delete', 'Delete team'),
('channel:create', 'Create channel'),
('channel:read', 'Read channel'),
('channel:update', 'Update channel'),
('channel:delete', 'Delete channel'),
('message:create', 'Create message'),
('message:read', 'Read message'),
('message:update', 'Update message'),
('message:delete', 'Delete message'),
('file:upload', 'Upload file'),
('file:download', 'Download file'),
('file:delete', 'Delete file'),
('admin:access', 'Admin access'),
('audit:read', 'Read audit logs');

