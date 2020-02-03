package ntut.csie.ezScrum.controller.delegator;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class TaskDelegator {
	private static String baseURL = "http://localhost:8080/taskService";
	private Client client;
	
	public TaskDelegator(Client client) {
		this.client = client;
	}
	
	public Response addTask(String description, int estimate, String notes, String backlogItemId) throws JSONException {
		JSONObject taskJSON = new JSONObject();
		taskJSON.put("description", description);
		taskJSON.put("estimate", estimate);
		taskJSON.put("notes", notes);
		
		Response response = client.target(baseURL)
		        .path("/backlog_items/" + backlogItemId + "/tasks")
		        .request()
		        .post(Entity.json(taskJSON.toString()));
		return response;
	}
	
	public JSONArray getTasksByWorkItemId(String backlogItemId) throws JSONException {
		JSONArray tasksJSON = null;
		
		Response response = client.target(baseURL)
		        .path("/backlog_items/" + backlogItemId + "/tasks")
		        .request()
		        .get();
		
		JSONObject taskJSON = new JSONObject(response.readEntity(String.class));
		tasksJSON = taskJSON.getJSONArray("taskList");
		return tasksJSON;
	}
	
	public Response editTask(String taskId, String description, int estimate, int remains, String notes) throws JSONException {
		JSONObject taskJSON = new JSONObject();
		taskJSON.put("description", description);
		taskJSON.put("estimate", estimate);
		taskJSON.put("remains", remains);
		taskJSON.put("notes", notes);
		
		Response response = client.target(baseURL)
		        .path("/tasks/" + taskId)
		        .request()
		        .put(Entity.json(taskJSON.toString()));
		return response;
	}
	
	public Response deleteTask(String taskId) {
		Response response = client.target(baseURL)
		        .path("/tasks/" + taskId)
		        .request()
		        .delete();
		return response;
	}
	
	public Response moveTask(String taskId, String status) throws JSONException {
		JSONObject taskJSON = new JSONObject();
		taskJSON.put("taskId", taskId);
		taskJSON.put("status", status);
		
		Response response = client.target(baseURL)
		        .path("/task_statuses/" + taskId)
		        .request()
		        .put(Entity.json(taskJSON.toString()));
		return response;
	}
	
	public Response uploadTaskAttachFile(String attachFileContents, String name, String taskId) throws JSONException {
		JSONObject taskAttachFileJSON = new JSONObject();
		taskAttachFileJSON.put("attachFileContents", attachFileContents);
		taskAttachFileJSON.put("name", name);
		
		Response response = client.target(baseURL)
		        .path("/tasks/" + taskId + "/task_attach_files")
		        .request()
		        .post(Entity.json(taskAttachFileJSON.toString()));
		return response;
	}
	
	public JSONArray getTaskAttachFilesByTaskId(String taskId) throws JSONException {
		JSONArray taskAttachFilesJSON = null;
		
		Response response = client.target(baseURL)
		        .path("/tasks/" + taskId + "/task_attach_files")
		        .request()
		        .get();
		
		JSONObject taskAttachFileJSON = new JSONObject(response.readEntity(String.class));
		taskAttachFilesJSON = taskAttachFileJSON.getJSONArray("taskAttachFileList");
		return taskAttachFilesJSON;
	}
	
	public Response downloadTaskAttachFile(String taskAttachFileId, String taskId) {
		Response response = client.target(baseURL)
		        .path("/tasks/" + taskId + "/task_attach_files/" + taskAttachFileId)
		        .request()
		        .get();
		return response;
	}
	
	public Response removeTaskAttachFile(String taskAttachFileId, String taskId) {
		Response response = client.target(baseURL)
		        .path("/tasks/" + taskId + "/task_attach_files/" + taskAttachFileId)
		        .request()
		        .delete();
		return response;
	}
	
	public JSONArray getHistoriesByTaskId(String taskId) throws JSONException {
		JSONArray historiesJSON = null;
		
		Response response = client.target(baseURL)
		        .path("/tasks/" + taskId + "/histories")
		        .request()
		        .get();
		
		JSONObject historyJSON = new JSONObject(response.readEntity(String.class));
		historiesJSON = historyJSON.getJSONArray("historyList");
		return historiesJSON;
	}
	
	public JSONObject getBurndownChartPointsBySprintDatesAndTaskIds(String sprintDates, String taskIds) throws JSONException {
		Response response = client.target(baseURL)
				.path("/sprints/" + sprintDates + "/tasks/" + taskIds + "/burndown_chart_points")
				.request()
				.get();
		
		JSONObject burndownChartPointsJSON = new JSONObject(response.readEntity(String.class));
		return burndownChartPointsJSON;
	}
}
