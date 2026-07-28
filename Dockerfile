# ---- Build stage: Render 등 docker build만 실행하는 환경에서도 jar를 직접 빌드 ----
FROM eclipse-temurin:21-jdk AS builder

WORKDIR /src

COPY . .

RUN chmod +x gradlew && ./gradlew clean build -x test --no-daemon

# ---- Runtime stage ----
FROM eclipse-temurin:21-jre

RUN apt-get update && apt-get install -y --no-install-recommends curl && rm -rf /var/lib/apt/lists/*

WORKDIR /app

# 빌드 스테이지 산출물만 복사 (시크릿/프로퍼티는 복사하지 않음)
COPY --from=builder /src/build/libs/*.jar app.jar

# 기본 프로필만 디폴트로 두고, 실제 프로필은 런타임에서 SPRING_PROFILES_ACTIVE로 주입
ENV DEFAULT_PROFILE=prod

EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=5s --start-period=40s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

# -D 옵션은 반드시 -jar 앞에 와야 함
# SPRING_PROFILES_ACTIVE가 없으면 DEFAULT_PROFILE 사용
ENTRYPOINT ["sh","-c","exec java $JAVA_OPTS -Dspring.profiles.active=${SPRING_PROFILES_ACTIVE:-$DEFAULT_PROFILE} -jar /app/app.jar"]