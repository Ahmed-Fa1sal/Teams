-- Migration: Add organization_id foreign key to teams
-- This migration is defensive: it will not fail if the column or constraint already exists.
-- Steps:
-- 1) Add nullable organization_id column if not exists
-- 2) (Optional) Backfill organization_id for existing teams using owner->organization
-- 3) Add foreign key constraint if not exists

DO $$
BEGIN
    -- Add column if it does not exist
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name = 'teams' AND column_name = 'organization_id'
    ) THEN
        ALTER TABLE teams ADD COLUMN organization_id BIGINT;
    END IF;

    -- Example backfill: uncomment and adjust to your schema if you want automatic backfill
    -- UPDATE teams t
    -- SET organization_id = om.organization_id
    -- FROM organization_members om
    -- WHERE om.user_id = t.owner_id
    --   AND t.organization_id IS NULL;

    -- Add foreign key constraint if it does not exist
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint WHERE conname = 'fk_teams_organization'
    ) THEN
        ALTER TABLE teams
            ADD CONSTRAINT fk_teams_organization
            FOREIGN KEY (organization_id) REFERENCES organizations(id);
    END IF;
END$$;

-- Note: we keep organization_id nullable here to avoid forcing a backfill during migration.
-- After verifying data, create a follow-up migration to set organization_id NOT NULL if appropriate.

