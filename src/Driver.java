public class Driver {
    public static void main(String[] args) {
        Server server = new Server(6666,"server");
        Client client = new Client(6667,"client1");
        Client client2 = new Client(6668,"client2");

        client.setServerParameters("Localhost", 6666);
        //client.setRequestMessage("Request message");

        client2.setServerParameters("Localhost", 6666);
        server.setClient2Address("Localhost", 6668);
        //client2.setRequestMessage("Request message");

        server.setReplyMessage("Reply message");


        server.start();

        client2.start();
        client.start();
    }
}
