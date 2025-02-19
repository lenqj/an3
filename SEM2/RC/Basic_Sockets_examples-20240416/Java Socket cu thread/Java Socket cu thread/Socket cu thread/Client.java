import java.io.*;
import java.net.*;

/**
 * Created by IntelliJ IDEA.
 * User: BG
 * Date: Apr 18, 2005
 * Time: 12:05:20 PM
 * To change this template use File | Settings | File Templates.
 */
public class Client {
     public static void main(String args[]) {
		Socket socket = null;
        DataOutputStream out = null;
        BufferedReader in = null;
        BufferedReader c = null;
        c = new BufferedReader(
			new InputStreamReader(System.in));

        String line = null;

		try {
			System.out.println("\nClient pornit!/Client online!");
			socket = new Socket(args[0], Integer.parseInt(args[1]));
			System.out.println("Socket info from client: " + socket);
			in = new BufferedReader(
					new InputStreamReader(socket.getInputStream()));
			out = new DataOutputStream(socket.getOutputStream());
				while(true) {
					System.out.print("\nScrie mesajul aici:/Write your message here:");
					line = c.readLine();
					out.writeBytes(line + "\n");
					out.flush();
					
					System.out.println("Trimis la server/Sent to the server:" + line);
					line= in.readLine();
					System.out.println("Receptionat de la server/Receiption from the server:" + line);
				}
		} catch (IOException e) {
		}
    }
}