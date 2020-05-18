package argoumlsplvcs;

import java.io.File;

import org.tmatesoft.svn.core.SVNDepth;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc.SVNWCUtil;
import org.tmatesoft.svn.core.wc2.SvnCheckout;
import org.tmatesoft.svn.core.wc2.SvnOperationFactory;
import org.tmatesoft.svn.core.wc2.SvnTarget;

public class Main2_DownloadRevisions {
	
	// List obtained from ChangesMain.java
	public static int[] revisionsWithJavaChanges = new int[] { 12, 14, 15, 16, 17, 18, 19, 23, 26, 27, 29, 33, 34, 35, 36, 37,
			38, 39, 40, 41, 43, 53, 54, 69, 77, 78, 79, 88, 89, 91, 103, 115, 119, 120, 121, 122, 123, 124, 127,
			132, 135, 143, 148, 149, 150, 151, 152, 153, 154, 155, 156 };

	public static void main(String[] args) {

		final SvnOperationFactory svnOperationFactory = new SvnOperationFactory();
		try {
			SVNURL url = SVNURL.parseURIEncoded("http://argouml-spl.tigris.org/svn/argouml-spl/trunk/src");
			ISVNAuthenticationManager authManager = SVNWCUtil.createDefaultAuthenticationManager("guest",
					(char[]) null);
			svnOperationFactory.setAuthenticationManager(authManager);

			
			for (int revisionNumber : revisionsWithJavaChanges) {
				// Checkout
				final SvnCheckout checkout = svnOperationFactory.createCheckout();
				File outputFolder = new File("C:/outputsvnkit/Revision" + revisionNumber);
				if (!outputFolder.exists()) {
					outputFolder.mkdirs();
					checkout.setSingleTarget(SvnTarget.fromFile(outputFolder));
					checkout.setSource(SvnTarget.fromURL(url));
					checkout.setDepth(SVNDepth.INFINITY);
					SVNRevision revision = SVNRevision.create(revisionNumber);
					checkout.setRevision(revision);
					checkout.run();
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			svnOperationFactory.dispose();
		}
	}
}
