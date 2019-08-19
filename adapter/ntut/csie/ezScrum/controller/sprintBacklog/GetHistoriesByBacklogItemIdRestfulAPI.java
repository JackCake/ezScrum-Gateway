package ntut.csie.ezScrum.controller.sprintBacklog;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.UUID;

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
import ntut.csie.ezScrum.controller.delegator.BacklogItemImportanceDelegator;
import ntut.csie.ezScrum.controller.delegator.ReleaseDelegator;
import ntut.csie.ezScrum.controller.delegator.SprintDelegator;

@Path("/backlog_items/{backlog_item_id}/histories")
@Singleton
public class GetHistoriesByBacklogItemIdRestfulAPI {
	private ApplicationContext applicationContext = ApplicationContext.getInstance();
	private ReleaseDelegator releaseDelegator = applicationContext.newReleaseDelegator();
	private SprintDelegator sprintDelegator = applicationContext.newSprintDelegator();
	private BacklogItemDelegator backlogItemDelegator = applicationContext.newBacklogItemDelegator();
	private BacklogItemImportanceDelegator backlogItemImportanceDelegator = applicationContext.newBacklogItemImportanceDelegator();
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public synchronized String getHistoriesByBacklogItemId(@PathParam("backlog_item_id") String backlogItemId) {
		JSONObject getHistoriesByBacklogItemIdOutput = new JSONObject();
		try {
			JSONArray workItemHistoriesJSON = backlogItemDelegator.getHistoriesByBacklogItemId(backlogItemId);
			List<JSONObject> backlogItemHistoryList = new ArrayList<>();
			String originalStatus = "";
			
			for(int i = 0; i < workItemHistoriesJSON.length(); i++) {
				JSONObject workItemHistoryJSON = workItemHistoriesJSON.getJSONObject(i);
				JSONObject backlogItemHistoryJSON = new JSONObject();
				backlogItemHistoryJSON.put("historyId", UUID.randomUUID().toString());
				backlogItemHistoryJSON.put("occurredOn", workItemHistoryJSON.getString("time"));
				String event = workItemHistoryJSON.getString("event");
				String description = "";
				if(event.equals(Event.created)) {
					backlogItemHistoryJSON.put("behavior", HistoryBehavior.create);
					description = "Create Backlog Item";
				} else if(event.equals(Event.descriptionEdited)) {
					backlogItemHistoryJSON.put("behavior", HistoryBehavior.editDescription);
					String originalDescription = workItemHistoryJSON.getString("workItemDescription");
					String newDescription = workItemHistoryJSON.getString("newWorkItemDescription");
					description = "\"" + originalDescription + "\" → \"" + newDescription + "\"";
				} else if(event.equals(Event.estimateChanged)) {
					backlogItemHistoryJSON.put("behavior", HistoryBehavior.changeEstimate);
					String originalEstimate = workItemHistoryJSON.getString("oringinalEstimate");
					String newEstimate = workItemHistoryJSON.getString("estimate");
					description = "\"" + originalEstimate + "\" → \"" + newEstimate + "\"";
				} else if(event.equals(Event.movedIn)) {
					if(originalStatus.isEmpty()) {
						continue;
					} else if(originalStatus.equals("To do")) {
						description = "\"" + originalStatus + "\" → \"Done\"";
					} else if(originalStatus.equals("Done")) {
						description = "\"" + originalStatus + "\" → \"To do\"";
					}
					backlogItemHistoryJSON.put("behavior", HistoryBehavior.changeStatus);
				} else if(event.equals(Event.movedOut)) {
					if(originalStatus.isEmpty() || originalStatus.equals("Done")) {
						originalStatus = "To do";
					} else if(originalStatus.equals("To do")) {
						originalStatus = "Done";
					}
					continue;
				} else if(event.equals(Event.noteChanged)) {
					backlogItemHistoryJSON.put("behavior", HistoryBehavior.editNotes);
					String originalNotes = workItemHistoryJSON.getString("oringinalNote");
					String newNotes = workItemHistoryJSON.getString("note");
					description = "\"" + originalNotes + "\" → \"" + newNotes + "\"";
				}
				backlogItemHistoryJSON.put("description", description);
				backlogItemHistoryList.add(backlogItemHistoryJSON);
			}
			
			JSONArray scheduledBacklogItemHistoriesJSON = releaseDelegator.getHistoriesByBacklogItemId(backlogItemId);
			for(int i = 0; i < scheduledBacklogItemHistoriesJSON.length(); i++) {
				JSONObject scheduledBacklogItemHistoryJSON = scheduledBacklogItemHistoriesJSON.getJSONObject(i);
				backlogItemHistoryList.add(scheduledBacklogItemHistoryJSON);
			}
			
			JSONArray committedBacklogItemHistoriesJSON = sprintDelegator.getHistoriesByBacklogItemId(backlogItemId);
			for(int i = 0; i < committedBacklogItemHistoriesJSON.length(); i++) {
				JSONObject committedBacklogItemHistoryJSON = committedBacklogItemHistoriesJSON.getJSONObject(i);
				backlogItemHistoryList.add(committedBacklogItemHistoryJSON);
			}
			
			JSONArray backlogItemImportanceHistoriesJSON = backlogItemImportanceDelegator.getHistoriesByBacklogItemId(backlogItemId);
			for(int i = 0; i < backlogItemImportanceHistoriesJSON.length(); i++) {
				JSONObject backlogItemImportanceHistoryJSON = backlogItemImportanceHistoriesJSON.getJSONObject(i);
				backlogItemHistoryList.add(backlogItemImportanceHistoryJSON);
			}
			
			Collections.sort(backlogItemHistoryList, new Comparator<JSONObject>() {

				@Override
				public int compare(JSONObject o1, JSONObject o2) {
					String occurredOn1 = "";
					String occurredOn2 = "";
					try {
						occurredOn1 = o1.getString("occurredOn");
						occurredOn2 = o2.getString("occurredOn");
					} catch (JSONException e) {
						e.printStackTrace();
					}
					DateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
					Date occurredOnDate1 = null;
					Date occurredOnDate2 = null;
					try {
						occurredOnDate1 = simpleDateFormat.parse(occurredOn1);
						occurredOnDate2 = simpleDateFormat.parse(occurredOn2);
					} catch (ParseException e) {
						e.printStackTrace();
					}
					return occurredOnDate1.compareTo(occurredOnDate2);
				}
				
			});
			
			getHistoriesByBacklogItemIdOutput.put("backlogItemHistoryList", backlogItemHistoryList);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return getHistoriesByBacklogItemIdOutput.toString();
	}
}
