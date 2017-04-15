#! /bin/sh
# Adds the startup script to run at startup and shutdown
echo "copying to etc/init.d/startup.sh"
cp ./startup.sh /etc/init.d/startup.sh
echo "changing script permission to execute"
chmod +x /etc/init.d/startup.sh
echo "adding to defaults"
update-rc.d startup.sh defaults
