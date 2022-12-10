import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientUI extends JFrame implements Runnable{
    private JPanel myPanel;
    private JTextField serverAddressInp;
    private JTextField portInp;
    private JTextArea textArea1;
    private JTextField msgInp;
    private JButton sendButton;
    private JButton startChatButton;
    private JTextField userName;
    private JButton leaveButton;

    ///////////////// MAIN CHAT /////////////////
    private Socket client;
    private BufferedReader in;
    private PrintWriter out;
    private boolean done;
    private ClientUI.InputHandler inputHandler;
    public ClientUI() {
        startChatButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                run();
            }
        });
        leaveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                out.println("/quit");
                shutdown();
            }
        });
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                inputHandler.run();
            }
        });
    }

    @Override
    public void run() {
        try{
            int port = Integer.parseInt(portInp.getText());
            client = new Socket(serverAddressInp.getText(), port);
            out = new PrintWriter(client.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            inputHandler = new ClientUI.InputHandler();
            Thread receivingThread = new Thread(() -> {
                out.println(userName.getText());
                String inMessage;
                while(true){
                    try {
                        if (((inMessage = in.readLine()) != null)){
                            textArea1.setText(textArea1.getText().trim() + "\n" + inMessage);
                        }
                    } catch (IOException e) {
                        //ignore
                    }

                }
            });
            receivingThread.start();
            Thread t = new Thread(inputHandler);
            t.start(); //open separate thread
        }catch (IOException e){
            //TODO: handle
            shutdown();
        }
    }

    public void shutdown(){
        done = true;
        try{
            out.println(userName + " shut down!");
            in.close();
            out.close();
            if(!client.isClosed()){
                client.close();
            }
        }catch (IOException e){
            //ignore
        }
    }

    class InputHandler implements Runnable{
        @Override
        public void run() {
            //send message
            String message = msgInp.getText();
            out.println(message);
            msgInp.setText("");

//            try{
//                BufferedReader inReader = new BufferedReader(new InputStreamReader(System.in));
//
//                while(!done){
//                    String message = inReader.readLine();
//                    if(message.equals("/quit")){
//                        inReader.close();
//                        shutdown();
//                    }else{
//                        out.println(message);
//                    }
//                }
//            }catch (IOException e){
//                // TODO: handle
//                shutdown();
//            }
        }
    }

    public static void main(String[] args) {
        ClientUI ui = new ClientUI();

        ui.setContentPane(ui.myPanel);
        ui.setTitle("52000643_Client");
        ui.setSize(700, 300);
        ui.setVisible(true);
        ui.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
    }
}
