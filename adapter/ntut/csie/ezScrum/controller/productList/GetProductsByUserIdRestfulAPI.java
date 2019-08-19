package ntut.csie.ezScrum.controller.productList;

import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ntut.csie.ezScrum.ApplicationContext;
import ntut.csie.ezScrum.controller.delegator.ProductDelegator;

@Path("/products")
@Singleton
public class GetProductsByUserIdRestfulAPI {
	private ApplicationContext applicationContext = ApplicationContext.getInstance();
	private ProductDelegator productDelegator = applicationContext.newProductDelegator();
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public synchronized String getProductsByUserId() {
		JSONObject getProductsByUserIdOutput = new JSONObject();
		try {
			JSONArray boardList = productDelegator.getProductsByUserId("1");
			JSONArray productList = new JSONArray();
			for(int i = 0; i < boardList.length(); i++) {
				JSONObject boardJSON = boardList.getJSONObject(i);
				JSONObject productJSON = new JSONObject();
				productJSON.put("productId", boardJSON.getString("boardId"));
				productJSON.put("orderId", i + 1);
				productJSON.put("name", boardJSON.getString("name"));
				productList.put(productJSON);
			}
			getProductsByUserIdOutput.put("productList", productList);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return getProductsByUserIdOutput.toString();
	}
}
