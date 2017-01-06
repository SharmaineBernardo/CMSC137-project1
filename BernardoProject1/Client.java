/*
	SHARMAINE D. BERNARDO
	PROJECT # 1
	Client.java
	References: http://www.inetdaemon.com/tutorials/internet/tcp/3-way_handshake.shtml
							https://systembash.com/a-simple-java-udp-server-and-udp-client/
							http://www.javaworld.com/article/2077322/core-java/core-java-sockets-programming-in-java-a-tutorial.html
	
*/

import java.io.*;
import java.net.*;
import java.util.*;

public class Client {
			static int syncNum = 0, seqNum = 0, ackNum = 0, syncBit = 0, ackBit = 0, finBit = 0, windowSize = 9;
			static String stringData;
    
     public static void parseFunction(String string){
   	     stringData = string.substring(0,1);
   		   syncNum = Integer.parseInt(string.substring(1,5));
            ackNum = Integer.parseInt(string.substring(5,9));
            syncBit = Integer.parseInt(string.substring(9,10));
            ackBit = Integer.parseInt(string.substring(10,11));
            finBit = Integer.parseInt(string.substring(11,12));
            windowSize = Integer.parseInt(string.substring(12,16));
            
    
    }
    
    public static void printDetails(int syncNum, int ackNum, int syncBit, int ackBit, int finBit, int windowSize){
         System.out.println("| Synchronization Number: " + syncNum);
        		System.out.println("| Acknowledgement Number: " + ackNum);
        		System.out.println("| Synchronization Bit:    " + syncBit);
        		System.out.println("| Acknowledgement Bit:    " + ackBit);
        		System.out.println("| Finish Bit:             " + finBit);
        		System.out.println("| Window Size:            " + windowSize + "\n");
    
    }
    	public static void main (String args[]) throws Exception {
        
        int choice = 0, sentflag = 0;
        double chance = 0;
        DatagramSocket clientSocket = new DatagramSocket();
        final InetAddress IPAddress = InetAddress.getByName("localhost");
        final int portNumber = 8789;
        byte[] sendData = new byte[1024];
        byte[] receiveData = new byte[1024];
        Random random = new Random();
        int stop = 0;

        String strSynNum;
        String strAckNum;
        String strSyncBit;
        String strAckBit;
        String strFinBit;
        String strWindowSize;
        String string = "C";

       
        do {
            
            sentflag = 0;
            choice = random.nextInt(4);
            if (choice == 3) {
              chance = 0.75;
            } else if (choice == 2) {
              chance = 0.5;
            } else if (choice == 2) {
              chance = 0.25;
            } else {
              chance = 0;
            }
            if (chance == 0) {
              sentflag = 1; // packet will be sent
            } else {
              if (random.nextDouble() >= chance) {
                sentflag = 1; // packet will be sent
              }
            }
            syncBit = 1;
            syncNum = random.nextInt(100); 
            seqNum = syncNum;
        
            string = "C" + String.format("%04d", syncNum) + String.format("%04d", ackNum) + String.format("%01d", syncBit) + String.format("%01d", ackBit) + String.format("%01d", finBit) +  String.format("%04d", windowSize);
            System.out.println("┌ Initial Data");
            printDetails(syncNum, ackNum, syncBit, ackBit, finBit, windowSize);
            //System.out.println(string);
            System.out.println();
            sendData = string.getBytes();
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, portNumber);
            if (sentflag == 1) {
              System.out.println("✔ Client sent a request to the Server.");
              Thread.sleep(1000);
              clientSocket.send(sendPacket);
              Thread.sleep(1000);
            } else {
              Thread.sleep(2000);
              System.out.println("✘ Network Timeout.\n Trying to send again...\n");
              Thread.sleep(2000);
              continue;
            }

            System.out.println();
            // RECEIVING
            DatagramPacket receivePacket;
            receivePacket = new DatagramPacket(receiveData, receiveData.length);
            clientSocket.receive(receivePacket);
            String string2 = new String(receivePacket.getData());
            System.out.println("✔ Request from Server has been received.");
            System.out.println();
           parseFunction(string2);

            System.out.println("┌ Data fetched from Server");
             printDetails(syncNum, ackNum, syncBit, ackBit, finBit, windowSize);

            syncBit = 0;
            ackBit = 1;
            int temp = syncNum;
            syncNum = ackNum;
            ackNum = temp + 1;
          
             string = stringData + String.format("%04d", syncNum) + String.format("%04d", ackNum) + String.format("%01d", syncBit) + String.format("%01d", ackBit) + String.format("%01d", finBit) +  String.format("%04d", windowSize);
            sendData = string.getBytes();
            sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, portNumber);
            System.out.println("Sending request to sender...");
            System.out.println();
            clientSocket.send(sendPacket);
            syncNum = 0;
            seqNum = 0;
    		ackNum = 0;

    		syncBit = 0;
    		ackBit = 0;		
    		finBit = 0;
            stop = random.nextInt(20);
            //stop = 1;
            windowSize = random.nextInt(101);
            if (stop < 3) {
                string = "D";
            }

        } while (string != "D");

        Thread.sleep(1000);
        finBit = 1;
        int origfinbit = 1;
        syncNum = random.nextInt(100);
   
         string = "D" + String.format("%04d", syncNum) + String.format("%04d", ackNum) + String.format("%01d", syncBit) + String.format("%01d", ackBit) + String.format("%01d", finBit) +  String.format("%04d", windowSize);
        System.out.println("Sending signal for disconnection...");
         printDetails(syncNum, ackNum, syncBit, ackBit, finBit, windowSize);
       
        System.out.println();
        sendData = string.getBytes();
        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, portNumber);
        clientSocket.send(sendPacket);

        DatagramPacket receivePacket;
        receivePacket = new DatagramPacket(receiveData, receiveData.length);
        clientSocket.receive(receivePacket);
        String string2 = new String(receivePacket.getData());
            parseFunction(string2);

        System.out.println("✘ A disconnnection signal has been received.");
       printDetails(syncNum, ackNum, syncBit, ackBit, finBit, windowSize);

        receivePacket = new DatagramPacket(receiveData, receiveData.length);
        clientSocket.receive(receivePacket);
        string2 = new String(receivePacket.getData());
            parseFunction(string2);

        System.out.println("Server is now ready to close...");
  printDetails(syncNum, ackNum, syncBit, ackBit, finBit, windowSize);
        System.out.println();

        //int temp = finBit;
        ackNum = syncNum + 1;
        //finBit = origfinbit;

         string = "D" + String.format("%04d", syncNum) + String.format("%04d", ackNum) + String.format("%01d", syncBit) + String.format("%01d", ackBit) + String.format("%01d", finBit) +  String.format("%04d", windowSize);
        System.out.println("Client is now ready to close...");
       
 printDetails(syncNum, ackNum, syncBit, ackBit, finBit, windowSize);
       
        System.out.println();
        sendData = string.getBytes();
        sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, portNumber);
        clientSocket.send(sendPacket);

        Thread.sleep(10000);
        System.out.println("Connection has been closed.");
    }

}
