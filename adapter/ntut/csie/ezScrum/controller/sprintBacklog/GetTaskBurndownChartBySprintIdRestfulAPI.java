package ntut.csie.ezScrum.controller.sprintBacklog;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

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
import ntut.csie.ezScrum.controller.delegator.SprintDelegator;
import ntut.csie.ezScrum.controller.delegator.TaskDelegator;

@Path("/products/{product_id}/sprints/{sprint_id}/task_burndown_chart")
@Singleton
public class GetTaskBurndownChartBySprintIdRestfulAPI {
	private ApplicationContext applicationContext = ApplicationContext.getInstance();
	private SprintDelegator sprintDelegator = applicationContext.newSprintDelegator();
	private TaskDelegator taskDelegator = applicationContext.newTaskDelegator();
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public synchronized String getTaskBurndownChart(
			@PathParam("product_id") String productId, 
			@PathParam("sprint_id") String sprintId) {
		JSONObject burndownChartPointsJSON = null;
		try {
			JSONObject sprintJSON = getSprint(sprintId, productId);
			String startDate = sprintJSON.getString("startDate");
			String endDate = sprintJSON.getString("endDate");
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			Calendar start = Calendar.getInstance();
			start.setTime(dateFormat.parse(startDate));
			Calendar end = Calendar.getInstance();
			end.setTime(dateFormat.parse(endDate));
			String sprintDates = "";
			for(Date date = start.getTime(); !start.after(end); start.add(Calendar.DATE, 1), date = start.getTime()) {
				sprintDates += dateFormat.format(date) + ",";
			}
			if(sprintDates.isEmpty()) {
				burndownChartPointsJSON = new JSONObject();
				burndownChartPointsJSON.put("idealPoints", new ArrayList<>());
				burndownChartPointsJSON.put("realPoints", new ArrayList<>());
				burndownChartPointsJSON.put("sprintDates", new ArrayList<>());
				return burndownChartPointsJSON.toString();
			} else {
				sprintDates = sprintDates.substring(0, sprintDates.length() - 1);
			}
			
			String taskIds = "";
			JSONArray committedBacklogItemsJSON = sprintDelegator.getCommittedBacklogItemsBySprintId(sprintId);
			for(int i = 0; i < committedBacklogItemsJSON.length(); i++) {
				JSONObject committedBacklogItemJSON = committedBacklogItemsJSON.getJSONObject(i);
				String backlogItemId = committedBacklogItemJSON.getString("backlogItemId");
				JSONArray taskList = taskDelegator.getTasksByWorkItemId(backlogItemId);
				for(int j = 0; j < taskList.length(); j++) {
					JSONObject taskJSON = taskList.getJSONObject(j);
					String taskId = taskJSON.getString("taskId");
					taskIds += taskId + ",";
				}
			}
			List<String> sprintDateList = new ArrayList<>(Arrays.asList(sprintDates.split(",")));
			if(taskIds.isEmpty()) {
				burndownChartPointsJSON = new JSONObject();
				burndownChartPointsJSON.put("idealPoints", new ArrayList<>());
				burndownChartPointsJSON.put("realPoints", new ArrayList<>());
				burndownChartPointsJSON.put("sprintDates", sprintDateList);
				return burndownChartPointsJSON.toString();
			} else {
				taskIds = taskIds.substring(0, taskIds.length() - 1);
			}
			
			burndownChartPointsJSON = taskDelegator.getBurndownChartPointsBySprintDatesAndTaskIds(sprintDates, taskIds);
			burndownChartPointsJSON.put("sprintDates", sprintDateList);
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return burndownChartPointsJSON.toString();
	}
	
	private JSONObject getSprint(String sprintId, String productId) throws JSONException {
		JSONArray sprintsJSON = sprintDelegator.getSprintsByProductId(productId);
		for(int i = 0; i < sprintsJSON.length(); i++) {
			JSONObject sprintJSON = sprintsJSON.getJSONObject(i);
			if(sprintJSON.getString("sprintId").equals(sprintId)) {
				return sprintJSON;
			}
		}
		return null;
	}
}
