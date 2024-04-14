# 빌드 단계에 사용할 Eclipse Temurin OpenJDK 17 이미지
FROM eclipse-temurin:17-jdk as build

# 작업 디렉토리 설정
WORKDIR /workspace/app

# 애플리케이션의 gradle wrapper와 소스 코드를 컨테이너에 복사
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY src src

# 애플리케이션 빌드
# -x test는 빌드 과정에서 테스트를 건너뛰도록 함
RUN ./gradlew build -x test

# 실행 단계에 사용할 Eclipse Temurin OpenJDK 17 JRE 이미지
FROM eclipse-temurin:17-jre

# 컨테이너 내부의 애플리케이션 실행을 위한 디렉토리 생성
WORKDIR /app

# 빌드 단계에서 생성된 실행 가능한 jar 파일을 실행 디렉토리로 복사
COPY --from=build /workspace/app/build/libs/*.jar app.jar

# 애플리케이션 실행
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app/app.jar"]
