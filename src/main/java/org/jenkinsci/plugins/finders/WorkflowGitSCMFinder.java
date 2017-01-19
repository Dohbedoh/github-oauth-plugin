package org.jenkinsci.plugins.finders;

import hudson.Extension;
import hudson.model.AbstractItem;
import hudson.plugins.git.GitSCM;
import hudson.scm.SCM;
import org.jenkinsci.plugins.GitSCMFinder;
import org.jenkinsci.plugins.workflow.job.WorkflowJob;

/**
 * Implementation of {@link GitSCMFinder} for Pipeline jobs.
 *
 * @author Allan Burdajewicz
 */
@Extension(optional = true)
public class WorkflowGitSCMFinder extends GitSCMFinder {

    @Override
    protected GitSCM getSCM(AbstractItem item) {
        if (item instanceof WorkflowJob) {
            SCM scm = ((WorkflowJob)item).getTypicalSCM();
            if (scm instanceof GitSCM) {
                return (GitSCM)scm;
            }
        }
        return null;
    }

    @Override
    protected boolean isFindable(AbstractItem item) {
        return item instanceof WorkflowJob;
    }
}
