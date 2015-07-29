1) Create a Virtual Machine
O.S: Ubuntu Linux server 14.04 LTS 64bit
features: memory 1Gb; hard disk 50Gb (but 20Gb could be enough)
2) download and copy Tomcat Server in /opt
version: 7.0.56
3) install Apache2 from apt-get
4) install MongoDb on a Linux VM (it could be this one). Create a database called "mosaic" and a collection called "process" (client softwares like Mongo VUE can facilitate these tasks)
5) download MosaicMiner from Github. Under WebContent-->WEB-INF/classes, modify the config.properties file as following
mongodb_server= {Mongodb VM IP}
mongodb_server_port= 27017 (if you use Mongodb standard port)
6) Deploy MosaicMiner.war on Tomcat server
In MosaicMiner-->WebContent-->extra-file you can find some files you will need.
7) In mosaic_process_json_test.txt, you can find a json. This is a test MongoDB document that you have to insert in the db named mosaic and collection named process
8) Modify mosaicminer_testpage.html: insert your VM IP and port in FORM tag {MOSAIC_MINER_SERVER_IP:PORT}.Copy the mosaicminer_testpage.html in var/www/html
9) In your VM in /usr/lib/cgi-bin/  copy these files: main_find_history_opt_new_json_inout_file and run_matlab.sh
10) Give these files 777 permissions (use this command: sudo chmod 777 -R /usr/lib/cgi-bin/*)
11) Download Matlab Compiler Runtime (MCR)
version: MRC 2013a 64Bit for Linux (you can find here: http://it.mathworks.com/products/compiler/mcr/)
12) these are some operations that have to be done in order to install properly the MRC. The operations are listed below:

cd bin/glnxa64
rm libstdc++.so.6
ln -s libstdc++.so.6.0.13 libstdc++.so.6
sudo ./install -mode silent -agreeToLicense yes
sudo apt-get install libxt6
sudo apt-get install libxmu6

13) It could be also necessary give 777 permissions to /tmp dir

TEST the installation:
1) Go to: http://{your MM_VM IP}/mosaicminer_testpage.html
2) fill the 3 fields of the form with 0. In the last field, put the _id field of the MongoDb document you inserted before (it should be: 5541e0f2f2a737b5a6061c26)
3) Click Go! and check the logs in your VM with che command: tail -f /opt/apache-tomcat-7.0.56/logs/catalina.out to monitor if some errors occurs
4) to check if the matlab procedure worked in the right way, go to /tmp dir: you should find 3 files: %d.txt %d_mat.txt %d_fine.txt

REMEMBER to modify properly the mosaic_dashboard.properties file in MosaicDashboard project
process_url=http://{your MM_VM IP}/MosaicMiner/mosaic/matlabservice/runprocessminer
mongodb_collection=process
mongodb_db1=mosaic
mongodb_server={your MongoDB VM IP}
mongodb_server_port=27017