import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.google.gson.Gson;

public class Retailer {
	public String retailerName;
	public String retailerEmail;
	public String retailerPassword;
	public int retailerId;
	public static BinarySemaphore retailerMutex = new BinarySemaphore();

	public Retailer(String email, String userName, String password) {
		this.retailerEmail = email;
		this.retailerName = userName;
		this.retailerPassword = password;
	}

	public Retailer(String userName, String password) {
		this.retailerName = userName;
		this.retailerPassword = password;
	}

	public Retailer(String email, String userName, String password, int id) {
		this.retailerEmail = email;
		this.retailerName = userName;
		this.retailerPassword = password;
		this.retailerId = id;
	}
	public static String createRetailer(String message, JSONParser parser, Gson gson) {

		Retailer retailer = gson.fromJson(message, Retailer.class);  

		//retailer

		if(retailer.retailerName != null) {
			
			if(retailer.retailerEmail != null) {
				
				File filex = new File("users.json");
				retailerMutex.P();
				if(filex.length() == 0) {
					try (FileWriter file = new FileWriter("users.json", true)) {


						JSONArray jsonArray = new JSONArray();
						int idCounter = jsonArray.size();
						
						retailer.retailerId = idCounter;

						jsonArray.add(gson.toJson(retailer));

						file.write(jsonArray.toJSONString());
						file.flush();
						retailerMutex.V();
						return "100";

					}
					catch (IOException e) {
						retailerMutex.V();
						e.printStackTrace();
					}
				}

				else {

					try {
						Object obj = parser.parse(new FileReader("users.json"));

						System.out.print("new signup");

						JSONArray jsonArray = (JSONArray)obj;
						int idCounter = jsonArray.size();
						
						retailer.retailerId = idCounter;

						jsonArray.add(gson.toJson(retailer));
						try (FileWriter file = new FileWriter("users.json")) {


							file.write(jsonArray.toJSONString());
							
							file.flush();
							retailerMutex.V();
							return "100";

						}
						catch (IOException e) {
							retailerMutex.V();
							e.printStackTrace();
						}
					} catch (IOException | ParseException e1) {
						// TODO Auto-generated catch block
						retailerMutex.V();
						e1.printStackTrace();
					}


				}

			} 

		}
		return "404";
	}

	public static String login(String message, JSONParser parser, Gson gson) {

		try {
			
			Retailer retailerMessage = gson.fromJson(message, Retailer.class);

			Object obj = parser.parse(new FileReader("users.json"));
			JSONArray jsonArray = (JSONArray) obj;
			
			for(int i = 0 ; i< jsonArray.size() ; i++) {
				Retailer retailer = gson.fromJson((String) jsonArray.get(i), Retailer.class);
				if(retailer.retailerName.equals(retailerMessage.retailerName) && retailer.retailerPassword.equals(retailerMessage.retailerPassword)) {
					Retailer ret = gson.fromJson((String) jsonArray.get(i), Retailer.class);
					return "103" + (String)gson.toJson(ret);
				}
			}
			return "404";

		} catch (IOException | ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "404";

	}
}
