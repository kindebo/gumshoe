echo "This could take awhile.."
cat /dev/urandom > /data/local/tmp/unalloc
sleep 10
rm /data/local/tmp/unalloc
