package com.example.presentation.ui.activities.gateways

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.example.presentation.ui.interfaces.DialogFragmentGateway
import com.example.presentation.ui.interfaces.FragmentStackGateway
import com.example.presentation.ui.interfaces.PermissionGateway
import java.lang.ref.WeakReference

class PermissionGatewayImpl(
    activity: ComponentActivity,
    private val fragmentStackGateway: FragmentStackGateway,
    private val dialogFragmentGateway: DialogFragmentGateway
) : PermissionGateway {
    private val activityReference = WeakReference(activity)

    private var onRequestLocation: ((Boolean) -> Unit)? = null
    private var onRequestContactSuccess: (() -> Unit)? = null

    private val accessLocationLauncher = activity.registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        onRequestLocation?.invoke(it)
    }

    private val readContactsLauncher = activity.registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            onRequestContactSuccess?.invoke()
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            handleReadContactsRequestRejection()
        }
    }

    override fun checkLocationPermission() = safeActivityContext {
        ContextCompat.checkSelfPermission(
            applicationContext,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun checkContactPermission() = safeActivityContext {
        ContextCompat.checkSelfPermission(
            applicationContext,
            Manifest.permission.READ_CONTACTS
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun requestLocationPermission(handler: (Boolean) -> Unit) {
        if (checkLocationPermission()) {
            handler(true)
        } else {
            onRequestLocation = handler
            accessLocationLauncher.launch(Manifest.permission.ACCESS_COARSE_LOCATION)
        }
    }

    override fun requestContactPermission(onSuccess: () -> Unit) {
        if (checkContactPermission()) {
            onSuccess()
        } else {
            onRequestContactSuccess = onSuccess
            readContactsLauncher.launch(Manifest.permission.READ_CONTACTS)
        }
    }

    override fun onRequestDialogDismissed() {
        readContactsLauncher.launch(Manifest.permission.READ_CONTACTS)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun handleReadContactsRequestRejection() = safeActivityContext {
        if (shouldShowRequestPermissionRationale(Manifest.permission.READ_CONTACTS)) {
            dialogFragmentGateway.requestPermission()
        } else {
            fragmentStackGateway.pleadForReadContactsPermission()
        }
    }

    private inline fun <T> safeActivityContext(block: ComponentActivity.() -> T): T {
        val dereferencedActivity = activityReference.get() ?: throw IllegalStateException(
            "${this::class.simpleName} must have a valid activity reference"
        )

        return block(dereferencedActivity)
    }
}
