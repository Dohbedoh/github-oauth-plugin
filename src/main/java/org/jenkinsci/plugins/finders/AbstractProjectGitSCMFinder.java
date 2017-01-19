package org.jenkinsci.plugins.finders;

import hudson.Extension;
import hudson.model.AbstractItem;
import hudson.model.AbstractProject;
import hudson.plugins.git.GitSCM;
import hudson.scm.SCM;
import org.jenkinsci.plugins.GitSCMFinder;

/**
 * Implementation of {@link GitSCMFinder} for {@link AbstractProject}.
 *
 * @author Allan Burdajewicz
 */
@Extension
public class AbstractProjectGitSCMFinder extends GitSCMFinder {

    @Override
    public GitSCM getSCM(AbstractItem item) {
        if (item instanceof AbstractProject) {
            SCM scm = ((AbstractProject) item).getScm();
            if (scm instanceof GitSCM) {
                return (GitSCM) scm;
            }
        }
        return null;
    }

    @Override
    public boolean isFindable(AbstractItem item) {
        return item instanceof AbstractProject;
    }
}