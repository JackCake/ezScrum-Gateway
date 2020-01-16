package ntut.csie.ezScrum.controller.delegator;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class BacklogItemAttachFileDelegator {
	private static String baseURL = "http://localhost:8080/backlogItemAttachFileService";
	private Client client;
	
	public BacklogItemAttachFileDelegator(Client client) {
		this.client = client;
	}
	
	public Response uploadBacklogItemAttachFile(String attachFileContent, String name, String backlogItemId) throws JSONException {
		JSONObject backlogItemAttachFileJSON = new JSONObject();
		backlogItemAttachFileJSON.put("attachFileContent", attachFileContent);
		backlogItemAttachFileJSON.put("name", name);
		
		Response response = client.target(baseURL)
		        .path("/backlog_items/" + backlogItemId + "/backlog_item_attach_files")
		        .request()
		        .post(Entity.json(backlogItemAttachFileJSON.toString()));
		return response;
	}
	
	public JSONArray getBacklogItemAttachFilesByBacklogItemId(String backlogItemId) throws JSONException {
		JSONArray backlogItemAttachFilesJSON = null;
		
		Response response = client.target(baseURL)
		        .path("/backlog_items/" + backlogItemId + "/backlog_item_attach_files")
		        .request()
		        .get();
		
		JSONObject backlogItemAttachFileJSON = new JSONObject(response.readEntity(String.class));
		backlogItemAttachFilesJSON = backlogItemAttachFileJSON.getJSONArray("backlogItemAttachFileList");
		return backlogItemAttachFilesJSON;
	}
	
	public Response downloadBacklogItemAttachFile(String backlogItemAttachFileId) {
		Response response = client.target(baseURL)
		        .path("/backlog_item_attach_files/" + backlogItemAttachFileId)
		        .request()
		        .get();
		return response;
	}
	
	public Response removeBacklogItemAttachFile(String backlogItemAttachFileId) {
		Response response = client.target(baseURL)
		        .path("/backlog_item_attach_files/" + backlogItemAttachFileId)
		        .request()
		        .delete();
		return response;
	}
}
