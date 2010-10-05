package utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import domain.Tweet;
import domain.User;


/** Classe que utiliza JDOM para realizar o parsing de ficheiros XML */
public class XmlParsingUtils
{
    /** Parsing de um nome de utilizador */
    public static String parseUsername(HttpResponse response)
    {
        try {
            Document d = new SAXBuilder().build(response.getEntity().getContent());
            return d.getRootElement().getChildText("name");

        } catch (IllegalStateException e) {
        } catch (JDOMException e) {
        } catch (IOException e) { }

        return null;
    }

    /** Parsing de um só utilizador */
    public static User parseUser(HttpResponse response)
    {
        try {
            Document d = new SAXBuilder().build(response.getEntity().getContent());
            Element e = d.getRootElement();
            return parseUser(e);

        } catch (IllegalStateException e) {
            System.err.println(e.getLocalizedMessage());
        } catch (JDOMException e) {
            System.err.println(e.getLocalizedMessage());
        } catch (IOException e) {
            System.err.println(e.getLocalizedMessage());
        }
        return null;
    }

    /** Parsing de um só utilizador */
    public static User parseUser(Element e)
    {
        User user = new User();

        user.setId(e.getChildText("id"));
        user.setName(e.getChildText("name"));
        user.setScreenName(e.getChildText("screen_name"));
        user.setDescription(e.getChildText("description"));
        user.setUrl(e.getChildText("url"));
        user.setProtectedProfile(Boolean.parseBoolean(e.getChildText("protected")));

        user.setFollowers(Integer.parseInt(e.getChildText("followers_count")));
        user.setFollowing(Integer.parseInt(e.getChildText("friends_count")));
        user.setCreatedAt(e.getChildText("created_at"));
        user.setTweets(Integer.parseInt(e.getChildText("statuses_count")));

        return user;
    }

    /** Parsing de um só tweet */
    public static Tweet parseTweet(Element e)
    {
        Tweet tweet = new Tweet();

        tweet.setId(e.getChildText("id"));
        tweet.setCreatedAt(e.getChildText("created_at"));
        tweet.setText(e.getChildText("text"));
        tweet.setSource(e.getChildText("source"));
        tweet.setTruncated(Boolean.parseBoolean(e.getChildText("truncated")));
        tweet.setReplyToTweetId(e.getChildText("in_reply_to_status_id"));
        tweet.setReplyToUserId(e.getChildText("in_reply_to_user_id"));

        return tweet;
    }

    /** Parsing de vários tweets */
    @SuppressWarnings("unchecked")
    public static ArrayList<Tweet> parseTweets(HttpResponse response)
    {
        ArrayList<Tweet> tweets = new ArrayList<Tweet>();

        try {
            Document d = new SAXBuilder().build(response.getEntity().getContent());

            List<Element> statuses = d.getRootElement().getChildren("status");
            for (Element status : statuses){
                Tweet t = parseTweet(status);
                t.setAuthor(parseUser(status.getChild("user")));
                tweets.add(t);
            }
            return tweets;

        } catch (IllegalStateException e) {
            System.err.println(e.getLocalizedMessage());
        } catch (JDOMException e) {
            System.err.println(e.getLocalizedMessage());
        } catch (IOException e) {
            System.err.println(e.getLocalizedMessage());
        }
        return null;
    }

    /** Parsing de vários utilizadores */
    @SuppressWarnings("unchecked")
    public static ArrayList<User> parseUsers(HttpResponse response) {
        ArrayList<User> results = new ArrayList<User>();

        try {
            Document d = new SAXBuilder().build(response.getEntity().getContent());
            List<Element> users = d.getRootElement().getChildren("user");
            for (Element user : users)
                results.add(parseUser(user));
            return results;

        } catch (IllegalStateException e) {
            System.err.println(e.getLocalizedMessage());
        } catch (JDOMException e) {
            System.err.println(e.getLocalizedMessage());
        } catch (IOException e) {
            System.err.println(e.getLocalizedMessage());
        }
        return null;
    }
}
