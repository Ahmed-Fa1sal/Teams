CREATE TABLE organizations (
                               id          BIGSERIAL       PRIMARY KEY,
                               name        VARCHAR(100)    NOT NULL UNIQUE,
                               description TEXT,
                               logo_url    VARCHAR(512),
                               active      BOOLEAN         NOT NULL DEFAULT TRUE,
                               tier        VARCHAR(50),
                               created_at  TIMESTAMP       NOT NULL DEFAULT NOW(),
                               updated_at  TIMESTAMP       NOT NULL DEFAULT NOW(),
                               created_by  BIGINT          REFERENCES users (id) ON DELETE SET NULL,
                               updated_by  BIGINT          REFERENCES users (id) ON DELETE SET NULL,
                               deleted_at  TIMESTAMP,
                               deleted     BOOLEAN         NOT NULL DEFAULT FALSE
);

CREATE INDEX idx_organizations_name   ON organizations (LOWER(name));
CREATE INDEX idx_organizations_active ON organizations (active)     WHERE deleted = FALSE;
CREATE INDEX idx_organizations_tier   ON organizations (tier)       WHERE deleted = FALSE;


CREATE TABLE organization_members (
                                      id              BIGSERIAL   PRIMARY KEY,
                                      organization_id BIGINT      NOT NULL REFERENCES organizations (id) ON DELETE CASCADE,
                                      user_id         BIGINT      NOT NULL REFERENCES users (id)         ON DELETE CASCADE,
                                      role            VARCHAR(50) NOT NULL,
                                      joined_at       TIMESTAMP   NOT NULL DEFAULT NOW(),
                                      created_at      TIMESTAMP   NOT NULL DEFAULT NOW(),
                                      updated_at      TIMESTAMP   NOT NULL DEFAULT NOW(),
                                      created_by      BIGINT      REFERENCES users (id) ON DELETE SET NULL,
                                      updated_by      BIGINT      REFERENCES users (id) ON DELETE SET NULL,
                                      deleted_at      TIMESTAMP,
                                      deleted         BOOLEAN     NOT NULL DEFAULT FALSE,

                                      CONSTRAINT uk_org_members_org_user UNIQUE (organization_id, user_id)
);

CREATE INDEX idx_org_members_org_id  ON organization_members (organization_id) WHERE deleted = FALSE;
CREATE INDEX idx_org_members_user_id ON organization_members (user_id)         WHERE deleted = FALSE;
CREATE INDEX idx_org_members_role    ON organization_members (role)             WHERE deleted = FALSE;

ALTER TABLE teams
    ADD COLUMN organization_id BIGINT REFERENCES organizations (id) ON DELETE SET NULL;

CREATE INDEX idx_teams_organization_id ON teams (organization_id);

COMMENT ON TABLE  organizations               IS 'Top-level organizational units';
COMMENT ON TABLE  organization_members        IS 'Membership and roles within an organization';
COMMENT ON COLUMN organizations.tier          IS 'Subscription tier: FREE | PROFESSIONAL | ENTERPRISE';
COMMENT ON COLUMN organization_members.role   IS 'ORG_ADMIN | TEAM_ADMIN | MEMBER';
COMMENT ON COLUMN organizations.deleted_at    IS 'Populated by service layer on soft-delete; NULL = active';
COMMENT ON COLUMN organization_members.deleted_at IS 'Populated by service layer on soft-delete; NULL = active';
