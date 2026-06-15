-- Migration pour les nouvelles fonctionnalités de réservation et événements privés

ALTER TABLE event ADD COLUMN IF NOT EXISTS est_prive BOOLEAN NOT NULL DEFAULT FALSE;

ALTER TABLE reservation ADD COLUMN IF NOT EXISTS id_user BIGINT NOT NULL DEFAULT 0;

CREATE INDEX IF NOT EXISTS idx_reservation_id_user ON reservation(id_user);
CREATE INDEX IF NOT EXISTS idx_reservation_id_event ON reservation("idEvent");
