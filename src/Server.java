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


public class Server {

	private static ServerSocket ss;
	private static Socket s;
	private static BufferedReader br;
	private static InputStreamReader isr;
	private static String message;
	private static PrintWriter pw;	
	static JSONParser parser = new JSONParser();

	/**
	 * 1) store ekleme
	 * 2) 
	 */
	public static void main(String[] args) throws ParseException {

		try {
			ss = new ServerSocket(6000);

			while(true) {

				try {
					System.out.println("message is :");
					s = ss.accept();

					System.out.println("message is2 :");

					isr = new InputStreamReader(s.getInputStream());
					br = new BufferedReader(isr);
					message = br.readLine();

					System.out.println(message);

					GsonBuilder builder = new GsonBuilder(); 
					builder.setPrettyPrinting(); 

					Gson gson = builder.create(); 
					Retailer retailer = gson.fromJson(message, Retailer.class);  
					
					//retailer

					if(retailer.retailerName != null) {
						System.out.print(retailer.retailerName);
						if(retailer.retailerEmail != null) {

							File filex = new File("users.json");
							if(filex.length() == 0) {
								try (FileWriter file = new FileWriter("users.json", true)) {


									JSONArray jsonArray = new JSONArray();

									jsonArray.add(gson.toJson(retailer));

									file.write(jsonArray.toJSONString());
									file.flush();

								}
								catch (IOException e) {
									e.printStackTrace();
								}
							}

							else {

								Object obj = parser.parse(new FileReader("users.json"));
								System.out.print("new signup");

								JSONArray jsonArray = (JSONArray)obj;

								jsonArray.add(gson.toJson(retailer));
								try (FileWriter file = new FileWriter("users.json")) {


									file.write(jsonArray.toJSONString());
									file.flush();

								}
								catch (IOException e) {
									e.printStackTrace();
								}

							}

						} 

						else {
							Object obj = parser.parse(new FileReader("users.json"));

							JSONArray jsonArray = (JSONArray) obj;

							System.out.println("login :"+ jsonArray.get(0).toString());
						}
					}

					Store store = gson.fromJson(message, Store.class);  
					
					//store

					if(store.storeName != null) {
						System.out.print(store.storeName);
						if(store.storeId == -1) {

							File filex = new File("stores.json");
							if(filex.length() == 0) {
								try (FileWriter file = new FileWriter("stores.json", true)) {


									JSONArray jsonArray = new JSONArray();

									jsonArray.add(gson.toJson(store));

									file.write(jsonArray.toJSONString());
									file.flush();

								}
								catch (IOException e) {
									e.printStackTrace();
								}
							}

							else {

								Object obj = parser.parse(new FileReader("stores.json"));
								System.out.print("new add store");

								JSONArray jsonArray = (JSONArray)obj;

								jsonArray.add(gson.toJson(store));
								try (FileWriter file = new FileWriter("stores.json")) {


									file.write(jsonArray.toJSONString());
									file.flush();

								}
								catch (IOException e) {
									e.printStackTrace();
								}

							}

						} 

						else {
							Object obj = parser.parse(new FileReader("stores.json"));

							JSONArray jsonArray = (JSONArray) obj;

							System.out.println("store :"+ jsonArray.get(0).toString());
						}
					}

					Discount discount = gson.fromJson(message, Discount.class);  

					if(discount.discountProduct != null) {
						System.out.print(discount.discountProduct);
						try (FileWriter file = new FileWriter("discounts.json", true)) {

							file.write(message);
							file.flush();

						} catch (IOException e) {
							e.printStackTrace();
						}
					}


					//Write JSON file

					pw = new PrintWriter(s.getOutputStream());
					pw.write(message);
					pw.flush();
					pw.close();
					br.close();
				}
				catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}



			}


		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
