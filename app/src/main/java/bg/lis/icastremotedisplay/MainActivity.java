package bg.lis.icastremotedisplay;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuItemCompat;
import androidx.mediarouter.app.MediaRouteActionProvider;
import androidx.mediarouter.media.MediaRouteSelector;
import androidx.mediarouter.media.MediaRouter;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.gms.cast.CastDevice;
import com.google.android.gms.cast.CastMediaControlIntent;
import com.google.android.gms.cast.CastRemoteDisplayLocalService;
import com.google.android.gms.cast.framework.CastButtonFactory;
import com.google.android.gms.cast.framework.CastContext;
import com.google.android.gms.common.api.Status;

public class MainActivity extends AppCompatActivity {

    private static final String INTENT_EXTRA_CAST_DEVICE = "INTENT_EXTRA_CAST_DEVICE";
    private TextView textView1;

    private MediaRouter mediaRouter;
    private MediaRouteSelector mediaRouteSelector;
    private MenuItem mediaRouteMenuItem;
    private CastDevice castDevice;
    private CastContext castContext;
    // delete me!

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView1 = (TextView)findViewById(R.id.textView1);

        CheckForPermision();

        castContext = CastContext.getSharedInstance(this);

        initMediaRouter();
    }

    private void CheckForPermision() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET)
                != PackageManager.PERMISSION_GRANTED) {
            textView1.setText("Permission is not granted");
            // Permission is not granted
        }
        else{
            textView1.setText("Permission granted!");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);

        mediaRouteMenuItem = CastButtonFactory.setUpMediaRouteButton(getApplicationContext(), menu, R.id.mi_action_cast);
        /*
        if (mediaRouteSelector != null) {
            MenuItem mediaRouteMenuItem = menu.findItem(R.id.mi_action_cast);

            if (MenuItemCompat.getActionProvider(mediaRouteMenuItem) instanceof MediaRouteActionProvider) {
                MediaRouteActionProvider mediaRouteActionProvider = (MediaRouteActionProvider) MenuItemCompat.getActionProvider(mediaRouteMenuItem);
                mediaRouteActionProvider.setRouteSelector(mediaRouteSelector);
            }
        }
        */
        return true;
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (!isRemoteDisplaying() && castDevice != null) {
            startCastService(castDevice);
        }
    }

    @Override
    public void onDestroy() {
        if (mediaRouter != null) {
            mediaRouter.removeCallback(mMediaRouterCallback);
        }
        super.onDestroy();
    }

    private boolean isRemoteDisplaying() {
        return CastRemoteDisplayLocalService.getInstance() != null;
    }

    private void initMediaRouter() {
        // We check if we are in the correct API version
        mediaRouter = MediaRouter.getInstance(getApplicationContext());
        //String appIDString = CastMediaControlIntent. DEFAULT_MEDIA_RECEIVER_APPLICATION_ID;
        String appIDString = getString(R.string.app_cast_id);
        mediaRouteSelector = new MediaRouteSelector.Builder().addControlCategory(CastMediaControlIntent.categoryForCast(appIDString)).build();
        if (isRemoteDisplaying()) {
            this.castDevice = CastDevice.getFromBundle(mediaRouter.getSelectedRoute().getExtras());
        } else {
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                castDevice = extras.getParcelable(INTENT_EXTRA_CAST_DEVICE);
            }
        }

        // Add a callback to the MediaRouter to trigger discovery
        mediaRouter.addCallback(mediaRouteSelector, mMediaRouterCallback, MediaRouter.CALLBACK_FLAG_REQUEST_DISCOVERY);
    }

    // Handle the callback event when the user selects a device.
    private final MediaRouter.Callback mMediaRouterCallback = new MediaRouter.Callback() {
        @Override
        public void onRouteSelected(MediaRouter router, MediaRouter.RouteInfo info) {
            castDevice = CastDevice.getFromBundle(info.getExtras());
            // At this point a Cast Device is selected and we start to Cast to this Device.
            startCastService(castDevice);
        }

        @Override
        public void onRouteUnselected(MediaRouter router, MediaRouter.RouteInfo info) {
            if (isRemoteDisplaying()) {
                CastRemoteDisplayLocalService.stopService();
            }
            castDevice = null;
        }
    };

    private void startCastService(CastDevice castDevice) {
        Intent intent = new Intent(MainActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent notificationPendingIntent = PendingIntent.getActivity(MainActivity.this, 0, intent, 0);

        CastRemoteDisplayLocalService.NotificationSettings settings = new CastRemoteDisplayLocalService.NotificationSettings.Builder().setNotificationPendingIntent(notificationPendingIntent).build();

        //String appIDString = CastMediaControlIntent. DEFAULT_MEDIA_RECEIVER_APPLICATION_ID;
        String appIDString = getString(R.string.app_cast_id);
        CastRemoteDisplayLocalService.startService(MainActivity.this, PresentationService.class,
                appIDString, castDevice, settings,
                new CastRemoteDisplayLocalService.Callbacks() {
                    @Override
                    public void onServiceCreated(CastRemoteDisplayLocalService service) {
                       // ((PresentationService) service).setRemoteText("Hello, Remote!");
                    }

                    @Override
                    public void onRemoteDisplaySessionStarted(CastRemoteDisplayLocalService service) {
                        //
                    }

                    @Override
                    public void onRemoteDisplaySessionError(Status errorReason) {
                        //
                    }

                    @Override
                    public void onRemoteDisplaySessionEnded(CastRemoteDisplayLocalService castRemoteDisplayLocalService) {
                        //
                    }
                });
        if (CastRemoteDisplayLocalService.getInstance() != null) {
            textView1.setText(CastRemoteDisplayLocalService.getInstance().getClass().toString());
        } else {
            textView1.setText("CastRemoteDisplayLocalService.getInstance() is NULL");
        }


    }
}