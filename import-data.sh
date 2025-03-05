#!/bin/bash
# Run the import script in the background
( echo "Waiting for PostgreSQL to be ready..."
until psql -U "$POSTGRES_USER" -d "$POSTGRES_DB" -c "SELECT 1;" &> /dev/null; do
  sleep 60
  echo "Waiting for PostgreSQL... still not ready."
done
echo "PostgreSQL is ready."

# Ensure the health_insurance table exists before proceeding
echo "Checking if health_insurance table exists..."
until psql -U "$POSTGRES_USER" -d "$POSTGRES_DB" -c "SELECT to_regclass('public.health_insurance');" | grep -q "health_insurance"; do
  echo "Waiting for health_insurance table to be created..."
  sleep 240
done

echo "Importing JSON data into PostgreSQL..."
psql -U "$POSTGRES_USER" -d "$POSTGRES_DB" -c"
WITH json_data AS (
    SELECT jsonb_array_elements(pg_read_file('/docker-entrypoint-initdb.d/health-care-insurance.json')::jsonb) AS data
)
INSERT INTO health_insurance (
    ik, datum, antragsschluessel, anrede, namenszeile_1, namenszeile_2, namenszeile_3, namenszeile_4,
    h_strasse, h_lkz, h_plz, h_ort, p_lkz, p_ort, p_plz, postfach, tel_vorwahl, tel_ruf_nummer, fax_vorwahl, fax_nummer
)
SELECT
    (data ->> 'IK')::BIGINT,
    data ->> 'Datum',
    (data ->> 'Antragsschluessel')::VARCHAR,
    (data ->> 'Anrede')::VARCHAR,
    data ->> 'Namenszeile_1',
    data ->> 'Namenszeile_2',
    data ->> 'Namenszeile_3',
    data ->> 'Namenszeile_4',
    data ->> 'H_Strasse',
    data ->> 'H_LKZ',
    data ->> 'H_PLZ',
    data ->> 'H_Ort',
    data ->> 'P_LKZ',
    data ->> 'P_Ort',
    (data ->> 'P_PLZ')::VARCHAR,
    (data ->> 'Postfach')::VARCHAR,
    data ->> 'TelVorwahl',
    data ->> 'TelRufNummer',
    data ->> 'FaxVorwahl',
    data ->> 'FaxNummer'
FROM json_data;
"

echo "JSON import completed."

echo "Inserting workflow and step data..."
psql -U "$POSTGRES_USER" -d "$POSTGRES_DB" -c "
-- Insert into workflow table if not exists
INSERT INTO workflow (id)
VALUES (1)
ON CONFLICT (id) DO NOTHING;

-- Insert steps into step table
INSERT INTO step (id, name, skippable, parent_step_id)
VALUES
    (0, 'Klinische Daten', false, NULL),
    (1, 'Anforderung', false, NULL),
    (2, 'Genetische Daten', false, NULL),
    (3, 'MTB-Beschluss und MTB-Report', false, NULL),
    (4, 'Übermittlung', false, NULL)
ON CONFLICT (id) DO NOTHING;

-- Insert into workflow_steps table if not exists
INSERT INTO workflow_steps (workflow_id, steps_id)
VALUES
    (1, 0),
    (1, 1),
    (1, 2),
    (1, 3),
    (1, 4)
ON CONFLICT DO NOTHING;
"
echo "Workflow and step data insertion completed."
) &
# Immediately exit with a successful status
exit 0
