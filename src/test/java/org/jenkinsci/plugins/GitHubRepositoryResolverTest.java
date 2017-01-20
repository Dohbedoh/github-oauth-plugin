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
import org.jenkinsci.plugins.resolvers.AbstractProjectGitSCMResolver;
import org.jenkinsci.plugins.resolvers.WorkflowGitSCMResolver;
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
 * Test of the {@link GitHubRepositoryResolver} methods.
 *
 * @author Allan Burdajewicz
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({WorkflowJob.class, Jenkins.class})
public class GitHubRepositoryResolverTest extends TestCase {

    @Mock
    private Jenkins jenkins;

    private ExtensionList<GitHubRepositoryResolver> finders = ExtensionList.create(jenkins, GitHubRepositoryResolver.class);

    @Before
    public void setUp() throws Exception {
        super.setUp();
        PowerMockito.mockStatic(Jenkins.class);
        PowerMockito.when(Jenkins.getInstance()).thenReturn(jenkins);
        PowerMockito.when(jenkins.getExtensionList(GitHubRepositoryResolver.class)).thenReturn(finders);
    }

    /**
     * Test of the {@link GitHubRepositoryResolver#isApplicable(AbstractItem)}
     */
    @Test
    public void testIsApplicable() {

        //Core
        Assert.assertFalse(GitHubRepositoryResolver.isApplicable(PowerMockito.mock(Job.class)));
        Assert.assertFalse(GitHubRepositoryResolver.isApplicable(PowerMockito.mock(AbstractItem.class)));

        //Abstract Project
        Assert.assertFalse(GitHubRepositoryResolver.isApplicable(PowerMockito.mock(FreeStyleProject.class)));
        Assert.assertFalse(GitHubRepositoryResolver.isApplicable(mockProjectNoGitSCM()));
        Assert.assertFalse(GitHubRepositoryResolver.isApplicable(mockProject("")));
        Assert.assertFalse(GitHubRepositoryResolver.isApplicable(mockProject("https://github.com/user/repo.git")));

        //Pipeline
        Assert.assertFalse(GitHubRepositoryResolver.isApplicable(PowerMockito.mock(WorkflowJob.class)));
        Assert.assertFalse(GitHubRepositoryResolver.isApplicable(mockPipelineNoGitSCM()));
        Assert.assertFalse(GitHubRepositoryResolver.isApplicable(mockPipeline("")));
        Assert.assertFalse(GitHubRepositoryResolver.isApplicable(mockPipeline("https://github.com/user/repo.git")));

        /*
         * Adding implementation for AbstractProjects
         */
        finders.add(0, new AbstractProjectGitSCMResolver());

        //Core
        Assert.assertFalse(GitHubRepositoryResolver.isApplicable(PowerMockito.mock(Job.class)));
        Assert.assertFalse(GitHubRepositoryResolver.isApplicable(PowerMockito.mock(AbstractItem.class)));

        //Abstract Project
        Assert.assertTrue(GitHubRepositoryResolver.isApplicable(PowerMockito.mock(FreeStyleProject.class)));
        Assert.assertTrue(GitHubRepositoryResolver.isApplicable(mockProjectNoGitSCM()));
        Assert.assertTrue(GitHubRepositoryResolver.isApplicable(mockProject("")));
        Assert.assertTrue(GitHubRepositoryResolver.isApplicable(mockProject("https://github.com/user/repo.git")));

        //Pipeline
        Assert.assertFalse(GitHubRepositoryResolver.isApplicable(PowerMockito.mock(WorkflowJob.class)));
        Assert.assertFalse(GitHubRepositoryResolver.isApplicable(mockPipelineNoGitSCM()));
        Assert.assertFalse(GitHubRepositoryResolver.isApplicable(mockPipeline("")));
        Assert.assertFalse(GitHubRepositoryResolver.isApplicable(mockPipeline("https://github.com/user/repo.git")));

        /*
         * Adding implementation for WorkflowJob
         */
        finders.add(0, new WorkflowGitSCMResolver());

        //Core
        Assert.assertFalse(GitHubRepositoryResolver.isApplicable(PowerMockito.mock(Job.class)));
        Assert.assertFalse(GitHubRepositoryResolver.isApplicable(PowerMockito.mock(AbstractItem.class)));

        //Abstract Project
        Assert.assertTrue(GitHubRepositoryResolver.isApplicable(PowerMockito.mock(FreeStyleProject.class)));
        Assert.assertTrue(GitHubRepositoryResolver.isApplicable(mockProjectNoGitSCM()));
        Assert.assertTrue(GitHubRepositoryResolver.isApplicable(mockProject("")));
        Assert.assertTrue(GitHubRepositoryResolver.isApplicable(mockProject("https://github.com/user/repo.git")));

        //Pipeline
        Assert.assertTrue(GitHubRepositoryResolver.isApplicable(PowerMockito.mock(WorkflowJob.class)));
        Assert.assertTrue(GitHubRepositoryResolver.isApplicable(mockPipelineNoGitSCM()));
        Assert.assertTrue(GitHubRepositoryResolver.isApplicable(mockPipeline("")));
        Assert.assertTrue(GitHubRepositoryResolver.isApplicable(mockPipeline("https://github.com/user/repo.git")));
    }

    /**
     * Test of the {@link GitHubRepositoryResolver#find(AbstractItem)}
     */
    @Test
    public void testGetSCM() {
        // Core
        Assert.assertNull(GitHubRepositoryResolver.find(PowerMockito.mock(Job.class)));
        Assert.assertNull(GitHubRepositoryResolver.find(PowerMockito.mock(AbstractItem.class)));

        // AbstractProject
        Assert.assertNull(GitHubRepositoryResolver.find(PowerMockito.mock(FreeStyleProject.class)));
        Assert.assertNull(GitHubRepositoryResolver.find(mockProjectNoGitSCM()));
        Assert.assertNull(GitHubRepositoryResolver.find(mockProject("https://github.com/user/repo.git")));

        // WorkflowJob
        Assert.assertNull(GitHubRepositoryResolver.find(PowerMockito.mock(WorkflowJob.class)));
        Assert.assertNull(GitHubRepositoryResolver.find(mockPipelineNoGitSCM()));
        Assert.assertNull(GitHubRepositoryResolver.find(mockPipeline("https://github.com/user/repo.git")));

        /*
         * Adding implementation for AbstractProjects
         */
        finders.add(0, new AbstractProjectGitSCMResolver());

        // Core
        Assert.assertNull(GitHubRepositoryResolver.find(PowerMockito.mock(Job.class)));
        Assert.assertNull(GitHubRepositoryResolver.find(PowerMockito.mock(AbstractItem.class)));

        // WorkflowJob
        Assert.assertNull(GitHubRepositoryResolver.find(PowerMockito.mock(WorkflowJob.class)));
        Assert.assertNull(GitHubRepositoryResolver.find(mockPipelineNoGitSCM()));
        Assert.assertNull(GitHubRepositoryResolver.find(mockPipeline("https://github.com/user/repo.git")));

        // AbstractProject
        Assert.assertNull(GitHubRepositoryResolver.find(PowerMockito.mock(FreeStyleProject.class)));
        Assert.assertNull(GitHubRepositoryResolver.find(mockProjectNoGitSCM()));
        Assert.assertNotNull(GitHubRepositoryResolver.find(mockProject("https://github.com/user/repo.git")));

        String repository = GitHubRepositoryResolver.find(mockProject(""));
        Assert.assertNull(repository);

        repository = GitHubRepositoryResolver.find(mockProject("https://github.com/user/repo.git"));
        Assert.assertNotNull(repository);
        Assert.assertEquals("user/repo", repository);

        /*
         * Adding implementation for WorkflowJob
         */
        finders.add(0, new WorkflowGitSCMResolver());

        // Core
        Assert.assertNull(GitHubRepositoryResolver.find(PowerMockito.mock(Job.class)));
        Assert.assertNull(GitHubRepositoryResolver.find(PowerMockito.mock(AbstractItem.class)));

        // AbstractProject
        Assert.assertNull(GitHubRepositoryResolver.find(PowerMockito.mock(FreeStyleProject.class)));
        Assert.assertNull(GitHubRepositoryResolver.find(mockProjectNoGitSCM()));
        Assert.assertNotNull(GitHubRepositoryResolver.find(mockProject("https://github.com/user/repo.git")));

        // WorkflowJob
        Assert.assertNull(GitHubRepositoryResolver.find(PowerMockito.mock(WorkflowJob.class)));
        Assert.assertNull(GitHubRepositoryResolver.find(mockPipelineNoGitSCM()));
        Assert.assertNotNull(GitHubRepositoryResolver.find(mockPipeline("https://github.com/user/repo.git")));

        repository = GitHubRepositoryResolver.find(mockPipeline(""));
        Assert.assertNull(repository);

        repository = GitHubRepositoryResolver.find(mockPipeline("https://github.com/user/repo.git"));
        Assert.assertNotNull(repository);
        Assert.assertEquals("user/repo", repository);
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
