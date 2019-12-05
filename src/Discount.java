import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.google.gson.Gson;

public class Discount {
	public String discountProduct;
	public int discountPercent;
	public int discountId;
	public int storeId;
	public static BinarySemaphore discountMutex = new BinarySemaphore();

	public Discount(String product, int discountPercent) {
		this.discountProduct = product;
		this.discountPercent = discountPercent;
	}

	public Discount(String product, int discountPercent, int id) {
		this.discountProduct = product;
		this.discountPercent = discountPercent;
		this.discountId = id;
	}

	public Discount(int discountId) {
		this.discountId = discountId;
	}
	
	//eklerken en alttakinden bir fazla id
	public static String createDiscount(String message, JSONParser parser, Gson gson) {
		
		
		Discount discount = gson.fromJson(message, Discount.class);  

		if(discount.discountProduct != null) {
			
			File filex = new File("discounts.json");
			discountMutex.P();
			if(filex.length() == 0) {
				
				try (FileWriter file = new FileWriter("discounts.json", true)) {
					
					
					JSONArray jsonArray = new JSONArray();
					int count = jsonArray.size();
					discount.discountId = count;
					jsonArray.add(gson.toJson(discount));

					file.write(jsonArray.toJSONString());
					file.flush();
					discountMutex.V();
					
					return "102";

				}
				catch (IOException e) {
					discountMutex.V();
					e.printStackTrace();
				}
			}

			else {

				try {
					
					Object obj = parser.parse(new FileReader("discounts.json"));

					JSONArray jsonArray = (JSONArray)obj;
					int count = jsonArray.size();
					if(count> 0) {
						Discount d = gson.fromJson((String) jsonArray.get(count - 1), Discount.class);
						
						discount.discountId = d.discountId + 1 ;
					}
					else {
						discount.discountId = 0 ;
					}
					

					jsonArray.add(gson.toJson(discount));
					try (FileWriter file = new FileWriter("discounts.json")) {


						file.write(jsonArray.toJSONString());
						file.flush();
						
						discountMutex.V();
						return "102";

					}
					catch (IOException e) {
						discountMutex.V(); //??????????
						e.printStackTrace();
					}
				} catch (IOException | ParseException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

			}

		}
		return "404";
	}

	public static String getDiscount(String message, JSONParser parser, Gson gson) {

		Discount discountMessage = gson.fromJson(message, Discount.class);
		try {
			Object obj = parser.parse(new FileReader("discounts.json"));
			JSONArray jsonArray = (JSONArray) obj;

			for(int i = 0; i< jsonArray.size();i++) {
				Discount discount = gson.fromJson((String) jsonArray.get(i), Discount.class);
				if(discount.storeId == discountMessage.storeId && discount.discountProduct.equals( discountMessage.discountProduct)) {
					return "200" + jsonArray.get(i);
				}
			}
		} catch (IOException | ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "404";
	}

	public static String getDiscountOfStore(String message, JSONParser parser, Gson gson) {
		File filex = new File("discounts.json");
		if(filex.length() == 0) {
			return "106";
		}
		
		Store store = gson.fromJson(message, Store.class);
		try {
			Object obj = parser.parse(new FileReader("discounts.json"));
			JSONArray jsonArray = (JSONArray) obj;
			JSONArray discounts = new JSONArray();

			for(int i = 0; i< jsonArray.size();i++) {
				Discount discount = gson.fromJson((String) jsonArray.get(i), Discount.class);
				if(discount.storeId == store.storeId) {
					discounts.add(jsonArray.get(i));
				}
			}
			return "106" + discounts;
		} catch (IOException | ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "404";
	}
	
	public static String deleteDiscount(String message, JSONParser parser, Gson gson) {
		Discount discountMessage = gson.fromJson(message, Discount.class);
		try {
			discountMutex.P();
			Object obj = parser.parse(new FileReader("discounts.json"));
			JSONArray jsonArray = (JSONArray) obj;
			JSONArray discounts = new JSONArray();
			

			for(int i = 0; i< jsonArray.size();i++) {
				Discount discount = gson.fromJson((String) jsonArray.get(i), Discount.class);
				if(discount.discountId != discountMessage.discountId) {
					discounts.add(jsonArray.get(i));
				}
				else if(!discount.discountProduct.equals(discountMessage.discountProduct)){
					discounts.add(jsonArray.get(i));
				}
			}
			
			try (FileWriter file = new FileWriter("discounts.json")) {


				file.write(discounts.toJSONString());
				file.flush();
				discountMutex.V();
				return "107";

			}
			catch (IOException e) {
				discountMutex.V();
				e.printStackTrace();
			}
			
		} catch (IOException | ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "404";
	}

}
