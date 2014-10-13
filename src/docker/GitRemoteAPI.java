package docker;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Collection;
import java.util.Date;

import org.eclipse.jgit.api.AddCommand;
import org.eclipse.jgit.api.CheckoutCommand;
import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.CommitCommand;
import org.eclipse.jgit.api.CreateBranchCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.LsRemoteCommand;
import org.eclipse.jgit.api.PushCommand;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

public class GitRemoteAPI {
	private String username = "";
	private String password = "";
	private String baseRepoName = "";
	private String remotePath = "${git.url}/git/" + username + "/"
			+ baseRepoName + ".git";

	public static void main(String[] args) {
		String projectName = "testproject";
		new GitRemoteAPI().execute(projectName);
	}

	public boolean isExistBranch(String targetRef, Collection<Ref> refs) {
		if (targetRef == null) {
			return false;
		}
		if (refs == null) {
			return false;
		}
		for (Ref ref : refs) {
			String name = ref.getName();
			if (targetRef.equals(name)) {
				return true;
			}
		}
		return false;
	}

	public void execute(String branchName) {
		File localPath = new File("repos");
		delete(localPath);
		try {
			FileRepository repository = new FileRepository(new File(localPath,
					".git"));
			Git git = new Git(repository);
			LsRemoteCommand lsRemote = Git.lsRemoteRepository();
			lsRemote.setRemote(remotePath);
			lsRemote.setHeads(true);

			if (isExistBranch("refs/heads/" + branchName, lsRemote.call())) {
				CloneCommand clone = Git.cloneRepository();
				clone.setBare(false);
				clone.setURI(remotePath);
				clone.setBranch(branchName);
				clone.setDirectory(localPath);
				clone.call();
			} else {
				CloneCommand clone = Git.cloneRepository();
				clone.setBare(false);
				clone.setURI(remotePath);
				clone.setDirectory(localPath);
				clone.call();

				CreateBranchCommand createBranch = git.branchCreate();
				createBranch.setName(branchName);
				createBranch.call();

				CheckoutCommand checkout = git.checkout();
				checkout.setName(branchName);
				checkout.call();
			}

			System.out.println("update test.txt");
			BufferedWriter writer = new BufferedWriter(new FileWriter(new File(
					localPath, "test.txt")));
			writer.write(new Date() + "\n");
			writer.close();

			System.out.println("add test.txt");
			AddCommand add = git.add();
			add.addFilepattern("test.txt");
			add.call();

			System.out.println("commit test.txt");
			CommitCommand commit = git.commit();
			commit.setMessage("jgit comment.");
			commit.call();

			UsernamePasswordCredentialsProvider cp = new UsernamePasswordCredentialsProvider(
					username, password);
			PushCommand push = git.push();
			push.setCredentialsProvider(cp);
			push.call();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	void delete(File root) {
		if (root == null) {
			return;
		}
		if (!root.exists()) {
			return;
		}
		if (root.isFile()) {
		} else {
			for (File file : root.listFiles()) {
				delete(file);
			}
		}
		root.delete();
	}
}
