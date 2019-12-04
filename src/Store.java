import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.google.gson.Gson;

public class Store {

	public int storeId;
	public double storeLatitude;
	public double storeLongitude;
	public String storeName;
	public String storeAddress;
	public ArrayList<Integer> retailerId;
	public static BinarySemaphore storeMutex = new BinarySemaphore();

	public Store(double latitude, double longitude, String name, String address){
		this.storeLatitude = latitude;
		this.storeLongitude = longitude;
		this.storeName = name;
		this.storeAddress =address;
	}

	Store(int id, double latitude, double longitude, String name, String address) {
		this.storeLatitude = latitude;
		this.storeLongitude = longitude;
		this.storeName = name;
		this.storeId = id;
		this.storeAddress = address;
	}
	public static String createStore(String message, JSONParser parser, Gson gson) {

		Store store = gson.fromJson(message, Store.class);  


		if(store.storeName != null) {
			System.out.println(store.storeName);


			File filex = new File("stores.json");
			storeMutex.P();
			if(filex.length() == 0) {
				try (FileWriter file = new FileWriter("stores.json", true)) {

					JSONArray jsonArray = new JSONArray();
					store.storeId = 0;

					jsonArray.add(gson.toJson(store));

					file.write(jsonArray.toJSONString());
					file.flush();
					storeMutex.V();
					DiscountServer.updateAllStores();
					return "101";

				}
				catch (IOException e) {
					storeMutex.V();
					e.printStackTrace();
				}
			}

			else {

				try {
					Object obj = parser.parse(new FileReader("stores.json"));
					System.out.print("new add store");

					JSONArray jsonArray = (JSONArray)obj;
					int count = jsonArray.size();
					if(count> 0) {
						Store d = gson.fromJson((String) jsonArray.get(count - 1), Store.class);
						
						store.storeId = d.storeId + 1 ;
					}
					else {
						store.storeId = 0 ;
					}

					jsonArray.add(gson.toJson(store));

					try (FileWriter file = new FileWriter("stores.json")) {


						file.write(jsonArray.toJSONString());
						file.flush();
						storeMutex.V();
						DiscountServer.updateAllStores();
						return "101";

					}
					catch (IOException e) {
						storeMutex.V();
						e.printStackTrace();
					}


				} catch (IOException | ParseException e1) {
					// TODO Auto-generated catch block
					storeMutex.V();
					e1.printStackTrace();
				}

			} 

		}
		return "404";
	}

	public static String getStore(String message, JSONParser parser, Gson gson) {


		try {
			Store storeMessage = gson.fromJson(message, Store.class);
			Object obj = parser.parse(new FileReader("stores.json"));
			JSONArray jsonArray = (JSONArray) obj;
			for( int i = 0 ; i< jsonArray.size(); i++) {
				Store store = gson.fromJson((String) jsonArray.get(i), Store.class);
				if(store.storeName.equals(storeMessage.storeName)) {
					return "200"+ jsonArray.get(i);
				}
			}

		} catch (IOException | ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "404";
	}

	public static String getStoresOfRetailer(String message, JSONParser parser, Gson gson) {
		File filex = new File("stores.json");
		if(filex.length() == 0) {
			return "105";
		} 
		try {
			Retailer retailer = gson.fromJson(message, Retailer.class);
			Object obj = parser.parse(new FileReader("stores.json"));

			JSONArray jsonArray = (JSONArray) obj;
			JSONArray stores = new JSONArray();

			for( int i = 0 ; i< jsonArray.size(); i++) {
				Store store = gson.fromJson((String) jsonArray.get(i), Store.class);
				if(store.retailerId.contains(retailer.retailerId)) {
					stores.add(jsonArray.get(i));
				}
			}

			return "105" + stores;

		} catch (IOException | ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "404";
	}

	public static String getAllStores(JSONParser parser, Gson gson) {
		File filex = new File("stores.json");
		if(filex.length() == 0) {
			return "104";
		}
		try {

			Object obj = parser.parse(new FileReader("stores.json"));

			JSONArray jsonArray = (JSONArray) obj;

			return "104" + jsonArray;

		} catch (IOException | ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "404";
	}
	
	public static String deleteStore(String message, JSONParser parser, Gson gson) {
	
		String storemessage = message.substring(0, message.indexOf('#'));
		Store storeMes = gson.fromJson(storemessage, Store.class);
		message = message.substring(message.indexOf('#')+1);
		int id = Integer.parseInt(message);
		
		try {
			storeMutex.P();
			Object obj = parser.parse(new FileReader("stores.json"));
			JSONArray jsonArray = (JSONArray) obj;
			JSONArray stores = new JSONArray();
			
			for(int i = 0; i< jsonArray.size();i++) {
				Store store = gson.fromJson((String) jsonArray.get(i), Store.class);
				if(store.storeId != storeMes.storeId) {
					stores.add(gson.toJson(store));
				}
				else {
					if(store.retailerId.size()>1) {
						ArrayList<Integer> a = store.retailerId;
						store.retailerId.clear();
						for(int z = 0;z< a.size(); z++) {
							int k = a.get(i);
							if(k != id) {
								store.retailerId.add(k);
							}
						}
						stores.add(gson.toJson(store));
					}
				}
			}
			
			Discount.discountMutex.P();
			Object obj2 = parser.parse(new FileReader("discounts.json"));
			JSONArray jsonArray2 = (JSONArray) obj2;
			JSONArray discounts = new JSONArray();
			
			for(int i = 0; i< jsonArray2.size();i++) {
				Discount discount = gson.fromJson((String) jsonArray2.get(i), Discount.class);
				if(discount.storeId != storeMes.storeId) {
					discounts.add(jsonArray2.get(i));
				}
			}
			
			try (FileWriter file = new FileWriter("stores.json")) {


				file.write(stores.toJSONString());
				file.flush();
				storeMutex.V();

			}
			
			try (FileWriter file = new FileWriter("discounts.json")) {


				file.write(discounts.toJSONString());
				file.flush();
				Discount.discountMutex.V();
				DiscountServer.updateAllStores();
				return "108";

			}
			catch (IOException e) {
				e.printStackTrace();
				storeMutex.V();
				Discount.discountMutex.V();
			}
			
		} catch (IOException | ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "404";
	}
	
	public static String addOwnership(String message, JSONParser parser, Gson gson) {
		String storemessage = message.substring(0, message.indexOf('#'));
		Store storeMes = gson.fromJson(storemessage, Store.class);
		message = message.substring(message.indexOf('#')+1);
		
		try {
			storeMutex.P();
			Object obj = parser.parse(new FileReader("stores.json"));
			JSONArray jsonArray = (JSONArray) obj;
			JSONArray stores = new JSONArray();
			
			for(int i = 0; i< jsonArray.size();i++) {
				Store store = gson.fromJson((String) jsonArray.get(i), Store.class);
				if(store.storeId == storeMes.storeId) {
					store.retailerId.add(Integer.parseInt(message));
				}
				stores.add(gson.toJson(store));
			}
			
			
			try (FileWriter file = new FileWriter("stores.json")) {


				file.write(stores.toJSONString());
				file.flush();
				DiscountServer.updateAllStores();
				storeMutex.V();
				return "109";

			}
			
			catch (IOException e) {
				storeMutex.V();
				e.printStackTrace();
			}
			
		} catch (IOException | ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "404";
	}
}

