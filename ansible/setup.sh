#!/bin/sh
if [[ "$ISOBASEDIR" == "" ]]
then
    echo "not set ISOBASEDIR"
    exit 1
fi
ssh root@dhcp-server hostname
rc=$?
if [[ "$rc" != "0" ]]
then
    exit 1
fi
scp -p config root@dhcp-server:/etc/selinux/config
result=$(ssh root@dhcp-server getenforce)
echo $result
if [[ "Disabled" != "$result" ]]
then
    ssh root@dhcp-server reboot
fi

hdiutil mount ${ISOBASEDIR}/rhel-server-6.5-x86_64-dvd.iso 
