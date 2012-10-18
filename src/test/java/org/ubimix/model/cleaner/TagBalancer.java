package org.ubimix.model.cleaner;

/**
 * @author kotelnikov
 */
public class TagBalancer {

    public interface IListener {

        void begin(String tag);

        void end(String tag);
    }

    protected static class TagContext {

        private TagDescriptor fDescriptor;

        private TagBalancer.TagContext fParent;

        private String fTag;

        public TagContext(
            TagDescriptor descriptor,
            TagBalancer.TagContext parent,
            String tag) {
            fParent = parent;
            fTag = tag;
            fDescriptor = descriptor;
        }

        private boolean acceptClosingTag(String tag) {
            return fTag.equals(tag);
        }

        private boolean acceptOpeningTag(String tag) {
            if (!fDescriptor.isDeclared(tag)) {
                // Accept all unknown tags
                return true;
            }
            boolean result = false;
            if (!fDescriptor.isDeclared(fTag)) {
                result = fParent == null || fParent.acceptOpeningTag(tag);
            } else {
                result = fDescriptor.accepts(fTag, tag);
            }
            return result;
        }

        public TagBalancer.TagContext getParent() {
            return fParent;
        }

        public String getTag() {
            return fTag;
        }

        public TagBalancer.TagContext getTargetClosingContext(String tag) {
            TagBalancer.TagContext result = null;
            TagBalancer.TagContext context = this;
            while (context != null && result == null) {
                if (context.acceptClosingTag(tag)) {
                    result = context.fParent;
                    break;
                }
                context = context.getParent();
            }
            return result;
        }

        public TagBalancer.TagContext getTargetOpeningContext(String tag) {
            TagBalancer.TagContext result = null;
            TagBalancer.TagContext context = this;
            while (context != null && result == null) {
                if (context.acceptOpeningTag(tag)) {
                    result = context;
                    break;
                }
                context = context.getParent();
            }
            return result;
        }

    }

    private TagBalancer.TagContext fContext;

    private TagDescriptor fDescriptor;

    private IListener fListener;

    public TagBalancer(TagDescriptor descriptor, IListener listener) {
        fListener = listener;
        fDescriptor = descriptor;
    }

    public void begin(String tag) {
        TagBalancer.TagContext targetContext = fContext != null ? fContext
            .getTargetOpeningContext(tag) : null;
        if (targetContext == null) {
            String parentTag = fDescriptor.getParentTag(tag);
            if (parentTag != null) {
                begin(parentTag);
                targetContext = fContext != null ? fContext
                    .getTargetOpeningContext(tag) : null;
            }
        }
        if (targetContext == null) {
            // No parent content was found. Reset the stack.
            finish();
        }
        if (targetContext != null) {
            while (targetContext != null && fContext != targetContext) {
                pop();
            }
        }
        push(tag);
    }

    public void end(String tag) {
        if (fContext != null) {
            TagBalancer.TagContext targetContext = fContext
                .getTargetClosingContext(tag);
            while (targetContext != null) {
                pop();
                if (fContext == targetContext) {
                    break;
                }
            }
        }
    }

    public void finish() {
        while (fContext != null) {
            pop();
        }
    }

    protected void pop() {
        if (fContext != null) {
            fListener.end(fContext.getTag());
            fContext = fContext.getParent();
        }
    }

    private void push(String tag) {
        fContext = new TagContext(fDescriptor, fContext, tag);
        fListener.begin(fContext.getTag());
    }
}