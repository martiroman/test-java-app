# Usa una imagen base de Java
FROM openjdk:11-jre-slim

# Copia el JAR ejecutable a la imagen
COPY target/mi-app-1.0-SNAPSHOT.jar /app/app.jar

# Define el directorio de trabajo
WORKDIR /app

# Comando para ejecutar la aplicación cuando se inicie el contenedor
CMD ["java", "-jar", "app.jar"]
