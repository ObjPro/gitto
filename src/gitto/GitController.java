package gitto;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

import java.io.File;
import java.util.Date;

public class GitController {

    private static GitController controller = new GitController();
    private Git git;

    private GitController() {}

    public static GitController sharedController() {
        return controller;
    }

    public File findRepository(File dir) {
        FileRepositoryBuilder builder = new FileRepositoryBuilder().findGitDir(dir);

        if (builder.getGitDir() == null) {
            return null;
        }

        try {
            this.git = new Git(builder.build());
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        return builder.getGitDir();
    }


    public File getCurrentRepository() {
        return this.git.getRepository().getDirectory();
    }

    public void init(File dir) {
        try {
            this.git = Git.init().setDirectory(dir).setBare(false).call();
            System.out.println("git init");
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public void add(File file) {
        try {
            // .gitのあるディレクトリからの相対パスを生成
            String path = file.getAbsolutePath().replaceAll(this.git.getRepository().getDirectory().getParent(), "");;

            if (file.isFile()) {
                path = file.getAbsolutePath().replaceAll(this.git.getRepository().getDirectory().getParent() + "/", "");
            } else if (file.getAbsolutePath().equals(this.git.getRepository().getDirectory().getParent())) { // カレントディレクトリ
                path = ".";
            }

            System.out.println("git add " + path);
            this.git.add().addFilepattern(path).call();

        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public void commit(){
        try{
            String commitMessage = new Date().toString();
            this.git.commit().setMessage(commitMessage).call();
            System.out.println("git commit -m " + commitMessage);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

}
