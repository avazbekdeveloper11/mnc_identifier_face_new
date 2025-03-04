package id.mncinnovation.mnc_identifier_face

import android.app.Activity
import android.app.Activity.RESULT_OK
import androidx.annotation.NonNull
import android.content.Context
import android.content.Intent
import android.util.Log
import id.mncinnovation.face_detection.MNCIdentifier
import id.mncinnovation.face_detection.analyzer.DetectionMode
import com.google.gson.Gson
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.PluginRegistry
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result

/** MncIdentifierFacePlugin */
class MncIdentifierFacePlugin : FlutterPlugin, MethodCallHandler, ActivityAware, PluginRegistry.ActivityResultListener {
  /// The MethodChannel that will the communication between Flutter and native Android
  private lateinit var channel: MethodChannel
  private lateinit var activity: Activity
  private lateinit var context: Context
  private lateinit var result: Result

  override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
    channel = MethodChannel(flutterPluginBinding.binaryMessenger, "mnc_identifier_face")
    channel.setMethodCallHandler(this)
    context = flutterPluginBinding.applicationContext
  }

  companion object {
    const val LIVENESS_DETECTION_REQUEST_CODE = 666
  }

  override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {
    this.result = result
    if (call.method == "startLivenessDetection") {
      // Set 0 to disable low memory allocation warning popup
      val lowMemoryThreshold = 0
      MNCIdentifier.setLowMemoryThreshold(lowMemoryThreshold)
      activity.startActivityForResult(MNCIdentifier.getLivenessIntent(context), LIVENESS_DETECTION_REQUEST_CODE)
    } else {
      result.notImplemented()
    }
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?): Boolean {
    if (resultCode == RESULT_OK) {
      when (requestCode) {
        LIVENESS_DETECTION_REQUEST_CODE -> {
          // Get liveness result
          val livenessResult = MNCIdentifier.getLivenessResult(data)
          if (livenessResult == null) {
            result.error("Null Result", "Mnc-identifier-face: Liveness result is null", null)
            return true
          }

          if (livenessResult.isSuccess) {  // Check if liveness detection success
            // Get image result
            val bitmap = livenessResult.getBitmap(context, DetectionMode.SMILE, onError = { message ->
              result.error("Failed", "Mnc-identifier-face: $message", null)
            })

            if (bitmap == null) {
              result.error("Bitmap Error", "Mnc-identifier-face: Bitmap is null", null)
              return true
            }

            val gson = Gson()
            result.success(gson.toJson(livenessResult))
          } else {  // Liveness Detection Error
            result.error("Invalid request code", "Mnc-identifier-face: Received request code: $requestCode", "Expected request code: ${LIVENESS_DETECTION_REQUEST_CODE}_REQUEST_CODE")
          }
        }
      }
      return true
    }
    return false
  }

  override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
    channel.setMethodCallHandler(null)
  }

  override fun onAttachedToActivity(binding: ActivityPluginBinding) {
    activity = binding.activity
    binding.addActivityResultListener(this)
  }

  override fun onDetachedFromActivityForConfigChanges() {
    // Not yet implemented
  }

  override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
    // Not yet implemented
  }

  override fun onDetachedFromActivity() {
    // Not yet implemented
  }
}