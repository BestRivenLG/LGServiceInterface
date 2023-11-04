#ssh root@8.134.191.74 /usr/product/java/javakill.sh
ssh -t root@8.134.191.74 "pkill -f 'java -jar /usr/product/java/LGService/LGServiceInterface-1.0-SNAPSHOT.jar'"
