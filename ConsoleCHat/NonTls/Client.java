import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter server IP address: ");
        String ip = scanner.nextLine();
        System.out.print("Enter server port: ");
        int port = 12345;
        try {
            port = Integer.parseInt(scanner.nextLine());
        } catch (Exception e) {
            System.out.println("Invalid input, using default port 12345.");
        }
        Socket socket = new Socket(ip, port);
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Connected to server. Type messages:");
        try {
            String line;
            while ((line = userInput.readLine()) != null) {
                out.write(line + "\n");
                out.flush();
                String response = in.readLine();
                System.out.println("Server: " + response);
            }
        } catch (IOException e) {
            // ignore
        }
        socket.close();
    }
}
