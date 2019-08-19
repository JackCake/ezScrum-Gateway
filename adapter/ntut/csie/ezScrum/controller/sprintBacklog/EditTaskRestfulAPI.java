package ntut.csie.ezScrum.controller.sprintBacklog;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.JSONException;
import org.json.JSONObject;

import ntut.csie.ezScrum.ApplicationContext;
import ntut.csie.ezScrum.controller.delegator.TaskDelegator;

@Path("/tasks")
@Singleton
public class EditTaskRestfulAPI {
	private ApplicationContext applicationContext = ApplicationContext.getInstance();
	private TaskDelegator taskDelegator = applicationContext.newTaskDelegator();

	@PUT
	@Path("/{task_id}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public synchronized String editTask(
			@PathParam("task_id") String taskId, 
			String taskInfo) {
		String responseString = "";
		try {
			JSONObject taskJSON = new JSONObject(taskInfo);
			String description = taskJSON.getString("description");
			int estimate = taskJSON.getInt("estimate");
			int remains = taskJSON.getInt("remains");
			String notes = taskJSON.getString("notes");
			
			Response response = taskDelegator.editTask(taskId, description, estimate, remains, notes);
			responseString = response.readEntity(String.class);
		} catch (JSONException e) {
			Map<String, Object> editTaskOutputMap = new HashMap<>();
			editTaskOutputMap.put("editSuccess", false);
			editTaskOutputMap.put("errorMessage", "Sorry, please try again!");
			JSONObject editTaskOutputJSON = new JSONObject(editTaskOutputMap);
			return editTaskOutputJSON.toString();
		}
		return responseString;
	}
}
