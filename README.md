# ArgoUML SPL adoption dataset
A dataset of a software product line extraction process

The project `ArgoUML2SPL_VCS` contains
* Data obtained and processed from the Version Control System (VCS) of ArgoUML SPL http://argouml-spl.tigris.org/ or the log of TortoiseSVN tool. Most of the data was automatically obtained from Java main methods but some of them required manual completion.
* Java main methods used to get and process the data.

### Dataset
* **ArgoUML_SPL_adoption_dataset.xlsx**: Information from svnTortoiseExport.txt but structured in an excel file and including information about branches creation and merges. Note that the original VCS was based on SVN where branching is different than in Git. We had to manually analyse the data to detect the branch creations and merges. Notice that https://github.com/argouml-tigris-org/argouml-spl/network fails to provide this information.
* **svnTortoiseExport.txt**: The export of TortoiseSVN log containing information from revisions 1 to 156. Author, date, message, files added, modified or deleted.
* **revisionGraph.html**: A visualisation of the commits and the branches using GitGraph.js. Author and messages included. 
* **matrix.xlsx**: For each revision with Java changes, the number of Lines of Code corresponding to each feature or feature interaction. This is interesting to see the evolution during the process of annotating the optional source code.
* **console.txt**: The output in the console from the GroundTruthExtractor of the ArgoUML SPL feature location benchmark https://github.com/but4reuse/argouml-spl-benchmark for each revision. This was used for the creation of matrix.xlsx.

### Java main methods
* **Main1_RevisionWithJavaChanges**: 
Inputs: svn repository and the selected branch or trunk. Output: List of revision numbers where Java changes has occurred. For example, in revision 1 we had some Java code. Then in revision 2 only a xml file was modified. Then in revision 3 Java code was modified. Then 3 will be in the list, but not 2.

* **Main2_DownloadRevisions**: 
Input: Manually include the list of revision numbers obtained in Main1 in the array of Main2. Output: A complete download of the repository at the given revisions, each in one separated folder.

* **Main3_GetFeaturesData**: 
The ArgoUMLSPLBenchmark project needs to be in the workspace as well. This is because the ground-truth extractor is needed, it outputs in the console information about the number of lines of code of the core feature and of the different features and feature interactions.
Inputs: A folder containing all the revisions in separated folders. 
Output: A ground-truth file in the ArgoUML SPL benchmark format with traces from features (feature interactions and negations) to Java code parts. The content of the console, will have to be manually copy-pasted and manually create a console.txt file. Check in Eclipse preferences that the console has no limit of lines/characters, otherwise content will be cropped.

* **Main4_ProcessGroundTruthExtractorConsoleOutput**: 
Input: a console.txt file. Output: a matrix.

* **Main5_RevisionDates**: 
Input: svnTortoiseExport.txt. Output: The date of each revision in dd/mm/yyyy format, and the number of commits per day.

* **Main_generateGitGraph**: 
Input: the ArgoUML_SPL_adoption_dataset.xlsx. Output: html code using GitGraph.js. Used to create revisionGraph.html.

* **Main_getFeatureEffort**: 
Input: the matrix.xlsx. Output: For each feature, effort information as the number of months used to extract each feature.

Note: The original VCS http://argouml-spl.tigris.org/ based on SVN will no longer exist. It was moved to a git based VCS on github https://github.com/argouml-tigris-org/argouml-spl maintaining the commit history.
