import java.io.*;
import java.net.*;
import java.util.Scanner;
import java.util.concurrent.*;

public class Server {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter port to listen on: ");
        int port = 12345;
        try {
            port = Integer.parseInt(scanner.nextLine());
        } catch (Exception e) {
            System.out.println("Invalid input, using default port 12345.");
        }
        ServerSocket serverSocket = null;
        ExecutorService pool = Executors.newCachedThreadPool();
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Server listening on port " + port + "...");
            while (true) {
                Socket clientSocket = serverSocket.accept();
                pool.execute(new ClientHandler(clientSocket));
            }
        } catch (IOException e) {
            System.err.println("Server error: " + e.getMessage());
        } finally {
            if (serverSocket != null) {
                try { serverSocket.close(); } catch (IOException e) {}
            }
            pool.shutdown();
        }
    }
}

class ClientHandler implements Runnable {
    private Socket socket;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    public void run() {
        String clientAddr = socket.getRemoteSocketAddress().toString();
        System.out.println("Client " + clientAddr + " connected.");
        try (
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()))
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
        System.out.println("Client " + clientAddr + " disconnected.");
        try { socket.close(); } catch (IOException e) {}
    }
}


