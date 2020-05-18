package argoumlsplvcs;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import utils.FileUtils;

public class Main5_RevisionDates {

	// to take all just a few
	static boolean onlyDatesOfRevisionWithJavaChanges = false;

	public static void main(String[] args) {
		Map<String,Integer> commitsPerDate = new LinkedHashMap<String,Integer>();
		
		List<String> lines = FileUtils.getLinesOfFile(new File("svnTortoiseExport.txt"));
		for (int i = 0; i < lines.size(); i++) {
			String line = lines.get(i);
			if (line.startsWith("Revision: ")) {
				String revisionNumber = line.substring("Revision: ".length(), line.length());
				boolean found = false;
				if (onlyDatesOfRevisionWithJavaChanges) {
					int rev = Integer.parseInt(revisionNumber);
					for (int x : Main2_DownloadRevisions.revisionsWithJavaChanges) {
						if (rev == x) {
							found = true;
						}
					}
				}
				if (!onlyDatesOfRevisionWithJavaChanges || (onlyDatesOfRevisionWithJavaChanges && found)) {
					System.out.print(revisionNumber);
					line = lines.get(i + 2);
					if (line.startsWith("Date: ")) {
						String date = (line.substring("Date: ".length(), line.length()));
						date = date.substring(date.indexOf(", ") + ", ".length(), date.lastIndexOf(" "));
						System.out.println(";" + date);
						
						Integer commits = commitsPerDate.get(date);
						if(commits==null) {
							commitsPerDate.put(date, 1);
						} else {
							commitsPerDate.put(date, commits + 1);
						}
					}
				}
			}
		}
		
	System.out.println("\n\n Number of commits per date");
		for(String key : commitsPerDate.keySet()) {
			String[] format = key.split("/");
			System.out.println(format[2] + "-" + format[1] + "-" + format[0] + "," + commitsPerDate.get(key));
		}
	}

}
