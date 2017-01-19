package org.jenkinsci.plugins;

import hudson.ExtensionPoint;
import hudson.model.AbstractItem;
import hudson.plugins.git.GitSCM;
import jenkins.model.Jenkins;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

/**
 * Class that provides method to find {@link GitSCM} for a specific {@link AbstractItem}.
 *
 * @author Allan Burdajewicz
 */
public abstract class GitSCMFinder implements ExtensionPoint {

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
     * {@link GitSCMFinder} and return the first non null result.
     *
     * @param item The item
     * @return The corresponding {@link GitSCM} or null if one cannot be found
     */
    @CheckForNull
    static GitSCM find(@Nonnull AbstractItem item) {

        GitSCM result = null;

        for (GitSCMFinder f : Jenkins.getInstance().getExtensionList(GitSCMFinder.class)) {
            result = f.getSCM(item);
            if (result != null) {
                break;
            }
        }

        return result;
    }

    /**
     * Check if there is at least one {@link GitSCMFinder} implementation applicable to job passed in.
     * @param item The item
     * @return return true if there is an applicable implementation for this item
     */
    @CheckForNull
    static boolean isApplicable(@Nonnull AbstractItem item) {
        for (GitSCMFinder f : Jenkins.getInstance().getExtensionList(GitSCMFinder.class)) {
            if (f.isFindable(item)) {
                return true;
            }
        }
        return false;
    }
}