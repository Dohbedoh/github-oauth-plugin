package org.jenkinsci.plugins;

import hudson.ExtensionList;
import hudson.model.AbstractItem;
import hudson.model.AbstractProject;
import hudson.model.FreeStyleProject;
import hudson.model.Job;
import hudson.plugins.git.GitSCM;
import hudson.plugins.git.UserRemoteConfig;
import hudson.scm.SCM;
import jenkins.model.Jenkins;
import junit.framework.TestCase;
import org.jenkinsci.plugins.finders.AbstractProjectGitSCMFinder;
import org.jenkinsci.plugins.finders.WorkflowGitSCMFinder;
import org.jenkinsci.plugins.workflow.job.WorkflowJob;
import org.jenkinsci.plugins.workflow.job.WorkflowRun;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Collections;
import java.util.List;

/**
 * Test of the {@link GitSCMFinder} methods.
 *
 * @author Allan Burdajewicz
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({WorkflowJob.class, Jenkins.class})
public class GitSCMFinderTest extends TestCase {

    @Mock
    private Jenkins jenkins;

    private ExtensionList<GitSCMFinder> finders = ExtensionList.create(jenkins, GitSCMFinder.class);

    @Before
    public void setUp() throws Exception {
        super.setUp();
        PowerMockito.mockStatic(Jenkins.class);
        PowerMockito.when(Jenkins.getInstance()).thenReturn(jenkins);
        PowerMockito.when(jenkins.getExtensionList(GitSCMFinder.class)).thenReturn(finders);
    }

    /**
     * Test of the {@link GitSCMFinder#isApplicable(AbstractItem)}
     */
    @Test
    public void testIsApplicable() {

        //Core
        Assert.assertFalse(GitSCMFinder.isApplicable(PowerMockito.mock(Job.class)));
        Assert.assertFalse(GitSCMFinder.isApplicable(PowerMockito.mock(AbstractItem.class)));

        //Abstract Project
        Assert.assertFalse(GitSCMFinder.isApplicable(PowerMockito.mock(FreeStyleProject.class)));
        Assert.assertFalse(GitSCMFinder.isApplicable(mockProjectNoGitSCM()));
        Assert.assertFalse(GitSCMFinder.isApplicable(mockProject("")));
        Assert.assertFalse(GitSCMFinder.isApplicable(mockProject("https://github.com/user/repo.git")));

        //Pipeline
        Assert.assertFalse(GitSCMFinder.isApplicable(PowerMockito.mock(WorkflowJob.class)));
        Assert.assertFalse(GitSCMFinder.isApplicable(mockPipelineNoGitSCM()));
        Assert.assertFalse(GitSCMFinder.isApplicable(mockPipeline("")));
        Assert.assertFalse(GitSCMFinder.isApplicable(mockPipeline("https://github.com/user/repo.git")));

        /*
         * Adding implementation for AbstractProjects
         */
        finders.add(0, new AbstractProjectGitSCMFinder());

        //Core
        Assert.assertFalse(GitSCMFinder.isApplicable(PowerMockito.mock(Job.class)));
        Assert.assertFalse(GitSCMFinder.isApplicable(PowerMockito.mock(AbstractItem.class)));

        //Abstract Project
        Assert.assertTrue(GitSCMFinder.isApplicable(PowerMockito.mock(FreeStyleProject.class)));
        Assert.assertTrue(GitSCMFinder.isApplicable(mockProjectNoGitSCM()));
        Assert.assertTrue(GitSCMFinder.isApplicable(mockProject("")));
        Assert.assertTrue(GitSCMFinder.isApplicable(mockProject("https://github.com/user/repo.git")));

        //Pipeline
        Assert.assertFalse(GitSCMFinder.isApplicable(PowerMockito.mock(WorkflowJob.class)));
        Assert.assertFalse(GitSCMFinder.isApplicable(mockPipelineNoGitSCM()));
        Assert.assertFalse(GitSCMFinder.isApplicable(mockPipeline("")));
        Assert.assertFalse(GitSCMFinder.isApplicable(mockPipeline("https://github.com/user/repo.git")));

        /*
         * Adding implementation for WorkflowJob
         */
        finders.add(0, new WorkflowGitSCMFinder());

        //Core
        Assert.assertFalse(GitSCMFinder.isApplicable(PowerMockito.mock(Job.class)));
        Assert.assertFalse(GitSCMFinder.isApplicable(PowerMockito.mock(AbstractItem.class)));

        //Abstract Project
        Assert.assertTrue(GitSCMFinder.isApplicable(PowerMockito.mock(FreeStyleProject.class)));
        Assert.assertTrue(GitSCMFinder.isApplicable(mockProjectNoGitSCM()));
        Assert.assertTrue(GitSCMFinder.isApplicable(mockProject("")));
        Assert.assertTrue(GitSCMFinder.isApplicable(mockProject("https://github.com/user/repo.git")));

        //Pipeline
        Assert.assertTrue(GitSCMFinder.isApplicable(PowerMockito.mock(WorkflowJob.class)));
        Assert.assertTrue(GitSCMFinder.isApplicable(mockPipelineNoGitSCM()));
        Assert.assertTrue(GitSCMFinder.isApplicable(mockPipeline("")));
        Assert.assertTrue(GitSCMFinder.isApplicable(mockPipeline("https://github.com/user/repo.git")));
    }

    /**
     * Test of the {@link GitSCMFinder#find(AbstractItem)}
     */
    @Test
    public void testGetSCM() {
        // Core
        Assert.assertNull(GitSCMFinder.find(PowerMockito.mock(Job.class)));
        Assert.assertNull(GitSCMFinder.find(PowerMockito.mock(AbstractItem.class)));

        // AbstractProject
        Assert.assertNull(GitSCMFinder.find(PowerMockito.mock(FreeStyleProject.class)));
        Assert.assertNull(GitSCMFinder.find(mockProjectNoGitSCM()));
        Assert.assertNull(GitSCMFinder.find(mockProject("https://github.com/user/repo.git")));

        // WorkflowJob
        Assert.assertNull(GitSCMFinder.find(PowerMockito.mock(WorkflowJob.class)));
        Assert.assertNull(GitSCMFinder.find(mockPipelineNoGitSCM()));
        Assert.assertNull(GitSCMFinder.find(mockPipeline("https://github.com/user/repo.git")));

        /*
         * Adding implementation for AbstractProjects
         */
        finders.add(0, new AbstractProjectGitSCMFinder());

        // Core
        Assert.assertNull(GitSCMFinder.find(PowerMockito.mock(Job.class)));
        Assert.assertNull(GitSCMFinder.find(PowerMockito.mock(AbstractItem.class)));

        // WorkflowJob
        Assert.assertNull(GitSCMFinder.find(PowerMockito.mock(WorkflowJob.class)));
        Assert.assertNull(GitSCMFinder.find(mockPipelineNoGitSCM()));
        Assert.assertNull(GitSCMFinder.find(mockPipeline("https://github.com/user/repo.git")));

        // AbstractProject
        Assert.assertNull(GitSCMFinder.find(PowerMockito.mock(FreeStyleProject.class)));
        Assert.assertNull(GitSCMFinder.find(mockProjectNoGitSCM()));
        Assert.assertNotNull(GitSCMFinder.find(mockProject("https://github.com/user/repo.git")));

        GitSCM scm = GitSCMFinder.find(mockProject(""));
        Assert.assertNotNull(scm);
        Assert.assertEquals(1, scm.getUserRemoteConfigs().size());
        Assert.assertEquals("", scm.getUserRemoteConfigs().get(0).getUrl());

        scm = GitSCMFinder.find(mockProject("https://github.com/user/repo.git"));
        Assert.assertNotNull(scm);
        Assert.assertEquals(1, scm.getUserRemoteConfigs().size());
        Assert.assertEquals("https://github.com/user/repo.git", scm.getUserRemoteConfigs().get(0).getUrl());

        /*
         * Adding implementation for WorkflowJob
         */
        finders.add(0, new WorkflowGitSCMFinder());

        // Core
        Assert.assertNull(GitSCMFinder.find(PowerMockito.mock(Job.class)));
        Assert.assertNull(GitSCMFinder.find(PowerMockito.mock(AbstractItem.class)));

        // AbstractProject
        Assert.assertNull(GitSCMFinder.find(PowerMockito.mock(FreeStyleProject.class)));
        Assert.assertNull(GitSCMFinder.find(mockProjectNoGitSCM()));
        Assert.assertNotNull(GitSCMFinder.find(mockProject("https://github.com/user/repo.git")));

        // WorkflowJob
        Assert.assertNull(GitSCMFinder.find(PowerMockito.mock(WorkflowJob.class)));
        Assert.assertNull(GitSCMFinder.find(mockPipelineNoGitSCM()));
        Assert.assertNotNull(GitSCMFinder.find(mockPipeline("https://github.com/user/repo.git")));

        scm = GitSCMFinder.find(mockPipeline(""));
        Assert.assertNotNull(scm);
        Assert.assertEquals(1, scm.getUserRemoteConfigs().size());
        Assert.assertEquals("", scm.getUserRemoteConfigs().get(0).getUrl());

        scm = GitSCMFinder.find(mockPipeline("https://github.com/user/repo.git"));
        Assert.assertNotNull(scm);
        Assert.assertEquals(1, scm.getUserRemoteConfigs().size());
        Assert.assertEquals("https://github.com/user/repo.git", scm.getUserRemoteConfigs().get(0).getUrl());
    }

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

    private AbstractProject mockProject(String url) {
        AbstractProject job = PowerMockito.mock(AbstractProject.class);
        GitSCM gitSCM = PowerMockito.mock(GitSCM.class);
        UserRemoteConfig userRemoteConfig = PowerMockito.mock(UserRemoteConfig.class);
        List<UserRemoteConfig> userRemoteConfigs = Collections.singletonList(userRemoteConfig);
        PowerMockito.when(job.getScm()).thenReturn(gitSCM);
        PowerMockito.when(gitSCM.getUserRemoteConfigs()).thenReturn(userRemoteConfigs);
        PowerMockito.when(userRemoteConfig.getUrl()).thenReturn(url);
        return job;
    }

    private AbstractProject mockProjectNoGitSCM() {
        AbstractProject job = PowerMockito.mock(AbstractProject.class);
        SCM otherSCM = PowerMockito.mock(SCM.class);
        PowerMockito.when(job.getScm()).thenReturn(otherSCM);
        return job;
    }
}
