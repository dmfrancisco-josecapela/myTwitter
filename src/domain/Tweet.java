package domain;


/**
 * Esta classe representa um tweet (ou também chamado status).
 * Retirado do tutorial colocado no WoC.
 */
public class Tweet
{
    public static final int MAX_LENGTH = 140; // Número máximo de caracteres

    private String id;
    private String createdAt;
    private String text;
    private String source;
    private boolean truncated;
    private String replyToTweetId;
    private String replyToUserId;
    private User author;

    public Tweet() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public boolean isTruncated() {
        return truncated;
    }

    public void setTruncated(boolean truncated) {
        this.truncated = truncated;
    }

    public String getReplyToTweetId() {
        return replyToTweetId;
    }

    public void setReplyToTweetId(String replyToTweetId) {
        this.replyToTweetId = replyToTweetId;
    }

    public String getReplyToUserId() {
        return replyToUserId;
    }

    public void setReplyToUserId(String replyToUserId) {
        this.replyToUserId = replyToUserId;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    /** Devolve a mensagem da estrutura tweet (incluindo autor e data) */
    public String toString()
    {
        String str = "\"";

        if (text.length() > 70) {
            str += text.substring(0, 70) + "\n";
            str += text.substring(70);
        }
        else str += text;
        str += "\"\n  by "+ this.author.getName() +" at "+ createdAt +".";

        return str;
    }
}
