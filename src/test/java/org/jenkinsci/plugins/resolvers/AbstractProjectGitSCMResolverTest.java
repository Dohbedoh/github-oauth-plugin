package org.jenkinsci.plugins.resolvers;

import hudson.model.AbstractItem;
import hudson.model.AbstractProject;
import hudson.model.Job;
import hudson.plugins.git.GitSCM;
import junit.framework.TestCase;
import org.jenkinsci.plugins.test.GitHubProjectsHelper;
import org.jenkinsci.plugins.workflow.job.WorkflowJob;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 * Test of the {@link AbstractProjectGitSCMResolverTest} methods.
 *
 * @author Allan Burdajewicz
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({WorkflowJob.class})
public class AbstractProjectGitSCMResolverTest extends TestCase {

    private AbstractProjectGitSCMResolver gitScmFinder = new AbstractProjectGitSCMResolver();

    /**
     * Test of the {@link AbstractProjectGitSCMResolver#isFindable(AbstractItem)}
     */
    @Test
    public void testIsFindable() {
        // Test return true for Workflow jobs
        Assert.assertTrue(gitScmFinder.isFindable(PowerMockito.mock(AbstractProject.class)));
        Assert.assertTrue(gitScmFinder.isFindable(GitHubProjectsHelper.mockProjectNoGitSCM()));
        Assert.assertTrue(gitScmFinder.isFindable(GitHubProjectsHelper.mockProject("")));
        Assert.assertTrue(gitScmFinder.isFindable(GitHubProjectsHelper.mockProject("https://github.com/user/repo.git")));

        // Test return false for other jobs
        Assert.assertFalse(gitScmFinder.isFindable(PowerMockito.mock(Job.class)));
        Assert.assertFalse(gitScmFinder.isFindable(PowerMockito.mock(AbstractItem.class)));
        Assert.assertFalse(gitScmFinder.isFindable(PowerMockito.mock(WorkflowJob.class)));
    }

    /**
     * Test of the {@link AbstractProjectGitSCMResolver#getSCM(AbstractItem)}
     */
    @Test
    public void testGetSCM() {
        // Test return false for other jobs
        Assert.assertNull(gitScmFinder.getSCM(PowerMockito.mock(Job.class)));
        Assert.assertNull(gitScmFinder.getSCM(PowerMockito.mock(AbstractItem.class)));
        Assert.assertNull(gitScmFinder.getSCM(PowerMockito.mock(WorkflowJob.class)));

        Assert.assertNull(gitScmFinder.getSCM(PowerMockito.mock(WorkflowJob.class)));
        Assert.assertNull(gitScmFinder.getSCM(GitHubProjectsHelper.mockProjectNoGitSCM()));
        Assert.assertNotNull(gitScmFinder.getSCM(GitHubProjectsHelper.mockProject("https://github.com/user/repo.git")));

        GitSCM scm = gitScmFinder.getSCM(GitHubProjectsHelper.mockProject(""));
        Assert.assertNotNull(scm);
        Assert.assertEquals(1, scm.getUserRemoteConfigs().size());
        Assert.assertEquals("", scm.getUserRemoteConfigs().get(0).getUrl());

        scm = gitScmFinder.getSCM(GitHubProjectsHelper.mockProject("https://github.com/user/repo.git"));
        Assert.assertNotNull(scm);
        Assert.assertEquals(1, scm.getUserRemoteConfigs().size());
        Assert.assertEquals("https://github.com/user/repo.git", scm.getUserRemoteConfigs().get(0).getUrl());
    }
}
