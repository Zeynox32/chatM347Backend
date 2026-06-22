#!/bin/bash

mongosh <<EOF
use admin

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