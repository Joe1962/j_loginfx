/* 
 * Copyright Joe1962
 * https://github.com/Joe1962
 */

/**
 * ext_uuid_ossp.sql
 *
 * Author:  joe1962
 * Created: Jul 29, 2025
 */

-- NOTE: To use with psql replace $DATABASE with correct DB name:
-- sudo -u postgres psql $DATABASE -f ext_uuid_ossp.sql



CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

;

