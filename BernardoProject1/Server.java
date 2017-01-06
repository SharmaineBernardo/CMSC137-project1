/*
	SHARMAINE D. BERNARDO
	PROJECT # 1
	Server.java
	References: http://www.inetdaemon.com/tutorials/internet/tcp/3-way_handshake.shtml
							https://systembash.com/a-simple-java-udp-server-and-udp-client/
							http://www.javaworld.com/article/2077322/core-java/core-java-sockets-programming-in-java-a-tutorial.html
	
*/


import java.io.*;
import java.net.*;
import java.util.*;

public class Server {
		static int syncNum = 0, seqNum = 0, ackNum = 0, syncBit = 0, ackBit = 0, finBit = 0, windowSize = 9;
		static String stringData;
    
    public static void main (String args[]) throws Exception {
				 
				DatagramSocket serverSocket = new DatagramSocket(8789);
        DatagramPacket receivePacket;

        final int portNumber = 8789;
        byte[] sendData = new byte[1024];
        byte[] receiveData = new byte[1024];
        Random random = new Random();

        String string;
        System.out.println("Waiting for connection...\n");
        // start of the running server
       
        while(true) {
        
            syncNum = random.nextInt(100); 
            seqNum = syncNum;
            receivePacket = new DatagramPacket(receiveData, receiveData.length);
            serverSocket.receive(receivePacket);
            string = new String(receivePacket.getData());
            parseFunction(string);
          
            //IF THERE IS DISCONNECTION SIGNAL
            if (stringData.equals("D")) {
                break;
            }
           
           //SINCE THERE IS NO CONNECTION
            System.out.println("┌ Data fetched from Client:");
          	printDetails(syncNum, ackNum, syncBit, ackBit, finBit, windowSize, stringData);
            ackNum = syncNum + 1;
            syncNum = seqNum;
            ackBit = 1;
            syncBit = 1;
        
            string = stringData + String.format("%04d", syncNum) + String.format("%04d", ackNum) + String.format("%01d", syncBit) + String.format("%01d", ackBit) + String.format("%01d", finBit) +  String.format("%04d", windowSize);
            InetAddress IPAddress = receivePacket.getAddress();
            int port = receivePacket.getPort();
            System.out.println("Sending Request to Client...");
            System.out.println();
            sendData = string.getBytes();
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
            serverSocket.send(sendPacket);


            receivePacket = new DatagramPacket(receiveData, receiveData.length);
            serverSocket.receive(receivePacket);
            String string2 = new String(receivePacket.getData());
      			 parseFunction(string2);
          
           
          
         printDetails(syncNum, ackNum, syncBit, ackBit, finBit, windowSize, stringData);
            System.out.println("✔ A connection has been made.");
            System.out.println();

        }
       //CLOSING THE CONNECTION HERE
        System.out.println("✘ A disconnnection signal has been received.");
        printDetails(syncNum, ackNum, syncBit, ackBit, finBit, windowSize, stringData);
        int origsynno = random.nextInt(100);
        ackNum = syncNum + 1;
        syncNum = 0;
        finBit = 0;

       string = stringData + String.format("%04d", syncNum) + String.format("%04d", ackNum) + String.format("%01d", syncBit) + String.format("%01d", ackBit) + String.format("%01d", finBit) +  String.format("%04d", windowSize);
        InetAddress IPAddress = receivePacket.getAddress();
        int port = receivePacket.getPort();
        System.out.println("Sending disconnection signal to the Client...");
        System.out.println();
        sendData = string.getBytes();
        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
        serverSocket.send(sendPacket);

        finBit = 1;
        syncNum = origsynno;
        //Thread.sleep(3000);
        //finBit = origfinbit;
    string = stringData + String.format("%04d", syncNum) + String.format("%04d", ackNum) + String.format("%01d", syncBit) + String.format("%01d", ackBit) + String.format("%01d", finBit) +  String.format("%04d", windowSize);
     IPAddress = receivePacket.getAddress();
        port = receivePacket.getPort();
        System.out.println("Server is now ready to close...\n");
        
        sendData = string.getBytes();
        sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
        serverSocket.send(sendPacket);

        receivePacket = new DatagramPacket(receiveData, receiveData.length);
        serverSocket.receive(receivePacket);
        string = new String(receivePacket.getData());
       
        System.out.println();
         parseFunction(string);

        System.out.println("Final data from Client:");
        printDetails(syncNum, ackNum, syncBit, ackBit, finBit, windowSize, stringData);


        Thread.sleep(1000);

    }
    
    public static void parseFunction(String string){
    	stringData = string.substring(0,1);
    	  syncNum = Integer.parseInt(string.substring(1,5));
            ackNum = Integer.parseInt(string.substring(5,9));
            syncBit = Integer.parseInt(string.substring(9,10));
            ackBit = Integer.parseInt(string.substring(10,11));
            finBit = Integer.parseInt(string.substring(11,12));
            windowSize = Integer.parseInt(string.substring(12,16));
            
    
    }
    
    public static void printDetails(int syncNum, int ackNum, int syncBit, int ackBit, int finBit, int windowSize, String stringData){
   
            
                    System.out.println("| Synchronization Number: " + syncNum);
        		System.out.println("| Acknowledgement Number: " + ackNum);
        		System.out.println("| Synchronization Bit:    " + syncBit);
        		System.out.println("| Acknowledgement Bit:    " + ackBit);
        		System.out.println("| Finish Bit:             " + finBit);
        		System.out.println("| Window Size:            " + windowSize + "\n");
    
    }
}
