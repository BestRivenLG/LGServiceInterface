
scp /Users/yaro/Desktop/Java/LGService-1.0-SNAPSHOT.jar root@api.asf.com://usr/product/java/LGService

ssh root@api.asf.com

pkill -f "java -jar /usr/LGService-1.0-SNAPSHOT.jar"

#nohup java -jar /usr/LGService-1.0-SNAPSHOT.jar -> /usr/LGService.log &
