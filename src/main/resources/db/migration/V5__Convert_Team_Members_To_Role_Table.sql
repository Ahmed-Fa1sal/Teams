-- Converts team_members from a pure many-to-many table into a membership table.
-- Each row now represents a user's role inside one specific team.

CREATE SEQUENCE IF NOT EXISTS team_members_id_seq;

ALTER TABLE team_members
    ADD COLUMN IF NOT EXISTS id BIGINT,
    ADD COLUMN IF NOT EXISTS role VARCHAR(50) NOT NULL DEFAULT 'MEMBER',
    ADD COLUMN IF NOT EXISTS joined_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP;

UPDATE team_members
SET id = nextval('team_members_id_seq')
WHERE id IS NULL;

ALTER TABLE team_members
    ALTER COLUMN id SET DEFAULT nextval('team_members_id_seq'),
    ALTER COLUMN id SET NOT NULL;

SELECT setval(
    'team_members_id_seq',
    COALESCE((SELECT MAX(id) FROM team_members), 0) + 1,
    false
);

-- The previous schema used PRIMARY KEY (team_id, user_id).
-- Replace it with id as the primary key and keep team/user uniqueness separately.
ALTER TABLE team_members DROP CONSTRAINT IF EXISTS team_members_pkey;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint WHERE conname = 'pk_team_members'
    ) THEN
        ALTER TABLE team_members ADD CONSTRAINT pk_team_members PRIMARY KEY (id);
    END IF;
END $$;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint WHERE conname = 'uk_team_members_team_user'
    ) THEN
        ALTER TABLE team_members ADD CONSTRAINT uk_team_members_team_user UNIQUE (team_id, user_id);
    END IF;
END $$;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint WHERE conname = 'chk_team_member_role'
    ) THEN
        ALTER TABLE team_members ADD CONSTRAINT chk_team_member_role CHECK (role IN ('OWNER', 'ADMIN', 'MEMBER'));
    END IF;
END $$;

-- Existing team owners should become OWNER inside their teams.
UPDATE team_members tm
SET role = 'OWNER'
FROM teams t
WHERE tm.team_id = t.id
  AND tm.user_id = t.owner_id;

CREATE INDEX IF NOT EXISTS idx_team_members_team_id ON team_members(team_id);
CREATE INDEX IF NOT EXISTS idx_team_members_user_id ON team_members(user_id);
CREATE INDEX IF NOT EXISTS idx_team_members_role ON team_members(role);
