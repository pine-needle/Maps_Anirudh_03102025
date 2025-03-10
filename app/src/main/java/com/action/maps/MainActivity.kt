package com.action.maps

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mGoogleMap:GoogleMap
    private val LOCATION_PERMISSION_REQUEST = 1
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val mapFragment =
            supportFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)

        val mapOptionButton:ImageButton = findViewById(R.id.mapOptionsMenu)
        val popupMenu = PopupMenu(this, mapOptionButton)
        popupMenu.menuInflater.inflate(R.menu.map_options,popupMenu.menu)
        popupMenu.setOnMenuItemClickListener { menuItem->
            changeMap(menuItem.itemId)
            true

        }

        mapOptionButton.setOnClickListener{
            popupMenu.show()
        }

    }

    private fun changeMap(itemId: Int) {
        when(itemId)
        {
            R.id.normal_map-> mGoogleMap.mapType = GoogleMap.MAP_TYPE_NORMAL
            R.id.hybrid_map-> mGoogleMap.mapType = GoogleMap.MAP_TYPE_HYBRID
            R.id.satellite_map-> mGoogleMap.mapType = GoogleMap.MAP_TYPE_SATELLITE
            R.id.terrain_map-> mGoogleMap.mapType = GoogleMap.MAP_TYPE_TERRAIN
        }

    }


    override fun onMapReady(googleMap: GoogleMap) {
        mGoogleMap = googleMap
        getLocationAccess()
        val latlng = LatLng(33.7490, -84.3880) // Atlanta, GA coordinates
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(latlng))
        mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(5f)) //  Zoom in for better view

        val bitmap = getBitmapFromVectorDrawable(this, R.drawable.baseline_add_location_alt_24)
        val icon = bitmap?.let { BitmapDescriptorFactory.fromBitmap(it) }

        mGoogleMap.addMarker(
            MarkerOptions()
                .position(latlng)
                .title("Atlanta, GA")
                .snippet("The capital of Georgia")
                .icon(icon)
        )
    }

    private fun getBitmapFromVectorDrawable(context: Context, drawableId: Int): Bitmap? {
        val drawable = ContextCompat.getDrawable(context, drawableId) ?: return null
        val bitmap = Bitmap.createBitmap(
            drawable.intrinsicWidth,
            drawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }

    private fun getLocationAccess(){
        if (ContextCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED) {
            mGoogleMap.isMyLocationEnabled = true
        }
        else
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),LOCATION_PERMISSION_REQUEST)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
        deviceId: Int
    ) {
        if (requestCode == LOCATION_PERMISSION_REQUEST){
            if (grantResults.contains(PackageManager.PERMISSION_GRANTED)){
                if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return
                }
                mGoogleMap.isMyLocationEnabled = true
            }
            else{
                Toast.makeText(this, "User not granted location access permission",Toast.LENGTH_LONG).show()
                finish()
            }
        }
    }
}
