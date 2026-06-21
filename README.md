# Rebuild all
```bash
cd user-service && ./gradlew clean bootJar && cd .. && cd restaurant-service && ./gradlew clean bootJar && cd .. && cd order-service && ./gradlew clean bootJar && cd .. && cd notification-service && ./gradlew clean bootJar && cd .. && cd gateway && ./gradlew clean bootJar && cd ..

docker compose up --build
```