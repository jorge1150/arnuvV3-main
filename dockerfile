# Usa una imagen oficial de OpenJDK 17
FROM openjdk:17-jdk-alpine

# Establece el directorio de trabajo
WORKDIR /app

# Copia y ejecuta Maven para compilar el JAR dentro del contenedor
COPY . .
RUN ./mvnw clean package -DskipTests

# Usa el JAR generado en target/
CMD ["java", "-jar", "target/arnuv-0.0.1-SNAPSHOT.jar"]