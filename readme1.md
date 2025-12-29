mvn clean package -DskipTests

docker compose down

docker compose build --no-cache

docker compose up -d

If you want everything in one go:

mvn clean package -DskipTests && docker compose down && docker compose build --no-cache && docker compose up -d







Step 1: Maven Build (Required)
This packages the new Secret and the Code Fixes.
Shell Script
mvn clean package -pl his-common,his-config-server,his-auth-service -am -DskipTests
Step 2: Docker Restart (Required)
This restarts the services with the new configuration.
Shell Script
docker-compose up -d --build --no-deps his-config-server his-auth-service
Step 3: Verify Startup
Check that his-auth-service is finally running green.
Shell Script
docker-compose logs -f his-auth-service
After these steps are done and the service is running, then you can proceed to generate the token and use Swagger





========
docker exec -it keycloak curl -X POST \
http://keycloak:8080/realms/master/protocol/openid-connect/token \
-H "Content-Type: application/x-www-form-urlencoded" \
-d "client_id=his-client" \
-d "client_secret=iHJIHU2c5tKEopgTW0wltGlkvIm982AE" \
-d "grant_type=client_credentials"


