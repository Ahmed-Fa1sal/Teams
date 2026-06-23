-- V10__Add_Conversation_Chat_Schema.sql
-- Extend conversations table with type, team, and channel references

ALTER TABLE conversations
    ADD COLUMN type VARCHAR(20) NOT NULL DEFAULT 'DIRECT',
    ADD COLUMN team_id BIGINT,
    ADD COLUMN channel_id BIGINT;

ALTER TABLE conversations
    ADD CONSTRAINT fk_conversations_team FOREIGN KEY (team_id) REFERENCES teams(id) ON DELETE CASCADE,
    ADD CONSTRAINT fk_conversations_channel FOREIGN KEY (channel_id) REFERENCES channels(id) ON DELETE CASCADE;

-- Recreate conversation_members as a proper entity table (add id + joined_at)
DROP TABLE IF EXISTS conversation_members;

CREATE TABLE conversation_members (
    id            BIGSERIAL PRIMARY KEY,
    conversation_id BIGINT NOT NULL,
    user_id       BIGINT NOT NULL,
    joined_at     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_conversation_members UNIQUE (conversation_id, user_id),
    CONSTRAINT fk_cm_conversation FOREIGN KEY (conversation_id) REFERENCES conversations(id) ON DELETE CASCADE,
    CONSTRAINT fk_cm_user         FOREIGN KEY (user_id)         REFERENCES users(id)         ON DELETE CASCADE
);

CREATE INDEX idx_conversation_members_conversation_id ON conversation_members(conversation_id);
CREATE INDEX idx_conversation_members_user_id         ON conversation_members(user_id);
CREATE INDEX idx_conversations_type                   ON conversations(type);
CREATE INDEX idx_conversations_team_id                ON conversations(team_id);
CREATE INDEX idx_conversations_channel_id             ON conversations(channel_id);
