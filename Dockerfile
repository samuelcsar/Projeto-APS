# Multi-stage build para Java 17 com WebServer embutido
FROM eclipse-temurin:17-jdk-alpine AS builder

WORKDIR /app

# Copia código-fonte e estrutura
COPY src ./src
COPY WebServer.java ./
COPY index.html ./
COPY frontend ./frontend

# Compila as classes Java do backend e do servidor Web
RUN mkdir -p bin && \
    javac -encoding UTF-8 -d bin src/model/*.java src/dao/*.java src/factory/*.java src/observer/*.java src/strategy/*.java src/thirdparty/*.java src/service/*.java src/facade/*.java src/controller/*.java src/view/*.java && \
    javac -encoding UTF-8 -cp bin -d bin WebServer.java

# Imagem final de execução (mais leve)
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Copia os binários compilados e os arquivos estáticos da etapa anterior
COPY --from=builder /app/bin ./bin
COPY index.html ./
COPY frontend ./frontend

# Expõe a porta 8000 usada pelo WebServer Java
EXPOSE 8000

# Executa o WebServer
CMD ["java", "-cp", "bin", "WebServer"]
