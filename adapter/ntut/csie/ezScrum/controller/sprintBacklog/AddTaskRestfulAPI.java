package ntut.csie.ezScrum.controller.sprintBacklog;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.JSONException;
import org.json.JSONObject;

import ntut.csie.ezScrum.ApplicationContext;
import ntut.csie.ezScrum.controller.delegator.TaskDelegator;

@Path("/committed_backlog_items/{backlog_item_id}/tasks")
@Singleton
public class AddTaskRestfulAPI {
	private ApplicationContext applicationContext = ApplicationContext.getInstance();
	private TaskDelegator taskDelegator = applicationContext.newTaskDelegator();
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public synchronized String addTask(
			@PathParam("backlog_item_id") String backlogItemId, 
			String taskInfo) {
		String responseString = "";
		try {
			JSONObject taskJSON = new JSONObject(taskInfo);
			String description = taskJSON.getString("description");
			int estimate = taskJSON.getInt("estimate");
			String notes = taskJSON.getString("notes");
			
			Response response = taskDelegator.addTask(description, estimate, notes, backlogItemId);
			responseString = response.readEntity(String.class);
		} catch (JSONException e) {
			e.printStackTrace();
			Map<String, Object> addTaskOutputMap = new HashMap<>();
			addTaskOutputMap.put("addSuccess", false);
			addTaskOutputMap.put("errorMessage", "Sorry, there is the problem when add the task. Please contact to the system administrator!");
			JSONObject addTaskOutputJSON = new JSONObject(addTaskOutputMap);
			return addTaskOutputJSON.toString();
		}
		return responseString;
	}
}
