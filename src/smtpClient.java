
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by lmgr on 19.09.2016.
 */
public class smtpClient {

    private static Socket socket;
    private static DataOutputStream OutputStream;
    private static DataInputStream InputStream;

    public static void main(String[] args) throws IOException {

        // HUSK Å LEGGE TIL DEBUG OPTIONS DIN JÆVLA DRITTUNGE

        String hostname = "";
        int isDebugging;

        if(args.length == 0) {
            System.err.println("ERROR. You must pass hostname and port parameters.");
            System.exit(0);
        } else {
            hostname = args[0];
            isDebugging = Integer.parseInt(args[1]);
        }

        // Try to open socket
        try {
            socket = new Socket(hostname, 25);
            System.out.println("Connection to host established.");
        } catch (UnknownHostException e) {
            System.err.println("Host not found.");
        } catch (IOException e) {
            System.err.println(e);
        }

        OutputStream = new DataOutputStream(socket.getOutputStream());
        InputStream = new DataInputStream(socket.getInputStream());
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));


        try {
            OutputStream.writeBytes("HELO " + hostname + "\n");
            readReplyFromServer();

            System.out.print("Sender: ");
            String senderAddress = reader.readLine();
            OutputStream.writeBytes("MAIL From: " + senderAddress + "\n");
            readReplyFromServer();

            System.out.print("Recipient: ");
            String recipientAddress = reader.readLine();
            OutputStream.writeBytes("RCPT To: " + recipientAddress + "\n");
            readReplyFromServer();

            OutputStream.writeBytes("DATA\n");
            readReplyFromServer();

            OutputStream.writeBytes("From: " + senderAddress + "\n");
            System.out.print("Subject: ");
            String subject = reader.readLine();
            OutputStream.writeBytes("Subject: " + subject + "\n");

            System.out.print("Message: ");
            String message = reader.readLine();
            OutputStream.writeBytes(message + "\n");
            OutputStream.writeBytes("\n.\n");
            readReplyFromServer();

            OutputStream.writeBytes("QUIT");
            readReplyFromServer();

            OutputStream.close();
            InputStream.close();
            socket.close();
            System.exit(0);

        } catch (UnknownHostException e) {
            System.err.println(e);
        } catch (IOException e) {
            System.err.println(e);
        }
    }

    public static void readReplyFromServer() throws IOException {
        System.out.println("Message from server: " + InputStream.readLine());
    }
}