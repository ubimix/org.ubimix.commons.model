package org.ubimix.model.cleaner;

import org.ubimix.commons.parser.html.HtmlTagDictionary;

/**
 * @author kotelnikov
 */
public class TagBurner extends CompositeTagProcessor {

    public static class TagBurnerConfig {

        private boolean fTransformBrToParagraph = true;

        public void setTransformBrToParagraph(boolean transformBrToParagraph) {
            fTransformBrToParagraph = transformBrToParagraph;
        }

        public boolean transformBrToParagraph() {
            return fTransformBrToParagraph;
        }
    }

    public TagBurner() {
        this(new TagBurnerConfig());
    }

    public TagBurner(TagBurnerConfig config) {
        CompositeTagProcessor cleaner = new CompositeTagProcessor();
        cleaner.addProcessor(new GenericTagProcessor());
        cleaner.addProcessor(new TextNodeReducer());
        cleaner.addProcessor(new InlineNodesProcessor(config
            .transformBrToParagraph()));

        DispatchingTagProcessor preprocessor = new DispatchingTagProcessor()
            .setDefaultProcessor(cleaner)
            .register(HtmlTagDictionary.A, new HtmlANodeProcessor())
            .register(
                HtmlTagDictionary.PRE,
                new HtmlPreformattedNodeProcessor());
        addProcessor(preprocessor);

        DispatchingTagProcessor postprocessor = new DispatchingTagProcessor()
            .setDefaultProcessor(new NullProcessor())
            .register(HtmlTagDictionary.SPAN, new HtmlSpanNodeProcessor())
            .register(HtmlTagDictionary.DIV, new HtmlDivNodeProcessor())
            .register(
                new HtmlListNodeProcessor(),
                HtmlTagDictionary.UL,
                HtmlTagDictionary.OL)
            .register(HtmlTagDictionary.TABLE, new HtmlTableNodeProcessor());
        addProcessor(postprocessor);
    }
}