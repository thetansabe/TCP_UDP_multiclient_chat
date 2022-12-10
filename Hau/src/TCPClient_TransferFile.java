package Final_Project;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.*;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import java.awt.Font;
import javax.swing.SwingConstants;

class ClientTransferView extends JFrame {
    private static final long serialVersionUID = 1L;

    private JLabel labelHost;
    private JTextField textFieldHost;
    private JLabel labelPort;
    private JTextField textFieldPort;
    private JButton btnBrowse;
    private JTextField textFieldFilePath;
    private JButton btnSendFile;
    private JTextArea textAreaResult;

    public ClientTransferView() {
        setTitle("Client - Transfer file using TCP protocol");
        labelHost = new JLabel("Host:");
        textFieldHost = new JTextField();
        labelPort = new JLabel("Port:");
        textFieldPort = new JTextField();
        labelHost.setBounds(20, 20, 50, 25);
        textFieldHost.setBounds(55, 20, 120, 25);
        labelPort.setBounds(190, 20, 50, 25);
        textFieldPort.setBounds(220, 20, 50, 25);

        textFieldFilePath = new JTextField();
        textFieldFilePath.setBounds(20, 50, 450, 25);
        btnBrowse = new JButton("Browse");
        btnBrowse.setBounds(470, 50, 80, 25);
        btnSendFile = new JButton("Send File");
        btnSendFile.setBounds(20, 80, 80, 25);
        textAreaResult = new JTextArea();
        textAreaResult.setBounds(20, 110, 490, 150);

        add(labelHost);
        add(textFieldHost);
        add(labelPort);
        add(textFieldPort);
        add(textFieldFilePath);
        add(btnBrowse);
        add(btnSendFile);
        add(textAreaResult);

        setLayout(null);
        setSize(600, 350);
        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    public void chooseFile() {
        final JFileChooser fc = new JFileChooser();
        fc.showOpenDialog(this);
        try {
            if (fc.getSelectedFile() != null) {
                textFieldFilePath.setText(fc.getSelectedFile().getPath());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public JLabel getLabelHost() {
        return labelHost;
    }

    public void setLabelHost(JLabel labelHost) {
        this.labelHost = labelHost;
    }

    public JTextField getTextFieldHost() {
        return textFieldHost;
    }

    public void setTextFieldHost(JTextField textFieldHost) {
        this.textFieldHost = textFieldHost;
    }

    public JLabel getLabelPort() {
        return labelPort;
    }

    public void setLabelPort(JLabel labelPort) {
        this.labelPort = labelPort;
    }

    public JTextField getTextFieldPort() {
        return textFieldPort;
    }

    public void setTextFieldPort(JTextField textFieldPort) {
        this.textFieldPort = textFieldPort;
    }

    public JButton getBtnBrowse() {
        return btnBrowse;
    }

    public void setBtnBrowse(JButton btnBrowse) {
        this.btnBrowse = btnBrowse;
    }

    public JTextField getTextFieldFilePath() {
        return textFieldFilePath;
    }

    public void setTextFieldFilePath(JTextField textFieldFilePath) {
        this.textFieldFilePath = textFieldFilePath;
    }

    public JButton getBtnSendFile() {
        return btnSendFile;
    }

    public void setBtnSendFile(JButton btnSendFile) {
        this.btnSendFile = btnSendFile;
    }

    public JTextArea getTextAreaResult() {
        return textAreaResult;
    }

    public void setTextAreaResult(JTextArea textAreaResult) {
        this.textAreaResult = textAreaResult;
    }
}

class ClientTransferController implements ActionListener {
    private ClientTransferView view;

    public ClientTransferController(ClientTransferView view) {
        this.view = view;
        view.getBtnBrowse().addActionListener(this);
        view.getBtnSendFile().addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals(view.getBtnBrowse().getText())) {
            view.chooseFile();
        }
        if (e.getActionCommand().equals(view.getBtnSendFile().getText())) {
            String host = view.getTextFieldHost().getText().trim();
            int port = Integer.parseInt(view.getTextFieldPort().getText().trim());
            String sourceFilePath = view.getTextFieldFilePath().getText();
            if (host != "" && sourceFilePath != "") {
                String destinationDir = "C:\\server\\"; 
                TCPClient_TransferFile tcpClient = new TCPClient_TransferFile(host, port, view.getTextAreaResult());
                tcpClient.connectServer();
                tcpClient.sendFile(sourceFilePath, destinationDir);
                tcpClient.closeSocket();
            }
        }
    }
}
public class TCPClient_TransferFile {
    private Socket client;
    private String host;
    private int port;
    private JTextArea textAreaLog;

    public TCPClient_TransferFile(String host, int port, JTextArea textAreaLog) {
        this.host = host;
        this.port = port;
        this.textAreaLog = textAreaLog;
    }
    
    public void connectServer() {
        try {
            client = new Socket(host, port);
            textAreaLog.append("connected to server.\n");
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendFile(String sourceFilePath, String destinationDir) {
        DataOutputStream outToServer = null;
        ObjectOutputStream oos = null;
        ObjectInputStream ois = null;
        try {
            outToServer = new DataOutputStream(client.getOutputStream());
            outToServer.writeUTF("Client from " + client.getLocalSocketAddress());
            FileInfo fileInfo = getFileInfo(sourceFilePath, destinationDir);
            oos = new ObjectOutputStream(client.getOutputStream());
            oos.writeObject(fileInfo);
            ois = new ObjectInputStream(client.getInputStream());
            fileInfo = (FileInfo) ois.readObject();
            if (fileInfo != null) {
                textAreaLog.append("send file to server " + fileInfo.getStatus() + "\n");
            }
	        } catch (IOException ex) {
	            ex.printStackTrace();
	        } catch (ClassNotFoundException e) {
	            e.printStackTrace();
	        } finally {
	            closeStream(oos);
	            closeStream(ois);
	            closeStream(outToServer);
        }
    }

    private FileInfo getFileInfo(String sourceFilePath, String destinationDir) {
        FileInfo fileInfo = null;
        BufferedInputStream bis = null;
        try {
            File sourceFile = new File(sourceFilePath);
            bis = new BufferedInputStream(new FileInputStream(sourceFile));
            fileInfo = new FileInfo();
            byte[] fileBytes = new byte[(int) sourceFile.length()];
            bis.read(fileBytes, 0, fileBytes.length);
            fileInfo.setFilename(sourceFile.getName());
            fileInfo.setDataBytes(fileBytes);
            fileInfo.setDestinationDirectory(destinationDir);
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            closeStream(bis);
        }
        return fileInfo;
    }

    public void closeSocket() {
        try {
            if (client != null) {
                client.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void closeStream(InputStream inputStream) {
        try {
            if (inputStream != null) {
                inputStream.close();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void closeStream(OutputStream outputStream) {
        try {
            if (outputStream != null) {
                outputStream.close();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    public static void main(String[] args) {
        ClientTransferView view = new ClientTransferView();
        view.getTextFieldPort().setText("2022");
        view.getTextFieldHost().setText("localhost");
        view.getBtnBrowse().setLocation(475, 76);
        view.getLabelHost().setLocation(20, 41);
        view.getTextFieldHost().setLocation(53, 41);
        view.getTextFieldFilePath().setLocation(20, 76);
        view.getBtnSendFile().setFont(new Font("Tahoma", Font.BOLD, 10));
        view.getTextAreaResult().setSize(535, 124);
        view.getBtnSendFile().setSize(114, 25);
        view.getBtnSendFile().setText("Send to server");
        view.getTextAreaResult().setLocation(20, 146);
        view.getBtnSendFile().setLocation(221, 111);
        view.getTextFieldPort().setLocation(386, 41);
        view.getLabelPort().setLocation(351, 41);
        

        view.setTitle("TCP Client - Transfer file using TCP protocol");
        new ClientTransferController(view);
    }
}


