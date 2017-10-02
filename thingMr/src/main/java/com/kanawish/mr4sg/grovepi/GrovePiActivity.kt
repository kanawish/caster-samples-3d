package com.kanawish.mr4sg.grovepi

import android.app.Activity
import android.os.Bundle
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import timber.log.Timber
import java.io.IOException
import java.util.concurrent.TimeUnit

// https://github.com/DexterInd/GrovePi/blob/master/Software/Java/src/com/dexterind/grovepi/Board.java
// https://github.com/DexterInd/GrovePi/blob/master/Software/Java/src/com/dexterind/grovepi/Grovepi.java
// https://www.dexterindustries.com/GrovePi/engineering/software-architecture/
// https://www.dexterindustries.com/GrovePi/programming/grovepi-protocol-adding-custom-sensors/
class GrovePiActivity : Activity() {

    private val disposable = CompositeDisposable()

    lateinit var grovePi: GrovePiManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.d("onCreate()")
    }

    override fun onResume() {
        super.onResume()

        grovePi = GrovePiManager()

        grovePi.pinMode(2, PinMode.OUTPUT)
        grovePi.pinMode(3, PinMode.INPUT)
        grovePi.pinMode(0, PinMode.INPUT)

        Observable.interval(1000, TimeUnit.MILLISECONDS)
                .map { it % 2 }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { i ->
                            Timber.d("d3 read: ${grovePi.testDRead(3)}")
                            Timber.d( "a0 read: ${grovePi.testARead(0)}")

                            try {
                                Timber.d("d2 write: $i ")
                                grovePi.digitalWrite(2, i.toByte())
                            } catch (e:IOException){Timber.e(e)}
                        },
                        { e -> Timber.d(e, "Caught an error.") }
                )
    }

    override fun onPause() {
        super.onPause()

        grovePi.device.close()
        disposable.dispose()
    }

}