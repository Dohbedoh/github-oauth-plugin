package org.jenkinsci.plugins.finders;

import hudson.model.AbstractItem;
import hudson.model.FreeStyleProject;
import hudson.model.Job;
import hudson.plugins.git.GitSCM;
import hudson.plugins.git.UserRemoteConfig;
import hudson.scm.SCM;
import junit.framework.TestCase;
import org.jenkinsci.plugins.workflow.job.WorkflowJob;
import org.jenkinsci.plugins.workflow.job.WorkflowRun;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Collections;
import java.util.List;

/**
 *
 *
 * @author Allan Burdajewicz
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({WorkflowJob.class})
public class WorkflowGitSCMFinderTest extends TestCase {

    private WorkflowGitSCMFinder gitScmFinder = new WorkflowGitSCMFinder();

    private Job<WorkflowJob, WorkflowRun> mockPipeline(String url) {
        WorkflowJob job = PowerMockito.mock(WorkflowJob.class);
        GitSCM gitSCM = PowerMockito.mock(GitSCM.class);
        UserRemoteConfig userRemoteConfig = PowerMockito.mock(UserRemoteConfig.class);
        List<UserRemoteConfig> userRemoteConfigs = Collections.singletonList(userRemoteConfig);
        PowerMockito.when(job.getTypicalSCM()).thenReturn(gitSCM);
        PowerMockito.when(gitSCM.getUserRemoteConfigs()).thenReturn(userRemoteConfigs);
        PowerMockito.when(userRemoteConfig.getUrl()).thenReturn(url);
        return job;
    }

    private Job<WorkflowJob, WorkflowRun> mockPipelineNoGitSCM() {
        WorkflowJob job = PowerMockito.mock(WorkflowJob.class);
        SCM otherSCM = PowerMockito.mock(SCM.class);
        PowerMockito.when(job.getTypicalSCM()).thenReturn(otherSCM);
        return job;
    }

    /**
     * Test of the {@link WorkflowGitSCMFinder#isFindable(AbstractItem)}
     */
    @Test
    public void testIsFindable() {
        // Test return true for Workflow jobs
        Assert.assertTrue(gitScmFinder.isFindable(PowerMockito.mock(WorkflowJob.class)));
        Assert.assertTrue(gitScmFinder.isFindable(mockPipelineNoGitSCM()));
        Assert.assertTrue(gitScmFinder.isFindable(mockPipeline("")));
        Assert.assertTrue(gitScmFinder.isFindable(mockPipeline("https://github.com/user/repo.git")));

        // Test return false for other jobs
        Assert.assertFalse(gitScmFinder.isFindable(PowerMockito.mock(Job.class)));
        Assert.assertFalse(gitScmFinder.isFindable(PowerMockito.mock(AbstractItem.class)));
        Assert.assertFalse(gitScmFinder.isFindable(PowerMockito.mock(FreeStyleProject.class)));
    }

    /**
     * Test of the {@link WorkflowGitSCMFinder#getSCM(AbstractItem)}
     */
    @Test
    public void testGetSCM() {
        // Test return false for other jobs
        Assert.assertNull(gitScmFinder.getSCM(PowerMockito.mock(Job.class)));
        Assert.assertNull(gitScmFinder.getSCM(PowerMockito.mock(AbstractItem.class)));
        Assert.assertNull(gitScmFinder.getSCM(PowerMockito.mock(FreeStyleProject.class)));

        Assert.assertNull(gitScmFinder.getSCM(PowerMockito.mock(WorkflowJob.class)));
        Assert.assertNull(gitScmFinder.getSCM(mockPipelineNoGitSCM()));
        Assert.assertNotNull(gitScmFinder.getSCM(mockPipeline("https://github.com/user/repo.git")));

        GitSCM scm = gitScmFinder.getSCM(mockPipeline(""));
        Assert.assertNotNull(scm);
        Assert.assertEquals(1, scm.getUserRemoteConfigs().size());
        Assert.assertEquals("", scm.getUserRemoteConfigs().get(0).getUrl());

        scm = gitScmFinder.getSCM(mockPipeline("https://github.com/user/repo.git"));
        Assert.assertNotNull(scm);
        Assert.assertEquals(1, scm.getUserRemoteConfigs().size());
        Assert.assertEquals("https://github.com/user/repo.git", scm.getUserRemoteConfigs().get(0).getUrl());
    }
}
