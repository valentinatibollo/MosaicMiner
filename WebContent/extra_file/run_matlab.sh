#!/bin/sh
# script for execution of deployed applications
#
# Sets up the MCR environment for the current $ARCH and executes 
# the specified command.
#



exe_name=$0 
exe_dir=`dirname "$0"`


now=$(date +%s%N)
  
 MCRROOT="/usr/local/MATLAB/MATLAB_Compiler_Runtime/v81/";
 
  LD_LIBRARY_PATH=.:${MCRROOT}/runtime/glnxa64;
  LD_LIBRARY_PATH=${LD_LIBRARY_PATH}:${MCRROOT}/bin/glnxa64;
  LD_LIBRARY_PATH=${LD_LIBRARY_PATH}:${MCRROOT}/sys/os/glnxa64;
	MCRJRE=${MCRROOT}/sys/java/jre/glnxa64/jre/lib/amd64;
	LD_LIBRARY_PATH=${LD_LIBRARY_PATH}:${MCRJRE}/native_threads; 
	LD_LIBRARY_PATH=${LD_LIBRARY_PATH}:${MCRJRE}/server;
	LD_LIBRARY_PATH=${LD_LIBRARY_PATH}:${MCRJRE}/client;
	LD_LIBRARY_PATH=${LD_LIBRARY_PATH}:${MCRJRE};  
  XAPPLRESDIR=${MCRROOT}/X11/app-defaults;
  export LD_LIBRARY_PATH;
  export XAPPLRESDIR;

 "${exe_dir}"/main_find_history_opt_new_json_inout_file $1 $2 $3 $4;
now=$(date +%s%N)

echo "fine" > /tmp/$now.txt
exit

