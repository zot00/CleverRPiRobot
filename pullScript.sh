#!/bin/sh
sleep 20
while [ 1 ]; do
	if [ $(git rev-parse HEAD) = $(git ls-remote $(git rev-parse --abbrev-ref @{u} |\
sed 's/\// /g') | cut -f1) ]; then
        	echo up to date
	else 
       		git pull;
		#remove gradle cache locks
		find ~/.gradle -type f -name "*.lock" | while read f; do rm $f; done
		rm -rf .gradle/*/*/*.lock 
        	./gradlew run
	fi
	sleep 5
done


