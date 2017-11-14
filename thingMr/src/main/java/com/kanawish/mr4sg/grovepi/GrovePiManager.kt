package com.kanawish.mr4sg.grovepi

import com.google.android.things.pio.I2cDevice
import com.google.android.things.pio.PeripheralManagerService
import io.reactivex.Completable
import io.reactivex.Single
import timber.log.Timber
import java.io.IOException
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class GrovePiManager @Inject constructor() {

    val device: I2cDevice

    init {
        val manager = PeripheralManagerService()
        device = manager.openI2cDevice(manager.i2cBusList[0], GROVEPI_ADDRESS.toInt())
    }

    private fun ByteArray.write() = device.writeRegBuffer(GROVEPI_ADDRESS.toInt(), this, this.size)

    fun pinMode(pin: Byte, mode: PinMode) =
            byteArrayOf(PIN_MODE_CMD, pin, mode.code, UNUSED).write()

    fun testDRead(pin: Byte): Byte {
        try {
            device.writeRegBuffer(GROVEPI_ADDRESS.toInt(), byteArrayOf(DIGITAL_READ_CMD, pin, UNUSED, UNUSED), 4)
            //        byteArrayOf(DIGITAL_READ_CMD, pin, UNUSED, UNUSED).write()
            //        Thread.sleep(250)
            return device.readRegByte(GROVEPI_ADDRESS.toInt())
        } catch (e: IOException) {
            Timber.e(e)
        }

        return -1
    }

    fun testARead(pin: Byte): Int {
        try {
            device.writeRegBuffer(GROVEPI_ADDRESS.toInt(), byteArrayOf(ANALOG_READ_CMD, pin, UNUSED, UNUSED), 4)
            //        byteArrayOf(DIGITAL_READ_CMD, pin, UNUSED, UNUSED).write()
            //        Thread.sleep(250)
            val buffSize = 4
            val byteArray = ByteArray(buffSize)
            device.readRegBuffer(GROVEPI_ADDRESS.toInt(), byteArray, buffSize)
            return byteArray[2] + byteArray[1].toInt().shl(8)
        } catch (e: IOException) {
            Timber.e(e)
        }

        return -1
    }

    fun digitalRead(pin: Byte): Single<Byte> = Single.create<Byte> { e ->
        byteArrayOf(DIGITAL_READ_CMD, pin, UNUSED, UNUSED).write()
        e.onSuccess(device.readRegByte(GROVEPI_ADDRESS.toInt()))
/*
        val disposable = Completable.timer(100, TimeUnit.MILLISECONDS).subscribe {
            e.onSuccess(device.readRegByte(GROVEPI_ADDRESS.toInt()))
        }
        e.setCancellable { disposable.dispose() }
*/
    }

    fun digitalWrite(pin: Byte, value: Byte) {
        byteArrayOf(DIGITAL_WRITE_CMD, pin, value, UNUSED).write()
    }

    fun analogRead(pin: Byte): Single<ByteArray> = Single.create<ByteArray> { e ->
        byteArrayOf(ANALOG_READ_CMD, pin, UNUSED, UNUSED).write()

        val disposable = Completable.timer(100, TimeUnit.MILLISECONDS).subscribe {
            val results = kotlin.ByteArray(4)
            device.readRegBuffer(GROVEPI_ADDRESS.toInt(), results, results.size)
            e.onSuccess(results)
        }
        e.setCancellable { disposable.dispose() }
    }

    fun analogWrite(pin: Byte, value: Byte) {
        byteArrayOf(ANALOG_WRITE_CMD, pin, value, UNUSED).write()
    }

    companion object {

        /**
         * BASE COMMANDS
         */
        const val GROVEPI_ADDRESS: Byte = 4

        const val UNUSED: Byte = 0

        const val PIN_MODE_CMD: Byte = 5

        const val DIGITAL_READ_CMD: Byte = 1
        const val DIGITAL_WRITE_CMD: Byte = 2

        const val ANALOG_READ_CMD: Byte = 3
        const val ANALOG_WRITE_CMD: Byte = 4

        /**
         * VARIOUS COMMANDS
         */
        const val ULTRASONIC_READ_CMD: Byte = 7

        const val FIRMWARE_VERSION_CMD: Byte = 8

        // Accelerometer (+/- 1.5g) read
        const val ACCELEROMETER_XYZ_CMD: Byte = 20

        const val RTC_GET_TIME_CMD: Byte = 30

        // DHT Pro sensor temperature
        const val DHT_TEMP_CMD: Byte = 40

        // TODO: More command codes to be added...
    }

}

