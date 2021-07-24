import java.io.*;
import java.net.*;
import java.util.*;
import java.nio.file.*;

class UDPServer {
    public static void main(String[] args) throws Exception {
        //Create datagram socket at port 8080
        DatagramSocket serverSocket = new DatagramSocket(8080);

        byte[] receiveData = new byte[1024];
        String nullByte = "\0";
        String errorCode = "";

        //TODO: have the server send the checksum back to client before gremlin function damages any packets

        while(true) {
            //Receiving datagram from client
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            serverSocket.receive(receivePacket);

            //Get IP address and port #
           InetAddress IPAddress = receivePacket.getAddress();
           int port = receivePacket.getPort();

            String sentence = new String(receivePacket.getData());
            System.out.println("Data received is: " + sentence);

            //get the file name out of the sentence above
            String[] arr = sentence.split(" ");
            String fileNameRequested = arr[1];

            //create bufferedReader to read in the file 
            //1 character equals 2 bytes.
            try {
            BufferedReader fileIn = new BufferedReader(new FileReader(fileNameRequested));
            StringBuilder fileData = new StringBuilder();
            String temp = fileIn.readLine();
            System.out.println("line: " + temp);
            while (temp != null) {
                System.out.println(temp);
                fileData.append(temp);
                temp = fileIn.readLine();
            }
             fileIn.close();
        } catch(FileNotFoundException e) {
                errorCode = "404 - File Not Found";
            }
            

            if (errorCode.isEmpty()) {
                errorCode = "200 - Document follows";
            }

            //converting file to byte array
            byte[] bytes = Files.readAllBytes(Paths.get(fileNameRequested));
            
            //Sending HTTPHeader:
            String HTTPHeader = "\n" + arr[2] + " " + errorCode + "\r\nContent-Type: text/plain\r\nContent-Length: " + bytes.length + " bytes\r\n";
           
            byte[] headerPacket = HTTPHeader.getBytes();
            //create a datagram with data to send, length, IP addr, port
            DatagramPacket sendedPacket = new DatagramPacket(headerPacket, headerPacket.length, IPAddress, port);
            //send datagram to the server
            serverSocket.send(sendedPacket); 

            boolean finished = false;
            int start = 0;
            int end = 1024;
            if (end > headerPacket.length) {
                end = headerPacket.length;
            }

            while (finished == false) {
                byte[] temp = new byte[1024];
                int dataIndex = 0;
                for(int i = start; i < end; i++) {
                
                    temp[dataIndex] = headerPacket[i];
                    dataIndex++;
                    if(i == headerPacket.length - 1) {
                        finished = true;
                    }
                }
                start = end;
                if (end + 1024 < headerPacket.length) {
                    end+= 1024;
                } else {
                    end = headerPacket.length;
                }
                 //create a datagram with data to send, length, IP addr, port
            DatagramPacket sendPacket = new DatagramPacket(temp, temp.length, IPAddress, port);
            //send datagram to the server
            serverSocket.send(sendPacket); 
            }





            finished = false;
            start = 0;
            end = 1024;
            if (end > bytes.length) {
                end = bytes.length;
            }

            while (finished == false) {
                byte[] data = new byte[1024];
                int dataIndex = 0;
                for(int i = start; i < end; i++) {
                
                    data[dataIndex] = bytes[i];
                    dataIndex++;
                    if(i == bytes.length - 1) {
                        finished = true;
                    }
                }
                start = end;
                if (end + 1024 < bytes.length) {
                    end+= 1024;
                } else {
                    end = bytes.length;
                }

            //create a datagram with data to send, length, IP addr, port
            DatagramPacket sendPacket = new DatagramPacket(data, data.length, IPAddress, port);
            //send datagram to the server
            serverSocket.send(sendPacket); 
            }

            //Sending the last nullbyte packet
            byte[] nullBytePacket = nullByte.getBytes();
            DatagramPacket sendPacket = new DatagramPacket(nullBytePacket, nullBytePacket.length, IPAddress, port);
            serverSocket.send(sendPacket); 
            System.out.println("----------------------------------\nnullByte packet sent.\n----------------------------------");
        }
    }
}