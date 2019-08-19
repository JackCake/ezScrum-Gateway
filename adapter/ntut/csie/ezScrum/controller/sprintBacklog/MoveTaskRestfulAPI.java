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

@Path("/task_statuses")
@Singleton
public class MoveTaskRestfulAPI {
	private ApplicationContext applicationContext = ApplicationContext.getInstance();
	private TaskDelegator taskDelegator = applicationContext.newTaskDelegator();
	
	@PUT
	@Path("/{task_id}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public synchronized String moveTask(
			@PathParam("task_id") String taskId, 
			String taskInfo) {
		String responseString = "";
		try {
			JSONObject taskJSON = new JSONObject(taskInfo);
			String status = taskJSON.getString("status");
			
			Response response = taskDelegator.moveTask(taskId, status);
			responseString = response.readEntity(String.class);
		} catch (JSONException e) {
			e.printStackTrace();
			Map<String, Object> moveTaskOutputMap = new HashMap<>();
			moveTaskOutputMap.put("moveSuccess", false);
			moveTaskOutputMap.put("errorMessage", "Sorry, please try again!");
			JSONObject moveTaskOutputJSON = new JSONObject(moveTaskOutputMap);
			return moveTaskOutputJSON.toString();
		}
		return responseString;
	}
}
