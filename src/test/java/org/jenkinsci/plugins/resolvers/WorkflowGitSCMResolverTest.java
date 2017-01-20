package org.jenkinsci.plugins.resolvers;

import hudson.model.AbstractItem;
import hudson.model.FreeStyleProject;
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
 * Test of the {@link WorkflowGitSCMResolverTest} methods.
 *
 * @author Allan Burdajewicz
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({WorkflowJob.class})
public class WorkflowGitSCMResolverTest extends TestCase {

    private WorkflowGitSCMResolver gitScmFinder = new WorkflowGitSCMResolver();

    /**
     * Test of the {@link WorkflowGitSCMResolver#isResolvable(AbstractItem)}
     */
    @Test
    public void testIsFindable() {
        // Test return true for Workflow jobs
        Assert.assertTrue(gitScmFinder.isResolvable(PowerMockito.mock(WorkflowJob.class)));
        Assert.assertTrue(gitScmFinder.isResolvable(GitHubProjectsHelper.mockPipelineNoGitSCM()));
        Assert.assertTrue(gitScmFinder.isResolvable(GitHubProjectsHelper.mockPipeline("")));
        Assert.assertTrue(gitScmFinder.isResolvable(GitHubProjectsHelper.mockPipeline("https://github.com/user/repo.git")));

        // Test return false for other jobs
        Assert.assertFalse(gitScmFinder.isResolvable(PowerMockito.mock(Job.class)));
        Assert.assertFalse(gitScmFinder.isResolvable(PowerMockito.mock(AbstractItem.class)));
        Assert.assertFalse(gitScmFinder.isResolvable(PowerMockito.mock(FreeStyleProject.class)));
    }

    /**
     * Test of the {@link WorkflowGitSCMResolver#getSCM(AbstractItem)}
     */
    @Test
    public void testGetSCM() {
        // Test return false for other jobs
        Assert.assertNull(gitScmFinder.getSCM(PowerMockito.mock(Job.class)));
        Assert.assertNull(gitScmFinder.getSCM(PowerMockito.mock(AbstractItem.class)));
        Assert.assertNull(gitScmFinder.getSCM(PowerMockito.mock(FreeStyleProject.class)));

        Assert.assertNull(gitScmFinder.getSCM(PowerMockito.mock(WorkflowJob.class)));
        Assert.assertNull(gitScmFinder.getSCM(GitHubProjectsHelper.mockPipelineNoGitSCM()));
        Assert.assertNotNull(gitScmFinder.getSCM(GitHubProjectsHelper.mockPipeline("https://github.com/user/repo.git")));

        GitSCM scm = gitScmFinder.getSCM(GitHubProjectsHelper.mockPipeline(""));
        Assert.assertNotNull(scm);
        Assert.assertEquals(1, scm.getUserRemoteConfigs().size());
        Assert.assertEquals("", scm.getUserRemoteConfigs().get(0).getUrl());

        scm = gitScmFinder.getSCM(GitHubProjectsHelper.mockPipeline("https://github.com/user/repo.git"));
        Assert.assertNotNull(scm);
        Assert.assertEquals(1, scm.getUserRemoteConfigs().size());
        Assert.assertEquals("https://github.com/user/repo.git", scm.getUserRemoteConfigs().get(0).getUrl());
    }
}
