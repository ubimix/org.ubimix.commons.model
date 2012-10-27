package org.ubimix.model.cleaner;

import org.ubimix.commons.parser.html.HtmlTagDictionary;

/**
 * @author kotelnikov
 */
public class TagBurner extends CompositeTagListProcessor {

    public TagBurner() {
        addProcessor(new GenericTagProcessor());
        addProcessor(new TextNodeReducer());
        addProcessor(new InlineNodesProcessor());
        DispatchingTagListProcessor dispatcher = new DispatchingTagListProcessor()
            .setDefaultProcessor(new NullProcessor())
            .register(HtmlTagDictionary.SPAN, new SkipNodeProcessor())
            .register(HtmlTagDictionary.DIV, new HtmlDivNodeProcessor())
            .register(
                new HtmlListNodeProcessor(),
                HtmlTagDictionary.UL,
                HtmlTagDictionary.OL)
            .register(HtmlTagDictionary.TABLE, new HtmlTableNodeProcessor());
        addProcessor(dispatcher);
    }
}