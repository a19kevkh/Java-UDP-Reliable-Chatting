import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.sql.SQLOutput;
import java.util.ArrayList;

public class Server extends Thread {

    EndPoint serverEnd;
    String replyMessage;
    String name;
    InetAddress client2Address = null;
    int client2Port;
    ArrayList<String> connectedMembers = new ArrayList<String>();
    ArrayList<String> memberNames = new ArrayList<String>();

    public Server(int serverPort, String name) {
        this.name = name;
        // Create an endPoint for this program identified by serverport
        serverEnd = new EndPoint(serverPort, name);
    }

    public boolean updateArray(String arrayName, InetAddress arrayAddress, int arrayPort) {
        String newUser = arrayName + "&" + arrayAddress.toString() + "-" + String.valueOf(arrayPort);
        boolean taken = false;
        //System.out.println(newUser);
        for(int i = 0; i < connectedMembers.size(); i++){
            if(connectedMembers.get(i).contains(arrayName)){
            return true;
            }
        }
        connectedMembers.add(newUser);
        memberNames.add(arrayName);
        return false;
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

    public String getSender(DatagramPacket tempPacket) {
        String sender = "Unknown";
        String tempReceivedMessage = serverEnd.unmarshall(tempPacket.getData());
        int index = tempReceivedMessage.indexOf("-"); //finds the location of - in the array

        if (index != -1) {
            sender = tempReceivedMessage.substring(0, index); //copies the start of the message until we reach & (& is not included)
        }
        return sender;
    }

    public void broadcast(String message) {
        if (connectedMembers.size() > 0) {
            for (int i = 0; i < connectedMembers.size(); i++) {
                String tempArrayString = connectedMembers.get(i);
                int indexAND = tempArrayString.indexOf("&") + 2;
                int indexLINE = tempArrayString.indexOf("-");
                InetAddress clientAddress = null;
                try {
                    clientAddress = InetAddress.getByName(tempArrayString.substring(indexAND, indexLINE));
                } catch (UnknownHostException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    System.out.println("No server address found");
                }
                String clientPort = tempArrayString.substring(indexLINE + 1, tempArrayString.length());

                DatagramPacket replyPacket = serverEnd.makeNewPacket(message, clientAddress, Integer.parseInt(clientPort));
                serverEnd.sendPacket(replyPacket);
            }
        }
    }
    public String getReceiver(String name, String trimmedMsg, int commandLength){
        // "name-/tell user msg"
        //  senderPart={name-} commandPart={/tell} Part3={user msg}
        int commandPartEnd = name.length() + commandLength + 2;
        String part3 = trimmedMsg.substring(commandPartEnd, trimmedMsg.length());
        for(int i = 0; i < memberNames.size(); i++){
            if(part3.contains(memberNames.get(i))){
                return memberNames.get(i);
            }
        }
        return null;
    }

    public String getMessageOnly(String name, String trimmedMsg, int commandLength){
        // "name-/tell user msg"
        //  senderPart={name-} commandPart={/tell} Part3={user msg}
        int commandPartEnd = name.length() + commandLength + 2;
        String part3 = trimmedMsg.substring(commandPartEnd, trimmedMsg.length());
        String user = getReceiver(name, trimmedMsg, commandLength);
        String msg = part3.substring(user.length() + 1, part3.length());
    return msg;
    }

    public String prepareMessage(String name, String message, int index){
        int endOfCommand = name.length() + index + 1;
        String finishedMessage = name + "- " + message.substring(endOfCommand, message.length());
        System.err.println("fn= "+finishedMessage);
        return finishedMessage;
    }

    public String getMessage(String finishedMessage){
        int index = finishedMessage.indexOf("-") + 1;
        String message = finishedMessage.substring(index,finishedMessage.length());
        System.err.println("msg= "+message);
        return message;
    }

    public void run() {
        do {
            DatagramPacket receivedPacket = serverEnd.receivePacket();

            // Get the message within packet
            String receivedMessage = serverEnd.unmarshall(receivedPacket.getData());
            String receivedMessageTrim = receivedMessage.trim();
            System.err.println("rmt= "+receivedMessageTrim);
            //System.out.println(receivedMessageTrim);
            //System.out.println("Server received: " + receivedMessage);
            //byta ut getAdress och port till hårdkodad client2
            // Make a reply packet
            //DatagramPacket replyPacket = serverEnd.makeNewPacket(replyMessage, receivedPacket.getAddress(), receivedPacket.getPort());

            //DatagramPacket replyPacket = serverEnd.makeNewPacket(receivedMessageTrim, client2Address, client2Port);
            //System.out.println(getSender(receivedPacket));
            // Now send back a reply packet to client
            //serverEnd.sendPacket(replyPacket);
            // Receive a packet from client
            //updateArray("Client2",client2Address, client2Port);

            // Check whether it is a “handshake” message
            if (receivedMessageTrim.contains("/handshake")) {
                // Get client name (it is a new chat-room member!)
                boolean taken = updateArray(getSender(receivedPacket), receivedPacket.getAddress(), receivedPacket.getPort());
                if(!taken){
                    broadcast("Server- " + getSender(receivedPacket) + " joined the chat!");
                }
                else {
                    broadcast("Server- Username already taken");    //GÖR OM, skicka som pm till clienten.
                }
                continue;
            }

            // Check whether it is a “tell” message
            if (receivedMessageTrim.contains("/tell")) {
                String memberData = null;
                //String finishedMessage = null;
                int index = 6;
                String finishedMessage = prepareMessage(getSender(receivedPacket), receivedMessageTrim, index); //6= ["/tell "] + index = ["clientName"]
                String message = getMessage(finishedMessage);
                for(int i = 0; i < connectedMembers.size(); i++){
                    int indexAND = connectedMembers.get(i).indexOf("&");
                    String clientName = connectedMembers.get(i).substring(0, indexAND);
                    //System.out.println("clientName= " +clientName+" RP= " + getSender(receivedPacket) + " FM=" + finishedMessage);
                    if(message.contains(clientName)){
                        memberData = connectedMembers.get(i);
                    }
                }

                if(memberData != null){
                    int indexAND = memberData.indexOf("&") + 2;
                    int indexLINE = memberData.indexOf("-");

                    String clientAddressString = memberData.substring(indexAND,indexLINE);
                    String clientPort = memberData.substring(indexLINE + 1, memberData.length());
                    InetAddress clientAddress = null;
                    try {
                        clientAddress = InetAddress.getByName(clientAddressString);
                    } catch (UnknownHostException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        System.out.println("No server address found");
                    }
                    System.err.println(finishedMessage + ","+clientAddressString+","+clientPort);
                    DatagramPacket replyPacket = serverEnd.makeNewPacket(finishedMessage, clientAddress, Integer.parseInt(clientPort));
                    serverEnd.sendPacket(replyPacket);
                }
                else{
                    broadcast("Server- Cannot find user");
                }

                // cut away "/tell" from the message
                // trim any leading spaces from the resulting message
                // split message into “recipient” name and the message
                continue;
            }

            // Check whether it is a “list” message
            if (receivedMessageTrim.contains("/test")) {
                String user = getReceiver(getSender(receivedPacket),receivedMessageTrim,5);
                String msg = getMessageOnly(getSender(receivedPacket),receivedMessageTrim,5);
                if(user != null){
                    System.out.println("User = [" + user + "]");
                    System.out.println("Msg = [" + msg + "]");
                }
                else {
                    System.out.println("ERR");
                }
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
            String finishedMessage = prepareMessage(getSender(receivedPacket), receivedMessageTrim, 0);
            broadcast(finishedMessage);
        } while (true);


    }
}