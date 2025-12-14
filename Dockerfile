# JDK 21 런타임
FROM eclipse-temurin:21-jre

WORKDIR /app

# 빌드 산출물만 복사 (시크릿/프로퍼티는 복사하지 않음)
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} app.jar

# 기본 프로필만 디폴트로 두고, 실제 프로필은 런타임에서 SPRING_PROFILES_ACTIVE로 주입
ENV DEFAULT_PROFILE=prod

EXPOSE 8080

# -D 옵션은 반드시 -jar 앞에 와야 함
# SPRING_PROFILES_ACTIVE가 없으면 DEFAULT_PROFILE 사용
ENTRYPOINT ["sh","-c","exec java $JAVA_OPTS -Dspring.profiles.active=${SPRING_PROFILES_ACTIVE:-$DEFAULT_PROFILE} -jar /app/app.jar"]