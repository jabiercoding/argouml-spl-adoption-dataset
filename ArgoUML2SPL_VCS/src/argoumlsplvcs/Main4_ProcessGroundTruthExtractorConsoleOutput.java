package argoumlsplvcs;

import java.io.File;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import utils.FileUtils;

public class Main4_ProcessGroundTruthExtractorConsoleOutput {

	public static void main(String[] args) {
		File consoleOutput = new File("console.txt");
		List<String> lines = FileUtils.getLinesOfFile(consoleOutput);
		int currentRevision = 0;
		Map<String, Integer> map = null; // new HashMap<String, Integer>();
		Map<Integer, Map<String, Integer>> revmap = new LinkedHashMap<Integer, Map<String, Integer>>();
		for (String line : lines) {
			if (line.startsWith("MetricsMain: Revision")) {
				currentRevision = Integer.parseInt(line.substring("MetricsMain: Revision".length()));
				map = new HashMap<String, Integer>(); 
			}
			if (line.startsWith("LoC_info;")) {
				String[] split = line.split(";");
				String feat = split[1];
				Integer loc = Integer.parseInt(split[2]);
				Integer i = map.get(feat);
				if(i == null) {
					map.put(feat, loc);
				} else {
					map.put(feat, i + loc);
				}
				revmap.put(currentRevision, map);
			}
		}
		
		// Get complete list of features
		List<String> features = new ArrayList<String>();
		for(Integer re : revmap.keySet()) {
			Map<String, Integer> mapi = revmap.get(re);
			for(String k : mapi.keySet()) {
				if(!features.contains(k)) {
					features.add(k);
				}
			}
		}
		// Sort them
		Collections.sort(features, Collator.getInstance());
		
		// Create matrix
		System.out.println();
		System.out.print("Revision;");
		for (String f: features) {
			System.out.print(f + ";");
		}
		
		System.out.println();
		for(Integer re : revmap.keySet()) {
			System.out.print(re + ";");
			Map<String, Integer> mapi = revmap.get(re);
			for(String f : features) {
				Integer loc = mapi.get(f);
				if(loc==null) {
					loc = 0;
				}
				System.out.print(loc + ";");
			}
			System.out.println();
		}
		
		
	}
}
