import com.google.gson.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


public class Server extends Thread{

	private static Socket s;
	private static BufferedReader br;
	private static InputStreamReader isr;
	private static String message;
	private static PrintWriter pw;	
	static JSONParser parser = new JSONParser();
	
	Server(Socket socket) {
        this.s = socket;
        try {
        	isr = new InputStreamReader(s.getInputStream());
			br = new BufferedReader(isr);
			pw = new PrintWriter(s.getOutputStream());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

	/**
	 * 1) store ekleme
	 * 2) 
	 */
	@Override 
	public void run() {

			try {
				System.out.println("message is :");
			
				message = br.readLine();

				System.out.println(message);
				String outMessage = "404";

				GsonBuilder builder = new GsonBuilder(); 
				builder.setPrettyPrinting(); 

				Gson gson = builder.create(); 
				
				String first = message.substring(0, 3);
				message = message.substring(3);
				
				if(first.equals("100")) {
					outMessage = Retailer.createRetailer(message, parser, gson);
				}
				else if(first.contentEquals("101")) {
					outMessage = Store.createStore(message, parser, gson);
				}
				else if(first.equals("102")) {
					outMessage = Discount.createDiscount(message, parser, gson);
				}
				else if(first.equals("103")) {
					outMessage = Retailer.login(message, parser, gson);
				}
				else if(first.equals("104")) {
					outMessage = Store.getAllStores(parser, gson);
				}
				else if(first.equals("105")) {
					outMessage = Store.getStoresOfRetailer(message, parser, gson);
				}
				else if(first.equals("106")) {
					outMessage = Discount.getDiscountOfStore(message, parser, gson);
				}
				else if(first.equals("107")) {
					outMessage = Discount.deleteDiscount(message, parser, gson);
				}
				else if(first.equals("108")) {
					outMessage = Store.deleteStore(message, parser, gson);
				}
				else if(first.equals("109")) {
					outMessage = Store.addOwnership(message, parser, gson);
				}

				//Write JSON file
				System.out.println(outMessage);

				//pw = new PrintWriter(s.getOutputStream());
				sendMessage(outMessage);
				br.close();
				s.close();
			}
			catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
	
	public void sendMessage(String msg) {
		pw.write(msg+"\n");
		pw.flush();
		pw.close();
		
	}
	/*
	 * atilacak mesajlar
	 * 1) getStoresofRetailer
	 * 2) getDiscountsofStore
	 * 3) getAllStores
	 */
}
