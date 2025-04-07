package com.amolg.flutterbarcodescanner;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;

import java.util.Map;

import io.flutter.embedding.android.FlutterActivity;
import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.embedding.engine.plugins.FlutterPluginBinding;
import io.flutter.plugin.common.EventChannel;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry.ActivityResultListener;
import io.flutter.plugin.common.EventChannel.StreamHandler;

/**
 * FlutterBarcodeScannerPlugin
 */
public class FlutterBarcodeScannerPlugin implements MethodCallHandler, ActivityResultListener, StreamHandler, FlutterPlugin, ActivityAware {
    private static final String CHANNEL = "flutter_barcode_scanner";

    private static FlutterActivity activity;
    private static Result pendingResult;
    private Map<String, Object> arguments;

    private static final String TAG = FlutterBarcodeScannerPlugin.class.getSimpleName();
    private static final int RC_BARCODE_CAPTURE = 9001;
    public static String lineColor = "";
    public static boolean isShowFlashIcon = false;
    public static boolean isContinuousScan = false;
    static EventChannel.EventSink barcodeStream;
    private EventChannel eventChannel;
    private MethodChannel channel;
    private ActivityPluginBinding activityBinding;
    private Application applicationContext;
    private FlutterPluginBinding pluginBinding;

    @Override
    public void onAttachedToEngine(FlutterPluginBinding binding) {
        applicationContext = (Application) binding.getApplicationContext();
        pluginBinding = binding;
    }

    @Override
    public void onDetachedFromEngine(FlutterPluginBinding binding) {
        applicationContext = null;
        pluginBinding = null;
    }

    @Override
    public void onAttachedToActivity(ActivityPluginBinding binding) {
        activityBinding = binding;
        activity = (FlutterActivity) binding.getActivity();
        channel = new MethodChannel(pluginBinding.getBinaryMessenger(), CHANNEL);
        channel.setMethodCallHandler(this);
        eventChannel = new EventChannel(pluginBinding.getBinaryMessenger(), "flutter_barcode_scanner_receiver");
        eventChannel.setStreamHandler(this);
        binding.addActivityResultListener(this);
    }

    @Override
    public void onDetachedFromActivity() {
        if (activityBinding != null) {
            activityBinding.removeActivityResultListener(this);
            activityBinding = null;
        }
        if (channel != null) {
            channel.setMethodCallHandler(null);
            channel = null;
        }
        if (eventChannel != null) {
            eventChannel.setStreamHandler(null);
            eventChannel = null;
        }
        activity = null;
    }

    @Override
    public void onDetachedFromActivityForConfigChanges() {
        onDetachedFromActivity();
    }

    @Override
    public void onReattachedToActivityForConfigChanges(ActivityPluginBinding binding) {
        onAttachedToActivity(binding);
    }

    @Override
    public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
        try {
            pendingResult = result;

            if (call.method.equals("scanBarcode")) {
                if (!(call.arguments instanceof Map)) {
                    throw new IllegalArgumentException("Plugin not passing a map as parameter: " + call.arguments);
                }
                arguments = (Map<String, Object>) call.arguments;
                lineColor = (String) arguments.get("lineColor");
                isShowFlashIcon = (boolean) arguments.get("isShowFlashIcon");
                if (null == lineColor || lineColor.equalsIgnoreCase("")) {
                    lineColor = "#DC143C";
                }
                if (null != arguments.get("scanMode")) {
                    if ((int) arguments.get("scanMode") == BarcodeCaptureActivity.SCAN_MODE_ENUM.DEFAULT.ordinal()) {
                        BarcodeCaptureActivity.SCAN_MODE = BarcodeCaptureActivity.SCAN_MODE_ENUM.QR.ordinal();
                    } else {
                        BarcodeCaptureActivity.SCAN_MODE = (int) arguments.get("scanMode");
                    }
                } else {
                    BarcodeCaptureActivity.SCAN_MODE = BarcodeCaptureActivity.SCAN_MODE_ENUM.QR.ordinal();
                }

                isContinuousScan = (boolean) arguments.get("isContinuousScan");

                startBarcodeScannerActivityView((String) arguments.get("cancelButtonText"), isContinuousScan);
            }
        } catch (Exception e) {
            Log.e(TAG, "onMethodCall: " + e.getLocalizedMessage());
        }
    }

    private void startBarcodeScannerActivityView(String buttonText, boolean isContinuousScan) {
        try {
            Intent intent = new Intent(activity, BarcodeCaptureActivity.class).putExtra("cancelButtonText", buttonText);
            if (isContinuousScan) {
                activity.startActivity(intent);
            } else {
                activity.startActivityForResult(intent, RC_BARCODE_CAPTURE);
            }
        } catch (Exception e) {
            Log.e(TAG, "startView: " + e.getLocalizedMessage());
        }
    }

    @Override
    public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_BARCODE_CAPTURE) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    try {
                        Barcode barcode = data.getParcelableExtra(BarcodeCaptureActivity.BarcodeObject);
                        String barcodeResult = barcode.rawValue;
                        pendingResult.success(barcodeResult);
                    } catch (Exception e) {
                        pendingResult.success("-1");
                    }
                } else {
                    pendingResult.success("-1");
                }
                pendingResult = null;
                arguments = null;
                return true;
            } else {
                pendingResult.success("-1");
            }
        }
        return false;
    }

    @Override
    public void onListen(Object o, EventChannel.EventSink eventSink) {
        try {
            barcodeStream = eventSink;
        } catch (Exception e) {
        }
    }

    @Override
    public void onCancel(Object o) {
        try {
            barcodeStream = null;
        } catch (Exception e) {
        }
    }

    public static void onBarcodeScanReceiver(final Barcode barcode) {
        try {
            if (barcode != null && !barcode.displayValue.isEmpty()) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        barcodeStream.success(barcode.rawValue);
                    }
                });
            }
        } catch (Exception e) {
            Log.e(TAG, "onBarcodeScanReceiver: " + e.getLocalizedMessage());
        }
    }
}