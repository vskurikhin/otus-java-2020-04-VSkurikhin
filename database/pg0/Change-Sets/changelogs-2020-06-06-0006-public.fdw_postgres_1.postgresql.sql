GRANT USAGE ON FOREIGN DATA WRAPPER postgres_fdw TO dbuser;

CREATE SERVER IF NOT EXISTS fdw_postgres_1 FOREIGN DATA WRAPPER postgres_fdw
  OPTIONS (host 'postgres-1.default.svc.cluster.local', port '5431', dbname 'db');

CREATE USER MAPPING IF NOT EXISTS FOR dbuser SERVER fdw_postgres_1
  OPTIONS (user 'dbuser', password 'password');
