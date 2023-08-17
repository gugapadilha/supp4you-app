
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.provider.Settings
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.guga.supp4youapp.BuildConfig
import com.guga.supp4youapp.R


// The code here is deprecated but we found no problem in our use case
fun AppCompatActivity.setStatusBarTransparent() {
    window.statusBarColor = Color.TRANSPARENT
    window.decorView.systemUiVisibility =
        (View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
}

// The code here is deprecated but we found no problem in our use case
fun AppCompatActivity.setStatusBarBlack() {
    window.statusBarColor = Color.BLACK
    window.decorView.systemUiVisibility =
        (View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
}

fun AppCompatActivity.openHelpChat() {
    val url = Uri.parse("https://jivo.chat/BZQlFuyXDI")
    val browserIntent = Intent(Intent.ACTION_VIEW, url)

    startActivity(browserIntent)
}

fun AppCompatActivity.openAppPlayStorePage() {
    val url = Uri.parse("market://details?id=${BuildConfig.APPLICATION_ID}")
    val storeIntent = Intent(Intent.ACTION_VIEW, url)

    startActivity(storeIntent)
}

fun AppCompatActivity.openAppSettingsPage() {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
        data = Uri.fromParts("package", packageName, null)
    }

    startActivity(intent)
}

fun AppCompatActivity.showConfirmExitDialog(): AlertDialog {
    return AlertDialog.Builder(this)
        .setTitle(R.string.confirm_exit_dialog_title)
        .setMessage(R.string.confirm_exit_dialog_message)
        .setPositiveButton(
            R.string.confirm_exit_dialog_positive_button
        ) { _, _ -> finishAndRemoveTask() }
        .setNegativeButton(
            R.string.confirm_exit_dialog_negative_button
        ) { dialog, _ -> dialog.dismiss() }
        .show()
}

fun AppCompatActivity.showRationaleCameraDialog(): AlertDialog {
    return AlertDialog.Builder(this)
        .setTitle(R.string.rationale_camera_title)
        .setMessage(R.string.rationale_camera_message)
        .setPositiveButton(
            R.string.rationale_positive_button
        ) { dialog, _ ->
            openAppSettingsPage()
            dialog.dismiss()
        }
        .setNegativeButton(
            R.string.rationale_negative_button
        ) { dialog, _ ->
            dialog.dismiss()
            finishAndRemoveTask()
        }
        .show()
}