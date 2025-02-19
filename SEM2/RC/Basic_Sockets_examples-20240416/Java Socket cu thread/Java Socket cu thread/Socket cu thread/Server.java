import java.io.*;
import java.net.ServerSocket;
import java.net.*;
/*
 * Created by IntelliJ IDEA.
 * User: BG
 * Date: Apr 18, 2005
 * Time: 12:23:20 PM
 * To change this template use File | Settings | File Templates.
 */

public class Server {
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = null;
        boolean listening = true;

        try {
            serverSocket = new ServerSocket(Integer.parseInt(args[0]));
        } catch (IOException e) {
            System.err.println("Eroare pe port:/Error on the port:" + Integer.parseInt(args[0]));
            System.exit(-1);
        }

        while (listening)

	    new Worker(serverSocket.accept()).start();
        serverSocket.close();

    }
}
class Worker extends Thread {
    private Socket socket = null;

    DataOutputStream out = null;
    BufferedReader in = null;
    boolean flag;
    String line;

    public Worker(Socket socket) {
	super("Worker");
	this.socket = socket;
	System.out.println("\nSocket info from server: " + socket);
    }

    public void run() {

	try {
	    in = new BufferedReader(
                              new InputStreamReader(socket.getInputStream()));
        out = new DataOutputStream(socket.getOutputStream());
        System.out.println("Client acceptat!/Accepted client!\n");
        flag = true;
                    while (flag) {
                       line = in.readLine();
                       System.out.println("Primit de la client/Received from the client:" + line + "\n");
                       flag = (line.compareTo("bye") != 0);
                       out.writeBytes(line + "\n");
                    }

                    out.close();
                    in.close();
                    socket.close();
             } catch (IOException e) {
	    e.printStackTrace();
	}
    }
}