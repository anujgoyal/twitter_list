package twitter.list;

import android.app.Application;
import android.content.Context;

import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;

import io.fabric.sdk.android.Fabric;

/**
 * Created by agoyal3 on 2/27/16.
 */
public class ApplicationTwitterBug extends Application {
    // https://apps.twitter.com/apps
    // https://apps.twitter.com/app/12026591/keys
    private static String CONSUMER_KEY = "j0wZ4UFQaedhTGQ5Ew9slpsj7";
    private static String CONSUMER_SECRET = "pa90cSzFQYu5K1WUt5DxxZcD7fBvgFdjhjAfp3fOdfwvQ3F5kx";

    @Override
    public void onCreate() {
        super.onCreate();
        TwitterAuthConfig authConfig = new TwitterAuthConfig(CONSUMER_KEY, CONSUMER_SECRET);
        Fabric.with(this, new Twitter(authConfig));
        //, new TwitterCore(authConfig), new TweetUi());

    }

}