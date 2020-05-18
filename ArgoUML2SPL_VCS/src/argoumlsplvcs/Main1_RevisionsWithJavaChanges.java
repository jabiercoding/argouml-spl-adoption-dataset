package argoumlsplvcs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNLogEntryPath;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

public class Main1_RevisionsWithJavaChanges {
	public static void main(String[] args) {

		try {
			DAVRepositoryFactory.setup();
			long startRevision = 0;
			long endRevision = -1; // HEAD (the latest) revision
			SVNURL url = SVNURL.parseURIEncoded("http://argouml-spl.tigris.org/svn/argouml-spl/trunk/src");
			ISVNAuthenticationManager authManager = SVNWCUtil.createDefaultAuthenticationManager("guest",
					(char[]) null);
			SVNRepository repository = SVNRepositoryFactory.create(url);
			repository.setAuthenticationManager(authManager);

			Collection logEntries = repository.log(new String[] { "" }, null, startRevision, endRevision, true, true);

			List<Integer> revisionsWithJavaChanges = new ArrayList<Integer>();
			System.out.println("revision;author;date;logMessage;changedPaths");
			for (Iterator entries = logEntries.iterator(); entries.hasNext();) {
				SVNLogEntry logEntry = (SVNLogEntry) entries.next();
				System.out.print(logEntry.getRevision() + ";" + logEntry.getAuthor() + ";"
						+ logEntry.getMessage().replaceAll("\n", " ") + ";");

				if (logEntry.getChangedPaths().size() > 0) {
					// System.out.println();
					// System.out.println("changed paths:");
					Set changedPathsSet = logEntry.getChangedPaths().keySet();

					for (Iterator changedPaths = changedPathsSet.iterator(); changedPaths.hasNext();) {
						SVNLogEntryPath entryPath = (SVNLogEntryPath) logEntry.getChangedPaths()
								.get(changedPaths.next());
						System.out.print(" " + entryPath.getType() + " " + entryPath.getPath()
								+ ((entryPath.getCopyPath() != null) ? " (from " + entryPath.getCopyPath()
										+ " revision " + entryPath.getCopyRevision() + ")" : "")
								+ ";");

						if (entryPath.getPath().startsWith("/trunk/src/") && entryPath.getPath().endsWith(".java")) {
							if (!revisionsWithJavaChanges.contains((int) logEntry.getRevision())) {
								revisionsWithJavaChanges.add((int) logEntry.getRevision());
							}
						}
					}

				}
				System.out.println();
			}
			System.out.println(revisionsWithJavaChanges.size());
			System.out.println(revisionsWithJavaChanges);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
