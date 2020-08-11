package svnJavaBuilder.ldcc.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNLogEntryPath;
import org.tmatesoft.svn.core.SVNProperties;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;

public class SvnJavaBuilder {

	public static void main(String[] args) throws Exception {
		String url = "svn://실제_접속가능한_SVN주소를_입력하기";
		// String svnUser = "";
		// String svnPassword = "";

		SVNURL svnUrl = SVNURL.parseURIDecoded(url);
		SVNRepository svnRepo = SVNRepositoryFactory.create(svnUrl);

		// BasicAuthenticationManager authManager = new
		// BasicAuthenticationManager(svnUser, svnPassword);
		// svnRepo.setAuthenticationManager(authManager);

		long latestRevision = svnRepo.getLatestRevision();
		System.out.println("latestRevision : " + latestRevision);

		long startRevision = latestRevision;
		long endRevision = latestRevision;

		Collection<SVNLogEntry> logEntries = null;
		logEntries = svnRepo.log(new String[] { "" }, null, startRevision, endRevision, true, true);

		Iterator entries = logEntries.iterator();
		while (entries.hasNext()) {
			SVNLogEntry logEntry = (SVNLogEntry) entries.next();
			if (logEntry == null) {
				continue;
			}

			System.out.println("---------------------------------------------");
			System.out.println("revision: " + logEntry.getRevision());
			System.out.println("author: " + logEntry.getAuthor());
			System.out.println("date: " + logEntry.getDate());
			System.out.println("log message: " + logEntry.getMessage());

			if (logEntry.getChangedPaths() == null || logEntry.getChangedPaths().size() == 0) {
				continue;
			}

			System.out.println();
			System.out.println("changed paths:");
			Set changedPathsSet = logEntry.getChangedPaths().keySet();
			Iterator changedPaths = changedPathsSet.iterator();
			while (changedPaths.hasNext()) {
				SVNLogEntryPath entryPath = (SVNLogEntryPath) logEntry.getChangedPaths().get(changedPaths.next());

				if (entryPath.getCopyPath() != null) {
					System.out.println(" " + entryPath.getType() + " " + entryPath.getPath() + "(from "
							+ entryPath.getCopyPath() + " revision " + entryPath.getCopyRevision() + ")");
				} else {
					System.out.println(" " + entryPath.getType() + " " + entryPath.getPath());
				}
			}
		}
	}

	public static void getSVNFileContentToFile(SVNRepository svnRepo, long revision, String path) {
		FileOutputStream outputStream = null;
		File file = null;

		try {
			file = new File("temp.txt");
			if (file.exists()) {
				file.delete();
				file.createNewFile();
			} else {
				file.createNewFile();
			}

			outputStream = new FileOutputStream(file);
			// svnRepo.getFile(path, SVNRevision.HEAD.getNumber(), new
			// SVNProperties(), outputStream);
			svnRepo.getFile(path, revision, new SVNProperties(), outputStream);

		} catch (Exception e) {
			e.printStackTrace();

		} finally {
			flush(outputStream);
			close(outputStream);
		}
	}

	public static void getSVNFileContent(SVNRepository svnRepo, long revision, String path) {
		ByteArrayOutputStream outputStream = null;

		try {
			outputStream = new ByteArrayOutputStream();
			// svnRepo.getFile(path, SVNRevision.HEAD.getNumber(), new
			// SVNProperties(), outputStream);
			svnRepo.getFile(path, revision, new SVNProperties(), outputStream);

			System.out.println(outputStream.toString());

		} catch (Exception e) {
			e.printStackTrace();

		} finally {
			flush(outputStream);
			close(outputStream);
		}
	}

	public static void flush(OutputStream outputStream) {
		try {
			if (outputStream != null) {
				outputStream.flush();
			}
		} catch (Exception e) {
			// ignore
		}
	}

	public static void close(OutputStream outputStream) {
		try {
			if (outputStream != null) {
				outputStream.close();
			}
		} catch (Exception e) {
			// ignore
		}
	}

}
