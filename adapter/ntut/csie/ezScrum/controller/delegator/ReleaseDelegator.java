package ntut.csie.ezScrum.controller.delegator;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ReleaseDelegator {
	private static String baseURL = "http://localhost:8080/releaseService";
	private Client client;
	
	public ReleaseDelegator(Client client) {
		this.client = client;
	}
	
	public Response addRelease(String name, String startDate, String endDate, 
			String description, String productId) throws JSONException {
		JSONObject releaseJSON = new JSONObject();
		releaseJSON.put("name", name);
		releaseJSON.put("startDate", startDate);
		releaseJSON.put("endDate", endDate);
		releaseJSON.put("description", description);
		
		Response response = client.target(baseURL)
		        .path("/products/" + productId + "/releases")
		        .request()
		        .post(Entity.json(releaseJSON.toString()));
		return response;
	}
	
	public JSONArray getReleasesByProductId(String productId) throws JSONException {
		JSONArray releasesJSON = null;
		
		Response response = client.target(baseURL)
		        .path("/products/" + productId + "/releases")
		        .request()
		        .get();

		JSONObject releaseJSON = new JSONObject(response.readEntity(String.class));
		releasesJSON = releaseJSON.getJSONArray("releaseList");
		return releasesJSON;
	}
	
	public Response editRelease(String releaseId, String name, String startDate, 
			String endDate, String description) throws JSONException {
		JSONObject releaseJSON = new JSONObject();
		releaseJSON.put("name", name);
		releaseJSON.put("startDate", startDate);
		releaseJSON.put("endDate", endDate);
		releaseJSON.put("description", description);
		
		Response response = client.target(baseURL)
				.path("/releases/" + releaseId)
		        .request()
		        .put(Entity.json(releaseJSON.toString()));
		return response;
	}
	
	public Response deleteRelease(String releaseId) throws JSONException {
		Response response = client.target(baseURL)
				.path("/releases/" + releaseId)
		        .request()
		        .delete();
		return response;
	}
	
	public Response scheduleBacklogItemToRelease(String backlogItemId, String releaseId) throws JSONException {
		JSONObject scheduledBacklogItemJSON = new JSONObject();
		scheduledBacklogItemJSON.put("backlogItemId", backlogItemId);
		
		Response response = client.target(baseURL)
		        .path("/releases/" + releaseId + "/scheduled_backlog_items")
		        .request()
		        .post(Entity.json(scheduledBacklogItemJSON.toString()));
		return response;
	}
	
	public JSONArray getScheduledBacklogItemsByReleaseId(String releaseId) throws JSONException {
		JSONArray scheduledBacklogItemsJSON = null;
		
		Response response = client.target(baseURL)
				.path("/releases/" + releaseId + "/scheduled_backlog_items")
		        .request()
		        .get();
		
		JSONObject scheduledBacklogItemJSON = new JSONObject(response.readEntity(String.class));
		scheduledBacklogItemsJSON = scheduledBacklogItemJSON.getJSONArray("scheduledBacklogItemList");
		return scheduledBacklogItemsJSON;
	}
	
	public Response unscheduleBacklogItemFromRelease(String backlogItemId, String releaseId) {
		Response response = client.target(baseURL)
				.path("/releases/" + releaseId + "/scheduled_backlog_items/" + backlogItemId)
		        .request()
		        .delete();
		return response;
	}
	
	public JSONArray getHistoriesByBacklogItemId(String backlogItemId) throws JSONException {
		JSONArray workItemHistoriesJSON = null;
		
		Response response = client.target(baseURL)
		        .path("/scheduled_backlog_items/" + backlogItemId + "/histories")
		        .request()
		        .get();
		
		JSONObject workItemHistoryJSON = new JSONObject(response.readEntity(String.class));
		workItemHistoriesJSON = workItemHistoryJSON.getJSONArray("historyList");
		return workItemHistoriesJSON;
	}
}
