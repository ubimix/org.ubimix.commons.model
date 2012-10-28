package org.ubimix.model.cleaner;

import org.ubimix.commons.parser.html.HtmlTagDictionary;

/**
 * @author kotelnikov
 */
public class TagBurner extends CompositeTagProcessor {

    public TagBurner() {
        CompositeTagProcessor cleaner = new CompositeTagProcessor();
        cleaner.addProcessor(new GenericTagProcessor());
        cleaner.addProcessor(new TextNodeReducer());
        cleaner.addProcessor(new InlineNodesProcessor());

        DispatchingTagProcessor preprocessor = new DispatchingTagProcessor()
            .setDefaultProcessor(cleaner)
            .register(
                HtmlTagDictionary.PRE,
                new HtmlPreformattedNodeProcessor());
        addProcessor(preprocessor);

        DispatchingTagProcessor postprocessor = new DispatchingTagProcessor()
            .setDefaultProcessor(new NullProcessor())
            .register(HtmlTagDictionary.SPAN, new SkipNodeProcessor())
            .register(HtmlTagDictionary.DIV, new HtmlDivNodeProcessor())
            .register(
                new HtmlListNodeProcessor(),
                HtmlTagDictionary.UL,
                HtmlTagDictionary.OL)
            .register(HtmlTagDictionary.TABLE, new HtmlTableNodeProcessor());
        addProcessor(postprocessor);
    }
}