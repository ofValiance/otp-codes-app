### register
curl -i -X POST http://localhost:8080/api/auth/register -H "Content-Type: application/json" -d "{\"login\": \"user1\", \"password\": \"1234\", \"role\": \"USER\"}"
curl -i -X POST http://localhost:8080/api/auth/register -H "Content-Type: application/json" -d "{\"login\": \"user2\", \"password\": \"4321\", \"role\": \"USER\"}"
curl -i -X POST http://localhost:8080/api/auth/register -H "Content-Type: application/json" -d "{\"login\": \"user3\", \"password\": \"0987\", \"role\": \"USER\"}"
curl -i -X POST http://localhost:8080/api/auth/register -H "Content-Type: application/json" -d "{\"login\": \"admin\", \"password\": \"1234\", \"role\": \"ADMIN\"}"

### login
curl -i -X POST http://localhost:8080/api/auth/login -H "Content-Type: application/json" -d "{\"login\": \"admin\", \"password\": \"1234\"}"
curl -i -X POST http://localhost:8080/api/auth/login -H "Content-Type: application/json" -d "{\"login\": \"user1\", \"password\": \"1234\"}"

### generate
curl -i -X POST http://localhost:8080/api/otp/generate -H "Content-Type: application/json" -H "Authorization: Bearer " -d "{\"operationId\": \"1\", \"channel\": \"FILE\", \"destination\": \"root\"}"

### validate
curl -i -X POST http://localhost:8080/api/otp/validate -H "Content-Type: application/json" -H "Authorization: Bearer " -d "{\"operationId\": \"1\", \"code\": \"317\"}"

### update config
curl -i -X PUT http://localhost:8080/api/admin/otp-config -H "Content-Type: application/json" -H "Authorization: Bearer " -d "{\"codeLength\": \"3\", \"ttlSeconds\": \"300\"}"

### get config
curl -i -X GET http://localhost:8080/api/admin/otp-config -H "Content-Type: application/json" -H "Authorization: Bearer "

### get users
curl -i -X GET http://localhost:8080/api/admin/users -H "Content-Type: application/json" -H "Authorization: Bearer "

### delete user
curl -i -X DELETE http://localhost:8080/api/admin/users/2 -H "Content-Type: application/json" -H "Authorization: Bearer "