import javax.net.ssl.*;
import java.io.*;
import java.security.KeyStore;
import java.util.concurrent.*;
import java.util.Scanner;

public class server {
    public static void main(String[] args) throws Exception {
        // Load keystore (change password and path as needed)
        char[] passphrase = "password".toCharArray();
        KeyStore ks = KeyStore.getInstance("JKS");
        ks.load(new FileInputStream("serverkeystore.jks"), passphrase);

        KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
        kmf.init(ks, passphrase);

        SSLContext sc = SSLContext.getInstance("TLS");
        sc.init(kmf.getKeyManagers(), null, null);

        int port = 12346;
        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.out.println("Invalid port, using default 12346");
            }
        } else {
            Scanner scanner = new Scanner(System.in);
            System.out.print("Enter port for server (default 12346): ");
            String input = scanner.nextLine();
            if (!input.trim().isEmpty()) {
                try {
                    port = Integer.parseInt(input.trim());
                } catch (NumberFormatException e) {
                    System.out.println("Invalid port, using default 12346");
                }
            }
        }

        SSLServerSocketFactory ssf = sc.getServerSocketFactory();
        SSLServerSocket s = (SSLServerSocket) ssf.createServerSocket(port);
        System.out.println("TLS Server listening on port " + port + "...");
        ExecutorService pool = Executors.newCachedThreadPool();
        while (true) {
            SSLSocket clientSocket = (SSLSocket) s.accept();
            pool.execute(new ClientHandler(clientSocket));
        }
    }
}

class ClientHandler implements Runnable {
    private SSLSocket socket;

    public ClientHandler(SSLSocket socket) {
        this.socket = socket;
    }

    public void run() {
        String clientAddr = socket.getRemoteSocketAddress().toString();
        System.out.println("TLS Client " + clientAddr + " connected.");
        try (
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        ) {
            String msg;
            while ((msg = in.readLine()) != null) {
                System.out.println(clientAddr + ": " + msg);
                out.write("Received: " + msg + "\n");
                out.flush();
            }
        } catch (IOException e) {
            // ignore
        }
        System.out.println("TLS Client " + clientAddr + " disconnected.");
        try { socket.close(); } catch (IOException e) {}
    }
}
