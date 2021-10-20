import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Random;
import java.util.TimerTask;
import java.util.Timer;

public class Client extends Thread implements ActionListener {
    ChatGUI chatGUI;
    EndPoint clientEnd;
    InetAddress serverAddress = null;
    int serverPortNumber;
    String requestMessage;
    String clientName; //ta bort sen
    String name;
    int NETWORK_FAILURE_RATE;
    int secondsPassed;
    boolean gotMessage;
    // Start up GUI (runs in its own thread)

    public Client(int clientPortNumber, String name) {
        this.name = name;
        // Create an endPoint on this computer to this
        // program identified by the provided port
        clientEnd = new EndPoint(clientPortNumber, name);
        //chatGUI = new ChatGUI(this,name);
    }

    // Client parameters include server references for processing transmissions
    public void setServerParameters(String serverAddressString, int serverPortNumber) {

        try {
            serverAddress = InetAddress.getByName(serverAddressString);
        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            System.out.println("No server address found on " + serverAddressString);
        }
        this.serverPortNumber = serverPortNumber;
    }

    public boolean failed(int rate){
        double r = (Math.random() * ((100 - 0) + 1)) + 0;
        if(r < rate){
            return true;
        }
        else{
            return false;
        }
    }

    public void run() {
        chatGUI = new ChatGUI(this,name);

        // Receive a reply from server
        while(true){
        DatagramPacket replyPacket = clientEnd.receivePacket();
        // Get the message within packet
        String replyMessage = clientEnd.unmarshall(replyPacket.getData());
        //System.out.println(name + " received: " + replyMessage);
        chatGUI.displayMessage(replyMessage);
        gotMessage = true;
        //String replyMessageTrim = replyMessage.trim();
        //System.err.println(replyMessageTrim+ ". " +name+" stop: "+System.nanoTime());
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // There is only one event coming out from the GUI and that’s
        // the carriage return in the text input field, which indicates the
        // message/command in the chat input field to be sent to the server
        DatagramPacket messagePacket;

        // get the text typed in input field, using ChatGUI utility method
        String message = chatGUI.getInput();

        // add sender name to message
        message = name + "-" + message;

        // create packet to carry the message, assuming any message fits
        // a packet size
        messagePacket = clientEnd.makeNewPacket(message, serverAddress, serverPortNumber);

        // send the message
        if(failed(30)){
            System.err.println("FAILED");
            gotMessage = false;
        }
        else{
            clientEnd.sendPacket(messagePacket);
            System.err.println("SUCCESS");
            gotMessage = true;
        }

        secondsPassed = 0;
        Timer myTimer = new Timer();
        TimerTask task = new TimerTask(){
            public void run(){
                secondsPassed++;
                System.out.println(secondsPassed);
            }
        };
        myTimer.schedule(task,0,1000);
        if(secondsPassed > 1 && gotMessage == false){
            System.err.println("Oops! message not sent: " + message);
            myTimer.cancel();
            task.cancel();
        }
        else if(secondsPassed > 1 && gotMessage == true){
            myTimer.purge();
            task.cancel();
        }

        //System.err.println(name+ " start: "+System.nanoTime());

        // clear the GUI input field, using a utility function of ChatGUI
        chatGUI.clearInput();
        //chatGUI.displayMessage(message);    //Ta bort sen
    }
}