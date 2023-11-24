
cd /usr/product/java/LGServices
echo '重置本地代码'
git reset --hard
echo '拉取远端代码'
git pull

echo 'package finished'
mvn clean package
echo '开始后台运行程序'
current_path=$(readlink -f .)

last_path=$(readlink -f ..)

log_dir="$last_path/LGService"
log_path="$last_path/LGService/LGService.log"
if [[ -d $log_dir ]]; then
    echo "找到 JAR 文件: $target"
else
    mkdir $log_path
    echo "没有找到 JAR 文件"
fi

#target="$current_path/target/JavaTestDemo-1.0-SNAPSHOT.jar"
target=$(find "$current_path/target" -maxdepth 1 -name "*.jar" | head -n 1)

if [[ -f "$target" ]]; then
    echo "找到 JAR 文件: $target"

    # 在这里执行对 JAR 文件的操作
    echo "结束之前进程"
    pkill -f "java -jar $target"
    echo "结束之前进程 完成"
    
    echo "开始后台运行jar程序"
    nohup java -jar $target > $log_path &
    echo "后台运行完成"

    # 查看进程
    ps -ef|grep java
else
    echo "没有找到 JAR 文件"
fi

