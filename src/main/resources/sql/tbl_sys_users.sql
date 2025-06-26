/* 
 * Copyright Joe1962
 * https://github.com/Joe1962
 */

/**
 * Author:  joe1962
 * Created: Jul 15, 2024
 */

-- NOTE: To use with psql replace $DATABASE with correct DB name:
-- sudo -u postgres psql $DATABASE -f tbl_sys_users.sql

-- NOTE: Replace $OWNER below with correct role name.



-- public.sys_users definition
-- DROP TABLE public.sys_users;
CREATE TABLE public.sys_users (
	uuid uuid NOT NULL DEFAULT uuid_generate_v4(),
	name varchar(64) NOT NULL,
	password varchar(64) NOT NULL,
	admin boolean NOT NULL DEFAULT false,
	CONSTRAINT pk_sys_users PRIMARY KEY (uuid),
	CONSTRAINT "uq_sys_users_name" UNIQUE (name)
)



TABLESPACE pg_default;

ALTER TABLE IF EXISTS public.sys_users OWNER to $OWNER;
GRANT ALL ON TABLE public.sys_users TO $OWNER;



-- Index: idx_sales_master_id_payment
-- DROP INDEX IF EXISTS public.idx_sys_users_name;
CREATE INDEX IF NOT EXISTS idx_sys_users_name
	ON public.sys_users USING btree
	(name ASC NULLS FIRST)
	TABLESPACE pg_default;

;

