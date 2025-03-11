FROM openjdk:25-ea-4-jdk-oraclelinux9

WORKDIR /app

COPY target/mini1.jar app.jar

COPY src/main/java/com/example/data /app/data

ENV USER_DATA_PATH=/app/data/users.json \
    CART_DATA_PATH=/app/data/carts.json \
    ORDER_DATA_PATH=/app/data/orders.json \
    PRODUCT_DATA_PATH=/app/data/products.json

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]