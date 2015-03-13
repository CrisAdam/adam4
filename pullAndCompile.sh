
activeDir=/home/ec2-user/adam4/run
gitDir=/home/ec2-user/adam4
config=`cat /home/ec2-user/config.txt`
srcDir=$gitDir/SFAServerWorkspace/SFAServer/src


cd $gitDir && git reset --hard HEAD
cd $gitDir && git fetch origin
cd $gitDir && git reset --hard origin/master
ls $gitDir/SFAServerWorkspace/SFAServer/src/**/**/*.java
#remove unrelated projects
rm -r $srcDir/com/adam4/misc
rm -r $srcDir/com/adam4/irc
rm -r $srcDir/com/adam4/spacenet
#remove test files that may not compile
find $srcDir -type f -name '*test*.java' -delete 
find $srcDir -type f -name '*Test*.java' -delete 

#/opt/jdk1.8.0_40/bin/javac -sourcepath $gitDir/SFAServerWorkspace/SFAServer/src/**/**/*.java



/opt/jdk1.8.0_40/bin/javac $(find $srcDir -name *.java)



cp $gitDir/Server/src/$package/*.class $activeDir/bin/$package/
cp $gitDir/Server/lib/* $activeDir/lib/

#cp $activeDir/bin/$package/* $gitDir/Server/src/$package/
#cp $activeDir/lib/* $gitDir/Server/lib/


/opt/jdk1.8.0_40/bin/java -cp $activeDir/lib/*:$activeDir/bin/ $package.TestMyLogger $config
