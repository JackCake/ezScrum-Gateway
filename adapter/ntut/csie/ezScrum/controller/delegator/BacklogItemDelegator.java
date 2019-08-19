package ntut.csie.ezScrum.controller.delegator;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class BacklogItemDelegator {
	private static String baseURL = "http://localhost:8080/kanban";
	private Client client;
	
	public BacklogItemDelegator(Client client) {
		this.client = client;
	}
	
	public Response addBacklogItem(String description, int estimate, String notes, String swimLaneId, String miniStageId, String stageId) throws JSONException {
		JSONObject workItemJSON = new JSONObject();
		workItemJSON.put("description", description);
		workItemJSON.put("estimate", estimate);
		workItemJSON.put("notes", notes);
		workItemJSON.put("deadline", "");
		workItemJSON.put("swimLaneId", swimLaneId);
		workItemJSON.put("miniStageId", miniStageId);
		workItemJSON.put("stageId", stageId);
		workItemJSON.put("categoryId", "");
		workItemJSON.put("userId", "");
		
		Response response = client.target(baseURL)
		        .path("/workItem/addWorkItem")
		        .request()
		        .post(Entity.json(workItemJSON.toString()));
		return response;
	}
	
	public JSONArray getWorkItemsBySwimLaneId(String swimLaneId) throws JSONException {
		JSONArray workItemsJSON = null;
		
		Response response = client.target(baseURL)
		        .path("/workItem/getWorkItemsBySwimLaneId/" + swimLaneId)
		        .request()
		        .get();
		
		JSONObject workItemJSON = new JSONObject(response.readEntity(String.class));
		workItemsJSON = workItemJSON.getJSONArray("workItemList");
		return workItemsJSON;
	}
	
	public Response editBacklogItem(String workItemId, String description, int estimate, String notes) throws JSONException {
		JSONObject workItemJSON = new JSONObject();
		workItemJSON.put("workItemId", workItemId);
		workItemJSON.put("description", description);
		workItemJSON.put("estimate", estimate);
		workItemJSON.put("notes", notes);
		workItemJSON.put("deadline", "");
		workItemJSON.put("categoryId", "");
		workItemJSON.put("userId", "");
		
		Response response = client.target(baseURL)
		        .path("/workItem/editWorkItem")
		        .request()
		        .post(Entity.json(workItemJSON.toString()));
		return response;
	}
	
	public Response deleteBacklogItem(String backlogItemId, String swimLaneId, String miniStageId, String stageId) throws JSONException {
		JSONObject workItemJSON = new JSONObject();
		workItemJSON.put("workItemId", backlogItemId);
		workItemJSON.put("swimLaneId", swimLaneId);
		workItemJSON.put("miniStageId", miniStageId);
		workItemJSON.put("stageId", stageId);
		
		Response response = client.target(baseURL)
		        .path("/workItem/deleteWorkItem")
		        .request()
		        .method("DELETE", Entity.json(workItemJSON.toString()));
		return response;
	}
	
	public Response moveBacklogItem(String boardId, 
			String originalStageId, 
			String newStageId, 
			String originalSwimLaneId, 
			String newSwimLaneId, 
			String workItemId
		) throws JSONException {
		JSONObject workItemJSON = new JSONObject();
		workItemJSON.put("boardId", boardId);
		workItemJSON.put("originalStageId", originalStageId);
		workItemJSON.put("newStageId", newStageId);
		workItemJSON.put("originalSwimLaneId", originalSwimLaneId);
		workItemJSON.put("newSwimLaneId", newSwimLaneId);
		workItemJSON.put("workItemId", workItemId);
		
		Response response = client.target(baseURL)
		        .path("/workItem/moveWorkItem")
		        .request()
		        .post(Entity.json(workItemJSON.toString()));
		return response;
	}
	
	public JSONArray getHistoriesByBacklogItemId(String workItemId) throws JSONException {
		JSONArray workItemHistoriesJSON = null;
		
		Response response = client.target(baseURL)
		        .path("/workItem/getHistoryByWorkItemId/" + workItemId)
		        .request()
		        .get();
		
		JSONObject workItemHistoryJSON = new JSONObject(response.readEntity(String.class));
		workItemHistoriesJSON = workItemHistoryJSON.getJSONArray("historyList");
		return workItemHistoriesJSON;
	}
}
