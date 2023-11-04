#scp -i iosphp.pem root@156.247.14.169://opt/app/lunpan/access.log /Users/yaro/Desktop/Php/log/1010

# php日志
#scp -i iosphp.pem root@156.247.14.169://home/wwwlogs/access.log /Users/yaro/Desktop/Php/log/1018


# java日志
#scp -i iosphp.pem root@156.247.14.169://opt/app/access.log /Users/yaro/Desktop/Php/log/java/1021
#
#scp -i iosphp.pem root@156.247.14.169://opt/app/access.log /Users/yaro/Desktop/Php/log/java/1021

#scp /Users/yaro/Desktop/Java/LGServiceInterface-1.0-SNAPSHOT.jar root@www.lopcool.com:/usr/product/java/LGService
#ssh root@www.lopcool.com


scp /Users/yaro/Desktop/Java/LGServiceInterface-1.0-SNAPSHOT.jar root@api.lopcool.com://usr/product/java/LGService
scp /Users/yaro/Desktop/Java/LGServiceInterface-1.0-SNAPSHOT.jar root@ymx.lopcool.com://usr/product/java/LGService
scp /Users/yaro/Desktop/Java/LGServiceInterface-1.0-SNAPSHOT.jar root@nlb.lopcool.com://usr/product/java/LGService

scp /Users/yaro/Desktop/Java/LGServiceInterface-1.0-SNAPSHOT.jar root@8.134.191.74://usr/product/java/LGService

8.134.191.74 root Rootqwe123

nohup java -jar /usr/product/java/LGService/LGServiceInterface-1.0-SNAPSHOT.jar -> /usr/product/java/LGService/LGService.log &


ssh root@api.lopcool.com
ssh root@ymx.lopcool.com
ssh root@nlb.lopcool.com
ssh root@8.134.191.74


ps -ef | grep java
kill -9 进程ID

pkill -f "java -jar /usr/product/java/LGService/LGServiceInterface-1.0-SNAPSHOT.jar"
