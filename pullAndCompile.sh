
activeDir=/SFA/StarFleetAssaultServer/Server
gitDir=/staging/SFA/StarFleetAssaultServer
package=com/adam4/SFA
config=`cat /SFA/config/ServerConfig.txt`



cd $gitDir && git reset --hard HEAD
cd $gitDir && git fetch origin
cd $gitDir && git reset --hard origin/master
javac -cp $gitDir/Server/src/$package/*.java
cp $gitDir/Server/src/$package/*.class $activeDir/bin/$package/
cp $gitDir/Server/lib/* $activeDir/lib/

#cp $activeDir/bin/$package/* $gitDir/Server/src/$package/
#cp $activeDir/lib/* $gitDir/Server/lib/


java -cp $activeDir/lib/*:$activeDir/bin/ $package.TestMyLogger $config
