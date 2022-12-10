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

public class UDPClient_Chat extends JFrame {

	private JPanel contentPane;
	private JTextField txtLocalhost;
	private JLabel lblPortno_2;
	private JTextField textField_1;
	private JLabel lblPortno;
	private JTextField datainput;
	private JButton btnSend;
	private InetAddress server;
	private DatagramSocket theSocket;
	private int portNo;
	private String hostName;
	private JTextField nameField;
	String message = "";
	byte[] buffer = new byte[4096];
	DatagramPacket serverPacket = new DatagramPacket(buffer, buffer.length);
	

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					UDPClient_Chat frame = new UDPClient_Chat();
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
	public UDPClient_Chat() {
		setTitle("UDP Client");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 520, 340);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblPortno_1 = new JLabel("Server: ");
		lblPortno_1.setHorizontalAlignment(SwingConstants.RIGHT);
		lblPortno_1.setFont(new Font("Tahoma", Font.BOLD, 15));
		lblPortno_1.setBounds(178, 34, 69, 31);
		contentPane.add(lblPortno_1);
		
		txtLocalhost = new JTextField();
		txtLocalhost.setText("localhost");
		txtLocalhost.setColumns(10);
		txtLocalhost.setBounds(252, 36, 69, 31);
		contentPane.add(txtLocalhost);
		
		lblPortno_2 = new JLabel("PortNo:");
		lblPortno_2.setHorizontalAlignment(SwingConstants.RIGHT);
		lblPortno_2.setFont(new Font("Tahoma", Font.BOLD, 15));
		lblPortno_2.setBounds(313, 36, 87, 31);
		contentPane.add(lblPortno_2);
		
		textField_1 = new JTextField();
		textField_1.setText("2022");
		textField_1.setColumns(10);
		textField_1.setBounds(410, 38, 69, 31);
		contentPane.add(textField_1);
		
		JLabel namelabel = new JLabel("Name: ");
		namelabel.setHorizontalAlignment(SwingConstants.RIGHT);
		namelabel.setFont(new Font("Tahoma", Font.BOLD, 15));
		namelabel.setBounds(7, 34, 87, 31);
		contentPane.add(namelabel);
		
		nameField = new JTextField();
		nameField.setText("UserA");
		nameField.setColumns(10);
		nameField.setBounds(99, 36, 69, 31);
		contentPane.add(nameField);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(31, 119, 448, 137);
		contentPane.add(scrollPane);
		
		JTextArea textArea = new JTextArea();
		scrollPane.setViewportView(textArea);
		
		lblPortno = new JLabel("Input Data:");
		lblPortno.setHorizontalAlignment(SwingConstants.RIGHT);
		lblPortno.setFont(new Font("Tahoma", Font.BOLD, 15));
		lblPortno.setBounds(10, 266, 98, 31);
		contentPane.add(lblPortno);
		
		datainput = new JTextField();
		datainput.setText("Hello");
		datainput.setColumns(10);
		datainput.setBounds(108, 268, 268, 31);
		contentPane.add(datainput);
		
		JButton btnNewButton = new JButton("Start");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				hostName = txtLocalhost.getText();
				portNo = Integer.parseInt(textField_1.getText());
				try {
					server = InetAddress.getByName(hostName);
					theSocket = new DatagramSocket();
					Component fame = null;
					JOptionPane.showMessageDialog(fame, "Ready to send data","UDP Protocol",JOptionPane.WARNING_MESSAGE);
					Runnable thread = () -> {
						while (true) {
							try {
								theSocket.receive(serverPacket);
							} catch (IOException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
							try {
								message = new String(serverPacket.getData(), 0, serverPacket.getLength(), "UTF-8");
							} catch (UnsupportedEncodingException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
							String username = message.split("---")[0];
							String message_content = message.split("---")[1];
							if(!username.equals(nameField.getText()))
								textArea.setText(textArea.getText().trim() + "\n" + username + ": " + message_content);
							if(message_content.equals("exit"))
								textArea.setText(textArea.getText().trim() + "\n" + username + " has left the chat" );
						}
					};
					new Thread(thread).start();
				}catch (UnknownHostException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
					} catch (SocketException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		btnNewButton.setFont(new Font("Tahoma", Font.BOLD, 15));
		btnNewButton.setBounds(109, 79, 114, 30);
		contentPane.add(btnNewButton);
		
		

		
		btnSend = new JButton("Send");
		btnSend.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String userInputData = datainput.getText();
				byte[] data = (nameField.getText() + "---" + userInputData).getBytes();
				DatagramPacket packet = new DatagramPacket(data, data.length, server, portNo);
				try {
					theSocket.send(packet);
					textArea.setText(textArea.getText().trim() + "\nMe: " + userInputData);
					
					
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				datainput.setText("");
			}
		});
		btnSend.setFont(new Font("Tahoma", Font.BOLD, 15));
		btnSend.setBounds(386, 266, 78, 31);
		contentPane.add(btnSend);
		
		JButton btnLeave = new JButton("Leave");
		btnLeave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		btnLeave.setFont(new Font("Tahoma", Font.BOLD, 15));
		btnLeave.setBounds(296, 79, 114, 30);
		contentPane.add(btnLeave);
		
	}
}
