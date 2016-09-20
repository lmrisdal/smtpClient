import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by lmgr on 19.09.2016.
 */
public class smtpClient {

    private static Socket smtpSocket;
    private static DataOutputStream os;
    private static DataInputStream is;

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
            smtpSocket = new Socket(hostname, 25);
            System.out.println("Connection to host established.");
        } catch (UnknownHostException e) {
            System.err.println("Host not found.");
        } catch (IOException e) {
            System.err.println(e);
        }

        os = new DataOutputStream(smtpSocket.getOutputStream());
        is = new DataInputStream(smtpSocket.getInputStream());
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));


        try {
            os.writeBytes("HELO " + hostname + "\n");
            readReplyFromServer();

            System.out.print("Sender: ");
            String senderAddress = reader.readLine();
            os.writeBytes("MAIL From: " + senderAddress + "\n");
            readReplyFromServer();

            System.out.print("Recipient: ");
            String recipientAddress = reader.readLine();
            os.writeBytes("RCPT To: " + recipientAddress + "\n");
            readReplyFromServer();

            os.writeBytes("DATA\n");
            readReplyFromServer();

            os.writeBytes("From: " + senderAddress + "\n");
            System.out.print("Subject: ");
            String subject = reader.readLine();
            os.writeBytes("Subject: " + subject + "\n");

            System.out.print("Message: ");
            String message = reader.readLine();
            os.writeBytes(message + "\n");
            os.writeBytes("\n.\n");
            readReplyFromServer();

            os.writeBytes("QUIT");
            readReplyFromServer();

            // clean up:
            // close the output stream
            // close the input stream
            // close the socket
            os.close();
            is.close();
            smtpSocket.close();
            System.exit(0);

        } catch (UnknownHostException e) {
            System.err.println("Trying to connect to unknown host: " + e);
        } catch (IOException e) {
            System.err.println("IOException:  " + e);
        }
        //}
    }

    public static void readReplyFromServer() throws IOException {
        System.out.println("Message from server: " + is.readLine());
    }
}