#!/bin/sh

# create use sportrait that will be owner of tables for sportrait app:
createuser -e -P sportrait

# using the created user, create DB:
createdb --owner sportrait -E UTF8 -e sportrait

# set the password for the use sportrait
psql -c "ALTER USER sportrait PASSWORD 'unartig';"