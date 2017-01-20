package org.jenkinsci.plugins;

import hudson.ExtensionPoint;
import hudson.model.AbstractItem;
import hudson.plugins.git.GitSCM;
import hudson.plugins.git.UserRemoteConfig;
import jenkins.model.Jenkins;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import java.util.List;

/**
 * Class that provides method to find {@link GitSCM} for a specific {@link AbstractItem}.
 *
 * @author Allan Burdajewicz
 */
public abstract class GitHubRepositoryResolver implements ExtensionPoint {

    /**
     * Find the {@link GitSCM} corresponding to the job passed in.
     * @param item The item
     * @return The corresponding {@link GitSCM} or null if one cannot be found
     */
    protected abstract GitSCM getSCM(AbstractItem item);

    /**
     * Check if the job passed in is applicable.
     * @param item The item
     * @return return true if there is an applicable implementation for this item
     */
    protected abstract boolean isFindable(AbstractItem item);

    /**
     * Find the {@link GitSCM} corresponding to the job passed in. Loop through the implementations of
     * {@link GitHubRepositoryResolver} and return the first non null result.
     *
     * @param item The item
     * @return The corresponding {@link GitSCM} or null if one cannot be found
     */
    @CheckForNull
    static String find(@Nonnull AbstractItem item) {

        String repositoryName = null;

        for (GitHubRepositoryResolver f : Jenkins.getInstance().getExtensionList(GitHubRepositoryResolver.class)) {
            GitSCM gitScm = f.getSCM(item);
            if (gitScm != null) {
                List<UserRemoteConfig> userRemoteConfigs = gitScm.getUserRemoteConfigs();
                if (!userRemoteConfigs.isEmpty()) {
                    String repoUrl = userRemoteConfigs.get(0).getUrl();
                    if (repoUrl != null) {
                        GitHubRepositoryName githubRepositoryName = GitHubRepositoryName.create(repoUrl);
                        if (githubRepositoryName != null) {
                            repositoryName = githubRepositoryName.userName + "/" + githubRepositoryName.repositoryName;
                        }
                    }
                }
                break;
            }
        }

        return repositoryName;
    }

    /**
     * Check if there is at least one {@link GitHubRepositoryResolver} implementation applicable to job passed in.
     * @param item The item
     * @return return true if there is an applicable implementation for this item
     */
    @CheckForNull
    static boolean isApplicable(@Nonnull AbstractItem item) {
        for (GitHubRepositoryResolver f : Jenkins.getInstance().getExtensionList(GitHubRepositoryResolver.class)) {
            if (f.isFindable(item)) {
                return true;
            }
        }
        return false;
    }
}