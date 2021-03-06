package com.gitee.jenkins.workflow;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;

import org.jenkinsci.plugins.workflow.steps.BodyExecution;
import org.jenkinsci.plugins.workflow.steps.BodyExecutionCallback;
import org.jenkinsci.plugins.workflow.steps.Step;
import org.jenkinsci.plugins.workflow.steps.StepContext;
import org.jenkinsci.plugins.workflow.steps.StepDescriptor;
import org.jenkinsci.plugins.workflow.steps.StepExecution;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.export.ExportedBean;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableSet;

import hudson.Extension;
import hudson.model.Run;
import hudson.model.TaskListener;

/**
 * @author <a href="mailto:robin.mueller@1und1.de">Robin Müller</a>
 */
@ExportedBean
public class GiteeBuildsStep extends Step {

    private List<String> builds;

    @DataBoundConstructor
    public GiteeBuildsStep() {
    }
    
    
	@Override
	public StepExecution start(StepContext context) throws Exception {
		return new GiteeBuildStepExecution(context, this);
	}

    @DataBoundSetter
    public void setBuilds(List<String> builds) {
        if (builds != null && builds.size() == 1) {
            this.builds = new ArrayList<>();
            for (String build : Splitter.on(",").omitEmptyStrings().trimResults().split(builds.get(0))) {
                this.builds.add(build);
            }
        } else {
            this.builds = builds;
        }
    }

    public List<String> getBuilds() {
        return builds;
    }

    public static class GiteeBuildStepExecution extends StepExecution {
        private static final long serialVersionUID = 1;

        private final transient Run<?, ?> run;

        private final transient GiteeBuildsStep step;

        private BodyExecution body;

        GiteeBuildStepExecution(StepContext context, GiteeBuildsStep step) throws Exception {
            super(context);
            this.step = step;
            run = context.get(Run.class);
        }
        
        @Override
        public boolean start() throws Exception {
            body = getContext().newBodyInvoker()
                .withCallback(new BodyExecutionCallback() {
                    @Override
                    public void onStart(StepContext context) {
                        run.addAction(new PendingBuildsAction(new ArrayList<>(step.builds)));
                    }

                    @Override
                    public void onSuccess(StepContext context, Object result) {
                        PendingBuildsAction action = run.getAction(PendingBuildsAction.class);
                        if (action != null && !action.getBuilds().isEmpty()) {
                            TaskListener taskListener = getTaskListener(context);
                            if (taskListener != null) {
                                taskListener.getLogger().println("There are still pending Gitee builds. Please check your configuration");
                            }
                        }
                        context.onSuccess(result);
                    }

                    @Override
                    public void onFailure(StepContext context, Throwable t) {
                        context.onFailure(t);
                    }
                })
                .start();
            return false;
        }

        @Override
        public void stop(@Nonnull Throwable cause) throws Exception {
            // should be no need to do anything special (but verify in JENKINS-26148)
            if (body != null) {
                body.cancel(cause);
            }
        }

        private TaskListener getTaskListener(StepContext context) {
            if (!context.isReady()) {
                return null;
            }
            try {
                return context.get(TaskListener.class);
            } catch (Exception x) {
                return null;
            }
        }
    }

    @Extension
    public static final class DescriptorImpl extends StepDescriptor {
        @Override
        public String getDisplayName() {
            return "Notify gitee about pending builds";
        }

        @Override
        public String getFunctionName() {
            return "giteeBuilds";
        }

        @Override
        public boolean takesImplicitBlockArgument() {
            return true;
        }

		@Override
		public Set<Class<?>> getRequiredContext() {
			return ImmutableSet.of(TaskListener.class, Run.class);
		}
    }
}
