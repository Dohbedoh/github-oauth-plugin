package org.jenkinsci.plugins.resolvers;

import hudson.Extension;
import hudson.model.AbstractItem;
import hudson.model.AbstractProject;
import hudson.plugins.git.GitSCM;
import hudson.scm.SCM;
import org.jenkinsci.plugins.GitHubRepositoryResolver;

/**
 * Implementation of {@link GitHubRepositoryResolver} for {@link AbstractProject}.
 *
 * @author Allan Burdajewicz
 */
@Extension
public class AbstractProjectGitSCMResolver extends GitHubRepositoryResolver {

    @Override
    protected GitSCM getSCM(AbstractItem item) {
        if (item instanceof AbstractProject) {
            SCM scm = ((AbstractProject) item).getScm();
            if (scm instanceof GitSCM) {
                return (GitSCM) scm;
            }
        }
        return null;
    }

    @Override
    protected boolean isResolvable(AbstractItem item) {
        return item instanceof AbstractProject;
    }
}