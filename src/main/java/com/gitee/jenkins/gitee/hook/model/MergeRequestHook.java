package com.gitee.jenkins.gitee.hook.model;


import net.karneim.pojobuilder.GeneratePojoBuilder;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import java.util.List;

/**
 * @author Robin Müller
 * @author Yashin Luo
 */
@GeneratePojoBuilder(intoPackage = "*.builder.generated", withFactoryMethod = "*")
public class MergeRequestHook extends WebHook {

    private User user;
    private User assignee;
    private Project project;
    private Action action;
    private State state;
    private MergeRequestObjectAttributes objectAttributes;
    private List<MergeRequestLabel> labels;

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    public State getState() {
        return this.state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getAssignee() {
        return assignee;
    }

    public void setAssignee(User assignee) {
        this.assignee = assignee;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    // adapt gitee hook
    public void setRepo(Project project) {
        this.project = project;
    }

    public MergeRequestObjectAttributes getObjectAttributes() {
        return objectAttributes;
    }

    public void setObjectAttributes(MergeRequestObjectAttributes objectAttributes) {
        this.objectAttributes = objectAttributes;
    }

    // adapt gitee hook
    public void setPullRequest(MergeRequestObjectAttributes objectAttributes) {
        this.objectAttributes = objectAttributes;
    }

    public List<MergeRequestLabel> getLabels() {
        return labels;
    }

    public void setLabels(List<MergeRequestLabel> labels) {
        this.labels = labels;
    }

    public String getWebHookDescription() {
        return getHookName() + " iid = " + objectAttributes.getIid() + " merge commit sha = " + objectAttributes.getMergeCommitSha();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MergeRequestHook that = (MergeRequestHook) o;
        return new EqualsBuilder()
                .append(user, that.user)
                .append(assignee, that.assignee)
                .append(project, that.project)
                .append(action, that.action)
                .append(state, that.state)
                .append(objectAttributes, that.objectAttributes)
                .append(labels, that.labels)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(user)
                .append(assignee)
                .append(project)
                .append(objectAttributes)
                .append(labels)
                .append(state)
                .append(action)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("user", user)
                .append("assignee", assignee)
                .append("project", project)
                .append("state", state)
                .append("action", action)
                .append("objectAttributes", objectAttributes)
                .append("labels", labels)
                .toString();
    }
}
