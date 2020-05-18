package featureEffort;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class Main_getFeatureEffort {

	private static final int REV_NUMBER_INITIALEXTRACTIONBEFOREMAINTENANCE = 135;

	public static void main(String[] args) {
		
		try {
			XSSFWorkbook workbook = new XSSFWorkbook("matrix.xlsx");
			XSSFSheet sheet = workbook.getSheetAt(0);
			String[] features = new String[] { "ACTIVITYDIAGRAM", "COGNITIVE", "COLLABORATIONDIAGRAM", "DEPLOYMENTDIAGRAM", "LOGGING", "SEQUENCEDIAGRAM", "STATEDIAGRAM", "USECASEDIAGRAM" };

			// for each feature
			for (String feature : features) {
				System.out.println(feature);
				int minRevisionIndex = Integer.MAX_VALUE;
				int maxRevisionIndex = Integer.MIN_VALUE;
				int maxRevisionInitialExtractionIndex = Integer.MIN_VALUE;

				Iterator<Row> rowIterator = sheet.iterator();
				// skip dates and revisions
				Row dates = rowIterator.next();
				Row revisionNumbers = rowIterator.next();

				int i = 0;
				int[] changeSizes = new int[dates.getLastCellNum()];
				
				while (rowIterator.hasNext()) {
					i++;
					Row row = rowIterator.next();
					Cell cell = row.getCell(0);
					String value = cell.getStringCellValue();
					
					// rows where the current feature is involved (including feature interactions)
					if (value.contains(feature)) {
						int previousValue = 0;
						for (int x = 1; x < row.getLastCellNum(); x++) {
							int currentValue = (int) row.getCell(x).getNumericCellValue();
							
							if (previousValue != currentValue) {
								
								changeSizes[x] = changeSizes[x] + Math.abs(currentValue - previousValue);
								
								if (maxRevisionIndex < x) {
									maxRevisionIndex = x;
								}
								if (minRevisionIndex > x) {
									minRevisionIndex = x;
								}
								String rev = revisionNumbers.getCell(x).getStringCellValue();
								int revNumber = Integer.parseInt(rev.split(" ")[1].trim());
								if (revNumber <= REV_NUMBER_INITIALEXTRACTIONBEFOREMAINTENANCE && maxRevisionInitialExtractionIndex < x) {
									maxRevisionInitialExtractionIndex = x;
								}
								previousValue = currentValue;
							}
						}
					}
				}
				
				// Hardcoded start for some features because the matrix dataset only has info about the trunk
				Date date1;
				Date date2 = dates.getCell(maxRevisionIndex).getDateCellValue();
				int revision1;
				int revision2;
				int revisionInitialExtraction;
	
				// new Date(int year, int month, int date);
				if(feature.equals("SEQUENCEDIAGRAM")) {
					revision1 = 80;
					date1 = new GregorianCalendar(2010, Calendar.JUNE, 21).getTime();
				} else if(feature.equals("USECASEDIAGRAM")) {
					revision1 = 81;
					date1 = new GregorianCalendar(2010, Calendar.JUNE, 21).getTime();
				} else if (feature.equals("COLLABORATIONDIAGRAM")) {
					revision1 = 95;
					date1 = new GregorianCalendar(2010, Calendar.AUGUST, 12).getTime();
				} else if (feature.equals("DEPLOYMENTDIAGRAM")) {
					revision1 = 98;
					date1 = new GregorianCalendar(2010, Calendar.AUGUST, 12).getTime();
				} else {
					String revisionString = revisionNumbers.getCell(minRevisionIndex).getStringCellValue();
					revision1 = Integer.parseInt(revisionString.substring("Revision ".length()));
					date1 = dates.getCell(minRevisionIndex).getDateCellValue();
				}
				
				System.out.println("GLOBAL INCLUDING MAINTENANCE");
				SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");    
				System.out.println(formatter.format(date1) + " , " + formatter.format(date2));
				
				String revisionString = revisionNumbers.getCell(maxRevisionIndex).getStringCellValue();
				revision2 = Integer.parseInt(revisionString.substring("Revision ".length()));
				
				long diff = date2.getTime() - date1.getTime();
			    System.out.println ("Days: " + TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS));
			    System.out.println ("Months: " + TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS) / 30.0);
				System.out.println("From revision to revision: " + revision1 + " -> " + revision2);
				System.out.println("Revisions in between (all revisions): " + (revision2 - revision1));
				
				System.out.println("Activity in the trunk (revisions with Java code changes)");
				
				int firstIndexWithValue = 0;
				int lastIndexWithValue = 0;
				int maxValue = Integer.MIN_VALUE;
				boolean valueFound = false;
				for (int j = 0; j < changeSizes.length; j++) {
					int value = changeSizes[j];
					if(value > maxValue) {
						maxValue = value;
					}
					if(value != 0) {
						lastIndexWithValue = j;
						if (!valueFound) {
							firstIndexWithValue = j;
							valueFound = true;
						}
					}
				}
				
				for (int j = firstIndexWithValue; j <= lastIndexWithValue; j++) {
					System.out.print(changeSizes[j] + ",");
				}
				System.out.println();
				
				// normalized
				for (int j = firstIndexWithValue; j <= lastIndexWithValue; j++) {
					double normalizedValue = (double)changeSizes[j] / (double)maxValue;
					System.out.print("(" + (j-firstIndexWithValue) + "," + normalizedValue + ") ");
				}
				
				System.out.println();
				System.out.println("SPL EXTRACTION: BEFORE THE MAINTENANCE PART");

				String revisionInitialExtractionString = revisionNumbers.getCell(maxRevisionInitialExtractionIndex).getStringCellValue();
				revisionInitialExtraction = Integer.parseInt(revisionInitialExtractionString.substring("Revision ".length()));
				
				Date date2InitialExtraction = dates.getCell(maxRevisionInitialExtractionIndex).getDateCellValue();
				System.out.println(formatter.format(date1) + " , " + formatter.format(date2InitialExtraction));
				
				long diff2 = date2InitialExtraction.getTime() - date1.getTime();
			    System.out.println ("Days: " + TimeUnit.DAYS.convert(diff2, TimeUnit.MILLISECONDS));
			    System.out.println ("Months: " + TimeUnit.DAYS.convert(diff2, TimeUnit.MILLISECONDS) / 30.0);
				System.out.println("From revision to revision: " + revision1 + " -> " + revisionInitialExtraction);
				System.out.println("Revisions in between (all revisions): " + (revisionInitialExtraction - revision1));

				System.out.println();
				System.out.println();
			}

			// workbook.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
