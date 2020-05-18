package argoumlsplvcs;

import java.io.File;
import java.util.List;

import groundTruthExtractor.GroundTruthExtractor;

public class Main3_GetFeaturesData {
	private static final int MAX_REVISION = 156;
	private static final String FOLDER_WITH_REVISIONS = "C:/outputsvnkit/";

	public static void main(String[] args) {
		File revisionsFolder = new File(FOLDER_WITH_REVISIONS);
		for (int revisionNumber = 0; revisionNumber <= MAX_REVISION; revisionNumber++) {
			File currentRevisionFolder = new File(revisionsFolder, "Revision" + revisionNumber);
			if (currentRevisionFolder.exists()) {
				System.out.println("MetricsMain: Revision" + revisionNumber);
				List<File> allJavaFiles = GroundTruthExtractor.getAllArgoUMLSPLRelevantJavaFiles(currentRevisionFolder);
				GroundTruthExtractor.extractGroundTruth(allJavaFiles, new File(revisionsFolder, "Revision" + revisionNumber + "_groundTruth"));
			}
		}
	}
}
