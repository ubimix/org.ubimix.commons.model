/**
 * 
 */
package org.ubimix.model.cleaner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * @author kotelnikov
 */
public class HtmlTagDictionary {

    /** Defines a hyperlink */
    public static final String A = "a";

    /** Defines an abbreviation */
    public static final String ABBR = "abbr";

    /** Not supported in HTML5 */
    public static final String ACRONYM = "acronym";

    /** Defines contact information for the author/owner of a document/article */
    public static final String ADDRESS = "address";

    public static final Set<String> ALL_ATTRIBUTES = new HashSet<String>();

    // The full list of all elements.
    public static final Set<String> ALL_ELEMENTS = new HashSet<String>();

    /** Not supported in HTML5 */
    public static final String APPLET = "applet";

    /** Defines an area inside an image-map */
    public static final String AREA = "area";

    /** New Defines an article */
    public static final String ARTICLE = "article";

    /** New Defines content aside from the page content */
    public static final String ASIDE = "aside";

    public static final String ATTR_ALIGN = "align";

    public static final String ATTR_ALT = "alt";

    public static final String ATTR_BACKGROUND = "background";

    public static final String ATTR_BGCOLOR = "bgcolor";

    public static final String ATTR_BORDER = "border";

    public static final String ATTR_CELLPADDING = "cellpadding";

    public static final String ATTR_CELLSPACING = "cellspacing";

    public static final String ATTR_CLASS = "class";

    public static final String ATTR_COLOR = "color";

    public static final String ATTR_FACE = "face";

    public static final String ATTR_HALIGN = "valign";

    public static final String ATTR_HREF = "href";

    public static final String ATTR_ID = "id";

    public static final String ATTR_SIZE = "size";

    public static final String ATTR_SRC = "src";

    public static final String ATTR_STYLE = "style";

    public static final String ATTR_TARGET = "target";

    public static final String ATTR_TITLE = "title";

    public static final String ATTR_VALIGN = "valign";

    /** New Defines sound content */
    public static final String AUDIO = "audio";

    /** Defines bold text */
    public static final String B = "b";

    /** Specifies the base URL/target for all relative URLs in a document */
    public static final String BASE = "base";

    /** Not supported in HTML5 */
    public static final String BASEFONT = "basefont";

    /**
     * New Isolates a part of text that might be formatted in a different
     * direction from other text outside it
     */
    public static final String BDI = "bdi";

    /** Overrides the current text direction */
    public static final String BDO = "bdo";

    /** Not supported in HTML5 */
    public static final String BIG = "big";

    // The full list of elements which can contain other block elements
    public static final Set<String> BLOCK_CONTAINER_ELEMENTS = new LinkedHashSet<String>();

    // The full list of all block elements.
    public static final Set<String> BLOCK_ELEMENTS = new LinkedHashSet<String>();

    /** Defines a section that is quoted from another source */
    public static final String BLOCKQUOTE = "blockquote";

    /** Defines the document's body */
    public static final String BODY = "body";

    // The full list of all elements available in the body.
    public static final Set<String> BODY_CONTENT_ELEMENTS = new HashSet<String>();

    /** Defines a single line break */
    public static final String BR = "br";

    /** Defines a clickable button */
    public static final String BUTTON = "button";

    /**
     * New Used to draw graphics, on the fly, via scripting (usually JavaScript)
     */
    public static final String CANVAS = "canvas";

    /** Defines a table caption */
    public static final String CAPTION = "caption";

    /** Not supported in HTML5 */
    public static final String CENTER = "center";

    /** Defines the title of a work */
    public static final String CITE = "cite";

    /** Defines a piece of computer code */
    public static final String CODE = "code";

    /** Specifies column properties for each column within a <colgroup> element */
    public static final String COL = "col";

    /** Specifies a group of one or more columns in a table for formatting */
    public static final String COLGROUP = "colgroup";

    /** HTML5: Defines a command button that a user can invoke */
    public static final String COMMAND = "command";

    /** HTML5: Specifies a list of pre-defined options for input controls */
    public static final String DATALIST = "datalist";

    /** Defines a description of an item in a definition list */
    public static final String DD = "dd";

    /** Defines a text that has been deleted from a document */
    public static final String DEL = "del";

    /** HTML5: Defines additional details that the user can view or hide */
    public static final String DETAILS = "details";

    /** Defines a definition term */
    public static final String DFN = "dfn";

    /** Not supported in HTML5 */
    public static final String DIR = "dir";

    /** Defines a section in a document */
    public static final String DIV = "div";

    /** Defines a definition list */
    public static final String DL = "dl";

    /** Defines a term (an item) in a definition list */
    public static final String DT = "dt";

    /** Defines emphasized text */
    public static final String EM = "em";

    /**
     * New Defines a container for an external application or interactive
     * content (a plug-in)
     */
    public static final String EMBED = "embed";

    public static final Set<String> EMPTY_ELEMENTS = new HashSet<String>();

    /** Groups related elements in a form */
    public static final String FIELDSET = "fieldset";

    /** HTML5: Defines a caption for a <figure> element */
    public static final String FIGCAPTION = "figcaption";

    /** HTML5: Specifies self-contained content */
    public static final String FIGURE = "figure";

    /** Not supported in HTML5 */
    public static final String FONT = "font";

    /** HTML5: Defines a footer for a document or section */
    public static final String FOOTER = "footer";

    /** Defines an HTML form for user input */
    public static final String FORM = "form";

    /** Not supported in HTML5 */
    public static final String FRAME = "frame";

    /** Not supported in HTML5 */
    public static final String FRAMESET = "frameset";

    /** Defines HTML headings */
    public static final String H1 = "h1";

    /** Defines HTML headings */
    public static final String H2 = "h2";

    /** Defines HTML headings */
    public static final String H3 = "h3";

    /** Defines HTML headings */
    public static final String H4 = "h4";

    /** Defines HTML headings */
    public static final String H5 = "h5";

    /** Defines HTML headings */
    public static final String H6 = "h6";

    /** Defines information about the document */
    public static final String HEAD = "head";

    /** HTML5: Defines a header for a document or section */
    public static final String HEADER = "header";

    public static final List<String> HEADERS = new ArrayList<String>();

    /**
     * New Groups heading (<h1>to
     * <h6>) elements
     */
    public static final String HGROUP = "hgroup";

    /** Defines a thematic change in the content */
    public static final String HR = "hr";

    /** Defines the root of an HTML document */
    public static final String HTML = "html";

    public static final Set<String> HTML5_ELEMENTS = new HashSet<String>();

    /** Defines a part of text in an alternate voice or mood */
    public static final String I = "i";

    /** Defines an inline frame */
    public static final String IFRAME = "iframe";

    /** Defines an image */
    public static final String IMG = "img";

    public static final List<String> INLINE_ELEMENTS = new ArrayList<String>();

    /** Defines an input control */
    public static final String INPUT = "input";

    /** Defines a text that has been inserted into a document */
    public static final String INS = "ins";

    /** Defines keyboard input */
    public static final String KBD = "kbd";

    /** HTML5: Defines a key-pair generator field (for forms) */
    public static final String KEYGEN = "keygen";

    /** Defines a label for an input element */
    public static final String LABEL = "label";

    /** Defines a caption for a <fieldset>, <figure>, or <details> element */
    public static final String LEGEND = "legend";

    /** Defines a list item */
    public static final String LI = "li";

    /**
     * Defines the relationship between a document and an external resource
     * (most used to link to style sheets)
     */
    public static final String LINK = "link";

    /** Defines a client-side image-map */
    public static final String MAP = "map";

    /** HTML5: Defines marked/highlighted text */
    public static final String MARK = "mark";

    /** Defines a list/menu of commands */
    public static final String MENU = "menu";

    /** Defines metadata about an HTML document */
    public static final String META = "meta";

    /** HTML5: Defines a scalar measurement within a known range (a gauge) */
    public static final String METER = "meter";

    /** HTML5: Defines navigation links */
    public static final String NAV = "nav";

    /** Not supported in HTML5 */
    public static final String NOFRAMES = "noframes";

    public static final Set<String> NON_CONTENT_ELEMENTS = new HashSet<String>();

    /**
     * Defines an alternate content for users that do not support client-side
     * scripts
     */
    public static final String NOSCRIPT = "noscript";

    public static final Set<String> NOT_SUPPORTED_IN_HTML5 = new HashSet<String>();

    /** Defines an embedded object */
    public static final String OBJECT = "object";

    /** Defines an ordered list */
    public static final String OL = "ol";

    /** Defines a group of related options in a drop-down list */
    public static final String OPTGROUP = "optgroup";

    /** Defines an option in a drop-down list */
    public static final String OPTION = "option";

    /** HTML5: Defines the result of a calculation */
    public static final String OUTPUT = "output";

    /** Defines a paragraph */
    public static final String P = "p";

    /** Defines a parameter for an object */
    public static final String PARAM = "param";

    /** Defines preformatted text */
    public static final String PRE = "pre";

    /** HTML5: Represents the progress of a task */
    public static final String PROGRESS = "progress";

    /** Defines a short quotation */
    public static final String Q = "q";

    /**
     * New Defines what to show in browsers that do not support ruby annotations
     */
    public static final String RP = "rp";

    /**
     * New Defines an explanation/pronunciation of characters (for East Asian
     * typography)
     */
    public static final String RT = "rt";

    /** HTML5: Defines a ruby annotation (for East Asian typography) */
    public static final String RUBY = "ruby";

    /** Defines text that is no longer correct */
    public static final String S = "s";

    /** Defines sample output from a computer program */
    public static final String SAMP = "samp";

    /** Defines a client-side script */
    public static final String SCRIPT = "script";

    /** HTML5: Defines a section in a document */
    public static final String SECTION = "section";

    /** Defines a drop-down list */
    public static final String SELECT = "select";

    /** Defines smaller text */
    public static final String SMALL = "small";

    /**
     * New Defines multiple media resources for media elements (<video> and
     * <audio>)
     */
    public static final String SOURCE = "source";

    /** Defines a section in a document */
    public static final String SPAN = "span";

    /** Not supported in HTML5 */
    public static final String STRIKE = "strike";

    /** Defines important text */
    public static final String STRONG = "strong";

    /** Defines style information for a document */
    public static final String STYLE = "style";

    /** Defines subscripted text */
    public static final String SUB = "sub";

    /** HTML5: Defines a visible heading for a <details> element */
    public static final String SUMMARY = "summary";

    /** Defines superscripted text */
    public static final String SUP = "sup";

    /** Defines a table */
    public static final String TABLE = "table";

    /** Groups the body content in a table */
    public static final String TBODY = "tbody";

    /** Defines a cell in a table */
    public static final String TD = "td";

    public static final String TEXT = "*text";

    /** Defines a multiline input control (text area) */
    public static final String TEXTAREA = "textarea";

    /** Groups the footer content in a table */
    public static final String TFOOT = "tfoot";

    /** Defines a header cell in a table */
    public static final String TH = "th";

    /** Groups the header content in a table */
    public static final String THEAD = "thead";

    /** HTML5: Defines a date/time */
    public static final String TIME = "time";

    /** Defines a title for the document */
    public static final String TITLE = "title";

    /** Defines a row in a table */
    public static final String TR = "tr";

    /** HTML5: Defines text tracks for media elements (<video> and <audio>) */
    public static final String TRACK = "track";

    /** Not supported in HTML5 */
    public static final String TT = "tt";

    /** Defines text that should be stylistically different from normal text */
    public static final String U = "u";

    /** Defines an unordered list */
    public static final String UL = "ul";

    /** Defines a variable */
    public static final String VAR = "var";

    /** HTML5: Defines a video or movie */
    public static final String VIDEO = "video";

    /** HTML5: Defines a possible line-break */
    public static final String WBR = "wbr";

    static {
        ALL_ELEMENTS.addAll(Arrays.asList(
            A,
            ABBR,
            ACRONYM,
            ADDRESS,
            APPLET,
            AREA,
            ARTICLE,
            ASIDE,
            AUDIO,
            B,
            BASE,
            BASEFONT,
            BDI,
            BDO,
            BIG,
            BLOCKQUOTE,
            BODY,
            BR,
            BUTTON,
            CANVAS,
            CAPTION,
            CENTER,
            CITE,
            CODE,
            COL,
            COLGROUP,
            COMMAND,
            DATALIST,
            DD,
            DEL,
            DETAILS,
            DFN,
            DIR,
            DIV,
            DL,
            DT,
            EM,
            EMBED,
            FIELDSET,
            FIGCAPTION,
            FIGURE,
            FONT,
            FOOTER,
            FORM,
            FRAME,
            FRAMESET,
            H1,
            H2,
            H3,
            H4,
            H5,
            H6,
            HEAD,
            HEADER,
            HGROUP,
            HR,
            HTML,
            I,
            IFRAME,
            IMG,
            INPUT,
            INS,
            KBD,
            KEYGEN,
            LABEL,
            LEGEND,
            LI,
            LINK,
            MAP,
            MARK,
            MENU,
            META,
            METER,
            NAV,
            NOFRAMES,
            NOSCRIPT,
            OBJECT,
            OL,
            OPTGROUP,
            OPTION,
            OUTPUT,
            P,
            PARAM,
            PRE,
            PROGRESS,
            Q,
            RP,
            RT,
            RUBY,
            S,
            SAMP,
            SCRIPT,
            SECTION,
            SELECT,
            SMALL,
            SOURCE,
            SPAN,
            STRIKE,
            STRONG,
            STYLE,
            SUB,
            SUMMARY,
            SUP,
            TABLE,
            TBODY,
            TD,
            TEXTAREA,
            TFOOT,
            TH,
            THEAD,
            TIME,
            TITLE,
            TR,
            TRACK,
            TT,
            U,
            UL,
            VAR,
            VIDEO,
            WBR));
        BODY_CONTENT_ELEMENTS.addAll(ALL_ELEMENTS);
        BODY_CONTENT_ELEMENTS.removeAll(Arrays.asList(
            BASE,
            BODY,
            HEAD,
            HTML,
            TITLE,
            LINK,
            META));

        HEADERS.add(H1);
        HEADERS.add(H2);
        HEADERS.add(H3);
        HEADERS.add(H4);
        HEADERS.add(H5);
        HEADERS.add(H6);

        BLOCK_ELEMENTS.addAll(HEADERS);
        BLOCK_ELEMENTS.addAll(Arrays.asList(
        // New (HTML5)
            ARTICLE,
            ASIDE,
            DETAILS,
            FIGCAPTION,
            FIGURE,
            FOOTER,
            HEADER,
            KEYGEN,
            NAV,
            OUTPUT,
            SECTION,
            SUMMARY,
            // Old
            BLOCKQUOTE,
            DIV,
            DL,
            FIELDSET,
            LEGEND,
            HR,
            IFRAME,
            OL,
            P,
            PRE,
            UL,
            TABLE));

        BLOCK_CONTAINER_ELEMENTS.addAll(Arrays.asList(
        // New (HTML5)
            FIGURE,
            FOOTER,
            HEADER,
            NAV,
            SECTION,
            SUMMARY,
            // Old
            BLOCKQUOTE,
            DIV,
            DD,
            FIELDSET,
            LI,
            PRE,
            TD,
            TH));

        NON_CONTENT_ELEMENTS.addAll(Arrays.asList(
            BUTTON,
            FORM,
            FRAME,
            HEAD,
            IFRAME,
            INPUT,
            OPTION,
            SELECT,
            SCRIPT,
            STYLE,
            TEXTAREA));
        EMPTY_ELEMENTS.addAll(Arrays.asList(HR, IMG, BR));

        INLINE_ELEMENTS.addAll(Arrays.asList(
            A,
            B,
            BR,
            EM,
            STRONG,
            I,
            IMG,
            SPAN,
            SUB,
            SUP,

            /* HTML 5 */
            AUDIO,
            MARK,
            PROGRESS,
            RUBY, /* ? */
            TIME,
            TRACK,
            VIDEO, /* ? */
            WBR));

        ALL_ATTRIBUTES.addAll(Arrays.asList(
            ATTR_ALIGN,
            ATTR_ALT,
            ATTR_BACKGROUND,
            ATTR_BGCOLOR,
            ATTR_BORDER,
            ATTR_CELLPADDING,
            ATTR_CELLSPACING,
            ATTR_CLASS,
            ATTR_COLOR,
            ATTR_FACE,
            ATTR_HALIGN,
            ATTR_HREF,
            ATTR_ID,
            ATTR_SIZE,
            ATTR_SRC,
            ATTR_STYLE,
            ATTR_TARGET,
            ATTR_TITLE,
            ATTR_VALIGN));

        HTML5_ELEMENTS.addAll(Arrays.asList(
            ARTICLE,
            ASIDE,
            AUDIO,
            COMMAND,
            DATALIST,
            DETAILS,
            FIGCAPTION,
            FIGURE,
            FOOTER,
            HEADER,
            KEYGEN,
            MARK,
            METER,
            NAV,
            OUTPUT,
            PROGRESS,
            RUBY,
            SECTION,
            SUMMARY,
            TIME,
            TRACK,
            VIDEO,
            WBR));

        NOT_SUPPORTED_IN_HTML5.addAll(Arrays.asList(
            ACRONYM,
            APPLET,
            BASEFONT,
            BIG,
            CENTER,
            DIR,
            FONT,
            FRAME,
            FRAMESET,
            NOFRAMES,
            STRIKE,
            TT));

    }

    public static int getHeaderLevel(String name) {
        return HEADERS.indexOf(name) + 1;
    }

    public static boolean isBlockContainerElement(String name) {
        return BLOCK_CONTAINER_ELEMENTS.contains(name);
    }

    public static boolean isBlockElement(String name) {
        return BLOCK_ELEMENTS.contains(name);
    }

    public static boolean isBodyContent(String name) {
        return BODY_CONTENT_ELEMENTS.contains(name);
    }

    public static boolean isContentElement(String name) {
        return !NON_CONTENT_ELEMENTS.contains(name);
    }

    public static boolean isDivElement(String name) {
        return DIV.equals(name);
    }

    public static boolean isEmptyElement(String name) {
        return EMPTY_ELEMENTS.contains(name);
    }

    public static boolean isFieldset(String name) {
        return FIELDSET.equals(name);
    }

    public static boolean isFontElement(String name) {
        return FONT.equals(name);
    }

    public static boolean isFormContentElement(String name) {
        return INPUT.equals(name)
            || SELECT.equals(name)
            || OPTION.equals(name)
            || BUTTON.equals(name)
            || TEXTAREA.equals(name);
    }

    public static boolean isFormElement(String name) {
        return FORM.equals(name);
    }

    public static boolean isHeader(String name) {
        return HEADERS.contains(name);
    }

    public static boolean isHTML5(String name) {
        return ALL_ELEMENTS.contains(name)
            && !NOT_SUPPORTED_IN_HTML5.contains(name);
    }

    public static boolean isHtmlAttribute(String name) {
        return ALL_ATTRIBUTES.contains(name);
    }

    public static boolean isHtmlElement(String name) {
        return ALL_ELEMENTS.contains(name);
    }

    public static boolean isImportantAttribute(String name) {
        return ATTR_TITLE.equals(name)
            || ATTR_SRC.equals(name)
            || ATTR_HREF.equals(name);
    }

    public static boolean isInlineElement(String name) {
        return INLINE_ELEMENTS.contains(name);
    }

    public static boolean isLineBreak(String name) {
        return BR.equals(name);
    }

    public static boolean isList(String name) {
        return DL.equals(name) || UL.equals(name) || OL.equals(name);
    }

    public static boolean isListItem(String name) {
        return LI.equals(name) || DT.equals(name) || DD.equals(name);
    }

    public static boolean isNewInHTML5(String name) {
        return !HTML5_ELEMENTS.contains(name);
    }

    public static boolean isNotSupportedInHTML5(String name) {
        return NOT_SUPPORTED_IN_HTML5.contains(name);
    }

    public static boolean isParagraph(String name) {
        return P.equals(name);
    }

    public static boolean isScriptElement(String name) {
        return SCRIPT.equals(name);
    }

    public static boolean isSpanElement(String name) {
        return SPAN.equals(name);
    }

    public static boolean isTableBody(String name) {
        return TBODY.equals(name);
    }

    public static boolean isTableCellElement(String name) {
        return TH.equals(name) || TD.equals(name);
    }

    public static boolean isTableElement(String name) {
        return TABLE.equals(name);
    }

    public static boolean isTableHeaderBodyOrRowElement(String name) {
        return THEAD.equals(name) || TBODY.equals(name) || TR.equals(name);
    }

    public static boolean isTableInnerElement(String name) {
        return isTableHeaderBodyOrRowElement(name) || isTableCellElement(name);
    }

    public static boolean isTableRow(String name) {
        return TR.equals(name);
    }

    public static boolean isTextflowContainer(String name) {
        return isHeader(name)
            || isParagraph(name)
            || isTableCellElement(name)
            || isListItem(name)
            || BLOCKQUOTE.equals(name)
            || PRE.equals(name);
    }

    public static boolean keepSpaces(String name) {
        return PRE.equals(name);
    }

}
