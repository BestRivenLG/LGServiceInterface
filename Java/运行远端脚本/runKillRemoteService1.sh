#ssh root@nlb.lopcool.com /usr/product/java/javakill.sh
ssh -t root@nlb.lopcool.com "pkill -f 'java -jar /usr/product/java/LGService/LGServiceInterface-1.0-SNAPSHOT.jar'"
