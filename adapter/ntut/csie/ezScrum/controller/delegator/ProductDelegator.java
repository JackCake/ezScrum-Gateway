package ntut.csie.ezScrum.controller.delegator;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ProductDelegator {
	private static String baseURL = "http://localhost:8080/kanban";
	private Client client;
	
	public ProductDelegator(Client client) {
		this.client = client;
	}
	
	public Response addProduct(String name, String userId) throws JSONException {
		JSONObject productJSON = new JSONObject();
		productJSON.put("name", name);
		productJSON.put("userId", userId);
		
		Response response = client.target(baseURL)
		        .path("/board/addBoard")
		        .request()
		        .post(Entity.json(productJSON.toString()));
		return response;
	}
	
	public JSONArray getProductsByUserId(String userId) throws JSONException {
		JSONArray productsJSON = null;
		
		Response response = client.target(baseURL)
		        .path("/board/getBoardsByUserId/" + userId)
		        .request()
		        .get();
		
		JSONObject productJSON = new JSONObject(response.readEntity(String.class));
		productsJSON = productJSON.getJSONArray("boardList");
		return productsJSON;
	}
	
	public Response editProduct(String productId, String name) throws JSONException {
		JSONObject productJSON = new JSONObject();
		productJSON.put("boardId", productId);
		productJSON.put("name", name);
		
		Response response = client.target(baseURL)
		        .path("/board/editBoard")
		        .request()
		        .post(Entity.json(productJSON.toString()));
		return response;
	}
	
	public Response deleteProduct(String productId) throws JSONException {
		JSONObject productJSON = new JSONObject();
		productJSON.put("boardId", productId);
		
		Response response = client.target(baseURL)
		        .path("/board/deleteBoard")
		        .request()
		        .method("DELETE", Entity.json(productJSON.toString()));
		return response;
	}
	
	public Response addStage(String title, String boardId) throws JSONException {
		JSONObject productJSON = new JSONObject();
		productJSON.put("title", title);
		productJSON.put("boardId", boardId);
		
		Response response = client.target(baseURL)
		        .path("/stage/addStage")
		        .request()
		        .post(Entity.json(productJSON.toString()));
		return response;
	}
	
	public JSONArray getStagesInBoardByProductId(String productId) throws JSONException {
		JSONArray stagesJSON = null;
		
		Response response = client.target(baseURL)
		        .path("/stage/getStagesByBoardId/" + productId)
		        .request()
		        .get();
		
		JSONObject getStagesByBoardIdOutputJSON = new JSONObject(response.readEntity(String.class));
		stagesJSON = getStagesByBoardIdOutputJSON.getJSONArray("stageList");
		return stagesJSON;
	}
}
