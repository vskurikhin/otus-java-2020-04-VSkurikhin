GRANT USAGE ON FOREIGN DATA WRAPPER postgres_fdw TO dbuser;

CREATE SERVER IF NOT EXISTS fdw_postgres_0 FOREIGN DATA WRAPPER postgres_fdw
  OPTIONS (host 'postgres-0.default.svc.cluster.local', port '5430', dbname 'db');

CREATE USER MAPPING IF NOT EXISTS FOR dbuser SERVER fdw_postgres_0
  OPTIONS (user 'dbuser', password 'password');
