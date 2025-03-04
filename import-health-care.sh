#!/bin/bash

echo "Waiting for PostgreSQL to be ready..."
until psql -U "$POSTGRES_USER" -d "$POSTGRES_DB" -c "SELECT 1;" &> /dev/null; do
  sleep 30
  echo "Waiting for PostgreSQL... still not ready."
done
echo "PostgreSQL is ready."

# Run init.sql explicitly
echo "Running init.sql..."
psql -U "$POSTGRES_USER" -d "$POSTGRES_DB" -f "/docker-entrypoint-initdb.d/init.sql" || echo "Failed to run init.sql"

# Ensure the health_insurance table exists before proceeding
echo "Checking if health_insurance table exists..."
until psql -U "$POSTGRES_USER" -d "$POSTGRES_DB" -c "SELECT to_regclass('public.health_insurance');" | grep -q "health_insurance"; do
  echo "Waiting for health_insurance table to be created..."
  sleep 30
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
