
package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author Admin
 */
public class Server {

    public static volatile ServerThreadBus serverThreadBus;
    public static Socket socketOfServer;

    public static void main(String[] args) {
        ServerSocket listener = null;
        serverThreadBus = new ServerThreadBus();
        System.out.println("Server is waiting to accept user...");
        int clientNumber = 0;
        // tao serverSocket lang nghe ket noi tu client tren cong 7777
        // bien num so luong client ket noi den
        try {
            listener = new ServerSocket(7777);
        } catch (IOException e) {
            System.out.println(e);
            System.exit(1);
        }
        // tao doi tuong threadpoolExecutor de quan ly viec chay cac thread de xu ly ket noi va tin nhan
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                10, // corePoolSize co	10 thread 
                100, // maximumPoolSize	toi da la 100
                10, // thread timeout	thoi gian cho cua mot thread la 10s
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(8) // queueCapacity neu ko co thread nao ss ket noi, dua vao 1 hang doi co dung luong toi da la 8queueCapcity
        );
        try {
            while (true) {
               
                socketOfServer = listener.accept(); // chap nhan cac ket noi tu client
                System.out.println(socketOfServer);
                ServerThread serverThread = new ServerThread(socketOfServer, clientNumber++); // tao serverthread xu ly cac ket noi do
                serverThreadBus.add(serverThread); // them serverthread vao serverthreadbus de theo doi va quan li tin nhan giua server va cac client
                System.out.println("So Thread dang chay la: "+serverThreadBus.getLength()+" "+socketOfServer);
                executor.execute(serverThread);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            try {
                listener.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}