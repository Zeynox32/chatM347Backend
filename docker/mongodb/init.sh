#!/bin/bash

mongosh <<EOF
use $MONGO_APP_DB

db.createUser({
  user: "$MONGO_APP_USER",
  pwd: "$MONGO_APP_PASSWORD",
  roles: [
    {
      role: "readWrite",
      db: "$MONGO_APP_DB"
    }
  ]
})
EOF