This is excellent news! The docker ps output shows that all of your services are
running and healthy, including his-auth-service. This means all the previous startup and 
configuration issues are resolved.

The new error you are seeing, DNS_PROBE_FINISHED_NXDOMAIN, is actually expected, and 
it's the final hurdle.
Why This Error Happens
•
The hostname keycloak is only valid inside the Docker network for containers to talk to each other.
•
Your web browser runs on your Windows machine, which is outside that Docker network. 
It has no idea how to find a server named keycloak.
The Solution: Use localhost
To access any of your services from your browser, you must use localhost along 
with the port that is mapped in your docker-compose.yml file.
For Keycloak, the mapping is: ports: - "8180:8080". This means port 8180 on your Windows machine
is connected to port 8080 inside the Keycloak container.
So, the correct address for Keycloak from your browser is http://localhost:8180.
Let's Test the Project
Now that everything is running correctly, you can proceed with testing the full application.
Step 1: Get the Access Token
Open a new terminal and run the following curl command, using localhost:8180 to connect to Keycloak:
Shell Script
curl -d "client_id=admin-cli" -d "username=admin" -d "password=admin" -d "grant_type=password" "http://localhost:8180/realms/master/protocol/openid-connect/token"
Step 2: Copy the Token
From the JSON response, copy the long string value from the access_token field.
Step 3: Authorize in Swagger
1.
Open your browser and go to the Swagger UI: http://localhost:8080/swagger-ui.html
2.
Click the Authorize button at the top right.
3.
In the popup, paste the token you copied into the "Value" field. Make sure to add Bearer 
before the token (with a space after "Bearer").
4.
Click Authorize, then Close.
Step 4: Test an API
1.
From the "Select a definition" dropdown, choose admin.
2.
Expand the GET /plans endpoint and click "Try it out".
3.
Click "Execute".
You should now get a 200 OK response with the API data. You have successfully fixed all the issues. Congratulation





===================for getting the token admin based
curl -X POST "http://localhost:8080/realms/master/protocol/openid-connect/token" \
-H "Content-Type: application/x-www-form-urlencoded" \
-d "client_id=his-client" \
-d "client_secret=1JTEnA4t9ysU2ReEBqsrT3nbUDTbBGaW" \
-d "grant_type=client_credentials"