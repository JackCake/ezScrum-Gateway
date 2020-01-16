package ntut.csie.ezScrum.controller.sprintBacklog;

import javax.inject.Singleton;
import javax.ws.rs.DELETE;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

import ntut.csie.ezScrum.ApplicationContext;
import ntut.csie.ezScrum.controller.delegator.TaskDelegator;

@Path("/tasks")
@Singleton
public class DeleteTaskRestfulAPI {
	private ApplicationContext applicationContext = ApplicationContext.getInstance();
	private TaskDelegator taskDelegator = applicationContext.newTaskDelegator();
	
	@DELETE
	@Path("/{task_id}")
	public synchronized String deleteTask(
			@PathParam("task_id") String taskId) {
		Response response = taskDelegator.deleteTask(taskId);
		return response.readEntity(String.class);
	}
}
