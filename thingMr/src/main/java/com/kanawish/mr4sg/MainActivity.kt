package com.kanawish.mr4sg

import android.app.Activity
import android.os.Bundle
import android.view.KeyEvent
import com.google.android.things.contrib.driver.button.Button
import com.google.android.things.contrib.driver.button.ButtonInputDriver
import com.google.android.things.pio.Gpio
import com.google.android.things.pio.PeripheralManagerService
import com.google.android.things.pio.SpiDevice
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import timber.log.Timber
import java.io.IOException
import java.nio.charset.Charset


const val SET_MOTOR_POWER = 21

class MainActivity : Activity() {

    val manager by lazy {
        PeripheralManagerService()
    }

    val blueLED by lazy {
        manager.openGpio(gpioPin(13)).apply {
            setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW)
        }
    }

    val buttonDrive by lazy {
        ButtonInputDriver(gpioPin(15), Button.LogicState.PRESSED_WHEN_HIGH, KeyEvent.KEYCODE_SPACE).apply {
            register()
        }
    }

    val spiDev0 by lazy {
        // TBD: Any reasons to prefer .0 over .1?
        manager.openSpiDevice("SPI3.0").apply {
            setMode(SpiDevice.MODE0)
            setFrequency(500000)
            setBitsPerWord(8)
            setBitJustification(false)
        }
    }

    val spiDev1 by lazy {
        // TBD: Any reasons to prefer .0 over .1?
        manager.openSpiDevice("SPI3.1").apply {
            setMode(SpiDevice.MODE0)
            setFrequency(500000)
            setBitsPerWord(8)
            setBitJustification(false)
        }
    }

    val keyRelay = PublishRelay.create<Pair<Int, KeyEvent>>()

    val disposables = CompositeDisposable()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Timber.d("${manager.gpioList}")

        Timber.d("${manager.pwmList}")
        Timber.d("${manager.i2cBusList}")
        Timber.d("${manager.i2sDeviceList}")
        Timber.d("${manager.spiBusList}")
        Timber.d("${manager.uartDeviceList}")

        manager.gpioList.forEach {
            Timber.d("reset $it")
            var gpio:Gpio? = null
            try {
                 gpio = manager.openGpio(it).apply {
                    setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW)
                    setActiveType(Gpio.ACTIVE_HIGH)
                    value = false
                }
            } catch (e: IOException) {
                Timber.d(e, "failed to open $it")
            } finally {
                gpio?.apply {
                    safeClose(this::close,"attempting to close $it")
                }
            }
        }

/*
        byteArrayOf(0x01, 0x06, 80).also {
            Timber.d("SPI LED Test")
            spiDev0.write(it, it.size)
            spiDev1.write(it, it.size)
        }
*/

        Timber.d("init $blueLED")
        Timber.d("init $buttonDrive")

    }

    override fun onResume() {
        super.onResume()

        disposables += keyRelay
                .filter { (code, _) -> code == KeyEvent.KEYCODE_SPACE }
                .map { (_, event) -> event.action == KeyEvent.ACTION_DOWN }
                .doOnNext { b -> Timber.d("blueLED: $b") }
                .subscribe {
                    blueLED.value = it
                }

        queryManufacturer(spiDev0)
        queryManufacturer(spiDev1)

    }

    override fun onPause() {
        super.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()

        buttonDrive.unregister()
        safeClose(buttonDrive::close, "on buttonDrive.close()")
        safeClose(blueLED::close, "on blueLED.close()")
        safeClose(spiDev0::close, "on spiDev0.close()")
        safeClose(spiDev1::close, "on spiDev1.close()")
    }

    fun safeClose(close: () -> Unit, errMsg: String) {
        try {
            close()
        } catch (e: IOException) {
            Timber.d(e, errMsg)
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        keyRelay.accept(keyCode to event)
        return super.onKeyDown(keyCode, event)
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent): Boolean {
        keyRelay.accept(keyCode to event)
        return super.onKeyUp(keyCode, event)
    }
}

fun queryManufacturer(bus:SpiDevice) {
    byteArrayOf(0x01, BpSpiMessageType.GET_MANUFACTURER.code()).apply {
        bus.write(this, this.size)
    }

    ByteArray(20).apply {
        bus.read(this,this.size)
        Timber.d(toString(Charset.defaultCharset()))
    }
}

const val PORT_1 = 0x01
const val PORT_2 = 0x02
const val PORT_3 = 0x04
const val PORT_4 = 0x08

const val PORT_A = 0x01
const val PORT_B = 0x02
const val PORT_C = 0x04
const val PORT_D = 0x08

const val MOTOR_FLOAT = -128

enum class BpSpiMessageType {
    NONE,

    GET_MANUFACTURER,
    GET_NAME,
    GET_HARDWARE_VERSION,
    GET_FIRMWARE_VERSION,
    GET_ID,
    SET_LED,
    GET_VOLTAGE_3V3,
    GET_VOLTAGE_5V,
    GET_VOLTAGE_9V,
    GET_VOLTAGE_VCC,
    SET_ADDRESS,

    SET_SENSOR_TYPE,

    GET_SENSOR_1,
    GET_SENSOR_2,
    GET_SENSOR_3,
    GET_SENSOR_4,

    I2C_TRANSACT_1,
    I2C_TRANSACT_2,
    I2C_TRANSACT_3,
    I2C_TRANSACT_4,

    SET_MOTOR_POWER,

    SET_MOTOR_POSITION,

    SET_MOTOR_POSITION_KP,

    SET_MOTOR_POSITION_KD,

    SET_MOTOR_DPS,

    SET_MOTOR_DPS_KP,

    SET_MOTOR_DPS_KD,

    SET_MOTOR_LIMITS,

    OFFSET_MOTOR_ENCODER,

    GET_MOTOR_A_ENCODER,
    GET_MOTOR_B_ENCODER,
    GET_MOTOR_C_ENCODER,
    GET_MOTOR_D_ENCODER,

    GET_MOTOR_A_STATUS,
    GET_MOTOR_B_STATUS,
    GET_MOTOR_C_STATUS,
    GET_MOTOR_D_STATUS ;

    fun code() = ordinal.toByte()
}

enum class SensorType {
    NONE,
    ONE,
    I2C,
    CUSTOM,

    TOUCH,
    NXT_TOUCH,
    EV3_TOUCH,

    NXT_LIGHT_ON,
    NXT_LIGHT_OFF,

    NXT_COLOR_RED,
    NXT_COLOR_GREEN,
    NXT_COLOR_BLUE,
    NXT_COLOR_FULL,
    NXT_COLOR_OFF,

    NXT_ULTRASONIC,

    EV3_GYRO_ABS,
    EV3_GYRO_DPS,
    EV3_GYRO_ABS_DPS,

    EV3_COLOR_REFLECTED,
    EV3_COLOR_AMBIENT,
    EV3_COLOR_COLOR,
    EV3_COLOR_RAW_REFLECTED,
    EV3_COLOR_COLOR_COMPONENTS,

    EV3_ULTRASONIC_CM,
    EV3_ULTRASONIC_INCHES,
    EV3_ULTRASONIC_LISTEN,

    EV3_INFRARED_PROXIMITY,
    EV3_INFRARED_SEEK,
    EV3_INFRARED_REMOTE
}

enum class SensorState {
    VALID_DATA,
    NOT_CONFIGURED,
    CONFIGURING,
    NO_DATA,
    I2C_ERROR
}

// Sensor type custom stuff
const val SC_PIN1_9V = 0x0002
const val SC_PIN5_OUT = 0x0010
const val SC_PIN5_STATE = 0x0020
const val SC_PIN6_OUT = 0x0100
const val SC_PIN6_STATE = 0x0200
const val SC_PIN1_ADC = 0x1000
const val SC_PIN6_ADC = 0x4000

// I2C stuff
const val I2C_MID_CLOCK = 0x01 // Send the clock pulse between reading and writing. Required by the NXT US sensor.
const val I2C_PIN1_9V = 0x02 // 9v pullup on pin 1
const val I2C_SAME = 0x04 // Keep performing the same transaction e.g. keep polling a sensor

// Motor status flag stuff
const val MSF_LOW_VOLTAGE_FLOAT = 0x01 // If the motors are floating due to low battery voltage
const val MSF_OVERLOADED = 0x02 // If the motors aren't close to the target (applies to position control and dps speed control).
