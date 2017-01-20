package org.jenkinsci.plugins.test;

import hudson.model.AbstractProject;
import hudson.model.Job;
import hudson.plugins.git.GitSCM;
import hudson.plugins.git.UserRemoteConfig;
import hudson.scm.SCM;
import org.jenkinsci.plugins.workflow.job.WorkflowJob;
import org.jenkinsci.plugins.workflow.job.WorkflowRun;
import org.powermock.api.mockito.PowerMockito;

import java.util.Collections;
import java.util.List;

/**
 * Helper to mock projects with or without GitHub SCM.
 *
 * @author Allan Burdajewicz
 */
public class GitHubProjectsHelper {

    /**
     * Mock a pipeline job with a Typical GitSCM pointing to the URL passed in.
     * @param url The repository URL
     * @return A {@link WorkflowJob}
     */
    public static Job<WorkflowJob, WorkflowRun> mockPipeline(String url) {
        WorkflowJob job = PowerMockito.mock(WorkflowJob.class);
        GitSCM gitSCM = PowerMockito.mock(GitSCM.class);
        UserRemoteConfig userRemoteConfig = PowerMockito.mock(UserRemoteConfig.class);
        List<UserRemoteConfig> userRemoteConfigs = Collections.singletonList(userRemoteConfig);
        PowerMockito.when(job.getTypicalSCM()).thenReturn(gitSCM);
        PowerMockito.when(gitSCM.getUserRemoteConfigs()).thenReturn(userRemoteConfigs);
        PowerMockito.when(userRemoteConfig.getUrl()).thenReturn(url);
        return job;
    }

    /**
     * Mock a pipeline project with an SCM that is not Git.
     * @return An {@link WorkflowJob}
     */
    public static Job<WorkflowJob, WorkflowRun> mockPipelineNoGitSCM() {
        WorkflowJob job = PowerMockito.mock(WorkflowJob.class);
        SCM otherSCM = PowerMockito.mock(SCM.class);
        PowerMockito.when(job.getTypicalSCM()).thenReturn(otherSCM);
        return job;
    }

    /**
     * Mock an abstract project with a GitSCM pointing to the URL passed in.
     * @param url The repository URL
     * @return An {@link AbstractProject}
     */
    public static AbstractProject mockProject(String url) {
        AbstractProject job = PowerMockito.mock(AbstractProject.class);
        GitSCM gitSCM = PowerMockito.mock(GitSCM.class);
        UserRemoteConfig userRemoteConfig = PowerMockito.mock(UserRemoteConfig.class);
        List<UserRemoteConfig> userRemoteConfigs = Collections.singletonList(userRemoteConfig);
        PowerMockito.when(job.getScm()).thenReturn(gitSCM);
        PowerMockito.when(gitSCM.getUserRemoteConfigs()).thenReturn(userRemoteConfigs);
        PowerMockito.when(userRemoteConfig.getUrl()).thenReturn(url);
        return job;
    }

    /**
     * Mock an abstract project with an SCM that is not Git.
     * @return An {@link AbstractProject}
     */
    public static AbstractProject mockProjectNoGitSCM() {
        AbstractProject job = PowerMockito.mock(AbstractProject.class);
        SCM otherSCM = PowerMockito.mock(SCM.class);
        PowerMockito.when(job.getScm()).thenReturn(otherSCM);
        return job;
    }
}
