
import android.content.Context
import android.content.Context.VIBRATOR_SERVICE
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.*
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.graphics.drawable.toBitmapOrNull
import com.google.android.instantapps.InstantApps
import com.guga.supp4youapp.R
import com.guga.supp4youapp.utils.Constants
import java.io.File
import java.io.FileOutputStream


private typealias AreGranted = Boolean

fun Context.checkPermissions(permissions: List<String>): AreGranted {
    return permissions.map { permission ->
        packageManager.checkPermission(permission, packageName)
    }.all { result ->
        result == PackageManager.PERMISSION_GRANTED
    }
}

fun Context.checkPermission(permission: String): AreGranted {
    val isPermissionGrantedResult = packageManager.checkPermission(permission, packageName)
    return isPermissionGrantedResult == PackageManager.PERMISSION_GRANTED
}

fun Context.getAppFileDir(): File {
    val cacheDir = externalCacheDir?.let { dir ->
        File(dir, getString(R.string.app_name)).apply {
            mkdir()
        }
    }

    return cacheDir?.takeIf { it.exists() } ?: filesDir
}

@ColorInt
fun Context.getColorRes(@ColorRes colorId: Int): Int {
    return ContextCompat.getColor(this, colorId)
}

fun Context.getDrawableRes(@DrawableRes drawableId: Int): Drawable? {
    return ContextCompat.getDrawable(this, drawableId)
}

fun Context.vibrate() {
    val durationMillis = 400L
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager = getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
        val vibrator = vibratorManager.defaultVibrator
        val effect = VibrationEffect.createOneShot(
            durationMillis,
            VibrationEffect.DEFAULT_AMPLITUDE
        )
        vibrator.vibrate(effect)
    } else {
        val vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator
        vibrator.vibrate(durationMillis)
    }
}

fun Context.isInstantApp(): Boolean = InstantApps.isInstantApp(this)

fun Context.shareImageFile(imageBitmap: Bitmap) {
    val sharedFile = createCacheFileFromBitmap(imageBitmap)

    val contentUri: Uri? = sharedFile?.let {
        FileProvider.getUriForFile(this, Constants.AUTHORITY, sharedFile)
    }

    if (contentUri != null) {
        val shareIntent = Intent()
            .setAction(Intent.ACTION_SEND)
            .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            .setDataAndType(contentUri, contentResolver.getType(contentUri))
            .putExtra(Intent.EXTRA_STREAM, contentUri)

        startActivity(Intent.createChooser(shareIntent, "Share Image"))
    }
}

fun Context.createCacheFileFromBitmap(bitmap: Bitmap): File? {
    return runCatching {
        val cachePath = File(cacheDir, "images").apply { mkdirs() }
        val cacheFile = File(cachePath, "shared_image.jpeg")
        val stream = FileOutputStream(cacheFile)

        val compressQuality = 100
        bitmap.compress(Bitmap.CompressFormat.PNG, compressQuality, stream)
        stream.close()

        cacheFile
    }.onFailure {

    }.getOrNull()
}

//suspend fun Context.loadImageAsync(imageFile: ListItem.FileItem): Bitmap? {
//    val request = ImageRequest.Builder(this)
//        .data(imageFile.imageURL)
//        .applyPOVFilters(this, imageFile.dateTimestamp)
//        .build()
//
//    return ImageLoader(this).execute(request)
//        .drawable
//        ?.toBitmapOrNull()
//}
