package svnJavaBuilder.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNLogEntryPath;
import org.tmatesoft.svn.core.SVNProperties;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.auth.BasicAuthenticationManager;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;

public class SvnJavaBuilder {

	public static void main(String[] args) throws Exception {

		String url = "svn url";
		String id = "id";
		String passwd = "passwd";
		String targetPath = "d:/build/";
		long revNum = 0;
		
		SvnJavaBuilder sjb = new SvnJavaBuilder();
		sjb.getSvnInfo(url, id, passwd, targetPath, revNum);
	}
	
	public void getSvnInfo(String url, String id, String passwd, String targetPath, long revNum){
		try {
			SVNURL svnUrl = SVNURL.parseURIDecoded(url);
			SVNRepository svnRepo = SVNRepositoryFactory.create(svnUrl);

			BasicAuthenticationManager authManager = new BasicAuthenticationManager(id, passwd);
			svnRepo.setAuthenticationManager(authManager);

			long latestRevision = revNum == 0 ? svnRepo.getLatestRevision() : revNum;

			long startRevision = latestRevision;
			long endRevision = latestRevision;

			Collection<SVNLogEntry> logEntries = svnRepo.log(new String[] {""}, null, startRevision, endRevision, true, true);
			

			Iterator<SVNLogEntry> entries = logEntries.iterator();
			while (entries.hasNext()) {
				SVNLogEntry logEntry = entries.next();
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

				Set changedPathsSet = logEntry.getChangedPaths().keySet();
				Iterator changedPaths = changedPathsSet.iterator();
				while (changedPaths.hasNext()) {
					SVNLogEntryPath entryPath = (SVNLogEntryPath) logEntry.getChangedPaths().get(changedPaths.next());
					this.getSVNFileContentToFile(svnRepo, latestRevision, entryPath.getPath(), targetPath);
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	public void getSVNFileContentToFile(SVNRepository svnRepo, long revNum, String path, String targetPath)
			throws Exception {
		FileOutputStream outputStream = null;

		try {
			String[] splitPath = path.split("/");
			String folder = targetPath + splitPath[1] + "/" + revNum + "/";
			String subFolder = "";

			for (int i = 2; i < splitPath.length - 1; i++) {
				subFolder += "/" + splitPath[i];
			}

			String fileName = path.substring(path.lastIndexOf("/") + 1, path.length());
			File fileFolder = new File(folder + subFolder);

			if (!fileFolder.exists()) {
				fileFolder.mkdirs();
			}

			String fullFileName = fileFolder + "/" + fileName;

			File newFile = new File(fullFileName);

			outputStream = new FileOutputStream(newFile);
			svnRepo.getFile(path, revNum, new SVNProperties(), outputStream);

			flush(outputStream);

			if (fileName.toLowerCase().indexOf(".java") > -1) {
				this.runCompileCmd(fullFileName);
			}

		} catch (Exception e) {
			e.printStackTrace();

		} finally {
			close(outputStream);
		}
	}
	
	public void runCompileCmd(String fullFileName) throws Exception {

		String encoding = " -encoding UTF-8";
		String sourcePath = " -sourcepath src path";
		String classPath = " -classpath lib path";
		String runCmd = "javac.exe" + encoding + sourcePath + classPath + " " + fullFileName;
		String[] cmd = new String[] { "cmd.exe", "/c", runCmd };
		
		Runtime run = Runtime.getRuntime();
		Process process = null;
		try {
			process = run.exec(cmd);
			printStream(process);
		} catch (Exception e) {
			// TODO: handle exception
		} finally {
			if(run != null){
				run.gc();
			}
			if(process != null){
				process.destroy();
			}
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

	public void printStream(Process process) throws IOException, InterruptedException {
		process.waitFor();
		try (InputStream psout = process.getInputStream()) {
			copy(psout, System.out);
		}
	}

	public void copy(InputStream input, OutputStream output) throws IOException {
		byte[] buffer = new byte[1024];
		int n = 0;
		while ((n = input.read(buffer)) != -1) {
			output.write(buffer, 0, n);
		}
	}
}
