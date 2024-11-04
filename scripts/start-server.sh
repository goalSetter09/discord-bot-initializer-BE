#!/bin/bash

echo "--------------- 서버 배포 시작 -----------------"
docker stop initializer-server || true
docker rm initializer-server || true
docker pull 479084400092.dkr.ecr.ap-northeast-2.amazonaws.com/initializer-server:latest
docker run -d --name initializer-server -p 8080:8080 479084400092.dkr.ecr.ap-northeast-2.amazonaws.com/initializer-server:latest
echo "--------------- 서버 배포 끝 -----------------"