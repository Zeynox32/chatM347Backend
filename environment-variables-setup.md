## You'll need to set the following environment-variables in the runtime-configuration in your IDE. Once you arrived at this place click environment variables and add:
- DB_USER
- DB_PASSWORD
- DB_NAME
- DB_URL
- FRONTEND_URL
## Also, you'll need to set the profile to "local" in the runtime-configuration
## For local testing navigate to the folder docker/ and add a .env-file with the following values:
- COMPOSE_PROJECT_NAME
- DB_NAME
- DB_PASSWORD
- DB_URL
- DB_USER
## For the last step, when you want to run the test, edit the test runtime-configuration and add:
- FRONTEND_URL
