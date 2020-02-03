package ntut.csie.ezScrum.controller.productList;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.JSONException;
import org.json.JSONObject;

import ntut.csie.ezScrum.ApplicationContext;
import ntut.csie.ezScrum.controller.delegator.ProductDelegator;

@Path("/products")
@Singleton
public class AddProductRestfulAPI {
	private ApplicationContext applicationContext = ApplicationContext.getInstance();
	private ProductDelegator productDelegator = applicationContext.newProductDelegator();

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public synchronized String addProduct(String productInfo) throws JSONException {
		JSONObject addProductOutput = new JSONObject();
		try {
			JSONObject productJSON = new JSONObject(productInfo);
			String name = productJSON.getString("name");
			String userId = productJSON.getString("userId");
			
			Response addProductResponse = productDelegator.addProduct(name, userId);
			boolean addProductSuccess = addProductResponse.getStatus() == Response.Status.OK.getStatusCode();
			if(!addProductSuccess) {
				addProductOutput.put("addSuccess", addProductSuccess);
				addProductOutput.put("errorMessage", "Sorry, it is not successful when add the product. Please contact to the system administrator!");
				return addProductOutput.toString();
			}
			
			JSONObject addBoardOutputJSON = new JSONObject(addProductResponse.readEntity(String.class));
			String boardId = addBoardOutputJSON.getString("boardId");
			
			String[] stageTitles = {"To do", "Doing", "Done"};
			for(String stageTitle : stageTitles) {
				Response addStageResponse = productDelegator.addStage(stageTitle, boardId);
				boolean addStageSuccess = addStageResponse.getStatus() == Response.Status.OK.getStatusCode();
				if(!addStageSuccess) {
					productDelegator.deleteProduct(boardId);
					addProductOutput.put("addSuccess", addStageSuccess);
					addProductOutput.put("errorMessage", "Sorry, it is not successful when create the task board in the sprint backlog of the product. Please contact to the system administrator!");
					return addProductOutput.toString();
				}
			}
			addProductOutput.put("addSuccess", true);
			addProductOutput.put("errorMessage", "");
		} catch (JSONException e) {
			e.printStackTrace();
			Map<String, Object> addProductOutputMap = new HashMap<>();
			addProductOutputMap.put("addSuccess", false);
			addProductOutputMap.put("errorMessage", "Sorry, there is the problem when add the product. Please contact to the system administrator!");
			JSONObject addProductOutputJSON = new JSONObject(addProductOutputMap);
			return addProductOutputJSON.toString();
		}
		
		return addProductOutput.toString();
	}
}
