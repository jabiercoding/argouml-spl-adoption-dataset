package revisiongraph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class Main_generateGitGraph {
	static Map<String, Integer> headers = new HashMap<String, Integer>();
	
	// We keep track of the used master or branch names
	static List<String> masterBranches = new ArrayList<String>();

	public static void main(String[] args) {
		try {
			XSSFWorkbook workbook = new XSSFWorkbook("ArgoUML_SPL_adoption_dataset.xlsx");
			XSSFSheet sheet = workbook.getSheetAt(0);

			// Create headers map
			Row firstRow = sheet.getRow(0);
			for (int i = 0; i < firstRow.getLastCellNum(); i++) {
				String header = firstRow.getCell(i).getStringCellValue();
				headers.put(header, i);
			}

			Iterator<Row> rowIterator = sheet.iterator();
			// skip first
			rowIterator.next();
			int i = 0;
			while (rowIterator.hasNext()) {
				i++;
				Row row = rowIterator.next();
				String masterBranch = get(row, "MasterBranch");

				if (masterBranch == null || masterBranch.isEmpty()) {
					// finished
					break;
				}
				
				boolean isTag = false;
				if(masterBranch.equals("tags")) {
					masterBranch = get(row, "BranchCreatedFrom");
					isTag = true;
				}
				
				// check if we need to create the branch
				if (!masterBranches.contains(masterBranch)) {
					masterBranches.add(masterBranch);
					String branchCreatedFrom = get(row, "BranchCreatedFrom");
					if(branchCreatedFrom == null || branchCreatedFrom.isEmpty()) {
						branchCreatedFrom = "gitgraph";
					}
					System.out.println("const " + masterBranch + "= " + branchCreatedFrom + ".branch(\"" + masterBranch + "\");");
				}
				
				// message only in one line
				String message = get(row, "Message");
				message = message.replaceAll("\n", " ");
				message = message.replaceAll("\\\"", "\\\\\"");
				
				// check if it is merge
				String branchMergedFrom = get(row, "BranchMergedFrom");
				if(branchMergedFrom==null || branchMergedFrom.isEmpty()) {
					System.out.println(masterBranch + ".commit({ hash:\"" + i + "\",\n subject:\"" + message
							+ "\",\n author:\"" + get(row, "User") + "\"});");
				} else {
					System.out.println(masterBranch + ".merge(" + branchMergedFrom + ",{ hash:\"" + i + "\",\n subject:\"" + message
							+ "\",\n author:\"" + get(row, "User") + "\"});");
				}
				if(isTag) {
					System.out.println(masterBranch + ".tag(\"tag rev." + i + "\");");
				}
			}
			workbook.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static String get(Row row, String header) {
		Cell cell = row.getCell(headers.get(header));
		if (cell == null) {
			return null;
		}
		String value = cell.getStringCellValue();
		return value;
	}
}
