package bg.lis.icastremotedisplay;

import android.content.Context;
import android.os.Bundle;
import android.view.Display;
import android.view.WindowManager;
import android.widget.TextView;

import com.google.android.gms.cast.CastPresentation;
import com.google.android.gms.cast.CastRemoteDisplayLocalService;

public class PresentationService extends CastRemoteDisplayLocalService {

    private DetailPresentation castPresentation;

    @Override
    public void onCreatePresentation(Display display) {
        dismissPresentation();
        castPresentation = new DetailPresentation(this, display);
        try {
            castPresentation.show();
        } catch (WindowManager.InvalidDisplayException ex) {
            dismissPresentation();
        }
    }

    @Override
    public void onDismissPresentation() {
        dismissPresentation();
    }

    private void dismissPresentation() {
        if (castPresentation != null) {
            castPresentation.dismiss();
            castPresentation = null;
        }
    }

    public void setRemoteText(String msg) {
        if (castPresentation != null) {
            castPresentation.setRemoteText(msg);
        }
    }

    public class DetailPresentation extends CastPresentation {

        private TextView textView1;

        public DetailPresentation(Context context, Display display) {
            super(context, display);
        }

        @Override
        public void onCreate(Bundle savedInstanceState){
            super.onCreate(savedInstanceState);
            setContentView(R.layout.remote_display_layout);

            textView1 = (TextView) findViewById(R.id.textView1);
        }

        @Override
        public void onDetachedFromWindow() {
            super.onDetachedFromWindow();
        }

        public void setRemoteText(String msg) {
            textView1.setText(msg);
        }
    }
}
