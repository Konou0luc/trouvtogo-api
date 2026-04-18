-- À exécuter une fois si l'erreur PostgreSQL apparaît :
--   ERROR: function lower(bytea) does not exist
-- Cela signifie que titre ou description sont encore en BYTEA (ancien schéma).
-- Connexion : psql -U ... -d collectobjet -f scripts/fix-objets-bytea-to-text.sql

DO $$
BEGIN
  IF EXISTS (
    SELECT 1 FROM information_schema.columns
    WHERE table_schema = 'public' AND table_name = 'objets'
      AND column_name = 'titre' AND data_type = 'bytea'
  ) THEN
    ALTER TABLE objets
      ALTER COLUMN titre TYPE varchar(255)
      USING convert_from(titre, 'UTF8');
  END IF;

  IF EXISTS (
    SELECT 1 FROM information_schema.columns
    WHERE table_schema = 'public' AND table_name = 'objets'
      AND column_name = 'description' AND data_type = 'bytea'
  ) THEN
    ALTER TABLE objets
      ALTER COLUMN description TYPE text
      USING convert_from(description, 'UTF8');
  END IF;
END $$;
