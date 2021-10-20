import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;

public class EndPoint {
    InetAddress address; // this endpoint address
    int portNumber; // the program port number used through this endpoint
    DatagramSocket socket; // the communication socket of this endpoint
    String name;
    public EndPoint(int m_portNumber, String name) {
        this.name = name;
        portNumber = m_portNumber;

        try { // only this exception is handled for illustration!
            address = InetAddress.getLocalHost();
            socket = new DatagramSocket(portNumber);
        } catch (Exception e) {
            System.err.println("Error creating endPoint!");
        }
        //System.out.println("port: " + m_portNumber +" address: " + address + " socket: " + socket);
    }

    public DatagramPacket makeNewPacket(String message, InetAddress destinationAddress, int portNumber) {
        DatagramPacket packet = null; // to represent packets
        byte[] buffer; // to represents payload messages within packets

        // Marshall message
        //String combined = name + "- " + message;//Buffer is "extended" to not only display the message, but also the senders name (client or server).
        //This is not necessary, as the message could be combined with the name already in the Client class, but ->
        //I believe this is a more reasonable approach to handle the names.
        String combined = message;

        buffer = combined.getBytes();

        // Put message in a new packet
        packet = new DatagramPacket(buffer, buffer.length, destinationAddress, portNumber);
        return (packet);
    }

    public void sendPacket(DatagramPacket packet) {

        // Send message via socket
        try {
            socket.send(packet);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            System.err.println("Failed to send packet.");
            e.printStackTrace();
        }
        //System.out.println(name + ": Sending a packet");
    }

    public DatagramPacket receivePacket() {

        // Create a new DatagramPacket object to buffer the received payload
        byte[] buffer = new byte[65535];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

        // Wait to receive request packet
        //System.out.println(name+": Waiting for a packet");
        try {
            socket.receive(packet);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            System.err.println("No packet recieved.");
            e.printStackTrace();
        }

        return (packet);
    }

    public String unmarshall(byte[] payload) {

        // *** Left as an exercise in the assignment ***
        //Trim

        String s = new String(payload, StandardCharsets.UTF_8);

        return s;
    }

    public void SendMessage(String message, InetAddress destinationAddress, int portNumber)
    {
        DatagramPacket newPacket = makeNewPacket(message, destinationAddress, portNumber);
        sendPacket(newPacket);
    }


}