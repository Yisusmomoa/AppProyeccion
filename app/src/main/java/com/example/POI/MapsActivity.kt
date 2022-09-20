package com.example.POI

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.core.app.ActivityCompat

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

import java.util.*

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var btnUsarUbicacion: Button
    private lateinit var geocoder: Geocoder
    private lateinit var coordenadas: LatLng

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        btnUsarUbicacion = findViewById(R.id.btnUsarUbicacion)
        btnUsarUbicacion.setOnClickListener {

            traducirCoordenadas()
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        activarMiUbicacion()

        mMap.setOnMapClickListener { coordenadas ->

            this.coordenadas = coordenadas
            mMap.clear()
            mMap.addMarker(MarkerOptions().position(coordenadas))
            btnUsarUbicacion.isEnabled = true
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        setResult(RESULT_CANCELED)
    }

    private fun traducirCoordenadas() {

        geocoder = Geocoder(this, Locale.getDefault())

        Thread {

            val direcciones = geocoder.getFromLocation(coordenadas.latitude, coordenadas.longitude, 1)
            if (direcciones.size > 0) {

                val direccion = direcciones[0].getAddressLine(0)

                setResult(RESULT_OK, Intent().putExtra("ubicacion", direccion))
                finish()
            } else {
                finish()
            }
        }.start()
    }

    private fun activarMiUbicacion() {

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {

            mMap.isMyLocationEnabled = true
        }
    }
}