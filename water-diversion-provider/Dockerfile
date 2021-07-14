# 基础镜像
FROM openjdk:8-jdk-alpine

# 作者信息
MAINTAINER "sgwl"

# 添加一个存储空间
VOLUME /tmp

ENV TZ=Asia/Shanghai
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

# 暴露8080端口
#EXPOSE 8030
ARG JAR_FILE

# 往容器中添加jar包
ADD ${JAR_FILE} app.jar

# 启动镜像自动运行程序
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/urandom","-jar","/app.jar"]

# 使用spring-boot对应配置环境
#CMD ["--spring.profiles.active=dev"]