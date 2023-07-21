package server;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

/**
 *
 * @author Admin
 */
public class ServerThread implements Runnable {

    private Socket socketOfServer;   // tao ket noi giua client va server
    private int clientNumber;	// so thu tu cua client ket noi voi may chu
    private BufferedReader is;	// nhan 
    private BufferedWriter os;	// gui tin nhan giua client va server
    private boolean isClosed;	

    public BufferedReader getIs() {
        return is;
    }

    public BufferedWriter getOs() {
        return os;
    }

    public int getClientNumber() {
        return clientNumber;
    }
// constructor cua class nhan vao socketofserver va clientnumber
// luu tru cac gia tri vao cac thuoc tinh 
    public ServerThread(Socket socketOfServer, int clientNumber) {
        this.socketOfServer = socketOfServer;
        this.clientNumber = clientNumber;
        System.out.println("Server thread number " + clientNumber + " Started");
        isClosed = false;
    }
// run dc thuc hien khi 1 luong class dc khoi tao
    @Override
    public void run() {
        try {
           // truyen va nhan tin nhan
            is = new BufferedReader(new InputStreamReader(socketOfServer.getInputStream()));
            os = new BufferedWriter(new OutputStreamWriter(socketOfServer.getOutputStream()));
            System.out.println("Khoi tao luong thanh cong, ID la:" + clientNumber);
            write("get-id" + "," + this.clientNumber);
            Server.serverThreadBus.sendOnlineList();	// danh sach cac client dang online
            Server.serverThreadBus.mutilCastSend("global-message"+","+"---Client "+this.clientNumber+"Dang dang nhap");
            String message;
            while (!isClosed) {
                message = is.readLine();
                if (message == null) {
                    break;
                }
                String[] messageSplit = message.split(",");
                if(messageSplit[0].equals("send-to-global")){
                    Server.serverThreadBus.boardCast(this.getClientNumber(),"global-message"+","+"Client "+messageSplit[2]+": "+messageSplit[1]);
                }
                if(messageSplit[0].equals("send-to-person")){
                    Server.serverThreadBus.sendMessageToPersion(Integer.parseInt(messageSplit[3]),"Client "+ messageSplit[2]+" (Toi ban): "+messageSplit[1]);
                }
            }
        } catch (IOException e) {
            isClosed = true;
            Server.serverThreadBus.remove(clientNumber);
            System.out.println(this.clientNumber+"Da thoat ");
            Server.serverThreadBus.sendOnlineList();
            Server.serverThreadBus.mutilCastSend("global-message"+","+"---Client "+this.clientNumber+"Da thoat ");
        }
    }
    public void write(String message) throws IOException{
        os.write(message);
        os.newLine();
        os.flush();
    }
}