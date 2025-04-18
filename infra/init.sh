#!/bin/bash
# 4GB SWAP MEMORY
sudo dd if=/dev/zero of=/swapfile bs=128M count=32
sudo chmod 600 /swapfile
sudo mkswap /swapfile
sudo swapon /swapfile
sudo sh -c 'echo "/swapfile swap swap defaults 0 0" >> /etc/fstab'

# INSTALL AND START DOCKER
yum install docker -y
systemctl enable docker
systemctl start docker

# CREATE DOCKER NETWORK
docker network create common

# INSTALL NGINX PROXY MANAGER
docker run -d \
  --name npm_1 \
  --restart unless-stopped \
  --network common \
  -p 80:80 \
  -p 443:443 \
  -p 81:81 \
  -e TZ=Asia/Seoul \
  -v /dockerProjects/npm_1/volumes/data:/data \
  -v /dockerProjects/npm_1/volumes/etc/letsencrypt:/etc/letsencrypt \
  jc21/nginx-proxy-manager:latest

# INSTALL REDIS
docker run -d \
  --name=redis_1 \
  --restart unless-stopped \
  --network common \
  -p 6379:6379 \
  -e TZ=Asia/Seoul \
  redis --requirepass ${var.password_1}

# INSTALL MYSQL
docker run -d \
  --name mysql_1 \
  --restart unless-stopped \
  -v /dockerProjects/mysql_1/volumes/var/lib/mysql:/var/lib/mysql \
  -v /dockerProjects/mysql_1/volumes/etc/mysql/conf.d:/etc/mysql/conf.d \
  --network common \
  -p 3306:3306 \
  -e MYSQL_ROOT_PASSWORD=${var.password_1} \
  -e TZ=Asia/Seoul \
  mysql:latest

# WAIT FOR MYSQL
echo "waiting for mysql booting..."
until docker exec mysql_1 mysql -uroot -p${var.password_1} -e "SELECT 1" &> /dev/null; do
  echo "mysql is not ready. retry after 5s..."
  sleep 5
done
echo "MySQL is ready. Running init script..."

docker exec mysql_1 mysql -uroot -p${var.password_1} -e "
CREATE USER 'lldjlocal'@'127.0.0.1' IDENTIFIED WITH caching_sha2_password BY '1234';
CREATE USER 'lldjlocal'@'172.17.%.%' IDENTIFIED WITH caching_sha2_password BY '1234';
CREATE USER 'lldjlocal'@'172.18.%.%' IDENTIFIED WITH caching_sha2_password BY '1234';
CREATE USER 'lldj'@'%' IDENTIFIED WITH caching_sha2_password BY '${var.password_1}';

GRANT ALL PRIVILEGES ON *.* TO 'lldjlocal'@'127.0.0.1';
GRANT ALL PRIVILEGES ON *.* TO 'lldjlocal'@'172.18.%.%';
GRANT ALL PRIVILEGES ON *.* TO 'lldj'@'%';

CREATE DATABASE glog_prod;

FLUSH PRIVILEGES;
"

echo "${var.github_access_token_1}" | docker login ghcr.io -u "${var.github_access_token_1_owner}" --password-stdin