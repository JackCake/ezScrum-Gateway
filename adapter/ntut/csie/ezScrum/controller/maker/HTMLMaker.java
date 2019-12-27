package ntut.csie.ezScrum.controller.maker;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

public class HTMLMaker {
	public String getReleaseInformationHtml(JSONObject releaseJSON, List<JSONObject> scheduledBacklogItemList) throws Exception {
		int orderId = releaseJSON.getInt("orderId");
		String name = releaseJSON.getString("name");
		String startDate = releaseJSON.getString("startDate");
		String endDate = releaseJSON.getString("endDate");
		String releaseDescription = releaseJSON.getString("description");
		
		DateFormat from = new SimpleDateFormat("yyyy-MM-dd");
		DateFormat to = new SimpleDateFormat("yyyy/MM/dd");
		startDate = to.format(from.parse(startDate));
		endDate = to.format(from.parse(endDate));
		
		String[] titles = new String[] {
				"Release#" + orderId, 
				"ezScrum, Release " + orderId, 
				"Release Name", 
				"Release Backlog(Estimates in Parenthesis)", 
				"Schedule"
				};
		
		Map<String, List<String>> contents = new HashMap<>();
		contents.put(titles[2], new ArrayList<>(Arrays.asList(new String[] {name})));
		
		List<String> releaseBacklogContents = new ArrayList<>();
		int totalEstimate = 0;
		for(JSONObject scheduledBacklogItemJSON : scheduledBacklogItemList) {
			String backlogItemDescription = scheduledBacklogItemJSON.getString("description");
			int estimate = scheduledBacklogItemJSON.getInt("estimate");
			releaseBacklogContents.add(backlogItemDescription + "(" + estimate + ")");
			totalEstimate += estimate;
		}
		releaseBacklogContents.add("Estimated velocity : " + totalEstimate + " points");
		contents.put(titles[3], releaseBacklogContents);
		
		List<String> scheduleContents = new ArrayList<>();
		scheduleContents.add("Release period : " + startDate + " to " + endDate);
		scheduleContents.add("Description : " + releaseDescription);
		contents.put(titles[4], scheduleContents);
		
		return generateHtmlTemplateWithContent(titles, contents);
	}
	
	public String getSprintInformationHtml(JSONObject sprintJSON, List<JSONObject> committedBacklogItemList) throws Exception {
		int orderId = sprintJSON.getInt("orderId");
		String goal = sprintJSON.getString("goal");
		String startDate = sprintJSON.getString("startDate");
		String endDate = sprintJSON.getString("endDate");
		String demoDate = sprintJSON.getString("demoDate");
		String demoPlace = sprintJSON.getString("demoPlace");
		String daily = sprintJSON.getString("daily");
		
		DateFormat from = new SimpleDateFormat("yyyy-MM-dd");
		DateFormat to = new SimpleDateFormat("yyyy/MM/dd");
		startDate = to.format(from.parse(startDate));
		endDate = to.format(from.parse(endDate));
		demoDate = to.format(from.parse(demoDate));
		
		String[] titles = new String[] {
				"Sprint#" + orderId, 
				"ezScrum, Sprint " + orderId, 
				"Sprint Goal", 
				"Sprint Backlog(Estimates in Parenthesis)", 
				"Schedule"
				};
		
		Map<String, List<String>> contents = new HashMap<>();
		contents.put(titles[2], new ArrayList<>(Arrays.asList(new String[] {goal})));
		
		List<String> sprintBacklogContents = new ArrayList<>();
		int totalEstimate = 0;
		for(JSONObject committedBacklogItemJSON : committedBacklogItemList) {
			String description = committedBacklogItemJSON.getString("description");
			int estimate = committedBacklogItemJSON.getInt("estimate");
			sprintBacklogContents.add(description + "(" + estimate + ")");
			totalEstimate += estimate;
		}
		sprintBacklogContents.add("Estimated velocity : " + totalEstimate + " points");
		contents.put(titles[3], sprintBacklogContents);
		
		List<String> scheduleContents = new ArrayList<>();
		scheduleContents.add("Sprint period : " + startDate + " to " + endDate);
		scheduleContents.add("Daily Scrum : " + daily);
		scheduleContents.add("Sprint demo : " + demoDate + " " + demoPlace);
		contents.put(titles[4], scheduleContents);
		
		return generateHtmlTemplateWithContent(titles, contents);
	}
	
	private String generateHtmlTemplateWithContent(String[] titles, Map<String, List<String>> contents) {
		List<String> goalContents = contents.get(titles[2]);
		List<String> sprintBacklogContents = contents.get(titles[3]);
		List<String> scheduleContents = contents.get(titles[4]);
		
		String template = "<html>" + 
				"<head>" + 
				"<title>" + 
				titles[0] + 
				"</title>" + 
				"<style>" + 
				"body {font-family: Arial, Helvetica, sans-serif;}" + 
				"h1 {font-size: 36px; font-weight: bolder;}" + 
				"h2 {font-size: 28px; font-weight: bolder;}" + 
				"li, p {font-size: 20px;}" + 
				"</style>" + 
				"</head>" + 
				"<body>" +
				"<center><h1><b>" + titles[1] + "</b></h1></center>" + 
				"<h2><b>" + titles[2] + "</b></h2>" + 
				"<ul>" + 
				"<li>" + goalContents.get(0) + "</li>" + 
				"</ul>" + 
				"<br/>" + 
				"<h2><b>" + titles[3] + "</b></h2>" + 
				"<ul>";
		
		for(int i = 0; i < sprintBacklogContents.size() - 1; i++) {
			template += "<li>" + sprintBacklogContents.get(i) + "</li>";
		}
		
		template += "</ul>" + 
				"<br/>" + 
				"<p>" + sprintBacklogContents.get(sprintBacklogContents.size() - 1) + "</p>" + 
				"<br/>" + 
				"<h2><b>" + titles[4] + "</b></h2>" + 
				"<ul>"; 
		
		for(String scheduleContent : scheduleContents) {
			template += "<li>" + scheduleContent + "</li>";
		}
		
		template += "</ul>" + 
				"<br/>" + 
				"</body>" + 
				"</html>";
		
		return template;
	}
}
