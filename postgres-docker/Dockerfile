FROM postgres
MAINTAINER Alexander Josef alexander.josef@sportrait.com

USER postgres

RUN mkdir -p /docker-entrypoint-initdb.d
ADD docker-entrypoint-initdb.d /docker-entrypoint-initdb.d/

# Adjust PostgreSQL configuration so that remote connections to the
# database are possible.
RUN echo "host all  all    0.0.0.0/0  md5" >> /var/lib/postgresql/data/pg_hba.conf