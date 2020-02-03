package ntut.csie.ezScrum.controller.maker;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

import org.json.JSONObject;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

public class PDFMaker {
	public File getTaskFile(String filePath, List<JSONObject> taskJSONs) throws Exception {
		BaseFont bfChinese = BaseFont.createFont(filePath, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
		File tempFile = File.createTempFile("ezScrum", Long.toString(System.nanoTime()));
		String path = tempFile.getAbsolutePath();
		Document document = new Document(PageSize.A4);
		PdfWriter.getInstance(document, new FileOutputStream(path));
		document.open();
		document.add(new Paragraph("\n")); //To make sure PDF is at least 1 page.
		// complete task size to even
		if (taskJSONs.size() % 2 == 1) {
			taskJSONs.add(null);
		}

		for (int i = 0; i < taskJSONs.size(); i += 2) {
			// generate table by task
			PdfPTable table = getPdfTableWithContent(bfChinese, taskJSONs.get(i), taskJSONs.get(i + 1));
			document.add(table);
			document.add(new Paragraph("\n"));
		}
		document.close();
		File file = new File(path);
		return file;
	}

	private PdfPTable getPdfTableWithContent(BaseFont bfChinese, JSONObject leftTaskJSON, JSONObject rightTaskJSON) throws Exception {
		PdfPTable table = generateCustomPdfPTable();
		// set left column cell
		PdfPCell leftColumnCell = new PdfPCell();
		String leftColumnCellContent = generateTaskCellContent(leftTaskJSON);
		Paragraph leftParagraph = new Paragraph(leftColumnCellContent, new Font(bfChinese, 12, Font.NORMAL));
		leftColumnCell.addElement(leftParagraph);
		table.addCell(leftColumnCell);
		// set middle column cell
		PdfPCell spaceCell = new PdfPCell();
		spaceCell.setBorder(PdfPCell.NO_BORDER);
		table.addCell(spaceCell);
		// set right column cell
		PdfPCell rightColumnCell = new PdfPCell();
		String rightColumnCellContent = generateTaskCellContent(rightTaskJSON);
		if(rightColumnCellContent.isEmpty()) {
			rightColumnCell.setBorder(PdfPCell.NO_BORDER);
		}else {
			Paragraph rightParagraph = new Paragraph(rightColumnCellContent, new Font(bfChinese, 12, Font.NORMAL));
			rightColumnCell.addElement(rightParagraph);
		}
		table.addCell(rightColumnCell);
		return table;
	}
	
	private PdfPTable generateCustomPdfPTable() {
		float tableWidth = 100f;
		int field = 3;
		float fieldWidth[] = { 4.5f, 1f, 4.5f };
		PdfPTable table = new PdfPTable(field);
		// 設定table的寬度
		table.setWidthPercentage(tableWidth);

		// 設定每個欄位的寬度
		try {
			table.setWidths(new float[] { fieldWidth[0], fieldWidth[1], fieldWidth[2] });
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		return table;
	}


	private String generateTaskCellContent(JSONObject taskJSON) throws Exception {
		if (taskJSON == null) {
			return "";
		}
		
		int orderId = taskJSON.getInt("orderId");
		String description = taskJSON.getString("description");
		int estimate = taskJSON.getInt("estimate");
		
		String taskCardContent = "Task Id # " + orderId + "\n" + description;
		int descriptionLength = description.length();
		if (descriptionLength < 175) {
			int addEndOfLineNum = descriptionLength / 35;
			for (int i = 0; i < (4 - addEndOfLineNum); i++) {
				taskCardContent += "\n";
			}
		}
		taskCardContent += estimate + " hrs";
		return taskCardContent;
	}

	public File getBacklogItemFile(String ttfPath, List<JSONObject> backlogItemJSONs) throws Exception {
		File temp = File.createTempFile("ezScrum", Long.toString(System.nanoTime()));
		String path = temp.getAbsolutePath();

		Document document = new Document(PageSize.A4);

		BaseFont bfChinese = BaseFont.createFont(ttfPath, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);

		PdfWriter.getInstance(document, new FileOutputStream(path));

		document.open();
		document.add(new Paragraph("\n")); //To make sure PDF is at least 1 page.

		for (JSONObject backlogItemJSON : backlogItemJSONs) {
			PdfPTable outerTable = new PdfPTable(1);
			outerTable.getDefaultCell().setBorder(PdfPCell.NO_BORDER);
			outerTable.getDefaultCell().setBorderWidth(1f);
			PdfPTable table = new PdfPTable(2);
			float[] widths = { 420f, 100f };
			table.setWidths(widths);

			PdfPCell cell;
			PdfPTable innerTable;
			Paragraph paragraph;

			int orderId = backlogItemJSON.getInt("orderId");
			String description = backlogItemJSON.getString("description");
			int estimate = backlogItemJSON.getInt("estimate");
			int importance = backlogItemJSON.getInt("importance");
			String notes = backlogItemJSON.getString("notes");
			
			// Title
			cell = new PdfPCell();
			cell.setBorder(PdfPCell.TOP | PdfPCell.LEFT | PdfPCell.RIGHT);
			cell.setBorderWidth(1f);
			cell.setColspan(2);
			Paragraph titleParagraph = new Paragraph("Sprint Backlog Item #" + orderId,
					new Font(bfChinese, 12, Font.NORMAL));
			cell.setPaddingLeft(6f);
			cell.addElement(titleParagraph);

			table.addCell(cell);

			// Tip
			cell = new PdfPCell();
			cell.setBorder(PdfPCell.LEFT);
			cell.setBorderWidth(1f);
			table.addCell(cell);

			cell = new PdfPCell();
			cell.setBorder(PdfPCell.RIGHT);
			cell.setBorderWidth(1f);
			paragraph = new Paragraph("Importance", new Font(bfChinese, 12, Font.NORMAL));
			paragraph.setAlignment(Paragraph.ALIGN_CENTER);
			cell.addElement(paragraph);
			table.addCell(cell);

			// Summary and importance
			cell = new PdfPCell();
			cell.setBorder(PdfPCell.LEFT);
			cell.setBorderWidth(1f);
			cell.setPaddingLeft(6f);
			cell.addElement(new Paragraph(description, new Font(bfChinese, 14, Font.NORMAL)));
			table.addCell(cell);

			innerTable = new PdfPTable(1);
			innerTable.setTotalWidth(80f);

			cell = new PdfPCell();
			cell.setBorderWidth(1f);
			cell.setMinimumHeight(60f);
			cell.setPaddingTop(-10f);
			cell.setVerticalAlignment(PdfPCell.ALIGN_TOP);
			paragraph = new Paragraph(String.valueOf(importance), new Font(bfChinese, 36, Font.NORMAL));
			paragraph.setAlignment(Paragraph.ALIGN_CENTER);

			cell.addElement(paragraph);
			innerTable.addCell(cell);

			cell = new PdfPCell(innerTable);
			cell.setBorder(PdfPCell.RIGHT);
			cell.setBorderWidth(1f);
			cell.setPadding(5f);

			table.addCell(cell);

			// Tip
			cell = new PdfPCell();
			cell.setBorder(PdfPCell.LEFT);
			cell.setBorderWidth(1f);
			cell.setPaddingLeft(6f);
			paragraph = new Paragraph("Notes", new Font(bfChinese, 12, Font.NORMAL));
			cell.addElement(paragraph);
			table.addCell(cell);

			cell = new PdfPCell();
			cell.setBorder(PdfPCell.RIGHT);
			cell.setBorderWidth(1f);
			paragraph = new Paragraph("Estimate", new Font(bfChinese, 12, Font.NORMAL));
			paragraph.setAlignment(Paragraph.ALIGN_CENTER);
			cell.addElement(paragraph);
			table.addCell(cell);

			// Notes and estimated
			innerTable = new PdfPTable(1);
			innerTable.setTotalWidth(80f);

			cell = new PdfPCell();
			cell.setBorderWidth(1f);
			cell.setMinimumHeight(60f);
			cell.setPaddingBottom(10f);
			cell.addElement(new Paragraph(notes, new Font(bfChinese, 14, Font.NORMAL)));

			innerTable.addCell(cell);

			cell = new PdfPCell();
			cell.setBorder(PdfPCell.NO_BORDER);
			innerTable.addCell(cell);

			cell = new PdfPCell(innerTable);
			cell.setBorder(PdfPCell.LEFT);
			cell.setBorderWidth(1f);
			cell.setPadding(5f);

			table.addCell(cell);

			innerTable = new PdfPTable(1);
			innerTable.setTotalWidth(80f);
			innerTable.setExtendLastRow(false);

			cell = new PdfPCell();
			cell.setBorderWidth(1f);
			cell.setMinimumHeight(60f);
			cell.setUseAscender(false);
			cell.setPaddingTop(-10f);
			paragraph = new Paragraph(String.valueOf(estimate), new Font(bfChinese, 36, Font.NORMAL));
			paragraph.setAlignment(Paragraph.ALIGN_CENTER);

			cell.addElement(paragraph);
			innerTable.addCell(cell);

			cell = new PdfPCell();
			cell.setBorder(PdfPCell.NO_BORDER);
			innerTable.addCell(cell);

			cell = new PdfPCell(innerTable);
			cell.setBorder(PdfPCell.RIGHT);
			cell.setBorderWidth(1f);
			cell.setPadding(5f);

			table.addCell(cell);

			cell = new PdfPCell();
			cell.setBorder(PdfPCell.BOTTOM | PdfPCell.LEFT);
			cell.setBorderWidth(1f);
			table.addCell(cell);

			cell = new PdfPCell();
			cell.setBorder(PdfPCell.RIGHT | PdfPCell.BOTTOM);
			cell.setBorderWidth(1f);
			table.addCell(cell);

			outerTable.addCell(table);
			document.add(outerTable);
			document.add(new Paragraph("\n"));
		}
			
		document.close();
		File file = new File(path);

		return file;

	}
}
