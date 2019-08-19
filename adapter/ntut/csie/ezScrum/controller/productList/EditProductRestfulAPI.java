package ntut.csie.ezScrum.controller.productList;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.JSONException;
import org.json.JSONObject;

import ntut.csie.ezScrum.ApplicationContext;
import ntut.csie.ezScrum.controller.delegator.ProductDelegator;

@Path("/products")
@Singleton
public class EditProductRestfulAPI {
	private ApplicationContext applicationContext = ApplicationContext.getInstance();
	private ProductDelegator productDelegator = applicationContext.newProductDelegator();

	@PUT
	@Path("/{product_id}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public synchronized String editProduct(
			@PathParam("product_id") String productId, 
			String productInfo) {
		JSONObject editProductOutput = new JSONObject();
		try {
			JSONObject productJSON = new JSONObject(productInfo);
			String name = productJSON.getString("name");
			
			Response response = productDelegator.editProduct(productId, name);
			boolean editSuccess = response.getStatus() == Response.Status.OK.getStatusCode();
			editProductOutput.put("editSuccess", editSuccess);
			if(editSuccess) {
				editProductOutput.put("errorMessage", "");
			} else {
				editProductOutput.put("errorMessage", "Sorry, please try again!");
			}
		} catch (JSONException e) {
			e.printStackTrace();
			Map<String, Object> editProductOutputMap = new HashMap<>();
			editProductOutputMap.put("editSuccess", false);
			editProductOutputMap.put("errorMessage", "Sorry, please try again!");
			JSONObject editProductOutputJSON = new JSONObject(editProductOutputMap);
			return editProductOutputJSON.toString();
		}
		return editProductOutput.toString();
	}
}
