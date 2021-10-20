public class Driver {
    public static void main(String[] args) {
        Server server = new Server(6666,"server");
        Client client = new Client(6667,"Erik");
        Client client2 = new Client(6668,"Tedd");
        Client client3 = new Client(6669,"client3");

        client.setServerParameters("Localhost", 6666);
        //client.setRequestMessage("Request message");

        client2.setServerParameters("Localhost", 6666);
        client3.setServerParameters("Localhost", 6666);
        //client2.setRequestMessage("Request message");

        server.start();
        client3.start();
        client2.start();
        client.start();
    }
}
