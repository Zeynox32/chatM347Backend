#!/bin/bash
set -e

HOST="${MONGODB_HOST:-mongodb}"

until mongosh --host "$HOST" --quiet -u "$MONGO_ROOT_USERNAME" -p "$MONGO_ROOT_PASSWORD" --authenticationDatabase admin --eval "db.runCommand({ping:1})" >/dev/null 2>&1; do
  echo "waiting for mongodb at $HOST ..."
  sleep 2
done

mongosh --host "$HOST" --quiet -u "$MONGO_ROOT_USERNAME" -p "$MONGO_ROOT_PASSWORD" --authenticationDatabase admin <<'EOF'
const dbName = process.env.MONGO_APP_DB;
const appDb = db.getSiblingDB(dbName);
if (appDb.getUser(process.env.MONGO_APP_USER) === null) {
  appDb.createUser({
    user: process.env.MONGO_APP_USER,
    pwd: process.env.MONGO_APP_PASSWORD,
    roles: [{ role: "readWrite", db: dbName }]
  });
  print("app_user created in " + dbName);
} else {
  print("app_user already exists in " + dbName);
}
EOF
