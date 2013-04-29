# sudo cp .s3cfg ~/.s3cfg

sudo aptitude update

# Install Java
sudo apt-get install openjdk-6-jdk
sudo apt-get install ant
export _JAVA_OPTIONS=-Xmx6g

# Install Redis
sudo apt-get install make
sudo apt-get install gcc

cd /tmp
wget http://redis.googlecode.com/files/redis-2.6.9.tar.gz
tar -zxf redis-2.6.9.tar.gz
cd redis-2.6.9
make
sudo make install

# Install S3 Cmd
wget -O- -q http://s3tools.org/repo/deb-all/stable/s3tools.key | sudo apt-key add -
sudo wget -O/etc/apt/sources.list.d/s3tools.list http://s3tools.org/repo/deb-all/stable/s3tools.list
sudo apt-get update && sudo apt-get install s3cmd

# DIR is not included in the bucket so everything is inside BUCKET_NAME/STUFF
# s3cmd sync -r DIR s3://BUCKET_NAME

# DIR is included in the bucket so it looks like BUCKET_NAME/DIR/STUFF
# s3cmd sync -r DIR/ s3://BUCKET_NAME

# s3cmd get -r s3://BUCKET_NAME/DIR .