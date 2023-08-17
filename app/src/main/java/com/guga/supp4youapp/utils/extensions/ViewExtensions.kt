//package com.guga.supp4youapp.utils.extensions
//
//import android.content.Context
//import android.view.animation.AnimationUtils
//import androidx.appcompat.widget.AppCompatButton
//import com.android.volley.toolbox.ImageRequest
//import com.guga.supp4youapp.R
//import vibrate
//
//
//fun AppCompatButton.shake(context: Context) {
//    startAnimation(AnimationUtils.loadAnimation(context, R.anim.shake))
//    context.vibrate()
//}
//
//fun ImageRequest.Builder.applyPOVFilters(context: Context, date: Long? = null): ImageRequest.Builder {
//    val appliedTransformations = buildList {
//        add(BlurTransformation(context, radius = 0.68f))
//        add(VignetteFilterTransformation(context, end = 0.90f))
//        date?.also { add(DateTransformation(context, it)) }
//    }
//    return transformations(appliedTransformations)
//}