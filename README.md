#TFTP Server and Client

This is an incomplete TFTP server and client.  I have chosen to implement the sending and retrieving of files as this seems to be the minimal viable product; however error handling is minimal.  Error handling and retransmission of blocks are not implemented.

#Server

I have checked in a built jar and some batch files to help with running of the client and server.  The server reads a properties file "tftpConfig.properties" in which the listening port and the write directory can be set.

To run, launch the server.bat  file in the target directory.

#Client

The client is a command line program that takes four arguments.
1. host - ipaddress of the server
2. GET or PUT - this is the operation that will be made upon the file.  
 	PUT will send a file to the server.
  	GET will retrieve a file from the server.
3. fileDir - is the directory in which the file to be sent resides or the directory that the file will be created.
4. filename - is the file name of the file to be sent or received.

To run, launch the client.bat file in the target directory.