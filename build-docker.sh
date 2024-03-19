docker images -a | grep "3170proj" | awk '{print $3}' | xargs docker rmi -f
docker rmi $(docker images -f "dangling=true" -q)
docker build -t 3170proj:latest .