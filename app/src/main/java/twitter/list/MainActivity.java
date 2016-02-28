package twitter.list;

import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

public class MainActivity extends AppCompatActivity {
    private final String TAG = getClass().getSimpleName();
    private static String TWITTER_LIST = "bugTwitter_20160228";
    private TwitterLoginButton mLoginButton;
    private TwitterSession mSession;
    private TextView mTvName;
    private Button mFollowButton, mUnfollowButton;
    private RelativeLayout mRel;
    private MyTwitterApiClient mAPI;
    private int mColorRed, mColorBlue;

    // assumes a valid session;
    // create a temporary Twitter List for test purposes
    private void setupList() {
        Log.i(TAG, "[setupList]");
        mLoginButton.setVisibility(View.GONE);
        mLoginButton.setClickable(false);
        mRel.setVisibility(View.VISIBLE);
        mAPI = new MyTwitterApiClient(mSession);
        mAPI.getCustomService().createList(TWITTER_LIST, "public",
                new com.twitter.sdk.android.core.Callback<JsonElement>() {
                    @Override
                    public void success(Result<JsonElement> result) {
                        Log.d(TAG, "[createList] status: " + result.response.getStatus());
                        mTvName.setText(TWITTER_LIST + " created.");
                        mTvName.setTextColor(mColorBlue);
                    }

                    @Override
                    public void failure(TwitterException e) {
                        Log.e(TAG, "[createList] error: " + e.getMessage());
                    }
                });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "[onCreate]");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // setup fields
        mLoginButton = (TwitterLoginButton) findViewById(R.id.button_twitter_login);
        mTvName = (TextView) findViewById(R.id.tv_info);
        mFollowButton = (Button) findViewById(R.id.button_follow);
        mUnfollowButton = (Button) findViewById(R.id.button_unfollow);
        mRel = (RelativeLayout) findViewById(R.id.rel_block);
        // setup colors
        mColorBlue = ContextCompat.getColor(this, android.R.color.holo_blue_dark);
        mColorRed = ContextCompat.getColor(this, android.R.color.holo_red_dark);

        // check session validity
        mSession = Twitter.getSessionManager().getActiveSession();
        if (mSession == null) {
            Log.d(TAG, "Twitter session null");
            mLoginButton.setVisibility(View.VISIBLE);
            mRel.setVisibility(View.GONE);
            mLoginButton.setClickable(true);
        } else {
            Log.d(TAG, "Twitter session valid");
            setupList();
        }

        // setup login button callback; notes that it calls the Activity.onActivityResult
        // which then has to call mLoginButton.onActivityResult
        mLoginButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                Log.d(TAG, "[success] session retrieved");
                mSession = Twitter.getSessionManager().getActiveSession();
                setupList();
            }

            @Override
            public void failure(TwitterException e) {
                Log.e(TAG, "[failure]" + e.getMessage());
            }
        });

        // follow a user
        mFollowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                followOnTwitter(TWITTER_LIST, "fabric");
            }
        });

        mUnfollowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                unfollowOnTwitter(TWITTER_LIST, "fabric");
            }
        });
    }

    // assumes valid mAPI, mSession
    private void followOnTwitter(final String listSlug, final String twitterId) {
        Log.i(TAG, "[followOnTwitter] " + listSlug + ", " + twitterId);
        mAPI.getCustomService().createListMember(listSlug, mSession.getUserName(), twitterId,
                new com.twitter.sdk.android.core.Callback<JsonElement>() {
                    @Override
                    public void success(Result<JsonElement> result) {
                        Log.d(TAG, "[createListMember] status: " + result.response.getStatus());
                        getListMembers(TWITTER_LIST, twitterId, true /*expected to be found*/);
                    }

                    @Override
                    public void failure(TwitterException e) {
                        Log.e(TAG, "[createListMember] error: " + e.getMessage());
                    }
                });
    }

    // assumes valid mAPI, mSession
    private void unfollowOnTwitter(final String listSlug, final String twitterId) {
        Log.i(TAG, "[unfollowOnTwitter] " + listSlug + ", " + twitterId);
        mAPI.getCustomService().removeListMember(listSlug, mSession.getUserName(), twitterId,
                new com.twitter.sdk.android.core.Callback<JsonElement>() {
                    @Override
                    public void success(Result<JsonElement> result) {
                        Log.d(TAG, "[removeListMember] status: " + result.response.getStatus());
                        getListMembers(TWITTER_LIST, twitterId, false);
                    }

                    @Override
                    public void failure(TwitterException e) {
                        Log.e(TAG, "[removeListMember] error: " + e.getMessage());
                    }
                });
    }

    private void getListMembers(final String slug, final String twitterId, final boolean isExpectedToBeFound) {
        Log.i(TAG, "[getListMembers] slug: " + slug);
        mAPI.getCustomService().getListMembers(slug, mSession.getUserName(), new Callback<JsonElement>() {
            @Override
            public void success(Result<JsonElement> result) {
                Log.d(TAG, "[getListMembers] success: " + result.response.getStatus());
                final JsonElement je = result.data;
                final JsonArray jUsers = je.getAsJsonObject().get("users").getAsJsonArray();
                Log.d(TAG, "[getListMembers] users: " + jUsers.size());

                boolean isCorrect = false;

                // reduce Users down to just screen_name
                for (int i = 0; i < jUsers.size(); ++i) {
                    final String listName = jUsers.get(i).getAsJsonObject().get("screen_name").toString();
                    Log.d(TAG, "[getListMembers] screen_name: " + listName);
                    // if twitterId was just added to list, would expect it to be found
                    // if twitterId was just removed from list, would not expect it to be found
                    if (twitterId.equals(listName)) {
                        isCorrect = isExpectedToBeFound ? true : false;
                    }
                }
                if (isExpectedToBeFound) {
                    mTvName.setText(twitterId + "followed: " + isCorrect);
                } else {
                    mTvName.setText(twitterId + "unfollowed: " + isCorrect);
                }

                // depending on correctness text is blue or red
                mTvName.setTextColor(isCorrect ? mColorBlue : mColorRed);
            }

            @Override
            public void failure(TwitterException e) {
                Log.e(TAG, "[getListMembers] failure: " + e.getMessage());
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i(TAG, "[onActivityResult]");
        super.onActivityResult(requestCode, resultCode, data);
        // Pass activity result to login button
        mLoginButton.onActivityResult(requestCode, resultCode, data);
    }
}
