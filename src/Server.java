import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.sql.SQLOutput;

public class Server extends Thread {

    EndPoint serverEnd;
    String replyMessage;
    String name;
    InetAddress client2Address = null;
    int client2Port;
    String receivedMessage = "FUCKED";

    public Server(int serverPort, String name) {
        this.name = name;
        // Create an endPoint for this program identified by serverport
        serverEnd = new EndPoint(serverPort, name);
    }

    public void setReplyMessage(String replyMessage) {

        this.replyMessage = replyMessage;

    }

    public void setClient2Address(String address, int client2Port) {
        try {
            client2Address = InetAddress.getByName(address);
        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            System.out.println("No server address found on " + address);
        }
        this.client2Port = client2Port;
    }

    public void run() {
        do {

            DatagramPacket receivedPacket = serverEnd.receivePacket();

            // Get the message within packet
            receivedMessage = serverEnd.unmarshall(receivedPacket.getData());
            String receivedMessageTrim = receivedMessage.trim();
            //System.out.println("Server received: " + receivedMessage);
            //byta ut getAdress och port till hårdkodad client2
            // Make a reply packet
            //DatagramPacket replyPacket = serverEnd.makeNewPacket(replyMessage, receivedPacket.getAddress(), receivedPacket.getPort());
            DatagramPacket replyPacket = serverEnd.makeNewPacket(receivedMessageTrim, client2Address, client2Port);
            // Now send back a reply packet to client
            serverEnd.sendPacket(replyPacket);
            // Receive a packet from client

            String x = "Unknown";
            int index = replyMessage.indexOf("-"); //finds the location of & in the array
            //System.out.println("index: " + index);
            if (index != -1)
            {
                x = replyMessage.substring(0,index); //copies the start of the message until we reach & (& is not included)
            }
/*
            String Y = "Unknown";
            int indeY = replyMessage.indexOf("/tell"); //finds the location of & in the array
            //System.out.println("index: " + index);
            if (index != -1)
            {
                Y = replyMessage.substring(0,index); //copies the start of the message until we reach & (& is not included)
            }

 */
            // Check whether it is a “handshake” message
            if (false) {

                continue;
            }

            // Check whether it is a “tell” message
            if (false) {
                // cut away "/tell" from the message
                // trim any leading spaces from the resulting message
                // split message into “recipient” name and the message
                continue;
            }

            // Check whether it is a “list” message
            if (false) {
                // Get connected member names list
                // sendPrivateMessage(namesList, "Server", getSender(receivedPacket));
                continue;
            }

            // Check whether it is a “leave” message
            if (false) {
                // (Server) broadcasts notification to members
                // broadcast(senderName + " left the chat", "Server");
                // remove sender name from chat members
                continue;
            }

            // if nothing of the above applies, it is a broadcast message
            // if senderName is a member then ...
            // broadcast(receivedMessage, getSender(receivedPacket));
            //(receivedMessage);
        } while (true);


    }
}