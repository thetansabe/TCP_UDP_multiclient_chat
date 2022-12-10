package Final_Project;

import java.awt.Component;
import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.Font;
import java.io.*;
import java.net.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

public class TCPServer_TransferFile extends JFrame {
	private JPanel contentPane;
	private static JTextArea msg_area;
	static ServerSocket serverSocket;
	static Socket server;
	DataInputStream dataFromClient = null;
	ObjectInputStream ois = null;
	ObjectOutputStream oos = null;
	private JLabel lblNewLabel;
	static Component frame = null;
	private JLabel lblNewLabel_1;
	private JTextField textField;
	private JLabel lblNewLabel_1_1;
	private JTextField textField_1 = new JTextField();
	private JButton msg_send_1;
	private static int port = 2001;
	private JButton close_btn;
	private String message = "";
	
	
	/**
	* Launch the application.
	*/
	
	public TCPServer_TransferFile() {
		setTitle("TCP Server - Transfer file using TCP protocol");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 532, 387);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		lblNewLabel_1 = new JLabel("ServerName: ");
		lblNewLabel_1.setFont(new Font("Tahoma", Font.BOLD, 13));
		lblNewLabel_1.setBounds(24, 23, 100, 36);
		contentPane.add(lblNewLabel_1);
		
		textField = new JTextField();
		textField.setText("localhost");
		textField.setColumns(10);
		textField.setBounds(120, 25, 76, 36);
		contentPane.add(textField);
		
		lblNewLabel_1_1 = new JLabel("PortNo: ");
		lblNewLabel_1_1.setFont(new Font("Tahoma", Font.BOLD, 13));
		lblNewLabel_1_1.setBounds(217, 23, 63, 36);
		contentPane.add(lblNewLabel_1_1);
		
		textField_1.setText("2022");
		textField_1.setColumns(10);
		textField_1.setBounds(279, 25, 81, 36);
		contentPane.add(textField_1);
		
		msg_send_1 = new JButton("Start");

		msg_send_1.setHorizontalAlignment(SwingConstants.LEFT);
		msg_send_1.setFont(new Font("Tahoma", Font.BOLD, 12));
		msg_send_1.setBounds(397, 23, 100, 36);
		contentPane.add(msg_send_1);
		msg_send_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				Runnable thread = () -> {
					TCPServer_TransferFile.port = Integer.parseInt(textField_1.getText());
					try {
						serverSocket = new ServerSocket(TCPServer_TransferFile.port);
						JOptionPane.showMessageDialog(frame, "The Server is waiting ....", "Note", JOptionPane.WARNING_MESSAGE);
						server = serverSocket.accept();
						JOptionPane.showMessageDialog(frame, "A Client is connected to the Server", "Note",JOptionPane.WARNING_MESSAGE);
						dataFromClient = new DataInputStream(server.getInputStream());
						System.out.println(dataFromClient.readUTF());
						 ois = new ObjectInputStream(server.getInputStream());
			                FileInfo fileInfo = (FileInfo) ois.readObject();
			                if (fileInfo != null) {
			                	 BufferedOutputStream bos = null;
			                     try {
			                         if (fileInfo != null) {
			                             File fileReceive = new File(fileInfo.getDestinationDirectory() + fileInfo.getFilename());
			                             bos = new BufferedOutputStream(new FileOutputStream(fileReceive));
			                             bos.write(fileInfo.getDataBytes());
			                             bos.flush();
			                         }
			                     } catch (IOException ex) {
			                         ex.printStackTrace();
			                     } finally {
			                    	 try {
			                             if (bos != null) {
			                                 bos.close();
			                             }
			                         } catch (IOException ex) {
			                             ex.printStackTrace();
			                         }
			                     }
			                }
			                oos = new ObjectOutputStream(server.getOutputStream());
			                fileInfo.setStatus("success");
							message += "Receive file successful!\n";
							msg_area.setText(message);
			                fileInfo.setDataBytes(null);
			                oos.writeObject(fileInfo);
					} catch (IOException ex) {
						ex.printStackTrace();
					} catch (ClassNotFoundException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					finally{
						 try {
							 if (ois != null) {
					             ois.close();
					         }
					     } catch (IOException ex) {
					          ex.printStackTrace();
					     }
						 try {
							 if (oos != null) {
					             oos.close();
					         }
					     } catch (IOException ex) {
					          ex.printStackTrace();
					     }
						 try {
							 if (dataFromClient != null) {
								 dataFromClient.close();
					         }
					     } catch (IOException ex) {
					          ex.printStackTrace();
					     }
						try{
							if(server!=null){
								server.close();
							}
						}
						catch(IOException ex){
							System.out.print(ex);
						}
					}
				};
				new Thread(thread).start();
				
			}
		});
		msg_area = new JTextArea();
		msg_area.setFont(new Font("Tahoma", Font.PLAIN, 13));
		msg_area.setEditable(false);
		msg_area.setBounds(24, 13, 470, 232);
		contentPane.add(msg_area);
		JScrollPane scrollPane = new JScrollPane(msg_area);
	
		scrollPane.setBounds(24, 71, 470, 174);
		contentPane.add(scrollPane);
		lblNewLabel = new JLabel("");
		
		lblNewLabel.setBounds(0, 2, 514, 338);
		contentPane.add(lblNewLabel);
		
		close_btn = new JButton("Close");
		close_btn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		close_btn.setHorizontalAlignment(SwingConstants.LEFT);
		close_btn.setFont(new Font("Tahoma", Font.BOLD, 12));
		close_btn.setBounds(397, 266, 100, 36);
		contentPane.add(close_btn);
		

	}
	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					TCPServer_TransferFile frame = new TCPServer_TransferFile();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		
	}
}