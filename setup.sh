#!/bin/sh
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
