package twitter.list;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

public class MainActivity extends AppCompatActivity {
    private final String TAG = getClass().getSimpleName();
    private TwitterLoginButton mLoginButton;
    private TwitterSession mSession;
    private TextView mTvName;
    private Button mBtnFollow, mBtnUnfollow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "[onCreate]");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mLoginButton = (TwitterLoginButton) findViewById(R.id.button_twitter_login);
        mTvName = (TextView) findViewById(R.id.tv_info);
        mBtnFollow = (Button) findViewById(R.id.button_follow);
        mBtnUnfollow = (Button) findViewById(R.id.button_unfollow);
        final RelativeLayout rel = (RelativeLayout) findViewById(R.id.rel_block);

        mSession = Twitter.getSessionManager().getActiveSession();
        if (mSession == null) {
            Log.d(TAG, "Twitter session null");
            mLoginButton.setVisibility(View.VISIBLE);
            rel.setVisibility(View.GONE);
            mLoginButton.setClickable(true);
        } else {
            Log.d(TAG, "Twitter session valid");
            mLoginButton.setVisibility(View.GONE);
            mLoginButton.setClickable(false);
        }

        // setup login button callback; notes that it calls the Activity.onActivityResult
        // which then has to call back into this Fragment's onActivityResult
        // very confusing!
        mLoginButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                Log.d(TAG, "[success] session retrieved");
                mSession = Twitter.getSessionManager().getActiveSession();
                mLoginButton.setVisibility(View.GONE);
                mLoginButton.setClickable(false);
                rel.setVisibility(View.VISIBLE);
            }

            @Override
            public void failure(TwitterException e) {
                Log.e(TAG, "[failure]" + e.getMessage());
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
