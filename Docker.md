# 1. Create a Docker volume for persistent MySQL data
docker volume create mysql_data

# 2. Run MySQL container
docker run -d \
  --name mysql-reservation \
  -p 3306:3306 \
  -e MYSQL_ROOT_PASSWORD=cse \
  -e MYSQL_DATABASE=reservation_system \
  -v mysql_data:/var/lib/mysql \
  mysql:8.0

# 3. Check if container is running
docker ps

# 4. View logs (wait until "ready for connections")
docker logs -f mysql-reservation

# 5. Starting and Stopping
docker stop mysql-reservation
docker start mysql-reservation

# 6. Connect
docker exec -it mysql-reservation mysql -u root -p