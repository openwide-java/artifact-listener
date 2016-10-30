FROM postgres:9.5

# Note that postgresql.sql is usually copied by postgres-start.sh to this directory
COPY init_db.sh /docker-entrypoint-initdb.d
COPY postgresql.sql /tmp