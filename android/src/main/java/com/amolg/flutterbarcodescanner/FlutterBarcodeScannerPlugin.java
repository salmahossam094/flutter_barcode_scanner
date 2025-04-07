import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;

import java.util.Map;

import io.flutter.embedding.android.FlutterActivity;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.EventChannel;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry;
import io.flutter.plugin.common.PluginRegistry.ActivityResultListener;
import io.flutter.plugin.common.EventChannel.StreamHandler;
import io.flutter.embedding.engine.plugins.lifecycle.FlutterLifecycleAdapter;


/**
 * FlutterBarcodeScannerPlugin
 *
 * @@ -50,46 +41,61 @@ public class FlutterBarcodeScannerPlugin implements MethodCallHandler, ActivityR
 * public static boolean isContinuousScan = false;
 * static EventChannel.EventSink barcodeStream;
 * private EventChannel eventChannel;
 * <p>
 * /**
 * V2 embedding
 * @param activity
 * @param registrar
 */
private MethodChannel channel;
private FlutterPluginBinding pluginBinding;
private ActivityPluginBinding activityBinding;
private Application applicationContext;
// This is null when not using v2 embedding;
private Lifecycle lifecycle;
private LifeCycleObserver observer;

public FlutterBarcodeScannerPlugin() {
    @Override
    public void onAttachedToEngine (FlutterPluginBinding binding){
        applicationContext = (Application) binding.getApplicationContext();
    }

     private FlutterBarcodeScannerPlugin(FlutterActivity activity,
    final PluginRegistry.Registrar registrar){
        FlutterBarcodeScannerPlugin.activity = activity;
        @Override
        public void onDetachedFromEngine (FlutterPluginBinding binding){
            applicationContext = null;
        }

        @Override
        public void onAttachedToActivity (ActivityPluginBinding binding){
            activityBinding = binding;
            activity = (FlutterActivity) binding.getActivity();
            channel = new MethodChannel(binding.getBinaryMessenger(), CHANNEL);
            channel.setMethodCallHandler(this);
            eventChannel = new EventChannel(binding.getBinaryMessenger(), "flutter_barcode_scanner_receiver");
            eventChannel.setStreamHandler(this);
            binding.addActivityResultListener(this);
        }

        /**
         * Plugin registration.
         */
        public static void registerWith ( final PluginRegistry.Registrar registrar){
            if (registrar.activity() == null) {
                return;
                @Override
                public void onDetachedFromActivity () {
                    if (activityBinding != null) {
                        activityBinding.removeActivityResultListener(this);
                        activityBinding = null;
                    }
                    Activity activity = registrar.activity();
                    Application applicationContext = null;
                    if (registrar.context() != null) {
                        applicationContext = (Application) (registrar.context().getApplicationContext());
                        if (channel != null) {
                            channel.setMethodCallHandler(null);
                            channel = null;
                        }
                        FlutterBarcodeScannerPlugin instance = new FlutterBarcodeScannerPlugin((FlutterActivity) registrar.activity(), registrar);
                        instance.createPluginSetup(registrar.messenger(), applicationContext, activity, registrar, null);
                        if (eventChannel != null) {
                            eventChannel.setStreamHandler(null);
                            eventChannel = null;
                        }
                        activity = null;
                    }

                    @Override
                    public void onDetachedFromActivityForConfigChanges () {
                        onDetachedFromActivity();
                    }

                    @Override
                    public void onReattachedToActivityForConfigChanges (ActivityPluginBinding
                    binding){
                        onAttachedToActivity(binding);
                    }

                    @Override
                    public void onMethodCall (@NonNull MethodCall call, @NonNull Result result){
                        // ... (Your existing method call handling logic) ...
                        try {
                            pendingResult = result;

                            @ @ -123, 6 + 129, 7 @@public void onMethodCall (@NonNull MethodCall
                            call, @NonNull Result result){
                            }

                            private void startBarcodeScannerActivityView (String buttonText,
                            boolean isContinuousScan){
                                // ... (Your existing startBarcodeScannerActivityView logic) ...
                                try {
                                    Intent intent = new Intent(activity, BarcodeCaptureActivity.class).putExtra("cancelButtonText", buttonText);
                                    if (isContinuousScan) {
                                        @ @ -135, 17 + 142, 9 @@
                                        private void startBarcodeScannerActivityView (String
                                        buttonText,boolean isContin
                                    }
                                }


                                /**
                                 * Get the barcode scanning results in onActivityResult
                                 *
                                 * @param requestCode
                                 * @param resultCode
                                 * @param data
                                 * @return
                                 */
                                @Override
                                public boolean onActivityResult ( int requestCode,
                                int resultCode, Intent data){
                                    // ... (Your existing onActivityResult logic) ...
                                    if (requestCode == RC_BARCODE_CAPTURE) {
                                        if (resultCode == CommonStatusCodes.SUCCESS) {
                                            if (data != null) {
                                                @ @ -169, 9 + 168, 9 @@
                                                public boolean onActivityResult ( int requestCode,
                                                int resultCode, Intent data){
                                                    return false;
                                                }


                                                @Override
                                                public void onListen (Object
                                                o, EventChannel.EventSink eventSink){
                                                    // ... (Your existing onListen logic) ...
                                                    try {
                                                        barcodeStream = eventSink;
                                                    } catch (Exception e) {
                                                        @ @ -180, 19 + 179, 15 @@
                                                        public void onListen (Object
                                                        o, EventChannel.EventSink eventSink){

                                                            @Override
                                                            public void onCancel (Object o){
                                                                // ... (Your existing onCancel logic) ...
                                                                try {
                                                                    barcodeStream = null;
                                                                } catch (Exception e) {

                                                                }
                                                            }

                                                            /**
                                                             * Continuous receive barcode
                                                             *
                                                             * @param barcode
                                                             */
                                                            public static void onBarcodeScanReceiver
                                                            ( final Barcode barcode){
                                                                // ... (Your existing onBarcodeScanReceiver logic) ...
                                                                try {
                                                                    if (barcode != null && !barcode.displayValue.isEmpty()) {
                                                                        activity.runOnUiThread(new Runnable() {
                                                                            @ @ -206,170+201,4@@

                                                                            public void run() {
                                                                                Log.e(TAG, "onBarcodeScanReceiver: " + e.getLocalizedMessage());
                                                                            }
                                                                        }

                                                                        @Override
                                                                        public void onAttachedToEngine
                                                                        (FlutterPluginBinding
                                                                        binding){
                                                                            pluginBinding = binding;
                                                                        }

                                                                        @Override
                                                                        public void onDetachedFromEngine
                                                                        (FlutterPluginBinding
                                                                        binding){
                                                                            pluginBinding = null;
                                                                        }

                                                                        @Override
                                                                        public void onDetachedFromActivityForConfigChanges
                                                                        () {
                                                                            onDetachedFromActivity();
                                                                        }

                                                                        @Override
                                                                        public void onReattachedToActivityForConfigChanges
                                                                        (ActivityPluginBinding
                                                                        binding){
                                                                            onAttachedToActivity(binding);
                                                                        }

                                                                        /**
                                                                         * Setup method
                                                                         * Created after Embedding V2 API release
                                                                         *
                                                                         * @param messenger
                                                                         * @param applicationContext
                                                                         * @param activity
                                                                         * @param registrar
                                                                         * @param activityBinding
                                                                         */
                                                                        private void createPluginSetup
                                                                        (
                                                                        final BinaryMessenger messenger,
                                                                        final Application applicationContext,
                                                                        final Activity activity,
                                                                        final PluginRegistry.Registrar registrar,
                                                                        final ActivityPluginBinding activityBinding)
                                                                        {


                                                                            this.activity = (FlutterActivity) activity;
                                                                            eventChannel =
                                                                                    new EventChannel(messenger, "flutter_barcode_scanner_receiver");
                                                                            eventChannel.setStreamHandler(this);


                                                                            this.applicationContext = applicationContext;
                                                                            channel = new MethodChannel(messenger, CHANNEL);
                                                                            channel.setMethodCallHandler(this);
                                                                            if (registrar != null) {
                                                                                // V1 embedding setup for activity listeners.
                                                                                observer = new LifeCycleObserver(activity);
                                                                                applicationContext.registerActivityLifecycleCallbacks(
                                                                                        observer); // Use getApplicationContext() to avoid casting failures.
                                                                                registrar.addActivityResultListener(this);
                                                                            } else {
                                                                                // V2 embedding setup for activity listeners.
                                                                                activityBinding.addActivityResultListener(this);
                                                                                lifecycle = FlutterLifecycleAdapter.getActivityLifecycle(activityBinding);
                                                                                observer = new LifeCycleObserver(activity);
                                                                                lifecycle.addObserver(observer);
                                                                            }
                                                                        }

                                                                        @Override
                                                                        public void onAttachedToActivity
                                                                        (ActivityPluginBinding
                                                                        binding){
                                                                            activityBinding = binding;
                                                                            createPluginSetup(
                                                                                    pluginBinding.getBinaryMessenger(),
                                                                                    (Application) pluginBinding.getApplicationContext(),
                                                                                    activityBinding.getActivity(),
                                                                                    null,
                                                                                    activityBinding);
                                                                        }

                                                                        @Override
                                                                        public void onDetachedFromActivity
                                                                        () {
                                                                            clearPluginSetup();
                                                                        }

                                                                        /**
                                                                         * Clear plugin setup
                                                                         */
                                                                        private void clearPluginSetup
                                                                        () {
                                                                            activity = null;
                                                                            activityBinding.removeActivityResultListener(this);
                                                                            activityBinding = null;
                                                                            lifecycle.removeObserver(observer);
                                                                            lifecycle = null;
                                                                            channel.setMethodCallHandler(null);
                                                                            eventChannel.setStreamHandler(null);
                                                                            channel = null;
                                                                            applicationContext.unregisterActivityLifecycleCallbacks(observer);
                                                                            applicationContext = null;
                                                                        }

                                                                        /**
                                                                         * Activity lifecycle observer
                                                                         */
                                                                        private class LifeCycleObserver
                                                                                implements Application.ActivityLifecycleCallbacks, DefaultLifecycleObserver {
                                                                            private final Activity thisActivity;

                                                                            LifeCycleObserver(Activity activity) {
                                                                                this.thisActivity = activity;
                                                                            }

                                                                            @Override
                                                                            public void onCreate(@NonNull LifecycleOwner owner) {
                                                                            }

                                                                            @Override
                                                                            public void onStart(@NonNull LifecycleOwner owner) {
                                                                            }

                                                                            @Override
                                                                            public void onResume(@NonNull LifecycleOwner owner) {
                                                                            }

                                                                            @Override
                                                                            public void onPause(@NonNull LifecycleOwner owner) {
                                                                            }

                                                                            @Override
                                                                            public void onStop(@NonNull LifecycleOwner owner) {
                                                                                onActivityStopped(thisActivity);
                                                                            }

                                                                            @Override
                                                                            public void onDestroy(@NonNull LifecycleOwner owner) {
                                                                                onActivityDestroyed(thisActivity);
                                                                            }

                                                                            @Override
                                                                            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                                                                            }

                                                                            @Override
                                                                            public void onActivityStarted(Activity activity) {
                                                                            }

                                                                            @Override
                                                                            public void onActivityResumed(Activity activity) {
                                                                            }

                                                                            @Override
                                                                            public void onActivityPaused(Activity activity) {
                                                                            }

                                                                            @Override
                                                                            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
                                                                            }

                                                                            @Override
                                                                            public void onActivityDestroyed(Activity activity) {
                                                                                if (thisActivity == activity && activity.getApplicationContext() != null) {
                                                                                    ((Application) activity.getApplicationContext())
                                                                                            .unregisterActivityLifecycleCallbacks(
                                                                                                    this);
                                                                                }
                                                                            }

                                                                            @Override
                                                                            public void onActivityStopped(Activity activity) {

                                                                            }
                                                                        }
                                                                    }