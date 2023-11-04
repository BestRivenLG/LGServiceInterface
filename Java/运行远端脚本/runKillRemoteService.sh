#ssh root@api.lopcool.com /usr/product/java/javakill.sh
ssh -t root@api.lopcool.com "pkill -f 'java -jar /usr/product/java/LGService/LGServiceInterface-1.0-SNAPSHOT.jar'"
