import java.io.*;
import java.net.*;

class UDPServer {
    public static void main(String[] args) throws Exception {
        DatagramSocket serverSocket = new DatagramSocket(8080);

        byte[] receiveData = new byte[1024];
        byte[] sendData = new byte[1024];

        while(true) {
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            serverSocket.receive(receivePacket);

            String sentence = new String(receivePacket.getData());
            System.out.println("Data received is: " + sentence);

            InetAddress IPAddress = receivePacket.getAddress();
            int port = receivePacket.getPort();

            // check if request is valid
            isValidRequest(sentence);

            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port); 
            serverSocket.send(sendPacket);

            serverSocket.close();

        }
    }

    public static Boolean isValidRequest(String request) {
        String[] r = request.split(" ");
        if (r.length != 3) {
            return false;
        } else 
            return true;
    }

    
}