import java.io.*;
import java.net.*;
import java.lang.Math;
import java.util.Random;
import java.util.ArrayList;

/*
    Authors: Colin Vijvere
             Chris Acosta
             Jacob Badolato
    Class: 
             COMP4320
             Dr. Alvin Lim
*/

class UDPClient {
    public static void main(String[] args) throws Exception {
       
        //variables
        byte[] sendData = new byte[1024];
        byte [] receiveData = new byte[1024];
        String request = "";
        String[] checkup;
        String modifiedSentence = "";

        //create input stream
        BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
        InetAddress IPAddress = null;

        //create client socket
        DatagramSocket clientSocket = new DatagramSocket();


        //Promt user for IP address of the server
        System.out.println("Enter server IPAdress to connect to.");
        String ip = inFromUser.readLine();
        try {
            //Translate hostname to IP address using DNS
            IPAddress = InetAddress.getByName(ip);
        } catch (UnknownHostException e) {
            System.out.println("invalid IPAddress! Please try again with a valid IP.");
        }


        //prompt user for the gremlin probability
        System.out.println("Enter your gRemLin probability mUaHaHahhaAHAHAahaa: ");
        double gremlinProb = Double.parseDouble(inFromUser.readLine());


       //prompt user for the HTTP request
        do {
        System.out.println("Enter an HTTP GET request.");
        request = inFromUser.readLine();
        checkup = request.split(" ");
        } while (checkup.length != 3);

        //convert the string into a byte array
        sendData = request.getBytes();


        //create a datagram with data to send, length, IP addr, port
        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, 8080);

        //send datagram to the server (this is the HTTP request)
        clientSocket.send(sendPacket); 


        //Read the datagram from the server till nullbyte packet is received
        boolean finished =false;
        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
        int count = 0;
        ArrayList<Integer> corruptedPackets = new ArrayList<>();

        while(finished == false) {
        clientSocket.receive(receivePacket);
        if (receivePacket.getData()[0] == (byte) '\u0000')
            break;
        

        if (count!=0) {
        modifiedSentence = new String(receivePacket.getData());
        System.out.print(modifiedSentence);
        }

        if (count > 1) {
            byte[] uncorrupted = receivePacket.getData();
            int uncorruptedChecksum = calcChecksum(uncorrupted);
            // GREMLIN MUAHAHAHHHA
            byte[] corrupted = Gremlin(gremlinProb, uncorrupted);
            int corruptedCheckSum = calcChecksum(corrupted);

            if(corruptedCheckSum != uncorruptedChecksum) {
                int num = count - 2;
                corruptedPackets.add(num);
            }

        }

        count++;
    } 

        System.out.println("\n-----------------------------------\nAll Packets Received\n-----------------------------------");
        if (corruptedPackets.isEmpty()) {
            System.out.println("No packets were corrupted");
        } else {
            System.out.println("The following packets were corrupted: \n" + corruptedPackets);
        }
        System.out.println("-----------------------------------");


        

        
        clientSocket.close();
    }


    //gremlin function to corrupt bytes in packets
    public static byte[] Gremlin(double p, byte[] pack) {
        if (Math.random() < p) {
            double x = Math.random();
            //1 byte corrupted
            if (x <= 0.5) {
                Random rand = new Random();
                int z = rand.nextInt(1024);
                pack[z] /= 2;
            }
            //2 bytes corrupted
            else if (x <= 0.8) {
                Random rand = new Random();
                int z = rand.nextInt(1024);
                int y = rand.nextInt(1024);
                if (x == y) {
                    y = rand.nextInt(1024);
                }
                pack[z] /= 2;
                pack[y] /= 2;
            }
            //3 bytes corrupted
            else {
                Random rand = new Random();
                int z = rand.nextInt(1024);
                int y = rand.nextInt(1024);
                int q = rand.nextInt(1024);
                if (x == y || x == q || y == q) {
                    y = rand.nextInt(1024);
                    q = rand.nextInt(1024);
                }
                pack[z] /= 2;
                pack[y] /= 2;
                pack[q] /= 2;
            }
        }
        return pack;
    }



    /*Calculates the checksum*/
    public static int calcChecksum(byte[] data) {
        int calc_checksum = 0;
        for (int i = 0; i < data.length; i++) {
            calc_checksum += data[i];
        }
        return calc_checksum;
    }

}