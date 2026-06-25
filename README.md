# OtpCodesApp

## Описание

Backend-система для отправки и валидации временных кодов. Цель сервиса -- обеспечить безопасность операций, 
требующих подтверждения.

Реализован REST API сервер с базой данных PostgreSQL, развертываемый в Docker.

## Пользовательский сценарий

- создать пользователя с уровнем доступа ADMIN или USER
- авторизоваться в системе
- ADMIN может:
  - управлять конфигурацией кодов (длиной, временем жизни)
  - просматривать список пользователей
  - удалять пользователей и связанные с ними коды
- USER может:
  - запросить код для операции, указав способ отправки (только FILE -- SMS, EMAIL и TELEGRAM не готовы)
  - подтвердить операцию с помощью полученного кода

## Установка

1) Установить Docker Desktop
2) Клонировать репозиторий
3) Переименовать .env.example в .env
4) В .env заполнить DB_USER, DB_PASSWORD, JWT_KEY
5) С открытым докером запустить launch.bat (win) или launch.sh (linux)

## Примеры запросов

В Windows Command Line.

### Регистрация

curl -X POST http://localhost:8080/api/auth/register -H "Content-Type: application/json" -d "{\"login\":
\"**логин**\", \"password\": \"**пароль**\", \"role\": \"**ADMIN или USER**\"}"

### Авторизация

curl -X POST http://localhost:8080/api/auth/login -H "Content-Type: application/json" -d "{\"login\": \"**логин**\",
\"password\": \"**пароль**\"}"

После проверки пароля сервер пришлет в ответе токен, который понадобится в других запросах к API.

### Запрос кода (USER)

curl -X POST http://localhost:8080/api/otp/generate -H "Content-Type: application/json" -H "Authorization: Bearer 
**ваш токен**" -d "{\"operationId\": \"**дайте номер операции для подтверждения**\", \"channel\": \"**FILE для 
сохранения кода в файл**\", \"destination\": \"**путь и файл для сохранения, например user1/code.txt**\"}"

По умолчанию путь сохранения задается относительно папки otp_output в корне проекта. Этот базовый путь можно 
изменить в .env файле (переменная OUTPUT_PATH).

### Подтверждение (USER)

curl -X POST http://localhost:8080/api/otp/validate -H "Content-Type: application/json" -H "Authorization: Bearer 
**ваш токен**" -d "{\"operationId\": \"**номер операции**\", \"code\": \"**полученный код**\"}"

### Просмотр и обновление конфигурации кодов (ADMIN)

curl -X GET http://localhost:8080/api/admin/otp-config -H "Content-Type: application/json" -H "Authorization:
Bearer **ваш токен**"

curl -X PUT http://localhost:8080/api/admin/otp-config -H "Content-Type: application/json" -H "Authorization:
Bearer **ваш токен**" -d "{\"codeLength\": \"**задайте длину кода, например 3**\", \"ttlSeconds\": \"**задайте время 
жизни, например 180**\"}"

### Просмотр списка пользователей и их удаление (ADMIN)

curl -i -X GET http://localhost:8080/api/admin/users -H "Content-Type: application/json" -H "Authorization: Bearer 
**ваш токен**"

curl -i -X DELETE http://localhost:8080/api/admin/users/{id_пользователя} -H "Content-Type: application/json" -H 
"Authorization: Bearer **ваш токен**"