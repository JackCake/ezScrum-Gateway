package ntut.csie.ezScrum.controller.delegator;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class TagDelegator {
	private static String baseURL = "http://localhost:8080/tagService";
	private Client client;
	
	public TagDelegator(Client client) {
		this.client = client;
	}
	
	public Response addTag(String name, String productId) throws JSONException {
		JSONObject tagJSON = new JSONObject();
		tagJSON.put("name", name);
		
		Response response = client.target(baseURL)
		        .path("/products/" + productId + "/tags")
		        .request()
		        .post(Entity.json(tagJSON.toString()));
		return response;
	}
	
	public JSONArray getTagsByProductId(String productId) throws JSONException {
		JSONArray tagsJSON = null;
		
		Response response = client.target(baseURL)
		        .path("/products/" + productId + "/tags")
		        .request()
		        .get();
		
		JSONObject tagJSON = new JSONObject(response.readEntity(String.class));
		tagsJSON = tagJSON.getJSONArray("tagList");
		return tagsJSON;
	}
	
	public Response editTag(String tagId, String name) throws JSONException {
		JSONObject tagJSON = new JSONObject();
		tagJSON.put("name", name);
		
		Response response = client.target(baseURL)
		        .path("/tags/" + tagId)
		        .request()
		        .put(Entity.json(tagJSON.toString()));
		return response;
	}
	
	public Response deleteTag(String tagId) {
		Response response = client.target(baseURL)
		        .path("/tags/" + tagId)
		        .request()
		        .delete();
		return response;
	}
	
	public Response assignTagToBacklogItem(String backlogItemId, String tagId) throws JSONException {
		JSONObject assignedTagJSON = new JSONObject();
		assignedTagJSON.put("tagId", tagId);
		
		Response response = client.target(baseURL)
		        .path("backlog_items/" + backlogItemId + "/assigned_tags")
		        .request()
		        .post(Entity.json(assignedTagJSON.toString()));
		return response;
	}
	
	public JSONArray getAssignedTagByBacklogItemId(String backlogItemId) throws JSONException {
		JSONArray assignedTagsJSON = null;
		
		Response response = client.target(baseURL)
				.path("/backlog_items/" + backlogItemId + "/assigned_tags")
		        .request()
		        .get();
		
		JSONObject assignedTagJSON = new JSONObject(response.readEntity(String.class));
		assignedTagsJSON = assignedTagJSON.getJSONArray("assignedTagList");
		return assignedTagsJSON;
	}
	
	public Response unassignTagFromBacklogItem(String assignedTagId) {
		Response response = client.target(baseURL)
				.path("/assigned_tags/" + assignedTagId)
		        .request()
		        .delete();
		return response;
	}
}
