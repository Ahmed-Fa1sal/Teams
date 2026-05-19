-- V3__Add_Spring_JPA_Auditing_Columns.sql
-- Add Spring Data JPA auditing columns (createdBy, updatedBy) to all BaseEntity tables

ALTER TABLE messages ADD COLUMN created_by BIGINT;
ALTER TABLE messages ADD COLUMN updated_by BIGINT;

ALTER TABLE attachments ADD COLUMN created_by BIGINT;
ALTER TABLE attachments ADD COLUMN updated_by BIGINT;

ALTER TABLE channels ADD COLUMN created_by BIGINT;
ALTER TABLE channels ADD COLUMN updated_by BIGINT;

ALTER TABLE teams ADD COLUMN created_by BIGINT;
ALTER TABLE teams ADD COLUMN updated_by BIGINT;

ALTER TABLE conversations ADD COLUMN created_by BIGINT;
ALTER TABLE conversations ADD COLUMN updated_by BIGINT;

ALTER TABLE notifications ADD COLUMN created_by BIGINT;
ALTER TABLE notifications ADD COLUMN updated_by BIGINT;

ALTER TABLE message_reactions ADD COLUMN created_by BIGINT;
ALTER TABLE message_reactions ADD COLUMN updated_by BIGINT;

ALTER TABLE message_reads ADD COLUMN created_by BIGINT;
ALTER TABLE message_reads ADD COLUMN updated_by BIGINT;

ALTER TABLE roles ADD COLUMN created_by BIGINT;
ALTER TABLE roles ADD COLUMN updated_by BIGINT;

ALTER TABLE permissions ADD COLUMN created_by BIGINT;
ALTER TABLE permissions ADD COLUMN updated_by BIGINT;


