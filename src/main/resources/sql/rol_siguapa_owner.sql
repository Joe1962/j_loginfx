/**
 * Author:  joe1962
 * Created: Jun 30, 2025
 */

-- NOTE: before using in production, replace password '123' with a secure one...

-- NOTE: Replace $OWNER below with correct role name.

-- NOTE: To use with psql replace $DATABASE with correct name:
-- sudo -u postgres psql $DATABASE -f rol_siguapa_owner.sql



-- DROP ROLE siguapa_owner;

CREATE ROLE $OWNER WITH 
	PASSWORD '123'
	NOSUPERUSER
	NOCREATEDB
	NOCREATEROLE
	INHERIT
	LOGIN
	NOREPLICATION
	NOBYPASSRLS
	CONNECTION LIMIT -1;

