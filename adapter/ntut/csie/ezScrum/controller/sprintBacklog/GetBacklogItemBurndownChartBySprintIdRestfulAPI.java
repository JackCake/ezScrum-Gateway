package ntut.csie.ezScrum.controller.sprintBacklog;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import ntut.csie.ezScrum.controller.delegator.BacklogItemDelegator;
import ntut.csie.ezScrum.controller.delegator.ProductDelegator;
import ntut.csie.ezScrum.controller.delegator.SprintDelegator;

@Path("/products/{product_id}/sprints/{sprint_id}/backlog_item_burndown_chart")
@Singleton
public class GetBacklogItemBurndownChartBySprintIdRestfulAPI {
	private ApplicationContext applicationContext = ApplicationContext.getInstance();
	private ProductDelegator productDelegator = applicationContext.newProductDelegator();
	private SprintDelegator sprintDelegator = applicationContext.newSprintDelegator();
	private BacklogItemDelegator backlogItemDelegator = applicationContext.newBacklogItemDelegator();
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public synchronized String getBacklogItemBurndownChart(
			@PathParam("product_id") String productId, 
			@PathParam("sprint_id") String sprintId) {
		JSONObject burndownChartPointsJSON = new JSONObject();
		try {
			JSONObject sprintJSON = getSprint(sprintId, productId);
			if(sprintJSON == null) {
				burndownChartPointsJSON.put("realPoints", new ArrayList<>());
				burndownChartPointsJSON.put("idealPoints", new ArrayList<>());
				burndownChartPointsJSON.put("sprintDates", new ArrayList<>());
				return burndownChartPointsJSON.toString();
			}
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
			if(!sprintDates.isEmpty()) {
				sprintDates = sprintDates.substring(0, sprintDates.length() - 1);
			}
			List<String> sprintDateList = new ArrayList<>(Arrays.asList(sprintDates.split(",")));
			
			JSONArray committedBacklogItemsJSON = sprintDelegator.getCommittedBacklogItemsBySprintId(sprintId);
			if(committedBacklogItemsJSON.length() == 0) {
				burndownChartPointsJSON.put("realPoints", new ArrayList<>());
				burndownChartPointsJSON.put("idealPoints", new ArrayList<>());
				burndownChartPointsJSON.put("sprintDates", sprintDateList);
				return burndownChartPointsJSON.toString();
			}
			List<String> backlogItemIds = new ArrayList<>();
			for(int i = 0; i < committedBacklogItemsJSON.length(); i++) {
				JSONObject committedBacklogItemJSON = committedBacklogItemsJSON.getJSONObject(i);
				String backlogItemId = committedBacklogItemJSON.getString("backlogItemId");
				backlogItemIds.add(backlogItemId);
			}
			
			Map<String, JSONObject> backlogItemJSONWithBacklogItemId = buildBacklogItemJSONWithBacklogItemIdMap(productId);
			Map<String, Map<String, Integer>> backlogItemEstimateProcessWithBacklogItemId = buildBacklogItemEstimateProcessWithBacklogItemIdMap(backlogItemIds, sprintDateList, backlogItemJSONWithBacklogItemId);
			Map<String, Map<String, Boolean>> backlogItemStatusProcessWithBacklogItemId = buildBacklogItemStatusProcessWithBacklogItemIdMap(backlogItemIds, sprintDateList);
			DateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
			List<Integer> realPoints = new ArrayList<>();
			List<Double> idealPoints = new ArrayList<>();
			for(String date : sprintDateList) {
				Date sprintDate = null;
				Date today = new Date();
				try {
					sprintDate = simpleDateFormat.parse(date);
				} catch (ParseException e) {
					e.printStackTrace();
				}
				if (sprintDate.after(today)) {
					realPoints.add(null);
				} else {
					int realPoint = 0;
					for(String backlogItemId : backlogItemIds) {
						Map<String, Boolean> backlogItemStatusProcessMap = backlogItemStatusProcessWithBacklogItemId.get(backlogItemId);
						boolean isDone = backlogItemStatusProcessMap.get(date);
						if(!isDone) {
							Map<String, Integer> backlogItemEstimateProcessMap = backlogItemEstimateProcessWithBacklogItemId.get(backlogItemId);
							realPoint += backlogItemEstimateProcessMap.get(date);
						}
					}
					realPoints.add(realPoint);
				}
			}
			
			if(realPoints.get(0) == null) {
				int realPointsOfFirstDate = 0;
				for(String backlogItemId : backlogItemIds) {
					JSONObject backlogItemJSON = backlogItemJSONWithBacklogItemId.get(backlogItemId);
					String status = backlogItemJSON.getString("status");
					if(!status.equals("Done")) {
						realPointsOfFirstDate += backlogItemJSON.getInt("estimate");
					}
				}
				realPoints.set(0, realPointsOfFirstDate);
			} else {
				
			}
			double idealPointsOfFirstDate = realPoints.get(0);
			idealPoints.add(idealPointsOfFirstDate);
			for(int i = 1; i < sprintDateList.size() - 1; i++) {
				idealPoints.add(idealPoints.get(i - 1) - (idealPointsOfFirstDate / (sprintDateList.size() - 1)));
			}
			idealPoints.add(0.0);
			
			burndownChartPointsJSON.put("realPoints", realPoints);
			burndownChartPointsJSON.put("idealPoints", idealPoints);
			burndownChartPointsJSON.put("sprintDates", sprintDateList);
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return burndownChartPointsJSON.toString();
	}
	
	private Map<String, Map<String, Integer>> buildBacklogItemEstimateProcessWithBacklogItemIdMap(List<String> backlogItemIds, List<String> sprintDates, Map<String, JSONObject> backlogItemJSONWithBacklogItemId) throws JSONException, ParseException {
		Map<String, Map<String, Integer>> backlogItemEstimateProcessWithBacklogItemId = new HashMap<>();
		DateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		DateFormat simpleDateFormat2 = new SimpleDateFormat("yyyy-MM-dd");
		for(String backlogItemId : backlogItemIds) {
			JSONArray workItemHistoriesJSON = backlogItemDelegator.getHistoriesByBacklogItemId(backlogItemId);
			for(int i = 0; i < workItemHistoriesJSON.length(); i++) {
				JSONObject workItemHistoryJSON = workItemHistoriesJSON.getJSONObject(i);
				String occurredOn = workItemHistoryJSON.getString("time");
				Date occurredOnDate = simpleDateFormat.parse(occurredOn);
				String date = simpleDateFormat2.format(occurredOnDate);
				String event = workItemHistoryJSON.getString("event");
				if(event.equals(Event.created)) {
					JSONObject backlogItemJSON = backlogItemJSONWithBacklogItemId.get(backlogItemId);
					int estimate = backlogItemJSON.getInt("estimate");
					Map<String, Integer> backlogItemEstimateProcessMap = new HashMap<>();
					backlogItemEstimateProcessMap.put(date, estimate);
					backlogItemEstimateProcessWithBacklogItemId.put(backlogItemId, backlogItemEstimateProcessMap);
				} else if(event.equals(Event.estimateChanged)) {
					int estimate = workItemHistoryJSON.getInt("estimate");
					Map<String, Integer> backlogItemEstimateProcessMap = backlogItemEstimateProcessWithBacklogItemId.get(backlogItemId);
					backlogItemEstimateProcessMap.put(date, estimate);
					backlogItemEstimateProcessWithBacklogItemId.put(backlogItemId, backlogItemEstimateProcessMap);
				}
			}
		}
		for(String backlogItemId : backlogItemIds) {
			Map<String, Integer> backlogItemEstimateProcessMap = backlogItemEstimateProcessWithBacklogItemId.get(backlogItemId);
			for(int i = 0; i < sprintDates.size(); i++) {
				if(!backlogItemEstimateProcessMap.containsKey(sprintDates.get(i))) {
					if(i == 0) {
						backlogItemEstimateProcessMap.put(sprintDates.get(i), 0);
					} else {
						int lastSprintDateRealPoints = backlogItemEstimateProcessMap.get(sprintDates.get(i - 1));
						backlogItemEstimateProcessMap.put(sprintDates.get(i), lastSprintDateRealPoints);
					}
					backlogItemEstimateProcessWithBacklogItemId.put(backlogItemId, backlogItemEstimateProcessMap);
				}
			}
		}
		return backlogItemEstimateProcessWithBacklogItemId;
	}
	
	private Map<String, Map<String, Boolean>> buildBacklogItemStatusProcessWithBacklogItemIdMap(List<String> backlogItemIds, List<String> sprintDates) throws JSONException, ParseException {
		Map<String, Map<String, Boolean>> backlogItemStatusProcessWithBacklogItemId = new HashMap<>();
		DateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		DateFormat simpleDateFormat2 = new SimpleDateFormat("yyyy-MM-dd");
		for(String backlogItemId : backlogItemIds) {
			JSONArray workItemHistoriesJSON = backlogItemDelegator.getHistoriesByBacklogItemId(backlogItemId);
			String originalStatus = "";
			for(int i = 0; i < workItemHistoriesJSON.length(); i++) {
				JSONObject workItemHistoryJSON = workItemHistoriesJSON.getJSONObject(i);
				String occurredOn = workItemHistoryJSON.getString("time");
				Date occurredOnDate = simpleDateFormat.parse(occurredOn);
				String date = simpleDateFormat2.format(occurredOnDate);
				String event = workItemHistoryJSON.getString("event");
				if(event.equals(Event.created)) {
					Map<String, Boolean> backlogItemStatusProcessMap = new HashMap<>();
					backlogItemStatusProcessMap.put(date, false);
					backlogItemStatusProcessWithBacklogItemId.put(backlogItemId, backlogItemStatusProcessMap);
				} else if(event.equals(Event.movedIn)) {
					Map<String, Boolean> backlogItemStatusProcessMap = backlogItemStatusProcessWithBacklogItemId.get(backlogItemId);
					if(originalStatus.equals("To do")) {
						backlogItemStatusProcessMap.put(date, true);
					} else if(originalStatus.equals("Done")) {
						backlogItemStatusProcessMap.put(date, false);
					}
					backlogItemStatusProcessWithBacklogItemId.put(backlogItemId, backlogItemStatusProcessMap);
				} else if(event.equals(Event.movedOut)) {
					if(originalStatus.equals("To do")) {
						originalStatus = "Done";
					} else if(originalStatus.isEmpty() || originalStatus.equals("Done")) {
						originalStatus = "To do";
					}
				}
			}
		}
		for(String backlogItemId : backlogItemIds) {
			Map<String, Boolean> backlogItemStatusProcessMap = backlogItemStatusProcessWithBacklogItemId.get(backlogItemId);
			for(int i = 0; i < sprintDates.size(); i++) {
				if(!backlogItemStatusProcessMap.containsKey(sprintDates.get(i))) {
					if(i == 0) {
						backlogItemStatusProcessMap.put(sprintDates.get(i), false);
					} else {
						boolean lastSprintDateStatus = backlogItemStatusProcessMap.get(sprintDates.get(i - 1));
						backlogItemStatusProcessMap.put(sprintDates.get(i), lastSprintDateStatus);
					}
					backlogItemStatusProcessWithBacklogItemId.put(backlogItemId, backlogItemStatusProcessMap);
				}
			}
		}
		return backlogItemStatusProcessWithBacklogItemId;
	}
	
	private Map<String, JSONObject> buildBacklogItemJSONWithBacklogItemIdMap(String productId) throws JSONException{
		Map<String, JSONObject> backlogItemMap = new HashMap<>();
		JSONArray stagesJSON = productDelegator.getStagesInBoardByProductId(productId);
		for(int i = 0; i < 3; i++) {
			JSONObject stageJSON = stagesJSON.getJSONObject(i);
			JSONObject miniStageJSON = stageJSON.getJSONArray("miniStageList").getJSONObject(0);
			JSONObject swimLaneJSON = miniStageJSON.getJSONArray("swimLaneList").getJSONObject(0);
			String swimLaneId = swimLaneJSON.getString("swimLaneId");
			JSONArray workItemsJSON = backlogItemDelegator.getWorkItemsBySwimLaneId(swimLaneId);
			for(int j = 0; j < workItemsJSON.length(); j++) {
				JSONObject workItemJSON = workItemsJSON.getJSONObject(j);
				String workItemId = workItemJSON.getString("workItemId");
				String status = "To do";
				if(i == 1) {
					status = "Doing";
				}else if (i == 2) {
					status = "Done";
				}
				JSONObject backlogItemJSON = new JSONObject();
				backlogItemJSON.put("status", status);
				backlogItemJSON.put("estimate", workItemJSON.getInt("estimate"));
				backlogItemMap.put(workItemId, backlogItemJSON);
			}
		}
		return backlogItemMap;
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
