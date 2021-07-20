import java.io.*;
import java.net.*;
// import java.util.Scanner;

// Jacob's IP so I don't have to keep looking for it: 172.19.57.194

class UDPClient {
    public static void main(String[] args) throws Exception {
        BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
        InetAddress IPAddress = null;

        DatagramSocket clientSocket = new DatagramSocket();

        System.out.println("Enter server IPAdress to connect to.");
        String ip = inFromUser.readLine();
        try {
            IPAddress = InetAddress.getByName(ip);
        } catch (UnknownHostException e) {
            System.out.println("invalid IPAddress! Please try again with a valid IP.");
        }

        byte[] sendData = new byte[1024];
        byte [] receiveData = new byte[1024];

        System.out.println("Enter an HTTP GET request.");
        String request = inFromUser.readLine();

        // String sentence = inFromUser.readLine();
        sendData = request.getBytes();

        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, 8080);

        clientSocket.send(sendPacket); 

        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
        clientSocket.receive(receivePacket);

        String modifiedSentence = new String(receivePacket.getData());

        System.out.println("FROM SERVER: " + modifiedSentence);
        clientSocket.close();
    }
}