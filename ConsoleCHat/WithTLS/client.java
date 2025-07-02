import javax.net.ssl.*;
import java.io.*;
import java.security.KeyStore;
import java.util.Scanner;

public class client {
    public static void main(String[] args) throws Exception {
        // Load truststore (change password and path as needed)
        char[] passphrase = "password".toCharArray();
        KeyStore ts = KeyStore.getInstance("JKS");
        ts.load(new FileInputStream("clienttruststore.jks"), passphrase);

        TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
        tmf.init(ts);

        SSLContext sc = SSLContext.getInstance("TLS");
        sc.init(null, tmf.getTrustManagers(), null);

        String serverAddress = "127.0.0.1";
        int port = 12346;
        Scanner scanner = null;
        if (args.length > 0) {
            serverAddress = args[0];
        } else {
            scanner = new Scanner(System.in);
            System.out.print("Enter server address (default 127.0.0.1): ");
            String inputAddr = scanner.nextLine();
            if (!inputAddr.trim().isEmpty()) {
                serverAddress = inputAddr.trim();
            }
        }
        if (args.length > 1) {
            try {
                port = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                System.out.println("Invalid port, using default 12346");
            }
        } else {
            if (scanner == null) scanner = new Scanner(System.in);
            System.out.print("Enter server port (default 12346): ");
            String inputPort = scanner.nextLine();
            if (!inputPort.trim().isEmpty()) {
                try {
                    port = Integer.parseInt(inputPort.trim());
                } catch (NumberFormatException e) {
                    System.out.println("Invalid port, using default 12346");
                }
            }
        }
        // KHÔNG đóng scanner để tránh đóng System.in
        // if (scanner != null) scanner.close();

        SSLSocketFactory sf = sc.getSocketFactory();
        SSLSocket socket = (SSLSocket) sf.createSocket(serverAddress, port);

        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Connected to TLS server " + serverAddress + ":" + port + ". Type messages:");
        try {
            String line;
            while ((line = userInput.readLine()) != null) {
                out.write(line + "\n");
                out.flush();
                String response = in.readLine();
                if (response == null) {
                    System.out.println("Server closed the connection.");
                    break;
                }
                System.out.println("Server: " + response);
            }
        } catch (IOException e) {
            // ignore
        }
        socket.close();
    }
}
