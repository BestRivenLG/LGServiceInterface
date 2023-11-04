
scp /Users/yaro/Desktop/Java/LGServiceInterface-1.0-SNAPSHOT.jar root@api.lopcool.com://usr/product/java/LGService
ssh -t root@api.lopcool.com "pkill -f 'java -jar /usr/product/java/LGService/LGServiceInterface-1.0-SNAPSHOT.jar'"

scp /Users/yaro/Desktop/Java/LGServiceInterface-1.0-SNAPSHOT.jar root@nlb.lopcool.com://usr/product/java/LGService
ssh -t root@api.lopcool.com "pkill -f 'java -jar /usr/product/java/LGService/LGServiceInterface-1.0-SNAPSHOT.jar'"

scp /Users/yaro/Desktop/Java/LGServiceInterface-1.0-SNAPSHOT.jar root@root@8.134.191.74://usr/product/java/LGService
ssh -t root@8.134.191.74 "pkill -f 'java -jar /usr/product/java/LGService/LGServiceInterface-1.0-SNAPSHOT.jar'"

scp /Users/yaro/Desktop/Java/LGServiceInterface-1.0-SNAPSHOT.jar root@ymx.lopcool.com://usr/product/java/LGService
ssh -t root@api.lopcool.com "pkill -f 'java -jar /usr/product/java/LGService/LGServiceInterface-1.0-SNAPSHOT.jar'"

