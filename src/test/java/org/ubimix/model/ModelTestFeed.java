/**
 * 
 */
package org.ubimix.model;

/**
 * @author kotelnikov
 */
public class ModelTestFeed {

    private ModelObject fAuthor;

    private ModelObject fFeed;

    private ModelObject fFirstPost;

    private ModelObject fPostWithSubentreis;

    private ModelObject fSecondPost;

    private ModelObject fSubEntry1;

    private ModelObject fSubEntry2;

    /**
     * 
     */
    public ModelTestFeed() {
        fAuthor = ModelObject
            .parse("{firstName: 'John', lastName: 'Smith'}")
            .setType("person");
        fFirstPost = newPost("First Post", "Just a simple try");
        fSecondPost = newPost("Second Post", "A very long post starts here...");

        fSubEntry1 = newPost("Proin a leo sed velit mollis luctus", ""
            + " Morbi ac magna nulla. Sed scelerisque venenatis"
            + " quam, et pretium leo rhoncus pretium. "
            + "Sed tristique, est eu cursus sodales, dui "
            + "lectus pulvinar enim, id "
            + "vestibulum nisi turpis nec metus.");
        fSubEntry2 = newPost(
            "Nunc aliquam commodo",
            "Sed a libero vel ipsum tempor euismod");
        fPostWithSubentreis = newPost(
            "Nunc risus metus, faucibus scelerisque",
            ""
                + "Phasellus lobortis condimentum augue, sed mattis augue "
                + "ornare nec. Donec nulla lorem, porttitor sed viverra "
                + "nec, malesuada sit amet felis. Praesent sagittis "
                + "dignissim hendrerit.").setValues(
            "entries",
            fSubEntry1,
            fSubEntry2);

        fFeed = new ModelObject();
        fFeed.setValue("!", "feed");
        fFeed.setValue("title", "John's blog");
        fFeed.setValue("date", "08/02/2012");
        fFeed.setValue("description", "It is my new blog");
        fFeed.setValue("author", fAuthor);
        fFeed.setValues(
            "entries",
            fFirstPost,
            fSecondPost,
            newPost("Lorem ipsum dolor sit amet. ", ""
                + "Praesent pretium, diam non pretium gravida, arcu arcu "
                + "lobortis sem, et venenatis sem metus et augue. Vivamus "
                + "at lectus ac ligula facilisis cursus. Vestibulum "
                + "accumsan mollis tortor non pellentesque. Etiam vitae "
                + "tellus massa. Morbi adipiscing elementum dignissim."),
            newPost("Maecenas augue nibh", ""
                + "Quisque quis dui eros. Proin sed odio viverra erat "
                + "elementum porta. Proin sed risus tellus, sed hendrerit "
                + "ligula. Vestibulum et consequat nibh"),
            newPost("Nunc risus metus, faucibus scelerisque", ""
                + "Phasellus lobortis condimentum augue, sed mattis augue "
                + "ornare nec. Donec nulla lorem, porttitor sed viverra "
                + "nec, malesuada sit amet felis. Praesent sagittis "
                + "dignissim hendrerit."),
            fPostWithSubentreis);
    }

    public ModelObject getAuthor() {
        return fAuthor;
    }

    public ModelObject getFeed() {
        return fFeed;
    }

    public ModelObject getFirstPost() {
        return fFirstPost;
    }

    public ModelObject getPostWithSubentreis() {
        return fPostWithSubentreis;
    }

    public ModelObject getSecondPost() {
        return fSecondPost;
    }

    public ModelObject getSubEntry1() {
        return fSubEntry1;
    }

    public ModelObject getSubEntry2() {
        return fSubEntry2;
    }

    private ModelObject newPost(String title, String content) {
        return new ModelObject()
            .setType("post")
            .setValue("title", title)
            .setValue("content", content);
    }

}
