#ssh root@ymx.lopcool.com /usr/product/java/javakill.sh
ssh -t root@ymx.lopcool.com "pkill -f 'java -jar /usr/product/java/LGService/LGServiceInterface-1.0-SNAPSHOT.jar'"
