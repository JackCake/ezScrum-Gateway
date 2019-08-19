package ntut.csie.ezScrum.controller.delegator;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SprintDelegator {
	private static String baseURL = "http://localhost:8080/sprintService";
	private Client client;
	
	public SprintDelegator(Client client) {
		this.client = client;
	}
	
	public Response addSprint(String goal, int interval, String startDate,
			String endDate, String demoDate, 
			String demoPlace, String daily, String productId) throws JSONException {
		JSONObject sprintJSON = new JSONObject();
		sprintJSON.put("goal", goal);
		sprintJSON.put("interval", interval);
		sprintJSON.put("startDate", startDate);
		sprintJSON.put("endDate", endDate);
		sprintJSON.put("demoDate", demoDate);
		sprintJSON.put("demoPlace", demoPlace);
		sprintJSON.put("daily", daily);
		
		Response response = client.target(baseURL)
		        .path("/products/" + productId + "/sprints")
		        .request()
		        .post(Entity.json(sprintJSON.toString()));
		return response;
	}
	
	public JSONObject getSprintBySprintId(String sprintId) throws JSONException {
		Response response = client.target(baseURL)
				.path("/sprints/" + sprintId)
				.request()
				.get();
		
		JSONObject sprintJSON = new JSONObject(response.readEntity(String.class));
		return sprintJSON.getJSONObject("sprint");
	}
	
	public JSONArray getSprintsByProductId(String productId) throws JSONException {
		JSONArray sprintsJSON = null;
		
		Response response = client.target(baseURL)
		        .path("/products/" + productId + "/sprints")
		        .request()
		        .get();

		JSONObject sprintJSON = new JSONObject(response.readEntity(String.class));
		sprintsJSON = sprintJSON.getJSONArray("sprintList");
		return sprintsJSON;
	}
	
	public Response editSprint(String sprintId, String goal, int interval, 
			String startDate, String endDate, String demoDate, 
			String demoPlace, String daily) throws JSONException {
		JSONObject sprintJSON = new JSONObject();
		sprintJSON.put("goal", goal);
		sprintJSON.put("interval", interval);
		sprintJSON.put("startDate", startDate);
		sprintJSON.put("endDate", endDate);
		sprintJSON.put("demoDate", demoDate);
		sprintJSON.put("demoPlace", demoPlace);
		sprintJSON.put("daily", daily);
		
		Response response = client.target(baseURL)
				.path("/sprints/" + sprintId)
		        .request()
		        .put(Entity.json(sprintJSON.toString()));
		return response;
	}
	
	public Response deleteSprint(String sprintId) throws JSONException {
		Response response = client.target(baseURL)
				.path("/sprints/" + sprintId)
		        .request()
		        .delete();
		return response;
	}
	
	public Response commitBacklogItemToSprint(String backlogItemId, String sprintId) throws JSONException {
		JSONObject committedBacklogItemJSON = new JSONObject();
		committedBacklogItemJSON.put("backlogItemId", backlogItemId);
		
		Response response = client.target(baseURL)
		        .path("/sprints/" + sprintId + "/committed_backlog_items")
		        .request()
		        .post(Entity.json(committedBacklogItemJSON.toString()));
		return response;
	}
	
	public JSONArray getCommittedBacklogItemsBySprintId(String sprintId) throws JSONException {
		JSONArray committedBacklogItemsJSON = null;
		
		Response response = client.target(baseURL)
				.path("/sprints/" + sprintId + "/committed_backlog_items")
		        .request()
		        .get();
		
		JSONObject committedBacklogItemJSON = new JSONObject(response.readEntity(String.class));
		committedBacklogItemsJSON = committedBacklogItemJSON.getJSONArray("committedBacklogItemList");
		return committedBacklogItemsJSON;
	}
	
	public Response dropBacklogItemFromSprint(String backlogItemId, String sprintId) {
		Response response = client.target(baseURL)
				.path("/sprints/" + sprintId + "/committed_backlog_items/" + backlogItemId)
		        .request()
		        .delete();
		return response;
	}
	
	public JSONArray getHistoriesByBacklogItemId(String backlogItemId) throws JSONException {
		JSONArray workItemHistoriesJSON = null;
		
		Response response = client.target(baseURL)
		        .path("/committed_backlog_items/" + backlogItemId + "/histories")
		        .request()
		        .get();
		
		JSONObject workItemHistoryJSON = new JSONObject(response.readEntity(String.class));
		workItemHistoriesJSON = workItemHistoryJSON.getJSONArray("historyList");
		return workItemHistoriesJSON;
	}
	
	public Response editRetrospective(String sprintId, String retrospective) throws JSONException {
		JSONObject retrospectiveJSON = new JSONObject();
		retrospectiveJSON.put("retrospective", retrospective);
		
		Response response = client.target(baseURL)
		        .path("/sprint_retrospectives/" + sprintId)
		        .request()
		        .put(Entity.json(retrospectiveJSON.toString()));
		return response;
	}
}
