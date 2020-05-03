package bg.lis.icastremotedisplay;

import android.content.Context;

import com.google.android.gms.cast.CastMediaControlIntent;
import com.google.android.gms.cast.framework.CastOptions;
import com.google.android.gms.cast.framework.OptionsProvider;
import com.google.android.gms.cast.framework.SessionProvider;

import java.util.List;

public class CastOptionsProvider implements OptionsProvider {
    @Override
    public CastOptions getCastOptions(Context context) {
        //String appIDString = CastMediaControlIntent. DEFAULT_MEDIA_RECEIVER_APPLICATION_ID;
        String appIDString = context.getString(R.string.app_cast_id);
        CastOptions castOptions = new CastOptions.Builder().setReceiverApplicationId(appIDString).build();

        return castOptions;
    }

    @Override
    public List<SessionProvider> getAdditionalSessionProviders(Context context) {
        return null;
    }
}
