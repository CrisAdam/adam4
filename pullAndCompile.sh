
activeDir=/home/ec2-user/adam4/run
gitDir=/home/ec2-user/adam4
package=com/adam4/SFA
config=`cat /home/ec2-user`



cd $gitDir && git reset --hard HEAD
cd $gitDir && git fetch origin
cd $gitDir && git reset --hard origin/master
javac -cp $gitDir/Server/src/$package/*.java
cp $gitDir/Server/src/$package/*.class $activeDir/bin/$package/
cp $gitDir/Server/lib/* $activeDir/lib/

#cp $activeDir/bin/$package/* $gitDir/Server/src/$package/
#cp $activeDir/lib/* $gitDir/Server/lib/


/opt/jdk1.8.0_40/bin/java -cp $activeDir/lib/*:$activeDir/bin/ $package.TestMyLogger $config
