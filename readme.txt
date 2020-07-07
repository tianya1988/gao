#运行示例:

java -jar /home/jason/project/gao/target/gao-1.0.jar /home/jason/Desktop/car2 2020-05-06_09:15:04 2020-05-07_02:28:04

# 打包注意事项:
# 打包命令
 mvn -Dmaven.test.skip=true  assembly:assembly

#pom文件中虽然assembly为红色,但是依然能打包成功

