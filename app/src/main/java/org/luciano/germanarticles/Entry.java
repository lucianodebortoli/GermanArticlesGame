package org.luciano.germanarticles;

public class Entry {

    private String ID;
    private String translation;
    private String traduccion;
    private String article;
    private String noun;

    enum columns {
        COLUMN_ID,
        COLUMN_ARTICLE,
        COLUMN_NOUN,
        COLUMN_TRANSLATION,
        COLUMN_TRADUCCION
    }

    public String getID() { return ID; }
    public void setID(String ID) { this.ID = ID; }

    public String getTranslation() { return translation; }
    public void setTranslation(String translation) { this.translation = translation; }

    public String getTraduccion() { return traduccion; }
    public void setTraduccion(String translation) { this.traduccion = translation; }

    public String getArticle() { return article; }
    public void setArticle(String article) { this.article = article; }

    public String getNoun() { return noun; }
    public void setNoun(String noun) {this.noun = noun; }

}
