package Final_Project;

import java.awt.Component;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.Font;
import javax.swing.SwingConstants;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JTextArea;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.net.*;
import java.io.*;
import javax.swing.JScrollPane;
import java.util.*;

public class UDPServer_Chat extends JFrame {

	private JPanel contentPane;
	private InetAddress server;
	private DatagramSocket theSocket;
	private int portNo;
	private JTextField textField;
	private JTextField textField_1;
	private int maxSize;
	static DataInputStream dataIn;
	static DataOutputStream dataOut;
	private JTextArea textArea;
	

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					UDPServer_Chat frame = new UDPServer_Chat();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public UDPServer_Chat() {
		setTitle("UDP Server");
		ArrayList<Integer> portList = new ArrayList<>();
		portList.add(-1);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 520, 340);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(31, 119, 448, 137);
		contentPane.add(scrollPane);
		
		JTextArea textArea = new JTextArea();
		scrollPane.setViewportView(textArea);
		
		JButton btnNewButton = new JButton("Click me");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				portNo = Integer.parseInt(textField.getText());
				maxSize = Integer.parseInt(textField_1.getText());
				byte[] buffer = new byte[maxSize];
				Runnable thread = () -> {
					DatagramSocket server = null;
					try {
						server = new DatagramSocket(portNo);
					} catch (SocketException e3) {
						// TODO Auto-generated catch block
						e3.printStackTrace();
					}
					DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
					Component frame = null;
					JOptionPane.showMessageDialog(frame, "Ready to receive data"," UDP Protocol",JOptionPane.WARNING_MESSAGE);
					String message ="";
					String content_message = "";
					while (true) {
				        try {
							server.receive(packet);
						} catch (IOException e2) {
							// TODO Auto-generated catch block
							e2.printStackTrace();
						}
				        try {
							message = new String(packet.getData(), 0, packet.getLength(), "UTF-8");
							content_message = message.split("---")[1];
						} catch (UnsupportedEncodingException e2) {
							// TODO Auto-generated catch block
							e2.printStackTrace();
						}
				        textArea.setText(textArea.getText().trim() + "\n" + packet.getAddress() + " at port " + packet.getPort() + " say: " + message);
				        if(!portList.contains(packet.getPort()))
				        	portList.add(packet.getPort());
				        System.out.println(portList);
				        String clientMessage = message;
				        byte[] clientMessageData = clientMessage.getBytes();  
				        textArea.setText(textArea.getText().trim() + "\n" + "Server re-send to client message: " + clientMessage);
				        if(content_message.equals("exit"))
				        	portList.remove(portList.indexOf(packet.getPort()));
				        for(Integer port : portList) {
				        	 if(port != -1) {
				        		 DatagramPacket clientMessagePacket = new DatagramPacket(clientMessageData, clientMessageData.length, packet.getAddress(), port);
				                 try {
									server.send(clientMessagePacket);
								} catch (IOException e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();
								}
				        	 } 
				        }

				    }
				};
				new Thread(thread).start();
			}
		});
		btnNewButton.setFont(new Font("Tahoma", Font.BOLD, 15));
		btnNewButton.setBounds(204, 79, 114, 30);
		contentPane.add(btnNewButton);
		
		JLabel lblPortno_2 = new JLabel("PortNo:");
		lblPortno_2.setHorizontalAlignment(SwingConstants.RIGHT);
		lblPortno_2.setFont(new Font("Tahoma", Font.BOLD, 15));
		lblPortno_2.setBounds(25, 36, 87, 31);
		contentPane.add(lblPortno_2);
		
		textField = new JTextField();
		textField.setText("2022");
		textField.setColumns(10);
		textField.setBounds(120, 38, 69, 31);
		contentPane.add(textField);
		
		JLabel lblPortno_1 = new JLabel("Max Packet Size:");
		lblPortno_1.setHorizontalAlignment(SwingConstants.RIGHT);
		lblPortno_1.setFont(new Font("Tahoma", Font.BOLD, 15));
		lblPortno_1.setBounds(219, 36, 152, 31);
		contentPane.add(lblPortno_1);
		
		textField_1 = new JTextField();
		textField_1.setText("56507");
		textField_1.setColumns(10);
		textField_1.setBounds(381, 38, 98, 31);
		contentPane.add(textField_1);
		
	}
}
