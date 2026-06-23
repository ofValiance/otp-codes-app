### register
curl -i -X POST http://localhost:8080/api/auth/register -H "Content-Type: application/json" -d "{\"login\": \"valera\", \"password\": \"secret123\", \"role\": \"USER\"}"

### login
curl -i -X POST http://localhost:8080/api/auth/login -H "Content-Type: application/json" -d "{\"login\": \"valera\", \"password\": \"secret123\"}"

### generate
curl -i -X POST http://localhost:8080/api/otp/generate -H "Content-Type: application/json" -H "Authorization: Bearer " -d "{\"operationId\": \"453\", \"channel\": \"FILE\", \"destination\": \"werwre\"}"

### validate
curl -i -X POST http://localhost:8080/api/otp/validate -H "Content-Type: application/json" -H "Authorization: Bearer " -d "{\"operationId\": \"2\", \"code\": \"572599\"}"