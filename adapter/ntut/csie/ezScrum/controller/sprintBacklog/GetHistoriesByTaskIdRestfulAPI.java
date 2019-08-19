package ntut.csie.ezScrum.controller.sprintBacklog;

import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ntut.csie.ezScrum.ApplicationContext;
import ntut.csie.ezScrum.controller.delegator.TaskDelegator;

@Path("/tasks/{task_id}/histories")
@Singleton
public class GetHistoriesByTaskIdRestfulAPI {
	private ApplicationContext applicationContext = ApplicationContext.getInstance();
	private TaskDelegator taskDelegator = applicationContext.newTaskDelegator();
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public synchronized String getHistoriesByTaskId(@PathParam("task_id") String taskId) {
		JSONObject getHistoriesByTaskIdOutput = new JSONObject();
		try {
			JSONArray taskHistoryList = taskDelegator.getHistoriesByTaskId(taskId);
			
			getHistoriesByTaskIdOutput.put("taskHistoryList", taskHistoryList);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return getHistoriesByTaskIdOutput.toString();
	}
}
