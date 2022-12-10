import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Stack;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server implements Runnable{
    //with runnable this class can be passed into a thread or a thread pool
    //can be executed concurrently
    private ArrayList<ConnectionHandler> connections;
    private ServerSocket server;
    private boolean done;
    private ExecutorService pool; //thread pool
    public  Server(InetAddress host, int port){
        connections = new ArrayList<>();
        done = false;
        try {
//            server = new ServerSocket(port, 10, host);
            server = new ServerSocket(port);
        }catch (IOException e){
            shutdown();
        }
    }

    @Override
    public void run() {
        try {
            pool = Executors.newCachedThreadPool();
            while(!done){
                Socket client = server.accept();
                ConnectionHandler newConnection = new ConnectionHandler(client);
                connections.add(newConnection);

                pool.execute(newConnection); //execute runnable -> run()
            }
        } catch (Exception e) {
            // TODO: shutdown
            shutdown();
        }
    }

    public void broadcast(String message){
        for(ConnectionHandler ch: connections){
            if(ch!=null){
                ch.sendMessage(message);
            }
        }
    }

    public void shutdown(){
        try{
            done = true;
            if(!server.isClosed()){
                server.close();
            }

            System.out.println("===Shut down server===");

            for(ConnectionHandler ch : connections){
                ch.shutdown();
            }
        }catch (IOException e){
            // ignore
        }
    }
    class ConnectionHandler implements Runnable{
        //this class handle individual connection

        private Socket client;
        private BufferedReader in;
        private PrintWriter out;
        private String nickName;

        public ConnectionHandler (Socket client){
            this.client = client;
        }

        @Override
        public void run() {
            try {
                out = new PrintWriter(client.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                out.println("Server opened");
                nickName = in.readLine();

                System.out.println(nickName + " connected!");
                broadcast(nickName + " joined the chat!");

                String message;
                while((message = in.readLine()) != null){
                    if(message.startsWith("/nick")){
                        // TODO: handle rename nickname
                        String[] messageSplit = message.split(" ", 2);
                        if(messageSplit.length == 2){
                            broadcast(nickName + " renamed to " + messageSplit[1]);
                            System.out.println(nickName+ " renamed to " + messageSplit[1]);
                            nickName = messageSplit[1];
                            out.println("Successfully changed nickname to " + nickName);
                        }else{
                            System.out.println("No nickname provided!");
                        }
                    } else if (message.startsWith("/quit")) {
                        // TODO: quit
                        broadcast(nickName + " left the chat :(");
                        shutdown();
                    } else if (message.startsWith("/quadratic")) {
                        String[] messageSplit = message.split(" ", 4);

                        int a = Integer.parseInt(messageSplit[1]);
                        int b = Integer.parseInt(messageSplit[2]);
                        int c = Integer.parseInt(messageSplit[3]);
                        
                        String res = quadraticSolver(a, b, c);
                        broadcast(nickName + " - quadratic solved: " + res);
                    } else if (message.startsWith("/arith_cal")) {
                        String[] messageSplit = message.split(" ",2);
                        
                        Double res = calPostfix(convertInfixToPostfix(messageSplit[1]));
                        broadcast(nickName + " - arithmetic calculated: " + res);
                    } else if (message.startsWith("/area")) {
                        String[] messageSplit = message.split(" ",4);
                        String kind = messageSplit[1];
                        int a = Integer.parseInt(messageSplit[2]);
                        int b = Integer.parseInt(messageSplit[3]);
                        Double res = 0.0;

                        switch (kind){
                            case "rectangle":
                                res = sRectangle(a,b);
                                break;
                            case "triangle":
                                res = sTriangle(a,b);
                                break;
                            default:
                                break;
                        }
                        broadcast(nickName+ " - area calculated: " + res);
                    } else{
                        //TODO: send message
                        if(message.trim() != "")
                            broadcast(nickName+ ": " + message);
                    }
                }
            } catch (IOException e) {
                shutdown();
            }
        }

        public void sendMessage(String message){
            out.println(message);
        }

        public void shutdown(){
            try{
                in.close();
                out.close();
                System.out.println("===Server shut down===");
                if(!client.isClosed()){
                    client.close();
                }
            }
            catch (IOException e){
                //ignore
            }
        }
    }

    /////////////// 3.3.2 - Tinh dien tich hinh hoc//////////
    public double sTriangle(int a, int h){
        return 0.5*a*h;
    }

    public double sRectangle(int a, int b){
        return a*b;
    }


    ////////////// 3.3.3 - Giai phuong trinh //////////////
    public String quadraticSolver(int a, int b, int c){
        String res = "";
        if (a == 0) {
            if (b == 0) {
                res = "Phương trình vô nghiệm";
            } else {
                res = "Phương trình có một nghiệm: " + "x = " + (-c / b);
            }
            return res;
        }
        // tính delta
        float delta = b*b - 4*a*c;
        float x1;
        float x2;
        // tính nghiệm
        if (delta > 0) {
            x1 = (float) ((-b + Math.sqrt(delta)) / (2*a));
            x2 = (float) ((-b - Math.sqrt(delta)) / (2*a));

            res = "Phương trình có 2 nghiệm là: "
                    + "x1 = " + x1 + " và x2 = " + x2;

        } else if (delta == 0) {
            x1 = (-b / (2 * a));
            res = "Phương trình có nghiệm kép: "
                    + "x1 = x2 = " + x1;
        } else {
            res = "Phương trình vô nghiệm!";
        }
        return res;
    }

    ////////// 3.3.4 - Tinh gia tri bieu thuc ///////////
    public String convertInfixToPostfix(String infix) {
        String postfix = "";
        Stack<Character> stack = new Stack<>();
        for (int i = 0; i < infix.length(); i++) {
            char ch = infix.charAt(i);
            // case operand
            if (ch >= 48 && ch <= 57) {
                postfix = postfix + ch;
            }

            if (ch >= 97 && ch <= 122) {
                postfix = postfix + ch;
            }
            // case '('
            if (ch == '(') {
                stack.push(ch);
            }
            // case ')'
            if (ch == ')') {
                while (stack.peek() != '(') {
                    postfix = postfix + stack.pop();
                }
                stack.pop();
            }
            // case operator
            if (ch == '+' || ch == '-' || ch == '*' || ch == '/' || ch == '^') {
                while (!stack.empty() &&
                        stack.peek() != '(' &&
                        precedence(ch) <= precedence(stack.peek())) // cho nay` kho nhat
                {
                    postfix = postfix + stack.pop();
                }
                stack.push(ch);
            }
        }
        // add operand cuoi' infix (hien dang o trong stack) vao postfix
        while (!stack.empty()) {
            postfix = postfix + stack.pop();
        }
        return postfix;
    }
    public int precedence(Character ch) {
        if (ch == '+' || ch == '-')
            return 1;
        if (ch == '*' || ch == '/')
            return 10;
        return 100; // (ch=="^");
    }
    public Double calPostfix(String postfix) {
        Stack<Double> stack = new Stack<>();

        for (int i = 0; i < postfix.length(); i++) {
            Character ch = postfix.charAt(i);
            // operand -> push vao stack
            if (ch >= 48 && ch <= 57) {
                stack.push(Double.parseDouble(String.valueOf(ch)));
            }
            // operator
            if (ch == '+' || ch == '-' || ch == '*' || ch == '/') {
                // pop arguments from stack;
                Double arg2 = stack.pop();
                Double arg1 = stack.pop();
                Double result = calOperation(arg2, arg1, ch);
                // push the result onto stack
                stack.push(result);
            }
        }
        while (stack.isEmpty())
            return -1.0; // sai
        return stack.pop();
    }
    public static Double calOperation(Double arg2, Double arg1, int ch) {
        if (ch == '+') {
            return arg1 + arg2;
        }
        if (ch == '-') {
            return arg1 - arg2;
        }
        if (ch == '*') {
            return arg1 * arg2;
        }
        return arg1 / arg2;
    }

    /////////////////////// MAIN ////////////////////////
    public static void main(String[] args){

        try {
            int port = Integer.parseInt(args[1]);
            InetAddress inet = InetAddress.getByName(args[0]);

            Server server = new Server(inet, port);
            server.run();
        }catch (UnknownHostException e){
            System.out.println("unknown host exception");
        }

    }
}
