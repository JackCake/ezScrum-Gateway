package ntut.csie.ezScrum.controller.delegator;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class BacklogItemImportanceDelegator {
	private static String baseURL = "http://localhost:8080/backlogItemImportanceService";
	private Client client;
	
	public BacklogItemImportanceDelegator(Client client) {
		this.client = client;
	}
	
	public Response addBacklogItemImportance(String backlogItemId, int importance) throws JSONException {
		JSONObject backlogItemImportanceJSON = new JSONObject();
		backlogItemImportanceJSON.put("backlogItemId", backlogItemId);
		backlogItemImportanceJSON.put("importance", importance);
		
		Response response = client.target(baseURL)
		        .path("/backlog_item_importances")
		        .request()
		        .post(Entity.json(backlogItemImportanceJSON.toString()));
		return response;
	}
	
	public JSONObject getBacklogItemImportanceByBacklogItemId(String backlogItemId) throws JSONException {
		JSONObject backlogItemImportanceJSON = null;
		
		Response response = client.target(baseURL)
		        .path("/backlog_item_importances/" + backlogItemId)
		        .request()
		        .get();
		
		JSONObject backlogItemImportanceOutputJSON = new JSONObject(response.readEntity(String.class));
		backlogItemImportanceJSON = backlogItemImportanceOutputJSON.getJSONObject("backlogItemImportance");
		return backlogItemImportanceJSON;
	}
	
	public Response editBacklogItemImportance(String backlogItemId, int importance) throws JSONException {
		JSONObject backlogItemImportanceJSON = new JSONObject();
		backlogItemImportanceJSON.put("importance", importance);
		
		Response response = client.target(baseURL)
		        .path("/backlog_item_importances/" + backlogItemId)
		        .request()
		        .put(Entity.json(backlogItemImportanceJSON.toString()));
		return response;
	}
	
	public Response deleteBacklogItemImportance(String backlogItemId) {
		Response response = client.target(baseURL)
		        .path("/backlog_item_importances/" + backlogItemId)
		        .request()
		        .delete();
		return response;
	}
	
	public JSONArray getHistoriesByBacklogItemId(String backlogItemId) throws JSONException {
		JSONArray workItemHistoriesJSON = null;
		
		Response response = client.target(baseURL)
		        .path("/backlog_item_importances/" + backlogItemId + "/histories")
		        .request()
		        .get();
		
		JSONObject workItemHistoryJSON = new JSONObject(response.readEntity(String.class));
		workItemHistoriesJSON = workItemHistoryJSON.getJSONArray("historyList");
		return workItemHistoriesJSON;
	}
}
